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
import terakoya.terakoyabe.entity.Post;
import terakoya.terakoyabe.entity.Reply;
import terakoya.terakoyabe.setting.Setting;
import terakoya.terakoyabe.util.Log;
import terakoya.terakoyabe.util.ServerError;

import java.util.List;

@RestController
@CrossOrigin(origins = Setting.SOURCE_SITE, maxAge = 3600, allowCredentials = "true")
@RequestMapping("/post")
public class PostController {


    @Autowired
    private BoardService boardService;

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Autowired
    private ReplyService replyService;


    @AllArgsConstructor
    @Data
    public static class ErrorResponse{
        String message;
    }

    @AllArgsConstructor
    @Data
    public static class CreateRequest{
        int board;
        String title;
        String content;
        String token;
        public String toString() {
            return "CreateRequest{" +
                    "board=" + board +
                    ", title='" + title + '\'' +
                    ", content='" + content + '\'' +
                    ", token='" + token + '\'' +
                    '}';
        }
    }

    @AllArgsConstructor
    @Data
    public static class CreateResponse{
        int pid;
    }

    
    private void insertPost(int releaseTime, int randomValue,int posterid, int board, String title, String content) {
        do {
            try {
                postService.insertPost(
                    releaseTime,
                    randomValue,
                    posterid,
                    board,
                    title, 
                    content
                );
                break;
                // 如果提交时撞键，则重新提交
            } catch (org.springframework.dao.DuplicateKeyException e){

            }
        } while (true);

    }

    // 检查帖子是否存在
    // 如果存在则返回该帖子
    // 否则返回 null
    private Post getPostById(int pid) {
        return postService.findPostById(pid);
    }


    // 检查帖子是否合法
    private boolean isPostValid(MyPost post) {
        return isPostValid(post.getTitle(), post.getContent());
    }

    private boolean isPostValid(String title, String content){
        // TODO: 添加验证帖子是否合法逻辑
        return true;
    }

    // 通过 releasetime 和 replytime 找到对应的帖子的 id
    int getPostIdByReleaseTimeAndReplyTime(int releaseTime, int replyTime) {
        List<Post> posts;
        do {
            // 等待 500ms
            try {
                Thread.sleep(500);
            } catch (InterruptedException e){
                // do nothing
            }
            posts = postService.findPostByReleaseTimeAndReplyTime(releaseTime, replyTime);
        } while (posts.isEmpty());

        return posts.getFirst().getId();
    }


    @PostMapping("/create")
    public ResponseEntity<?> create(
        @RequestBody CreateRequest data
    )
    {
        try {
            String token = data.getToken();
            // 验证 token
            if (!TokenController.verifyToken(token)) {
                return ResponseEntity.status(401).body(new ErrorResponse("token 验证失败，请重新登录"));
            }
            Log.info("PostController::create\n"+data.toString());

            int posterid = TokenController.getUid(token);

            String title = data.getTitle();
            String content = data.getContent();
            int board;
            if (false) {
                return ResponseEntity.status(400).body(new ErrorResponse("板块不能为空"));
            } else {
                board = data.getBoard();
            }

            // 检查输入是否为空
            if (title.isEmpty() || content.isEmpty()){
                return ResponseEntity.status(400).body(new ErrorResponse("标题或内容不能为空"));
            }

            // 检查板块是否存在
            if (!boardService.isBoardExists(board)){
                return ResponseEntity.status(400).body(new ErrorResponse("板块不存在或已被删除"));
            }

            // 验证帖子是否合法
            if (!isPostValid(title, content)){
                return ResponseEntity.status(400).body(new ErrorResponse("帖子不合法"));
            }

            int releaseTime = MyUtil.getCurrentTime();

            int randomValue = MyUtil.getRandomValue();

            // 打印调试信息

            // 插入数据库
            insertPost(
                releaseTime,
                randomValue,
                posterid,
                board,
                title,
                content
            );


            int id = getPostIdByReleaseTimeAndReplyTime(releaseTime, randomValue);

            // 将 id 对应的表的 replytime 设为 releaseTime
            postService.updateReplyTime(id, releaseTime);

            return ResponseEntity.ok().body(new CreateResponse(id));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ServerError(e));
        }

    }

    // 内部 post 类
    @AllArgsConstructor
    @Data
    public static class MyPost{
        int id;
        int releaseTime;
        int replyTime;
        int posterid;
        int board;
        String  title;
        String  content;
        String  token;

        public String toString(){
            return "MyPost{" +
                    "id=" + id +
                    ", releaseTime=" + releaseTime +
                    ", replyTime=" + replyTime +
                    ", posterid=" + posterid +
                    ", board=" + board +
                    ", title='" + title + '\'' +
                    ", content='" + content + '\'' +
                    ", token='" + token + '\'' +
                    '}';

        }
    }

    @PostMapping("/edit")
    public ResponseEntity<?> edit(
        @RequestBody MyPost data
    )
    {
        try {
            String token = data.getToken();
            if (!TokenController.verifyToken(token)){
                return ResponseEntity.status(401).body(new ErrorResponse("token 验证失败，请重新登录"));
            }

            Log.info("PostController::edit\n"+data.toString());

            int uid = TokenController.getUid(token);
            // 验证是否是管理用户
            if (!userService.isAdmin(uid)){
                return ResponseEntity.status(403).body(new ErrorResponse("权限不足"));
            }

            System.out.println(data.toString());

            int pid;
            if (false){
                return ResponseEntity.status(400).body(new ErrorResponse("pid 不能为空"));
            } else {
                pid = data.getId();
            }
            // 验证帖子是否存在
            if (getPostById(pid) == null){
                return ResponseEntity.status(400).body(new ErrorResponse("帖子不存在或已被删除"));
            }

            // 验证板块是否存在
            if (!boardService.isBoardExists(data.getBoard())){
                return ResponseEntity.status(400).body(new ErrorResponse("板块不存在或已被删除"));
            }

            // 验证帖子是否合法
            if (!isPostValid(data)){
                return ResponseEntity.status(400).body(new ErrorResponse("帖子不合法"));
            }


            // 更新数据库
            postService.updatePost(
                data.getId(),
                data.getTitle(),
                data.getContent(),
                data.getBoard()
            );
            
            return ResponseEntity.ok().body(new CreateResponse(data.getId()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ServerError(e));
        }
    }

    @AllArgsConstructor
    @Data
    public static class DeleteRequest{
        int pid;
        String  token;
        public String toString() {
            return "DeleteRequest{" +
                    "pid=" + pid +
                    ", token='" + token + '\'' +
                    '}';
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<?> delete(
        @RequestBody DeleteRequest data
    )
    {
        try {
            String token = data.getToken();
            if (!TokenController.verifyToken(token)){
                return ResponseEntity.status(401).body(new ErrorResponse("token 验证失败，请重新登录"));
            }

            Log.info("PostController::delete\n"+data.toString());
            int uid = TokenController.getUid(token);

            // 验证是否是管理员
            boolean isAdmin = userService.isAdmin(uid);

            int pid;
            if (false){
                return ResponseEntity.status(400).body(new ErrorResponse("pid 不能为空"));
            } else {
                pid = data.getPid();
            }

            Post post = getPostById(pid);
            // 验证帖子是否存在
            if (post == null){
                return ResponseEntity.status(400).body(new ErrorResponse("帖子不存在或已被删除"));
            }

            // 验证权限
            if (!isAdmin){
                // 如果不是管理员，那么只有发布者自己可以删除帖子
                if (post.getPosterid() != uid){
                    return ResponseEntity.status(403).body(new ErrorResponse("权限不足"));
                }
            }
            

            postService.updatePost(
                pid,
                "该帖子已被删除",
                "该帖子已被删除",
                -1
            );
            replyService.deleteByPostid(pid);
            
            return ResponseEntity.ok().body("删除成功");
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ServerError(e));
        }
    }

    @AllArgsConstructor
    @Data
    public static class GetLatestRequest{
        int bid;
        int page;
        public String toString() {
            return "GetLatestRequest{" +
                    "bid=" + bid +
                    ", page=" + page +
                    '}';
        }
    }

    @PostMapping("/latest")
    public ResponseEntity<?> getLatest(
        @RequestBody GetLatestRequest data
    )
    {
        try {
            Log.info("PostController::getLatest\n"+data.toString());

            int bid;
            int page;
            if (data == null){
                bid = 0;
                page = 1;
            } else {
                if (false)  bid = 0;
                else                        bid = data.getBid();
                if (false) page = 1;
                else                        page = data.getPage();
            }
            int size = 50;

            List<Post> posts;
            if (bid == 0){
                // 从所有帖子中查询最新帖子
                posts = postService.getLatestPosts(page, size);
            }
            else {
                // 检查板块是否存在
                if (!boardService.isBoardExists(bid)){
                    return ResponseEntity.status(400).body(new ErrorResponse("板块不存在或已被删除"));
                }

                // 从指定板块中查询最新帖子
                posts = postService.getLatestPostsByBoard(bid, page, size);
            }

            return ResponseEntity.ok().body(posts);
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ServerError(e));
        }
    }

    @AllArgsConstructor
    @Data
    public static class GetListRequest{
        int page;
        int bid;
        String poster;
        String keyword;
        String token;
        public String toString() {
            return "GetListRequest{" +
                    "page=" + page +
                    ", bid=" + bid +
                    ", poster='" + poster + '\'' +
                    ", keyword='" + keyword + '\'' +
                    ", token='" + token + '\'' +
                    '}';
        }
    }

    @AllArgsConstructor
    @Data
    public static class GetListResponse{
        int postCount;
        List<Post> posts;
    }

    @PostMapping("/list")
    public ResponseEntity<?> getList(
        @RequestBody(required = false) GetListRequest data
    )
    {  
        try {
            String token = data.getToken();
            // 验证 token
            if (!TokenController.verifyToken(token)){
                return ResponseEntity.status(401).body(new ErrorResponse("token 验证失败，请重新登录"));
            }
            Log.info("PostController::getList\n"+data.toString());
            int uid = TokenController.getUid(token);
            // 验证是否是管理员
            if (!userService.isAdmin(uid)){
                return ResponseEntity.status(403).body(new ErrorResponse("权限不足"));
            }

            int page;
            if (false){
                return ResponseEntity.status(400).body(new ErrorResponse("page 不能为空"));
            } else {
                page = data.getPage();
            }
            int size = 50;
            int bid;
            if (false){
                bid = -1;
            } else {
                bid = data.getBid();
            }
            String poster;
            int posterid;
            if (data.getPoster() == null){
                posterid = -1;
            } else {
                poster = data.getPoster();
                // 如果 poster 全部由数字组成
                if (poster.matches("\\d+")) {
                    posterid = Integer.parseInt(poster);
                } else {
                    // 如果 poster 不是数字，则认为是用户名
                    posterid = userService.getUseridByUsername(poster);
                }
            }
            String keyword;
            if (data.getKeyword() == null){
                keyword = "";
            } else {
                keyword = data.getKeyword();
            }

            List<Post> posts;
            
            posts = postService.getPostsByBoardPosterAndKeyword(
                bid,
                posterid,
                keyword,
                page,
                size
            );

            int postCount = postService.getPostCountByBoardPosterAndKeyword(
                bid,
                posterid,
                keyword
            );

            return ResponseEntity.ok().body(new GetListResponse(
                postCount,
                posts
            ));

        } catch (Exception e){
            return ResponseEntity.status(500).body(new ServerError(e));
        }
    }


    @Data
    public static class GetPostRequest{
        int page;
        public String toString(){
            return "GetPostRequest{" +
                    "page=" + page +
                    '}';
        }
    }
    @AllArgsConstructor
    @Data
    public static class GetPostResponse{
        Post post;
        int replyCount;
        List<Reply> replies;
    }

    @PostMapping("/{pid}")
    public ResponseEntity<?> getPost(
        @PathVariable("pid") int pid,
        @RequestBody(required = false) GetPostRequest data
    )
    {
        try {
            Log.info("PostController::getPost\npid:"+pid+"\npageint:"+data);
            Post post = getPostById(pid);
            // 检查帖子是否存在
            if (post == null){
                return ResponseEntity.status(400).body(new ErrorResponse("帖子不存在或已被删除"));
            }
            int page;
            // page = Objects.requireNonNullElse(pageint, 1);
            // page = data.getPage();
            if (data == null){
                page = 1;
            } else {
                page = data.getPage();
            }
            int size = 50;
            int offset = (page - 1) * size;
            // 获取回复列表内容
            List<Reply> replies = replyService.getRepliesByPostid(pid, offset, size);

            return ResponseEntity.ok().body(new GetPostResponse(
                post, 
                replies.size(),
                replies
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ServerError(e));
        }
    }

    @PostMapping("/test")
    public ResponseEntity<?> test(){
        // 该函数用于测试log功能是否好用
        try {
            Integer i = null;
            int ii = i;
            return ResponseEntity.ok("test" + ii);
        } catch (Exception e){
            return ResponseEntity.status(500).body(new ServerError(e));
        }
    }
}
