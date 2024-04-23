package terakoya.terakoyabe.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.Data;
import terakoya.terakoyabe.Service.BoardService;
import terakoya.terakoyabe.Service.UserService;
import terakoya.terakoyabe.entity.Post;
import terakoya.terakoyabe.mapper.PostMapper;
import terakoya.terakoyabe.setting.Setting;

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

    // 服务器内部错误
    ResponseEntity<?> serverError(Exception e){
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(e.toString());
    }

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

    // 生成一个10位的随机字符串
    private String generateRandomString(int length) {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }


    synchronized static private void insertPost(PostMapper postMapper, int releaseTime, int randomValue,int posterid, int board, String title, String content) {
        postMapper.insertPost(releaseTime, randomValue, posterid, board, title, content, board, randomValue);
    }

    // 检查帖子是否存在
    private boolean isPostExists(int pid) {
        List<Post> posts = postMapper.getPostById(pid);
        if (posts.isEmpty()){
            return false;
        } else {
            return true;
        }
    }

    // 检查帖子是否合法
    private boolean isPostValid(Post post) {
        return isPostValid(post.getTitle(), post.getContent());
    }

    private boolean isPostValid(String title, String content){
        // TODO: 添加验证帖子是否合法逻辑
        return true;
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
                return ResponseEntity.status(400).body(new ErrorResponse("板块不存在"));
            }

            // 验证帖子是否合法


            int releaseTime = (int) (System.currentTimeMillis() / 1000);

            int randomValue = (int) (Math.random() * 1000000000);
            
            // 打印调试信息

            // 插入数据库
            insertPost(
                postMapper,
                releaseTime,
                randomValue,
                posterid,
                board,
                title,
                content
            );
           
            int i=0;
            List<Post> posts;
            do {
                System.out.println("waiting for reply" + i);
                i++;
                // 等待 500ms
                Thread.sleep(500);
                posts = postMapper.getPostByReleaseTimeAndReplyTime(releaseTime, randomValue);
            } while (posts.isEmpty());

            int id = posts.getFirst().getId();

            // 将 id 对应的表的 replytime 设为 releaseTime
            postMapper.updateReplyTime(id, releaseTime);

            return ResponseEntity.ok().body(new CreateResponse(posts.getFirst().getId()));
        } catch (Exception e) {
            return serverError(e);
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
            if (!isPostExists(pid)){
                return ResponseEntity.status(400).body(new ErrorResponse("帖子不存在"));
            }

            // 验证板块是否存在
            if (!boardService.isBoardExists(post.getBoard())){
                return ResponseEntity.status(400).body(new ErrorResponse("板块不存在"));
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
            return serverError(e);
        }
    }

    
    
}
