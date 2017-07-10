package com.zfsbswx.activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import com.mycommonlib.core.PayCommon;
import com.mycommonlib.model.ComTransInfo;
import com.tool.utils.activityManager.AppManager;
import com.tool.utils.utils.LogUtils;
import com.tool.utils.utils.SPUtils;
import com.tool.utils.utils.StringUtils;
import com.tool.utils.utils.ToastUtils;
import com.tool.utils.utils.WifiChangeBroadcastReceiver;
import com.zfsbswx.common.CommonFunc;
import com.zfsbswx.config.Config;
import com.zfsbswx.config.Constants;
import com.zfsbswx.core.action.Printer;
import com.zfsbswx.core.action.SbsAction;
import com.zfsbswx.model.LoginApiResponse;
import com.zfsbswx.model.SbsPrinterData;
import com.zfsbswx.myapplication.MyApplication;
import com.zfsbswx.service.LoginReceiver;

import org.litepal.crud.DataSupport;

import java.util.Calendar;
import java.util.TimeZone;

import static com.zfsbswx.common.CommonFunc.startAction;

public abstract class BaseActivity extends Activity {

    private static final String TAG = "BaseActivity";


    // 上下文实例
    protected Context context;
    // 应用全局的实例
    protected MyApplication application;
    // 核心层的Action实例
//	protected EmvImpl emvImpl;
    public SbsAction sbsAction;
    protected Printer printer;
    protected SbsPrinterData printerData;


    WifiChangeBroadcastReceiver receiver;


    public BaseActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        application = (MyApplication) this.getApplication();
//		emvImpl = application.getEmvImpl();
        sbsAction = application.getSbsAction();
        printerData = new SbsPrinterData();
        printer = Printer.getInstance(this);


        //获取广播对象
        receiver = new WifiChangeBroadcastReceiver();
        //创建意图过滤器
        IntentFilter filter = new IntentFilter();
        //添加动作，监听网络
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(receiver, filter);

        //封装一个定时器用于登录
        setAlarmToLogin();
    }

    private void setAlarmToLogin() {
        Intent intent = new Intent(BaseActivity.this, LoginReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(BaseActivity.this, 0, intent, 0);

        long firstTime = SystemClock.elapsedRealtime();    // 开机之后到现在的运行时间(包括睡眠时间)
        long systemTime = System.currentTimeMillis();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8")); // 这里时区需要设置一下，不然会有8个小时的时间差
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // 选择的每天定时时间
        long selectTime = calendar.getTimeInMillis();

        // 如果当前时间大于设置的时间，那么就从第二天的设定时间开始
        if (systemTime > selectTime) {
//            Toast.makeText(BaseActivity.this, "设置的时间小于当前时间", Toast.LENGTH_SHORT).show();
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            selectTime = calendar.getTimeInMillis();
        }

        // 计算现在时间到设定时间的时间差
        long time = selectTime - systemTime;
        firstTime += time;

        long DAY = 1000L * 60 * 60 * 24;

        // 进行闹铃注册
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        manager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                firstTime, DAY, sender);

        Log.e(TAG, "time ==== " + time + ", selectTime ===== "
                + selectTime + ", systemTime ==== " + systemTime + ", firstTime === " + firstTime);

//        Toast.makeText(BaseActivity.this, "设置重复闹铃成功! ", Toast.LENGTH_LONG).show();
    }


    /**
     * 设置参数
     */
    private void setPayParams() {
        LogUtils.e("再次设置参数");
        int keyIndex = MyApplication.getInstance().getLoginData().getKeyIndex();
        String mid = MyApplication.getInstance().getLoginData().getMerchantNo();
        String tid = MyApplication.getInstance().getLoginData().getTerminalNo();
        PayCommon.setParams(this, keyIndex, mid, tid, new PayCommon.ComTransResult<ComTransInfo>() {
            @Override
            public void success(ComTransInfo transInfo) {
                if (!(boolean) SPUtils.get(BaseActivity.this, Config.AID_KEY, false)) {
                    DownloadAid();
                } else if (!(boolean) SPUtils.get(BaseActivity.this, Config.CPK_KEY, false)) {
                    DownloadCapk();
                } else if (!(boolean) SPUtils.get(BaseActivity.this, Config.BLACKLIST_KEY, false)) {
                    DownloadBlackList();
                } else {
                    if (!CommonFunc.isLogin(BaseActivity.this, Config.FY_LOGIN_TIME, Config.DEFAULT_FY_LOGIN_TIME)) {
                        startAction(BaseActivity.this, InputAmountActivity.class, true);
                        return;
                    }
                    payLogin();
                }
            }

            @Override
            public void failed(String error) {

            }
        });
    }


    /**
     * 下载参数
     */
    protected void DownParams() {
        PayCommon.DownParams(this, new PayCommon.ComTransResult<ComTransInfo>() {
            @Override
            public void success(ComTransInfo transInfo) {
                setPayParams();
            }

            @Override
            public void failed(String error) {

            }
        });
    }

    /**
     * 下载主密钥
     */
    protected void DownMasterKey() {

        if (MyApplication.getInstance().getLoginData().isDownMasterKey()) {
            ToastUtils.CustomShow(this, "主密钥已经下载过了");
            return;
        }

        String mid = MyApplication.getInstance().getLoginData().getMerchantNo();
        String tid = MyApplication.getInstance().getLoginData().getTerminalNo();
        String other = MyApplication.getInstance().getLoginData().getOther();

        PayCommon.DownMasterKey(this, other, mid, tid, new PayCommon.ComTransResult<ComTransInfo>() {
            @Override
            public void success(ComTransInfo transInfo) {
                setMaterKeyIsDownLoad();
                if (Config.isHs){
                    DownloadAid();
                }else {
                    DownParams();
                }
            }

            @Override
            public void failed(String error) {

            }
        });
    }


    /**
     * 保存密钥状态
     */
    protected void setMaterKeyIsDownLoad() {
        MyApplication.getInstance().getLoginData().setDownMasterKey(true);
        //更新数据库
        ContentValues values = new ContentValues();
        values.put("isDownMasterKey", true);
        DataSupport.update(LoginApiResponse.class, values, MyApplication.getInstance().getLoginData().getId());

        if (MyApplication.getInstance().getLoginData().getKeyIndex() == Constants.FY_INDEX_1) {
            SPUtils.put(this, Config.FY_M_DOWN_MASTER, true);
        } else {
            SPUtils.put(this, Config.FY_DOWN_MASTER, true);
        }
        LogUtils.e(TAG, "设置密钥使用状态：使用");
    }


    /**
     * 下载AID
     */
    protected void DownloadAid() {

        PayCommon.DownloadAid(this, new PayCommon.ComTransResult<ComTransInfo>() {
            @Override
            public void success(ComTransInfo transInfo) {

                if (!(boolean) SPUtils.get(BaseActivity.this, Config.CPK_KEY, false)) {
                    SPUtils.put(BaseActivity.this, Config.AID_KEY, true);
                    DownloadCapk();
                } else if (!(boolean) SPUtils.get(BaseActivity.this, Config.BLACKLIST_KEY, false) && Config.isFy) {
                    DownloadBlackList();
                } else {
                    if (!CommonFunc.isLogin(BaseActivity.this, Config.FY_LOGIN_TIME, Config.DEFAULT_FY_LOGIN_TIME)) {
                        startAction(BaseActivity.this, InputAmountActivity.class, true);
                        return;
                    }
                    payLogin();
                }
            }

            @Override
            public void failed(String error) {

            }
        });
    }


    /**
     * 下载公钥
     */
    protected void DownloadCapk() {

        PayCommon.DownloadCapk(this, new PayCommon.ComTransResult<ComTransInfo>() {
            @Override
            public void success(ComTransInfo transInfo) {
                SPUtils.put(BaseActivity.this, Config.CPK_KEY, true);
                if (!(boolean) SPUtils.get(BaseActivity.this, Config.AID_KEY, false)) {
                    DownloadAid();
                } else if (!(boolean) SPUtils.get(BaseActivity.this, Config.BLACKLIST_KEY, false) && Config.isFy) {
                    DownloadBlackList();
                } else {
                    if (!CommonFunc.isLogin(BaseActivity.this, Config.FY_LOGIN_TIME, Config.DEFAULT_FY_LOGIN_TIME)) {
                        startAction(BaseActivity.this, InputAmountActivity.class, true);
                        return;
                    }
                    payLogin();
                }
            }

            @Override
            public void failed(String error) {

            }
        });
    }

    /**
     * 下载黑名单
     */
    protected void DownloadBlackList() {

        PayCommon.DownloadBlackList(this, new PayCommon.ComTransResult<ComTransInfo>() {
            @Override
            public void success(ComTransInfo transInfo) {
                SPUtils.put(BaseActivity.this, Config.BLACKLIST_KEY, true);
                if (!(boolean) SPUtils.get(BaseActivity.this, Config.AID_KEY, false)) {
                    DownloadAid();
                } else if (!(boolean) SPUtils.get(BaseActivity.this, Config.CPK_KEY, false)) {
                    DownloadCapk();
                } else {
                    if (!CommonFunc.isLogin(BaseActivity.this, Config.FY_LOGIN_TIME, Config.DEFAULT_FY_LOGIN_TIME)) {
                        startAction(BaseActivity.this, InputAmountActivity.class, true);
                        return;
                    }
                    payLogin();
                }
            }

            @Override
            public void failed(String error) {

            }
        });
    }


    /**
     * 签到
     */
    protected void payLogin() {

        String mid = MyApplication.getInstance().getLoginData().getMerchantNo();
        String tid = MyApplication.getInstance().getLoginData().getTerminalNo();
        PayCommon.login(this, mid, tid, new PayCommon.ComTransResult<ComTransInfo>() {
            @Override
            public void success(ComTransInfo transInfo) {

                //如果签到成功说明主密钥下载过了，不用在下载了，直接设置为true
                SaveLoginMasterKey();
                // 登录成功 保存今天的时间
                SPUtils.put(BaseActivity.this, Constants.HS_LOGIN_TIME, StringUtils.getCurDate());
                startAction(BaseActivity.this, InputAmountActivity.class, true);
            }

            @Override
            public void failed(String error) {

            }
        });
    }

    /**
     * 如果签到成功说明主密钥下载过了，不用在下载了，直接设置为true
     */
    private void SaveLoginMasterKey() {

        MyApplication.getInstance().getLoginData().setDownMasterKey(true);
        //更新数据库
        ContentValues values = new ContentValues();
        values.put("isDownMasterKey", true);
        DataSupport.update(LoginApiResponse.class, values, MyApplication.getInstance().getLoginData().getId());
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
    }
}
