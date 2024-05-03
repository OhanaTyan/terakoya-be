package terakoya.terakoyabe.Service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import terakoya.terakoyabe.MyUtil;
import terakoya.terakoyabe.Service.ReplyService;
import terakoya.terakoyabe.entity.Reply;
import terakoya.terakoyabe.mapper.ReplyMapper;

import java.util.List;

@Service
public class ReplyServiceImpl implements ReplyService {

    @Autowired
    ReplyMapper replyMapper;

    @Override
    public void insertReply(int postid, int replytime, int replyer, String content){
        replyMapper.createReply(postid, replytime, replyer, content);
    }

    @Override
    public List<Reply> findReplyByPostidAndReplytime(int postid, int replytime) {
        return replyMapper.findReplyByPostidAndReplytime(postid, replytime);
    }

    @Override
    public void updateReplyidByPostid(int replyid, int postid) {
        replyMapper.updateReplyidByPostid(replyid, postid);
    }

    @Override
    public List<Reply> findReplyById(int replyid) {
        return replyMapper.findReplyByReplyid(replyid);
    }

    @Override
    public void updateContent(int replyid, String content) {
        replyMapper.updateContent(replyid, content);
    }

    @Override
    public void deleteReply(int replyid) {
        replyMapper.deleteReply(replyid);
    }

    @Override
    public List<Reply> getAllReplies(int page, int size) {
        int offset = MyUtil.getOffset(page, size);
        return replyMapper.getAllReplies(offset, size);
    }

    @Override
    public int getReplyCount() {
        return replyMapper.getReplyCount();
    }

    @Override
    public List<Reply> getRepliesByPostid(int posterid, int page, int size) {
        int offset = MyUtil.getOffset(page, size);
        return replyMapper.getRepliesByPostid(posterid, offset, size);
    }

    @Override
    public int getReplyCountByPosterid(int posterid) {
        return replyMapper.getReplyCountByPosterid(posterid);
    }

    @Override
    public List<Reply> getLatestReplies() {
        return replyMapper.getLatestReplies();
    }


}
