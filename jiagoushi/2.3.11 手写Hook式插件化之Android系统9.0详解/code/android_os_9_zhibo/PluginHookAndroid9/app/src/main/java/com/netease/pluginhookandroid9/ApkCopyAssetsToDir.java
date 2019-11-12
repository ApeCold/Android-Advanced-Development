package com.netease.pluginhookandroid9;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Time: 2019-08-10
 * Author: Liudeli
 * Description: TODO 将assets中的文件（plugin-debug.apk）拷贝到app的缓存目录，然后返回拷贝之后文件的路径
 */
public class ApkCopyAssetsToDir {

    /**
     * 同学们，此方法的主要目的是：从assets 拷贝到 app的cache目录
     * @param context
     * @param fileName
     * @return  例如是这样：/data/user/0/com.netease.pluginhookandroid9/cache/plugin-debug.apk
     *
     * 不可能反正SD
     */
    public static String copyAssetToCache(Context context, String fileName) {
        // 同学们 此app的缓存目录 --> 会默认在 cache目录...，同学们可以自己去看看哦
        File cacheDir = context.getCacheDir();
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();// TODO 如果没有缓存目录，就创建
        }
        File outPath = new File(cacheDir, fileName);// TODO 创建输出的文件位置
        if (outPath.exists()) {
            outPath.delete(); // TODO 如果该文件已经存在，就删掉
        }
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            // 创建文件，如果创建成功，就返回true
            boolean res = outPath.createNewFile();
            if (res) {
                is = context.getAssets().open(fileName);// 拿到main/assets目录的输入流，用于读取字节
                fos = new FileOutputStream(outPath); // 读取出来的字节最终写到outPath
                byte[] buf = new byte[is.available()];// 缓存区
                int byteCount;

                // 开始循环读取
                while ((byteCount = is.read(buf)) != -1) {
                    fos.write(buf, 0, byteCount);
                }
                return outPath.getAbsolutePath();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // TODO 同学们 一定要记得关闭资源，为了不去性能的磨损
                fos.flush();
                is.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
