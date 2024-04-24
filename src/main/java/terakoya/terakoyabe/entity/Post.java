package terakoya.terakoyabe.entity;

import java.sql.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Post {
    private int     id;
    private int     releasetime;    // 发布时间
    private int     replytime;      // 回复时间
    //private User    poster;
    private int     posterid;
    private int     board;
    private String  title;
    private String  content;
    private int     likes;   // 因为 like 是关键字，所以改为 likes
    private int     dislike;

    public String toString() {
        return "Post [id=" + id + ", releasetime=" + new Date(releasetime) + ", replytime=" + new Date(replytime) + ", posterid=" + posterid + ", board=" + board + ", title=" + title + ", content=" + content + ", likes=" + likes + ", dislike=" + dislike + "]";
    }
}
