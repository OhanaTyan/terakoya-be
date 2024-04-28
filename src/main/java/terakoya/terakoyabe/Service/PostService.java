package terakoya.terakoyabe.Service;

import terakoya.terakoyabe.entity.Post;

public interface PostService {
    
	Post getPostById(int pid);

	void updateReplyTime(int pid, int replyTime);
}
