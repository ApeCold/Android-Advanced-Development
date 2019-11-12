package com.wangyi.webrtc;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


/**
 * Created by dds on 2018/11/7.
 * android_shuai@163.com
 */
public class MainActivity extends AppCompatActivity {
    private EditText et_signal;
    private EditText et_port;
    private EditText et_room;

    private EditText edit_test_wss;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initVar();

    }

    private void initView() {
        et_signal = findViewById(R.id.et_signal);
        et_port = findViewById(R.id.et_port);
        et_room = findViewById(R.id.et_room);
        edit_test_wss = findViewById(R.id.et_wss);
    }

    private void initVar() {
        // et_signal.setText("ws://192.168.1.122:3000");
        //  et_port.setText("3000");
        et_room.setText("666555");
    }

    public void JoinRoomSingleVideo(View view) {
        WebrtcUtil.callSingle(this,
                et_signal.getText().toString(),
                et_room.getText().toString().trim() + ":" + et_port.getText().toString().trim(),
                true);
    }

    public void JoinRoomSingleAudio(View view) {
        WebrtcUtil.callSingle(this,
                et_signal.getText().toString(),
                et_room.getText().toString().trim() + ":" + et_port.getText().toString().trim(),
                false);
    }

    public void JoinRoom(View view) {
        WebrtcUtil.call(this, et_signal.getText().toString(), et_room.getText().toString().trim());

    }

    //test wss
    public void wss(View view) {
        WebrtcUtil.testWs(edit_test_wss.getText().toString());
    }
}
