package terakoya.terakoyabe;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/user")
public class UserController {

    // 服务器内部失败
    ResponseEntity<?> serverError(Exception e){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(e.getMessage());
    }

    // 登录成功
    ResponseEntity<?> loginSuccess(){
        String token = "123";

        return ResponseEntity.ok()
                .body(token);

//        return ResponseEntity.ok().build();
    }
    // 登录失败，返回401，并将errorMessage设置为
    ResponseEntity<?> loginFail(String errorMessage){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(errorMessage);
    }


    /*
    function handleClick1() {
    let username = "test";
    let password = "123";
    axios.post(
        "http://localhost:9999/api/user/login",
        qs.stringify({
            username: username,
            password: password
        })
    ).then(res => {
        console.log(res.data);
        sessionStorage.setItem("username", username);
        sessionStorage.setItem("password", password);
    }).catch(err => {
        console.log(err);
    });
}

     */
    @PostMapping("/login")
    public ResponseEntity<?> login(String username, String password){
        try {
            System.out.print(username);
            if (username.equals("test")){
                return loginSuccess();
            } else {
                return loginFail("用户名或密码错误");
            }
        } catch (Exception e) {
            return serverError(e);
        }
    } 


}
