package terakoya.terakoyabe.Service;



// 这里只收容不仅仅是 UserController 接口能用到的接口
public interface UserService {

    boolean isAdmin(int uid);

    int getIdByUsername(String username);

}
