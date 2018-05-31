package com.acm.scoresystem.Repository;

import android.content.Context;

import com.acm.scoresystem.DAO.UserDAO;
import com.acm.scoresystem.Model.User;

import java.util.List;

public class UserRepository implements RepoInterface<User> {
    private UserDAO dao;

    public UserRepository(Context context) {
        this.dao = new UserDAO(context);
    }

    @Override
    public void add(User user) {
        dao.add(user);
    }

    @Override
    public void edit(User user) {
        dao.edit(user);
    }

    @Override
    public void remove(User user) {
        dao.remove(user);
    }

    public User getFirst() {
        return dao.getFirst();
    }

    @Override
    public List<User> getAll() {
        return dao.getAll();
    }

    @Override
    public long countItems() {
        return dao.countItems();
    }
}
