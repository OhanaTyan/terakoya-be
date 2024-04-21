package terakoya.terakoyabe.deprecated;
/* 
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.catalina.connector.Response;
import org.springframework.boot.autoconfigure.jms.JmsProperties.Listener.Session;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:5173/", maxAge = 3600, allowCredentials = "true")
@RequestMapping("/user")
public class UserController {

    // 服务器内部失败
    ResponseEntity<?> serverError(Exception e){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(e.getMessage());
    }

    @AllArgsConstructor
    @Data
    public static class ErrorResponse {
        String message;
    }

    @Data
    public static class LoginRequest {
        String username;
        String password;
    }

    @AllArgsConstructor
    @Data
    public static class LoginResponse {
        int id;
        String token;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest data, HttpServletResponse response){
        try {
            if (data.username.equals("test") && data.password.equals("123")){
                // 将token存入cookie中
                Cookie cookie = new Cookie("token", "token");
                cookie.setPath("/");
                response.addCookie(cookie);
                return ResponseEntity.ok(new LoginResponse(123, "token"));
            } else {
                return ResponseEntity.status(401).body(new ErrorResponse("用户名或密码错误"));
            }
        } catch (Exception e){
            return serverError(e);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody LoginRequest data){
        try {
            if (data.username.equals("test")) {
                return ResponseEntity.status(401).body(new ErrorResponse("用户名已存在"));
            } else if (data.password.length() < 3) {
                return ResponseEntity.status(400).body(new ErrorResponse("用户名或密码不合法"));
            } else {
                return ResponseEntity.ok().body("注册成功");
            }
        } catch (Exception e){
            return serverError(e);
        }
    }

    // 测试能否收到cookie
    
    @PostMapping("/test")
    public ResponseEntity<?> test(@CookieValue(value="token") String token){
        try {
            if (token == null) {
                return ResponseEntity.status(401).body(new ErrorResponse("请先登录"));
            } else {
                return ResponseEntity.ok().body("测试成功");
            }
        } catch (Exception e){
            return serverError(e);
        }
    }

    // 测试能否发送cookie
    @PostMapping("/test2")
    public ResponseEntity<?> test2(HttpServletResponse response){
        try {
            Cookie cookie = new Cookie("token", "token");
            cookie.setPath("/");
            response.addCookie(cookie);
            return ResponseEntity.ok().body("测试成功");
        } catch (Exception e){
            return serverError(e);
        }
    }
    
    // 测试能否删除cookie
    @PostMapping("/test3")
    public ResponseEntity<?> test3(HttpServletResponse response){
        try {
            Cookie cookie = new Cookie("token", "");
            cookie.setPath("/");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
            return ResponseEntity.ok().body("测试成功");
        } catch (Exception e){
            return serverError(e);
        }
    }

}

*/