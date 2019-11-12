package neteases.skinproject;

import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.Observable;

public class SkinEngine extends Observable {

    private Application application;

    /**
     * 单例模式
     */
    private static SkinEngine instance = null;

    public static SkinEngine getInstance() {
        if (null == instance) {
            synchronized (SkinEngine.class) {
                if (null == instance) {
                    instance = new SkinEngine();
                }
            }
        }
        return instance;
    }

    private SkinEngine() {}

    /**
     * 给Application初始化的
     */
    public void skinApplicationInit(Application application) throws IllegalAccessException {
        if (this.application != null) {
            throw new IllegalAccessException("init number > 1");
        }

        this.application = application;
        // 注册Activity生命周期监听，之前讲过AOP切面
        application.registerActivityLifecycleCallbacks(new SkinActivityLifecycleCallbacks());
        // 单例初始化对象
        SkinResources.applicationInit(application);
    }

    /**
     * 用户点击 换肤按钮
     * @param skinPath
     */
    public void updateSkin(String skinPath) throws IllegalAccessException {

        SkinResources.getInstance().setSkinResources(application, skinPath);

        /**
         * 我是被观察者，通知所有的观察者
         * 点击换肤第一步：通知所有的观察者，需要换肤了
         */
        setChanged();
        notifyObservers();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void updatePhoneStatusBarAction(Activity activity) {
        activity.getWindow().setStatusBarColor(SkinResources.getInstance().getColor(R.color.colorPrimaryDark));
    }

}
