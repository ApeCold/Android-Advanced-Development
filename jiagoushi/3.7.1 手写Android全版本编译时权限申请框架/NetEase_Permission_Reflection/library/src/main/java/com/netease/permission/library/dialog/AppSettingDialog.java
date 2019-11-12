package com.netease.permission.library.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

public class AppSettingDialog implements DialogInterface.OnClickListener {

    public static final int SETTING_CODE = 333; // 跳转设置监听回调标识码

    private Activity activity;
    private String title; // 对话框标题
    private String message; // 解释为什么需要这组权限的提示内容
    private String positiveButton; // 确定按钮
    private String negativeButton; // 取消按钮
    private DialogInterface.OnClickListener listener; // 对话框点击监听
    private int requestCode; // 请求标识码

    private AppSettingDialog(Builder builder) {
        this.activity = builder.activity;
        this.title = builder.title;
        this.message = builder.message;
        this.positiveButton = builder.positiveButton;
        this.negativeButton = builder.negativeButton;
        this.listener = builder.listener;
        this.requestCode = builder.requestCode;
    }

    public void show() {
        if (listener != null) {
            showDialog();
        } else {
            throw new IllegalArgumentException("对话框监听不能为空");
        }
    }

    // 显示对话框
    private void showDialog() {
        new AlertDialog.Builder(activity)
                .setCancelable(false)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButton, this)
                .setNegativeButton(negativeButton, listener)
                .create()
                .show();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        // 点击跳转设置
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        activity.startActivityForResult(intent, requestCode);
    }

    public static class Builder {

        private Activity activity;
        private String title; // 对话框标题
        private String message; // 解释为什么需要这组权限的提示内容
        private String positiveButton; // 确定按钮
        private String negativeButton; // 取消按钮
        private DialogInterface.OnClickListener listener; // 对话框点击监听
        private int requestCode = -1; // 请求标识码

        public Builder(@NonNull Activity activity) {
            this.activity = activity;
        }

        public Builder setMessage(String rationale) {
            this.message = rationale;
            return this;
        }

        public Builder setListener(DialogInterface.OnClickListener listener) {
            this.listener = listener;
            return this;
        }

        public Builder setRequestCode(int requestCode) {
            this.requestCode = requestCode;
            return this;
        }

        public AppSettingDialog build() {
            this.title = "需要的授权";
            this.message = TextUtils.isEmpty(message) ? "打开设置，启动权限" : message;
            this.positiveButton = activity.getString(android.R.string.ok);
            this.negativeButton = activity.getString(android.R.string.cancel);
            this.requestCode = requestCode > 0 ? requestCode : SETTING_CODE;

            return new AppSettingDialog(this);
        }
    }
}
