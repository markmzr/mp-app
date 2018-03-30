package marketplace;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class User {
    @NotNull
    @Size(min=2, max=30)
    private String username;

    @NotNull
    @Size(min=2, max=30)
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean validUsername() {
        if (username.length() >= 2 && username.length() <= 30) {
            return true;
        }
        else {
            return false;
        }
    }

    public Boolean validPassword() {
        if (password.length() >= 2 && password.length() <= 30) {
            return true;
        }
        else {
            return false;
        }
    }
}
