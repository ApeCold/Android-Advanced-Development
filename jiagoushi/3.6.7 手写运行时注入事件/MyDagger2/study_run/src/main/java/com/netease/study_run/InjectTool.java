package com.netease.study_run;

import android.util.Log;
import android.view.View;

import com.netease.study_run.annotation.BindView;
import com.netease.study_run.annotation.Click;
import com.netease.study_run.annotation.ContentView;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class InjectTool {

    private static final String TAG = InjectTool.class.getSimpleName();

    public static void inject(Object object) {
        injectSetContentView(object);

        injectBindView(object);

        injectClick(object);
    }

    /**
     * 把布局里面的控件ID 和 Activity方法绑定起来，建立事件
     * @param object == MainActivity
     */
    private static void injectClick(final Object object) {

        Class<?> mainActivityClass = object.getClass();

        // 遍历MainActivity所有的方法
        Method[] declaredMethods = mainActivityClass.getDeclaredMethods();

        for (final Method declaredMethod : declaredMethods) { // ...  private void show() {}  onCreate  test111  test222
            declaredMethod.setAccessible(true);

            Click click = declaredMethod.getAnnotation(Click.class);

            if (click == null) {
                Log.d(TAG, "Click == null ");
                continue;
            }

            // get R.id.bt_test3
            int viewID = click.value();

            try {
                Method findViewByIdMethod = mainActivityClass.getMethod("findViewById", int.class);
                Object resultView = findViewByIdMethod.invoke(object, viewID); // findViewById(viewID);

                View view = (View) resultView; // View view = findViewById(viewID == R.id.bt_test3);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 执行此方法 private void show() {}
                        // declaredMethod == show
                        try {
                            declaredMethod.invoke(object);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    }

    /**
     * 把一系列控件注入到 Activity中去
     * @param object == MainActivity
     */
    private static void injectBindView(Object object) {

        Class<?> mainActivityClass = object.getClass();

        // TODO 遍历MainActivity里面所有的注解 （字段上的）
        // 遍历 MainActivity里面所有的字段
        Field[] fields = mainActivityClass.getDeclaredFields();

        for (Field field : fields) { // Button button1;   TextView textView;   String string;
            field.setAccessible(true);

            BindView bindView = field.getAnnotation(BindView.class);
            if (null == bindView) {
                Log.d(TAG, "BindView is null");
                continue; // 结束本次循环，进入下一个循环
            }

            // get R.id.bt_test1
            int viewID = bindView.value();

            // 把控件给实例化出来
            // button1 = findViewById(R.id.bt_test1);

            try {
                Method findViewByIdMethod = mainActivityClass.getMethod("findViewById", int.class);
                Object resultView = findViewByIdMethod.invoke(object, viewID); // 执行此函数---findViewById(R.id.bt_test1);

                // 给我们的字段，字段赋值了
                // button1 = findViewById(R.id.bt_test1);
                // filed = findViewById(viewID);
                field.set(object, resultView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 把布局注入到 Activity中去
     * @param object == MainActivity
     */
    private static void injectSetContentView(Object object) {

        Class<?> mMainActivityClass = object.getClass();

        // 拿到MainActivity里面的（ContentView）注解
        ContentView mContentView = mMainActivityClass.getAnnotation(ContentView.class);

        if (null == mContentView) {
            Log.d(TAG, "ContentView is null ");
            return;
        }

        // 拿到用户设置的布局ID
        int layoutID = mContentView.value();

        // 我们需要执行 setContentView(R.layout.activity_main); 把布局注入到Activity
        try {
            Method setContentViewMethod = mMainActivityClass.getMethod("setContentView", int.class);
            setContentViewMethod.invoke(object, layoutID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
