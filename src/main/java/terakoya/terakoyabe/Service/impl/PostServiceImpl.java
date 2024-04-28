package terakoya.terakoyabe.Service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import terakoya.terakoyabe.Service.PostService;
import terakoya.terakoyabe.entity.Post;
import terakoya.terakoyabe.mapper.PostMapper;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostMapper postMapper;

	@Override
	public Post getPostById(int pid) { 
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
    
}
