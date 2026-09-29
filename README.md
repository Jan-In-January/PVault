# PVault - Password Vault Application

A secure, modular desktop Password Vault built in **Java** and **JavaFX**.

---

## 🚀 How to Run the Application

You **do not need to install Maven** to run this project. The Maven Wrapper (`mvnw`) is included and preconfigured.

From the project root:
```bash
# On Windows (Command Prompt or PowerShell)
.\mvnw.cmd javafx:run

# On Linux or macOS
./mvnw javafx:run
```

---

## 👥 Module Architecture & Team Responsibilities

The codebase has been designed with **decoupled interfaces and mock implementations** so every team member can work independently without conflicts.

| Module | Feature | Interface to Implement | Mock / Starting Point | Team Member |
|---|---|---|---|---|
| **Module 1** | Master Login / Authentication | [`AuthService.java`](file:///e:/Projects/PVault/src/main/java/com/pvault/service/AuthService.java) | [`MockAuthService.java`](file:///e:/Projects/PVault/src/main/java/com/pvault/service/mock/MockAuthService.java) | Member 1 |
| **Module 2** | Vault Storage & AES Encryption | [`VaultService.java`](file:///e:/Projects/PVault/src/main/java/com/pvault/service/VaultService.java) | [`MockVaultService.java`](file:///e:/Projects/PVault/src/main/java/com/pvault/service/mock/MockVaultService.java) | Member 2 |
| **Module 3** | Password Generator | [`GeneratorService.java`](file:///e:/Projects/PVault/src/main/java/com/pvault/service/GeneratorService.java) | [`MockGeneratorService.java`](file:///e:/Projects/PVault/src/main/java/com/pvault/service/mock/MockGeneratorService.java) | Member 3 |
| **Module 4** | Strength Analyzer | [`AnalyzerService.java`](file:///e:/Projects/PVault/src/main/java/com/pvault/service/AnalyzerService.java) | [`MockAnalyzerService.java`](file:///e:/Projects/PVault/src/main/java/com/pvault/service/mock/MockAnalyzerService.java) | Member 4 |
| **Module 5** | UI & Navigation | [`App.java`](file:///e:/Projects/PVault/src/main/java/com/pvault/App.java), [`LoginView.java`](file:///e:/Projects/PVault/src/main/java/com/pvault/ui/LoginView.java), [`MainDashboardView.java`](file:///e:/Projects/PVault/src/main/java/com/pvault/ui/MainDashboardView.java) | Handled (You) | Assigned to You |

---

## 🛠️ Instructions for Teammates

### Member 1: Master Login / Authentication
- **Task:** Implement [`AuthService`](file:///e:/Projects/PVault/src/main/java/com/pvault/service/AuthService.java).
- **Core Requirements:**
  - Hash master password with **SHA-256**.
  - Compare entered hash against stored hash.
  - Implement lockout mechanism (e.g. lockout after 3 failed attempts).
- **Hook into UI:** Once ready, instantiate your implementation in [`App.java`](file:///e:/Projects/PVault/src/main/java/com/pvault/App.java#L23) replacing `MockAuthService`.

### Member 2: Password Vault & AES Encryption
- **Task:** Implement [`VaultService`](file:///e:/Projects/PVault/src/main/java/com/pvault/service/VaultService.java).
- **Core Requirements:**
  - Use loops to iterate and filter saved accounts.
  - Use if-else to prevent duplicate site entries and confirm deletion.
  - Encrypt password with **AES** when saving; decrypt on view/copy.
- **Hook into UI:** Instantiate your implementation in [`App.java`](file:///e:/Projects/PVault/src/main/java/com/pvault/App.java#L24) replacing `MockVaultService`.

### Member 3: Password Generator
- **Task:** Refine [`GeneratorService`](file:///e:/Projects/PVault/src/main/java/com/pvault/service/GeneratorService.java) / [`MockGeneratorService`](file:///e:/Projects/PVault/src/main/java/com/pvault/service/mock/MockGeneratorService.java).
- **Core Requirements:**
  - Loop to build the password character-by-character.
  - Switch statement to select the character pool (Letters, Numbers, Symbols).
  - Validation to ensure required character types are present.

### Member 4: Password Strength Analyzer
- **Task:** Refine [`AnalyzerService`](file:///e:/Projects/PVault/src/main/java/com/pvault/service/AnalyzerService.java) / [`MockAnalyzerService`](file:///e:/Projects/PVault/src/main/java/com/pvault/service/mock/MockAnalyzerService.java).
- **Core Requirements:**
  - Loop to inspect character diversity (uppercase, lowercase, numbers, symbols).
  - Switch to map score to `StrengthLevel` (Weak, Medium, Strong, Very Strong).
  - Provide specific improvement tips.
