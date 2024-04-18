package terakoya.terakoyabe;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
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
        String token;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest data){
        try {
            if (data.username.equals("test")){
                return ResponseEntity.ok(new LoginResponse("yjsp114514"));
            } else {
                return ResponseEntity.status(401).body(new ErrorResponse("用户名或密码错误"));
            }
        } catch (Exception e) {
            return serverError(e);
        }
    }
}
