package neteases.skinproject;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Method;

/**
 * 访问 本地存储的皮肤包资源(xxx.skin) 或 当前app运行的apk包资源
 */
public class SkinResources {

    private final static String TAG = SkinResources.class.getSimpleName();

    private static SkinResources instance;

    private Resources mSkinResources; // 此Resource 可以加载本地存储 xxx.skin 皮肤包资源
    private String mSkinPkgName; // 皮肤包资源所在包名
    private boolean isDefaultSkin = true; // 默认是 默认的皮肤(最原始的)

    private Resources mAppResources; // 此Resource 可以加载当前App运行的apk资源

    private SkinResources(Context context) {
        // 初始化app资源器
        mAppResources = context.getResources();
    }

    /**
     * 给Application进行初始化的，目的是绑定Application 而不是 绑定Activity
     */
    public static void applicationInit(Context context) {
        if (instance == null) {
            synchronized (SkinResources.class) {
                if (instance == null) {
                    instance = new SkinResources(context);
                }
            }
        }
    }

    public static SkinResources getInstance() {
        return instance;
    }

    /**
     * 重置
     */
    public void reset() {
        mSkinResources = null;
        mSkinPkgName = "";
        isDefaultSkin = true;
    }

    /**
     * 此方法是给 点击换肤时使用的，目的就是创建出mSkinResources ---> 访问 本地存储 xxx.skin 皮肤包
     * @param application
     * @param skinPath
     */
    public void setSkinResources(Application application, String skinPath) {

        File file = new File(skinPath);
        if (!file.exists()) {
            Log.e(TAG, "Error skinPath not exist...");
        }

        try {
            // 创建资源管理器
            AssetManager assetManager = AssetManager.class.newInstance();
            // 由于AssetManager中的addAssetPath和setApkAssets方法都被@hide，目前只能通过反射去执行方法
            Method addAssetPath = assetManager.getClass().getDeclaredMethod("addAssetPath", String.class);
            // 设置私有方法可访问
            addAssetPath.setAccessible(true);
            // 执行addAssetPath方法
            addAssetPath.invoke(assetManager, skinPath);

            // 创建加载外部的皮肤包(xxx.skin)文件Resources
            mSkinResources = new Resources(assetManager,
                    mAppResources.getDisplayMetrics(), mAppResources.getConfiguration());

            // 根据apk文件路径（皮肤包也是apk文件），获取该应用的包名
            mSkinPkgName = application.getPackageManager().getPackageArchiveInfo
                    (skinPath, PackageManager.GET_ACTIVITIES).packageName;

            // 是否使用默认皮肤
            isDefaultSkin = TextUtils.isEmpty(mSkinPkgName);
        } catch (Exception e) {
            e.printStackTrace();

            // 发生异常就证明 通过skinPath 获取 packageName 失败了
            isDefaultSkin = true;
        }
    }

    /**
     * 此方法干了两件事：
     * 1.如果没有皮肤，那就直接return resId;
     * 2.如果有皮肤，那就通过mSkinResources读取本地xxx.skin皮肤包资源的id，如果没有读取到那就返回0，给外面判断
     */
    public int getIdentifier(int resId) {
        // 如果没有皮肤，就直接返回resId
        if (isDefaultSkin) {
            return resId;
        }

        String resName = mAppResources.getResourceEntryName(resId); // ic_launcher
        String resType = mAppResources.getResourceTypeName(resId); // drawable
        return mSkinResources.getIdentifier(resName, resType, mSkinPkgName);
    }

    public int getColor(int resId) {
        if (isDefaultSkin) { // 如果没有皮肤，那就加载当前App运行的Apk资源
            return mAppResources.getColor(resId);
        }
        int skinId = getIdentifier(resId);
        if (skinId == 0) { // 如果为0，那就加载当前App运行的Apk资源
            return mAppResources.getColor(resId);
        }
        // skinId不等于0 ，就加载 本地存储的 xxx.skin皮肤包资源
        return mSkinResources.getColor(skinId);
    }

    public ColorStateList getColorStateList(int resId) {
        if (isDefaultSkin) { // 如果没有皮肤，那就加载当前App运行的Apk资源
            return mAppResources.getColorStateList(resId);
        }
        int skinId = getIdentifier(resId);
        if (skinId == 0) { // 如果为0，那就加载当前App运行的Apk资源
            return mAppResources.getColorStateList(resId);
        }
        // skinId不等于0 ，就加载 本地存储的 xxx.skin皮肤包资源
        return mSkinResources.getColorStateList(skinId);
    }

    public ColorStateList getColorStateList2(int resId, int attrValueInt) {
        if (isDefaultSkin) { // 如果没有皮肤，那就加载当前App运行的Apk资源
            return mAppResources.getColorStateList(resId);
        }
        int skinId = getIdentifier(attrValueInt);
        if (skinId == 0) { // 如果为0，那就加载当前App运行的Apk资源
            return mAppResources.getColorStateList(attrValueInt);
        }
        // skinId不等于0 ，就加载 本地存储的 xxx.skin皮肤包资源
        return mSkinResources.getColorStateList(skinId);
    }

    public Drawable getDrawable(int resId) {
        //如果有皮肤  isDefaultSkin false 没有就是true
        if (isDefaultSkin) {
            return mAppResources.getDrawable(resId);
        }
        int skinId = getIdentifier(resId);
        if (skinId == 0) {
            return mAppResources.getDrawable(resId);
        }
        // skinId不等于0 ，就加载 本地存储的 xxx.skin皮肤包资源
        return mSkinResources.getDrawable(skinId);
    }

    public Drawable getDrawableMipmap(int resId) {
        // 如果为true，就是没有皮肤
        if (isDefaultSkin) { // 如果没有皮肤，那就加载当前App运行的Apk资源
            return mAppResources.getDrawable(resId);
        }

        int skinId = getIdentifier(resId);
        if (skinId == 0) { // 如果为0，那就加载当前App运行的Apk资源
            return mAppResources.getDrawable(resId);
        }

        // 外部皮肤包的 资源去getXXX
        // skinId不等于0 ，就加载 本地存储的 xxx.skin皮肤包资源
        return mSkinResources.getDrawable(skinId);
    }

    public String getString(int resId) {
        try {
            if (isDefaultSkin) {  // 如果没有皮肤，那就加载当前App运行的Apk资源
                return mAppResources.getString(resId);
            }
            int skinId = getIdentifier(resId);
            if (skinId == 0) { // 如果为0，那就加载当前App运行的Apk资源
                return mAppResources.getString(skinId);
            }
            // skinId不等于0 ，就加载 本地存储的 xxx.skin皮肤包资源
            return mSkinResources.getString(skinId);
        } catch (Resources.NotFoundException e) {

        }
        return null;
    }


    /**
     * 获取background 是特殊情况，因为：
     * 可能是color
     * 可能是drawable
     * 可能是mipmap
     * 所有得到当前属性的类型Resources.getResourceTypeName(resId); 进行判断
     * @return
     */
    public Object getBackground(int resId) {
        String resourceTypeName = mAppResources.getResourceTypeName(resId);

        if (resourceTypeName.equals("color")) {
            return getColor(resId);
        } else if (resourceTypeName.equals("drawable") || resourceTypeName.equals("mipmap")) {
            // drawable or mipmap
            return getDrawable(resId);
        }
        return getColorStateList(resId);
    }

    /**
     * 获取src 是特殊情况，因为：
     * 可能是Color
     * 可能是Drawable
     * 可能是Mipmap
     * @param resId
     * @return
     */
    public Object getSrc(int resId) {
        // 获得的可能是 color/drawable/mipmap，
        String resourceTypeName = mAppResources.getResourceTypeName(resId);

        if (resourceTypeName.equals("color")) {
            return getColor(resId);
        } else {
            // drawable or mipmap
            return getDrawable(resId);
        }
    }

    /**
     * 获得字体
     * @param resId
     * @return
     */
    public Typeface getTypeface(int resId) {
        /**
         * 获取到了字符串，可能是 本地xxx.skin皮肤包资源的字符串 还是 当前App运行的Apk资源的字符串，这个不关注
         * 暂停了 ...
         */
        String skinTypefacePath = getString(resId);
        if (TextUtils.isEmpty(skinTypefacePath)) {
            return Typeface.DEFAULT;
        }
        try {
            Typeface typeface;
            if (isDefaultSkin) {
                typeface = Typeface.createFromAsset(mAppResources.getAssets(), skinTypefacePath);
                return typeface;

            }
            typeface = Typeface.createFromAsset(mSkinResources.getAssets(), skinTypefacePath);
            return typeface;
        } catch (RuntimeException e) {
        }
        return Typeface.DEFAULT;
    }

    public boolean getDefaultSkin() {
        return isDefaultSkin;
    }

}
