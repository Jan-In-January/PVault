package com.pvault.service.mock;

import com.pvault.service.AuthService;

/*
Wayne :>>>>>>

What's done:
- Authentication
- Password lockout

To do:
- add json for storing hashed master password
- add auth generator for lockout mechanism
- account sign up
*/

public class MockAuthService implements AuthService {

    private static final String DEFAULT_MASTER_PASSWORD = "admin";
    private int remainingAttempts = 3;
    private boolean lockedOut = false;
    private boolean masterSet = true;

    @Override
    public boolean authenticate(String enteredMasterPassword) {
        if (lockedOut) {
            return false;
        }

        // Mock check (Module 1 owner will replace with SHA-256 hash comparison):
        if (DEFAULT_MASTER_PASSWORD.equals(enteredMasterPassword)) {
            remainingAttempts = 3;
            return true;
        } else {
            remainingAttempts--;
            if (remainingAttempts <= 0) {
                lockedOut = true;
            }
            return false;
        }
    }

    @Override
    public boolean isLockedOut() {
        return lockedOut;
    }

    @Override
    public int getRemainingAttempts() {
        return remainingAttempts;
    }

    @Override
    public boolean hasMasterPasswordSet() {
        return masterSet;
    }

    @Override
    public boolean setupMasterPassword(String newMasterPassword) {
        this.masterSet = true;
        this.remainingAttempts = 3;
        this.lockedOut = false;
        return true;
    }
}
