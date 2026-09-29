---
description: Project Context is a Password Vault made in Java (Group Task)
---

Core Modules
1. Master Login / Authentication
If-else: compares the SHA-256 hash of the entered master password against the stored hash; grants or denies vault access; locks out after repeated failed attempts
2. Password Vault (store/retrieve)
Loop: iterates through all saved entries to display them in the list, and to search/filter by site name
If-else: checks if an entry with that site name already exists before adding (prevent duplicates); confirms before delete
AES encrypt on save, AES decrypt on view/copy
3. Password Generator
Loop: builds the random password character-by-character until it reaches the requested length
Switch: determines which character pool to pull from based on selected option (Letters Only / Letters+Numbers / Letters+Numbers+Symbols)
If-else: ensures the generated password meets minimum requirements (e.g., must contain at least one number/symbol if selected) before accepting it
4. Password Strength Analyzer
Loop: scans each character of the input password to check for uppercase, lowercase, digits, and symbols
Switch: converts the computed score into a strength label (Weak / Medium / Strong / Very Strong)
If-else: flags specific weaknesses (too short, no numbers, common pattern, etc.) and gives feedback
5. Main Menu / Navigation
Loop: keeps the application running, handling tab switches between Vault, Generator, and Analyzer
Switch: routes button/menu actions to the correct panel
Security note: AES is symmetric and reversible (needed since you must retrieve the actual saved password), while SHA-256 is a one-way hash (correct for the master password, since you only ever need to verify it, never recover it).

