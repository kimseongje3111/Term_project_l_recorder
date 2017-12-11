package com.example.seongje.l_recorder;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * Created by SEONGJE on 2017-12-04.
 */

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        Handler hd = new Handler(){
            @Override
            public void handleMessage(Message msg){
                finish();
            }

        };
        hd.sendEmptyMessageDelayed(0,3000);
    }

}
