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
async function performOperation(wallet, type, value, targetWallet=null) {
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
        if (err.response && err.response.status >= 400 && err.response.status <= 500) {
            console.warn(`⚠️ Operação ${type} falhou para wallet ${wallet.walletId}:`, err.response.data || err.message);
            return;
        } else {
            throw err;
        }
    }

    // Aplica lock apenas para atualizar saldo local
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

        if (!valid) {
            console.warn(`⚠️ Saldo divergente na carteira ${wallet.walletId}: local=${wallet.balance} api=${apiBalance}`);
        }
    } finally {
        releaseLock(wallet.walletId);
        if (type === 'transfer' && targetWallet) releaseLock(targetWallet.walletId);
    }
}

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
function randomWallet(excludeId=null) {
    let choices = excludeId ? wallets.filter(w => w.walletId !== excludeId) : wallets;
    return choices[Math.floor(Math.random() * choices.length)];
}

// Executa todas as operações em batches
(async () => {
    for (let i = 0; i < totalOperations; i += batchSize) {
        let batch = [];
        for (let j = 0; j < batchSize && (i+j)<totalOperations; j++) {
            const opTypeRand = Math.random();
            const wallet = randomWallet();
            if (opTypeRand < 0.33) {
                const value = 50 + Math.floor(Math.random() * 5000);
                batch.push(performOperation(wallet, 'deposit', value));
            } else if (opTypeRand < 0.66) {
                const value = await safeValue(wallet);
                batch.push(performOperation(wallet, 'withdraw', value));
            } else {
                const target = randomWallet(wallet.walletId);
                const value = await safeValue(wallet);
                batch.push(performOperation(wallet, 'transfer', value, target));
            }
        }
        await Promise.all(batch);
        if ((i/batchSize)%10===0) console.log(`✔️ ${i+batch.length} operações processadas`);
    }
    console.log('🎉 Todas as operações concluídas');
})();

