package com.bumbumapps.bouncy;

import android.os.CountDownTimer;


public  class  Timers {

    public static CountDownTimer timer(){
        return new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                 Globals.TIMER_FINISHED = true;
            }
        };
    }

}