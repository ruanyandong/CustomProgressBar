package com.example.ai.customprogressbar;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private HorizontalProgressbarWithProgress mProgress;
    private RoundProgressBarWithProgress mRoundProgress;

    private static final int MSG_UPDATE=1;


    private Handler mHandler=new Handler(){

        @Override
        public void handleMessage(Message msg) {

            if (msg.what==MSG_UPDATE){
                int progress=mProgress.getProgress();
                mProgress.setProgress(++progress);
                mRoundProgress.setProgress(++progress);
                if (progress>=100){
                    mHandler.removeMessages(MSG_UPDATE);
                }

               //内部封装了Message对象
                mHandler.sendEmptyMessageDelayed(MSG_UPDATE,100);
            }


        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgress=findViewById(R.id.id_progress01);

        mRoundProgress=findViewById(R.id.id_progress02);
        /**
         * 触发handler
         */
        mHandler.sendEmptyMessage(MSG_UPDATE);




    }


}
