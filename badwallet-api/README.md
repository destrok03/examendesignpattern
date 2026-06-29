# BadWallet — Système de Portefeuille Électronique

Projet d'examen Design Pattern — L3 GLRS/CDSD — ISM Dakar 2026

---

## 📋 Description

BadWallet est un système de gestion de portefeuilles électroniques composé de deux microservices Spring Boot :

| Service | Port | Rôle |
|---------|------|------|
| `badwallet-api` | **8080** | Gestion des wallets, dépôts, retraits, transferts, paiements |
| `payment-service` | **8081** | Gestion des factures et paiements externes |

---

## 🏗️ Design Patterns utilisés

| Pattern | Classe | Description |
|---------|--------|-------------|
| **Repository** | `WalletRepository`, `TransactionRepository` | Accès aux données via JPA |
| **Service Layer** | `WalletService` / `WalletServiceImpl` | Logique métier isolée |
| **DTO** | `WalletCreateRequest`, `WalletResponseDTO`... | Transfert de données |
| **Mapper** | `WalletMapper`, `TransactionMapper` | Transformation entité ↔ DTO |
| **Strategy** | `IFeeStrategy`, `WithdrawFeeStrategy`, `NoFeeStrategy` | Calcul des frais |
| **Factory** | `FeeStrategyFactory` | Instanciation de la bonne Strategy |
| **Adapter** | `IPaymentServiceAdapter`, `PaymentServiceHttpAdapter` | Appels HTTP vers payment-service |
| **IoC** | `AppConfig` + Spring Container | Injection des dépendances |

---

## 🚀 Lancer le projet

### Prérequis
- Java 17
- Maven
- MySQL (port 3306)

### 1. Lancer payment-service (port 8081)
```bash
cd payment-service
mvn spring-boot:run
```

### 2. Lancer badwallet-api (port 8080)
```bash
cd badwallet-api
mvn spring-boot:run
```

> ⚠️ Lancer **payment-service en premier** car badwallet-api l'appelle via HTTP.

---

## 🗄️ Bases de données

| Base | Service |
|------|---------|
| `badwallet_db` | badwallet-api (wallets, transactions) |
| `payment_db` | payment-service (factures) |

Les bases sont créées automatiquement au démarrage (`createDatabaseIfNotExist=true`).

---

## 🧪 Tester l'API

Ouvrir `test.http` dans VS Code avec l'extension **REST Client**.

### Données de test insérées automatiquement au démarrage

**Wallets (badwallet-api) :**
| Code | Téléphone | Solde |
|------|-----------|-------|
| WLT-0000001 | +221770000001 | 250 000 XOF |
| WLT-0000002 | +221770000002 | 150 000 XOF |
| WLT-0000003 | +221770000003 | 500 000 XOF |

**Factures (payment-service) :**
| Référence | Wallet | Service | Montant |
|-----------|--------|---------|---------|
| FAC-ISM-1-1 | WLT-0000001 | ISM | 150 000 XOF |
| FAC-WOYA-1-1 | WLT-0000001 | WOYAFAL | 25 000 XOF |
| FAC-ISM-3-1 | WLT-0000003 | ISM | 150 000 XOF |
| FAC-ISM-3-3 | WLT-0000003 | ISM | 50 000 XOF |

---

## 📡 Endpoints — badwallet-api (port 8080)

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| POST | `/api/v1/wallets/seed` | Seeder la BDD (async) |
| POST | `/api/v1/wallets` | Créer un wallet |
| GET | `/api/v1/wallets?page=0&size=10` | Lister (paginé) |
| GET | `/api/v1/wallets/{phone}` | Consulter par téléphone |
| GET | `/api/v1/wallets/{phone}/balance` | Solde uniquement |
| POST | `/api/v1/wallets/{id}/deposit` | Dépôt |
| POST | `/api/v1/wallets/withdraw` | Retrait (frais 1%, max 5000) |
| POST | `/api/v1/wallets/transfer` | Transfert entre wallets |
| POST | `/api/v1/wallets/pay` | Payer facture du mois |
| POST | `/api/v1/wallets/pay-factures` | Payer factures spécifiques |
| GET | `/api/v1/wallets/{phone}/transactions` | Historique |
| GET | `/api/v1/external/factures/{code}/current` | Proxy factures mois |
| GET | `/api/v1/external/factures/{code}/current?unite=X` | Proxy par unité |
| GET | `/api/v1/external/factures/{code}/periode` | Proxy par période |

---

## 📡 Endpoints — payment-service (port 8081)

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/v1/factures?page=0&size=10` | Lister (paginé) |
| GET | `/api/v1/factures/{code}/current` | Factures mois en cours |
| GET | `/api/v1/factures/{code}/current?unite=X` | Filtrer par unité |
| GET | `/api/v1/factures/{code}/periode?debut&fin` | Sur une période |
| POST | `/api/v1/factures/{reference}/pay` | Payer une facture |

---

## 📂 Structure du projet

```
BadWallet/
├── badwallet-api/          ← Service principal (port 8080)
│   ├── src/main/java/com/badwallet/wallet/
│   │   ├── adapter/        ← Adapter Pattern (HTTP vers payment-service)
│   │   ├── config/         ← IoC : AppConfig (@Bean)
│   │   ├── controller/     ← WalletController, ExternalFactureController
│   │   ├── dto/            ← request/ et response/
│   │   ├── entity/         ← Wallet, Transaction
│   │   ├── exception/      ← GlobalExceptionHandler + exceptions custom
│   │   ├── mapper/         ← WalletMapper, TransactionMapper
│   │   ├── mock/           ← WalletMock (CommandLineRunner)
│   │   ├── repository/     ← WalletRepository, TransactionRepository
│   │   ├── service/        ← WalletService + impl/WalletServiceImpl
│   │   └── strategy/       ← Strategy + Factory Pattern
│   ├── test.http           ← Tests REST Client VS Code
│   └── pom.xml
│
└── payment-service/        ← Service factures (port 8081)
    ├── src/main/java/com/badwallet/payment/
    │   ├── controller/     ← FactureController
    │   ├── dto/            ← request/ et response/
    │   ├── entity/         ← Facture
    │   ├── exception/      ← GlobalExceptionHandler
    │   ├── mapper/         ← FactureMapper
    │   ├── mock/           ← FactureMock (CommandLineRunner)
    │   ├── repository/     ← FactureRepository
    │   └── service/        ← FactureService + impl/FactureServiceImpl
    └── pom.xml
```

---

## 🌿 Stratégie Git (Feature Branching)

```
main
└── develop
    ├── feature/wallet-create
    ├── feature/wallet-list
    ├── feature/wallet-balance
    ├── feature/deposit
    ├── feature/withdraw
    ├── feature/transfer
    ├── feature/pay
    ├── feature/pay-factures
    ├── feature/transactions
    ├── feature/seed
    └── feature/payment-service
```

---

## 👤 Auteur

**Amina** — Groupe 4 — L3 GLRS/CDSD — ISM Dakar 2026
