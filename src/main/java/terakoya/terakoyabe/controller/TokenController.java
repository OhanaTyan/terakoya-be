package terakoya.terakoyabe.controller;

import org.springframework.beans.factory.annotation.Autowired;
import terakoya.terakoyabe.mapper.UserMapper;

import java.util.HashMap;
import java.util.Map;

public class TokenController {
    // id -> token

    @Autowired
    UserMapper userMapper;

    private static Map<String, String> tokenMap;

    static{
        tokenMap = new HashMap<String, String>();
    }

    // 获取长度为10的随机字母字符串
    private static String generateString(){
        String str = "abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++){
            int index = (int)(Math.random() * str.length());
            sb.append(str.charAt(index));
        }
        return sb.toString();
    }


    public static String generateToken(int uid) throws Exception{
        String uidStr = Integer.toString(uid);
        if (tokenMap.get(uidStr) != null){
            tokenMap.remove(uidStr);
        }
        String token = generateString();
        tokenMap.put(uidStr, token);
        return token;
    }

    public static boolean verifyToken(int uid, String token) throws Exception{
        
        System.out.println(uid);
        System.out.println(token);
        String uidStr = Integer.toString(uid);
        if (tokenMap.get(uidStr) == null){
            return false;
        }
        return tokenMap.get(uidStr).equals(token);
    }


    public static void invalidateToken(String uid, String token) throws Exception{
        if (tokenMap.get(uid) == null){
            return;
        }
        if (tokenMap.get(uid).equals(token)){
            tokenMap.remove(uid);
        }
    }
}

    
