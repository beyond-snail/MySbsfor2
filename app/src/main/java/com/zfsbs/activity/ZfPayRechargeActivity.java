package com.zfsbs.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mycommonlib.core.PayCommon;
import com.mycommonlib.model.ComTransInfo;
import com.tool.utils.activityManager.AppManager;
import com.tool.utils.dialog.CommonDialog;
import com.tool.utils.utils.ALog;
import com.tool.utils.utils.LogUtils;
import com.tool.utils.utils.SPUtils;
import com.tool.utils.utils.StringUtils;
import com.tool.utils.utils.ToastUtils;
import com.yzq.testzxing.zxing.android.CaptureActivity;
import com.zfsbs.R;
import com.zfsbs.common.CommonFunc;
import com.zfsbs.config.Config;
import com.zfsbs.config.Constants;
import com.zfsbs.core.action.FyBat;
import com.zfsbs.core.action.Printer;
import com.zfsbs.core.myinterface.ActionCallbackListener;
import com.zfsbs.model.ChargeBlance;
import com.zfsbs.model.FailureData;
import com.zfsbs.model.FyMicropayRequest;
import com.zfsbs.model.FyMicropayResponse;
import com.zfsbs.model.FyQueryRequest;
import com.zfsbs.model.FyQueryResponse;
import com.zfsbs.model.FyRefundResponse;
import com.zfsbs.model.RechargeUpLoad;
import com.zfsbs.model.SbsPrinterData;
import com.zfsbs.myapplication.MyApplication;

import org.litepal.crud.DataSupport;

import static com.zfsbs.config.Constants.PAY_FY_ALY;
import static com.zfsbs.config.Constants.PAY_FY_UNION;
import static com.zfsbs.config.Constants.PAY_FY_WX;
import static com.zfsbs.config.Constants.REQUEST_CAPTURE_ALY;
import static com.zfsbs.config.Constants.REQUEST_CAPTURE_UNIPAY;
import static com.zfsbs.config.Constants.REQUEST_CAPTURE_WX;
import static com.zfsbs.config.Constants.REQUEST_CASH;
import static com.zfsbs.config.Constants.REQUEST_flot_CASH;


public class ZfPayRechargeActivity extends BaseActivity implements View.OnClickListener {

    private FyBat fybat;

//    private RechargeAmount vo;
    private String cardNo;
    private String tgy;
//    private String cardId;
    private String oldAmount;
    private String actualAmount;
    private String orderNo;

    private LinearLayout ll_payType;
    private LinearLayout ll_payFinish;
    private LinearLayout ll_no_pay_amount;
    private LinearLayout ll_payQuery;

    private TextView orderAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_recharge_pay);
//        AppManager.getAppManager().addActivity(this);
        initTitle("选择充值方式");

        initData();
        initView();

    }

    private void initData() {

        cardNo = getIntent().getBundleExtra("data").getString("cardNo");
        tgy = getIntent().getBundleExtra("data").getString("tgy");
        oldAmount = getIntent().getBundleExtra("data").getString("oldAmount");
        actualAmount = getIntent().getBundleExtra("data").getString("actualAmount");
        orderNo = getIntent().getBundleExtra("data").getString("orderNo");

//        cardId = getIntent().getBundleExtra("data").getString("card_id");
//        vo = (RechargeAmount) getIntent().getBundleExtra("data").getSerializable("RechargeAmount");
//        if (vo == null){
//            ToastUtils.CustomShow(mContext, "数据有误");
//            onBackPressed();
//            return;
//        }

        fybat = new FyBat(this, listener1);
    }

    private void initView() {

        orderAmount = (TextView) findViewById(R.id.id_order_amount);
        orderAmount.setText(StringUtils.formatStrMoney(oldAmount));

        textView(R.id.id_dz_amount).setText(StringUtils.formatStrMoney(actualAmount));
        TextView etCardNo = textView(R.id.id_memberCardNo);
        etCardNo.setText(cardNo);


        ll_payType = (LinearLayout) findViewById(R.id.ll_pay_type);
        ll_payFinish = (LinearLayout) findViewById(R.id.ll_pay_finish);
        ll_payQuery = (LinearLayout) findViewById(R.id.ll_pay_query);
        ll_no_pay_amount = (LinearLayout) findViewById(R.id.ll_no_pay_amount);

        linearLayout(R.id.pay_wx).setOnClickListener(this);
        linearLayout(R.id.pay_aly).setOnClickListener(this);
        linearLayout(R.id.pay_cash).setOnClickListener(this);
        linearLayout(R.id.id_pay_flot).setOnClickListener(this);
        linearLayout(R.id.id_ll_unionpay).setOnClickListener(this);

        button(R.id.id_print).setOnClickListener(this);
        button(R.id.id_finish).setOnClickListener(this);
        button(R.id.id_query).setOnClickListener(this);
        button(R.id.id_terminal_query_sure).setOnClickListener(this);

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
            if (!StringUtils.isEmpty(datas.getAuthCode()) && datas.getAuthCode().equals(data.getMchnt_order_no())) {
                ToastUtils.CustomShow(ZfPayRechargeActivity.this, "请确认消费者交易成功。");
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
            if (data.getType().equals(PAY_FY_ALY)) {
                setFyPayFailureQuery(data.getOutOrderNum(), data.getAmount() + "", data.getType(), true, Constants.PAY_WAY_RECHARGE_ALY, Constants.FY_FAILURE_PAY);
            } else if (data.getType().equals(PAY_FY_WX)) {
                setFyPayFailureQuery(data.getOutOrderNum(), data.getAmount() + "", data.getType(), true, Constants.PAY_WAY_RECHARGE_WX, Constants.FY_FAILURE_PAY);
            } else if (data.getType().equals(PAY_FY_UNION)) {
                setFyPayFailureQuery(data.getOutOrderNum(), data.getAmount() + "", data.getType(), true, Constants.PAY_WAY_RECHARGE_UNIPAY, Constants.FY_FAILURE_PAY);
            }

        }

        @Override
        public void onFailure(FyQueryRequest data) {
            showLayoutEndQuery();
            if (data == null) {
                ToastUtils.CustomShow(ZfPayRechargeActivity.this, "请求数据为空，无法查询末笔");
                return;
            }
            if (data.getOrder_type().equals(PAY_FY_ALY)) {
                setFyQueryFailureQuery(data.getOutOrderNum(), data.getOrder_type(), data.getMchnt_order_no(), true, Constants.PAY_WAY_RECHARGE_ALY, Constants.FY_FAILURE_QUERY);
            } else if (data.getOrder_type().equals(PAY_FY_WX)) {
                setFyQueryFailureQuery(data.getOutOrderNum(), data.getOrder_type(), data.getMchnt_order_no(), true, Constants.PAY_WAY_RECHARGE_WX, Constants.FY_FAILURE_QUERY);
            } else if (data.getOrder_type().equals(PAY_FY_UNION)) {
                setFyPayFailureQuery(data.getOutOrderNum(), data.getOrder_type(), data.getMchnt_order_no(), true, Constants.PAY_WAY_RECHARGE_UNIPAY, Constants.FY_FAILURE_QUERY);
            }

        }


        @Override
        public void onLogin() {
            AppManager.getAppManager().finishAllActivity();

            CommonFunc.startAction(ZfPayRechargeActivity.this, OperatorLoginActivity1.class, false);

        }
    };

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
        data.setApp_type(0);
        data.setMember(isMember);
//        data.setCardId(cardId);
        data.setReal_get_money(Integer.parseInt(actualAmount));
        data.setReal_pay_money(Integer.parseInt(oldAmount));
        data.setTgy(tgy);
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
        data.setApp_type(0);
        data.setMember(isMember);
//        data.setCardId(cardId);
        data.setReal_get_money(Integer.parseInt(actualAmount));
        data.setReal_pay_money(Integer.parseInt(oldAmount));
        data.setTgy(tgy);
        CommonFunc.setBackFailureInfo(this, data);
    }


    private void showLayoutEndQuery() {
        ll_payType.setVisibility(View.GONE);
        ll_payQuery.setVisibility(View.VISIBLE);
    }

    private void showLayout() {
        ll_payType.setVisibility(View.GONE);
        ll_payFinish.setVisibility(View.VISIBLE);
    }


    private void setFySmPay1(FyMicropayResponse data) {
        printerData.setMerchantName(MyApplication.getInstance().getLoginData().getFyMerchantName());
        printerData.setMerchantNo(MyApplication.getInstance().getLoginData().getFyMerchantNo());
        printerData.setTerminalId(StringUtils.getTerminalNo(StringUtils.getSerial()));
        printerData.setOperatorNo((String) SPUtils.get(this, Constants.USER_NAME, ""));
        printerData.setTransNo(data.getTransaction_id());
        printerData.setAuthCode(data.getMchnt_order_no());
        printerData.setDateTime(StringUtils.formatTime(data.getTxn_begin_ts()));
        printerData.setOrderAmount(Integer.parseInt(actualAmount));
        printerData.setAmount(StringUtils.formatStrMoney(data.getTotal_amount()));
        printerData.setScanPayType(MyApplication.getInstance().getLoginData().getScanPayType());
        if (data.getOrder_type().equals(PAY_FY_ALY)) {
            printerData.setPayType(Constants.PAY_WAY_RECHARGE_ALY);
        } else if (data.getOrder_type().equals(PAY_FY_WX)) {
            printerData.setPayType(Constants.PAY_WAY_RECHARGE_WX);
        } else if (data.getOrder_type().equals(PAY_FY_UNION)) {
            printerData.setPayType(Constants.PAY_WAY_RECHARGE_UNIPAY);
        }

        printerData.setClientOrderNo(orderNo);

        //流水上送
        RechargeUpLoad rechargeUpLoad = new RechargeUpLoad();

        rechargeUpLoad.setSid(MyApplication.getInstance().getLoginData().getSid());
        rechargeUpLoad.setPayAmount(Integer.parseInt(oldAmount));
        rechargeUpLoad.setOrderNo(printerData.getClientOrderNo());
        rechargeUpLoad.setActivateCode(MyApplication.getInstance().getLoginData().getActiveCode());
        rechargeUpLoad.setMerchantNo(MyApplication.getInstance().getLoginData().getFyMerchantNo());
        rechargeUpLoad.setT(StringUtils.getdate2TimeStamp(printerData.getDateTime()));
        rechargeUpLoad.setSerialNum(StringUtils.getSerial());
        rechargeUpLoad.setPayType(printerData.getPayType());
        rechargeUpLoad.setOperator_num((String) SPUtils.get(mContext, Constants.USER_NAME, ""));

        //这个地方支付与充值传的是一样
        if (data.getOrder_type().equals(PAY_FY_ALY)) {
            rechargeUpLoad.setPayType(Constants.PAY_WAY_ALY);
        } else if (data.getOrder_type().equals(PAY_FY_WX)) {
            rechargeUpLoad.setPayType(Constants.PAY_WAY_WX);
        } else if (data.getOrder_type().equals(PAY_FY_UNION)) {
            rechargeUpLoad.setPayType(Constants.PAY_WAY_UNIPAY);
        }
        rechargeUpLoad.setPromotion_num(tgy);
        rechargeUpLoad.setTransNo(printerData.getTransNo());
        rechargeUpLoad.setAuthCode(printerData.getAuthCode());

        rechargeUpload(rechargeUpLoad);


    }


    private void setFySmPayQurey1(FyQueryResponse data) {
        printerData.setMerchantName(MyApplication.getInstance().getLoginData().getFyMerchantName());
        printerData.setMerchantNo(MyApplication.getInstance().getLoginData().getFyMerchantNo());
        printerData.setTerminalId(StringUtils.getTerminalNo(StringUtils.getSerial()));
        printerData.setOperatorNo((String) SPUtils.get(this, Constants.USER_NAME, ""));
        printerData.setTransNo(data.getTransaction_id());
        printerData.setAuthCode(data.getMchnt_order_no());
        printerData.setDateTime(StringUtils.getCurTime());
        printerData.setOrderAmount(Integer.parseInt(actualAmount));
        printerData.setAmount(StringUtils.formatStrMoney(data.getOrder_amt()));
        printerData.setScanPayType(MyApplication.getInstance().getLoginData().getScanPayType());
        if (data.getOrder_type().equals(PAY_FY_ALY)) {
            printerData.setPayType(Constants.PAY_WAY_RECHARGE_ALY);
        } else if (data.getOrder_type().equals(PAY_FY_WX)) {
            printerData.setPayType(Constants.PAY_WAY_RECHARGE_WX);
        } else if (data.getOrder_type().equals(PAY_FY_UNION)) {
            printerData.setPayType(Constants.PAY_WAY_RECHARGE_UNIPAY);
        }

        printerData.setClientOrderNo(orderNo);


        //流水上送
        RechargeUpLoad rechargeUpLoad = new RechargeUpLoad();
        rechargeUpLoad.setSid(MyApplication.getInstance().getLoginData().getSid());
        rechargeUpLoad.setPayAmount(Integer.parseInt(oldAmount));
        rechargeUpLoad.setOrderNo(printerData.getClientOrderNo());
        rechargeUpLoad.setActivateCode(MyApplication.getInstance().getLoginData().getActiveCode());
        rechargeUpLoad.setMerchantNo(MyApplication.getInstance().getLoginData().getFyMerchantNo());
        rechargeUpLoad.setT(StringUtils.getdate2TimeStamp(printerData.getDateTime()));
        rechargeUpLoad.setSerialNum(StringUtils.getSerial());
        rechargeUpLoad.setPayType(printerData.getPayType());
        rechargeUpLoad.setOperator_num((String) SPUtils.get(mContext, Constants.USER_NAME, ""));

        //这个地方支付与充值传的是一样
        if (data.getOrder_type().equals(PAY_FY_ALY)) {
            rechargeUpLoad.setPayType(Constants.PAY_WAY_ALY);
        } else if (data.getOrder_type().equals(PAY_FY_WX)) {
            rechargeUpLoad.setPayType(Constants.PAY_WAY_WX);
        } else if (data.getOrder_type().equals(PAY_FY_UNION)) {
            rechargeUpLoad.setPayType(Constants.PAY_WAY_UNIPAY);
        }
        rechargeUpLoad.setPromotion_num(tgy);
        rechargeUpLoad.setTransNo(printerData.getTransNo());
        rechargeUpLoad.setAuthCode(printerData.getAuthCode());


        rechargeUpload(rechargeUpLoad);


    }


    private void setCashPrintData1(int oddChangeAmout, int payType) {
        printerData.setMerchantName(MyApplication.getInstance().getLoginData().getTerminalName());
        printerData.setMerchantNo(MyApplication.getInstance().getLoginData().getMerchantNo());
        printerData.setTerminalId(StringUtils.getSerial());
        printerData.setOperatorNo((String) SPUtils.get(this, Constants.USER_NAME, ""));
        printerData.setDateTime(StringUtils.getCurTime());
        printerData.setAmount(StringUtils.formatStrMoney(oldAmount));
        printerData.setReceiveAmount(StringUtils.formatStrMoney(actualAmount));
        printerData.setOddChangeAmout(StringUtils.formatIntMoney(oddChangeAmout));
        printerData.setPayType(payType);

    }

    /**
     * 现金
     *
     * @param oddChangeAmout
     */
    private void payCash1(int oddChangeAmout, int payType) {

        //设置打印信息
        setCashPrintData1(oddChangeAmout, payType);

        //打印订单号与流水上送统一
        printerData.setClientOrderNo(orderNo);

        //流水上送
        RechargeUpLoad rechargeUpLoad = new RechargeUpLoad();
        rechargeUpLoad.setSid(MyApplication.getInstance().getLoginData().getSid());
        rechargeUpLoad.setPayAmount(Integer.parseInt(oldAmount));
        rechargeUpLoad.setOrderNo(printerData.getClientOrderNo());
        rechargeUpLoad.setActivateCode(MyApplication.getInstance().getLoginData().getTerminalNo());
        rechargeUpLoad.setMerchantNo(MyApplication.getInstance().getLoginData().getMerchantNo());
        rechargeUpLoad.setT(StringUtils.getdate2TimeStamp(printerData.getDateTime()));
        rechargeUpLoad.setSerialNum(StringUtils.getSerial());
        rechargeUpLoad.setPayType(printerData.getPayType());
        rechargeUpLoad.setOperator_num((String) SPUtils.get(mContext, Constants.USER_NAME, ""));
        rechargeUpLoad.setPayType(Constants.PAY_WAY_CASH);
        rechargeUpLoad.setPromotion_num(tgy);

        rechargeUpload(rechargeUpLoad);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_ll_unionpay:
                CommonFunc.startResultAction(ZfPayRechargeActivity.this, CaptureActivity.class, null, REQUEST_CAPTURE_UNIPAY);
                break;
            case R.id.pay_wx:
                CommonFunc.startResultAction(ZfPayRechargeActivity.this, CaptureActivity.class, null, REQUEST_CAPTURE_WX);
                break;
            case R.id.pay_aly:
                CommonFunc.startResultAction(ZfPayRechargeActivity.this, CaptureActivity.class, null, REQUEST_CAPTURE_ALY);
                break;
            case R.id.pay_cash: {
                Bundle bundle = new Bundle();
                bundle.putString("amount", orderAmount.getText().toString());
                CommonFunc.startResultAction(this, ZfPayCashActivity.class, bundle, REQUEST_CASH);
            }
                break;
            case R.id.id_pay_flot:
                payflot1();
                break;
            case R.id.id_print:
                Printer.print(printerData, ZfPayRechargeActivity.this);
                break;

            case R.id.id_query:
                setLastQuerySend1();
                break;
            case R.id.id_finish:
            case R.id.id_terminal_query_sure:
                finish();
                break;
            default:
                break;
        }
    }



    /**
     * 刷卡
     */
    private void payflot1() {

        String mid = MyApplication.getInstance().getLoginData().getMerchantNo();
        String tid = MyApplication.getInstance().getLoginData().getTerminalNo();
        PayCommon.sale(this, Integer.parseInt(oldAmount), mid, tid, new PayCommon.ComTransResult<ComTransInfo>() {
            @Override
            public void success(ComTransInfo transInfo) {
                //设置打印的信息
                setFlotPrintData1(transInfo);



                printerData.setClientOrderNo(orderNo);


                //流水上送
                RechargeUpLoad rechargeUpLoad = new RechargeUpLoad();
                rechargeUpLoad.setSid(MyApplication.getInstance().getLoginData().getSid());
                rechargeUpLoad.setPayAmount(Integer.parseInt(oldAmount));
                rechargeUpLoad.setOrderNo(printerData.getClientOrderNo());
                rechargeUpLoad.setActivateCode(MyApplication.getInstance().getLoginData().getActiveCode());
                rechargeUpLoad.setMerchantNo(MyApplication.getInstance().getLoginData().getFyMerchantNo());
                rechargeUpLoad.setT(StringUtils.getdate2TimeStamp(printerData.getDateTime()));
                rechargeUpLoad.setSerialNum(StringUtils.getSerial());
                rechargeUpLoad.setPayType(printerData.getPayType());
                rechargeUpLoad.setOperator_num((String) SPUtils.get(mContext, Constants.USER_NAME, ""));


                rechargeUpLoad.setPayType(Constants.PAY_WAY_FLOT);

                rechargeUpLoad.setPromotion_num(tgy);
                rechargeUpLoad.setTransNo(printerData.getTransNo());
                rechargeUpLoad.setAuthCode(printerData.getAuthCode());


                rechargeUpload(rechargeUpLoad);

            }

            @Override
            public void failed(String error) {
                final CommonDialog confirmDialog = new CommonDialog(ZfPayRechargeActivity.this, error);
                confirmDialog.show();
                confirmDialog.setClicklistener(new CommonDialog.ClickListenerInterface() {
                    @Override
                    public void doConfirm() {

                    }
                });
            }
        });


    }



    protected void setFlotPrintData1(ComTransInfo transInfo) {
        printerData.setMerchantName(MyApplication.getInstance().getLoginData().getTerminalName());
        printerData.setMerchantNo(MyApplication.getInstance().getLoginData().getMerchantNo());//(transInfo.getMid());
        printerData.setTerminalId(transInfo.getTid());
        printerData.setOperatorNo((String) SPUtils.get(this, Constants.USER_NAME, ""));
        printerData.setAcquirer(transInfo.getAcquirerCode());
        printerData.setIssuer(transInfo.getIssuerCode());
        printerData.setCardNo(transInfo.getPan());
        printerData.setTransType(transInfo.getTransType() + "");
        printerData.setExpDate(transInfo.getExpiryDate());
        printerData.setBatchNO(StringUtils.fillZero(transInfo.getBatchNumber() + "", 6));
        printerData.setVoucherNo(StringUtils.fillZero(transInfo.getTrace() + "", 6));
        printerData.setDateTime(
                StringUtils.formatTime(StringUtils.getCurYear() + transInfo.getTransDate() + transInfo.getTransTime()));
        printerData.setAuthNo(transInfo.getAuthCode());
        printerData.setReferNo(transInfo.getRrn());
        printerData.setOrderAmount(Integer.parseInt(actualAmount));
        printerData.setAmount(StringUtils.formatIntMoney(transInfo.getTransAmount()));
        printerData.setPayType(Constants.PAY_WAY_RECHARGE_FLOT);
    }




    private void setLastQuerySend1() {

        switch (CommonFunc.recoveryFailureInfo(this).getPay_type()) {
            case Constants.PAY_WAY_RECHARGE_ALY:
            case Constants.PAY_WAY_RECHARGE_WX:
                if (CommonFunc.recoveryFailureInfo(this).getFaiureType() == Constants.FY_FAILURE_PAY) {
                    ZfFyPayQuery();
                } else if (CommonFunc.recoveryFailureInfo(this).getFaiureType() == Constants.FY_FAILURE_QUERY) {
                    ZfFyQuery();
                }
                break;
        }

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_flot_CASH: {
                int oddChangeAmout = data.getBundleExtra("bundle").getInt("oddChangeAmout");

                payCash1(oddChangeAmout, Constants.PAY_WAY_PAY_FLOT);
            }
                break;
            case REQUEST_CASH: {
                int oddChangeAmout = data.getBundleExtra("bundle").getInt("oddChangeAmout");

                payCash1(oddChangeAmout, Constants.PAY_WAY_RECHARGE_CASH);
            }
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
            case REQUEST_CAPTURE_UNIPAY:
                String result_uni = data.getExtras().getString(CaptureActivity.SCAN_RESULT);
                LogUtils.e("result", result_uni);
                FyUnionPay1(result_uni);
                break;
            default:
                break;
        }
    }


    private void FyWxPay1(String code) {
        printerData.setPayType(Constants.PAY_WAY_RECHARGE_WX);
        printerData.setClientOrderNo(orderNo);
        fybat.pay1(code, PAY_FY_WX, printerData.getClientOrderNo(), Integer.parseInt(oldAmount));
    }


    private void FyAlyPay1(String code) {
        printerData.setPayType(Constants.PAY_WAY_RECHARGE_ALY);
        printerData.setClientOrderNo(orderNo);
        fybat.pay1(code, PAY_FY_ALY, printerData.getClientOrderNo(), Integer.parseInt(oldAmount));
    }


    private void FyUnionPay1(String code) {
        printerData.setPayType(Constants.PAY_WAY_UNIPAY);
        printerData.setClientOrderNo(orderNo);
        fybat.pay1(code, PAY_FY_UNION, printerData.getClientOrderNo(), Integer.parseInt(oldAmount));
    }





    /**
     * 将流水上送的数据转成字串保存在打印的对象中
     * 不管成功失败，流水上送的数据保存下来
     *
     * @param request
     */
    private void setRechargeUpLoadData(RechargeUpLoad request) {
        Gson gson = new Gson();
        String data = gson.toJson(request);
        ALog.json(data);
        printerData.setRechargeUpload(data);
    }


    private void rechargeUpload(final RechargeUpLoad rechargeUpLoad){
        sbsAction.rechargePay(mContext, rechargeUpLoad, new ActionCallbackListener<ChargeBlance>() {
            @Override
            public void onSuccess(ChargeBlance data) {
                setRechargeUpLoadData(rechargeUpLoad);
                printerData.setPromotion_num(rechargeUpLoad.getPromotion_num());
                printerData.setPacektRemian(data.getPacektRemian());
                printerData.setRealize_card_num(data.getRealize_card_num());
                printerData.setMember_name(data.getMember_name());
                PrinterDataSave();

                showLayout();

                // 打印
                Printer.getInstance(mContext).print(printerData, mContext);
            }

            @Override
            public void onFailure(String errorEvent, String message) {
                ToastUtils.CustomShow(ZfPayRechargeActivity.this, errorEvent + "#" + message);
                showLayout();
                setRechargeUpLoadData(rechargeUpLoad);
                printerData.setUploadFlag(true);
                printerData.setApp_type(0);
                // 保存打印的数据，不保存图片数据
                PrinterDataSave();
                // 打印
                Printer.print(printerData, ZfPayRechargeActivity.this);
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
}
