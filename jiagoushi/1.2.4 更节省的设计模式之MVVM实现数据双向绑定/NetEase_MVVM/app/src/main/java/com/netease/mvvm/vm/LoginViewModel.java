package com.netease.mvvm.vm;

import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.netease.mvvm.databinding.ActivityMvvmLoginBinding;
import com.netease.mvvm.model.UserInfo;

public class LoginViewModel {

    public UserInfo userInfo;

    public LoginViewModel(ActivityMvvmLoginBinding binding) {
        userInfo = new UserInfo();
        // 将ViewModel和View进行绑定，通过DataBinding工具
        binding.setLoginViewModel(this);
    }

    public TextWatcher nameInputListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // View层接收到用户的输入，改变Model层的javabean属性
            userInfo.name.set(String.valueOf(s));
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    public TextWatcher pwdInputListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // View层接收到用户的输入，改变Model层的javabean属性
            userInfo.pwd.set(String.valueOf(s));
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    public View.OnClickListener loginClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // 模拟网络请求
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // Model层属性的变更，改变View层的显示
                    // userInfo.name.set("Mir Peng");
                    SystemClock.sleep(2000);

                    if ("netease".equals(userInfo.name.get()) && "163".equals(userInfo.pwd.get())) {
                        Log.e("netease >>> ", "登录成功!");
                    } else {
                        Log.e("netease >>> ", "登录失败!");
                    }
                }
            }).start();
        }
    };
}
