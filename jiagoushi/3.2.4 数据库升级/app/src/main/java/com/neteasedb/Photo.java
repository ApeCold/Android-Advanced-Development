package com.neteasedb;

import android.database.sqlite.SQLiteOpenHelper;

import com.neteasedb.annotation.DbTable;

@DbTable("tb_photo")
public class Photo {

    private String time;
    private String path;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    //  先把数据表进行一个备份 或者 说 叫重命名  tb_photo tb_photo_bak
    // 重新创建要给tb_photo表
    // 将重命名后的 表里的数据 导入到新建的tb_photo
    // 将之前的备份的表 删除掉 tb_photo_bak
    // 前提就是必须得知道 我们这里面又什么表 xml升级 缺陷 就是 只能针对我们自己的项目
}
