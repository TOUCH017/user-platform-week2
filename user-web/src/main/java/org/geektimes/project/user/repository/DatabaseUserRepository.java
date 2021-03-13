package org.geektimes.project.user.repository;

import org.geektimes.project.user.domian.User;

import java.util.Collection;

public class DatabaseUserRepository implements UserRepository {


    @Override
    public boolean save(User user) {
        return false;
    }

    @Override
    public boolean deleteById(Long userId) {
        return false;
    }

    @Override
    public User getById(Long userId) {
        return null;
    }

    @Override
    public User getByNameAndPassword(String userName, String password) {
        return null;
    }

    @Override
    public Collection<User> getAll() {
        return null;
    }

    @Override
    public boolean update(User user) {
        return false;
    }
}
