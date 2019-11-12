package com.wangyi.wangyiroom;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    EditText et_room;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et_room = findViewById(R.id.et_room);
    }

    public void JoinRoom(View view) {
        WebRTCManager.getInstance().connect(this, et_room.getText().toString());
    }
}
