package com.netease.modular.personal.debug;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.netease.common.utils.Cons;
import com.netease.modular.personal.Personal_MainActivity;
import com.netease.modular.personal.R;

public class Personal_DebugActivity extends Personal_DebugBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_activity_debug);

        Log.e(Cons.TAG, "personal/debug/Personal_DebugActivity");
    }

    public void jump(View view) {
        startActivity(new Intent(this, Personal_MainActivity.class));
    }
}
