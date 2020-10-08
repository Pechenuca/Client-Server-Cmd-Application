package max.database;

import java.io.Serializable;

public class Credentials implements Serializable {


    public final int id;
    public final String username;
    public final String password;

    public Credentials(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    @Override
    public String toString() {
        return "Credentials{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}