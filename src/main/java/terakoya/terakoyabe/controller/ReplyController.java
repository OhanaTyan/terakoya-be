package terakoya.terakoyabe.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import terakoya.terakoyabe.MyUtil;
import terakoya.terakoyabe.Service.BoardService;
import terakoya.terakoyabe.Service.PostService;
import terakoya.terakoyabe.Service.UserService;
import terakoya.terakoyabe.entity.Board;
import terakoya.terakoyabe.entity.Post;
import terakoya.terakoyabe.entity.Reply;
import terakoya.terakoyabe.mapper.ReplyMapper;
import terakoya.terakoyabe.setting.Setting;
import terakoya.terakoyabe.util.ErrorResponse;
import terakoya.terakoyabe.util.ServerError;

@RestController
@CrossOrigin(origins = Setting.SOURCE_SITE, maxAge = 3600, allowCredentials = "true")
@RequestMapping("/reply")
public class ReplyController {

    @Autowired
    ReplyMapper replyMapper;

    @Autowired
    PostService postService;

    @Autowired
    UserService userService;

    @Autowired
    BoardService boardService;

    // 返回 id
    private int createReply(int postid, int replytime, int replyer, String content){
        int randomValue = MyUtil.getRandomValue();
        do {
            try {
                replyMapper.createReply(
                    -randomValue,
                    replytime, 
                    replyer, 
                    content, 
                    0,
                    0
                );
                break;
                // 如果提交时撞键则重新提交
            } catch (org.springframework.dao.DuplicateKeyException e) {
                continue;
            }
        } while (true);
        List<Reply> replies;
        do {
            replies = replyMapper.getReplyByPostIdAndReplyTime(-randomValue, replytime);
        } while (replies.isEmpty());
        
        int id = replies.getFirst().getId();
        replyMapper.updatePostid(id, postid);  
        return id;
    }

    @AllArgsConstructor
    @Data
    private static class CreateResponse{
        private int pid;
    }

    @AllArgsConstructor
    @Data
    public static class CreateRequest{
        private Integer pid;
        private String content;
    }

    // 创建回复
    @PostMapping("/create")
    public ResponseEntity<?> create(
        @RequestBody CreateRequest data,
        @SessionAttribute(name="token", required = false) String token
    )
    {
        try {
            // 验证token
            if (!TokenController.verifyToken(token)) {
                return ResponseEntity.status(401).body(new ErrorResponse("token 验证失败，请重新登录"));
            }

            int uid = TokenController.getUid(token);

            int pid;
            if (data.getPid() == null) {
                return ResponseEntity.status(400).body(new ErrorResponse("缺少参数 pid"));
            } else {
                pid = data.getPid();
            }
            String content = data.getContent();

            // 检查内容是否为空
            if (content == null || content.isEmpty()){
                return ResponseEntity.status(400).body(new ErrorResponse("内容不能为空"));
            }

            // 检查帖子是否存在
            if (postService.getPostById(pid) == null){
                return ResponseEntity.status(400).body(new ErrorResponse("帖子不存在"));
            }

            int currentTime = MyUtil.getCurrentTime();
            int id = createReply(
                pid,
                currentTime,
                uid,
                content
            );

            // 更新所在帖子的最新回复时间
            postService.updateReplyTime(pid, currentTime);

            return ResponseEntity.ok().body(new CreateResponse(id));

        } catch (Exception e){
            return ResponseEntity.status(500).body(new ServerError(e));
        }
    }
 
    @AllArgsConstructor
    @Data
    public static class EditRequest{
        Integer rid;
        String content;
    }

    public boolean isReplyExists(int rid){
        List<Reply> replies = replyMapper.getReplyById(rid);
        if (replies.isEmpty()){
            return false;
        } else {
            return true;
        }
    }

    @PostMapping("/edit")
    public ResponseEntity<?> edit(
        @RequestBody EditRequest editRequest,
        @SessionAttribute(name="token", required = false) String token  
    )
    {
        try {
            // 验证token
            if (!TokenController.verifyToken(token)) {
                return ResponseEntity.status(401).body(new ErrorResponse("token 验证失败，请重新登录"));    
            }

            int uid = TokenController.getUid(token);
            // 验证是否是管理员
            if (!userService.isAdmin(uid)){
                return ResponseEntity.status(403).body(new ErrorResponse("权限不足"));
            }

            // TODO:检查回复是否存在
            if (isReplyExists(uid)){
                return ResponseEntity.status(400).body(new ErrorResponse("回复不存在"));
            }

            int rid;
            if (editRequest.getRid() == null){
                return ResponseEntity.status(400).body(new ErrorResponse("缺少参数 rid"));
            } else {
                rid = editRequest.getRid();
            }
            String content = editRequest.getContent();

            // 检查内容是否为空
            if (content == null || content.isEmpty()){
                return ResponseEntity.status(400).body(new ErrorResponse("内容不能为空"));
            }

            replyMapper.updateContent(rid, content);

            return ResponseEntity.ok("回复修改成功");
        } catch (Exception e){
            return ResponseEntity.status(500).body(new ServerError(e));
        }
    }

    @AllArgsConstructor
    @Data
    public static class DeleteRequest{
        Integer rid;
    }

    @PostMapping("/delete")
    public ResponseEntity<?> delete(
        @RequestBody DeleteRequest data,
        @SessionAttribute(name="token", required = false) String token
    )
    {
        try {
            // 验证token
            if (!TokenController.verifyToken(token)) {
                return ResponseEntity.status(401).body(new ErrorResponse("token 验证失败，请重新登录"));    
            }
            int uid = TokenController.getUid(token);

            // 验证是否是管理员
            if (!userService.isAdmin(uid)){
                return ResponseEntity.status(403).body(new ErrorResponse("权限不足"));
            }
            int rid;
            if (data.getRid() == null){
                return ResponseEntity.status(400).body(new ErrorResponse("缺少参数 rid"));
            } else {
                rid = data.getRid();
            }

            // 检查回复是否存在
            if (!isReplyExists(rid)){
                return ResponseEntity.status(400).body(new ErrorResponse("回复不存在"));
            }
            
            // 删除回复
            replyMapper.deleteReply(rid);

            return ResponseEntity.ok("回复删除成功");

        } catch (Exception e){
            return ResponseEntity.status(500).body(new ServerError(e));
        }
    }

    @AllArgsConstructor
    @Data
    public static class GetListRequest{
        Integer page;
        String poster;
    }
    
    @AllArgsConstructor
    @Data
    private static class GetListResponse{
        int postCount;
        List<Reply> replys;
    }


    @PostMapping("/list")
    public ResponseEntity<?> list(
        @RequestBody GetListRequest data,
        @SessionAttribute(name="token", required = false) String token
    )
    {
        try {
            // 验证token
            if (!TokenController.verifyToken(token)) {
                return ResponseEntity.status(401).body(new ErrorResponse("token 验证失败，请重新登录"));    
            }

            int uid = TokenController.getUid(token);
            // 验证是否是管理员
            if (!userService.isAdmin(uid)){
                return ResponseEntity.status(403).body(new ErrorResponse("权限不足"));
            }
            int page = 1;
            if (data.getPage() != null){
                page = data.getPage();
            } else {
                return ResponseEntity.status(400).body(new ErrorResponse("缺少参数 page"));
            }
            String poster = data.getPoster();
            int posterid = -1;
            int size = 50;
            int offset = (page - 1) * size;
            int replyCount = 0;
            List<Reply> replies;
            if (poster == null || poster.isEmpty()){
                // 获取所有回复
                replies = replyMapper.getAllReplies(offset, size);
                replyCount = replyMapper.getReplyCount();
            } else {
                // 如果 poster 全部由数字组成
                if (poster.matches("^\\d+$")){
                    posterid = Integer.parseInt(poster);
                    replies = replyMapper.getRepliesByPoster(posterid, offset, size);
                    replyCount = replyMapper.getReplyCountByPosterid(posterid);
                } else {
                    posterid = userService.getUserIdByUsername(poster);
                    if (posterid == -1){
                        replies = replyMapper.getAllReplies(offset, size);
                        replyCount = replyMapper.getReplyCount();
                    } else {
                        replies = replyMapper.getRepliesByPoster(posterid, offset, size);
                        replyCount = replyMapper.getReplyCountByPosterid(posterid);
                    }
                }
            }
            return ResponseEntity.ok().body(new GetListResponse(replyCount, replies));
        } catch (Exception e){
            return ResponseEntity.status(500).body(new ServerError(e));
        }
    }


    @AllArgsConstructor
    @Data
    private static class GetLatestResponse{
        private List<Reply> replies; // 这两个字段默认相等
        private List<Reply> replys;
        private List<Board> boards;
    }

    @GetMapping("/latest")
    public ResponseEntity<?> getLatest(){
        try {
            List<Reply> replies = replyMapper.getLatest();
            // 获取最新回复所在的板块
            ArrayList<Board> boards = new ArrayList<>();
            for (Reply reply : replies){
                int postid = reply.getPostid();
                Post post = postService.getPostById(postid);
                int boardid = post.getBoard();
                Board board = boardService.getBoardById(boardid);
                boards.add(board);
            }

            return ResponseEntity.ok().body(new GetLatestResponse(replies, replies, boards));

        } catch (Exception e){
            return ResponseEntity.status(500).body(new ServerError(e));
        }
    }

}
