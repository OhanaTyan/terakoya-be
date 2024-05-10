package terakoya.terakoyabe.mapper;

import org.apache.ibatis.annotations.*;
import terakoya.terakoyabe.entity.Reply;

import java.util.List;

@Mapper
public interface ReplyMapper {

    @Insert("INSERT INTO replies (id, postid, replytime, replyer, content) " +
            "SELECT COALESCE(MAX(id), 0)+1, #{postid}, #{replytime}, #{replyer}, #{content} " +
            "FROM replies")
    void createReply(int postid, int replytime, int replyer, String content);

    // 通过 postid 和 replytime 查询
    @Select("SELECT * FROM replies WHERE postid = #{postid} AND replytime = #{replytime}")
    List<Reply> findReplyByPostidAndReplytime(int postid, int replytime);

    @Update("UPDATE replies SET postid = #{postid} WHERE id = #{replyid}")
    void updateReplyidByPostid(int replyid, int postid);

    @Update("UPDATE replies SET content = #{content} WHERE id = #{replyid}")
    void updateContent(int replyid, String content);

    // TODO
    @Select("SELECT replies.id, replytime, replyer, content, postid, users.username, role "+
            "FROM replies JOIN users ON replies.replyer = users.uid " +
            "WHERE postid = #{postid} ORDER BY replytime LIMIT #{size} OFFSET #{offset}")
    List<Reply> getRepliesByPostid(int postid, int offset, int size);

    @Delete("DELETE FROM replies WHERE postid = #{postId}")
    void deleteByPostid(int postId);

    // TODO
    @Select("SELECT replies.id, replytime, replyer, content, postid, users.username, role "+
            "FROM replies JOIN users ON replies.replyer = users.uid "+
            "WHERE id = #{replyid}")
    List<Reply> findReplyByReplyid(int replyid);

    @Delete("DELETE FROM replies WHERE id = #{replyid}")
    void deleteReply(int replyid);

    // TODO
    @Select("SELECT replies.id, replytime, replyer, content, postid, users.username, role "+
            "FROM replies JOIN users ON replyer = users.uid "+
            "ORDER BY replytime DESC LIMIT 15")
    List<Reply> getLatestReplies();

    // TODO
    @Select("SELECT replies.id, replytime, replyer, content, postid, users.username, role "+
            "FROM replies JOIN users ON replyer = users.uid "+
            "ORDER BY replytime DESC LIMIT #{size} OFFSET #{offset} ")
    List<Reply> getAllReplies(int offset, int size);

    @Select("SELECT COUNT(*) FROM replies ")
    int getReplyCount();

    // TODO
    @Select("SELECT replies.id, replytime, replyer, content, postid, users.username, role "+
            "FROM replies JOIN users ON replyer = users.uid "+
            "WHERE replyer = #{posterid} " +
            "ORDER BY replytime DESC LIMIT #{size} OFFSET #{offset} ")
    List<Reply> getRepliesByPoster(int posterid, int offset, int size);

    @Select("SELECT COUNT(*) FROM replies WHERE replyer = #{posterid} " )
    int getReplyCountByPosterid(int posterid);


}
