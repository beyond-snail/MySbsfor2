package com.zfsbs.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hd.core.HdAction;
import com.hd.model.HdAdjustScoreResponse;
import com.myokhttp.MyOkHttp;
import com.myokhttp.response.JsonResponseHandler;
import com.tool.utils.activityManager.AppManager;
import com.tool.utils.dialog.LoadingDialog;
import com.tool.utils.utils.AlertUtils;
import com.tool.utils.utils.EncryptMD5Util;
import com.tool.utils.utils.LogUtils;
import com.tool.utils.utils.SPUtils;
import com.tool.utils.utils.StringUtils;
import com.tool.utils.utils.ToastUtils;
import com.wosai.upay.bean.UpayResult;
import com.zfsbs.R;
import com.zfsbs.common.CommonFunc;
import com.zfsbs.config.Config;
import com.zfsbs.config.Constants;
import com.zfsbs.core.action.BATPay;
import com.zfsbs.core.action.FyBat;
import com.zfsbs.core.action.Printer;
import com.zfsbs.core.action.RicherQb;
import com.zfsbs.core.myinterface.ActionCallbackListener;
import com.zfsbs.core.myinterface.BatInterface;
import com.zfsbs.model.ChargeBlance;
import com.zfsbs.model.FyMicropayRequest;
import com.zfsbs.model.FyMicropayResponse;
import com.zfsbs.model.FyQueryRequest;
import com.zfsbs.model.FyQueryResponse;
import com.zfsbs.model.FyRefundResponse;
import com.zfsbs.model.RechargeUpLoad;
import com.zfsbs.model.RicherGetMember;
import com.zfsbs.model.SbsPrinterData;
import com.zfsbs.model.StkPayRequest;
import com.zfsbs.model.TransUploadRequest;
import com.zfsbs.model.TransUploadResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class RecordItemInfoActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout lv;
    private LinearLayout lv_flot;
    private LinearLayout lv_cash;
    private LinearLayout lv_bat;
    private LinearLayout lv_old_orderId;

    private TextView tvMerchantName;
    private TextView tvMerchantNo;
    private TextView tvTerminalNo;
    private TextView tvCardNo;
    private TextView tvFOrderAmount;
    private TextView tvFAmount;
    private TextView tvFBackAmount;
    private TextView tvFPointCoverMoney;
    private TextView tvFCouponCoverMoney;
    private TextView tvTransTime;
    private TextView tvTransPayType;
    private TextView tvBatchNo;
    private TextView tvTraceNo;
    private TextView tvReferNo;
    private TextView tvClientOrderNo;
    private TextView tvOldOrderNo;

    private TextView tvBMerchantName;
    private TextView tvBMerchantNo;
    private TextView tvBTerminalNo;
    private TextView tvBClientOrderNo;
    private TextView tvBAuthCode;
    private TextView tvBOrderAmount;
    private TextView tvBAmount;
    private TextView tvBBackAmount;
    private TextView tvBPointCoverMoney;
    private TextView tvBCouponCoverMoney;
    private TextView tvBTransTime;
    private TextView tvBTransPayType;

    private TextView tvCMerchantName;
    private TextView tvCMerchantNo;
    private TextView tvCTerminalNo;
    private TextView tvCClientOrderNo;
    private TextView tvCOrderAmount;
    private TextView tvCAmount;
    private TextView tvCBackAmount;
    private TextView tvCPointCoverMoney;
    private TextView tvCCouponCoverMoney;
    private TextView tvCTransTime;
    private TextView tvCTransPayType;

    private Button btnPrinter;
    private Button btnRefund;
    private Button btnTransUpload;
    private Button btnQuery;
    private SbsPrinterData recordData;
    private Intent myintent;

    private String refund_order_no; //退款订单号

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_record_item_info);
//        AppManager.getAppManager().addActivity(this);
        initTitle("交易详情");

        myintent = new Intent();

        initView();
        initData();
        addListener();
    }

    private void initView() {

        lv = (LinearLayout) findViewById(R.id.activity_record_item_info);
        lv_flot = (LinearLayout) findViewById(R.id.id_lv_flot);
        lv_bat = (LinearLayout) findViewById(R.id.id_lv_bat);
        lv_cash = (LinearLayout) findViewById(R.id.id_lv_cash);
        lv_old_orderId = (LinearLayout) findViewById(R.id.id_ly_old_orderNO);

        tvMerchantName = (TextView) findViewById(R.id.id_merchant_name);
        tvMerchantNo = (TextView) findViewById(R.id.id_merchant_no);
        tvTerminalNo = (TextView) findViewById(R.id.id_terminal_no);
        tvCardNo = (TextView) findViewById(R.id.id_CardNo);
        tvFOrderAmount = (TextView) findViewById(R.id.id_flot_OrderAmount);
        tvFAmount = (TextView) findViewById(R.id.id_flot_amount);
        tvFBackAmount = (TextView) findViewById(R.id.id_flot_back_amount);
        tvFPointCoverMoney = (TextView) findViewById(R.id.id_flot_PointCoverMoney);
        tvFCouponCoverMoney = (TextView) findViewById(R.id.id_flot_CouponCoverMoney);
        tvTransTime = (TextView) findViewById(R.id.id_transTime);
        tvTransPayType = (TextView) findViewById(R.id.id_transPayType);
        tvBatchNo = (TextView) findViewById(R.id.id_batch_no);
        tvTraceNo = (TextView) findViewById(R.id.id_trace_no);
        tvReferNo = (TextView) findViewById(R.id.id_refer_no);
        tvClientOrderNo = (TextView) findViewById(R.id.id_folt_ClientOrderNo);
        tvOldOrderNo = (TextView) findViewById(R.id.id_oldorderNO);

        tvBMerchantName = (TextView) findViewById(R.id.id_bat_merchant_name);
        tvBMerchantNo = (TextView) findViewById(R.id.id_bat_merchant_no);
        tvBTerminalNo = (TextView) findViewById(R.id.id_bat_terminal_no);
        tvBClientOrderNo = (TextView) findViewById(R.id.id_ClientOrderNo);
        tvBAuthCode = (TextView) findViewById(R.id.id_AuthCode);
        tvBOrderAmount = (TextView) findViewById(R.id.id_OrderAmount);
        tvBAmount = (TextView) findViewById(R.id.id_bat_amount);
        tvBBackAmount = (TextView) findViewById(R.id.id_bat_back_amount);
        tvBPointCoverMoney = (TextView) findViewById(R.id.id_PointCoverMoney);
        tvBCouponCoverMoney = (TextView) findViewById(R.id.id_CouponCoverMoney);
        tvBTransTime = (TextView) findViewById(R.id.id_bat_transTime);
        tvBTransPayType = (TextView) findViewById(R.id.id_bat_transPayType);


        tvCMerchantName = (TextView) findViewById(R.id.id_cash_merchant_name);
        tvCMerchantNo = (TextView) findViewById(R.id.id_cash_merchant_no);
        tvCTerminalNo = (TextView) findViewById(R.id.id_cash_terminal_no);
        tvCClientOrderNo = (TextView) findViewById(R.id.id_cash_ClientOrderNo);
        tvCOrderAmount = (TextView) findViewById(R.id.id_cash_OrderAmount);
        tvCAmount = (TextView) findViewById(R.id.id_cash_amount);
        tvCBackAmount = (TextView) findViewById(R.id.id_cash_back_amount);
        tvCPointCoverMoney = (TextView) findViewById(R.id.id_cash_PointCoverMoney);
        tvCCouponCoverMoney = (TextView) findViewById(R.id.id_cash_CouponCoverMoney);
        tvCTransTime = (TextView) findViewById(R.id.id_cash_transTime);
        tvCTransPayType = (TextView) findViewById(R.id.id_cash_transPayType);


        btnPrinter = (Button) findViewById(R.id.id_printer);
        btnRefund = (Button) findViewById(R.id.id_refund);
        btnTransUpload = (Button) findViewById(R.id.id_transUpload);
        btnQuery = (Button) findViewById(R.id.id_sm_query);

    }

    private void initData() {
        Bundle bundle = new Bundle();
        bundle = getIntent().getExtras();
        recordData = (SbsPrinterData) bundle.getSerializable("Record");
        if (recordData == null) {
            return;
        }

        refund_order_no = recordData.getRefund_order_no();
        LogUtils.e(recordData.toString());
        showLayout(recordData.getPayType());
        showData(recordData.getPayType());

    }

    private void showData(int payType) {
        switch (payType) {
            case Constants.PAY_WAY_FLOT:
            case Constants.PAY_WAY_UNDO:
            case Constants.PAY_WAY_AUTHCANCEL:
            case Constants.PAY_WAY_AUTHCOMPLETE:
            case Constants.PAY_WAY_VOID_AUTHCOMPLETE:
            case Constants.PAY_WAY_PREAUTH:
                show_flot();
                break;
            case Constants.PAY_WAY_ALY:
            case Constants.PAY_WAY_WX:
            case Constants.PAY_WAY_UNIPAY:
            case Constants.PAY_WAY_BFB:
            case Constants.PAY_WAY_JD:
            case Constants.PAY_WAY_QB:
            case Constants.PAY_WAY_STK:
            case Constants.PAY_WAY_RECHARGE_ALY:
            case Constants.PAY_WAY_RECHARGE_WX:
            case Constants.PAY_WAY_RECHARGE_UNIPAY:
                show_bat();
                break;
            case Constants.PAY_WAY_CASH:
            case Constants.PAY_WAY_PAY_FLOT:
            case Constants.PAY_WAY_RECHARGE_CASH:
                show_cash();
                break;
            default:
                break;
        }
    }

    private void showLayout(int type) {
        switch (type) {
            case Constants.PAY_WAY_FLOT:
            case Constants.PAY_WAY_AUTHCOMPLETE:
            case Constants.PAY_WAY_PREAUTH:
                lv_cash.setVisibility(View.GONE);
                lv_bat.setVisibility(View.GONE);
                lv_flot.setVisibility(View.VISIBLE);
                btnRefund.setVisibility(View.GONE);

                break;
            case Constants.PAY_WAY_UNDO:
            case Constants.PAY_WAY_AUTHCANCEL:
            case Constants.PAY_WAY_VOID_AUTHCOMPLETE:
                lv_cash.setVisibility(View.GONE);
                lv_bat.setVisibility(View.GONE);
                lv_flot.setVisibility(View.VISIBLE);
                lv_old_orderId.setVisibility(View.VISIBLE);
                if (!recordData.isRefundUpload()) {
                    btnRefund.setVisibility(View.VISIBLE);
                    btnRefund.setText("撤销流水上送");

                } else {
                    btnRefund.setVisibility(View.GONE);
                }
                break;
            case Constants.PAY_WAY_ALY:
            case Constants.PAY_WAY_WX:
            case Constants.PAY_WAY_UNIPAY:
            case Constants.PAY_WAY_BFB:
            case Constants.PAY_WAY_JD:
            case Constants.PAY_WAY_QB:
            case Constants.PAY_WAY_STK:

                lv_cash.setVisibility(View.GONE);
                lv_bat.setVisibility(View.VISIBLE);
                lv_flot.setVisibility(View.GONE);

                //交易失败显示
//                if (recordData.isStatus()) {
//                    btnPrinter.setVisibility(View.GONE);
//                    btnRefund.setVisibility(View.GONE);
//                    btnTransUpload.setVisibility(View.GONE);
//                    btnQuery.setVisibility(View.VISIBLE);
//
//                    break;
//                }

                if (recordData.isRefund()) {
                    if (recordData.isRefundUpload()) {
                        btnRefund.setVisibility(View.GONE);
                    } else {
                        btnRefund.setVisibility(View.VISIBLE);
                        btnRefund.setText("退款流水上送");
                    }
                } else {
                    btnRefund.setVisibility(View.VISIBLE);
                }

                break;
            case Constants.PAY_WAY_RECHARGE_ALY:
            case Constants.PAY_WAY_RECHARGE_WX:
            case Constants.PAY_WAY_RECHARGE_UNIPAY:
                lv_cash.setVisibility(View.GONE);
                lv_bat.setVisibility(View.VISIBLE);
                lv_flot.setVisibility(View.GONE);
                btnRefund.setVisibility(View.GONE);
                break;
//            case Constants.PAY_WAY_QB:
//                lv_cash.setVisibility(View.GONE);
//                lv_bat.setVisibility(View.VISIBLE);
//                lv_flot.setVisibility(View.GONE);
//                btnRefund.setVisibility(View.GONE);
////                if (recordData.isRefund()) {
////                    if (recordData.isRefundUpload()){
////                        btnRefund.setVisibility(View.GONE);
////                    }else {
////                        btnRefund.setVisibility(View.VISIBLE);
////                        btnRefund.setText("退款流水上送");
////                    }
////                }else {
////                    btnRefund.setVisibility(View.VISIBLE);
////                }
//                break;
            case Constants.PAY_WAY_CASH:
            case Constants.PAY_WAY_PAY_FLOT:
            case Constants.PAY_WAY_RECHARGE_CASH:
                lv_cash.setVisibility(View.VISIBLE);
                lv_bat.setVisibility(View.GONE);
                lv_flot.setVisibility(View.GONE);
                btnRefund.setVisibility(View.GONE);
                break;
            default:
                break;
        }

        if (recordData.isUploadFlag()) {
            btnTransUpload.setVisibility(View.VISIBLE);
        } else {
            btnTransUpload.setVisibility(View.GONE);

        }
    }

    private void show_cash() {
        tvCMerchantName.setText(StringUtils.isEmpty(recordData.getMerchantName()) ? "" : recordData.getMerchantName());
        tvCMerchantNo.setText(StringUtils.isEmpty(recordData.getMerchantNo()) ? "" : recordData.getMerchantNo());
        tvCTerminalNo.setText(StringUtils.isEmpty(recordData.getTerminalId()) ? "" : recordData.getTerminalId());
        tvCClientOrderNo.setText(StringUtils.isEmpty(recordData.getClientOrderNo()) ? "" : recordData.getClientOrderNo());
        if (recordData.getPayType() == Constants.PAY_WAY_RECHARGE_CASH || recordData.getPayType() == Constants.PAY_WAY_PAY_FLOT) {
            textView(R.id.id_text_C_orderAmount).setText("充值金额");
            textView(R.id.id_text_C_amount).setText("到账金额");
            textView(R.id.id_text_C_blance_amount).setText("会员账号余额：");
            linearLayout(R.id.id_cash_recharge_balance).setVisibility(View.VISIBLE);
            textView(R.id.id_C_r_balance_amount).setText(StringUtils.formatIntMoney(recordData.getPacektRemian()));
        } else {
            textView(R.id.id_text_C_orderAmount).setText("实收金额");
            textView(R.id.id_text_C_amount).setText("订单金额");
            linearLayout(R.id.id_cash_recharge_balance).setVisibility(View.GONE);
        }
        tvCOrderAmount.setText(StringUtils.isEmpty(recordData.getAmount()) ? "" : recordData.getAmount());
        tvCAmount.setText(StringUtils.isEmpty(recordData.getReceiveAmount()) ? "" : recordData.getReceiveAmount());
        tvCBackAmount.setText(StringUtils.isEmpty(StringUtils.formatIntMoney(recordData.getBackAmt())) ? "" : StringUtils.formatIntMoney(recordData.getBackAmt()));
        tvCPointCoverMoney.setText(StringUtils.isEmpty(StringUtils.formatIntMoney(recordData.getPointCoverMoney())) ? "" : StringUtils.formatIntMoney(recordData.getPointCoverMoney()));
        tvCCouponCoverMoney.setText(StringUtils.isEmpty(StringUtils.formatIntMoney(recordData.getCouponCoverMoney())) ? "" : StringUtils.formatIntMoney(recordData.getCouponCoverMoney()));
        tvCTransTime.setText(StringUtils.isEmpty(recordData.getDateTime()) ? "" : recordData.getDateTime());
        tvCTransPayType.setText(StringUtils.isEmpty(Constants.getPayWayDesc(recordData.getPayType())) ? "" : Constants.getPayWayDesc(recordData.getPayType()));
    }

    private void show_bat() {
        tvBMerchantName.setText(StringUtils.isEmpty(recordData.getMerchantName()) ? "" : recordData.getMerchantName());
        tvBMerchantNo.setText(StringUtils.isEmpty(recordData.getMerchantNo()) ? "" : recordData.getMerchantNo());
        tvBTerminalNo.setText(StringUtils.isEmpty(recordData.getTerminalId()) ? "" : recordData.getTerminalId());
//        if (!recordData.isStatus()) {
        tvBClientOrderNo.setText(StringUtils.isEmpty(recordData.getClientOrderNo()) ? "" : recordData.getClientOrderNo());
        tvBAuthCode.setText(StringUtils.isEmpty(recordData.getAuthCode()) ? "" : recordData.getAuthCode());


        if (recordData.getPayType() == Constants.PAY_WAY_RECHARGE_ALY || recordData.getPayType() == Constants.PAY_WAY_RECHARGE_WX ||
                recordData.getPayType() == Constants.PAY_WAY_RECHARGE_UNIPAY) {
            textView(R.id.id_text_B_orderAmount).setText("到账金额");
            textView(R.id.id_text_B_amount).setText("充值金额");
            textView(R.id.id_text_B_blance_amount).setText("会员账号余额：");
            linearLayout(R.id.id_bat_recharge_balance).setVisibility(View.VISIBLE);
            textView(R.id.id_bat_r_balance_amount).setText(StringUtils.formatIntMoney(recordData.getPacektRemian()));

        } else {
            textView(R.id.id_text_B_orderAmount).setText("订单金额");
            textView(R.id.id_text_B_amount).setText("支付金额");
            linearLayout(R.id.id_bat_recharge_balance).setVisibility(View.GONE);
        }

        tvBOrderAmount.setText(StringUtils.isEmpty(StringUtils.formatIntMoney(recordData.getOrderAmount())) ? "" : StringUtils.formatIntMoney(recordData.getOrderAmount()));
//        } else {
//            tvBClientOrderNo.setVisibility(View.GONE);
//            tvBAuthCode.setVisibility(View.GONE);
//            tvBOrderAmount.setVisibility(View.GONE);
//        }

        tvBAmount.setText(StringUtils.isEmpty(recordData.getAmount()) ? "" : recordData.getAmount());
//        if (!recordData.isStatus()) {
        tvBBackAmount.setText(StringUtils.isEmpty(StringUtils.formatIntMoney(recordData.getBackAmt())) ? "" : StringUtils.formatIntMoney(recordData.getBackAmt()));
        tvBPointCoverMoney.setText(StringUtils.isEmpty(StringUtils.formatIntMoney(recordData.getPointCoverMoney())) ? "" : StringUtils.formatIntMoney(recordData.getPointCoverMoney()));
        tvBCouponCoverMoney.setText(StringUtils.isEmpty(StringUtils.formatIntMoney(recordData.getCouponCoverMoney())) ? "" : StringUtils.formatIntMoney(recordData.getCouponCoverMoney()));
//        } else {
//            tvBBackAmount.setVisibility(View.GONE);
//            tvBPointCoverMoney.setVisibility(View.GONE);
//            tvBCouponCoverMoney.setVisibility(View.GONE);
//        }
        tvBTransTime.setText(StringUtils.isEmpty(recordData.getDateTime()) ? "" : recordData.getDateTime());
        tvBTransPayType.setText(StringUtils.isEmpty(Constants.getPayWayDesc(recordData.getPayType())) ? "" : Constants.getPayWayDesc(recordData.getPayType()));

    }

    private void show_flot() {
        tvMerchantName.setText(StringUtils.isEmpty(recordData.getMerchantName()) ? "" : recordData.getMerchantName());
        tvMerchantNo.setText(StringUtils.isEmpty(recordData.getMerchantNo()) ? "" : recordData.getMerchantNo());
        tvTerminalNo.setText(StringUtils.isEmpty(recordData.getTerminalId()) ? "" : recordData.getTerminalId());
        tvClientOrderNo.setText(StringUtils.isEmpty(recordData.getClientOrderNo()) ? "" : recordData.getClientOrderNo());
        tvOldOrderNo.setText(StringUtils.isEmpty(recordData.getOldOrderId()) ? "" : recordData.getOldOrderId());
        tvCardNo.setText(StringUtils.isEmpty(recordData.getCardNo()) ? "" : StringUtils.formatCardNo(recordData.getCardNo()));
        tvFOrderAmount.setText(StringUtils.isEmpty(StringUtils.formatIntMoney(recordData.getOrderAmount())) ? "" : StringUtils.formatIntMoney(recordData.getOrderAmount()));
        tvFAmount.setText(StringUtils.isEmpty(recordData.getAmount()) ? "" : recordData.getAmount());
        tvFBackAmount.setText(StringUtils.isEmpty(StringUtils.formatIntMoney(recordData.getBackAmt())) ? "" : StringUtils.formatIntMoney(recordData.getBackAmt()));
        tvFPointCoverMoney.setText(StringUtils.isEmpty(StringUtils.formatIntMoney(recordData.getPointCoverMoney())) ? "" : StringUtils.formatIntMoney(recordData.getPointCoverMoney()));
        tvFCouponCoverMoney.setText(StringUtils.isEmpty(StringUtils.formatIntMoney(recordData.getCouponCoverMoney())) ? "" : StringUtils.formatIntMoney(recordData.getCouponCoverMoney()));
        tvTransTime.setText(StringUtils.isEmpty(recordData.getDateTime()) ? "" : recordData.getDateTime());
        tvTransPayType.setText(StringUtils.isEmpty(Constants.getPayWayDesc(recordData.getPayType())) ? "" : Constants.getPayWayDesc(recordData.getPayType()));
        tvBatchNo.setText(StringUtils.isEmpty(recordData.getBatchNO()) ? "" : recordData.getBatchNO());
        tvTraceNo.setText(StringUtils.isEmpty(recordData.getVoucherNo()) ? "" : recordData.getVoucherNo());
        tvReferNo.setText(StringUtils.isEmpty(recordData.getReferNo()) ? "" : recordData.getReferNo());

    }

    private void addListener() {
        btnPrinter.setOnClickListener(this);
        btnRefund.setOnClickListener(this);
        btnTransUpload.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_printer: {
//                printer(recordData);
                if (recordData.getApp_type() == Config.APP_HD) {
                    printer(recordData);
                } else if (recordData.getApp_type() == Config.APP_YXF) {
                    printer(recordData);
                } else {
                    if (recordData.getPayType() == Constants.PAY_WAY_RECHARGE_ALY || recordData.getPayType() == Constants.PAY_WAY_RECHARGE_WX ||
                            recordData.getPayType() == Constants.PAY_WAY_PAY_FLOT || recordData.getPayType() == Constants.PAY_WAY_RECHARGE_CASH
                            || recordData.getPayType() == Constants.PAY_WAY_RECHARGE_UNIPAY || recordData.getPayType() == Constants.PAY_WAY_STK || recordData.getPayType() == Constants.PAY_WAY_QB) {
                        printer(recordData);
                    } else {
                        Gson gson = new Gson();
                        TransUploadRequest data = gson.fromJson(recordData.getTransUploadData(), TransUploadRequest.class);
                        getPrinterData(data.getSid(), data.getClientOrderNo());
                    }
                }
            }
            break;
            case R.id.id_refund:
                showCustomizeDialog();

                break;
            case R.id.id_transUpload: {
                if (recordData.getApp_type() == Config.APP_SBS) {
                    if (recordData.getPayType() == Constants.PAY_WAY_RECHARGE_ALY || recordData.getPayType() == Constants.PAY_WAY_RECHARGE_WX ||
                            recordData.getPayType() == Constants.PAY_WAY_RECHARGE_UNIPAY ||
                    recordData.getPayType() == Constants.PAY_WAY_PAY_FLOT || recordData.getPayType() == Constants.PAY_WAY_RECHARGE_CASH) {
                        Gson gson = new Gson();
                        RechargeUpLoad request = gson.fromJson(recordData.getRechargeUpload(), RechargeUpLoad.class);
                        rechargeUpload(request);

                    } else {
                        Gson gson = new Gson();
                        TransUploadRequest data = gson.fromJson(recordData.getTransUploadData(), TransUploadRequest.class);
                        transUploadAction(data);
                    }
                } else if (recordData.getApp_type() == Config.APP_YXF) {
                    sendYxf();
                } else if (recordData.getApp_type() == Config.APP_Richer_e) {
                    Gson gson = new Gson();
                    TransUploadRequest data = gson.fromJson(recordData.getTransUploadData(), TransUploadRequest.class);
//                LogUtils.e(data.toString());
                    data.setCardNo(recordData.getPhoneNo());
                    Richer_transUploadAction(data);//(recordData.getRequest());
                } else if (recordData.getApp_type() == Config.APP_HD) {
                    Gson gson = new Gson();
                    TransUploadRequest data = gson.fromJson(recordData.getTransUploadData(), TransUploadRequest.class);
                    transUploadAction(data);
                }
            }
            break;
            case R.id.id_sm_query:
//                smQuery();
                break;
        }
    }


    private void showCustomizeDialog() {
    /* @setView 装入自定义View ==> R.layout.dialog_customize
     * 由于dialog_customize.xml只放置了一个EditView，因此和图8一样
     * dialog_customize.xml可自定义更复杂的View
     */

        if (!recordData.isRefundUpload() && recordData.isRefund()) {
            setTransCancel(recordData.getPayType(), refund_order_no);
            return;
        }
//        AlertDialog.Builder customizeDialog = new AlertDialog.Builder(RecordItemInfoActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
//        final View dialogView = LayoutInflater.from(RecordItemInfoActivity.this)
//                .inflate(R.layout.activity_dialog_pass, null);
//        customizeDialog.setTitle("请输入主管理员密码");
//        customizeDialog.setView(dialogView);
//        customizeDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                // 获取EditView中的输入内容
//                EditText edit_text = (EditText) dialogView.findViewById(R.id.edit_text);
//
//                String pass = (String) SPUtils.get(RecordItemInfoActivity.this, Constants.MASTER_PASS, Constants.DEFAULT_MASTER_PASS);
//                if (StringUtils.isEmpty(edit_text.getText().toString())) {
//                    ToastUtils.CustomShow(RecordItemInfoActivity.this, "请输入主管理密码");
//                    return;
//                }
//                if (!StringUtils.isEquals(pass, edit_text.getText().toString())) {
//                    ToastUtils.CustomShow(RecordItemInfoActivity.this, "主管理密码错误");
//                    return;
//                }
//                refundTrans();
//            }
//        });
//        customizeDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//
//            }
//        });
//        customizeDialog.show();

        final View view = this.getLayoutInflater().inflate(R.layout.activity_password, null);
        AlertUtils.alertSetPassword(mContext, "请输入主管理员密码。", "确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                // 获取EditView中的输入内容
                EditText edit_text = (EditText) view.findViewById(R.id.et_password);

                String pass = (String) SPUtils.get(RecordItemInfoActivity.this, Constants.MASTER_PASS, Constants.DEFAULT_MASTER_PASS);
                if (StringUtils.isEmpty(edit_text.getText().toString())) {
                    ToastUtils.CustomShow(RecordItemInfoActivity.this, "请输入主管理密码");
                    return;
                }
                if (!StringUtils.isEquals(pass, edit_text.getText().toString())) {
                    ToastUtils.CustomShow(RecordItemInfoActivity.this, "主管理密码错误");
                    return;
                }
                dialog.dismiss();
                refundTrans();
            }

        }, view);
    }


    private void getPrinterData(Long sid, String ClientOrderNo) {

        final LoadingDialog dialog = new LoadingDialog(this);
        dialog.show("获取打印信息...");
        this.sbsAction.getPrinterData(this, sid, ClientOrderNo, new ActionCallbackListener<TransUploadResponse>() {

            @Override
            public void onSuccess(TransUploadResponse data) {
//                setTransUpdateResponse(data, dialog, false);

                Gson gson = new GsonBuilder().serializeNulls().create();
                String counponStr = gson.toJson(data.getCoupon());

                //更新
                ContentValues values = new ContentValues();
                values.put("point_url", data.getPoint_url());
                values.put("point", data.getPoint());
                values.put("pointCurrent", data.getPointCurrent());
                values.put("couponData", counponStr);
                values.put("coupon", data.getCoupon_url());
//                values.put("title_url", data.getTitle_url());
//                values.put("money", data.getMoney());
                values.put("backAmt", data.getBackAmt());
                DataSupport.update(SbsPrinterData.class, values, recordData.getId());

                recordData.setPoint_url(data.getPoint_url());
                recordData.setPoint(data.getPoint());
                recordData.setPointCurrent(data.getPointCurrent());
                recordData.setCoupon(data.getCoupon_url());
//                recordData.setCoupon(data.getCoupon());
//                recordData.setTitle_url(data.getTitle_url());
//                recordData.setMoney(data.getMoney());
                recordData.setCouponData(counponStr);
                recordData.setBackAmt(data.getBackAmt());

                dialog.dismiss();
                printer(recordData);
            }

            @Override
            public void onFailure(String errorEvent, String message) {
                dialog.dismiss();
//                ToastUtils.CustomShow(RecordItemInfoActivity.this, errorEvent + "#" + message);
                printer(recordData);
            }

            @Override
            public void onFailurTimeOut(String s, String error_msg) {

            }

            @Override
            public void onLogin() {
                AppManager.getAppManager().finishAllActivity();
                if (Config.OPERATOR_UI_BEFORE) {
                    CommonFunc.startAction(RecordItemInfoActivity.this, OperatorLoginActivity.class, false);
                } else {
                    CommonFunc.startAction(RecordItemInfoActivity.this, OperatorLoginActivity1.class, false);
                }
            }
        });
    }

    private void printer(final SbsPrinterData data) {
        final LoadingDialog dialog = new LoadingDialog(this);
        dialog.show("正在打印...");

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
                data.setPoint_bitmap(point_bitmap);
                data.setCoupon_bitmap(title_bitmap);
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putSerializable("data", data);
                msg.setData(bundle);
                mhandler.sendMessage(msg);

            }
        }).start();
    }

    private Handler mhandler = new Handler() {
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();

            SbsPrinterData data = (SbsPrinterData) bundle.getSerializable("data");
            // 打印
            Printer.print(data, RecordItemInfoActivity.this);
        }
    };


    private void rechargeUpload(final RechargeUpLoad rechargeUpLoad) {
        if (rechargeUpLoad == null) {
            ToastUtils.CustomShow(RecordItemInfoActivity.this, "充值流水为空");
            return;
        }

        sbsAction.rechargePay(mContext, rechargeUpLoad, new ActionCallbackListener<ChargeBlance>() {
            @Override
            public void onSuccess(ChargeBlance data) {
//                ToastUtils.CustomShow(RecordItemInfoActivity.this, data);
                //更新
                ContentValues values = new ContentValues();
                values.put("promotion_num", rechargeUpLoad.getPromotion_num());
                values.put("pacektRemian", data.getPacektRemian());
                values.put("realize_card_num", data.getRealize_card_num());
                values.put("member_name", data.getMember_name());
                values.put("recharge_order_num", data.getRecharge_order_num());
                values.put("sh_name", data.getSh_name());
                values.put("UploadFlag", false);
                DataSupport.update(SbsPrinterData.class, values, recordData.getId());

                recordData.setPacektRemian(data.getPacektRemian());
                recordData.setUploadFlag(false);
                myintent.putExtra("uploadFlag", true);


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

//                        textView(R.id.id_bat_r_balance_amount).setText(recordData.getPacektRemian());
//                        textView(R.id.id_C_r_balance_amount).setText(recordData.getPacektRemian());

                        setTvText(R.id.id_bat_r_balance_amount, StringUtils.formatIntMoney(recordData.getPacektRemian()));
                        setTvText(R.id.id_C_r_balance_amount, StringUtils.formatIntMoney(recordData.getPacektRemian()));
                        btnTransUpload.setVisibility(View.GONE);
                        btnTransUpload.invalidate();

                    }
                });

                setResult(Activity.RESULT_OK, myintent);

            }

            @Override
            public void onFailure(String errorEvent, String message) {
                ToastUtils.CustomShow(RecordItemInfoActivity.this, errorEvent + "#" + message);


            }

            @Override
            public void onFailurTimeOut(String s, String error_msg) {

            }

            @Override
            public void onLogin() {

            }
        });
    }


    //流水上送
    private void transUploadAction(final TransUploadRequest transUploadRequest) {

        if (transUploadRequest == null) {
            ToastUtils.CustomShow(RecordItemInfoActivity.this, "交易流水为空");
            return;
        }
        LogUtils.e(transUploadRequest.toString());

        final LoadingDialog dialog = new LoadingDialog(this);
        if (recordData.getApp_type() == Config.APP_HD) {
            dialog.show("正在计算积分...");
        } else {
            dialog.show("正在上传交易流水...");
        }
        this.sbsAction.transUpload(this, transUploadRequest, new ActionCallbackListener<TransUploadResponse>() {
            @Override
            public void onSuccess(TransUploadResponse data) {
                dialog.dismiss();
//                LogUtils.e(data.toString());

                if (recordData.getApp_type() != Config.APP_HD) {
                    ToastUtils.CustomShow(RecordItemInfoActivity.this, "交易流水上送成功");
                }

                Gson gson = new GsonBuilder().serializeNulls().create();
                String counponStr = gson.toJson(data.getCoupon());

                //更新
                ContentValues values = new ContentValues();
                values.put("point_url", data.getPoint_url());
                values.put("point", data.getPoint());
                values.put("pointCurrent", data.getPointCurrent());
                values.put("couponData", counponStr);
//                values.put("coupon", data.getCoupon());
//                values.put("title_url", data.getTitle_url());
//                values.put("money", data.getMoney());
                values.put("backAmt", data.getBackAmt());
                values.put("UploadFlag", false);
                DataSupport.update(SbsPrinterData.class, values, recordData.getId());

                recordData.setPoint_url(data.getPoint_url());
                recordData.setPoint(data.getPoint());
                recordData.setPointCurrent(data.getPointCurrent());
//                recordData.setCoupon(data.getCoupon());
//                recordData.setTitle_url(data.getTitle_url());
//                recordData.setMoney(data.getMoney());
                recordData.setCouponData(counponStr);
                recordData.setBackAmt(data.getBackAmt());
                recordData.setUploadFlag(false);

                myintent.putExtra("uploadFlag", true);
                myintent.putExtra("backAmt", data.getBackAmt());
                myintent.putExtra("printer", data);


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        tvFBackAmount.setText(StringUtils.isEmpty(StringUtils.formatIntMoney(recordData.getBackAmt())) ? "" : StringUtils.formatIntMoney(recordData.getBackAmt()));
                        tvCBackAmount.setText(StringUtils.isEmpty(StringUtils.formatIntMoney(recordData.getBackAmt())) ? "" : StringUtils.formatIntMoney(recordData.getBackAmt()));
                        tvBBackAmount.setText(StringUtils.isEmpty(StringUtils.formatIntMoney(recordData.getBackAmt())) ? "" : StringUtils.formatIntMoney(recordData.getBackAmt()));
                        btnTransUpload.setVisibility(View.GONE);
                        btnTransUpload.invalidate();

                    }
                });
                if (recordData.getApp_type() == Config.APP_HD) {
                    //如果是海鼎的话，上送计算后的积分
                    goHdAdjustScore(transUploadRequest.getPhone(), data.getPoint());
                } else {
                    setResult(Activity.RESULT_OK, myintent);
                }
            }

            @Override
            public void onFailure(String errorEvent, String message) {
                dialog.dismiss();
                ToastUtils.CustomShow(RecordItemInfoActivity.this, errorEvent + "#" + message);
            }

            @Override
            public void onFailurTimeOut(String s, String error_msg) {

            }

            @Override
            public void onLogin() {
                dialog.dismiss();
                AppManager.getAppManager().finishAllActivity();
                if (Config.OPERATOR_UI_BEFORE) {
                    CommonFunc.startAction(RecordItemInfoActivity.this, OperatorLoginActivity.class, false);
                } else {
                    CommonFunc.startAction(RecordItemInfoActivity.this, OperatorLoginActivity1.class, false);
                }
            }
        });
    }

    private void refundTrans() {


        if (recordData.getPayType() == Constants.PAY_WAY_UNDO) {
            setTransCancel(Constants.PAY_WAY_UNDO, recordData.getVoucherNo());
            return;
        }

        if (recordData.getPayType() == Constants.PAY_WAY_QB || recordData.getPayType() == Constants.PAY_WAY_STK) {
            setTransPacketCancel(Constants.PAY_WAY_REFUND_QB);
            return;
        }


        String sm_type = recordData.getScanPayType();
//        int amount = (int) (Double.parseDouble(recordData.getAmount()) * 100);
        BigDecimal big = new BigDecimal(recordData.getAmount());
        int amount = big.multiply(new BigDecimal(100)).intValue();
        if (!StringUtils.isEmpty(sm_type) && StringUtils.isEquals(sm_type, Constants.SM_TYPE_FY)) {

            FyBat fybat = new FyBat(this, new FyBat.FYPayResultEvent() {

                @Override
                public void onSuccess(FyMicropayResponse data) {

                }

                @Override
                public void onSuccess(FyQueryResponse data) {

                }

                @Override
                public void onSuccess(FyRefundResponse data) {
                    // 更新到数据库
                    ContentValues values = new ContentValues();
                    values.put("isRefund", true);
                    values.put("refund_order_no", data.getRefund_order_no());
                    DataSupport.update(SbsPrinterData.class, values, recordData.getId());

                    //记录当前成功的订单号，用于如果退款流水上送失败，再次流水上送用。
                    refund_order_no = data.getRefund_order_no();

                    recordData.setRefund(true);

                    myintent.putExtra("refund_order_no", refund_order_no);
//                    myintent.putExtra("isRefund", true);
//                    setResult(Activity.RESULT_OK, myintent);


                    if (recordData.getPayType() == Constants.PAY_WAY_ALY) {
                        setTransCancel(Constants.PAY_WAY_REFUND_ALY, refund_order_no);
                    } else if (recordData.getPayType() == Constants.PAY_WAY_WX) {
                        setTransCancel(Constants.PAY_WAY_REFUND_WX, refund_order_no);
                    } else if (recordData.getPayType() == Constants.PAY_WAY_UNIPAY) {
                        setTransCancel(Constants.PAY_WAY_REFUND_UNIPAY, refund_order_no);
                    }

                }

                @Override
                public void onFailure(FyMicropayRequest data) {

                }

                @Override
                public void onFailure(FyQueryRequest data) {

                }

                @Override
                public void onFailure(int statusCode, String error_msg, String type, String order_no) {

                }

                @Override
                public void onLogin() {
                    ToastUtils.CustomShow(RecordItemInfoActivity.this, "登录失效，请重新登录。。。");
                    AppManager.getAppManager().finishAllActivity();
                    if (Config.OPERATOR_UI_BEFORE) {
                        CommonFunc.startAction(RecordItemInfoActivity.this, OperatorLoginActivity.class, false);
                    } else {
                        CommonFunc.startAction(RecordItemInfoActivity.this, OperatorLoginActivity1.class, false);
                    }
                }
            });
            if (recordData.getPayType() == Constants.PAY_WAY_ALY) {
                if (!recordData.isRefund()) {
                    fybat.refund(Constants.PAY_FY_ALY, recordData.getMerchantNo(), recordData.getAuthCode(), amount);
                } else {
                    setTransCancel(Constants.PAY_WAY_REFUND_ALY, refund_order_no);
                }
            } else if (recordData.getPayType() == Constants.PAY_WAY_WX) {
                if (!recordData.isRefund()) {
                    fybat.refund(Constants.PAY_FY_WX, recordData.getMerchantNo(), recordData.getAuthCode(), amount);
                } else {
                    setTransCancel(Constants.PAY_WAY_REFUND_WX, refund_order_no);
                }
            } else if (recordData.getPayType() == Constants.PAY_WAY_UNIPAY) {
                if (!recordData.isRefund()) {
                    fybat.refund(Constants.PAY_FY_UNION, recordData.getMerchantNo(), recordData.getAuthCode(), amount);
                } else {
                    setTransCancel(Constants.PAY_WAY_REFUND_UNIPAY, refund_order_no);
                }
            }
        } else if (!StringUtils.isEmpty(sm_type) && StringUtils.isEquals(sm_type, Constants.SM_TYPE_SQB)) {

            if (recordData.getPayType() == Constants.PAY_WAY_ALY) {
                if (recordData.isRefund()) {
                    setTransCancel(Constants.PAY_WAY_REFUND_ALY, refund_order_no);
                    return;
                }
            } else if (recordData.getPayType() == Constants.PAY_WAY_WX) {
                if (recordData.isRefund()) {
                    setTransCancel(Constants.PAY_WAY_REFUND_WX, refund_order_no);
                    return;
                }
            } else if (recordData.getPayType() == Constants.PAY_WAY_UNIPAY) {
                if (recordData.isRefund()) {
                    setTransCancel(Constants.PAY_WAY_REFUND_UNIPAY, refund_order_no);
                    return;
                }
            }

            BATPay bat = new BATPay(this);


            bat.refund(recordData.getAuthCode(), recordData.getClientOrderNo(), amount + "", new BatInterface() {
                @Override
                public void success_bat(UpayResult result) {
                    // 更新到数据库
                    ContentValues values = new ContentValues();
                    values.put("isRefund", true);
                    values.put("refund_order_no", result.getTrade_no());
                    DataSupport.update(SbsPrinterData.class, values, recordData.getId());

                    //记录当前成功的订单号，用于如果退款流水上送失败，再次流水上送用。
                    refund_order_no = result.getTrade_no();

                    recordData.setRefund(true);

                    myintent.putExtra("refund_order_no", refund_order_no);

//                    Intent intent = new Intent();
//                    intent.putExtra("isRefund", true);
//                    setResult(Activity.RESULT_OK, intent);

                    LogUtils.e("退款成功");
                    ToastUtils.CustomShow(RecordItemInfoActivity.this, "退款成功");
                    if (recordData.getPayType() == Constants.PAY_WAY_ALY) {
                        setTransCancel(Constants.PAY_WAY_REFUND_ALY, result.getTrade_no());
                    } else if (recordData.getPayType() == Constants.PAY_WAY_WX) {
                        setTransCancel(Constants.PAY_WAY_REFUND_WX, result.getTrade_no());
                    } else if (recordData.getPayType() == Constants.PAY_WAY_UNIPAY) {
                        setTransCancel(Constants.PAY_WAY_REFUND_UNIPAY, result.getTrade_no());
                    }

                }

                @Override
                public void failed_bat(String error_code, String error_msg) {
                    ToastUtils.CustomShow(RecordItemInfoActivity.this, error_code + "#" + error_msg);
                    LogUtils.e("退款失败" + error_msg);
                }

                @Override
                public void onLogin() {
                    ToastUtils.CustomShow(RecordItemInfoActivity.this, "登录失效，请重新登录。。。");
                    AppManager.getAppManager().finishAllActivity();

                    if (Config.OPERATOR_UI_BEFORE) {
                        CommonFunc.startAction(RecordItemInfoActivity.this, OperatorLoginActivity.class, false);
                    } else {
                        CommonFunc.startAction(RecordItemInfoActivity.this, OperatorLoginActivity1.class, false);
                    }
                }
            });
        }
    }


    private void setTransCancel(int payType, final String authCode) {

        if (recordData.getApp_type() == Config.APP_YXF || (recordData.getApp_type() == Config.APP_HD && !recordData.isMember())) {

            // 更新到数据库
            ContentValues values = new ContentValues();
            values.put("isRefundUpload", true);

            DataSupport.update(SbsPrinterData.class, values, recordData.getId());


            myintent.putExtra("isRefund", (recordData.getPayType() == Constants.PAY_WAY_UNDO || recordData.getPayType() == Constants.PAY_WAY_AUTHCANCEL || recordData.getPayType() == Constants.PAY_WAY_VOID_AUTHCOMPLETE) ? false : true);
            myintent.putExtra("isRefundUpload", true);
            setResult(Activity.RESULT_OK, myintent);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btnRefund.setVisibility(View.GONE);
                    btnRefund.invalidate();
                }
            });
            return;
        } else if (recordData.getApp_type() == Config.APP_Richer_e) {

            // 更新到数据库
            ContentValues values = new ContentValues();
            values.put("isRefundUpload", true);

            DataSupport.update(SbsPrinterData.class, values, recordData.getId());


            myintent.putExtra("isRefund", (recordData.getPayType() == Constants.PAY_WAY_UNDO || recordData.getPayType() == Constants.PAY_WAY_AUTHCANCEL || recordData.getPayType() == Constants.PAY_WAY_VOID_AUTHCOMPLETE) ? false : true);
            myintent.putExtra("isRefundUpload", true);
            setResult(Activity.RESULT_OK, myintent);


            Richer_upLoadTransData();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btnRefund.setVisibility(View.GONE);
                    btnRefund.invalidate();
                }
            });
            return;

        }


        Gson gson = new Gson();
        TransUploadRequest data = gson.fromJson(recordData.getTransUploadData(), TransUploadRequest.class);
        String oldOrderNo = data.getClientOrderNo();
        long t = StringUtils.getdate2TimeStamp(StringUtils.getCurTime());//data.getT();
        final String phone = data.getPhone();
        final int point = recordData.getPoint();
        final TransUploadRequest request = new TransUploadRequest();

        String orderId = CommonFunc.getNewClientSn();

        request.setAction("2");
        request.setOld_trade_order_num(oldOrderNo);
        request.setNew_trade_order_num(orderId);
        request.setPayType(payType);
        request.setAuthCode(authCode);
        request.setT(t);
        request.setSid(data.getSid());

        this.sbsAction.transCancelRefund(this, request, new ActionCallbackListener<String>() {
            @Override
            public void onSuccess(String data) {
                ToastUtils.CustomShow(RecordItemInfoActivity.this, data);
                // 更新到数据库
                ContentValues values = new ContentValues();
                values.put("isRefundUpload", true);

                DataSupport.update(SbsPrinterData.class, values, recordData.getId());

                recordData.setRefund(true);
                recordData.setRefundUpload(true);

                myintent.putExtra("isRefund", (recordData.getPayType() == Constants.PAY_WAY_UNDO || recordData.getPayType() == Constants.PAY_WAY_AUTHCANCEL || recordData.getPayType() == Constants.PAY_WAY_VOID_AUTHCOMPLETE) ? false : true);
                myintent.putExtra("isRefundUpload", true);


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btnRefund.setVisibility(View.GONE);
                        btnRefund.invalidate();
                    }
                });
                if (recordData.getApp_type() == Config.APP_HD) {
                    goHdAdjustScore(phone, -point);
                }
                setResult(Activity.RESULT_OK, myintent);
            }

            @Override
            public void onFailure(String errorEvent, String message) {
                ToastUtils.CustomShow(RecordItemInfoActivity.this, message);
                recordData.setRefund(true);
                recordData.setRefundUpload(false);
                recordData.setRefund_order_no(authCode);
                btnRefund.setText("退款流水上送");

                myintent.putExtra("isRefund", (recordData.getPayType() == Constants.PAY_WAY_UNDO || recordData.getPayType() == Constants.PAY_WAY_AUTHCANCEL || recordData.getPayType() == Constants.PAY_WAY_VOID_AUTHCOMPLETE) ? false : true);
                setResult(Activity.RESULT_OK, myintent);
            }

            @Override
            public void onFailurTimeOut(String s, String error_msg) {

            }

            @Override
            public void onLogin() {
                ToastUtils.CustomShow(RecordItemInfoActivity.this, "登录失效，请重新登录。。。");
                AppManager.getAppManager().finishAllActivity();
                if (Config.OPERATOR_UI_BEFORE) {
                    CommonFunc.startAction(RecordItemInfoActivity.this, OperatorLoginActivity.class, false);
                } else {
                    CommonFunc.startAction(RecordItemInfoActivity.this, OperatorLoginActivity1.class, false);
                }
            }
        });
    }


    private String phone;
    private int point = 0;
    private void setTransPacketCancel(int payType) {


        final TransUploadRequest request = new TransUploadRequest();

//        if (recordData.getPayType() == Constants.PAY_WAY_STK ){
            Gson gson = new Gson();
            StkPayRequest data = gson.fromJson(recordData.getStkRequestData(), StkPayRequest.class);
            long t = StringUtils.getdate2TimeStamp(StringUtils.getCurTime());


            String orderId = CommonFunc.getNewClientSn();

            request.setAction("2");
            request.setOld_trade_order_num(data.getOrderNo());
            request.setNew_trade_order_num(orderId);
            request.setPayType(payType);
            request.setSid(data.getSid());
            request.setT(t);

//        }else {
//            Gson gson = new Gson();
//            TransUploadRequest data = gson.fromJson(recordData.getTransUploadData(), TransUploadRequest.class);
//            LogUtils.e("setTransPacketCancel", data.toString());
//            String oldOrderNo = data.getClientOrderNo();
//            int sid = data.getSid();
//            phone = data.getPhone();
//            point = recordData.getPoint();
//            long t = StringUtils.getdate2TimeStamp(StringUtils.getCurTime());
//
//
//            String orderId = CommonFunc.getNewClientSn();
//
//            request.setAction("2");
//            request.setOld_trade_order_num(oldOrderNo);
//            request.setNew_trade_order_num(orderId);
//            request.setPayType(payType);
//            request.setSid(sid);
//            request.setT(t);
//        }

        this.sbsAction.transPacketCancelRefund(this, request, new ActionCallbackListener<String>() {
            @Override
            public void onSuccess(String data) {
                ToastUtils.CustomShow(RecordItemInfoActivity.this, data);
                // 更新到数据库
                ContentValues values = new ContentValues();
                values.put("isRefundUpload", true);
                values.put("isRefund", true);
                DataSupport.update(SbsPrinterData.class, values, recordData.getId());
                recordData.setRefund(true);


                myintent.putExtra("isRefund", (recordData.getPayType() == Constants.PAY_WAY_UNDO || recordData.getPayType() == Constants.PAY_WAY_AUTHCANCEL || recordData.getPayType() == Constants.PAY_WAY_VOID_AUTHCOMPLETE) ? false : true);
                myintent.putExtra("isRefundUpload", true);
//                setResult(Activity.RESULT_OK, myintent);
                if (recordData.getApp_type() == Config.APP_Richer_e) {

                    Richer_upLoadTransData();

                } else if (recordData.getApp_type() == Config.APP_HD) {
                    goHdAdjustScore(phone, -point);
                } else {
                    setResult(Activity.RESULT_OK, myintent);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btnRefund.setVisibility(View.GONE);
                        btnRefund.invalidate();
                    }
                });
            }

            @Override
            public void onFailure(String errorEvent, String message) {
                ToastUtils.CustomShow(RecordItemInfoActivity.this, message);
            }

            @Override
            public void onFailurTimeOut(String s, String error_msg) {

            }

            @Override
            public void onLogin() {
                ToastUtils.CustomShow(RecordItemInfoActivity.this, "登录失效，请重新登录。。。");
                AppManager.getAppManager().finishAllActivity();
                if (Config.OPERATOR_UI_BEFORE) {
                    CommonFunc.startAction(RecordItemInfoActivity.this, OperatorLoginActivity.class, false);
                } else {
                    CommonFunc.startAction(RecordItemInfoActivity.this, OperatorLoginActivity1.class, false);
                }
            }
        });
    }


    /**
     * 赢消费
     */
    private void sendYxf() {

        String money = recordData.getAmount();//"0.01";
        String mobile = recordData.getPhoneNo();//"13979328519";
        String time = String.valueOf(StringUtils.getdate2TimeStamp(recordData.getDateTime()));
        String orderId = time + StringUtils.getSerial();

        String admin_id = (String) SPUtils.get(this, Config.YXF_MERCHANT_ID, Config.YXF_DEFAULT_MERCHANTID);

        if (StringUtils.isEmpty(admin_id)) {

            ToastUtils.CustomShow(this, "上送商户ID为空");
            return;
        }

        if (StringUtils.isEmpty(money)) {
            ToastUtils.CustomShow(this, "上送金额为空");
            return;
        }

        if (StringUtils.isEmpty(mobile)) {
            ToastUtils.CustomShow(this, "上送手机号为空");
            return;
        }


        String before = admin_id + money + mobile + time + orderId + Config.YXF_KEY;
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

        MyOkHttp.get().get(this, Config.YXF_URL, paramsMap, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                LogUtils.e("sendYxf", response.toString());
                dialog.dismiss();

                try {
                    String state = response.getString("state");
                    String info = response.getString("info");
                    if (StringUtils.isEquals(state, "0")) {
                        ToastUtils.CustomShow(RecordItemInfoActivity.this, "上送成功");

                        //更新
                        ContentValues values = new ContentValues();

                        values.put("UploadFlag", false);
                        DataSupport.update(SbsPrinterData.class, values, recordData.getId());

                        recordData.setUploadFlag(false);

                        myintent.putExtra("uploadFlag", true);
                        setResult(Activity.RESULT_OK, myintent);

                    } else {
                        ToastUtils.CustomShow(RecordItemInfoActivity.this, !StringUtils.isEmpty(info) ? info : "上送失败");

                    }


                } catch (JSONException e) {
//                    e.printStackTrace();
                    ToastUtils.CustomShow(RecordItemInfoActivity.this, "返回数据解析失败");

                }


            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                dialog.dismiss();
                LogUtils.e("sendYxf", error_msg);


            }
        });
    }

    private void Richer_upLoadTransData() {
        String money = recordData.getAmount();//"0.01";
        String mobile = recordData.getPhoneNo();//"13979328519";
        String time = String.valueOf(StringUtils.getdate2TimeStamp(recordData.getDateTime()));
        TransUploadRequest request = new TransUploadRequest();
        request.setCardNo(recordData.getPhoneNo());
        request.setPayType(11);
        String amountStr = recordData.getAmount();
        BigDecimal big = new BigDecimal(amountStr);
        int amount = big.multiply(new BigDecimal(100)).intValue();
        request.setBankAmount(amount);
        request.setCash(amount);
        request.setMerchantNo(recordData.getMerchantNo());
        request.setT(Integer.parseInt(time));
        request.setClientOrderNo(recordData.getClientOrderNo());
        request.setTransNo(recordData.getTransNo());
        request.setAuthCode(recordData.getAuthCode());
        Richer_transUploadAction(request);
    }

    private void Richer_transUploadAction(final TransUploadRequest request) {

        RicherQb.UploadTransInfo(RecordItemInfoActivity.this, request, new ActionCallbackListener<RicherGetMember>() {
            @Override
            public void onSuccess(RicherGetMember data) {

                LogUtils.e(data.toString());
                ToastUtils.CustomShow(RecordItemInfoActivity.this, "交易流水上送成功");
                // 更新到数据库

                ContentValues values = new ContentValues();
                values.put("uploadFlag", false);
                DataSupport.update(SbsPrinterData.class, values, recordData.getId());
                //更新
                recordData.setUploadFlag(false);

                myintent.putExtra("uploadFlag", true);

                setResult(Activity.RESULT_OK, myintent);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        tvFBackAmount.setText(StringUtils.isEmpty(StringUtils.formatIntMoney(recordData.getBackAmt())) ? "" : StringUtils.formatIntMoney(recordData.getBackAmt()));
                        tvCBackAmount.setText(StringUtils.isEmpty(StringUtils.formatIntMoney(recordData.getBackAmt())) ? "" : StringUtils.formatIntMoney(recordData.getBackAmt()));
                        tvBBackAmount.setText(StringUtils.isEmpty(StringUtils.formatIntMoney(recordData.getBackAmt())) ? "" : StringUtils.formatIntMoney(recordData.getBackAmt()));
                        btnTransUpload.setVisibility(View.GONE);
                        btnTransUpload.invalidate();

                    }
                });
            }


            @Override
            public void onFailure(String errorEvent, String message) {
                ToastUtils.CustomShow(RecordItemInfoActivity.this, message);
                setResult(Activity.RESULT_OK, myintent);
            }

            @Override
            public void onLogin() {
                AppManager.getAppManager().finishAllActivity();

                if (Config.OPERATOR_UI_BEFORE) {
                    CommonFunc.startAction(RecordItemInfoActivity.this, OperatorLoginActivity.class, false);
                } else {
                    CommonFunc.startAction(RecordItemInfoActivity.this, OperatorLoginActivity1.class, false);
                }
            }

            @Override
            public void onFailurTimeOut(String s, String error_msg) {

            }
        });
    }


    /**
     * 发送给海鼎
     *
     * @param phone
     * @param points
     */
    private void goHdAdjustScore(final String phone, final int points) {
        //上送积分到海鼎
        HdAction.HdAdjustScore(this, phone, points, new HdAction.HdCallResult() {
            @Override
            public void onSuccess(String data) {

                HdAdjustScoreResponse response = new Gson().fromJson(data, HdAdjustScoreResponse.class);

                //保存流水号和总积分
                myintent.putExtra("total_points", response.getResult().getScoreTotal());
                myintent.putExtra("flowNo", response.getResult().getFlowNo());

                recordData.setFlowNo(response.getResult().getFlowNo());
                recordData.setPointCurrent(Integer.parseInt(response.getResult().getScoreTotal()));

                setResult(Activity.RESULT_OK, myintent);
            }

            @Override
            public void onFailed(String errorCode, String message) {
                ToastUtils.CustomShow(RecordItemInfoActivity.this, errorCode + "#" + message);

                setResult(Activity.RESULT_OK, myintent);
            }
        });
    }
}
