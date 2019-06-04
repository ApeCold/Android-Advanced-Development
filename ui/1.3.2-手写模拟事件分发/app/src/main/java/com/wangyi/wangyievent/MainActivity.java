package com.wangyi.wangyievent;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    LinearLayout container;
    TextView text;
    public static String TAG = "touch";
    View view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        container = findViewById(R.id.container);

        MyViewGroup myViewGroup = new MyViewGroup(this);
        MyView myView = new MyView(this);
        Handler handler = new Handler();
        handler.handleMessage(new Message());
//        handler.sendEmptyMessage(message);
//        Message message= Message.obtain();
//  设置了一个点击事件  代表消费了
        myView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "OnClickListener: ");
            }
        });

        myViewGroup.addView(new MyView(this));







        text = findViewById(R.id.text);

        container.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.i(TAG, "onTouch: container 打印========> ");
                return false;
            }
        });
        text.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.i(TAG, "onTouch: text 打印========> ");
                return false;
            }
        });
    }
}
