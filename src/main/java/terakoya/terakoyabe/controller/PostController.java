package terakoya.terakoyabe.controller;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.Data;
import terakoya.terakoyabe.MyUtil;
import terakoya.terakoyabe.Service.BoardService;
import terakoya.terakoyabe.Service.PostService;
import terakoya.terakoyabe.Service.UserService;
import terakoya.terakoyabe.entity.Post;
import terakoya.terakoyabe.entity.Reply;
import terakoya.terakoyabe.mapper.PostMapper;
import terakoya.terakoyabe.mapper.ReplyMapper;
import terakoya.terakoyabe.setting.Setting;
import terakoya.terakoyabe.util.ServerError;

@RestController
@CrossOrigin(origins = Setting.SOURCE_SITE, maxAge = 3600, allowCredentials = "true")
@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private BoardService boardService;

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Autowired
    private ReplyMapper replyMapper;


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
    }

    @AllArgsConstructor
    @Data
    public static class CreateResponse{
        int pid;
    }

    
    private void insertPost(int releaseTime, int randomValue,int posterid, int board, String title, String content) {
        do {
            try {
                postMapper.insertPost(
                    releaseTime,
                    randomValue,
                    posterid,
                    board,
                    title, 
                    content,
                    0,
                    0
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
        return postService.getPostById(pid);
    }

    
    // 检查帖子是否合法
    private boolean isPostValid(Post post) {
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
            posts = postMapper.getPostByReleaseTimeAndReplyTime(releaseTime, replyTime);
        } while (posts.isEmpty());
        
        return posts.getFirst().getId();
    }


    @RequestMapping("/create")
    public ResponseEntity<?> create(
        @RequestBody CreateRequest createRequest,
        @CookieValue(name="uid", required = false) int posterid,
        @CookieValue(name="token", required = false) String token
    )
    {
        try {
            // 验证 token
            if (!TokenController.verifyToken(posterid, token)) {
                return ResponseEntity.status(401).body(new ErrorResponse("token 验证失败，请重新登录"));
            }
            String title = createRequest.getTitle();
            String content = createRequest.getContent();
            int board = createRequest.getBoard();

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
            postMapper.updateReplyTime(id, releaseTime);

            return ResponseEntity.ok().body(new CreateResponse(id));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ServerError(e));
        }

    }

    @PostMapping("/edit")
    public ResponseEntity<?> edit(
        @RequestBody Post post,
        @CookieValue(name="uid", required = false) int uid,
        @CookieValue(name="token", required = false) String token 
    ) 
    {
        try {
            if (!TokenController.verifyToken(uid, token)){
                return ResponseEntity.status(401).body(new ErrorResponse("token 验证失败，请重新登录"));
            }

            // 验证是否是管理用户
            if (!userService.isAdmin(uid)){
                return ResponseEntity.status(403).body(new ErrorResponse("权限不足"));
            }

            System.out.println(post.toString());

            int pid = post.getId();
            // 验证帖子是否存在
            if (getPostById(pid) == null){
                return ResponseEntity.status(400).body(new ErrorResponse("帖子不存在或已被删除"));
            }

            // 验证板块是否存在
            if (!boardService.isBoardExists(post.getBoard())){
                return ResponseEntity.status(400).body(new ErrorResponse("板块不存在或已被删除"));
            }

            // 验证帖子是否合法
            if (!isPostValid(post)){
                return ResponseEntity.status(400).body(new ErrorResponse("帖子不合法"));
            }


            // 更新数据库
            postMapper.updatePost(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getBoard()
            );
            
            return ResponseEntity.ok().body(new CreateResponse(post.getId()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ServerError(e));
        }
    }

    @AllArgsConstructor
    @Data
    public static class DeleteRequest{
        int pid;
        // 如果不加下面的字段，会出现无法解析的错误
        String unused;
        /*
        DefaultHandlerExceptionResolver : Resolved [org.springframework.http.
        converter.HttpMessageNotReadableException: JSON parse error: Cannot
        construct instance of `terakoya.terakoyabe.controller
        .PostController$DeleteRequest` (although at least one Creator exists):
        cannot deserialize from Object value (no delegate- or property-based
        Creator)]
         */
    }

    @PostMapping("/delete")
    public ResponseEntity<?> delete(
        @RequestBody DeleteRequest data,
        @CookieValue(name="uid", required = false) int uid,
        @CookieValue(name="token", required = false) String token 
    ) 
    {
        try {
            if (!TokenController.verifyToken(uid, token)){
                return ResponseEntity.status(401).body(new ErrorResponse("token 验证失败，请重新登录"));
            }
            // 验证是否是管理员
            boolean isAdmin = userService.isAdmin(uid);

            int pid = data.getPid();

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
            

            postMapper.updatePost(
                pid,
                "该帖子已被删除",
                "该帖子已被删除",
                -1
            );
            replyMapper.deleteByPostId(pid);
            
            return ResponseEntity.ok().body("删除成功");
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ServerError(e));
        }
    }

    @AllArgsConstructor
    @Data
    public static class GetLatestRequest{
        Integer bid;
        Integer page;
    }

    @GetMapping("/latest")
    public ResponseEntity<?> getLatest(
        @RequestBody(required = false) GetLatestRequest data
    )
    {
        try {
            int bid;
            int page;
            if (data == null){
                bid = 0;
                page = 1;
            } else {
                if (data.getBid() == null)  bid = 0;
                else                        bid = data.getBid();
                if (data.getPage() == null) page = 1;
                else                        page = data.getPage();
            }
            int size = 50;
            int offset = (page - 1) * size;

            List<Post> posts;
            if (bid == 0){
                // 从所有帖子中查询最新帖子
                posts = postMapper.getLatestPosts(offset, size);
            }
            else {
                // 检查板块是否存在

                if (!boardService.isBoardExists(bid)){
                    return ResponseEntity.status(400).body(new ErrorResponse("板块不存在或已被删除"));
                }

                // 从指定板块中查询最新帖子
                posts = postMapper.getLatestPostsByBoard(bid, offset, size);
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
        Integer bid;
        String poster;
        String keyword;
    }

    @AllArgsConstructor
    @Data
    public static class GetListResponse{
        int postCount;
        List<Post> posts;
    }

    @PostMapping("/list")
    public ResponseEntity<?> getList(
        @RequestBody(required = false) GetListRequest data,
        @CookieValue(name="uid", required = false) int uid,
        @CookieValue(name="token", required = false) String token
    )
    {  
        try{
            // 验证 token
            if (!TokenController.verifyToken(uid, token)){
                return ResponseEntity.status(401).body(new ErrorResponse("token 验证失败，请重新登录"));
            }
            // 验证是否是管理员
            if (!userService.isAdmin(uid)){
                return ResponseEntity.status(403).body(new ErrorResponse("权限不足"));
            }

            int page = data.getPage();
            int size = 50;
            int offset = (page - 1) * size;
            int bid;
            if (data.getBid() == null){
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
                    posterid = userService.getUserIdByUsername(poster);
                }
            }
            String keyword;
            if (data.getKeyword() == null){
                keyword = "";
            } else {
                keyword = data.getKeyword();
            }

            List<Post> posts;
            
            posts = postMapper.getPostsByBoardPosterAndKeyword(
                bid,
                posterid,
                keyword,
                offset,
                size
            );

            int postCount = postMapper.getPostCountByBoardPosterAndKeyword(
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


    @AllArgsConstructor
    @Data
    public static class GetPostResponse{
        Post post;
        int replyCount;
        List<Reply> replies;
    }

    @GetMapping("/{pid}")
    public ResponseEntity<?> getPost(
        @PathVariable("pid") int pid,
        @RequestBody(required = false) Integer pageInteger
    )
    {
        try {
            Post post = getPostById(pid);
            // 检查帖子是否存在
            if (post == null){
                return ResponseEntity.status(400).body(new ErrorResponse("帖子不存在或已被删除"));
            }
            int page;
            page = Objects.requireNonNullElse(pageInteger, 1);
            int size = 50;
            int offset = (page - 1) * size;
            // 获取回复列表内容
            List<Reply> replies = replyMapper.getReplyByPostId(pid, offset, size);

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
        // 该函数用于测试异常功能是否好用
        try {
            replyMapper.deleteReply(-12);
            return ResponseEntity.ok("test");
        } catch (Exception e){
            return ResponseEntity.ok("test");
        }
    }
}
