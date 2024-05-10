package terakoya.terakoyabe.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Reply {
    private int id;
    private int replytime;
    private int replyer;
    private int role;
    private int postid;
    private String username;
    private String content;

}

