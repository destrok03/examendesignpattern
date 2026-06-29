# Guide — Organisation VS Code & GitHub — BadWallet

## 1. Organisation dans VS Code

### Ouvrir les deux projets en même temps (Workspace)

Dans VS Code, tu peux ouvrir les deux services en même temps :

1. `File` → `Add Folder to Workspace`
2. Ajoute `badwallet-api`
3. Ajoute `payment-service`
4. `File` → `Save Workspace As` → `BadWallet.code-workspace`

Tu verras les deux projets dans l'explorateur côte à côte.

### Extensions VS Code indispensables

| Extension | Rôle |
|-----------|------|
| **REST Client** | Exécuter le fichier `test.http` |
| **Extension Pack for Java** | Support Java + Maven |
| **Spring Boot Extension Pack** | Démarrer Spring Boot depuis VS Code |
| **GitLens** | Visualiser les branches et commits |

### Lancer les services depuis VS Code

Avec Spring Boot Extension Pack installé :
- Panneau gauche → icône Spring Boot
- Clique sur le service à démarrer (▶️)
- Ou : Terminal → `mvn spring-boot:run`

### Utiliser test.http

1. Ouvre `test.http` dans VS Code
2. Clique sur **Send Request** au-dessus de chaque requête
3. La réponse s'affiche dans un panneau à droite

---

## 2. Organisation sur GitHub

### Structure des dépôts

**Option recommandée : 1 dépôt avec 2 dossiers**

```
BadWallet/                    ← dépôt GitHub unique
├── badwallet-api/
├── payment-service/
├── README.md
└── .gitignore
```

### Initialiser le dépôt Git

```bash
# À la racine du projet
cd BadWallet
git init
git add .
git commit -m "Initial commit — structure de base"

# Créer le repo sur GitHub, puis :
git remote add origin https://github.com/TON-USERNAME/BadWallet.git
git push -u origin main
```

### Créer la branche develop

```bash
git checkout -b develop
git push -u origin develop
```

### Workflow Feature Branching

Pour chaque endpoint, créer une branche feature depuis develop :

```bash
# 1. Se placer sur develop
git checkout develop

# 2. Créer la branche feature
git checkout -b feature/wallet-create

# 3. Coder l'endpoint
# ... développement ...

# 4. Committer
git add .
git commit -m "feat: POST /api/v1/wallets - création de wallet"

# 5. Push la branche
git push -u origin feature/wallet-create

# 6. Merger dans develop (quand l'endpoint est terminé)
git checkout develop
git merge feature/wallet-create
git push origin develop

# 7. Supprimer la branche feature (optionnel)
git branch -d feature/wallet-create
```

### Liste des branches à créer

```bash
git checkout -b feature/wallet-create      # POST /api/v1/wallets
git checkout -b feature/wallet-list        # GET  /api/v1/wallets
git checkout -b feature/wallet-balance     # GET  /api/v1/wallets/{phone}/balance
git checkout -b feature/deposit            # POST /api/v1/wallets/{id}/deposit
git checkout -b feature/withdraw           # POST /api/v1/wallets/withdraw
git checkout -b feature/transfer           # POST /api/v1/wallets/transfer
git checkout -b feature/pay               # POST /api/v1/wallets/pay
git checkout -b feature/pay-factures       # POST /api/v1/wallets/pay-factures
git checkout -b feature/transactions       # GET  /api/v1/wallets/{phone}/transactions
git checkout -b feature/seed               # POST /api/v1/wallets/seed
git checkout -b feature/payment-service    # tout payment-service
git checkout -b feature/external-proxy     # /api/v1/external/factures
```

### Convention de commits

```
feat: nouvelle fonctionnalité
fix: correction de bug
refactor: refactorisation
docs: documentation
test: tests
chore: tâches diverses
```

Exemples :
```bash
git commit -m "feat: POST /withdraw - retrait avec WithdrawFeeStrategy"
git commit -m "feat: Adapter Pattern - PaymentServiceHttpAdapter"
git commit -m "fix: correction calcul frais plafond 5000 CFA"
git commit -m "docs: README endpoints et design patterns"
```

### Release finale

```bash
# Quand tout est dans develop et testé :
git checkout main
git merge develop
git tag v1.0.0
git push origin main --tags
```

---

## 3. Email à envoyer au prof

**À :** douvewane85@gmail.com  
**Objet :** Examen de Design Pattern L3 S2 2026 - Amina - GLRS/CDSD  

Bonjour Professeur,

Veuillez trouver ci-dessous le lien vers mon dépôt GitHub pour l'examen de Design Pattern.

🔗 Lien GitHub : https://github.com/TON-USERNAME/BadWallet

Cordialement,  
Amina
