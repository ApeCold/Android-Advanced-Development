package com.netease.pluginhookandroid9;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Time: 2019-08-10
 * Author: Liudeli
 * Description: 即将要加载的时候，需要把ProxyActivity 给 换回来，换成目标LoginActivity，我们也称为【还原操作】
 */
public class ActivityThreadmHRestore {

    /**
     * TODO 同学们，注意：此方法 适用于 21以下的版本 以及 21_22_23_24_25  26_27_28 等系统版本
     * @param mContext
     * @throws Exception
     */
    public static void mActivityThreadmHAction(Context mContext) throws Exception {
        if (AndroidSdkVersion.isAndroidOS_26_27_28()) {
            do_26_27_28_mHRestore();
        } else if (AndroidSdkVersion.isAndroidOS_21_22_23_24_25()){
            do_21_22_23_24_25_mHRestore();
        } else {
            throw new IllegalStateException("实在是没有检测到这种系统，需要对这种系统单独处理...");
        }
    }


    /**
     * 高版本 》》》》》》》》》》》》》》》》》》》》》》》》》》》》》》》》》》》》》
     */

    /**
     * TODO 同学们看到此方法，应该明白，就是专门给 26_27_28 系统版本 做【还原操作】的
     */
    private final static void do_26_27_28_mHRestore() throws Exception {
        // @1 怎么得到？ 看源码..
        Class mActivityThreadClass = Class.forName("android.app.ActivityThread");
        Object mActivityThread = mActivityThreadClass.getMethod("currentActivityThread").invoke(null);
        Field mHField = mActivityThreadClass.getDeclaredField("mH");
        mHField.setAccessible(true);
        Object mH = mHField.get(mActivityThread);

        Field mCallbackField = Handler.class.getDeclaredField("mCallback");
        mCallbackField.setAccessible(true);
        // @1  @2
        // 把系统中的Handler.Callback实现 替换成 我们自己写的Custom_26_27_28_Callback，主动权才在我们手上
        mCallbackField.set(mH, new Custom_26_27_28_Callback());
    }

    // @2
    private static class Custom_26_27_28_Callback implements Handler.Callback {

        @Override
        public boolean handleMessage(Message msg) {
            if (Parameter.EXECUTE_TRANSACTION == msg.what) {

                /*final ClientTransaction transaction = (ClientTransaction) msg.obj;
                mTransactionExecutor.execute(transaction);*/

                Object mClientTransaction = msg.obj;

                try {
                    // @1
                    // Field mActivityCallbacksField = mClientTransaction.getClass().getDeclaredField("mActivityCallbacks");
                    Class<?> mClientTransactionClass = Class.forName("android.app.servertransaction.ClientTransaction");
                    Field mActivityCallbacksField = mClientTransactionClass.getDeclaredField("mActivityCallbacks");
                    mActivityCallbacksField.setAccessible(true);
                    // List<ClientTransactionItem> mActivityCallbacks;
                    List mActivityCallbacks = (List) mActivityCallbacksField.get(mClientTransaction);

                    // TODO 需要判断
                    if (mActivityCallbacks.size() == 0) {
                        return false;
                    }

                    Object mLaunchActivityItem = mActivityCallbacks.get(0);

                    Class mLaunchActivityItemClass = Class.forName("android.app.servertransaction.LaunchActivityItem");

                    // TODO 需要判断
                    if (!mLaunchActivityItemClass.isInstance(mLaunchActivityItem)) {
                        return false;
                    }

                   Field mIntentField = mLaunchActivityItemClass.getDeclaredField("mIntent");
                   mIntentField.setAccessible(true);

                    // @2 需要拿到真实的Intent
                    Intent proxyIntent = (Intent) mIntentField.get(mLaunchActivityItem);
                    Log.d("hook", "proxyIntent:" + proxyIntent);
                    Intent targetIntent = proxyIntent.getParcelableExtra(Parameter.TARGET_INTENT);
                    if (targetIntent != null) {
                        mIntentField.set(mLaunchActivityItem, targetIntent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            return false;
        }
    }








    // >>>>>>>>>>>>>>>>>>>>>>>> 下面是 就是专门给 21_22_23_24_25 系统版本 做【还原操作】的    低版本



    /**
     * TODO 同学们看到此方法，应该明白，就是专门给 21_22_23_24_25 系统版本 做【还原操作】的
     */
    private final static void do_21_22_23_24_25_mHRestore() throws Exception {
        Class<?> mActivityThreadClass = Class.forName("android.app.ActivityThread");
        Field msCurrentActivityThreadField = mActivityThreadClass.getDeclaredField("sCurrentActivityThread");
        msCurrentActivityThreadField.setAccessible(true);
        Object mActivityThread = msCurrentActivityThreadField.get(null);

        // 如何获取@1
        Field mHField = mActivityThreadClass.getDeclaredField("mH");
        mHField.setAccessible(true);
        Handler mH = (Handler) mHField.get(mActivityThread);
        Field mCallbackFile = Handler.class.getDeclaredField("mCallback");
        mCallbackFile.setAccessible(true);

        // @1    @2
        mCallbackFile.set(mH, new Custom_21_22_23_24_25_Callback());
    }

    // @2
    private static final class Custom_21_22_23_24_25_Callback implements Handler.Callback {

        @Override
        public boolean handleMessage(Message msg) {
            if (Parameter.LAUNCH_ACTIVITY == msg.what) {
                Object mActivityClientRecord = msg.obj;
                try {
                    Field intentField = mActivityClientRecord.getClass().getDeclaredField("intent");
                    intentField.setAccessible(true);
                    Intent proxyIntent = (Intent) intentField.get(mActivityClientRecord);
                    // TODO 还原操作，要把之前的LoginActivity给换回来
                    Intent targetIntent = proxyIntent.getParcelableExtra(Parameter.TARGET_INTENT);
                    if (targetIntent != null) {
                        // 同学们：这种方式比方式要好一些哦，但是需要注意：必须是 setComponent的方式才使用哦
                        // proxyIntent.setComponent(targetIntent.getComponent());

                        // 反射的方式
                        intentField.set(mActivityClientRecord, targetIntent);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return false;
        }
    }

}
