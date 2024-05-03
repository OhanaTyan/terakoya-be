package terakoya.terakoyabe.Service;


import terakoya.terakoyabe.entity.Reply;

import java.util.List;

public interface ReplyService {

    void insertReply(int postid, int replytime, int replyer, String content);

    List<Reply> findReplyByPostidAndReplytime(int postid, int replytime);

    void updateReplyidByPostid(int replyid, int postid);

    List<Reply> findReplyById(int replyid);

    void updateContent(int replyid, String content);

    void deleteReply(int replyid);

    List<Reply> getAllReplies(int page, int size);

    int getReplyCount();

    List<Reply> getRepliesByPostid(int posterid, int page, int size);

    int getReplyCountByPosterid(int posterid);

    List<Reply> getLatestReplies();

    void deleteByPostid(int postid);
}
