package com.yunfei.toolmaker.service;

import com.yunfei.toolmaker.po.UserDo;

public interface UserService {
    void insertUser(UserDo user);
    Boolean exist(String userName);

    Boolean passwordMatch(String userName, String password);

    Boolean checkAdmin(String userName);
}
