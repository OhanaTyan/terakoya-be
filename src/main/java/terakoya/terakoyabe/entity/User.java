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
}
