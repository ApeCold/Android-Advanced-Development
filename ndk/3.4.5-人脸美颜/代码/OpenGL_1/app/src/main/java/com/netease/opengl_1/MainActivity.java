package com.netease.opengl_1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private MyGLSurfaceView mGLSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGLSurfaceView = findViewById(R.id.my_gl_sruface_view);
        ((MyRecordButton) findViewById(R.id.btn_record)).setOnRecordListener(
                new MyRecordButton.OnRecordListener() {
                    /**
                     * 开始录制
                     */
                    @Override
                    public void onStartRecording() {
                        mGLSurfaceView.startRecording();
                    }

                    /**
                     * 停止录制
                     */
                    @Override
                    public void onStopRecording() {
                        mGLSurfaceView.stopRecording();
                        Toast.makeText(MainActivity.this, "录制完成！", Toast.LENGTH_SHORT).show();
                    }
                });

        ((RadioGroup) findViewById(R.id.group_record_speed)).setOnCheckedChangeListener(
                new RadioGroup.OnCheckedChangeListener() {
                    /**
                     * 选择录制模式
                     * @param group
                     * @param checkedId
                     */
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        switch (checkedId) {
                            case R.id.rbtn_record_speed_extra_slow: //极慢
                                mGLSurfaceView.setSpeed(MyGLSurfaceView.Speed.MODE_EXTRA_SLOW);
                                break;
                            case R.id.rbtn_record_speed_slow:   //慢
                                mGLSurfaceView.setSpeed(MyGLSurfaceView.Speed.MODE_SLOW);
                                break;
                            case R.id.rbtn_record_speed_normal: //正常
                                mGLSurfaceView.setSpeed(MyGLSurfaceView.Speed.MODE_NORMAL);
                                break;
                            case R.id.rbtn_record_speed_fast:   //快
                                mGLSurfaceView.setSpeed(MyGLSurfaceView.Speed.MODE_FAST);
                                break;
                            case R.id.rbtn_record_speed_extra_fast: //极快
                                mGLSurfaceView.setSpeed(MyGLSurfaceView.Speed.MODE_EXTRA_FAST);
                                break;
                        }
                    }
                });

        ((CheckBox)findViewById(R.id.chk_bigeye)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mGLSurfaceView.enableBigEye(isChecked);
            }
        });
        ((CheckBox)findViewById(R.id.chk_stick)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mGLSurfaceView.enableStick(isChecked);
            }
        });
        ((CheckBox)findViewById(R.id.chk_beauty)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mGLSurfaceView.enableBeauty(isChecked);
            }
        });
    }

}
