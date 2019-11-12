package com.netease.hookproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "" + ((Button) v).getText(), Toast.LENGTH_SHORT).show();
            }
        });




        // 在不修改以上代码的情况下，通过Hook把 ((Button) v).getText() 内容给修改
        try {
            hook(button); // button就是View对象
        } catch (Exception e) {
            e.printStackTrace();

            Toast.makeText(this, "Hook失败" + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void hook(View view) throws Exception {

        // 之前 的 还是 用户写的 实现代码
        // 为了获取 @1 对象，需要执行 这个方法，才能拿到
        /*
            ListenerInfo getListenerInfo()
         */
        Class mViewClass = Class.forName("android.view.View");
        Method getListenerInfoMethod = mViewClass.getDeclaredMethod("getListenerInfo");
        getListenerInfoMethod.setAccessible(true); // 授权
        // 执行方法
        Object  mListenerInfo = getListenerInfoMethod.invoke(view);

        // 替 换  public OnClickListener mOnClickListener; 替换我们自己的
        Class mListenerInfoClass = Class.forName("android.view.View$ListenerInfo");

        Field mOnClickListenerField = mListenerInfoClass.getField("mOnClickListener");

        final Object mOnClickListenerObj = mOnClickListenerField.get(mListenerInfo); // 需要@1对象


        // 1.监听 onClick，当用户点击按钮的时候-->onClick， 我们自己要先拦截这个事件
        // 动态代理
        // mOnClickListener 本质是==OnClickListener
        Object mOnClickListenerProxy = Proxy.newProxyInstance(MainActivity.class.getClassLoader(), // 1加载器

                new Class[]{View.OnClickListener.class}, // 2要监听的接口，监听什么接口，就返回什么接口

                new InvocationHandler() { // 3监听接口方法里面的回调

                    /**
                     *
                     * void onClick(View v);
                     *
                     * onClick ---> Method
                     * View v ---> Object[] args
                     *
                     * @param proxy
                     * @param method
                     * @param args
                     * @return
                     * @throws
                     */
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        // 加入了自己逻辑
                        Log.d("hook", "拦截到了 OnClickListener的方法了");
                        Button button = new Button(MainActivity.this);
                        button.setText("同学们大家好....");

                        // 让系统程序片段 --- 正常继续的执行下去
                        return method.invoke(mOnClickListenerObj, button);
                    }
                });



        // 狸猫换太子 把系统的 mOnClickListener  换成 我们自己写的 动态代理
        mOnClickListenerField.set(mListenerInfo, mOnClickListenerProxy); // 替换的 我们自己的动态代理
    }
}
