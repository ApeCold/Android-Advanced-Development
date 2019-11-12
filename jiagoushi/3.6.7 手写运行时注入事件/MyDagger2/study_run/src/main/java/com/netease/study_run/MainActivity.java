package com.netease.study_run;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.netease.study_run.annotation.BindView;
import com.netease.study_run.annotation.Click;
import com.netease.study_run.annotation.ContentView;

@ContentView(R.layout.activity_main) // 5415151
public class MainActivity extends AppCompatActivity {

    @BindView(R.id.bt_test1)
    Button button1;

    TextView textView;

    String string;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        InjectTool.inject(this);

        Log.d("MainActivity", "onCreate: " + button1.getText().toString());
    }

    @Click(R.id.bt_test3)
    private void show() {
        Toast.makeText(this, "show is run", Toast.LENGTH_SHORT).show();
    }

    private void test111() {

    }

    private void test222() {

    }
}
