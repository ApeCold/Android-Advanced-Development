package com.neteasedb.subdb;


import com.neteasedb.User;
import com.neteasedb.db.BaseDaoFactory;
import com.neteasedb.db.UserDao;

import java.io.File;

// 用来产生私有数据库存放的位置
public enum PrivateDatabaseEnums {

    database("");

    private String value;
    PrivateDatabaseEnums(String value){

    }

    public String getValue(){
        UserDao userDao = BaseDaoFactory.getInstance().getBaseDao(UserDao.class, User.class);
        if(userDao!=null){
            User curUser = userDao.getCurrentUser();
            if(curUser != null){
                File file = new File("data/data/com.neteasedb/");
                if(!file.exists()){
                    file.mkdirs();
                }
                return file.getAbsolutePath()+"/u_"+curUser.getId()+"_private.db";
            }
        }
        return null;
    }

}
