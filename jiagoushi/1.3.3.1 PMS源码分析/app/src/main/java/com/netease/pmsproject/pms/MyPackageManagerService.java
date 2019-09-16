package com.netease.pmsproject.pms;

import android.content.pm.PackageInfo;
import android.os.RemoteException;

import com.netease.pmsproject.MyIPackageManager;

public class MyPackageManagerService extends MyIPackageManager.Stub {

    @Override
    public PackageInfo getPackageInfo(String packageName, int flags, int userId) throws RemoteException {
        return null;
    }
}
