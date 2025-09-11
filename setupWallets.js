const fs = require('fs');
const axios = require('axios');

const baseUrl = 'http://localhost:8085'; // ajuste conforme sua API
const walletEnvFile = './walletapp_env.json';
const numUsers = 10000;

async function createUsersAndWallets() {
    let wallets = [];

    for (let i = 0; i < numUsers; i++) {
        const username = `user_${i}_${Date.now()}`;
        const password = `pass_${i}`;

        try {
            // 1️⃣ Cria usuário
            const userRes = await axios.post(`${baseUrl}/users`, {}, {
                headers: { 'X-username': username, 'X-password': password }
            });

            // 2️⃣ Cria carteira do usuário
            const walletRes = await axios.post(`${baseUrl}/wallets`, {}, {
                headers: { 'X-username': username, 'X-password': password }
            });

            const wallet = {
                walletId: walletRes.data.id,
                username,
                password,
                balance: 0
            };

            wallets.push(wallet);
            console.log(`Criado: ${username} -> ${wallet.walletId}`);

        } catch (err) {
            console.error(`Erro ao criar usuário/carteira: ${err.message}`);
        }
    }

    // Valida se criou alguma carteira
    if (!wallets.length) throw new Error("Nenhuma carteira foi criada. Verifique a API.");

    // Salva em JSON
    fs.writeFileSync(walletEnvFile, JSON.stringify({ wallets }, null, 2));
    console.log(`Arquivo ${walletEnvFile} gerado com sucesso!`);
}

// Executa
createUsersAndWallets().catch(err => {
    console.error('Erro no setup:', err.message);
});

