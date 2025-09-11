# 💳 Wallet API

A **Wallet API** é um serviço RESTful para simulação e gerenciamento de carteiras digitais.  
Ela foi projetada para testes de carga, automação e cenários de integração entre múltiplos usuários, garantindo operações financeiras consistentes.

---

## 🚀 Funcionalidades
- **Usuários**
    - Criação e autenticação simplificada via headers.
- **Carteiras**
    - Cada usuário pode ter uma ou mais carteiras.
    - Consulta de saldo em tempo real.
- **Operações**
    - **Depósito**: adiciona valor à carteira.
    - **Retirada**: remove valor da carteira (sem permitir saldo negativo).
    - **Transferência**: envia valores entre carteiras distintas de forma atômica.
- **Validações**
    - Nenhuma operação permite saldo negativo.
    - Todas as transações retornam o saldo atualizado.

---

## 📡 Endpoints

### 👤 Usuários
| Método | Endpoint       | Descrição             |
|--------|----------------|-----------------------|
| POST   | `/users`       | Cria um novo usuário |

---

### 💼 Carteiras
| Método | Endpoint     | Descrição                |
|--------|--------------|--------------------------|
| POST   | `/wallets`   | Cria uma nova carteira   |
| GET    | `/wallets/{id}` | Consulta saldo de uma carteira |

---

### 💰 Operações
| Método | Endpoint                                      | Descrição                       |
|--------|-----------------------------------------------|---------------------------------|
| PATCH  | `/wallets/{id}/deposit`                       | Realiza depósito                |
| PATCH  | `/wallets/{id}/withdraw`                      | Realiza retirada                |
| PATCH  | `/wallets/{id}/transfer/to/{targetWalletId}`  | Realiza transferência           |

---

## 🔐 Autenticação
Todas as requisições que envolvem **carteiras e operações** devem conter os headers:

```http
X-username: <nome do usuário>
X-password: <senha do usuário>
```


---

## 📊 Observações
- Nenhum nome de usuário pode repetir.
- Nenhum saldo pode ficar negativo.
- Transferências debitam e creditam as carteiras de forma instântanea (Sincrona).
- Projeto possui integração com scripts automatizados de stress test (Node.js)

---

## ⚙️ Pré-requisitos

- **JDK 24** (ou superior)
- **Docker**
- **Node** (Testes de Stress)

## ▶️ Executando

### 1. Executar a aplicação
```bash
  ./gradlew bootRun
```

## 🧪 Executando Testes

### 1. Executar build da aplicação
```bash
  ./gradlew clean build
```

## 📊 Testes de Stress

### 1. Executar a aplicação
```bash
  ./gradlew bootRun
```

### 2. Executar massa de dados
```bash
  node setupWallets.js
```

### 3. Executar multiplas operações em paralelo
```bash
  node run40kOperations.js
```
