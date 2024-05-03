package terakoya.terakoyabe.controller;


import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import terakoya.terakoyabe.Service.UserService;
import terakoya.terakoyabe.entity.User;
import terakoya.terakoyabe.mapper.UserMapper;
import terakoya.terakoyabe.setting.Setting;
import terakoya.terakoyabe.util.ErrorResponse;
import terakoya.terakoyabe.util.*;
import terakoya.terakoyabe.util.ServerError;

import java.util.List;

@RestController
@CrossOrigin(origins = Setting.SOURCE_SITE, maxAge = 3600, allowCredentials = "true")
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserMapper userMapper;
    @Autowired
    UserService userService;


    @Data
    public static class LoginRequest {
        String  username;
        String  password;
        String  token;
    }

    @AllArgsConstructor
    @Data
    public static class LoginResponse {
        int id;
        String token;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest data, HttpSession session) {
        try {
            // 验证用户名和密码是否为空
            if (data == null || data.getUsername() == null || data.getPassword() == null){
                return ResponseEntity.status(401).body(new ErrorResponse("用户名和密码不能为空"));
            }
            User user = userMapper.getUserByUsernameAndPassword(data.getUsername(), data.getPassword());
            if (user == null){
                return ResponseEntity.status(401).body(new ErrorResponse("用户名或密码错误"));
            } else {
                // 生成 token
                String token = TokenController.generateToken(user.getUid());
                // 将 token 写入 session
                session.setAttribute("token", token);
                // 打印登录信息
                Log.info("登录成功，用户：" + user.toString());
                // 打印 token 信息
                Log.info("生成 token：" + token);
                return ResponseEntity.ok().body(new LoginResponse(user.getUid(), token));
            }
        } catch(Exception e){
            return ResponseEntity.status(500).body(new ServerError(e));
        }
    }


    static public boolean isUsernameValid(String username){
        // 用户名长度介于 1 和 15 之间
        // 是由大小写英文字母、数字、汉字、特殊字符组成的字符串
        // 不含有空格
        // 字符串不能只有数字
        if (username == null || username.isEmpty() || username.length() > 15){
            return false;
        }

        Log.info(username);

        boolean isAllNumber = true;
        for (int i = 0; i < username.length(); i++){
            char c = username.charAt(i);
            // 如果 c 不是数字
            if (!Character.isDigit(c)){
                isAllNumber = false;
            }  
        }
        // 如果字符串全部是数字，则返回 false，否则返回 true
        return !isAllNumber;
    }

    static public boolean isPasswordValid(String password){
        // TODO:完善密码验证规则
        return true;
    }

    public void insertUser(String username, String password){
        do{
            try { 
                userMapper.insertUser(username, password, 1);
                break;
            } catch (org.springframework.dao.DuplicateKeyException e){
                continue;
            }
        } while (true);
    }

 

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody LoginRequest data) {
        try {
            if (data == null){
                return ResponseEntity.status(400).body(new ErrorResponse("用户名和密码不能为空"));
            }
            String username = data.getUsername();
            String password = data.getPassword();
            if (username == null || password == null){
                return ResponseEntity.status(400).body(new ErrorResponse("用户名和密码不能为空"));
            }
            // 验证用户名是否存在
            User user = userMapper.getUserByUsername(username);
            if (user != null){
                // 用户名已存在
                return ResponseEntity.status(400).body(new ErrorResponse("用户名已存在"));
            } else {
                // 验证用户名是否满足设定条件
                if (!isUsernameValid(username)){
                    return ResponseEntity.status(400).body(new ErrorResponse("用户名不满足设定条件"));
                }
                // 验证密码是否满足设定条件
                if (!isPasswordValid(password)){
                    return ResponseEntity.status(400).body(new ErrorResponse("密码不满足设定条件"));
                }
                insertUser(username, password);
                // userMapper.insertUser(username, password, 1);
                   
                return ResponseEntity.ok("注册成功");
            }
        } catch(Exception e){
            return ResponseEntity.status(500).body(new ServerError(e));
        }
    }

    // 登出
    @PostMapping("/logout")
    public ResponseEntity<?> logout(
        String token
    )
    {
        try {

            TokenController.invalidateToken(token);
            // 登出成功
            return ResponseEntity.ok("登出成功");
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ServerError(e));
        }
    }

    // 信息修改
    @PostMapping("/updateAuth")
    public ResponseEntity<?> updateAuth(
        @RequestBody LoginRequest data
    )
    {
        try {
            String token = data.getToken();
            // 验证 token
            if (!TokenController.verifyToken(token)){
                return ResponseEntity.status(401).body(new ErrorResponse("token 验证失败，请重新登录"));
            }

            String username = data.getUsername();
            String password = data.getPassword();
            // 验证用户名是否存在
            User user = userMapper.getUserByUsername(username);
            // 获取操作用户的 uid
            int uid = TokenController.getUid(token);
            // 如果用户名已存在
            if (user != null){
                // 判断该用户是否为当前用户
                if (user.getUid() != uid){
                    return ResponseEntity.status(401).body(new ErrorResponse("该用户名已存在"));
                } 
                // 否则说明，虽然用户名已存在，但是是同一个用户，可以修改密码
            }
            // 验证用户名是否满足设定条件
            if (!isUsernameValid(username)) {
                return ResponseEntity.status(400).body(new ErrorResponse("用户名不满足设定条件"));
            }
            // 验证密码是否满足设定条件
            if (!isPasswordValid(password)){
                return ResponseEntity.status(400).body(new ErrorResponse("密码不满足设定条件"));
            }
            if (user.getPassword().equals(password)){
                // 密码未修改
                return ResponseEntity.status(400).body(new ErrorResponse("密码未修改"));
            }
            // 修改用户信息
            userMapper.updateUser(uid, username, password, user.getRole());

            // 增加 Access-Control-Allow-Credentials 头信息，允许跨域请求携带 cookie
            return ResponseEntity.ok("权限修改成功");
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ServerError(e));
        }
    }

    @AllArgsConstructor
    @Data
    static public class UpdateRoleRequest{
        int useridint;
        String  role;
        String  token;
    }

    // 权限修改
    @PostMapping("/updateRole")
    public ResponseEntity<?> updateRole(
        @RequestBody UpdateRoleRequest data
    )
    {
        try {
            String token = data.getToken();
            // 验证 token
            if (!TokenController.verifyToken(token)){
                return ResponseEntity.status(401).body(new ErrorResponse("token 验证失败，请重新登录"));
            }

            int authid = TokenController.getUid(token);
            // 验证用户权限是否为管理员
            if (!userService.isAdmin(authid)){
                return ResponseEntity.status(403).body(new ErrorResponse("权限不足"));
            }

            int uid;
            int newRole;
            // 验证权限是否合法
            if (false){
                return ResponseEntity.status(400).body(new ErrorResponse("用户 id 不能为空"));
            } else {
                uid = data.getUseridint();
            }
            if (data.getRole() == null){
                return ResponseEntity.status(400).body(new ErrorResponse("权限不能为空"));
            } else {
                newRole = Integer.parseInt(data.getRole());
            }
            if (newRole < 1 || newRole > 2){
                return ResponseEntity.status(400).body(new ErrorResponse("权限不合法"));
            }

            // 修改用户权限
            userMapper.updateUserRole(uid, newRole);

            return ResponseEntity.ok("权限修改成功");

        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ServerError(e));
        }
    }

    @Data
    public static class UserListRequest{
        int page;
        String keyword;
        String token;
    }

    @Data
    public static class UserListResponse {
        int postCount;
        List<User> users;
    }

    // 管理后台用户
    @PostMapping("/list")
    public ResponseEntity<?> list(
        @RequestBody UserListRequest data
    )
    {
        try {
            String token = data.getToken();
            // 验证 token
            if (!TokenController.verifyToken(token)){
                return ResponseEntity.status(401).body(new ErrorResponse("token 验证失败，请重新登录"));
            }
            int authid = TokenController.getUid(token);
            // 验证用户权限是否为管理员
            if (!userService.isAdmin(authid)){
                return ResponseEntity.status(403).body(new ErrorResponse("权限不足"));
            }

            // int page = data.getPage();
            int page = 1;
            if (false){
                page = data.getPage();
            }
            String keyword = data.getKeyword();

            if (keyword == null){
                keyword = "";
            }

            int size = 20;
            int offset = (page - 1) * size;
            List<User> users = userMapper.getUserList(offset, size, keyword);


            UserListResponse response = new UserListResponse();
            response.setPostCount(userMapper.getPostCountByUser(keyword));
            response.setUsers(users);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ServerError(e));
        }
    }

}
