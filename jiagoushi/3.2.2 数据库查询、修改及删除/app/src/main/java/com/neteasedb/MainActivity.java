package com.neteasedb;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.neteasedb.db.BaseDao;
import com.neteasedb.db.BaseDaoFactory;
import com.neteasedb.db.OrderDao;

import java.util.List;

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
//                BaseDao baseDao = BaseDaoFactory.getInstance().getBaseDao(User.class);
//                baseDao.insert(new User(1,"netease1","111"));
//                baseDao.insert(new User(2,"netease2","111"));
//                baseDao.insert(new User(3,"netease3","111"));
//                baseDao.insert(new User(4,"netease4","111"));
//                baseDao.insert(new User(5,"netease5","111"));
//                baseDao.insert(new User(6,"netease6","111"));
            }
        });

        // 如何自动创建数据库
        // 如何自动创建数据表
        // 如何让用户在使用的时候非常方便
        // 将user对象里面的类名 属性 转换成 创建数据库表的sql语句
        // create table user(id integer,name text,password text);

        findViewById(R.id.select).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 基类  user 商品表 订单表（关联查询）
//                BaseDao baseDao = BaseDaoFactory.getInstance().getBaseDao(User.class);
//                User where = new User();
////                where.setName("netease");
//                where.setPassword("111");
//                List<User> list = baseDao.query(where);
//                Log.e("neteas database: "," list size is "+list.size());
//                for(int i=0;i<list.size();i++){
//                    System.out.println(list.get(i).toString());
//                }
            }
        });

        findViewById(R.id.update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //update tb_user where name='netease' set password='1111'
//                BaseDao baseDao = BaseDaoFactory.getInstance().getBaseDao(User.class);
//                User user = new User();
//                user.setId(2);
//                user.setName("netease111111");
//                user.setPassword("111");
//
//                User where = new User();
//                where.setId(2);
//                baseDao.update(user,where);
//                Toast.makeText(MainActivity.this,"执行成功!",Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                OrderDao orderDao = BaseDaoFactory.getInstance().getBaseDao(OrderDao.class,User.class);
//
//
////                BaseDao baseDao = BaseDaoFactory.getInstance().getBaseDao(User.class);
//                User where = new User();
//                where.setName("netease111111");
//                orderDao.delete(where);
            }
        });

    }
}
