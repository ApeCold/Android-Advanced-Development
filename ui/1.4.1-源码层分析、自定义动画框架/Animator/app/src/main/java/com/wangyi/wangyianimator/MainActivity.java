package com.wangyi.wangyianimator;

import android.animation.ObjectAnimator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.wangyi.wangyianimator.animator.LineInterpolator;
import com.wangyi.wangyianimator.animator.MyObjectAnimator;

public class MainActivity extends AppCompatActivity {
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.btn);


    }

    public void scale(View view) {
//
        ObjectAnimator objectAnimator = ObjectAnimator.
                ofFloat(button, "scaleX", 2f);
        objectAnimator.setDuration(3000);
        objectAnimator.start();
    }
}
