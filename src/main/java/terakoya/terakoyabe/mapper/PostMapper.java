package terakoya.terakoyabe.mapper;

import java.util.List;

import org.apache.ibatis.annotations.*;

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


    // 批量将 board 为 boardId 的帖子的 board 改为 0
    @Update("UPDATE posts SET board = 0 WHERE board = #{boardId}")
    void updateBoardToZero(int boardId);

    @Select("SELECT * FROM posts ORDER BY replytime DESC LIMIT #{size} OFFSET #{offset}")
    List<Post> getLatestPosts(int offset, int size);

    @Select("SELECT * FROM posts WHERE board = #{boardId} ORDER BY replytime DESC LIMIT #{size} OFFSET #{offset}")
    List<Post> getLatestPostsByBoard(int boardId,  int offset, int size);

    @Select(
        "SELECT COUNT(*) FROM posts "+
            "WHERE "+
                "((#{bid} == -1) OR (board == #{bid}) )" +
                "AND "+
                "( "+
                    "(posterid = #{poster}) "+
                    "OR "+
                    "(" +
                        "posterid "+
                        "IN " + 
                        "("+
                            "SELECT uid from users WHERE username LIKE CONCAT('%', #{poster}, '%')"+
                        ")"+
                    ") "+
                ") "+
                "AND "+
                    "(title LIKE CONCAT('%', #{keyword}, '%') OR content LIKE CONCAT('%', #{keyword}, '%')) "

    )
    int getPostCountByBoardPosterAndKeyword(int bid, String poster, String keyword);


    @Select(
        "SELECT * FROM posts "+
            "WHERE "+
                "((#{bid} == -1) OR (board == #{bid}) )" +
                "AND "+
                "( "+
                    "(posterid = #{poster}) "+
                    "OR "+
                    "(" +
                        "posterid "+
                        "IN " + 
                        "("+
                            "SELECT uid from users WHERE username LIKE CONCAT('%', #{poster}, '%') "+
                        ")"+
                    ") "+
                ") "+
                "AND "+
                    "(title LIKE CONCAT('%', #{keyword}, '%') OR content LIKE CONCAT('%', #{keyword}, '%')) "+
        "ORDER BY replytime DESC LIMIT #{size} OFFSET #{offset}"

    )
    List<Post> getPostsByBoardPosterAndKeyword(String poster, String keyword, int offset, int size);
}

