package terakoya.terakoyabe.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import terakoya.terakoyabe.entity.Post;

import java.util.List;

@Mapper
public interface PostMapper {
    
    
    
    @Insert("INSERT INTO posts (id, releasetime, replytime, posterid, board, title, content)  "+
            "SELECT COALESCE(MAX(id), 0)+1, #{releaseTime}, #{replyTime}, #{posterid}, #{board}, #{title}, #{content} " +
            "FROM posts")
    void insertPost(int releaseTime, int replyTime, int posterid, int board, String title, String content);

    @Select("SELECT * FROM posts WHERE releasetime = #{releaseTime} AND replytime = #{replyTime}")
    List<Post> findPostByReleaseTimeAndReplyTime(int releaseTime, int replyTime);

    // TODO
    @Select("SELECT posts.id, releasetime, posterid, board, title, content, replytime, username, role " +
            "FROM posts JOIN users ON posts.posterid = users.uid WHERE id = #{id}")
    List<Post> getPostById(int id);

    @Update("UPDATE posts SET replytime = #{replyTime} WHERE id = #{id}")
    void updateReplyTime(int id, int replyTime);


    @Update("UPDATE posts SET title = #{title}, content = #{content}, board = #{board} WHERE id = #{id}")
    void updatePost(int id, String title, String content, int board);


    // 批量将 board 为 boardid 的帖子的 board 改为 0
    @Update("UPDATE posts SET board = 0 WHERE board = #{boardid}")
    void updateBoardToZero(int boardid);

    // TODO
    @Select("SELECT posts.id, releasetime, posterid, board, title, content, replytime, users.username, users.role "+
            "FROM posts JOIN users ON posts.posterid = users.uid "+
            "ORDER BY replytime DESC LIMIT #{size} OFFSET #{offset}")
    List<Post> getLatestPosts(int offset, int size);

    // TODO
    @Select("SELECT posts.id, releasetime, posterid, board, title, content, replytime, users.username, role "+
            "FROM posts JOIN users ON posts.posterid = users.uid"+
            " WHERE board = #{boardid} ORDER BY replytime DESC LIMIT #{size} OFFSET #{offset}")
    List<Post> getLatestPostsByBoard(int boardid,  int offset, int size);

    String sql =
       "WHERE "+
                    "((#{bid} = -1) OR (board = #{bid}) )" +
                "AND "+
                    "((#{posterid} = -1) OR (posterid = #{posterid})) "+
                "AND "+
                    "( "+ // keyword 是空串
                        "(LENGTH(#{keyword}) = 0) "+
                    "OR "+
                        "(title LIKE CONCAT('%', #{keyword}, '%')) "+
                    "OR "+
                        "(content LIKE CONCAT('%', #{keyword}, '%')) "+
                    ") ";

 


    @Select(
        "SELECT COUNT(*) FROM posts "+
        sql
    )
    int getPostCountByBoardPosterAndKeyword(int bid, int posterid, String keyword);


    // TODO
    @Select(
        "SELECT posts.id, releasetime, posterid, board, title, content, replytime, username, role "+
                " FROM posts JOIN users ON posts.posterid = users.uid "+
        sql +
        "ORDER BY replytime DESC LIMIT #{size} OFFSET #{offset}"

    )
    List<Post> getPostsByBoardPosterAndKeyword(int bid, int posterid, String keyword, int offset, int size);
}

