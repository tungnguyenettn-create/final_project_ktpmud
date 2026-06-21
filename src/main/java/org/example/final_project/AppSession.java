package org.example.final_project;

import org.example.final_project.api.AccountHandling;

public class AppSession {

    private static volatile AppSession instance = null;

    // JWT tu server co exp = 1 gio, client tu enforce timeout ngan hon neu muon
    private static final long SESSION_TIMEOUT_MS = 60 * 60 * 1000; // 1 gio

    private String currentAccountId;
    private String authToken;        // JWT that tu Flask
    private boolean isAuthenticated;
    private long loginTimestamp;

    private AppSession() {}

    public static AppSession getInstance() {
        if (instance == null) {
            synchronized (AppSession.class) {
                if (instance == null) {
                    instance = new AppSession();
                }
            }
        }
        return instance;
    }

    /**
     * Goi API login, neu thanh cong thi luu JWT that vao session.
     *
     * @return 2  = thanh cong
     *         1  = sai mat khau
     *         0  = tai khoan khong ton tai
     *        -1  = loi he thong
     */
    public synchronized int login(String username, String password) {
        String[] tokenHolder = new String[1];
        int result = AccountHandling.login(username, password, tokenHolder);

        if (result == 2 && tokenHolder[0] != null) {
            this.currentAccountId = username;
            this.authToken        = tokenHolder[0]; // JWT that tu Flask
            this.isAuthenticated  = true;
            this.loginTimestamp   = System.currentTimeMillis();
        }

        return result;
    }

    public synchronized boolean isAuthenticated() {
        if (!isAuthenticated) return false;
        if (System.currentTimeMillis() - loginTimestamp > SESSION_TIMEOUT_MS) {
            logout();
            return false;
        }
        return true;
    }

    public synchronized void logout() {
        this.currentAccountId = null;
        this.authToken        = null;
        this.isAuthenticated  = false;
        this.loginTimestamp   = 0;
    }

    public synchronized String getCurrentAccountId() {
        return isAuthenticated() ? currentAccountId : null;
    }

    /**
     * Tra ve JWT de truyen vao cac ham AccountHandling, TransferHandling, v.v.
     * Vi du: AccountHandling.getBalance(AppSession.getInstance().getAuthToken())
     */
    public synchronized String getAuthToken() {
        return isAuthenticated() ? authToken : null;
    }
}