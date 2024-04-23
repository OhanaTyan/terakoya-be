package terakoya.terakoyabe.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
    private int uid;
    private String username;
    private String password;
    private int role;

    public String toString() {
        return "User [uid=" + uid + ", username=" + username + ", password=" + password + ", role=" + role + "]";
    }
}
