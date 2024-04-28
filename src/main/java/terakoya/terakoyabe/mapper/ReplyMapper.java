package terakoya.terakoyabe.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import terakoya.terakoyabe.entity.Reply;

@Mapper
public interface ReplyMapper {

    @Insert("INSERT INTO replies (id, postid, replytime, replyer, content, likes, dislike)" + 
            "SELECT COALESCE(MAX(id), 0)+1, #{postid}, #{replytime}, #{replyer}, #{content}, #{likes}, #{dislike}"+
            "FROM replies")
    void createReply(int postid, int replytime, int replyer, String content, int likes, int dislike);

    // 通过 postid 和 replytime 查询
    @Select("SELECT * FROM replies WHERE postid = #{postid} AND replytime = #{replytime}")
    List<Reply> getReplyByPostIdAndReplyTime(int postid, int replytime);

    @Update("UPDATE replies SET postid = #{postid} WHERE id = #{id}")
    void updatePostid(int id, int postid);

    @Update("UPDATE replies SET content = #{content} WHERE id = #{rid}")
    void updateContent(int rid, String content);

    @Select("SELECT * FROM replies WHERE postid = #{postid} ORDER BY replytime ASC LIMIT #{size} OFFSET #{offset}")
    List<Reply> getReplyByPostId(int postid, int offset, int size);

    @Delete("DELETE FROM replies WHERE postid = #{postId}")
    void deleteByPostId(int postId);
    
    @Select("SELECT * FROM replies WHERE id = #{rid}")
    List<Reply> getReplyById(int rid);

    @Delete("DELETE FROM replies WHERE id = #{rid}")
    void deleteReply(int rid);

    @Select("SELECT * FROM replies ORDER BY replytime DESC LIMIT 15")
    List<Reply> getLatest();
}
