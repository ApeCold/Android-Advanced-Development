package com.neteasedb.db;

// 维护用户的共有数据

import android.util.Log;

import com.neteasedb.User;

import java.util.List;

public class UserDao extends BaseDao<User> {

    @Override
    public long insert(User entity) {
        // 查询该表中所有的用户记录
        List<User> list = query(new User());
        User where = null;
        for (User user:list){
            where = new User();
            where.setId(user.getId());
            user.setStatus(0);
            update(user,where);
            Log.e("neteasedb","用户"+user.getName()+"更改为未登录状态");
        }
        entity.setStatus(1);
        Log.e("neteasedb","用户"+entity.getName()+"登录");
        return super.insert(entity);
    }

    // 获取当前登录的User
    public User getCurrentUser(){
        User user = new User();
        user.setStatus(1);
        List<User> list = query(user);
        if(list.size() > 0){
            return list.get(0);
        }
        return null;
    }

}
