package neteases.skinproject;

import android.app.Application;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            SkinEngine.getInstance().skinApplicationInit(this);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
