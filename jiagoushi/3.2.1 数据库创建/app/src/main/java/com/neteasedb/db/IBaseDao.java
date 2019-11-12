package com.neteasedb.db;

public interface IBaseDao<T> {

    long insert(T entity);

}
