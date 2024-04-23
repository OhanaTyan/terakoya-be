package terakoya.terakoyabe.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import terakoya.terakoyabe.entity.Post;

@Mapper
public interface PostMapper {
    
    
    
    @Insert("INSERT INTO posts (id, releasetime, replytime, posterid, board, title, content, likes, dislike)  "+
            "SELECT COALESCE(MAX(id), 0)+1, #{releaseTime}, #{replyTime}, #{posterid}, #{board}, #{title}, #{content}, #{likes}, #{dislike} " + 
            "FROM posts")
    void insertPost(int releaseTime, int replyTime, int posterid, int board, String title, String content, int likes, int dislike);

    @Select("SELECT * FROM posts WHERE releasetime = #{releaseTime} AND replytime = #{replyTime}")
    List<Post> getPostByReleaseTimeAndReplyTime(int releaseTime, int replyTime);

    @Select("SELECT * FROM posts WHERE id = #{id}")
    List<Post> getPostById(int id);

    @Update("UPDATE posts SET replytime = #{replyTime} WHERE id = #{id}")
    void updateReplyTime(int id, int replyTime);

    @Update("UPDATE posts SET title = #{title}, content = #{content}, board = #{board} WHERE id = #{id}")
    void updatePost(int id, String title, String content, int board);

    // 验证某个帖子是否是某个用户发的
    @Insert("SELECT * FROM posts WHERE id = #{pid} AND posterid = #{posterid}")
    List<Post> getPostByIdAndPosterId(int pid, int posterid);
}
