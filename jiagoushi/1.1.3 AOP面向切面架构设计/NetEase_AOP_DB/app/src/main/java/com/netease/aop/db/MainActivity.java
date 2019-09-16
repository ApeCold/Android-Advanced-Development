package com.netease.aop.db;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class MainActivity extends AppCompatActivity implements DBOperation {

    private DBOperation db;
    private final static String TAG = "netease >>> ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // db = this;
        db = (DBOperation) Proxy.newProxyInstance(DBOperation.class.getClassLoader(),
                new Class[]{DBOperation.class}, new DBHander(this));
    }

    class DBHander implements InvocationHandler {

        private DBOperation db;

        DBHander(DBOperation db) {
            this.db = db;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (db != null) {
                Log.e(TAG, "操作数据库之前，开始备份……");
                save(); // 查询数据库后备份，详细过程省略
                Log.e(TAG, "数据备份完成，等待操作");
                return method.invoke(db, args);
            }
            return null;
        }
    }

    // 点击事件
    public void jump(View view) {
        // startActivity(new Intent(this, OrderActivity.class));
        // 常规写法
        // db.save();
        db.delete();
    }

    @Override
    public void insert() {
        Log.e(TAG, "新增数据");
    }

    @Override
    public void delete() {
        Log.e(TAG, "删除数据");
    }

    @Override
    public void update() {
        Log.e(TAG, "修改数据");
    }

    @Override
    public void save() {
        Log.e(TAG, "保存数据");
    }
}
