package terakoya.terakoyabe.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import terakoya.terakoyabe.entity.User;

@Mapper
public interface UserMapper {
    
    @Select("SELECT * FROM users WHERE username = #{username} AND password = #{password}")
    User getUserByUsernameAndPassword(String username, String password);

    @Select("SELECT * FROM users WHERE username = #{username}")
    User getUserByUsername(String username);

    // 增加新用户
    @Insert("INSERT INTO users (uid, username, password, role) SELECT COALESCE(MAX(uid), 0)+1, #{username}, #{password}, #{role} FROM users")
    void insertUser(String username, String password, int role);

    // 更新用户信息
    @Insert("UPDATE users SET username = #{username}, password = #{password}, role = #{role} WHERE uid = #{uid}")
    void updateUser(int uid, String username, String password, int role);

    @Insert("UPDATE users SET role = #{newRole} WHERE uid = #{uid}")
    void updateUserRole(int uid, int newRole);

    @Select("SELECT * FROM users WHERE username LIKE CONCAT(#{keyword}, '%')  ORDER BY username LIMIT #{size} OFFSET #{offset}")
    List<User> getUserList(int offset, int size, String keyword);

    @Select("SELECT COUNT(*) FROM users WHERE username LIKE CONCAT('%', #{keyword}, '%')")
    int getPostCountByUser(String keyword);

    @Select("SELECT * FROM users WHERE uid = #{uid}")
    User getUserById(int uid);

}