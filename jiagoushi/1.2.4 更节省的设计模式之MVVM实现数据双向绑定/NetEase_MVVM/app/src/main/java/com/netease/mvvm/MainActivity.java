package com.netease.mvvm;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.netease.mvvm.databinding.ActivityMvvmLoginBinding;
import com.netease.mvvm.vm.LoginViewModel;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 1、必须先ReBuilder，2、书写代码绑定
        ActivityMvvmLoginBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_mvvm_login);

        new LoginViewModel(binding);
    }
}
