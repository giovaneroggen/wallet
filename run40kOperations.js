const fs = require('fs');
const axios = require('axios');

const baseUrl = 'http://localhost:8085';
const walletEnvFile = './walletapp_env.json';
const operationsLog = './operations.log';
const totalOperations = 40000;
const batchSize = 100;

// Lê todos os usuários e carteiras
let wallets = JSON.parse(fs.readFileSync(walletEnvFile)).wallets;

// Inicializa lock simples por carteira
const locks = {};
wallets.forEach(w => { locks[w.walletId] = false; });

// Função simples de lock por walletId
function acquireLock(walletId) {
    return new Promise(resolve => {
        const tryLock = () => {
            if (!locks[walletId]) {
                locks[walletId] = true;
                resolve();
            } else {
                setTimeout(tryLock, 1);
            }
        };
        tryLock();
    });
}
function releaseLock(walletId) {
    locks[walletId] = false;
}

// Função para executar operação e logar saldo
async function performOperation(wallet, type, value, targetWallet = null, stats = null) {
    let apiUrl = `${baseUrl}/wallets/${wallet.walletId}`;
    if (type === 'deposit') apiUrl += '/deposit';
    if (type === 'withdraw') apiUrl += '/withdraw';
    if (type === 'transfer' && targetWallet) apiUrl += `/transfer/to/${targetWallet.walletId}`;

    let res;
    try {
        res = await axios.patch(apiUrl, { value }, {
            headers: { 'X-username': wallet.username, 'X-password': wallet.password }
        });
    } catch (err) {
        if (err.response) {
            const status = err.response.status;
            const data = err.response.data;
            const logEntry = {
                type,
                walletId: wallet.walletId,
                username: wallet.username,
                targetWalletId: targetWallet ? targetWallet.walletId : null,
                value,
                status,
                errorMessage: data?.message || data || err.message,
                timestamp: new Date().toISOString()
            };
            fs.appendFileSync(operationsLog, JSON.stringify(logEntry) + '\n');
            console.warn(`⚠️ Operação ${type} falhou para wallet ${wallet.walletId} (status ${status}):`, logEntry.errorMessage);
            return;
        } else {
            console.error(`❌ Erro inesperado na operação ${type} wallet ${wallet.walletId}:`, err.message);
            throw err;
        }
    }

    await acquireLock(wallet.walletId);
    if (type === 'transfer' && targetWallet) await acquireLock(targetWallet.walletId);

    try {
        const oldBalance = wallet.balance;

        if (type === 'deposit') wallet.balance += value;
        if (type === 'withdraw') wallet.balance -= value;
        if (type === 'transfer' && targetWallet) {
            wallet.balance -= value;
            targetWallet.balance += value;
        }

        const apiBalance = res.data.balanceAfterOperation;
        const valid = wallet.balance === apiBalance;

        if (!valid && stats) {
            stats.divergence++;
            console.warn(`⚠️ Saldo divergente na carteira ${wallet.walletId}: local=${wallet.balance} api=${apiBalance}`);
        }

        const logEntry = {
            type,
            walletId: wallet.walletId,
            username: wallet.username,
            value,
            oldBalance,
            balanceAfterLocal: wallet.balance,
            balanceAfterApi: apiBalance,
            balanceMatch: valid,
            targetWalletId: targetWallet ? targetWallet.walletId : null,
            timestamp: new Date().toISOString()
        };
        fs.appendFileSync(operationsLog, JSON.stringify(logEntry) + '\n');
    } finally {
        releaseLock(wallet.walletId);
        if (type === 'transfer' && targetWallet) releaseLock(targetWallet.walletId);
    }
}

// Função para garantir saldo mínimo
async function ensureBalance(wallet) {
    if (wallet.balance < 1) {
        const depositValue = 100 + Math.floor(Math.random() * 5000);
        console.log(`💰 Saldo insuficiente na wallet ${wallet.walletId}. Realizando depósito de ${depositValue}`);
        await performOperation(wallet, 'deposit', depositValue);
    }
}

// Função para gerar valor seguro
async function safeValue(wallet) {
    await ensureBalance(wallet);
    return Math.floor(Math.random() * wallet.balance) + 1;
}

// Escolhe wallet aleatória
function randomWallet(excludeId = null) {
    let choices = excludeId ? wallets.filter(w => w.walletId !== excludeId) : wallets;
    return choices[Math.floor(Math.random() * choices.length)];
}

// Executa todas as operações em batches
(async () => {
    const finalStats = { success: 0, failed: 0, conflict: 0, divergence: 0 };

    for (let i = 0; i < totalOperations; i += batchSize) {
        let batch = [];
        let batchStats = { success: 0, failed: 0, conflict: 0, divergence: 0 };

        for (let j = 0; j < batchSize && (i + j) < totalOperations; j++) {
            const opTypeRand = Math.random();
            const wallet = randomWallet();

            const wrappedOp = async () => {
                try {
                    let target = null;
                    let value;

                    if (opTypeRand < 0.33) {
                        value = 50 + Math.floor(Math.random() * 5000);
                        await performOperation(wallet, 'deposit', value, null, batchStats);
                    } else if (opTypeRand < 0.66) {
                        value = await safeValue(wallet);
                        await performOperation(wallet, 'withdraw', value, null, batchStats);
                    } else {
                        target = randomWallet(wallet.walletId);
                        value = await safeValue(wallet);
                        await performOperation(wallet, 'transfer', value, target, batchStats);
                    }

                    batchStats.success++;
                } catch (err) {
                    batchStats.failed++;
                    if (err.response && err.response.status === 409) {
                        batchStats.conflict++;
                    }
                    console.error(`❌ Operação falhou para wallet ${wallet.walletId}:`, err.message);
                }
            };

            batch.push(wrappedOp());
        }

        await Promise.all(batch);

        // Atualiza estatísticas finais
        finalStats.success += batchStats.success;
        finalStats.failed += batchStats.failed;
        finalStats.conflict += batchStats.conflict;
        finalStats.divergence += batchStats.divergence;

        console.log(`📊 Batch ${(i / batchSize) + 1}: Sucesso=${batchStats.success}, Falha=${batchStats.failed}, Conflito=${batchStats.conflict}, Divergência=${batchStats.divergence}`);
    }

    console.log('🎉 Todas as operações concluídas');
    console.log('========== RESUMO FINAL ==========');
    console.log(`Total de operações    : ${totalOperations}`);
    console.log(`Sucesso               : ${finalStats.success}`);
    console.log(`Falha                 : ${finalStats.failed}`);
    console.log(`Conflito (409)        : ${finalStats.conflict}`);
    console.log(`Divergência de saldo  : ${finalStats.divergence}`);
})();
