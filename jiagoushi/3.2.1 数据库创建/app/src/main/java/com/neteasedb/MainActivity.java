package com.neteasedb;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.neteasedb.db.BaseDao;
import com.neteasedb.db.BaseDaoFactory;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView(){
        findViewById(R.id.insert).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseDao baseDao = BaseDaoFactory.getInstance().getBaseDao(User.class);
                baseDao.insert(new User(1,"netease","111"));
            }
        });

        // 如何自动创建数据库
        // 如何自动创建数据表
        // 如何让用户在使用的时候非常方便
        // 将user对象里面的类名 属性 转换成 创建数据库表的sql语句
        // create table user(id integer,name text,password text);

    }
}
