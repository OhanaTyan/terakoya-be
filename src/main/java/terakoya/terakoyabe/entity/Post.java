package terakoya.terakoyabe.entity;

import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
public class Post {
    private int id;
    private int releasetime;    // 发布时间
    private int replytime;      // 回复时间
    //private User    poster;
    private int posterid;
    private String username;
    private int board;
    private String title;
    private String content;

    public String toString() {
        return "Post [id=" + id + ", releasetime=" + new Date(releasetime) + ", replytime=" + new Date(replytime) + ", posterid=" + posterid + ", board=" + board + ", title=" + title + ", content=" + content;
    }
}
