package terakoya.terakoyabe.Service;

import terakoya.terakoyabe.entity.Post;

public interface PostService {
    
	public Post getPostById(int pid);

	public void updateReplyTime(int pid, int replyTime);
}
