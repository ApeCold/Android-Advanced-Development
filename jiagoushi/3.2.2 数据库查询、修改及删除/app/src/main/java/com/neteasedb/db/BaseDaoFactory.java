package com.neteasedb.db;

import android.database.sqlite.SQLiteDatabase;

public class BaseDaoFactory {

    private static final BaseDaoFactory instance = new BaseDaoFactory();
    public static BaseDaoFactory getInstance(){
        return instance;
    }
    private SQLiteDatabase sqLiteDatabase;
    private String sqlitePath;
    private BaseDaoFactory(){
        sqlitePath = "data/data/com.neteasedb/ne.db";
        sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(sqlitePath,null);
    }


    // 生产basedao对象
    public <T extends BaseDao<M>,M> T getBaseDao(Class<T> daoClass,Class<M> entityClass){
        BaseDao baseDao = null;
        try {
            baseDao = daoClass.newInstance();
            baseDao.init(sqLiteDatabase,entityClass);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return (T)baseDao;
    }

}
