package terakoya.terakoyabe.test;

import terakoya.terakoyabe.controller.UserController;
import terakoya.terakoyabe.util.Log;

public class testToken {
    public static void main(String[] args) {
        /*
        // 帮我写个测试代码，用于测试TokenController类的功能
        try {
            String token = TokenController.generateToken("test");
            Log.info(token);
            Log.info(TokenController.verifyToken("test", "123"));
            Log.info(TokenController.verifyToken("test", token));
            TokenController.invalidateToken("test", token);   
            Log.info(TokenController.verifyToken("test", token));
            token = TokenController.generateToken("test");
            Log.info(token);
            Log.info(TokenController.verifyToken("test", "123"));
            Log.info(TokenController.verifyToken("test", token));
            TokenController.invalidateToken("test", "123");   
            Log.info(TokenController.verifyToken("test", "123"));

        } catch (Exception e){
            e.printStackTrace();
        }*/

        Log.info(Boolean.toString(UserController.isUsernameValid("123456")));
    }
}
