package com.acm.scoresystem.Repository;

import java.util.List;

public interface RepoInterface<T> {

    void add(T item);

    void edit(T item);

    void remove(T item);

    List<T> getAll();

    long countItems();

}
