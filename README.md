# 💳 Wallet API

[![Java](https://img.shields.io/badge/Java-24-blue)](https://www.oracle.com/java/)
[![Spring](https://img.shields.io/badge/Spring-Boot-green)](https://spring.io/projects/spring-boot)
[![MongoDB](https://img.shields.io/badge/MongoDB-Database-brightgreen)](https://www.mongodb.com/)
[![Gradle](https://img.shields.io/badge/Gradle-Build-orange)](https://gradle.org/)
[![Docker](https://img.shields.io/badge/Docker-Container-blue)](https://www.docker.com/)

The **Wallet API** is a RESTful service for simulating and managing digital wallets.  
It was designed for **load testing**, **automation**, and **integration scenarios** between multiple users, ensuring consistent financial operations.

---

## 🚀 Features
- **Users**
  - Simplified creation and authentication via headers.
- **Wallets**
  - Each user can have one or more wallets.
  - Real-time balance queries.
- **Operations**
  - **Deposit**: adds funds to the wallet.
  - **Withdraw**: removes funds from the wallet (without allowing negative balance).
  - **Transfer**: atomically transfers funds between different wallets.
- **Validations**
  - No operation allows negative balance.
  - All transactions return the updated balance.

---

## 📡 Endpoints

### 👤 Users
| Method | Endpoint       | Description        |
|--------|----------------|--------------------|
| POST   | `/users`       | Creates a new user |

### 💼 Wallets
| Method | Endpoint        | Description              |
|--------|-----------------|--------------------------|
| POST   | `/wallets`      | Creates a new wallet     |
| GET    | `/wallets/{id}` | Retrieves wallet balance |

### 💰 Operations
| Method | Endpoint                                      | Description             |
|--------|-----------------------------------------------|-------------------------|
| PATCH  | `/wallets/{id}/deposit`                       | Performs a deposit      |
| PATCH  | `/wallets/{id}/withdraw`                      | Performs a withdrawal   |
| PATCH  | `/wallets/{id}/transfer/to/{targetWalletId}`  | Performs a transfer     |

---

## 🔐 Authentication
All requests involving **wallets and operations** must include the following headers:

```http
X-username: <user name>
X-password: <user password>
```

---

## 📊 Notes
- Usernames must be unique.
- No balance can be negative.
- Transfers debit and credit wallets instantly (synchronously).
- The project includes integration with automated **stress test scripts** (Node.js).

---

## ⚙️ Prerequisites

- **JDK 24** (or higher)
- **Docker**
- **Node.js** (for Stress Tests)

---

## ▶️ Quick Start

Clone the repository and run the application:

```bash
git clone https://github.com/your-username/wallet-api.git
cd wallet-api
./gradlew bootRun
```

The API will be available at:  
👉 `http://localhost:8085`

---

## 🧪 Running Tests

Build and test the application:

```bash
./gradlew clean build
```

---

## 📊 Stress Tests

Start the application:

```bash
./gradlew bootRun
```

Run data setup:

```bash
node setupWallets.js
```

Execute multiple parallel operations:

```bash
node run40kOperations.js
```

---
