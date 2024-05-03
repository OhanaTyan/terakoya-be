package terakoya.terakoyabe.Service;

import terakoya.terakoyabe.entity.Post;

import java.util.List;

public interface PostService {
    
	Post findPostById(int pid);

	void updateReplyTime(int pid, int replyTime);

	void updateBoardToZero(int boardid);

	void insertPost(int releaseTime, int replyTime, int posterid, int board, String title, String content);

	List<Post> findPostByReleaseTimeAndReplyTime(int releaseTime, int replyTime);

	void updatePost(int id, String title, String content, Integer board);

	List<Post> getLatestPosts(int page, int size);

	List<Post> getLatestPostsByBoard(int boardid, int page, int size);

	List<Post> getPostsByBoardPosterAndKeyword(int bid, int posterid, String keyword, int page, int size);

	int getPostCountByBoardPosterAndKeyword(int boardid, int posterid, String keyword);
}
