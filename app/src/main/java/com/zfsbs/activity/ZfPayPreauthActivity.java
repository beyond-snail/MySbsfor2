package com.zfsbs.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mycommonlib.core.PayCommon;
import com.mycommonlib.model.ComTransInfo;
import com.tool.utils.activityManager.AppManager;
import com.tool.utils.dialog.CommonDialog;
import com.tool.utils.dialog.LoadingDialog;
import com.tool.utils.utils.ALog;
import com.tool.utils.utils.AlertUtils;
import com.tool.utils.utils.LogUtils;
import com.tool.utils.utils.SPUtils;
import com.tool.utils.utils.StringUtils;
import com.tool.utils.utils.ToastUtils;
import com.zfsbs.R;
import com.zfsbs.common.CommonFunc;
import com.zfsbs.config.Config;
import com.zfsbs.config.Constants;
import com.zfsbs.core.action.Printer;
import com.zfsbs.core.myinterface.ActionCallbackListener;
import com.zfsbs.model.Couponsn;
import com.zfsbs.model.SbsPrinterData;
import com.zfsbs.model.TransUploadRequest;
import com.zfsbs.model.TransUploadResponse;
import com.zfsbs.myapplication.MyApplication;

import org.litepal.crud.DataSupport;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.zfsbs.common.CommonFunc.startAction;

public class ZfPayPreauthActivity extends BaseActivity implements OnClickListener {

    private static final String TAG = "ZfPayActivity";

    private TextView tOrderAmount;
    private TextView tPayAmount;
    private TextView tPayPointAmount;
    private TextView tPayCouponAmount;

//    private LinearLayout btnPayflot;
//    private LinearLayout btnCash;
//    private LinearLayout btnAly;
//    private LinearLayout btnWx;
//    private LinearLayout btnQb;


    private LinearLayout btnPreauth;
    private LinearLayout btnAuthCancel;
    private LinearLayout btnAuthComplete;
    private LinearLayout btnVoidAuthComplete;



    private Button btnPrint;
    private Button btnPrintfinish;
    private Button btnNopayAmount;
    private Button btnQuery;
    private Button btnQueryEnd;

    private LinearLayout ll_payType;
    private LinearLayout ll_payFinish;
    private LinearLayout ll_payQuery;

    private List<SbsPrinterData> allData;
    private String orderNo;


    private int amount;


    private int app_type = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_pay_preauth_type);
        initTitle("预授权");

        app_type = (int) SPUtils.get(this, Config.APP_TYPE, Config.DEFAULT_APP_TYPE);

        amount = getIntent().getIntExtra("amount", 0);


        initView();
        addListenster();

    }

    private void initView() {


        tOrderAmount = (TextView) findViewById(R.id.id_orderAmount);
        tPayAmount = (TextView) findViewById(R.id.id_payAmount);
        tPayPointAmount = (TextView) findViewById(R.id.id_pointAmount);
        tPayCouponAmount = (TextView) findViewById(R.id.id_coupon_amount);

        tOrderAmount.setText(StringUtils.formatIntMoney(amount));
        tPayAmount.setText(StringUtils.formatIntMoney(amount));


        btnPreauth = (LinearLayout) findViewById(R.id.pay_preauth);
        btnAuthCancel = (LinearLayout) findViewById(R.id.pay_authCancel);
        btnAuthComplete = (LinearLayout) findViewById(R.id.pay_authComplete);
        btnVoidAuthComplete = (LinearLayout) findViewById(R.id.pay_voidAuthComplete);


        btnPrint = (Button) findViewById(R.id.id_print);
        btnPrintfinish = (Button) findViewById(R.id.id_finish);
        btnNopayAmount = (Button) findViewById(R.id.id_no_pay_amount);
        btnQuery = (Button) findViewById(R.id.id_query);
        btnQueryEnd = (Button) findViewById(R.id.id_terminal_query_sure);


        ll_payType = (LinearLayout) findViewById(R.id.ll_pay_type);
        ll_payFinish = (LinearLayout) findViewById(R.id.ll_pay_finish);
        ll_payQuery = (LinearLayout) findViewById(R.id.ll_pay_query);

    }





    private void addListenster() {

        btnPreauth.setOnClickListener(this);
        btnAuthCancel.setOnClickListener(this);
        btnAuthComplete.setOnClickListener(this);
        btnVoidAuthComplete.setOnClickListener(this);


        btnPrint.setOnClickListener(this);
        btnPrintfinish.setOnClickListener(this);
        btnNopayAmount.setOnClickListener(this);
        btnQuery.setOnClickListener(this);
        btnQueryEnd.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        CommonFunc.startAction(this, InputAmountActivity2.class, true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_print:

                Gson gson = new Gson();
                TransUploadRequest data = gson.fromJson(printerData.getTransUploadData(), TransUploadRequest.class);
                LogUtils.e(data.toString());
                getPrinterData(data);

                break;
            case R.id.id_finish:
            case R.id.id_terminal_query_sure: {
                startAction(this, InputAmountActivity2.class, true);
            }
                break;
            case R.id.pay_preauth:
                preauth();
                break;
            case R.id.pay_authCancel:
                authCancel();
                break;
            case R.id.pay_authComplete:
                authComplete();
                break;
            case R.id.pay_voidAuthComplete:
                voidAuthComplete();
                break;
            default:
                break;
        }
    }


    /**
     * 预授权
     */
    private void preauth() {
        String mid = MyApplication.getInstance().getLoginData().getMerchantNo();
        String tid = MyApplication.getInstance().getLoginData().getTerminalNo();
        PayCommon.PreAuth(this, amount, mid, tid, new PayCommon.ComTransResult<ComTransInfo>() {
            @Override
            public void success(ComTransInfo transInfo) {
                ToastUtils.CustomShow(ZfPayPreauthActivity.this, "预授权成功");
                setFlotPrintData1(transInfo, Constants.PAY_WAY_PREAUTH);


                //设置流水上送需要上送的参数
                TransUploadRequest request = CommonFunc.setTransUploadData(printerData,
                        CommonFunc.recoveryMemberInfo(ZfPayPreauthActivity.this),
                        CommonFunc.getNewClientSn(),
                        printerData.getVoucherNo(), printerData.getReferNo());

                //打印的订单号与流水上送的统一
                printerData.setClientOrderNo(request.getClientOrderNo());

                //流水上送
                transUploadAction1(request);
            }

            @Override
            public void failed(String error) {
                final CommonDialog confirmDialog = new CommonDialog(ZfPayPreauthActivity.this, error);
                confirmDialog.show();
                confirmDialog.setClicklistener(new CommonDialog.ClickListenerInterface() {
                    @Override
                    public void doConfirm() {

                    }
                });
            }
        });
    }


    /**
     * 预授权撤销
     */
    private void authCancel(){

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_set_password, null);
        LinearLayout ll_authcode = (LinearLayout) view.findViewById(R.id.id_ll_authCode);
        ll_authcode.setVisibility(View.VISIBLE);
        LinearLayout ll_date = (LinearLayout) view.findViewById(R.id.id_ll_date);
        ll_date.setVisibility(View.VISIBLE);
        final EditText etName = (EditText) view.findViewById(R.id.et_name);
        final EditText etOldDate = (EditText) view.findViewById(R.id.et_Ic_no);
        AlertUtils.alertSetPassword(this, "输入授权码", "确认",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       dialog.dismiss();
                        String mid = MyApplication.getInstance().getLoginData().getMerchantNo();
                        String tid = MyApplication.getInstance().getLoginData().getTerminalNo();
                        String authCode = etName.getText().toString().trim();
                        String oldDate = etOldDate.getText().toString().trim();
                        if (StringUtils.isBlank(authCode)){
                            ToastUtils.CustomShow(ZfPayPreauthActivity.this, "授权码不可为空");
                            return;
                        }
                        if (StringUtils.isBlank(oldDate)){
                            ToastUtils.CustomShow(ZfPayPreauthActivity.this, "输入日期");
                            return;
                        }
                        if (!getAuthCode(authCode, Constants.PAY_WAY_PREAUTH)){
                            return;
                        }
                        PayCommon.AuthCancel(ZfPayPreauthActivity.this, amount, mid, tid, authCode, oldDate, new PayCommon.ComTransResult<ComTransInfo>() {
                            @Override
                            public void success(ComTransInfo transInfo) {
                                ToastUtils.CustomShow(ZfPayPreauthActivity.this, "预授权撤销成功");
                                setFlotPrintData1(transInfo, Constants.PAY_WAY_AUTHCANCEL);


                                //设置流水上送需要上送的参数
//                                TransUploadRequest request = CommonFunc.setTransUploadData(printerData,
//                                        CommonFunc.recoveryMemberInfo(ZfPayPreauthActivity.this),
//                                        CommonFunc.getNewClientSn(ZfPayPreauthActivity.this, printerData.getPayType()),
//                                        printerData.getVoucherNo(), printerData.getReferNo());
//
//                                //打印的订单号与流水上送的统一
//                                printerData.setClientOrderNo(request.getClientOrderNo());

                                //流水上送
//                                transUploadAction1(request);
                                setTransCancel(Constants.PAY_WAY_AUTHCANCEL);
                            }

                            @Override
                            public void failed(String error) {
                                final CommonDialog confirmDialog = new CommonDialog(ZfPayPreauthActivity.this, error);
                                confirmDialog.show();
                                confirmDialog.setClicklistener(new CommonDialog.ClickListenerInterface() {
                                    @Override
                                    public void doConfirm() {

                                    }
                                });
                            }
                        });
                    }

                }, view);


    }


    /**
     * 预授权完成
     */
    private void authComplete(){
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_set_password, null);
        LinearLayout ll_authcode = (LinearLayout) view.findViewById(R.id.id_ll_authCode);
        ll_authcode.setVisibility(View.VISIBLE);
        LinearLayout ll_date = (LinearLayout) view.findViewById(R.id.id_ll_date);
        ll_date.setVisibility(View.VISIBLE);
        final EditText etName = (EditText) view.findViewById(R.id.et_name);
        final EditText etOldDate = (EditText) view.findViewById(R.id.et_Ic_no);

        AlertUtils.alertSetPassword(this, "输入原交易信息", "确认",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        String mid = MyApplication.getInstance().getLoginData().getMerchantNo();
                        String tid = MyApplication.getInstance().getLoginData().getTerminalNo();
                        String authCode = etName.getText().toString().trim();
                        String oldDate = etOldDate.getText().toString().trim();
                        if (StringUtils.isBlank(authCode)){
                            ToastUtils.CustomShow(ZfPayPreauthActivity.this, "授权码不可为空");
                            return;
                        }
                        if (StringUtils.isBlank(oldDate)){
                            ToastUtils.CustomShow(ZfPayPreauthActivity.this, "输入日期");
                            return;
                        }
                        if (!getAuthCode(authCode, Constants.PAY_WAY_PREAUTH)){
                            return;
                        }
                        PayCommon.AuthComplete(ZfPayPreauthActivity.this, amount, mid, tid, authCode, oldDate, new PayCommon.ComTransResult<ComTransInfo>() {
                            @Override
                            public void success(ComTransInfo transInfo) {
                                ToastUtils.CustomShow(ZfPayPreauthActivity.this, "预授权完成成功");
                                setFlotPrintData1(transInfo, Constants.PAY_WAY_AUTHCOMPLETE);

                                //设置流水上送需要上送的参数
                                TransUploadRequest request = CommonFunc.setTransUploadData(printerData,
                                        CommonFunc.recoveryMemberInfo(ZfPayPreauthActivity.this),
                                        CommonFunc.getNewClientSn(),
                                        printerData.getVoucherNo(), printerData.getReferNo());

                                //打印的订单号与流水上送的统一
                                printerData.setClientOrderNo(request.getClientOrderNo());

                                //流水上送
                                transUploadAction1(request);
//                                setTransCancel(Constants.PAY_WAY_AUTHCOMPLETE);
                            }

                            @Override
                            public void failed(String error) {
                                final CommonDialog confirmDialog = new CommonDialog(ZfPayPreauthActivity.this, error);
                                confirmDialog.show();
                                confirmDialog.setClicklistener(new CommonDialog.ClickListenerInterface() {
                                    @Override
                                    public void doConfirm() {

                                    }
                                });
                            }
                        });
                    }

                }, view);

    }

    private void voidAuthComplete(){

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_set_password, null);
        LinearLayout ll_trace_no = (LinearLayout) view.findViewById(R.id.id_ll_trace_no);
        ll_trace_no.setVisibility(View.VISIBLE);
        final EditText ettrace = (EditText) view.findViewById(R.id.et_trace);
        AlertUtils.alertSetPassword(this, "输入原交易信息", "确认",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        String mid = MyApplication.getInstance().getLoginData().getMerchantNo();
                        String tid = MyApplication.getInstance().getLoginData().getTerminalNo();

                        if (StringUtils.isBlank(ettrace.getText().toString().trim())){
                            ToastUtils.CustomShow(ZfPayPreauthActivity.this, "原交易流水不可为空");
                            return;
                        }
                        int trace_no = Integer.parseInt(ettrace.getText().toString());
                        if (!getTraceNo(ettrace.getText().toString().trim(), Constants.PAY_WAY_AUTHCOMPLETE)){
                            return;
                        }
                        PayCommon.VoidAuthComplete(ZfPayPreauthActivity.this, amount, mid, tid,  trace_no, new PayCommon.ComTransResult<ComTransInfo>() {
                            @Override
                            public void success(ComTransInfo transInfo) {
                                ToastUtils.CustomShow(ZfPayPreauthActivity.this, "预授权完成撤销成功");
                                setFlotPrintData1(transInfo, Constants.PAY_WAY_VOID_AUTHCOMPLETE);

                                //设置流水上送需要上送的参数
//                                TransUploadRequest request = CommonFunc.setTransUploadData(printerData,
//                                        CommonFunc.recoveryMemberInfo(ZfPayPreauthActivity.this),
//                                        CommonFunc.getNewClientSn(ZfPayPreauthActivity.this, printerData.getPayType()),
//                                        printerData.getVoucherNo(), printerData.getReferNo());
//
//                                //打印的订单号与流水上送的统一
//                                printerData.setClientOrderNo(request.getClientOrderNo());

                                //流水上送
//                                transUploadAction1(request);
                                setTransCancel(Constants.PAY_WAY_VOID_AUTHCOMPLETE);
                            }

                            @Override
                            public void failed(String error) {
                                final CommonDialog confirmDialog = new CommonDialog(ZfPayPreauthActivity.this, error);
                                confirmDialog.show();
                                confirmDialog.setClicklistener(new CommonDialog.ClickListenerInterface() {
                                    @Override
                                    public void doConfirm() {

                                    }
                                });
                            }
                        });
                    }

                }, view);


    }




    protected void setFlotPrintData1(ComTransInfo transInfo, int type) {
        printerData.setMerchantName(MyApplication.getInstance().getLoginData().getTerminalName());
        printerData.setMerchantNo(MyApplication.getInstance().getLoginData().getMerchantNo());//(transInfo.getMid());
        printerData.setTerminalId(transInfo.getTid());
        printerData.setOperatorNo((String) SPUtils.get(this, Constants.USER_NAME, ""));
        printerData.setAcquirer(transInfo.getAcquirerCode());
        printerData.setAuthNo(transInfo.getAuthCode());
        printerData.setIssuer(transInfo.getIssuerCode());
        printerData.setCardNo(transInfo.getPan());
        printerData.setTransType(transInfo.getTransType() + "");
        printerData.setExpDate(transInfo.getExpiryDate());
        printerData.setBatchNO(StringUtils.fillZero(transInfo.getBatchNumber() + "", 6));
        printerData.setVoucherNo(StringUtils.fillZero(transInfo.getTrace() + "", 6));
        printerData.setDateTime(
                StringUtils.formatTime(StringUtils.getCurYear() + transInfo.getTransDate() + transInfo.getTransTime()));
        printerData.setReferNo(transInfo.getRrn());
        printerData.setOrderAmount(amount);
        printerData.setAmount(StringUtils.formatIntMoney(transInfo.getTransAmount()));
        printerData.setPayType(type);
    }


    private boolean getAuthCode(String authCode, int type) {

        // 从交易记录中读取数据
        allData = DataSupport.order("id desc").limit(100).find(SbsPrinterData.class);
        if (allData.size() <= 0) {
            ToastUtils.CustomShow(this, "没有交易记录");
            return false;
        }

        // 遍历
        for (int i = 0; i < allData.size(); i++) {
            // 遍历刷卡支付
            if (allData.get(i).getPayType() == type && !StringUtils.isEmpty(allData.get(i).getAuthNo())) {
                if (StringUtils.isEquals(allData.get(i).getAuthNo(),authCode)) {
                    //获取交易的订单号
                    Gson gson = new Gson();
                    TransUploadRequest data = gson.fromJson(allData.get(i).getTransUploadData(), TransUploadRequest.class);

                    if (data != null) {
                        orderNo = data.getClientOrderNo();
                    }
                    return true;
                }
            }
        }
        ToastUtils.CustomShow(this, "没有该笔交易");
        return false;
    }


    private boolean getTraceNo(String traceNo, int type) {
        // 从交易记录中读取数据
        allData = DataSupport.order("id desc").limit(100).find(SbsPrinterData.class);
        if (allData.size() <= 0) {
            ToastUtils.CustomShow(this, "没有交易记录");
            return false;
        }

        // 遍历
        for (int i = 0; i < allData.size(); i++) {
            // 遍历刷卡支付
            if (allData.get(i).getPayType() == type && !StringUtils.isEmpty(allData.get(i).getVoucherNo())) {
                if (StringUtils.isEquals(allData.get(i).getVoucherNo(),traceNo)) {
                    //获取交易的订单号
                    Gson gson = new Gson();
                    TransUploadRequest data = gson.fromJson(allData.get(i).getTransUploadData(), TransUploadRequest.class);

                    if (data != null) {
                        orderNo = data.getClientOrderNo();
                    }
                    return true;
                }
            }
        }
        ToastUtils.CustomShow(this, "没有该笔交易");
        return false;
    }





    /**
     * 保存数据
     */
    private void PrinterDataSave() {

        CommonFunc.ClearFailureInfo(this);
        CommonFunc.PrinterDataDelete();
        printerData.setStatus(true);
        if (printerData.save()) {
            LogUtils.e("打印数据存储成功");
        } else {
            LogUtils.e("打印数据存储失败");
        }
    }


    private void getPrinterData(final TransUploadRequest request) {

        final LoadingDialog dialog = new LoadingDialog(this);
        dialog.show("获取打印信息...");
        this.sbsAction.getPrinterData(this, request.getSid(), request.getClientOrderNo(), new ActionCallbackListener<TransUploadResponse>() {

            @Override
            public void onSuccess(TransUploadResponse data) {
                setTransUpdateResponse(data, dialog, false);
            }

            @Override
            public void onFailure(String errorEvent, String message) {
                dialog.dismiss();
                ToastUtils.CustomShow(ZfPayPreauthActivity.this, errorEvent + "#" + message);
            }

            @Override
            public void onFailurTimeOut(String s, String error_msg) {

            }

            @Override
            public void onLogin() {
                dialog.dismiss();
                AppManager.getAppManager().finishAllActivity();
                if (Config.OPERATOR_UI_BEFORE) {
                    CommonFunc.startAction(ZfPayPreauthActivity.this, OperatorLoginActivity.class, false);
                } else {
                    CommonFunc.startAction(ZfPayPreauthActivity.this, OperatorLoginActivity1.class, false);
                }
            }
        });
    }



    /**
     * 撤销流水上送
     */
    private void setTransCancel(int type) {
        final TransUploadRequest request = new TransUploadRequest();
        String orderId = CommonFunc.getNewClientSn();
        printerData.setClientOrderNo(orderId);
        printerData.setOldOrderId(orderNo);
        request.setAction("2");
        request.setOld_trade_order_num(orderNo);
        request.setNew_trade_order_num(orderId);
        request.setPayType(type);
        request.setAuthCode(printerData.getVoucherNo());
        request.setClientOrderNo(orderNo);
        request.setT(StringUtils.getdate2TimeStamp(printerData.getDateTime()));

        this.sbsAction.transCancelRefund(this, request, new ActionCallbackListener<String>() {
            @Override
            public void onSuccess(String data) {
                showLayout();
                ToastUtils.CustomShow(ZfPayPreauthActivity.this, "撤销成功");
                printerData.setRefundUpload(true);
                //这个地方用来 在交易记录里去打印用的
                request.setSid(MyApplication.getInstance().getLoginData().getSid());
                // 备份下交易流水数据
                setTransUpLoadData(request);
                printerData.setApp_type(app_type);



                PrinterDataSave();
                // 打印
                Printer.print(printerData, ZfPayPreauthActivity.this);


            }

            @Override
            public void onFailure(String errorEvent, String message) {
                showLayout();
                ToastUtils.CustomShow(ZfPayPreauthActivity.this, "撤销成功");
                // 备份下交易流水数据
                request.setSid(MyApplication.getInstance().getLoginData().getSid());
                setTransUpLoadData(request);
                // 保存打印的数据，不保存图片数据
                printerData.setApp_type(app_type);
                PrinterDataSave();
                // 打印
                Printer.print(printerData, ZfPayPreauthActivity.this);

            }

            @Override
            public void onFailurTimeOut(String s, String error_msg) {

            }

            @Override
            public void onLogin() {

            }
        });
    }




    /**
     * 流水上送
     *
     * @param request
     */
    private void transUploadAction1(final TransUploadRequest request) {
        final LoadingDialog dialog = new LoadingDialog(this);
        dialog.show("正在上传交易流水...");
        dialog.setCancelable(false);
        this.sbsAction.transUpload(this, request, new ActionCallbackListener<TransUploadResponse>() {
            @Override
            public void onSuccess(TransUploadResponse data) {

                setTransUpLoadData(request);
                // 设置流水返回的数据
                setTransUpdateResponse(data, dialog, true);
            }

            @Override
            public void onFailure(String errorEvent, String message) {
                dialog.dismiss();
                ToastUtils.CustomShow(ZfPayPreauthActivity.this, errorEvent + "#" + message);
                showLayout();

                setTransUpLoadData(request);
                // 设置当前交易流水需要上送
                printerData.setUploadFlag(true);
                printerData.setApp_type(app_type);


                // 保存打印的数据，不保存图片数据
                PrinterDataSave();

                // 打印
                Printer.print(printerData, ZfPayPreauthActivity.this);
            }

            @Override
            public void onFailurTimeOut(String s, String error_msg) {

            }

            @Override
            public void onLogin() {
                dialog.dismiss();
                AppManager.getAppManager().finishAllActivity();



            }
        });
    }





    private void showLayout() {
        ll_payType.setVisibility(View.GONE);
        ll_payFinish.setVisibility(View.VISIBLE);
    }

    /**
     * 将流水上送的数据转成字串保存在打印的对象中
     * 不管成功失败，流水上送的数据保存下来
     *
     * @param request
     */
    private void setTransUpLoadData(TransUploadRequest request) {
        Gson gson = new Gson();
        String data = gson.toJson(request);
        ALog.json(data);
        printerData.setTransUploadData(data);
    }


    /**
     * 用来返回主线程 打印小票
     */
    private Handler mhandler = new Handler() {
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            Bitmap point_bitmap = bundle.getParcelable("point_bitmap");
            Bitmap title_bitmap = bundle.getParcelable("title_bitmap");
            printerData.setPoint_bitmap(point_bitmap);
            printerData.setCoupon_bitmap(title_bitmap);

            showLayout();

            // 打印
            Printer.getInstance(ZfPayPreauthActivity.this).print(printerData, ZfPayPreauthActivity.this);

        }


    };

    protected void setTransUpdateResponse(final TransUploadResponse data, final LoadingDialog dialog, boolean flag) {
        printerData.setPoint_url(data.getPoint_url());
        printerData.setPoint(data.getPoint());
        printerData.setPointCurrent(data.getPointCurrent());
        printerData.setCoupon(data.getCoupon_url());
        setCounponData(data.getCoupon());
        printerData.setBackAmt(data.getBackAmt());
        printerData.setApp_type(app_type);
        if (flag) {
            // 保存打印的数据，不保存图片数据
            PrinterDataSave();
        }

        //开启线程下载二维码图片
        new Thread(new Runnable() {

            @Override
            public void run() {

                Bitmap point_bitmap = null;
                Bitmap title_bitmap = null;
                if (!StringUtils.isEmpty(data.getPoint_url())) {
                    try {
                        point_bitmap = Glide.with(getApplicationContext())
                                .load(data.getPoint_url())
                                .asBitmap()
                                .centerCrop()
                                .into(200, 200).get();

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }

                if (!StringUtils.isEmpty(data.getCoupon_url())) {

                    try {
                        title_bitmap = Glide.with(getApplicationContext())
                                .load(data.getCoupon_url())
                                .asBitmap()
                                .centerCrop()
                                .into(200, 200).get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }


                dialog.dismiss();

                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putParcelable("point_bitmap", point_bitmap);
                bundle.putParcelable("title_bitmap", title_bitmap);
                msg.setData(bundle);
                mhandler.sendMessage(msg);

            }
        }).start();

    }

    private void setCounponData(List<Couponsn> data) {
        Gson gson = new GsonBuilder().serializeNulls().create();
        String counponStr = gson.toJson(data);
        printerData.setCouponData(counponStr);
    }


}
