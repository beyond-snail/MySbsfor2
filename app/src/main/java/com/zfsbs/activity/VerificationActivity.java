package com.zfsbs.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tool.utils.utils.StringUtils;
import com.tool.utils.utils.ToastUtils;
import com.zfsbs.R;
import com.zfsbs.core.myinterface.ActionCallbackListener;
import com.zfsbs.model.TicektResponse;
import com.zfsbs.myapplication.MyApplication;


public class VerificationActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout ll;
    private TextView tType;
    private TextView tName;
    private TextView tOldPrice;
    private TextView tPayPrice;
    private TextView tGet;
    private TextView tStatus;
    private EditText tNo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_veriflation);
        initTitle("券码核销");
        initView();
    }

    private void initView() {
        tNo = editText(R.id.id_ticket_no);
        button(R.id.id_ticket_check).setOnClickListener(this);
        button(R.id.id_sure).setOnClickListener(this);
        ll = linearLayout(R.id.id_ll_ticket);
        tType = textView(R.id.id_ticket_type);
        tName = textView(R.id.id_ticket_name);
        tOldPrice = textView(R.id.id_ticket_old_price);
        tPayPrice = textView(R.id.id_ticket_pay_price);
        tGet = textView(R.id.id_ticket_get);
        tStatus = textView(R.id.id_ticket_status);

        imageView(R.id.id_scan);

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
                commitTicket();
                break;
        }
    }

    private void commitTicket() {
        int sid = MyApplication.getInstance().getLoginData().getSid();
        String sn = StringUtils.getSerial();
        String ticketNo = tNo.getText().toString().trim();

        sbsAction.ticketPay(this, sid, ticketNo, sn, new ActionCallbackListener<String>() {
            @Override
            public void onSuccess(String data) {
                ToastUtils.CustomShow(VerificationActivity.this, data);
                onBackPressed();
            }

            @Override
            public void onFailure(String errorEvent, String message) {
                ToastUtils.CustomShow(VerificationActivity.this, message);
            }

            @Override
            public void onFailurTimeOut(String s, String error_msg) {

            }

            @Override
            public void onLogin() {

            }
        });
    }

    private void checkTicket() {

        int sid = MyApplication.getInstance().getLoginData().getSid();
        String sn = StringUtils.getSerial();
        String ticketNo = tNo.getText().toString().trim();

        sbsAction.ticketcheck(this, sid, ticketNo, sn, new ActionCallbackListener<TicektResponse>() {
            @Override
            public void onSuccess(TicektResponse data) {
                if (data == null){
                    ToastUtils.CustomShow(VerificationActivity.this, "无券信息");
                    return;
                }
                ll.setVisibility(View.VISIBLE);
                tType.setText(data.getTicketType());
                tName.setText(data.getTicketName());
                tOldPrice.setText(data.getOldAmount());
                tPayPrice.setText(data.getPayAmount());
                tGet.setText(data.getGetWay());
                tStatus.setText(data.getStatus());
            }

            @Override
            public void onFailure(String errorEvent, String message) {
                ToastUtils.CustomShow(VerificationActivity.this, message);
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
