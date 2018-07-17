package com.acm.scoresystem.Repository;

import android.content.Context;

import com.acm.scoresystem.DAO.NotificationDAO;
import com.acm.scoresystem.Model.Notification;

import java.util.List;

public class NotificationRepository implements RepoInterface<Notification> {

    private NotificationDAO dao;

    public NotificationRepository(Context context) {
        this.dao = new NotificationDAO(context);
    }

    @Override
    public void add(Notification notification) {
        dao.add(notification);
    }

    @Override
    public void edit(Notification notification) {
        dao.edit(notification);
    }

    @Override
    public void remove(Notification notification) {
        dao.remove(notification);
    }

    @Override
    public List<Notification> getAll() {
        return dao.getAll();
    }

    @Override
    public long countItems() {
        return dao.countItems();
    }
}
