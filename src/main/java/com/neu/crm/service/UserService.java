package com.neu.crm.service;

import com.neu.crm.bean.User;

public interface UserService {
    User login(User user);

    boolean register(User user);

}
