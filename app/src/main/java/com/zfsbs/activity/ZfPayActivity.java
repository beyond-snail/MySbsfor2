package com.zfsbs.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.hd.core.HdAction;
import com.hd.model.HdAdjustScoreResponse;
import com.mycommonlib.core.PayCommon;
import com.mycommonlib.model.ComTransInfo;
import com.myokhttp.MyOkHttp;
import com.myokhttp.response.JsonResponseHandler;
import com.tool.utils.activityManager.AppManager;
import com.tool.utils.dialog.CommonDialog;
import com.tool.utils.dialog.LoadingDialog;
import com.tool.utils.utils.ALog;
import com.tool.utils.utils.EncryptMD5Util;
import com.tool.utils.utils.LogUtils;
import com.tool.utils.utils.SPUtils;
import com.tool.utils.utils.StringUtils;
import com.tool.utils.utils.ToastUtils;
import com.wosai.upay.bean.UpayResult;
import com.yzq.testzxing.zxing.android.CaptureActivity;
import com.zfsbs.R;
import com.zfsbs.common.CommonFunc;
import com.zfsbs.config.Config;
import com.zfsbs.config.Constants;
import com.zfsbs.core.action.BATPay;
import com.zfsbs.core.action.FyBat;
import com.zfsbs.core.action.Printer;
import com.zfsbs.core.action.RicherQb;
import com.zfsbs.core.action.ZfQbAction;
import com.zfsbs.core.myinterface.ActionCallbackListener;
import com.zfsbs.core.myinterface.BatInterface;
import com.zfsbs.model.ActivateApiResponse;
import com.zfsbs.model.FailureData;
import com.zfsbs.model.FyMicropayRequest;
import com.zfsbs.model.FyMicropayResponse;
import com.zfsbs.model.FyQueryRequest;
import com.zfsbs.model.FyQueryResponse;
import com.zfsbs.model.FyRefundResponse;
import com.zfsbs.model.LoginApiResponse;
import com.zfsbs.model.MemberTransAmountResponse;
import com.zfsbs.model.RicherGetMember;
import com.zfsbs.model.SbsPrinterData;
import com.zfsbs.model.TransUploadRequest;
import com.zfsbs.model.TransUploadResponse;
import com.zfsbs.model.ZfQbResponse;
import com.zfsbs.myapplication.MyApplication;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.zfsbs.common.CommonFunc.startAction;
import static com.zfsbs.common.CommonFunc.startResultAction;
import static com.zfsbs.config.Constants.PAY_FY_ALY;
import static com.zfsbs.config.Constants.PAY_FY_WX;
import static com.zfsbs.config.Constants.REQUEST_CAPTURE_ALY;
import static com.zfsbs.config.Constants.REQUEST_CAPTURE_QB;
import static com.zfsbs.config.Constants.REQUEST_CAPTURE_WX;
import static com.zfsbs.config.Constants.REQUEST_CASH;

public class ZfPayActivity extends BaseActivity implements OnClickListener {

    private static final String TAG = "ZfPayActivity";

    private TextView tOrderAmount;
    private TextView tPayAmount;
    private TextView tPayPointAmount;
    private TextView tPayCouponAmount;
    private LinearLayout btnPayflot;
    private LinearLayout btnCash;
    private LinearLayout btnAly;
    private LinearLayout btnWx;
    private LinearLayout btnQb;
    private Button btnPrint;
    private Button btnPrintfinish;
    private Button btnNopayAmount;
    private Button btnQuery;
    private Button btnQueryEnd;

    private LinearLayout ll_payType;
    private LinearLayout ll_payFinish;
    private LinearLayout ll_no_pay_amount;
    private LinearLayout ll_payQuery;

    private LinearLayout ll_pointAmount;
    private LinearLayout ll_couponAmount;

    //    private int amount;
    private int orderAmount;

    private BATPay bat;
    private FyBat fybat;
    private ZfQbAction qbpay;
//	private FyBat.FYPayResultEvent fyPayResultEvent;

    private MemberTransAmountResponse memberTransAmount;
    //    private MemberTransAmountResponse getMemberData;
    private String goundId;


    private String phone = ""; //手机号
//    private boolean isYxf = false; //是否是第三方
//	private boolean isYxfUpload = false; //是否上送流水

    private int app_type = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_type);
//        AppManager.getAppManager().addActivity(this);

        app_type = (int) SPUtils.get(this, Config.APP_TYPE, Config.DEFAULT_APP_TYPE);

        initView();
//        initData();
        getData();
        addListenster();

    }

    private void initView() {


        tOrderAmount = (TextView) findViewById(R.id.id_orderAmount);
        tPayAmount = (TextView) findViewById(R.id.id_payAmount);
        tPayPointAmount = (TextView) findViewById(R.id.id_pointAmount);
        tPayCouponAmount = (TextView) findViewById(R.id.id_coupon_amount);

        btnPayflot = (LinearLayout) findViewById(R.id.pay_flot);
        btnCash = (LinearLayout) findViewById(R.id.pay_cash);
        btnAly = (LinearLayout) findViewById(R.id.pay_aly);
        btnWx = (LinearLayout) findViewById(R.id.pay_wx);
        btnQb = (LinearLayout) findViewById(R.id.pay_qb);
        btnPrint = (Button) findViewById(R.id.id_print);
        btnPrintfinish = (Button) findViewById(R.id.id_finish);
        btnNopayAmount = (Button) findViewById(R.id.id_no_pay_amount);
        btnQuery = (Button) findViewById(R.id.id_query);
        btnQueryEnd = (Button) findViewById(R.id.id_terminal_query_sure);


        ll_payType = (LinearLayout) findViewById(R.id.ll_pay_type);
        ll_payFinish = (LinearLayout) findViewById(R.id.ll_pay_finish);
        ll_payQuery = (LinearLayout) findViewById(R.id.ll_pay_query);
        ll_no_pay_amount = (LinearLayout) findViewById(R.id.ll_no_pay_amount);

        ll_pointAmount = (LinearLayout) findViewById(R.id.id_ll_pointAmount);
        ll_couponAmount = (LinearLayout) findViewById(R.id.id_ll_coupon_amount);

        if (!Constants.isUsedQb || app_type == Config.APP_Richer_e) {
            btnQb.setVisibility(View.INVISIBLE);
            btnCash.setVisibility(View.INVISIBLE);
        }

        if (app_type == Config.APP_HD) {
            ll_pointAmount.setVisibility(View.INVISIBLE);
            ll_couponAmount.setVisibility(View.INVISIBLE);
            btnQb.setVisibility(View.INVISIBLE);
        }else if (app_type == Config.APP_YXF ){
            btnQb.setVisibility(View.INVISIBLE);
//            btnCash.setVisibility(View.INVISIBLE);
        }


    }


    private void getData() {
        MemberTransAmountResponse getMemberData = CommonFunc.recoveryMemberInfo(this);
        if (getMemberData != null) {
            tOrderAmount.setText(StringUtils.formatIntMoney(getMemberData.getTradeMoney()));
            tPayAmount.setText(StringUtils.formatIntMoney(getMemberData.getRealMoney()));
            tPayPointAmount.setText(StringUtils.formatIntMoney(getMemberData.getPointCoverMoney()));
            tPayCouponAmount.setText(StringUtils.formatIntMoney(getMemberData.getCouponCoverMoney()));
            if (getMemberData.getRealMoney() == 0) {
                ll_no_pay_amount.setVisibility(View.VISIBLE);
                ll_payType.setVisibility(View.GONE);
            }
        }


        bat = new BATPay(this);
        fybat = new FyBat(this, listener1);
        qbpay = new ZfQbAction(this);

        if (!StringUtils.isEmpty(CommonFunc.recoveryMemberInfo(ZfPayActivity.this).getMemberCardNo())) {
            printerData.setPhoneNo(CommonFunc.recoveryMemberInfo(ZfPayActivity.this).getMemberCardNo());
        }

        if (app_type == Config.APP_Richer_e) {
            btnQb.setVisibility(View.INVISIBLE);
        }
    }


    private FyBat.FYPayResultEvent listener1 = new FyBat.FYPayResultEvent() {
        @Override
        public void onSuccess(FyMicropayResponse data) {

            setFySmPay1(data);
        }

        @Override
        public void onSuccess(FyQueryResponse data) {
            //先判断本地数据是否存在，防止从华尔街平台拿到的是上一笔成功的交易
            SbsPrinterData datas = DataSupport.findLast(SbsPrinterData.class);
//            LogUtils.e("AuthCode",datas.getAuthCode());
//            LogUtils.e("getMchnt_order_no",data.getMchnt_order_no());
            if (!StringUtils.isEmpty(datas.getAuthCode()) && datas.getAuthCode().equals(data.getMchnt_order_no())) {
                ToastUtils.CustomShow(ZfPayActivity.this, "请确认消费者交易成功。");
                return;
            }
            setFySmPayQurey1(data);
        }

        @Override
        public void onSuccess(FyRefundResponse data) {

        }

        @Override
        public void onFailure(int statusCode, String error_msg, String type, String query_amount) {
            showLayoutEndQuery();
        }

        @Override
        public void onFailure(FyMicropayRequest data) {
            showLayoutEndQuery();
            if (data.getType().equals(Constants.PAY_FY_ALY)) {
                setFyPayFailureQuery(data.getOutOrderNum(), data.getAmount() + "", data.getType(), true, Constants.PAY_WAY_ALY, Constants.FY_FAILURE_PAY);
            } else if (data.getType().equals(Constants.PAY_FY_WX)) {
                setFyPayFailureQuery(data.getOutOrderNum(), data.getAmount() + "", data.getType(), true, Constants.PAY_WAY_WX, Constants.FY_FAILURE_PAY);
            }

        }

        @Override
        public void onFailure(FyQueryRequest data) {
            showLayoutEndQuery();
            if (data == null) {
                ToastUtils.CustomShow(ZfPayActivity.this, "请求数据为空，无法查询末笔");
                return;
            }
            if (data.getOrder_type().equals(Constants.PAY_FY_ALY)) {
                setFyQueryFailureQuery(data.getOutOrderNum(), data.getOrder_type(), data.getMchnt_order_no(), true, Constants.PAY_WAY_ALY, Constants.FY_FAILURE_QUERY);
            } else if (data.getOrder_type().equals(Constants.PAY_FY_WX)) {
                setFyQueryFailureQuery(data.getOutOrderNum(), data.getOrder_type(), data.getMchnt_order_no(), true, Constants.PAY_WAY_WX, Constants.FY_FAILURE_QUERY);
            }

        }


        @Override
        public void onLogin() {
            AppManager.getAppManager().finishAllActivity();
            if (Config.OPERATOR_UI_BEFORE) {
                CommonFunc.startAction(ZfPayActivity.this, OperatorLoginActivity.class, false);
            } else {
                CommonFunc.startAction(ZfPayActivity.this, OperatorLoginActivity1.class, false);
            }
        }
    };


    private void showLayoutEndQuery() {
        ll_payType.setVisibility(View.GONE);
        ll_payQuery.setVisibility(View.VISIBLE);
    }


    private void addListenster() {
        btnPayflot.setOnClickListener(this);
        btnCash.setOnClickListener(this);
        btnAly.setOnClickListener(this);
        btnWx.setOnClickListener(this);
        btnQb.setOnClickListener(this);
        btnPrint.setOnClickListener(this);
        btnPrintfinish.setOnClickListener(this);
        btnNopayAmount.setOnClickListener(this);
        btnQuery.setOnClickListener(this);
        btnQueryEnd.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        Bundle bundle = new Bundle();
//        bundle.putBoolean("yxf", isYxf);
//        startAction(this, InputAmountActivity.class, bundle, true);
        startAction(this, InputAmountActivity.class, true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_print:
                if (app_type == Config.APP_SBS) {
                    Gson gson = new Gson();
                    TransUploadRequest data = gson.fromJson(printerData.getTransUploadData(), TransUploadRequest.class);
                    LogUtils.e(data.toString());
                    getPrinterData(data);//(printerData.getRequest());
                } else if (app_type == Config.APP_YXF) {
                    Printer.print(printerData, ZfPayActivity.this);
                } else if (app_type == Config.APP_Richer_e) {
                    Printer.print(printerData, ZfPayActivity.this);
                } else if (app_type == Config.APP_HD) {
                    Printer.print(printerData, ZfPayActivity.this);
                }
                break;
            case R.id.id_finish:
            case R.id.id_terminal_query_sure: {
                startAction(this, InputAmountActivity.class, true);
            }
            break;
            case R.id.pay_flot:
                payflot1();

                break;
            case R.id.pay_cash: {
                Bundle bundle = new Bundle();
                bundle.putString("amount", tPayAmount.getText().toString());
                startResultAction(this, ZfPayCashActivity.class, bundle, REQUEST_CASH);
            }
            break;
            case R.id.pay_aly:
                payBat(Constants.PAY_WAY_ALY);
                break;
            case R.id.pay_wx:
                payBat(Constants.PAY_WAY_WX);
                break;
            case R.id.pay_qb:
                startResultAction(ZfPayActivity.this, CaptureActivity.class, null, REQUEST_CAPTURE_QB);
                break;
            case R.id.id_no_pay_amount:
                setNoPayAmount1();
                break;
            case R.id.id_query:
                setLastQuerySend1();

                break;
            default:
                break;
        }
    }


    private void setNoPayAmount1() {

        setCashPrintData1(0);

        if (app_type == Config.APP_SBS) {

            //设置流水上送参数
            TransUploadRequest request = CommonFunc.setTransUploadData(printerData, CommonFunc.recoveryMemberInfo(this),
                    CommonFunc.getNewClientSn(this, printerData.getPayType()), "", ""
            );

            //打印订单号与流水上送统一
            printerData.setClientOrderNo(request.getClientOrderNo());

            //流水上送
            transUploadAction1(request);
        } else if (app_type == Config.APP_Richer_e) {

            //设置流水上送参数
            TransUploadRequest request = CommonFunc.setTransUploadData(printerData, CommonFunc.recoveryMemberInfo(this),
                    CommonFunc.getNewClientSn(this, printerData.getPayType()), "", ""
            );

            //打印订单号与流水上送统一
            printerData.setClientOrderNo(request.getClientOrderNo());

            //流水上送
            Richer_transUploadAction(request);
        } else if (app_type == Config.APP_YXF) {
            if (!StringUtils.isEmpty(CommonFunc.recoveryMemberInfo(this).getMemberCardNo())) {
                printerData.setPhoneNo(CommonFunc.recoveryMemberInfo(this).getMemberCardNo());
                sendYxf(printerData);
            } else {
                printerData.setApp_type(app_type);
                printerData.setClientOrderNo(CommonFunc.getNewClientSn(ZfPayActivity.this, printerData.getPayType()));
                PrinterDataSave();
                Printer.getInstance(ZfPayActivity.this).print(printerData, ZfPayActivity.this);
                showLayout();
            }
        } else if (app_type == Config.APP_HD) {

            boolean isMember = (boolean) SPUtils.get(this, Config.isHdMember, false);
            if (isMember) {
                //设置流水上送参数
                TransUploadRequest request = CommonFunc.setTransUploadData(printerData, CommonFunc.recoveryMemberInfo(this),
                        CommonFunc.getNewClientSn(this, printerData.getPayType()), "", ""
                );

                //打印订单号与流水上送统一
                printerData.setClientOrderNo(request.getClientOrderNo());
                printerData.setMember(isMember);
                //流水上送
                transUploadAction2(request);
            }else {
                printerData.setApp_type(app_type);
                printerData.setMember(isMember);
                printerData.setClientOrderNo(CommonFunc.getNewClientSn(ZfPayActivity.this, printerData.getPayType()));
                PrinterDataSave();
                Printer.getInstance(ZfPayActivity.this).print(printerData, ZfPayActivity.this);
                showLayout();
            }
        }

    }


    private void setLastQuerySend1() {

        switch (CommonFunc.recoveryFailureInfo(this).getPay_type()) {
            case Constants.PAY_WAY_QB:
                ZfQbQuery();
                break;
            case Constants.PAY_WAY_ALY:
            case Constants.PAY_WAY_WX:
                if (CommonFunc.recoveryFailureInfo(this).getFaiureType() == Constants.FY_FAILURE_PAY) {
                    ZfFyPayQuery();
                } else if (CommonFunc.recoveryFailureInfo(this).getFaiureType() == Constants.FY_FAILURE_QUERY) {
                    ZfFyQuery();
                }
                break;

        }

    }


    private void payBat(int type) {
        String sm_type = MyApplication.getInstance().getLoginData().getScanPayType();

        //富友扫码
        if (!StringUtils.isEmpty(sm_type) && StringUtils.isEquals(sm_type, Constants.SM_TYPE_FY)) {

            switch (type) {
                case Constants.PAY_WAY_ALY:
                    startResultAction(ZfPayActivity.this, CaptureActivity.class, null, REQUEST_CAPTURE_ALY);
                    break;
                case Constants.PAY_WAY_WX:
                    startResultAction(ZfPayActivity.this, CaptureActivity.class, null, REQUEST_CAPTURE_WX);
                    break;
            }

            return;
        }

        //收钱吧
        boolean flag = MyApplication.getInstance().getLoginData().isActive();
        if (!flag) {
            bat.activite(new BatInterface() {

                @Override
                public void success_bat(UpayResult result) {
                    MyApplication.getInstance().getLoginData().setActive(true);
                    // 更新到数据库
                    ContentValues values = new ContentValues();
                    values.put("isActive", true);
                    DataSupport.update(LoginApiResponse.class, values,
                            MyApplication.getInstance().getLoginData().getId());
                    activateAction(1);
                }

                @Override
                public void failed_bat(String error_code, String error_msg) {
                    ToastUtils.CustomShow(ZfPayActivity.this, error_code + "#" + error_msg);
                    // 保存激活状态
                    MyApplication.getInstance().getLoginData().setActive(false);
                    MyApplication.getInstance().getLoginData().setActiveCode("");
                    // 更新到数据库
                    ContentValues values = new ContentValues();
                    values.put("isActive", false);
                    values.put("activeCode", "");
                    DataSupport.update(LoginApiResponse.class, values,
                            MyApplication.getInstance().getLoginData().getId());
                    activateAction(0);
                }

                @Override
                public void onLogin() {

                }
            });
            return;
        }
        // 设置订单号
        bat.setMyOrderId(CommonFunc.getNewClientSn(this, type));
        bat.pay(type, CommonFunc.recoveryMemberInfo(this).getRealMoney(), new BatInterface() {

            @Override
            public void success_bat(UpayResult result) {
                paySqb1(result);
            }

            @Override
            public void failed_bat(String error_code, String error_msg) {
                ToastUtils.CustomShowLong(ZfPayActivity.this, error_code + "#" + error_msg);
                //支付失败直接跳出
                startAction(ZfPayActivity.this, InputAmountActivity.class, true);
            }

            @Override
            public void onLogin() {
                ToastUtils.CustomShow(ZfPayActivity.this, "登录失效，请重新登录。。。");
                AppManager.getAppManager().finishAllActivity();
                if (Config.OPERATOR_UI_BEFORE) {
                    CommonFunc.startAction(ZfPayActivity.this, OperatorLoginActivity.class, false);
                } else {
                    CommonFunc.startAction(ZfPayActivity.this, OperatorLoginActivity1.class, false);
                }
            }
        });
    }


    private void paySqb1(UpayResult result) {

        setBatPrintData1(result);

        TransUploadRequest request = CommonFunc.setTransUploadData(printerData,
                CommonFunc.recoveryMemberInfo(ZfPayActivity.this),
                bat.getMyOrderId(),
                printerData.getTransNo(), printerData.getAuthCode());
        if (app_type == Config.APP_Richer_e) {

            Richer_transUploadAction(request);
        } else {
            transUploadAction1(request);
        }


    }


    /**
     * 刷卡
     */
    private void payflot1() {

        String mid = MyApplication.getInstance().getLoginData().getMerchantNo();
        String tid = MyApplication.getInstance().getLoginData().getTerminalNo();
        PayCommon.sale(this, CommonFunc.recoveryMemberInfo(this).getRealMoney(), mid, tid, new PayCommon.ComTransResult<ComTransInfo>() {
            @Override
            public void success(ComTransInfo transInfo) {
                //设置打印的信息
                setFlotPrintData1(transInfo);

                //判断是否是商博士
                if (app_type == Config.APP_SBS) {

                    //设置流水上送需要上送的参数
                    TransUploadRequest request = CommonFunc.setTransUploadData(printerData,
                            CommonFunc.recoveryMemberInfo(ZfPayActivity.this),
                            CommonFunc.getNewClientSn(ZfPayActivity.this, printerData.getPayType()),
                            printerData.getVoucherNo(), printerData.getReferNo());

                    //打印的订单号与流水上送的统一
                    printerData.setClientOrderNo(request.getClientOrderNo());

                    //流水上送
                    transUploadAction1(request);
                } else if (app_type == Config.APP_Richer_e) {
                    //设置流水上送需要上送的参数
                    TransUploadRequest request = CommonFunc.setTransUploadData(printerData,
                            CommonFunc.recoveryMemberInfo(ZfPayActivity.this),
                            CommonFunc.getNewClientSn(ZfPayActivity.this, printerData.getPayType()),
                            printerData.getVoucherNo(), printerData.getReferNo());

                    //打印的订单号与流水上送的统一
                    printerData.setClientOrderNo(request.getClientOrderNo());

                    Richer_transUploadAction(request);
                } else if (app_type == Config.APP_YXF) {
                    if (!StringUtils.isEmpty(CommonFunc.recoveryMemberInfo(ZfPayActivity.this).getMemberCardNo())) {
                        printerData.setPhoneNo(CommonFunc.recoveryMemberInfo(ZfPayActivity.this).getMemberCardNo());
//                        printerData.setYxf(true);
                        sendYxf(printerData);
                    } else {
                        printerData.setApp_type(app_type);
                        printerData.setClientOrderNo(CommonFunc.getNewClientSn(ZfPayActivity.this, printerData.getPayType()));
                        PrinterDataSave();
                        Printer.getInstance(ZfPayActivity.this).print(printerData, ZfPayActivity.this);
                        showLayout();
                    }
                } else if (app_type == Config.APP_HD) {
                    boolean isMember = (boolean) SPUtils.get(ZfPayActivity.this, Config.isHdMember, false);
                    if (isMember) {
                        //设置流水上送需要上送的参数
                        TransUploadRequest request = CommonFunc.setTransUploadData(printerData,
                                CommonFunc.recoveryMemberInfo(ZfPayActivity.this),
                                CommonFunc.getNewClientSn(ZfPayActivity.this, printerData.getPayType()),
                                printerData.getVoucherNo(), printerData.getReferNo());
                        printerData.setMember(isMember);
                        //打印的订单号与流水上送的统一
                        printerData.setClientOrderNo(request.getClientOrderNo());

                        //流水上送
                        transUploadAction2(request);
                    }else {
                        printerData.setApp_type(app_type);
                        printerData.setMember(isMember);
                        printerData.setClientOrderNo(CommonFunc.getNewClientSn(ZfPayActivity.this, printerData.getPayType()));
                        PrinterDataSave();
                        Printer.getInstance(ZfPayActivity.this).print(printerData, ZfPayActivity.this);
                        showLayout();
                    }
                }
            }

            @Override
            public void failed(String error) {
//                ToastUtils.CustomShow(ZfPayActivity.this, error);
                final CommonDialog confirmDialog = new CommonDialog(ZfPayActivity.this, error);
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
     * 现金
     *
     * @param oddChangeAmout
     */
    private void payCash1(int oddChangeAmout) {

        //设置打印信息
        setCashPrintData1(oddChangeAmout);

        if (app_type == Config.APP_SBS) {
            //设置流水上送参数
            TransUploadRequest request = CommonFunc.setTransUploadData(printerData, CommonFunc.recoveryMemberInfo(this),
                    CommonFunc.getNewClientSn(this, printerData.getPayType()), "", ""
            );

            //打印订单号与流水上送统一
            printerData.setClientOrderNo(request.getClientOrderNo());

            //流水上送
            transUploadAction1(request);
        } else if (app_type == Config.APP_Richer_e) {
            //设置流水上送参数
            TransUploadRequest request = CommonFunc.setTransUploadData(printerData, CommonFunc.recoveryMemberInfo(this),
                    CommonFunc.getNewClientSn(this, printerData.getPayType()), "", ""
            );

            //打印订单号与流水上送统一
            printerData.setClientOrderNo(request.getClientOrderNo());

            Richer_transUploadAction(request);
        } else if (app_type == Config.APP_YXF) {
            if (!StringUtils.isEmpty(CommonFunc.recoveryMemberInfo(ZfPayActivity.this).getMemberCardNo())) {
                printerData.setPhoneNo(CommonFunc.recoveryMemberInfo(ZfPayActivity.this).getMemberCardNo());
                sendYxf(printerData);
            } else {
                printerData.setApp_type(app_type);
                printerData.setClientOrderNo(CommonFunc.getNewClientSn(ZfPayActivity.this, printerData.getPayType()));
                PrinterDataSave();
                Printer.getInstance(ZfPayActivity.this).print(printerData, ZfPayActivity.this);
                showLayout();
            }
        } else if (app_type == Config.APP_HD) {
            boolean isMember = (boolean) SPUtils.get(this, Config.isHdMember, false);
            if (isMember) {
                //设置流水上送参数
                TransUploadRequest request = CommonFunc.setTransUploadData(printerData, CommonFunc.recoveryMemberInfo(this),
                        CommonFunc.getNewClientSn(this, printerData.getPayType()), "", ""
                );

                //打印订单号与流水上送统一
                printerData.setClientOrderNo(request.getClientOrderNo());
                printerData.setMember(isMember);
                //流水上送
                transUploadAction2(request);
            }else {
                printerData.setApp_type(app_type);
                printerData.setMember(isMember);
                printerData.setClientOrderNo(CommonFunc.getNewClientSn(this, printerData.getPayType()));
                PrinterDataSave();
                Printer.getInstance(ZfPayActivity.this).print(printerData, ZfPayActivity.this);
                showLayout();
            }
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_CASH:
                int receiveAmount = data.getBundleExtra("bundle").getInt("receiveAmount");
                int oddChangeAmout = data.getBundleExtra("bundle").getInt("oddChangeAmout");

                payCash1(oddChangeAmout);

                break;
            case REQUEST_CAPTURE_WX:
                String result_wx = data.getExtras().getString(CaptureActivity.SCAN_RESULT);
                LogUtils.e("result", result_wx);
                FyWxPay1(result_wx);
                break;
            case REQUEST_CAPTURE_ALY:
                String result_aly = data.getExtras().getString(CaptureActivity.SCAN_RESULT);
                LogUtils.e("result", result_aly);
                FyAlyPay1(result_aly);
                break;
            case REQUEST_CAPTURE_QB:
                String result_qb = data.getExtras().getString(CaptureActivity.SCAN_RESULT);
                LogUtils.e("result", result_qb);
                ZfQbPay1(result_qb);

                break;
            default:
                break;
        }
    }


    private void FyWxPay1(String code) {
        printerData.setPayType(Constants.PAY_WAY_WX);
        printerData.setClientOrderNo(CommonFunc.getNewClientSn(this, printerData.getPayType()));
        fybat.pay1(code, PAY_FY_WX, printerData.getClientOrderNo(), CommonFunc.recoveryMemberInfo(this).getRealMoney());
    }


    private void FyAlyPay1(String code) {
        printerData.setPayType(Constants.PAY_WAY_ALY);
        printerData.setClientOrderNo(CommonFunc.getNewClientSn(this, printerData.getPayType()));
        fybat.pay1(code, PAY_FY_ALY, printerData.getClientOrderNo(), CommonFunc.recoveryMemberInfo(this).getRealMoney());
    }


    private void ZfQbPay1(String result_qb) {

        if (StringUtils.isEmpty(result_qb)) {
            ToastUtils.CustomShow(this, "获取扫码信息为空");
            return;
        }
        int sid = MyApplication.getInstance().getLoginData().getSid();
        final String orderNo = CommonFunc.getNewClientSn(this, Constants.PAY_WAY_QB);
        final String time = StringUtils.getFormatCurTime();
        final String traceNum = StringUtils.getFormatCurTime() + StringUtils.createRandomNumStr(5);
        this.qbpay.qbAction1(sid, orderNo, CommonFunc.recoveryMemberInfo(this).getRealMoney() + "", time, traceNum, result_qb, new ActionCallbackListener<ZfQbResponse>() {
            @Override
            public void onSuccess(ZfQbResponse data) {
                //流水上送
                setQbPay1(data, orderNo, time, traceNum);
            }

            @Override
            public void onFailure(String errorEvent, String message) {
                ToastUtils.CustomShow(ZfPayActivity.this, errorEvent + "#" + message);
            }

            @Override
            public void onFailurTimeOut(String s, String error_msg) {
                ToastUtils.CustomShow(ZfPayActivity.this, s + "#" + error_msg);
                showLayoutEndQuery();
                //设置末笔查询数据
                setQbFailureQuery(orderNo, time, traceNum, Constants.PAY_WAY_QB);
            }

            @Override
            public void onLogin() {
                AppManager.getAppManager().finishAllActivity();
                if (Config.OPERATOR_UI_BEFORE) {
                    CommonFunc.startAction(ZfPayActivity.this, OperatorLoginActivity.class, false);
                } else {
                    CommonFunc.startAction(ZfPayActivity.this, OperatorLoginActivity1.class, false);
                }
            }
        });

    }


    /**
     * 钱包末笔查询
     */
    private void ZfQbQuery() {
        CommonFunc.ZfQbFailQuery(this, new ActionCallbackListener<ZfQbResponse>() {
            @Override
            public void onSuccess(ZfQbResponse data) {

                FailureData failureData = CommonFunc.recoveryFailureInfo(ZfPayActivity.this);
                //流水上送
                setQbPay1(data, failureData.getOrderNo(),
                        failureData.getTime(), failureData.getTraceNum());
            }

            @Override
            public void onFailure(String errorEvent, String message) {
                ToastUtils.CustomShow(ZfPayActivity.this, errorEvent + "#" + message);
            }

            @Override
            public void onFailurTimeOut(String s, String error_msg) {
                ToastUtils.CustomShow(ZfPayActivity.this, s + "#" + error_msg);
            }

            @Override
            public void onLogin() {
                AppManager.getAppManager().finishAllActivity();
                if (Config.OPERATOR_UI_BEFORE) {
                    CommonFunc.startAction(ZfPayActivity.this, OperatorLoginActivity.class, false);
                } else {
                    CommonFunc.startAction(ZfPayActivity.this, OperatorLoginActivity1.class, false);
                }
            }
        });
    }


    /**
     * 富友扫码支付异常处理
     */
    private void ZfFyPayQuery() {
        fybat.terminalQuery1(CommonFunc.recoveryFailureInfo(this).getOrder_type(), CommonFunc.recoveryFailureInfo(this).getAmount(), true,
                CommonFunc.recoveryFailureInfo(this).getOutOrderNo());
    }

    /**
     * 富友扫码查询异常处理
     */
    private void ZfFyQuery() {

        fybat.query1(this, CommonFunc.recoveryFailureInfo(this).getOrder_type(), CommonFunc.recoveryFailureInfo(this).getOrderNo(),
                CommonFunc.recoveryFailureInfo(this).getOutOrderNo());
    }


    /**
     * 设置钱包异常查询
     *
     * @param orderNo
     * @param time
     * @param payWayQb
     */
    private void setQbFailureQuery(String orderNo, String time, String traceNum, int payWayQb) {
        FailureData data = new FailureData();
        data.setPay_type(payWayQb);
        data.setOrderNo(orderNo);
        data.setTime(time);
        data.setTraceNum(traceNum);
        data.setStatus(true);
        CommonFunc.setBackFailureInfo(this, data);
    }

    /**
     * 设置富友SM异常查询
     *
     * @param amount
     * @param type
     * @param isStatus
     */
    private void setFyPayFailureQuery(String outOrderNum, String amount, String type, boolean isStatus, int payWay, int failureType) {

        boolean isMember = (boolean) SPUtils.get(this, Config.isHdMember, false);

        FailureData data = new FailureData();
        data.setOutOrderNo(outOrderNum);
        data.setAmount(amount);
        data.setOrder_type(type);
        data.setStatus(isStatus);
        data.setPay_type(payWay);
        data.setFaiureType(failureType);
        data.setApp_type(app_type);
        data.setMember(isMember);
        CommonFunc.setBackFailureInfo(this, data);

    }

    /**
     * 设置富友查询异常查询数据
     *
     * @param type
     * @param order_no
     * @param isStatus
     * @param payWay
     */
    private void setFyQueryFailureQuery(String outOrderNum, String type, String order_no, boolean isStatus, int payWay, int failureType) {
        boolean isMember = (boolean) SPUtils.get(this, Config.isHdMember, false);
        FailureData data = new FailureData();
        data.setOutOrderNo(outOrderNum);
        data.setOrder_type(type);
        data.setStatus(isStatus);
        data.setPay_type(payWay);
        data.setOrderNo(order_no);
        data.setFaiureType(failureType);
        data.setApp_type(app_type);
        data.setMember(isMember);
        CommonFunc.setBackFailureInfo(this, data);
    }

    /**
     * 设置收钱吧查询异常查询数据
     *
     * @param type
     * @param order_no
     * @param isStatus
     * @param payWay
     */
    private void setSqbFailureQuery(String outOrderNum, String type, String order_no, boolean isStatus, int payWay, int failureType) {
        FailureData data = new FailureData();
        data.setOutOrderNo(outOrderNum);
        data.setOrder_type(type);
        data.setStatus(isStatus);
        data.setPay_type(payWay);
        data.setOrderNo(order_no);
        data.setFaiureType(failureType);
        data.setApp_type(app_type);
        CommonFunc.setBackFailureInfo(this, data);
    }


    private void setQbPay1(ZfQbResponse data, String orderNo, String time, String traceNum) {
        printerData.setMerchantName(MyApplication.getInstance().getLoginData().getTerminalName());
        printerData.setMerchantNo(data.getGroupId());
        printerData.setTerminalId(StringUtils.getSerial());
        printerData.setOperatorNo((String) SPUtils.get(this, Constants.USER_NAME, ""));
        printerData.setDateTime(time);
        printerData.setClientOrderNo(orderNo);
        printerData.setTransNo(traceNum);
        printerData.setAuthCode(data.getSystemOrderNo());
        printerData.setDateTime(StringUtils.formatTime(time));
        printerData.setOrderAmount(CommonFunc.recoveryMemberInfo(this).getTradeMoney());
        printerData.setAmount(StringUtils.formatIntMoney(CommonFunc.recoveryMemberInfo(this).getRealMoney()));
        printerData.setPointCoverMoney(CommonFunc.recoveryMemberInfo(this).getPointCoverMoney());
        printerData.setCouponCoverMoney(CommonFunc.recoveryMemberInfo(this).getCouponCoverMoney());
        printerData.setPayType(Constants.PAY_WAY_QB);

        if (app_type == Config.APP_SBS) {
            TransUploadRequest request = CommonFunc.setTransUploadData(printerData, CommonFunc.recoveryMemberInfo(this),
                    CommonFunc.getNewClientSn(this, printerData.getPayType()), printerData.getTransNo(), printerData.getAuthCode()
            );
            //这个地方保持和支付的时候一直
            request.setClientOrderNo(orderNo);
            transUploadAction1(request);
        } else if (app_type == Config.APP_Richer_e) {
            TransUploadRequest request = CommonFunc.setTransUploadData(printerData, CommonFunc.recoveryMemberInfo(this),
                    CommonFunc.getNewClientSn(this, printerData.getPayType()), printerData.getTransNo(), printerData.getAuthCode()
            );
            printerData.setClientOrderNo(request.getClientOrderNo());

            Richer_transUploadAction(request);
        } else if (app_type == Config.APP_YXF) {

            if (!StringUtils.isEmpty(CommonFunc.recoveryMemberInfo(ZfPayActivity.this).getMemberCardNo())) {
                printerData.setPhoneNo(CommonFunc.recoveryMemberInfo(ZfPayActivity.this).getMemberCardNo());
                sendYxf(printerData);
            } else {
                printerData.setApp_type(app_type);
                printerData.setClientOrderNo(CommonFunc.getNewClientSn(ZfPayActivity.this, printerData.getPayType()));
                PrinterDataSave();
                Printer.getInstance(ZfPayActivity.this).print(printerData, ZfPayActivity.this);
                showLayout();
            }

        } else if (app_type == Config.APP_HD) {
            boolean isMember = (boolean) SPUtils.get(this, Config.isHdMember, false);
            if (isMember) {
                TransUploadRequest request = CommonFunc.setTransUploadData(printerData, CommonFunc.recoveryMemberInfo(this),
                        CommonFunc.getNewClientSn(this, printerData.getPayType()), printerData.getTransNo(), printerData.getAuthCode()
                );
                //这个地方保持和支付的时候一直
                request.setClientOrderNo(orderNo);
                printerData.setMember(isMember);
                transUploadAction2(request);
            }else {
                printerData.setApp_type(app_type);
                printerData.setMember(isMember);
                printerData.setClientOrderNo(CommonFunc.getNewClientSn(ZfPayActivity.this, printerData.getPayType()));
                PrinterDataSave();
                Printer.getInstance(ZfPayActivity.this).print(printerData, ZfPayActivity.this);
                showLayout();
            }
        }


    }


    private void setCashPrintData1(int oddChangeAmout) {
        printerData.setMerchantName(MyApplication.getInstance().getLoginData().getTerminalName());
        printerData.setMerchantNo(MyApplication.getInstance().getLoginData().getMerchantNo());
        printerData.setTerminalId(StringUtils.getSerial());
        printerData.setOperatorNo((String) SPUtils.get(this, Constants.USER_NAME, ""));
        printerData.setDateTime(StringUtils.getCurTime());
        printerData.setAmount(StringUtils.formatIntMoney(CommonFunc.recoveryMemberInfo(this).getTradeMoney()));
        printerData.setReceiveAmount(StringUtils.formatIntMoney(CommonFunc.recoveryMemberInfo(this).getRealMoney()));
        printerData.setOddChangeAmout(StringUtils.formatIntMoney(oddChangeAmout));
        printerData.setPointCoverMoney(CommonFunc.recoveryMemberInfo(this).getPointCoverMoney());
        printerData.setCouponCoverMoney(CommonFunc.recoveryMemberInfo(this).getCouponCoverMoney());
        printerData.setPayType(Constants.PAY_WAY_CASH);

    }


    protected void setFlotPrintData1(ComTransInfo transInfo) {
        printerData.setMerchantName(MyApplication.getInstance().getLoginData().getTerminalName());
        printerData.setMerchantNo(MyApplication.getInstance().getLoginData().getMerchantNo());//(transInfo.getMid());
        printerData.setTerminalId(transInfo.getTid());
        printerData.setOperatorNo((String) SPUtils.get(this, Constants.USER_NAME, ""));
        printerData.setAcquirer(transInfo.getAcquirerCode());
        printerData.setIssuer(transInfo.getIssuerCode());
        printerData.setCardNo(StringUtils.formatCardNo(transInfo.getPan()));
        printerData.setTransType(transInfo.getTransType() + "");
        printerData.setExpDate(transInfo.getExpiryDate());
        printerData.setBatchNO(StringUtils.fillZero(transInfo.getBatchNumber() + "", 6));
        printerData.setVoucherNo(StringUtils.fillZero(transInfo.getTrace() + "", 6));
        printerData.setDateTime(
                StringUtils.formatTime(StringUtils.getCurYear() + transInfo.getTransDate() + transInfo.getTransTime()));
        printerData.setAuthNo(transInfo.getAuthCode());
        printerData.setReferNo(transInfo.getRrn());
        printerData.setPointCoverMoney(CommonFunc.recoveryMemberInfo(this).getPointCoverMoney());
        printerData.setCouponCoverMoney(CommonFunc.recoveryMemberInfo(this).getCouponCoverMoney());
        printerData.setOrderAmount(CommonFunc.recoveryMemberInfo(this).getTradeMoney());
        printerData.setAmount(StringUtils.formatIntMoney(transInfo.getTransAmount()));
        printerData.setPayType(Constants.PAY_WAY_FLOT);
    }


    protected void setBatPrintData1(UpayResult result) {
        printerData.setMerchantName(MyApplication.getInstance().getLoginData().getActivateCodemerchantName());
        printerData.setMerchantNo(MyApplication.getInstance().getLoginData().getActivateCodeMerchantNo());
        printerData.setTerminalId(MyApplication.getInstance().getLoginData().getActiveCode());
        printerData.setOperatorNo((String) SPUtils.get(this, Constants.USER_NAME, ""));
        printerData.setClientOrderNo(result.getClient_sn());
        printerData.setTransNo(result.getTrade_no());
        printerData.setAuthCode(result.getSn());
        printerData.setDateTime(StringUtils.getCurTime());
        printerData.setOrderAmount(CommonFunc.recoveryMemberInfo(this).getTradeMoney());
        printerData.setAmount(StringUtils.formatStrMoney(result.getNet_amount()));
        printerData.setPointCoverMoney(CommonFunc.recoveryMemberInfo(this).getPointCoverMoney());
        printerData.setCouponCoverMoney(CommonFunc.recoveryMemberInfo(this).getCouponCoverMoney());
        printerData.setScanPayType(MyApplication.getInstance().getLoginData().getScanPayType());
        if (result.getPayway().equals(Constants.PAY_WAY_ALY + "")) {
            printerData.setPayType(Constants.PAY_WAY_ALY);
        } else if (result.getPayway().equals(Constants.PAY_WAY_WX + "")) {
            printerData.setPayType(Constants.PAY_WAY_WX);
        } else if (result.getPayway().equals(Constants.PAY_WAY_BFB + "")) {
            printerData.setPayType(Constants.PAY_WAY_BFB);
        } else if (result.getPayway().equals(Constants.PAY_WAY_JD + "")) {
            printerData.setPayType(Constants.PAY_WAY_JD);
        }
    }


    private void setFySmPay1(FyMicropayResponse data) {
        printerData.setMerchantName(MyApplication.getInstance().getLoginData().getFyMerchantName());
        printerData.setMerchantNo(MyApplication.getInstance().getLoginData().getFyMerchantNo());
        printerData.setTerminalId(StringUtils.getTerminalNo(StringUtils.getSerial()));
        printerData.setOperatorNo((String) SPUtils.get(this, Constants.USER_NAME, ""));
        printerData.setTransNo(data.getTransaction_id());
        printerData.setAuthCode(data.getMchnt_order_no());
        printerData.setDateTime(StringUtils.formatTime(data.getTxn_begin_ts()));
        printerData.setOrderAmount(CommonFunc.recoveryMemberInfo(this).getTradeMoney());
        printerData.setAmount(StringUtils.formatStrMoney(data.getTotal_amount()));
        printerData.setPointCoverMoney(CommonFunc.recoveryMemberInfo(this).getPointCoverMoney());
        printerData.setCouponCoverMoney(CommonFunc.recoveryMemberInfo(this).getCouponCoverMoney());
        printerData.setScanPayType(MyApplication.getInstance().getLoginData().getScanPayType());
        if (data.getOrder_type().equals(Constants.PAY_FY_ALY)) {
            printerData.setPayType(Constants.PAY_WAY_ALY);
        } else if (data.getOrder_type().equals(Constants.PAY_FY_WX)) {
            printerData.setPayType(Constants.PAY_WAY_WX);
        }

        if (app_type == Config.APP_SBS) {
            TransUploadRequest request = CommonFunc.setTransUploadData(printerData, CommonFunc.recoveryMemberInfo(this),
                    CommonFunc.getNewClientSn(this, printerData.getPayType()), printerData.getTransNo(), printerData.getAuthCode()
            );
            printerData.setClientOrderNo(request.getClientOrderNo());
            transUploadAction1(request);
        } else if (app_type == Config.APP_Richer_e) {
            TransUploadRequest request = CommonFunc.setTransUploadData(printerData, CommonFunc.recoveryMemberInfo(this),
                    CommonFunc.getNewClientSn(this, printerData.getPayType()), printerData.getTransNo(), printerData.getAuthCode()
            );
            printerData.setClientOrderNo(request.getClientOrderNo());

            Richer_transUploadAction(request);
        } else if (app_type == Config.APP_YXF) {

            if (!StringUtils.isEmpty(CommonFunc.recoveryMemberInfo(ZfPayActivity.this).getMemberCardNo())) {
                printerData.setPhoneNo(CommonFunc.recoveryMemberInfo(ZfPayActivity.this).getMemberCardNo());
                sendYxf(printerData);
            } else {
                printerData.setApp_type(app_type);
                printerData.setClientOrderNo(CommonFunc.getNewClientSn(ZfPayActivity.this, printerData.getPayType()));
                PrinterDataSave();
                Printer.getInstance(ZfPayActivity.this).print(printerData, ZfPayActivity.this);
                showLayout();
            }

        } else if (app_type == Config.APP_HD) {
            boolean isMember = (boolean) SPUtils.get(this, Config.isHdMember, false);
            if (isMember) {
                TransUploadRequest request = CommonFunc.setTransUploadData(printerData, CommonFunc.recoveryMemberInfo(this),
                        CommonFunc.getNewClientSn(this, printerData.getPayType()), printerData.getTransNo(), printerData.getAuthCode()
                );
                printerData.setClientOrderNo(request.getClientOrderNo());
                printerData.setMember(isMember);
                transUploadAction2(request);
            }else {
                printerData.setApp_type(app_type);
                printerData.setMember(isMember);
                printerData.setClientOrderNo(CommonFunc.getNewClientSn(ZfPayActivity.this, printerData.getPayType()));
                PrinterDataSave();
                Printer.getInstance(ZfPayActivity.this).print(printerData, ZfPayActivity.this);
                showLayout();
            }
        }

    }


    private void setFySmPayQurey1(FyQueryResponse data) {
        printerData.setMerchantName(MyApplication.getInstance().getLoginData().getFyMerchantName());
        printerData.setMerchantNo(MyApplication.getInstance().getLoginData().getFyMerchantNo());
        printerData.setTerminalId(StringUtils.getTerminalNo(StringUtils.getSerial()));
        printerData.setOperatorNo((String) SPUtils.get(this, Constants.USER_NAME, ""));
        printerData.setTransNo(data.getTransaction_id());
        printerData.setAuthCode(data.getMchnt_order_no());
        printerData.setDateTime(StringUtils.getCurTime());
        printerData.setOrderAmount(CommonFunc.recoveryMemberInfo(this).getTradeMoney());
        printerData.setAmount(StringUtils.formatStrMoney(data.getOrder_amt()));
        printerData.setPointCoverMoney(CommonFunc.recoveryMemberInfo(this).getPointCoverMoney());
        printerData.setCouponCoverMoney(CommonFunc.recoveryMemberInfo(this).getCouponCoverMoney());
        printerData.setScanPayType(MyApplication.getInstance().getLoginData().getScanPayType());
        if (data.getOrder_type().equals(Constants.PAY_FY_ALY)) {
            printerData.setPayType(Constants.PAY_WAY_ALY);
        } else if (data.getOrder_type().equals(Constants.PAY_FY_WX)) {
            printerData.setPayType(Constants.PAY_WAY_WX);
        }

        if (app_type == Config.APP_SBS) {
            TransUploadRequest request = CommonFunc.setTransUploadData(printerData, CommonFunc.recoveryMemberInfo(this),
                    CommonFunc.getNewClientSn(this, printerData.getPayType()), printerData.getTransNo(), printerData.getAuthCode()
            );
            printerData.setClientOrderNo(request.getClientOrderNo());
            transUploadAction1(request);
        } else if (app_type == Config.APP_Richer_e) {
            TransUploadRequest request = CommonFunc.setTransUploadData(printerData, CommonFunc.recoveryMemberInfo(this),
                    CommonFunc.getNewClientSn(this, printerData.getPayType()), printerData.getTransNo(), printerData.getAuthCode()
            );
            printerData.setClientOrderNo(request.getClientOrderNo());

            Richer_transUploadAction(request);
        } else if (app_type == Config.APP_YXF) {
            if (!StringUtils.isEmpty(CommonFunc.recoveryMemberInfo(ZfPayActivity.this).getMemberCardNo())) {
                printerData.setPhoneNo(CommonFunc.recoveryMemberInfo(ZfPayActivity.this).getMemberCardNo());
                sendYxf(printerData);
            } else {
                printerData.setApp_type(app_type);
                printerData.setClientOrderNo(CommonFunc.getNewClientSn(ZfPayActivity.this, printerData.getPayType()));
                PrinterDataSave();
                Printer.getInstance(ZfPayActivity.this).print(printerData, ZfPayActivity.this);
                showLayout();
            }

        } else if (app_type == Config.APP_HD) {
            boolean isMember = (boolean) SPUtils.get(this, Config.isHdMember, false);
            if (isMember) {
                TransUploadRequest request = CommonFunc.setTransUploadData(printerData, CommonFunc.recoveryMemberInfo(this),
                        CommonFunc.getNewClientSn(this, printerData.getPayType()), printerData.getTransNo(), printerData.getAuthCode()
                );
                printerData.setClientOrderNo(request.getClientOrderNo());
                printerData.setMember(isMember);
                transUploadAction2(request);
            }else {
                printerData.setApp_type(app_type);
                printerData.setMember(isMember);
                printerData.setClientOrderNo(CommonFunc.getNewClientSn(ZfPayActivity.this, printerData.getPayType()));
                PrinterDataSave();
                Printer.getInstance(ZfPayActivity.this).print(printerData, ZfPayActivity.this);
                showLayout();
            }
        }

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
                ToastUtils.CustomShow(ZfPayActivity.this, errorEvent + "#" + message);
            }

            @Override
            public void onFailurTimeOut(String s, String error_msg) {

            }

            @Override
            public void onLogin() {
                dialog.dismiss();
                AppManager.getAppManager().finishAllActivity();
                if (Config.OPERATOR_UI_BEFORE) {
                    CommonFunc.startAction(ZfPayActivity.this, OperatorLoginActivity.class, false);
                } else {
                    CommonFunc.startAction(ZfPayActivity.this, OperatorLoginActivity1.class, false);
                }
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
                ToastUtils.CustomShow(ZfPayActivity.this, errorEvent + "#" + message);
                showLayout();

                setTransUpLoadData(request);
                // 设置当前交易流水需要上送
                printerData.setUploadFlag(true);
                printerData.setApp_type(app_type);
                // 保存打印的数据，不保存图片数据
                PrinterDataSave();
                // 打印
                Printer.print(printerData, ZfPayActivity.this);
            }

            @Override
            public void onFailurTimeOut(String s, String error_msg) {

            }

            @Override
            public void onLogin() {
                dialog.dismiss();
                AppManager.getAppManager().finishAllActivity();
                if (Config.OPERATOR_UI_BEFORE) {
                    CommonFunc.startAction(ZfPayActivity.this, OperatorLoginActivity.class, false);
                } else {
                    CommonFunc.startAction(ZfPayActivity.this, OperatorLoginActivity1.class, false);
                }
            }
        });
    }


    /**
     * 流水上送,目的是计算海鼎的积分
     *
     * @param request
     */
    private void transUploadAction2(final TransUploadRequest request) {
        final LoadingDialog dialog = new LoadingDialog(this);
        dialog.show("正在计算积分...");
        dialog.setCancelable(false);
        this.sbsAction.transUpload(this, request, new ActionCallbackListener<TransUploadResponse>() {
            @Override
            public void onSuccess(TransUploadResponse data) {
                dialog.dismiss();
                setTransUpLoadData(request);
                printerData.setApp_type(app_type);
                printerData.setPoint(data.getPoint());
                printerData.setPhoneNo(request.getPhone());
                // 上送积分
                HdAction.HdAdjustScore(ZfPayActivity.this, request.getPhone(), data.getPoint(), new HdAction.HdCallResult() {
                    @Override
                    public void onSuccess(String data) {

                        HdAdjustScoreResponse response = new Gson().fromJson(data, HdAdjustScoreResponse.class);

                        //保存流水号和总积分
                        printerData.setPointCurrent(Integer.parseInt(response.getResult().getScoreTotal()));
                        printerData.setFlowNo(response.getResult().getFlowNo());

                        // 保存打印的数据，不保存图片数据
                        PrinterDataSave();
                        // 打印
                        Printer.print(printerData, ZfPayActivity.this);
                        showLayout();
                    }

                    @Override
                    public void onFailed(String errorCode, String message) {
                        ToastUtils.CustomShow(ZfPayActivity.this, errorCode + "#" + message);
                        // 保存打印的数据，不保存图片数据
                        PrinterDataSave();
                        // 打印
                        Printer.print(printerData, ZfPayActivity.this);
                        showLayout();
                    }
                });
            }

            @Override
            public void onFailure(String errorEvent, String message) {
                dialog.dismiss();
                ToastUtils.CustomShow(ZfPayActivity.this, errorEvent + "#" + message);
                showLayout();

                setTransUpLoadData(request);
                // 设置当前交易流水需要上送
                printerData.setUploadFlag(true);
                printerData.setApp_type(app_type);
                // 保存打印的数据，不保存图片数据
                PrinterDataSave();
                // 打印
                Printer.print(printerData, ZfPayActivity.this);
            }

            @Override
            public void onFailurTimeOut(String s, String error_msg) {

            }

            @Override
            public void onLogin() {
                dialog.dismiss();
                AppManager.getAppManager().finishAllActivity();
                if (Config.OPERATOR_UI_BEFORE) {
                    CommonFunc.startAction(ZfPayActivity.this, OperatorLoginActivity.class, false);
                } else {
                    CommonFunc.startAction(ZfPayActivity.this, OperatorLoginActivity1.class, false);
                }
            }
        });
    }


    private void sendYxf(final SbsPrinterData recordData) {

        String money = recordData.getAmount();//"0.01";
        String mobile = recordData.getPhoneNo();//"13979328519";
        String time = String.valueOf(StringUtils.getdate2TimeStamp(recordData.getDateTime()));
        String orderId = time + StringUtils.getSerial();

        String admin_id = (String) SPUtils.get(this, Config.YXF_MERCHANT_ID, Config.YXF_DEFAULT_MERCHANTID);

        if (StringUtils.isEmpty(admin_id)) {

            ToastUtils.CustomShow(this, "上送商户ID为空");
        }

        if (StringUtils.isEmpty(money)) {
            ToastUtils.CustomShow(this, "上送金额为空");
        }

        if (StringUtils.isEmpty(mobile)) {
            ToastUtils.CustomShow(this, "上送手机号为空");
        }


        String before = admin_id.trim() + money + mobile + time + orderId + Config.YXF_KEY;
        LogUtils.e(before);
        String skey = EncryptMD5Util.MD5(before);

        Map<String, String> paramsMap = new LinkedHashMap<String, String>();
        paramsMap.put("arr1", admin_id);
        paramsMap.put("arr2", money);
        paramsMap.put("arr3", mobile);
        paramsMap.put("arr4", time);
        paramsMap.put("arr5", orderId);
        paramsMap.put("skey", skey);


        LogUtils.e(paramsMap.toString());

        final LoadingDialog dialog = new LoadingDialog(this);
        dialog.show("正在上送...");
        dialog.setCancelable(false);

        MyOkHttp.get().get(this, Config.YXF_URL, paramsMap, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                LogUtils.e("sendYxf", response.toString());
                recordData.setApp_type(app_type);

                try {
                    String state = response.getString("state");
                    String info = response.getString("info");
                    String qr_code = response.getString("qr_code");
                    if (StringUtils.isEquals(state, "0")) {
                        ToastUtils.CustomShow(ZfPayActivity.this, "上送成功");

                        //判断二维码链接是否为空，为空直接打印，不为空去下载
                        if (StringUtils.isEmpty(qr_code)) {
                            dialog.dismiss();

                            PrinterDataSave();
                            Printer.getInstance(ZfPayActivity.this).print(recordData, ZfPayActivity.this);
                            showLayout();
                        }else {

                            yxf_setTransUpdateResponse(recordData, qr_code, dialog, true);
                        }


                    } else {
                        dialog.dismiss();
                        ToastUtils.CustomShow(ZfPayActivity.this, !StringUtils.isEmpty(info) ? info : "上送失败");
                        recordData.setUploadFlag(false);

                        PrinterDataSave();
                        Printer.getInstance(ZfPayActivity.this).print(recordData, ZfPayActivity.this);
                        showLayout();
                    }


                } catch (JSONException e) {
                    dialog.dismiss();
                    ToastUtils.CustomShow(ZfPayActivity.this, "返回数据解析失败");
                    recordData.setUploadFlag(false);

                    PrinterDataSave();
                    Printer.getInstance(ZfPayActivity.this).print(recordData, ZfPayActivity.this);
                    showLayout();
                }


            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                dialog.dismiss();
                LogUtils.e("sendYxf", error_msg);

                recordData.setUploadFlag(false);
                recordData.setApp_type(app_type);
                PrinterDataSave();
                Printer.getInstance(ZfPayActivity.this).print(recordData, ZfPayActivity.this);
                showLayout();

            }
        });
    }



    protected void yxf_setTransUpdateResponse(final SbsPrinterData data, final String qr_code, final LoadingDialog dialog, boolean flag) {

        data.setCoupon(qr_code);
        if (flag) {
            // 保存打印的数据，不保存图片数据
            PrinterDataSave();
        }

        //开启线程下载二维码图片
        new Thread(new Runnable() {

            @Override
            public void run() {

                Bitmap title_bitmap = null;

                if (!StringUtils.isEmpty(qr_code)) {

                    try {
                        title_bitmap = Glide.with(getApplicationContext())
                                .load(qr_code)
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
                bundle.putParcelable("title_bitmap", title_bitmap);
                msg.setData(bundle);
                mhandler.sendMessage(msg);

            }
        }).start();

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
//        LogUtils.e(data);
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
            Printer.getInstance(ZfPayActivity.this).print(printerData, ZfPayActivity.this);

        }


    };

    protected void setTransUpdateResponse(final TransUploadResponse data, final LoadingDialog dialog, boolean flag) {
        printerData.setPoint_url(data.getPoint_url());
        printerData.setPoint(data.getPoint());
        printerData.setPointCurrent(data.getPointCurrent());
        printerData.setCoupon(data.getCoupon());
        printerData.setTitle_url(data.getTitle_url());
        printerData.setMoney(data.getMoney());
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

                if (!StringUtils.isEmpty(data.getCoupon())) {

                    try {
                        title_bitmap = Glide.with(getApplicationContext())
                                .load(data.getCoupon())
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


//    private String getMenchantNo(int PayType) {
//        String sm_type = MyApplication.getInstance().getLoginData().getScanPayType();
//        String menchantNo = "";
//        if (PayType == Constants.PAY_WAY_ALY || PayType == Constants.PAY_WAY_BFB || PayType == Constants.PAY_WAY_JD
//                || PayType == Constants.PAY_WAY_WX) {
//            if (!StringUtils.isEmpty(sm_type) && StringUtils.isEquals(sm_type, Constants.SM_TYPE_SQB)) {
//                menchantNo = MyApplication.getInstance().getLoginData().getActivateCodeMerchantNo();
//            } else {
//                menchantNo = MyApplication.getInstance().getLoginData().getFyMerchantNo();
//            }
//        } else if (PayType == Constants.PAY_WAY_FLOT || PayType == Constants.PAY_WAY_CASH) {
//            menchantNo = MyApplication.getInstance().getLoginData().getMerchantNo();
//        } else if (PayType == Constants.PAY_WAY_QB) {
//            menchantNo = printerData.getMerchantNo();
//        }
//
//        return menchantNo;
//    }
//
//    private String getAuthCode(int PayType) {
//        String authCode = "";
//
//        if (PayType == Constants.PAY_WAY_ALY || PayType == Constants.PAY_WAY_BFB || PayType == Constants.PAY_WAY_JD
//                || PayType == Constants.PAY_WAY_WX || PayType == Constants.PAY_WAY_QB) {
//            authCode = printerData.getAuthCode();
//        } else if (PayType == Constants.PAY_WAY_FLOT) {
//            authCode = printerData.getReferNo();
//        }
//        return authCode;
//    }
//
//    private String getClientOrderNo(int PayType) {
//        String sm_type = MyApplication.getInstance().getLoginData().getScanPayType();
//        String orderId = "";
//        if (PayType == Constants.PAY_WAY_ALY || PayType == Constants.PAY_WAY_BFB || PayType == Constants.PAY_WAY_JD
//                || PayType == Constants.PAY_WAY_WX) {
//            if (!StringUtils.isEmpty(sm_type) && StringUtils.isEquals(sm_type, Constants.SM_TYPE_SQB)) {
//                orderId = bat.getMyOrderId();
//            } else if (!StringUtils.isEmpty(sm_type) && StringUtils.isEquals(sm_type, Constants.SM_TYPE_FY)) {
//                orderId = CommonFunc.getNewClientSn(this, PayType);
//            }
//        } else {
//            orderId = CommonFunc.getNewClientSn(this, PayType);
//        }
//
//        return orderId;
//    }
//
//    private String getTransNo(int PayType) {
//        String transNo = "";
//        if (PayType == Constants.PAY_WAY_FLOT) {
//            transNo = printerData.getVoucherNo();//AuthNo();// getReferNo();
//        } else if (PayType == Constants.PAY_WAY_ALY || PayType == Constants.PAY_WAY_BFB
//                || PayType == Constants.PAY_WAY_JD || PayType == Constants.PAY_WAY_WX ||
//                PayType == Constants.PAY_WAY_QB) {
//            transNo = printerData.getTransNo();
//        }
//        return transNo;
//    }


    private void activateAction(int isSuccess) {


        final LoginApiResponse loginData = MyApplication.getInstance().getLoginData();
        int sid = loginData.getSid();
        String activateCode = loginData.getActiveCode();
        this.sbsAction.active(this, sid, isSuccess, activateCode, new ActionCallbackListener<ActivateApiResponse>() {
            @Override
            public void onSuccess(ActivateApiResponse data) {
                // 设置到当前使用 同时 更新到数据库
                loginData.setActivateCodemerchantName(data.getMerchantName());
                loginData.setActivateCodeMerchantNo(data.getMerchantNo());
                // 更新到数据库
                ContentValues values = new ContentValues();
                values.put("activateCodeMerchantNo", data.getMerchantNo());
                values.put("activateCodemerchantName", data.getMerchantName());
                values.put("activateMerchantNanme", data.getTerminalName());
                DataSupport.update(LoginApiResponse.class, values, loginData.getId());
            }

            @Override
            public void onFailure(String errorEvent, String message) {
                // 这里反馈没有成功，那边就让这个激活码的状态为false ，重新签到的时候再次去激活，反馈
                // 保存激活状态
                MyApplication.getInstance().getLoginData().setActive(false);
                MyApplication.getInstance().getLoginData().setActiveCode("");
                // 更新到数据库
                ContentValues values = new ContentValues();
                values.put("isActive", false);
                values.put("activeCode", "");
                DataSupport.update(LoginApiResponse.class, values,
                        MyApplication.getInstance().getLoginData().getId());
            }

            @Override
            public void onFailurTimeOut(String s, String error_msg) {

            }

            @Override
            public void onLogin() {
                AppManager.getAppManager().finishAllActivity();
                if (Config.OPERATOR_UI_BEFORE) {
                    CommonFunc.startAction(ZfPayActivity.this, OperatorLoginActivity.class, false);
                } else {
                    CommonFunc.startAction(ZfPayActivity.this, OperatorLoginActivity1.class, false);
                }
            }
        });
    }


    protected void Richer_setTransUpdateResponse(final RicherGetMember data, final LoadingDialog dialog, boolean flag) {

        if (flag) {
            // 保存打印的数据，不保存图片数据
            PrinterDataSave();
        }
        new Thread(new Runnable() {

            @Override
            public void run() {


                dialog.dismiss();

                Message msg = new Message();
                Bundle bundle = new Bundle();
                msg.setData(bundle);
                mhandler.sendMessage(msg);

            }
        }).start();

    }

    private void Richer_transUploadAction(final TransUploadRequest request) {
        final LoadingDialog dialog = new LoadingDialog(this);
        dialog.show("正在上传交易流水...");
        dialog.setCancelable(false);
        RicherQb.UploadTransInfo(ZfPayActivity.this, request, new ActionCallbackListener<RicherGetMember>() {
            @Override
            public void onSuccess(RicherGetMember data) {
                // 备份数据
//				CommonFunc.TransUploadDataBack(request);

                setTransUpLoadData(request);
                // 备份下交易流水数据
//				printerData.setRequest(request);
                // 设置流水返回的数据
                Richer_setTransUpdateResponse(data, dialog, true);
                printerData.setApp_type(app_type);

                PrinterDataSave();
            }


            @Override
            public void onFailure(String errorEvent, String message) {
                dialog.dismiss();
                ToastUtils.CustomShow(ZfPayActivity.this, errorEvent + "#" + message);
                ll_payType.setVisibility(View.GONE);
                ll_payFinish.setVisibility(View.VISIBLE);
                // 备份数据
//				CommonFunc.TransUploadDataBack(request);
                setTransUpLoadData(request);
                // 备份下交易流水数据
                printerData.setUploadFlag(true);
//				printerData.setRequest(request);
                // 保存打印的数据，不保存图片数据
                printerData.setApp_type(app_type);
                PrinterDataSave();
                // 打印
                Printer.print(printerData, ZfPayActivity.this);
            }

            @Override
            public void onLogin() {
                AppManager.getAppManager().finishAllActivity();
                if (Config.OPERATOR_UI_BEFORE) {
                    CommonFunc.startAction(ZfPayActivity.this, OperatorLoginActivity.class, false);
                } else {
                    CommonFunc.startAction(ZfPayActivity.this, OperatorLoginActivity1.class, false);
                }
            }

            @Override
            public void onFailurTimeOut(String s, String error_msg) {

            }
        });
    }
}
