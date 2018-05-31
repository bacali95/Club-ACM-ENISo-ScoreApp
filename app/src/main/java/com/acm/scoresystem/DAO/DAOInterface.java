package com.acm.scoresystem.DAO;

import java.util.List;

public interface DAOInterface<T> {

    void add(T item);

    void edit(T item);

    void remove(T item);

    List<T> getAll();

    void removeAll();

    long countItems();
}
