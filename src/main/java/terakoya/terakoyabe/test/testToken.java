package terakoya.terakoyabe.test;

import terakoya.terakoyabe.controller.UserController;

public class testToken {
    public static void main(String[] args) {
        /*
        // 帮我写个测试代码，用于测试TokenController类的功能
        try {
            String token = TokenController.generateToken("test");
            System.out.println(token);
            System.out.println(TokenController.verifyToken("test", "123"));
            System.out.println(TokenController.verifyToken("test", token));
            TokenController.invalidateToken("test", token);   
            System.out.println(TokenController.verifyToken("test", token));
            token = TokenController.generateToken("test");
            System.out.println(token);
            System.out.println(TokenController.verifyToken("test", "123"));
            System.out.println(TokenController.verifyToken("test", token));
            TokenController.invalidateToken("test", "123");   
            System.out.println(TokenController.verifyToken("test", "123"));

        } catch (Exception e){
            e.printStackTrace();
        }*/

        System.out.println(UserController.isUsernameValid("123456"));
    }
}
