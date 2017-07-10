package com.zfsbs.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.zfsbs.R;

/**
 * Created by zf on 2017/3/9.
 */

public class LDMainActivity extends BaseActivity implements View.OnClickListener {

    private Button loginButton;
    private Button saleButton;
    private Button revokeButton;
    private Button transQueryButton;
    private Button startServiceButton;
    private Button stopServiceButton;
    private Button setParamButton;
//    private IWizarPayment wizarPayment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ldmainactivity);
//        AppManager.getAppManager().addActivity(this);
        initView();
        addEventListener();
        bindService();

    }

    void initView(){
        loginButton=(Button)findViewById(R.id.id_login);
        saleButton=(Button)findViewById(R.id.id_sale);
        revokeButton=(Button)findViewById(R.id.id_revoke);
        startServiceButton=(Button)findViewById(R.id.id_startService);
        stopServiceButton=(Button)findViewById(R.id.id_stopService);
        transQueryButton=(Button)findViewById(R.id.id_query);
        setParamButton=(Button)findViewById(R.id.id_setParams);

    }

    void addEventListener(){
        loginButton.setOnClickListener(this);
        saleButton.setOnClickListener(this);
        revokeButton.setOnClickListener(this);
        startServiceButton.setOnClickListener(this);
        stopServiceButton.setOnClickListener(this);
        transQueryButton.setOnClickListener(this);
        setParamButton.setOnClickListener(this);

    }
//    private ServiceConnection serviceConnection = new ServiceConnection()
//    {
//
////        public void onServiceDisconnected(ComponentName name)
////        {
////            ToastUtils.CustomShow(LDMainActivity.this,"链接已断开");
////            wizarPayment = null;
////        }
////
////        public void onServiceConnected(ComponentName name, IBinder service)
////        {
////            wizarPayment = IWizarPayment.Stub.asInterface(service);
////            ToastUtils.CustomShow(LDMainActivity.this,"链接成功");
////
////        }
//    };

    public void bindService()
    {
//        try
//        {
//            bindService(new Intent(IWizarPayment.class.getName()), serviceConnection, Context.BIND_AUTO_CREATE);
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
    }
//
//    public void unBindService()
//    {
//        if(wizarPayment != null)
//        {
//            unbindService(serviceConnection);
//            wizarPayment = null;
//        }
//    }


//    @Override
//    protected void onDestroy() {
//        unBindService();
//        super.onDestroy();
//
//    }

    @Override
    public void onClick(View view) {
//        JSONObject param = new JSONObject();
//        String ret = "ok";
//        if(view.getId() != R.id.id_startService && wizarPayment == null)
//        {
//            ToastUtils.CustomShow(this,"未连接服务");
//            return;
//        }
//        try {
//            param.put("AppID", "com.zfsbs");
//            param.put("AppName", "com.zfsbs");
//            switch (view.getId()) {
//
//                case R.id.id_login:
//                    param.put("OptCode", "01");
//                    param.put("OptPass", "0000");
////                    ret = wizarPayment.login(param.toString());
//                    LogUtils.e("zw", ret);
//                    break;
//                case R.id.id_sale:
//                    param.put("TransAmount", "100");
////                param.put("TransType", 1);
////                    ret = wizarPayment.payCash(param.toString());
//                    break;
//                case R.id.id_revoke:
////                param.put("TransType", 4);
////                ret = wizarPayment.consumeCancel(param.toString());
////                    ret = wizarPayment.consumeCancel(param.toString());
//                    break;
//                case R.id.id_startService:
//                    bindService();
//                    break;
//                case R.id.id_stopService:
//                    unBindService();
//                    break;
//                case R.id.id_query:
//
//                    break;
//                case R.id.id_setParams:
//
//                    break;
//
//            }
//        }catch (JSONException e){
//            e.printStackTrace();
//        }catch (RemoteException e){
//            e.printStackTrace();
//        }
//        ToastUtils.CustomShow(this,ret);
//
    }
}
