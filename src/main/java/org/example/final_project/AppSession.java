package org.example.final_project;

public class AppSession {
    private static volatile AppSession instance = null;
    private static final long SESSION_TIMEOUT_MS = 600000;

    private String currentUser;
    private String authToken;
    private boolean isAuthenticated;
    private long loginTimestamp;

    private String currentFullName;
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

    // Synchronized to ensure only one thread can log in at a time
    public synchronized boolean login(String username, String password, String full_name) {
        if (username != null && !username.isEmpty() && password != null && !password.isEmpty()) {
            this.currentUser = username;
            this.currentFullName = full_name;
            this.authToken = "TOKEN_" + System.currentTimeMillis();
            this.isAuthenticated = true;
            this.loginTimestamp = System.currentTimeMillis();
            return true;
        }
        return false;
    }

    // Synchronized because it reads and potentially modifies state (calls logout)
    public synchronized boolean isAuthenticated() {
        if (!isAuthenticated) return false;

        if (System.currentTimeMillis() - loginTimestamp > SESSION_TIMEOUT_MS) {
            logout(); // Internal call is safe since the lock is reentrant
            return false;
        }
        return true;
    }

    // Synchronized to clear state safely
    public synchronized void logout() {
        this.currentUser = null;
        this.authToken = null;
        this.isAuthenticated = false;
        this.loginTimestamp = 0;
    }

    public synchronized String getCurrentUser() {
        return isAuthenticated() ? currentUser : null;
    }

    public synchronized String getAuthToken() {
        return isAuthenticated() ? authToken : null;
    }
    public synchronized String getCurrentFullName() {
        return isAuthenticated() ? currentFullName : null;
    }
}