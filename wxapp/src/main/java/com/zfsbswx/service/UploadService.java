package com.zfsbswx.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import java.util.Date;

/**
 * 流水上送启动一个定时服务
 */

/**
 * 启动定时任务
 * Intent intent = new Intent(this,UploadService.class);
    startService(intent);
 *
 */
public class UploadService extends Service {


    /**
     * 每隔60s执行一次
     */
    private static final int LOAD_TIME = 60*1000;



    public UploadService() {
    }


    Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            // process incoming messages here

        }
    };


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("TAG", "打印时间: " + new Date().
                        toString());

//                handler.sendEmptyMessage(0);

//                UploadFunc.getInstance().isCheckUploadStatus(UploadService.this);
            }
        }).start();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int five = LOAD_TIME; // 这是5s
        long triggerAtTime = SystemClock.elapsedRealtime() + five;
        Intent i = new Intent(this, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }
}
