package com.zfsbs.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tool.utils.msrcard.MsrCard;
import com.tool.utils.utils.NumberInputHelper;
import com.tool.utils.utils.StringUtils;
import com.tool.utils.utils.ToastUtils;
import com.tool.utils.view.MyGridView;
import com.yzq.testzxing.zxing.android.CaptureActivity;
import com.zfsbs.R;
import com.zfsbs.adapter.AdapterOilCardMeal;
import com.zfsbs.common.CommonFunc;
import com.zfsbs.core.myinterface.ActionCallbackListener;
import com.zfsbs.model.CardId;
import com.zfsbs.model.RechargeAmount;
import com.zfsbs.model.RechargeMeal;
import com.zfsbs.myapplication.MyApplication;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class RechargeActivity extends BaseActivity implements OnClickListener {

    public static final int REQUEST_CAPTURE = 0;

    private RechargeAmount vo;
    private List<RechargeAmount> list = new ArrayList<RechargeAmount>();

    private AdapterOilCardMeal adapter;
    private EditText etCardNo;
    private EditText etOperator;
    private EditText etAmount;
    private TextView tv;

    private RelativeLayout ll_amount;
    private LinearLayout ll_tv;
//    private LinearLayout ll_meal;
    private MyGridView gridview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_recharge_amount2);
//        AppManager.getAppManager().addActivity(this);
        initTitle("充值");
        initView();


    }


    private MsrCard.TrackData listener = new MsrCard.TrackData() {
        @Override
        public void onSuccess(String track2Data) {
//            String cardNumber = track2Data.substring(0, track2Data.indexOf("="));
            etCardNo.setText(track2Data);
            etCardNo.setSelection(etCardNo.length());
        }

        @Override
        public void onFail() {
            MsrCard.getMsrCard(mContext).closeMsrCard();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(200);
                        MsrCard.getMsrCard(mContext).openMsrCard(listener);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }).start();

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MsrCard.getMsrCard(mContext).closeMsrCard();
    }


    private void initView() {

        etCardNo = (EditText) findViewById(R.id.id_phoneNo);
        etOperator = (EditText) findViewById(R.id.id_tgy);
        etAmount = (EditText) findViewById(R.id.id_enter_amount);
        NumberInputHelper.format(etAmount, 2);
        etAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (StringUtils.changeY2F(etAmount.getText().toString().trim()).length() > 10){
                    ToastUtils.CustomShow(mContext, "金额过大");
                    etAmount.setText(s.subSequence(0, s.length()-1));
                    etAmount.setSelection(s.length()-1);
                    return;
                }
                if (list.size() > 0){
                    long temp_amt = Long.parseLong(StringUtils.changeY2F(etAmount.getText().toString().trim()));
                    tv.setText(StringUtils.formatIntMoney(setPercentageInfo(temp_amt)));
                }else{
                    tv.setText(StringUtils.formatIntMoney(StringUtils.stringToInt(StringUtils.getDouble(etAmount.getText().toString().trim()) + "")));
                }
            }
        });


        tv = (TextView) findViewById(R.id.id_tv_amount);

        ll_amount = (RelativeLayout) findViewById(R.id.id_ll_amount);
//        ll_meal = (LinearLayout) findViewById(R.id.id_ll_meal);
        ll_tv = (LinearLayout) findViewById(R.id.id_ll_tv);


        button(R.id.id_btn_recharge).setOnClickListener(this);
        imageView(R.id.id_scan).setOnClickListener(this);

        gridview = (MyGridView) findViewById(R.id.gridview);
        adapter = new AdapterOilCardMeal(list, mContext);
        gridview.setAdapter(adapter);


        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                vo = list.get(position);
                for (int i = 0; i < list.size(); i++) {
                    list.get(i).setIsdefault(0);
                }
                vo.setIsdefault(1);
                adapter.notifyDataSetChanged();


            }
        });
    }


    private long setPercentageInfo(long amt){

        Collections.sort(list, new Comparator<RechargeAmount>() {
            @Override
            public int compare(RechargeAmount o1, RechargeAmount o2) {
                return o2.getReal_pay_money() - o1.getReal_pay_money();
            }
        });

        for (int i = 0; i < list.size(); i++){
            if (amt >= list.get(i).getReal_pay_money()){

                BigDecimal percentage = new BigDecimal(list.get(i).getReal_get_money()).divide(new BigDecimal(10000), 2, BigDecimal.ROUND_FLOOR);

                return new BigDecimal(amt).multiply(percentage.add(new BigDecimal(1))).longValue();//amt + StringUtils.stringToInt(amountToPoint+"");
            }
        }

        return amt;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_btn_recharge:
                if (StringUtils.isEmpty(etCardNo.getText().toString().trim())) {
                    ToastUtils.CustomShow(mContext, "卡号或手机号不为空");
                    return;
                }

                if (ll_amount.getVisibility() == View.VISIBLE){
                    if (StringUtils.isBlank(etAmount.getText().toString().trim())){
                        ToastUtils.CustomShow(mContext, "请输入金额");
                        return;
                    }
                }

                loadRechargeSureData();
                break;
            case R.id.id_scan:
                CommonFunc.startResultAction(RechargeActivity.this, CaptureActivity.class, null, REQUEST_CAPTURE);
                break;
            default:
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_CAPTURE:
                // 处理扫描结果（在界面上显示）
                String phoneNo = data.getStringExtra(CaptureActivity.SCAN_RESULT);
                etCardNo.setText(phoneNo);
                break;

            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadData();
    }

    private void loadData() {
        Long sid = MyApplication.getInstance().getLoginData().getSid();
        sbsAction.recharge(mContext, sid, new ActionCallbackListener<RechargeMeal>() {
            @Override
            public void onSuccess(RechargeMeal data) {

                //固定金额送
                if (data.getRechargeType() == 1 && data.getCombineInfo() != null){
                    if (data.getCombineInfo().size() <= 0) {
                        ToastUtils.CustomShow(mContext, "获取充值金额失败");
                        return;
                    }
                    list.clear();
                    list.addAll(data.getCombineInfo());
                    list.get(0).setIsdefault(1);
                    adapter.notifyDataSetChanged();
                    vo = list.get(0);
                    ll_amount.setVisibility(View.GONE);
                    ll_tv.setVisibility(View.GONE);
                    gridview.setVisibility(View.VISIBLE);
                }else if (data.getRechargeType() == 2 && data.getPercentageInfo() != null){
                    //按百分比赠送
                    if (data.getPercentageInfo().size() <= 0) {
                        ToastUtils.CustomShow(mContext, "获取充值金额失败");
                        return;
                    }
                    list.clear();
                    list.addAll(data.getPercentageInfo());
                    ll_amount.setVisibility(View.VISIBLE);
                    ll_tv.setVisibility(View.VISIBLE);
                    gridview.setVisibility(View.GONE);
                }else if (data.getRechargeType() == 3){
                    list.clear();
                    ll_tv.setVisibility(View.VISIBLE);
                    ll_amount.setVisibility(View.VISIBLE);
                    gridview.setVisibility(View.GONE);
                }


                MsrCard.getMsrCard(mContext).openMsrCard(listener);
            }



            @Override
            public void onFailure(String errorEvent, String message) {
                ToastUtils.CustomShow(mContext, message);
            }

            @Override
            public void onFailurTimeOut(String s, String error_msg) {

            }

            @Override
            public void onLogin() {

            }
        });


    }







    private void loadRechargeSureData(){


        if (StringUtils.changeY2F(etAmount.getText().toString().trim()).length() > 10){
            ToastUtils.CustomShow(mContext, "金额过大");
            return;
        }

        long amount = Long.parseLong(StringUtils.changeY2F(etAmount.getText().toString().trim()));

        Long sid = MyApplication.getInstance().getLoginData().getSid();
        String cardNo = etCardNo.getText().toString().trim();
        final String orderNo = CommonFunc.getNewClientSn();
        sbsAction.rechargeSure(mContext, sid, amount, orderNo, cardNo, new ActionCallbackListener<CardId>() {
            @Override
            public void onSuccess(CardId data) {
                MsrCard.getMsrCard(mContext).closeMsrCard();
                Bundle bundle = new Bundle();
//                bundle.putSerializable("RechargeAmount", vo);
                bundle.putString("orderNo", orderNo);
                bundle.putString("cardNo", etCardNo.getText().toString().trim());
                bundle.putString("actualAmount", data.getActualAmount());
                bundle.putString("oldAmount", StringUtils.changeY2F(etAmount.getText().toString().trim()));
                bundle.putString("tgy", etOperator.getText().toString().trim());
//                bundle.putString("card_id", data.getActualAmount());

                startActivity(new Intent(mContext, ZfPayRechargeActivity.class).putExtra("data", bundle));

                finish();
            }

            @Override
            public void onFailure(String errorEvent, String message) {
                ToastUtils.CustomShow(mContext, message);
            }

            @Override
            public void onFailurTimeOut(String s, String error_msg) {

            }

            @Override
            public void onLogin() {

            }
        });
    }


}
