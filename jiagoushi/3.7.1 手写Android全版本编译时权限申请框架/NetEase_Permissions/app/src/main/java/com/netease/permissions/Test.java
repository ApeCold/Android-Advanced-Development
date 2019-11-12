//package com.netease.permissions;
//
//import android.support.annotation.NonNull;
//import android.support.v4.app.ActivityCompat;
//
//import com.netease.permissions.library.listener.PermissionRequest;
//import com.netease.permissions.library.listener.RequestPermission;
//import com.netease.permissions.library.utils.PermissionUtils;
//
//import java.lang.ref.WeakReference;
//
//public class MainActivity$Permissions implements RequestPermission<MainActivity> {
//
//    private static final int REQUEST_SHOWCAMERA = 666;
//    private static String[] PERMISSION_SHOWCAMERA;
//
//    public MainActivity$Permissions() {
//    }
//
//    public void requestPermission(MainActivity target, String[] permissions) {
//        PERMISSION_SHOWCAMERA = permissions;
//        if (PermissionUtils.hasSelfPermissions(target, PERMISSION_SHOWCAMERA)) {
//            target.showCamera();
//        } else if (PermissionUtils.shouldShowRequestPermissionRationale(target, PERMISSION_SHOWCAMERA)) {
//            target.showRationaleForCamera(new MainActivity$Permissions.PermissionRequestImpl(target));
//        } else {
//            ActivityCompat.requestPermissions(target, PERMISSION_SHOWCAMERA, 666);
//        }
//
//    }
//
//    public void onRequestPermissionsResult(MainActivity target, int requestCode, @NonNull int[] grantResults) {
//        switch (requestCode) {
//            case 666:
//                if (PermissionUtils.verifyPermissions(grantResults)) {
//                    target.showCamera();
//                } else if (!PermissionUtils.shouldShowRequestPermissionRationale(target, PERMISSION_SHOWCAMERA)) {
//                    target.showNeverAskForCamera();
//                } else {
//                    target.showDeniedForCamera();
//                }
//            default:
//        }
//    }
//
//    private static final class PermissionRequestImpl implements PermissionRequest {
//        private final WeakReference<MainActivity> weakTarget;
//
//        private PermissionRequestImpl(MainActivity target) {
//            this.weakTarget = new WeakReference(target);
//        }
//
//        public void proceed() {
//            MainActivity target = (MainActivity) this.weakTarget.get();
//            if (target != null) {
//                ActivityCompat.requestPermissions(target, MainActivity$Permissions.PERMISSION_SHOWCAMERA, 666);
//            }
//
//        }
//    }
//}
