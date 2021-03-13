package org.geektimes.project.user.service;


import org.geektimes.project.user.domian.User;
import org.geektimes.project.user.sql.LocalTransactional;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.validation.Validator;

public class UserServiceImpl implements UserService {

    @Resource(name = "bean/EntityManager")
    private EntityManager entityManager;

    @Resource(name = "bean/Validator")
    private Validator validator;

    @Override
    // 默认需要事务
    @LocalTransactional
    public User register(User user) {
        // before process
//        EntityTransaction transaction = entityManager.getTransaction();
//        transaction.begin();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        // 主调用
        entityManager.persist(user);
        transaction.commit();
        return user;
    }

    @Override
    public boolean deregister(User user) {
        return false;
    }

    @Override
    @LocalTransactional
    public boolean update(User user) {
        return false;
    }

    @Override
    public User queryUserById(Long id) {
      return   entityManager.find(User.class,id);
    }

    @Override
    public User queryUserByNameAndPassword(String name, String password) {
        return null;
    }
}
