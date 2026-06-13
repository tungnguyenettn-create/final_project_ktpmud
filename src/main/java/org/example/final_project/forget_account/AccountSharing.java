package org.example.final_project.forget_account;

public class AccountSharing {



    private static AccountSharing instance;
    private String identityCard;

    private AccountSharing() {}

    public static AccountSharing getInstance(){
        if (instance == null) {
            instance = new AccountSharing();
        }
        return instance;
    }

    public String getIdentityCard() {
        return identityCard;
    }

    public void setIdentityCard(String identityCard) {
        this.identityCard = identityCard;
    }

    // Optional: Clear data after use to prevent stale data bugs
    public void clear() {
        this.identityCard = null;
    }
}
