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

    
}
