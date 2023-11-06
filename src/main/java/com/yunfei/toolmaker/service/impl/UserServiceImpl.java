package com.yunfei.toolmaker.service.impl;

import com.yunfei.toolmaker.dao.UserQueryDao;
import com.yunfei.toolmaker.po.UserDo;
import com.yunfei.toolmaker.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Slf4j
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserQueryDao userQueryDao;
    @Transactional
    @Override
    public void insertUser(UserDo user) {
        userQueryDao.insert(user);
        log.info("insert data:{}", user);
    }

    @Override
    public Boolean exist(String userName) {
        UserDo userDo = new UserDo();
        userDo.setName(userName);
        List<UserDo> query = userQueryDao.query(userDo);
        return !query.isEmpty();
    }

    @Override
    public Boolean passwordMatch(String userName, String password) {
        UserDo userDo = new UserDo();
        userDo.setName(userName);
        List<UserDo> query = userQueryDao.query(userDo);
        if (query.isEmpty()) {
            return false;
        }
        return query.get(0).getPassword().equals(password);
    }

    @Override
    public Boolean checkAdmin(String userName) {
        UserDo userDo = new UserDo();
        userDo.setName(userName);
        List<UserDo> query = userQueryDao.query(userDo);
        if (query.isEmpty()) {
            log.info("the user:{} doesn't exist.", userName);
            return false;
        }
        return query.get(0).getAdminFlag();
    }
}
