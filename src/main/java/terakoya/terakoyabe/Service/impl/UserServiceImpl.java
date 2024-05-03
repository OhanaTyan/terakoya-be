package terakoya.terakoyabe.Service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import terakoya.terakoyabe.Service.UserService;
import terakoya.terakoyabe.entity.User;
import terakoya.terakoyabe.mapper.UserMapper;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public boolean isAdmin(int uid) {
        User user = userMapper.getUserById(uid);
        if (user == null){
            return false;
        } 
        return user.getRole() == 2;
    }

    @Override
    public int getUseridByUsername(String poster) {
        User user = userMapper.getUserByUsername(poster);
        if (user == null){
            return -1;
        }
        return user.getUid();
    }

    @Override
    public boolean isUseridExists(int userid) {
        User user = userMapper.getUserById(userid);
        if (user == null){
            return false;
        } else {
            return true;
        }
    }

    
}
