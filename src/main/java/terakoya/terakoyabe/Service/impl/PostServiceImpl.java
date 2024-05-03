package terakoya.terakoyabe.Service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import terakoya.terakoyabe.MyUtil;
import terakoya.terakoyabe.Service.PostService;
import terakoya.terakoyabe.entity.Post;
import terakoya.terakoyabe.mapper.PostMapper;
import terakoya.terakoyabe.util.Log;

import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostMapper postMapper;

	@Override
	public Post findPostById(int pid) {
        List<Post> posts = postMapper.getPostById(pid);
        if (posts.isEmpty()){
            return null;
        } else {
            Post post = posts.getFirst();
            if (post.getBoard() == -1){
                // 帖子已被删除
                return null;
            }
            return post;
        }
 
	}

    @Override
    public void updateReplyTime(int pid, int replyTime) {
        postMapper.updateReplyTime(pid, replyTime);
    }

    @Override
    public void updateBoardToZero(int boardid) {
       postMapper.updateBoardToZero(boardid);
    }

    @Override
    public void insertPost(int releaseTime, int replyTime, int posterid, int board, String title, String content) {
        postMapper.insertPost(releaseTime, replyTime, posterid, board, title, content);
    }

    @Override
    public List<Post> findPostByReleaseTimeAndReplyTime(int releaseTime, int replyTime) {
        return postMapper.findPostByReleaseTimeAndReplyTime(releaseTime, replyTime);
    }

    @Override
    public void updatePost(int id, String title, String content, int board) {
        postMapper.updatePost(id, title, content, board);
    }

    @Override
    public List<Post> getLatestPosts(int page, int size) {
        Log.info("PostServiceImpl::getLatestPosts\npage:"+page+"\nsize:"+size);
        int offset = MyUtil.getOffset(page, size);
        return postMapper.getLatestPosts(offset, size);
    }

    @Override
    public List<Post> getLatestPostsByBoard(int boardid, int page, int size) {
        Log.info("PostServiceImpl::getLatestPostsByBoard\nboardid:"+boardid+"\npage:"+page+"\nsize:"+size);
        int offset = MyUtil.getOffset(page, size);
        return postMapper.getLatestPostsByBoard(boardid, offset, size);
    }

    @Override
    public List<Post> getPostsByBoardPosterAndKeyword(int bid, int posterid, String keyword, int page, int size) {
        int offset = MyUtil.getOffset(page, size);
        return postMapper.getPostsByBoardPosterAndKeyword(bid, posterid, keyword, offset, size);
    }

    @Override
    public int getPostCountByBoardPosterAndKeyword(int boardid, int posterid, String keyword) {
        return 0;
    }

}
