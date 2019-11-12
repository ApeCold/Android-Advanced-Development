package com.neteasedb.update;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.neteasedb.User;
import com.neteasedb.db.BaseDaoFactory;
import com.neteasedb.db.UserDao;

import org.w3c.dom.Document;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class UpdateManager {

    private List<User> userList;

    public void startUpdateDb(Context context){
        UserDao userDao = BaseDaoFactory.getInstance().getBaseDao(UserDao.class,User.class);
        userList = userDao.query(new User());
        // 解析xml文件
        UpdateXml updateXml = readDbXml(context);
        // 拿到当前版本信息
        UpdateStep updateStep = analyseUpdateStep(updateXml);
        if(updateStep == null){
            return;
        }
        // 获取更新用得对象
        List<UpdateDb> updateDbs = updateStep.getUpdateDbs();
        for(User user:userList){
            // 得到每个用户得数据库对象
            SQLiteDatabase database = getDb(user.getId());
            if(database == null){
                return;
            }
            for(UpdateDb updateDb : updateDbs){
                String sql_rename = updateDb.getSql_rename();
                String sql_create = updateDb.getSql_create();
                String sql_insert = updateDb.getSql_insert();
                String sql_delete = updateDb.getSql_delete();
                String[] sqls = new String[]{sql_rename,sql_create,sql_insert,sql_delete};
                executeSql(database,sqls);
                Log.i("netease",user.getId()+"用户数据库升级成功");
            }
        }
    }

    private void executeSql(SQLiteDatabase database, String[] sqls) {
        if(sqls == null || sqls.length == 0){
            return;
        }
        // 事务
        database.beginTransaction();
        for(String sql : sqls){
            sql = sql.replace("\r\n"," ");
            sql = sql.replace("\n"," ");
            if(!"".equals(sql.trim())){
                database.execSQL(sql);
            }
        }
        database.setTransactionSuccessful();
        database.endTransaction();
    }

    private SQLiteDatabase getDb(Integer id) {
        SQLiteDatabase sqlDb = null;
        File file = new File("data/data/com.neteasedb/u_"+id+"_private.db");
        if(!file.exists()){
            Log.e("netease",file.getAbsolutePath()+"数据库不存在");
            return null;
        }
        return SQLiteDatabase.openOrCreateDatabase(file,null);
    }

    private UpdateStep analyseUpdateStep(UpdateXml updateXml) {
        UpdateStep thisStep = null;
        if(updateXml == null){
            return null;
        }
        List<UpdateStep> steps = updateXml.getUpdateSteps();
        if(steps == null || steps.size() == 0){
            return null;
        }
        for(UpdateStep step:steps){
            if(step.getVersionFrom() == null || step.getVersionTo() == null){
            }else{
                String[] versionArray = step.getVersionFrom().split(",");
                if(versionArray != null && versionArray.length > 0){
                    for(int i = 0; i < versionArray.length;i++){
                        // 数据保存在sp里面 或者文本文件 V002 就代表当前版本信息
                        // V003 应该从服务器获取
                        if("V002".equalsIgnoreCase(versionArray[i]) &&
                                step.getVersionTo().equalsIgnoreCase("V003")){
                            thisStep = step;
                            break;
                        }
                    }
                }
            }
        }
        return thisStep;
    }

    private UpdateXml readDbXml(Context context) {
        InputStream is = null;
        Document document = null;
        try {
            is = context.getAssets().open("updateXml.xml");
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            document = builder.parse(is);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(is != null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(document == null){
                return null;
            }
        }
        UpdateXml xml = new UpdateXml(document);
        return xml;
    }

}
