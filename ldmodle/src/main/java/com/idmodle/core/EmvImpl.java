package com.idmodle.core;

/**
 * Created by zf on 2017/3/10.
 */

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.view.KeyEvent;

import com.tool.utils.dialog.LoadingDialog;
import com.tool.utils.utils.LogUtils;
import com.tool.utils.utils.StringUtils;
import com.tool.utils.utils.ToastUtils;



import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;


import com.tool.utils.dialog.LoadingDialog;
import com.tool.utils.utils.LogUtils;
import com.tool.utils.utils.StringUtils;
import com.tool.utils.utils.ToastUtils;
import com.wizarpos.paymentrouter.aidl.IWizarPayment;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;


public class EmvImpl extends Activity{

    private static final String TAG = "EmvImpl";


    private static EmvImpl emvImpl;



    // 状态信息
    private int errorCode;
    private String errorMessage;
    private int curProcessCode;
    private String curProcessMessage;

    private final int PROGRESS_NOTIFIER = 1;
    private final int SUCCESS_NOTIFIER = 2;
    private final int FAIL_NOTIFIER = 3;

    private Context mContext;
    private LoadingDialog dialog;
    private int trans_amount;
    private int oldTraceNo;
    private String oldRrn;
    private String oldDate;


    //富友双商户 密钥索引对应
    public static final int FY_INDEX_0 = 0;
    public static final int FY_INDEX_1 = 1;

    private TransResult transResult;
    Map<String,String> params;

    private IWizarPayment wizarPayment = null;
    private String ret="";

    private ServiceConnection serviceConnection = new ServiceConnection()
    {

        public void onServiceDisconnected(ComponentName name)
        {

            wizarPayment = null;
            ToastUtils.CustomShow(mContext,"联动链接已断开");


        }

        public void onServiceConnected(ComponentName name, IBinder service)
        {
            wizarPayment = IWizarPayment.Stub.asInterface(service);

            ToastUtils.CustomShow(mContext,"联动链接成功");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    TransInfo transInfo=new TransInfo();

                    transInfo.setRespCode("00");

                    transInfo.setRespDesc("");

                    Message msg=new Message();
                    msg.what=SUCCESS_NOTIFIER;
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("data",transInfo);
                    msg.setData(bundle);
                    handler.sendMessage(msg);


                }
            }).start();
//            TransInfo transInfo=new TransInfo();
//            transResult.success(transInfo);



        }
    };

    public void bindService()
    {

        try
        {
            mContext.bindService(new Intent(IWizarPayment.class.getName()), serviceConnection, Context.BIND_AUTO_CREATE);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void unBindService()
    {
        if(wizarPayment != null)
        {
            mContext.unbindService(serviceConnection);
            wizarPayment = null;
        }
    }

    public interface TransResult<T> {
        public void success(T transInfo);

        public void failed(String error);
    }



    public EmvImpl(Context context) {

    }

    public synchronized static EmvImpl getInstance(Context context) {
        if (emvImpl == null) {
            emvImpl = new EmvImpl(context);
        }
        return emvImpl;
    }



    private LoadingDialog CustomDialog(Context context, String message) {
        if (dialog == null) {
            dialog = new LoadingDialog(context);
            dialog.setOnKeyListener(KeyListener);
        }
        mContext = context;
        dialog.show(message);
        return dialog;
    }

    public void customDialogDismiss() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    private DialogInterface.OnKeyListener KeyListener = new DialogInterface.OnKeyListener() {

        @Override
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                LogUtils.e(TAG,"================onkey===========");
                customDialogDismiss();

            }
            return false;
        }
    };

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            Bundle bundle=msg.getData();
            TransInfo transInfo=(TransInfo)bundle.getSerializable("data");
              switch (msg.what){


                  case SUCCESS_NOTIFIER:
                      transResult.success(transInfo);
                      break;
                  case FAIL_NOTIFIER:
                      if (transInfo!=null)
                      transResult.failed(transInfo.getRespDesc());
                      break;
              }
        }

        ;
    };












    /**
     * 获取参数
     */
    public void getParam(Context context, TransResult result) {
        mContext = context;
        transResult = result;

    }

    /**
     * 设置参数
     */
    public void setParam(Context context, int index, final String mid, final String tid, TransResult result) {//, int mid) {
        CustomDialog(context, "正在上传数据...");
        transResult = result;
        mContext=context;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    if (wizarPayment==null){
                        ToastUtils.CustomShow(mContext,"服务已断开");
                        return;
                    }
                    JSONObject param=new JSONObject();
                    param.put("AppID", "com.zfsbs");
                    param.put("AppName", "com.zfsbs");





                    SimpleDateFormat dateFormat = new SimpleDateFormat("MMdd");
                    String date=dateFormat.format(new Date());
                    param.put("ReqTransDate",date);
                    param.put("TransType","501");

                    param.put("tid", tid);
                    param.put("mid", mid);
                    param.put("priIP","106.120.215.234");
                    param.put("priPort", "4008");

                    ret=wizarPayment.transact(param.toString());
                    customDialogDismiss();
                    try{
                        if (ret.length()>0) {
                            JSONObject jsonObject = new JSONObject(ret);
                            TransInfo transInfo=new TransInfo();

                            transInfo.setRespCode(jsonObject.getString("RespCode"));

                            transInfo.setRespDesc(jsonObject.getString("RespDesc"));
                            if (transInfo.getRespCode().equals("00")) {
                                Message msg=new Message();
                                msg.what=SUCCESS_NOTIFIER;
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("data",transInfo);
                                msg.setData(bundle);
                                handler.sendMessage(msg);

                            }else{
                                Message msg=new Message();
                                msg.what=FAIL_NOTIFIER;
                                Bundle bundle = new Bundle();
                                msg.setData(bundle);
                                bundle.putSerializable("data",transInfo);
                                handler.sendMessage(msg);

                            }

                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }

//            {"RespCode":"00","TransTime":"164003","TID":"122010000011","RespDesc":"交易成功","OptCode":"01","AppID":"com.zfsbs","AppName":"com.zfsbs","MID":"829393115200089","OperatorType":1,"BatchNum":10010,"TransDate":"0310"}

                    LogUtils.e(ret);
                }catch (JSONException e){
                    e.printStackTrace();
                }catch (RemoteException e){
                    e.printStackTrace();
                }
            }
        }).start();





    }

    /**
     * 下载参数
     * @param context
     * @param result
     */
    public void downLoadParams(Context context, TransResult result){
        CustomDialog(context, "正在下载参数,请耐心等待...");
        transResult = result;

    }

    /**
     * 下载主密钥
     */
    public void InitKey(Context context, TransResult result) {//, int mid) {
        CustomDialog(context, "正在下载主密钥,请耐心等待...");
        transResult = result;

    }

    /**
     * 下载主密钥
     */
    public void DownTmk(Context context, String other, String mid, String tid, TransResult result){
        CustomDialog(context, "正在下载主密钥,请耐心等待...");
        transResult = result;

    }
    /**
     * 下载黑名单
     */
    public void DownBlackList(Context context, TransResult result){
        CustomDialog(context, "正在下载黑名单,请耐心等待...");
        transResult = result;

    }

    /**
     * 下载AID
     */
    public void downloadAid(Context context, TransResult result) {
        CustomDialog(context, "正在下载AID参数,请耐心等待...");
        transResult = result;

    }

    /**
     * 下载公钥
     */
    public void downloadCapk(Context context, TransResult result) {
        CustomDialog(context, "正在下载公钥参数,请耐心等待...");
        transResult = result;

    }


    public void bindService(Context context,TransResult result){
        transResult = result;
        mContext=context;
        bindService();
    }

    public void unBindService(Context context){
            mContext=context;
        unBindService();
    }
    /**
     * 签到
     */
    public void login(Context context, String mid, String tid,TransResult result) {//, int mid) {
        CustomDialog(context, "正在签到...");
        transResult = result;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    if (wizarPayment==null){
                        ToastUtils.CustomShow(mContext,"服务已断开");
                        return;
                    }
                    JSONObject param=new JSONObject();
                    param.put("AppID", "com.zfsbs");
                    param.put("AppName", "com.zfsbs");
                    param.put("OptCode", "01");
                    param.put("OptPass", "0000");

//                    getPOSInfo
//                    SimpleDateFormat dateFormat = new SimpleDateFormat("MMdd");
//                    String date=dateFormat.format(new Date());
//                    param.put("ReqTransDate",date);
//                    param.put("TransType","99");


                    SimpleDateFormat dateFormat = new SimpleDateFormat("MMdd");
                    String date=dateFormat.format(new Date());
//                    param.put("ReqTransDate",date);
//                    param.put("TransType","11");

//                    param.put("priIP","106.120.215.234");
//                    param.put("priPort","4008");
//                    param.put("mid","829393115200089");
//                    param.put("tid","122010000011");

                    ret=wizarPayment.login(param.toString());
                    customDialogDismiss();
                    try{
                        if (ret.length()>0) {
                            JSONObject jsonObject = new JSONObject(ret);
                            TransInfo transInfo=new TransInfo();
                            transInfo.setTransTime(jsonObject.getString("TransTime"));
                            transInfo.setTransDate(jsonObject.getString("TransDate"));
                            transInfo.setRespCode(jsonObject.getString("RespCode"));
                            transInfo.setTid(jsonObject.getString("TID"));
                            transInfo.setRespDesc(jsonObject.getString("RespDesc"));
                            transInfo.setBatchNumber(jsonObject.getInt("BatchNum"));
                            transInfo.setMerchantName(jsonObject.getString("MID"));
                            if (transInfo.getRespCode().equals("00")) {
                                Message msg=new Message();
                                msg.what=SUCCESS_NOTIFIER;
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("data",transInfo);
                                msg.setData(bundle);
                                handler.sendMessage(msg);

                            }else{
                                Message msg=new Message();
                                msg.what=FAIL_NOTIFIER;
                                Bundle bundle = new Bundle();
                                msg.setData(bundle);
                                bundle.putSerializable("data",transInfo);
                                handler.sendMessage(msg);

                            }

                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }

//            {"RespCode":"00","TransTime":"164003","TID":"122010000011","RespDesc":"交易成功","OptCode":"01","AppID":"com.zfsbs","AppName":"com.zfsbs","MID":"829393115200089","OperatorType":1,"BatchNum":10010,"TransDate":"0310"}

                    LogUtils.e(ret);
                }catch (JSONException e){
                    e.printStackTrace();
                }catch (RemoteException e){
                    e.printStackTrace();
                }
            }
        }).start();





    }

    /**
     * 结算
     */
    public void settle(Context context, String mid, String tid, TransResult result) {//, int mid) {
        CustomDialog(context, "正在上传数据...");
        transResult = result;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    if (wizarPayment==null){
                        ToastUtils.CustomShow(mContext,"服务已断开");
                        return;
                    }
                    JSONObject param=new JSONObject();
                    param.put("AppID", "com.zfsbs");
                    param.put("AppName", "com.zfsbs");


                    ret=wizarPayment.settle(param.toString());
                    customDialogDismiss();
                    try{
                        if (ret.length()>0) {
                            JSONObject jsonObject = new JSONObject(ret);
                            TransInfo transInfo=new TransInfo();

                            transInfo.setRespCode(jsonObject.getString("RespCode"));
                            transInfo.setRespDesc(jsonObject.getString("RespDesc"));
                            if (transInfo.getRespCode().equals("00")) {
                                Message msg=new Message();
                                msg.what=SUCCESS_NOTIFIER;
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("data",transInfo);
                                msg.setData(bundle);
                                handler.sendMessage(msg);

                            }else{
                                Message msg=new Message();
                                msg.what=FAIL_NOTIFIER;
                                Bundle bundle = new Bundle();
                                msg.setData(bundle);
                                bundle.putSerializable("data",transInfo);
                                handler.sendMessage(msg);

                            }

                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }

//            {"RespCode":"00","TransTime":"164003","TID":"122010000011","RespDesc":"交易成功","OptCode":"01","AppID":"com.zfsbs","AppName":"com.zfsbs","MID":"829393115200089","OperatorType":1,"BatchNum":10010,"TransDate":"0310"}

                    LogUtils.e(ret);
                }catch (JSONException e){
                    e.printStackTrace();
                }catch (RemoteException e){
                    e.printStackTrace();
                }
            }
        }).start();





    }

    /**
     * 查询余额
     */
    public void balance(Context context, String mid, String tid) {//, int mid) {
        CustomDialog(context, "正在查询余额...");

    }

    /**
     * 消费
     */
    public void sale(Context context, int amount, String mid, String tid, TransResult result) {

        trans_amount = amount;
        transResult = result;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    if (wizarPayment==null){
                        ToastUtils.CustomShow(mContext,"服务已断开");
                        return;
                    }
                    JSONObject param=new JSONObject();
                    param.put("AppID", "com.zfsbs");
                    param.put("AppName", "com.zfsbs");
                    param.put("TransType", "1");
                    param.put("TransAmount", trans_amount+"");

                    param.put("TransIndexCode",StringUtils.createOrderNo());

                    SimpleDateFormat dateFormat = new SimpleDateFormat("MMdd");
                    String date=dateFormat.format(new Date());
                    param.put("ReqTransDate",date);
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");
                    String time=timeFormat.format(new Date());
                    param.put("ReqTransTime",time);
                    param.put("NoPrintReceipt","1");

                    LogUtils.e(param.toString());
                    ret=wizarPayment.payCash(param.toString());

                    try{
                        if (ret.length()>0) {
                            JSONObject jsonObject = new JSONObject(ret);
                            TransInfo transInfo=new TransInfo();
                            transInfo.setIssuerName(jsonObject.getString("IssuerName"));
                            transInfo.setRrn(jsonObject.getString("ReferCode"));       //索引号
                            transInfo.setMid(jsonObject.getString("MerchantID"));
                            transInfo.setBatchNumber(jsonObject.getInt("BatchNum"));
                            transInfo.setTransAmount(jsonObject.getInt("TransAmount"));
                            transInfo.setAcquirerName(jsonObject.getString("AcquirerName"));
                            transInfo.setTransType(jsonObject.getInt("TransType"));
                            transInfo.setTrace(jsonObject.getInt("CertNum"));
                            transInfo.setPan(jsonObject.getString("CardNum"));
                            transInfo.setExpiryDate(jsonObject.getString("Expiry"));
                            transInfo.setAuthCode(jsonObject.getString("AuthCode"));


                            transInfo.setTransTime(jsonObject.getString("TransTime"));
                            transInfo.setTransDate(jsonObject.getString("TransDate"));
                            transInfo.setRespCode(jsonObject.getString("RespCode"));
                            transInfo.setTid(jsonObject.getString("TerminalID"));
                            transInfo.setRespDesc(jsonObject.getString("RespDesc"));


                            if (transInfo.getRespCode().equals("00")) {
                                Message msg=new Message();
                                msg.what=SUCCESS_NOTIFIER;
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("data",transInfo);
                                msg.setData(bundle);
                                handler.sendMessage(msg);

                            }else{
                                Message msg=new Message();
                                msg.what=FAIL_NOTIFIER;
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("data",transInfo);
                                msg.setData(bundle);
                                handler.sendMessage(msg);

                            }

                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }

//            {"RespCode":"00","TransTime":"164003","TID":"122010000011","RespDesc":"交易成功","OptCode":"01","AppID":"com.zfsbs","AppName":"com.zfsbs","MID":"829393115200089","OperatorType":1,"BatchNum":10010,"TransDate":"0310"}

                    LogUtils.e(ret);
                }catch (JSONException e){
                    e.printStackTrace();
                }catch (RemoteException e){
                    e.printStackTrace();
                }
            }
        }).start();



    }

    /**
     * 消费撤销
     */
    public void voidSale(Context context, int trace_no, String mid, String tid, TransResult result) {//, int mid) {

        oldTraceNo = trace_no;
        transResult = result;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    if (wizarPayment==null){
                        ToastUtils.CustomShow(mContext,"服务已断开");
                        return;
                    }
                    JSONObject param=new JSONObject();
                    param.put("AppID", "com.zfsbs");
                    param.put("AppName", "com.zfsbs");
                    param.put("TransType", "13");
//                    param.put("AppMchntNo",mid);
//                    param.put("AppTermNo",tid);
                    param.put("NoPrintReceipt","1");
//            param.put("operatorPwd","123456");
//            param.put("oriTraceNo", oldTraceNo);



                    LogUtils.e(param.toString());
                    ret=wizarPayment.consumeCancel(param.toString());

                    try{
                        if (ret.length()>0) {
                            JSONObject jsonObject = new JSONObject(ret);
                            TransInfo transInfo=new TransInfo();
                            transInfo.setIssuerName(jsonObject.getString("IssuerName"));
                            transInfo.setRrn(jsonObject.getString("ReferCode"));       //索引号
                            transInfo.setMid(jsonObject.getString("MerchantID"));
                            transInfo.setBatchNumber(jsonObject.getInt("BatchNum"));
                            transInfo.setTransAmount(jsonObject.getInt("TransAmount"));
                            transInfo.setAcquirerName(jsonObject.getString("AcquirerName"));
                            transInfo.setTransType(jsonObject.getInt("TransType"));
                            transInfo.setTrace(jsonObject.getInt("CertNum"));
                            transInfo.setPan(jsonObject.getString("CardNum"));
                            transInfo.setExpiryDate(jsonObject.getString("Expiry"));
                            transInfo.setAuthCode(jsonObject.getString("AuthCode"));


                            transInfo.setTransTime(jsonObject.getString("TransTime"));
                            transInfo.setTransDate(jsonObject.getString("TransDate"));
                            transInfo.setRespCode(jsonObject.getString("RespCode"));
                            transInfo.setTid(jsonObject.getString("TerminalID"));
                            transInfo.setRespDesc(jsonObject.getString("RespDesc"));
                            transInfo.setOldTrace(jsonObject.getInt("OldTrace"));


                            if (transInfo.getRespCode().equals("00")) {
                                Message msg=new Message();
                                msg.what=SUCCESS_NOTIFIER;
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("data",transInfo);
                                msg.setData(bundle);
                                handler.sendMessage(msg);

                            }else{
                                Message msg=new Message();
                                msg.what=FAIL_NOTIFIER;
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("data",transInfo);
                                msg.setData(bundle);
                                handler.sendMessage(msg);

                            }

                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }

//            {"RespCode":"00","TransTime":"164003","TID":"122010000011","RespDesc":"交易成功","OptCode":"01","AppID":"com.zfsbs","AppName":"com.zfsbs","MID":"829393115200089","OperatorType":1,"BatchNum":10010,"TransDate":"0310"}

                    LogUtils.e(ret);
                }catch (JSONException e){
                    e.printStackTrace();
                }catch (RemoteException e){
                    e.printStackTrace();
                }

            }
        }).start();
    }

    /**
     * 退货
     */
    public void refund(Context context, int amount, String old_rrn, String old_date, String mid, String tid, TransResult result) {//, int mid) {
        CustomDialog(context, "请刷卡或者插卡");
        trans_amount = amount;
        oldRrn = old_rrn;
        oldDate = old_date;
        transResult = result;

    }

    /**
     * 设置参数
     */
    public void setParam(Context context, Object object, TransResult result) {//, int mid) {
        CustomDialog(context, "正在上传数据...");
        transResult = result;
        params=(Map<String, String>) object;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    if (wizarPayment==null){
                        ToastUtils.CustomShow(mContext,"服务已断开");
                        return;
                    }
                    JSONObject param=new JSONObject();
                    param.put("AppID", "com.zfsbs");
                    param.put("AppName", "com.zfsbs");





                    SimpleDateFormat dateFormat = new SimpleDateFormat("MMdd");
                    String date=dateFormat.format(new Date());
                    param.put("ReqTransDate",date);
                    param.put("TransType","501");

                    if (params.get("tid").length()==8) {
                        param.put("tid", params.get("tid"));
                    }
                    if (params.get("mid").length()==15) {
                        param.put("mid", params.get("mid"));
                    }
                    if (params.get("priIP").length()>7) {
                        param.put("priIP", params.get("priIP"));
                    }
                    if (params.get("priPort").length()>0) {
                        param.put("priPort", params.get("priPort"));
                    }



                    ret=wizarPayment.transact(param.toString());
                    customDialogDismiss();
                    try{
                        if (ret.length()>0) {
                            JSONObject jsonObject = new JSONObject(ret);
                            TransInfo transInfo=new TransInfo();

                            transInfo.setRespCode(jsonObject.getString("RespCode"));

                            transInfo.setRespDesc(jsonObject.getString("RespDesc"));
                            if (transInfo.getRespCode().equals("00")) {
                                Message msg=new Message();
                                msg.what=SUCCESS_NOTIFIER;
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("data",transInfo);
                                msg.setData(bundle);
                                handler.sendMessage(msg);

                            }else{
                                Message msg=new Message();
                                msg.what=FAIL_NOTIFIER;
                                Bundle bundle = new Bundle();
                                msg.setData(bundle);
                                bundle.putSerializable("data",transInfo);
                                handler.sendMessage(msg);

                            }

                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }

//            {"RespCode":"00","TransTime":"164003","TID":"122010000011","RespDesc":"交易成功","OptCode":"01","AppID":"com.zfsbs","AppName":"com.zfsbs","MID":"829393115200089","OperatorType":1,"BatchNum":10010,"TransDate":"0310"}

                    LogUtils.e(ret);
                }catch (JSONException e){
                    e.printStackTrace();
                }catch (RemoteException e){
                    e.printStackTrace();
                }
            }
        }).start();





    }

}



