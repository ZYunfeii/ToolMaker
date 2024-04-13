package com.yunfei.toolmaker.service;

import com.yunfei.toolmaker.po.UserDo;

import java.util.List;

public interface UserService {
    void insertUser(UserDo user);
    Boolean exist(String userName);

    List<UserDo> select(UserDo userDo);

    Boolean passwordMatch(String userName, String password);

    Boolean checkAdmin(String userName);
}
