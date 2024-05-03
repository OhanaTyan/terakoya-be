package terakoya.terakoyabe.controller;

import org.springframework.beans.factory.annotation.Autowired;
import terakoya.terakoyabe.mapper.UserMapper;
import terakoya.terakoyabe.util.Log;

import java.util.HashMap;
import java.util.Map;

public class TokenController {

    @Autowired
    UserMapper userMapper;

    // token -> id
    private static final Map<String, Integer> tokenMap = new HashMap<>();
    private static final Map<Integer, String> idMap = new HashMap<>();


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
        if (idMap.containsKey(uid)){
            // 使token失效
            invalidateToken(idMap.get(uid));
        }

        String token = generateString();
        while (tokenMap.containsKey(token)){
            token = generateString();
        }
        tokenMap.put(token, uid);
        idMap.put(uid, token);
        return token;
    }

    public static boolean verifyToken(String token) throws Exception{
        Log.info("传入token:" + token);
        if (tokenMap.containsKey(token)){
            return true;
        } else {
            return false;
        }
    }

    public static int getUid(String token) throws Exception{
        if (tokenMap.containsKey(token)){
            return tokenMap.get(token);
        }
        return -1;
    }


    public static void invalidateToken(String token) throws Exception{
        if (tokenMap.containsKey(token)){
            int uid = tokenMap.get(token);
            tokenMap.remove(token);
            idMap.remove(uid);
        }
    }
}

    
