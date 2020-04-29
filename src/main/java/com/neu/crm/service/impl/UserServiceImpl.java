package com.neu.crm.service.impl;

import com.neu.crm.bean.User;
import com.neu.crm.mapper.UserMapper;
import com.neu.crm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User login(User user) {
        return userMapper.selectOne(user);
    }

    @Override
    public boolean register(User user) {
        User dbUser=new User();
        dbUser.setUsername(user.getUsername());
        if (userMapper.selectOne(dbUser)!=null){
            return false;
        }
        userMapper.insertSelective(user);
        return true;
    }

}
