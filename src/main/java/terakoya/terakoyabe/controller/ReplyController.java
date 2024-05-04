package terakoya.terakoyabe.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import terakoya.terakoyabe.MyUtil;
import terakoya.terakoyabe.Service.BoardService;
import terakoya.terakoyabe.Service.PostService;
import terakoya.terakoyabe.Service.ReplyService;
import terakoya.terakoyabe.Service.UserService;
import terakoya.terakoyabe.entity.Board;
import terakoya.terakoyabe.entity.Post;
import terakoya.terakoyabe.entity.Reply;
import terakoya.terakoyabe.setting.Setting;
import terakoya.terakoyabe.util.ErrorResponse;
import terakoya.terakoyabe.util.Log;
import terakoya.terakoyabe.util.ServerError;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = Setting.SOURCE_SITE, maxAge = 3600, allowCredentials = "true")
@RequestMapping("/reply")
public class ReplyController {

    @Autowired
    PostService postService;

    @Autowired
    UserService userService;

    @Autowired
    ReplyService replyService;

    @Autowired
    BoardService boardService;

    // 返回 id
    private int createReply(int postid, int replytime, int replyer, String content){
        int randomValue = MyUtil.getRandomValue();
        do {
            try {
                replyService.insertReply(
                    -randomValue,
                    replytime, 
                    replyer, 
                    content
                );
                break;
                // 如果提交时撞键则重新提交
            } catch (org.springframework.dao.DuplicateKeyException e) {
                continue;
            }
        } while (true);
        List<Reply> replies;
        do {
            replies = replyService.findReplyByPostidAndReplytime(-randomValue, replytime);
        } while (replies.isEmpty());
        
        int replyid = replies.getFirst().getId();
        replyService.updateReplyidByPostid(replyid, postid);
        return replyid;
    }

    @AllArgsConstructor
    @Data
    private static class CreateResponse{
        private int pid;
    }

    @AllArgsConstructor
    @Data
    public static class CreateRequest{
        int pid;
        String  content;
        String  token;
        public String toString(){
            return "CreateRequest [pid=" + pid + ", content=" + content + ", token=" + token + "]";
        }
    }

    // 创建回复
    @PostMapping("/create")
    public ResponseEntity<?> create(
        @RequestBody CreateRequest data
    )
    {
        try {
            String token = data.getToken();
            // 验证token
            if (!TokenController.verifyToken(token)) {
                return ResponseEntity.status(401).body(new ErrorResponse("token 验证失败，请重新登录"));
            }
            Log.info("ReplyController::create" + data.toString());

            int uid = TokenController.getUid(token);

            int postid;
            if (false) {
                return ResponseEntity.status(400).body(new ErrorResponse("缺少参数 pid"));
            } else {
                postid = data.getPid();
            }
            String content = data.getContent();

            // 检查内容是否为空
            if (content == null || content.isEmpty()){
                return ResponseEntity.status(400).body(new ErrorResponse("内容不能为空"));
            }

            // 检查帖子是否存在
            if (postService.findPostById(postid) == null){
                return ResponseEntity.status(400).body(new ErrorResponse("帖子不存在"));
            }

            int currentTime = MyUtil.getCurrentTime();
            int id = createReply(
                postid,
                currentTime,
                uid,
                content
            );

            // 更新所在帖子的最新回复时间
            postService.updateReplyTime(postid, currentTime);

            return ResponseEntity.ok().body(new CreateResponse(id));

        } catch (Exception e){
            return ResponseEntity.status(500).body(new ServerError(e));
        }
    }
 
    @AllArgsConstructor
    @Data
    public static class EditRequest{
        int rid;
        String  content;
        String  token;
        public String toString(){
            return "EditRequest [replyid=" + rid + ", content=" + content + ", token=" + token + "]";
        }
    }

    public boolean isReplyExists(int replyid){
        List<Reply> replies = replyService.findReplyById(replyid);
        if (replies.isEmpty()){
            return false;
        } else {
            return true;
        }
    }

    @PostMapping("/edit")
    public ResponseEntity<?> edit(
        @RequestBody EditRequest data
    )
    {
        try {
            String token = data.getToken();
            // 验证token
            if (!TokenController.verifyToken(token)) {
                return ResponseEntity.status(401).body(new ErrorResponse("token 验证失败，请重新登录"));    
            }
            Log.info("ReplyController::edit" + data.toString());

            int uid = TokenController.getUid(token);
            // 验证是否是管理员
            if (!userService.isAdmin(uid)){
                return ResponseEntity.status(403).body(new ErrorResponse("权限不足"));
            }

            // TODO:检查回复是否存在
            if (isReplyExists(uid)){
                return ResponseEntity.status(400).body(new ErrorResponse("回复不存在"));
            }

            int replyid;
            if (false){
                return ResponseEntity.status(400).body(new ErrorResponse("缺少参数replyid"));
            } else {
               replyid = data.getRid();
            }
            String content = data.getContent();

            // 检查内容是否为空
            if (content == null || content.isEmpty()){
                return ResponseEntity.status(400).body(new ErrorResponse("内容不能为空"));
            }

            replyService.updateContent(replyid, content);

            return ResponseEntity.ok("回复修改成功");
        } catch (Exception e){
            return ResponseEntity.status(500).body(new ServerError(e));
        }
    }

    @AllArgsConstructor
    @Data
    public static class DeleteRequest{
        int rnd;
        String  token;
        public String toString(){
            return "DeleteRequest [replyid=" + rid + ", token=" + token + "]";
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<?> delete(
        @RequestBody DeleteRequest data
    )
    {
        try {
            String token = data.getToken();
            // 验证token
            if (!TokenController.verifyToken(token)) {
                return ResponseEntity.status(401).body(new ErrorResponse("token 验证失败，请重新登录"));    
            }
            Log.info("ReplyController::delete" + data.toString());

            int uid = TokenController.getUid(token);

            // 验证是否是管理员
            if (!userService.isAdmin(uid)){
                return ResponseEntity.status(403).body(new ErrorResponse("权限不足"));
            }
            int replyid;
            if (false){
                return ResponseEntity.status(400).body(new ErrorResponse("缺少参数replyid"));
            } else {
               replyid = data.getRid();
            }

            // 检查回复是否存在
            if (!isReplyExists(replyid)){
                return ResponseEntity.status(400).body(new ErrorResponse("回复不存在"));
            }
            
            // 删除回复
            replyService.deleteReply(replyid);

            return ResponseEntity.ok("回复删除成功");

        } catch (Exception e){
            return ResponseEntity.status(500).body(new ServerError(e));
        }
    }

    @AllArgsConstructor
    @Data
    public static class GetListRequest{
        int page;
        String  poster;
        String  token;
        public String toString(){
            return "GetListRequest [page=" + page + ", poster=" + poster + ", token=" + token + "]";
        }
    }
    
    @AllArgsConstructor
    @Data
    private static class GetListResponse{
        int postCount;
        List<Reply> replys;
    }


    @PostMapping("/list")
    public ResponseEntity<?> list(
        @RequestBody GetListRequest data
    )
    {
        try {
            String token = data.getToken();
            // 验证token
            if (!TokenController.verifyToken(token)) {
                return ResponseEntity.status(401).body(new ErrorResponse("token 验证失败，请重新登录"));    
            }

            Log.info("ReplyController::list" + data.toString());

            int uid = TokenController.getUid(token);
            // 验证是否是管理员
            if (!userService.isAdmin(uid)){
                return ResponseEntity.status(403).body(new ErrorResponse("权限不足"));
            }
            int page;
            if (true){
                page = data.getPage();
            } else {
                return ResponseEntity.status(400).body(new ErrorResponse("缺少参数 page"));
            }
            String poster = data.getPoster();
            int posterid;
            int size = 50;
            int replyCount = 0;
            List<Reply> replies;
            
            if (poster == null || poster.isEmpty()){
                // fall through
                // 获取所有回复
            } else {
                // 如果 poster 全部由数字组成
                if (poster.matches("^\\d+$")){
                    posterid = Integer.parseInt(poster);
                    // 检查用户是否存在
                    if (userService.isUseridExists(posterid)){
                        // 如果用户存在，则返回该用户相关回复
                        replies = replyService.getRepliesByPostid(posterid, page, size);
                        replyCount = replyService.getReplyCountByPosterid(posterid);
                        return ResponseEntity.ok().body(new GetListResponse(replyCount, replies));
                    }
                    // fall through
                } else {
                    posterid = userService.getUseridByUsername(poster);
                    if (posterid == -1){
                        // 用户存在
                        replies = replyService.getRepliesByPostid(posterid, page, size);
                        replyCount = replyService.getReplyCountByPosterid(posterid); 
                        return ResponseEntity.ok().body(new GetListResponse(replyCount, replies));
                    } 
                    // fall through
                }
           }
            // 用户不存在，获取所有回复
            replies = replyService.getAllReplies(page, size);
            replyCount = replyService.getReplyCount();
 
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

    @PostMapping("/latest")
    public ResponseEntity<?> getLatest(){
        try {
            List<Reply> replies = replyService.getLatestReplies();
            // 获取最新回复所在的板块
            ArrayList<Board> boards = new ArrayList<>();
            for (Reply reply : replies){
                int postid = reply.getPostid();
                Post post = postService.findPostById(postid);
                int boardid = post.getBoard();
                Board board = boardService.findBoardById(boardid);
                boards.add(board);
            }

            return ResponseEntity.ok().body(new GetLatestResponse(replies, replies, boards));

        } catch (Exception e){
            return ResponseEntity.status(500).body(new ServerError(e));
        }
    }

}
