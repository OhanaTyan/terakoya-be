package terakoya.terakoyabe.controller;
/* 
import java.util.HashMap;
import java.util.Map;
/* 
public class TokenControllerImpl implements TokenController {
    // token -> uid
    private Map<String, String> tokenMap;

    public TokenControllerImpl() {
        if (tokenMap == null) {
            tokenMap = new HashMap<>();
        }
    }

    // 生成一个10个字符小写随机字符串
    private String generateRandomString() {
        String chars = "abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            int index = (int) (Math.random() * chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }

    @Override
    public String generateToken(String uid) {
        if (tokenMap.containsKey(uid)) {
    //        return tokenMap.get(uid);
            // 将旧token
        } 
        String token = generateRandomString();
        tokenMap.put(uid, token);
        return token;
    }

    @Override
    public boolean validateToken(String uid, String token) {
        throw new UnsupportedOperationException("Unimplemented method 'validateToken'");
    }

    @Override
    public void invalidateToken(String uid, String token) {
        throw new UnsupportedOperationException("Unimplemented method 'invalidateToken'");
    }

}
*/