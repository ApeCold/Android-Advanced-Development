package com.neteasedb.subdb;

import android.database.sqlite.SQLiteDatabase;

import com.neteasedb.db.BaseDao;
import com.neteasedb.db.BaseDaoFactory;

public class BaseDaoSubFactory extends BaseDaoFactory {

    private static final BaseDaoSubFactory instance = new BaseDaoSubFactory();
    public static BaseDaoSubFactory getInstance(){
        return instance;
    }

    // 定义一个用于实现分库的数据库对象
    protected SQLiteDatabase subSqliteDatabase;

    // 生产basedao对象
    public <T extends BaseDao<M>,M> T getBaseDao(Class<T> daoClass, Class<M> entityClass){
        BaseDao baseDao = null;
        if(map.get(PrivateDatabaseEnums.database.getValue()) != null){
            return (T)map.get(PrivateDatabaseEnums.database.getValue());
        }
        subSqliteDatabase=SQLiteDatabase.openOrCreateDatabase(PrivateDatabaseEnums.database.getValue(),null);
        try {
            baseDao = daoClass.newInstance();
            baseDao.init(subSqliteDatabase,entityClass);

            map.put(PrivateDatabaseEnums.database.getValue(),baseDao);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return (T)baseDao;
    }

}
