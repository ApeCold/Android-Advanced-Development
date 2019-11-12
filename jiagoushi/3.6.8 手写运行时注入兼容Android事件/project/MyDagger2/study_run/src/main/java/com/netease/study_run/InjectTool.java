package com.netease.study_run;

import android.util.Log;
import android.view.View;

import com.netease.study_run.annotation.BindView;
import com.netease.study_run.annotation.Click;
import com.netease.study_run.annotation.ContentView;
import com.netease.study_run.annotation_common.OnBaseCommon;
import com.netease.study_run.annotation_common.OnClickCommon;
import com.netease.study_run.annotation_common.OnClickLongCommon;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class InjectTool {

    private static final String TAG = InjectTool.class.getSimpleName();

    public static void inject(Object object) {
        injectSetContentView(object);

        injectBindView(object);

        injectClick(object);

        injectEvnent(object); // 兼容Android一系列事件
    }

    /**
     * 兼容Android一系列事件，考虑到扩展
     */
    private static void injectEvnent(final Object mainActivityObject) {

        Class<?> mainActivityClass = mainActivityObject.getClass();

        Method[] declaredMethods = mainActivityClass.getDeclaredMethods();

        for (final Method declaredMethod : declaredMethods) { // 遍历Activity的方法
            declaredMethod.setAccessible(true);

            // 以前的方式，这种方式是不可以的，因为这种方式是具体的获取， 不能具体的获取，因为是动态变化的
           /* Click click = declaredMethod.getAnnotation(Click.class);
            OnClickLongCommon onClickLongCommon = declaredMethod.getAnnotation(OnClickLongCommon.class);
            OnClickCommon onClickCommon = declaredMethod.getAnnotation(OnClickCommon.class);*/

            // 只要我们的注解有 @OnBaseCommon，就代表可以使用，必须要包含OnBaseCommon

            // 这是找不到的，因为有多个注解的情况
            /*OnBaseCommon onBaseCommon = declaredMethod.getAnnotation(OnBaseCommon.class);
            if (onBaseCommon == null) {
            }*/

            Annotation[] annotations = declaredMethod.getAnnotations();//  @Deprecated   @OnClickCommon(R.id.bt_t1)
            for (Annotation annotation : annotations) {
                Class<? extends Annotation> annotationType = annotation.annotationType();

                // 寻找是否有 OnBaseCommon
                OnBaseCommon onBaseCommon = annotationType.getAnnotation(OnBaseCommon.class);
                if (onBaseCommon == null) {
                    // 结束本次循环，进入下一个循环
                    Log.d(TAG, "OnBaseCommon == null ");
                    continue;
                }

                // 证明已经找到了 含有OnBaseCommon的注解
                // 获取事件三要素
                String setCommonListener = onBaseCommon.setCommonListener(); // setOnClickListener
                Class setCommonObjectListener = onBaseCommon.setCommonObjectListener(); // View.OnClickListener.class
                String callbackMethod = onBaseCommon.callbackMethod(); // onClick(View v) {}

                // 之前的方式,，由于是动态变化的，不能这样拿，所以才使用反射
                // annotationType.getAnnotation(OnClickLongCommon.class).value();

                // get R.id.bt_t1 == 8865551
                try {
                    Method valueMethod = annotationType.getDeclaredMethod("value");
                    valueMethod.setAccessible(true);
                    int value = (int) valueMethod.invoke(annotation);

                    // 实例化 R.id.bt_t1 得到View
                    // findViewById(8865551);
                    Method findViewByIdMethod = mainActivityClass.getMethod("findViewById", int.class);
                    // View view = findViewById(8865551);
                    Object viewObject = findViewByIdMethod.invoke(mainActivityObject, value);

                    // Method mViewMethod = view.getClass().getMethod("setOnClickListener", View.OnClickListener.class);
                    Method mViewMethod = viewObject.getClass().getMethod(setCommonListener, setCommonObjectListener);

                    // view.setOnClicListener(new View.OnClickListener...)

                    // 动态代理
                    Object proxy = Proxy.newProxyInstance(
                            setCommonObjectListener.getClassLoader(),
                            new Class[]{setCommonObjectListener}, // OnClickListener
                            new InvocationHandler() {
                                @Override
                                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                                    // 执行MainActivity里面的方法
                                    return declaredMethod.invoke(mainActivityObject, null);
                                }
                            });

                    // 狸猫换太子  换成我们的动态代理
                    mViewMethod.invoke(viewObject, proxy);

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }

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
