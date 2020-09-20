package javaProject.database;

public class CurrentUser {
    private Credentials credentials;

    public CurrentUser(Credentials credentials) {
        this.credentials = credentials;
    }

    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }

    public Credentials getCredentials() {
        return credentials;
    }
}
