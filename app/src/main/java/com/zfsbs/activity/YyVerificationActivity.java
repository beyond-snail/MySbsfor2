package com.zfsbs.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hd.enums.EnumConsts;
import com.tool.utils.utils.StringUtils;
import com.tool.utils.utils.ToastUtils;
import com.yzq.testzxing.zxing.android.CaptureActivity;
import com.zfsbs.R;
import com.zfsbs.common.CommonFunc;
import com.zfsbs.config.EnumConstsSbs;
import com.zfsbs.core.myinterface.ActionCallbackListener;
import com.zfsbs.model.TicektResponse;
import com.zfsbs.model.YyTicektResponse;
import com.zfsbs.myapplication.MyApplication;

import java.util.Date;


public class YyVerificationActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout ll;
    private TextView tType;
    private TextView tName;
    private TextView tPayPrice;
    private TextView tStatus;
    private EditText tNo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_yy_veriflation);
        initTitle("优惠券核销");
        initView();
    }

    private void initView() {
        tNo = editText(R.id.id_ticket_no);
        button(R.id.id_ticket_check).setOnClickListener(this);
        button(R.id.id_sure).setOnClickListener(this);
        ll = linearLayout(R.id.id_ll_ticket);
        tType = textView(R.id.id_ticket_type);
        tName = textView(R.id.id_ticket_name);
        tPayPrice = textView(R.id.id_ticket_pay_price);
        tStatus = textView(R.id.id_ticket_status);

        imageView(R.id.id_scan).setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.id_ticket_check:
                if (StringUtils.isEmpty(tNo.getText().toString().trim())){
                    ToastUtils.CustomShow(this, "请输入券码或扫码");
                    return;
                }
                checkTicket();
                break;
            case R.id.id_sure:
                if (StringUtils.isEmpty(tNo.getText().toString().trim())){
                    ToastUtils.CustomShow(this, "请输入券码或扫码");
                    return;
                }
//                commitTicket();

                Intent mIntent = new Intent();
                mIntent.putExtra("name", tName.getText().toString());
                mIntent.putExtra("amount", StringUtils.changeY2F(tPayPrice.getText().toString()));
                mIntent.putExtra("yyId", yyId);
                mIntent.putExtra("couponCode",StringUtils.removeBlank(tNo.getText().toString().trim(), ' '));
                // 设置结果，并进行传送
                this.setResult(RESULT_OK, mIntent);
                finish();

                break;
            case R.id.id_scan:
                CommonFunc.startResultAction(YyVerificationActivity.this, CaptureActivity.class, null, 1);
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case 1:
                String result = data.getExtras().getString(CaptureActivity.SCAN_RESULT);
                tNo.setText(result);
                checkTicket();
                break;
            default:
                break;
        }
    }

//    private void commitTicket() {
//        int sid = MyApplication.getInstance().getLoginData().getSid();
//        String sn = StringUtils.getSerial();
//        String ticketNo = tNo.getText().toString().trim();
//        String orderNo = CommonFunc.getNewClientSn();
//
//        sbsAction.ticketPay(this, sid, ticketNo, sn, orderNo, new ActionCallbackListener<String>() {
//            @Override
//            public void onSuccess(String data) {
//                ToastUtils.CustomShow(YyVerificationActivity.this, data);
//                onBackPressed();
//            }
//
//            @Override
//            public void onFailure(String errorEvent, String message) {
//                ToastUtils.CustomShow(YyVerificationActivity.this, message);
//            }
//
//            @Override
//            public void onFailurTimeOut(String s, String error_msg) {
//
//            }
//
//            @Override
//            public void onLogin() {
//
//            }
//        });
//    }

    private Long yyId;
    private void checkTicket() {

        Long sid = MyApplication.getInstance().getLoginData().getSid();
        String ticketNo = StringUtils.removeBlank(tNo.getText().toString().trim(), ' ');

        sbsAction.yyticketcheck(this, sid, ticketNo, new ActionCallbackListener<YyTicektResponse>() {
            @Override
            public void onSuccess(YyTicektResponse data) {
                if (data == null){
                    ToastUtils.CustomShow(YyVerificationActivity.this, "无券信息");
                    return;
                }

                Date startTime = StringUtils.getDateFromString(data.getStartTime(), "yyyy-MM-dd HH:mm:ss");
                Date endTime = StringUtils.getDateFromString(data.getEndTime(), "yyyy-MM-dd HH:mm:ss");
                Date curTime = new Date();
                if (curTime.getTime() < startTime.getTime() || curTime.getTime() > endTime.getTime()){
                    ToastUtils.CustomShow(mContext, "该券不在核销时间段内");
                    return;
                }
                if (data.getStatus() == EnumConstsSbs.CouponUseStatus.Verified.getType() || data.getStatus() == EnumConstsSbs.CouponUseStatus.locked.getType()){
                    ToastUtils.CustomShow(mContext, "该券已核销或已锁定");
                    return;
                }

                ll.setVisibility(View.VISIBLE);
                tType.setText("异业优惠券");
                tName.setText(data.getName());
                tPayPrice.setText(StringUtils.formatIntMoney(data.getValue().intValue()));
                tStatus.setText(EnumConstsSbs.CouponUseStatus.fromType(data.getStatus()).getName()+"");
                yyId = data.getId();


            }

            @Override
            public void onFailure(String errorEvent, String message) {
                ToastUtils.CustomShow(YyVerificationActivity.this, message);
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
