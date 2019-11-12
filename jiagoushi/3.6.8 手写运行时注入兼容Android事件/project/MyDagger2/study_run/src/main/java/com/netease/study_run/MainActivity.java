package com.netease.study_run;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.netease.study_run.annotation.BindView;
import com.netease.study_run.annotation.Click;
import com.netease.study_run.annotation.ContentView;
import com.netease.study_run.annotation_common.OnClickCommon;
import com.netease.study_run.annotation_common.OnClickLongCommon;
import com.netease.study_run.annotation_common.OnDragCommon;

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




    //////////////////////////////////////////////// 下面是兼容事件代码

    // 点击事件
    @Deprecated
    @OnClickCommon(R.id.bt_t1) // 变化的
    private void test111() {
        Toast.makeText(this, "兼容 点击事件 run", Toast.LENGTH_SHORT).show();


        // 我们需要动态变化事件  事件三要素
        Button button = null;

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });

        button.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                return false;
            }
        });

        // todo 事件三要素1 订阅方式  setOnClickListener， setOnLongClickListener  ...

        // todo 事件三要素2 事件源对象 View.OnClickListener，  View.OnLongClickListener  ...

        // todo 事件三要素3 具体执行的方法（消费事件的方法）   onClick(View v) ，  onLongClick(View v)
    }

    // 长按事件
    @OnClickLongCommon(R.id.bt_t2)  // 变化的
    private boolean test222() {
        Toast.makeText(this, "兼容 长按事件 run", Toast.LENGTH_SHORT).show();
        return false;
    }

   /* @OnDragCommon(R.id.bt_t3)
    private boolean test3333() {
        Toast.makeText(this, "兼容 test3333 run", Toast.LENGTH_SHORT).show();
        return false;
    }*/
}
