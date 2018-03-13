package com.zfsbs.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tool.utils.utils.StringUtils;
import com.tool.utils.utils.ToastUtils;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.zfsbs.R;
import com.zfsbs.common.CommonFunc;
import com.zfsbs.core.myinterface.ActionCallbackListener;
import com.zfsbs.model.TicektResponse;
import com.zfsbs.myapplication.MyApplication;
import com.zfsbs.view.MyDialog;


public class VerificationActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout ll;
//    private TextView tType;
    private TextView tName;
    private TextView tOldPrice;
    private TextView tPayPrice;
//    private TextView tGet;
    private TextView tStatus;
    private EditText tNo;
//    private int status;

    private Button id_sure;
    private Button id_cz;

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
        button(R.id.id_cz).setOnClickListener(this);
        ll = linearLayout(R.id.id_ll_ticket);
//        tType = textView(R.id.id_ticket_type);
        tName = textView(R.id.id_ticket_name);
        tOldPrice = textView(R.id.id_ticket_old_price);
        tPayPrice = textView(R.id.id_ticket_pay_price);
//        tGet = textView(R.id.id_ticket_get);
        tStatus = textView(R.id.id_ticket_status);

        imageView(R.id.id_scan).setOnClickListener(this);

        id_sure = button(R.id.id_sure);
        id_cz = button(R.id.id_cz);

    }

    private void reset(){
        tName.setText("");
        tOldPrice.setText("");
        tPayPrice.setText("");
        tStatus.setText("");
        tNo.setText("");
        ll.setVisibility(View.GONE);
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
            case R.id.id_cz:
                if (StringUtils.isEmpty(tNo.getText().toString().trim())){
                    ToastUtils.CustomShow(this, "请输入券码或扫码");
                    return;
                }
                commitTicketRefund();
                break;
            case R.id.id_sure:
                if (StringUtils.isEmpty(tNo.getText().toString().trim())){
                    ToastUtils.CustomShow(this, "请输入券码或扫码");
                    return;
                }
                commitTicket();
                break;
            case R.id.id_scan:
                CommonFunc.startResultAction(VerificationActivity.this, CaptureActivity.class, null, 1);
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
                String result = data.getExtras().getString(CodeUtils.RESULT_STRING);
                tNo.setText(result);
                checkTicket();
                break;
            default:
                break;
        }
    }


    private void commitTicketRefund() {
        Long sid = MyApplication.getInstance().getLoginData().getSid();
        String sn = StringUtils.getSerial();
        String ticketNo = tNo.getText().toString().trim();

        sbsAction.ticketRefund(this, sid, ticketNo, sn, new ActionCallbackListener<String>() {
            @Override
            public void onSuccess(String data) {
                ToastUtils.CustomShow(VerificationActivity.this, data);
//                onBackPressed();
                reset();
            }

            @Override
            public void onFailure(String errorEvent, String message) {
//                ToastUtils.CustomShow(VerificationActivity.this, message);
                MyDialog.Builder builder = new MyDialog.Builder(mContext);
                builder.setTitle("提示");
                builder.setMessage(message);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }

            @Override
            public void onFailurTimeOut(String s, String error_msg) {

            }

            @Override
            public void onLogin() {

            }
        });
    }

    private void commitTicket() {
        Long sid = MyApplication.getInstance().getLoginData().getSid();
        String sn = StringUtils.getSerial();
        String ticketNo = tNo.getText().toString().trim();
        String orderNo = CommonFunc.getNewClientSn();

        sbsAction.ticketPay(this, sid, ticketNo, sn, orderNo, new ActionCallbackListener<String>() {
            @Override
            public void onSuccess(String data) {
                ToastUtils.CustomShow(VerificationActivity.this, data);
//                onBackPressed();
                reset();
            }

            @Override
            public void onFailure(String errorEvent, String message) {
//                ToastUtils.CustomShow(VerificationActivity.this, message);
                MyDialog.Builder builder = new MyDialog.Builder(mContext);
                builder.setTitle("提示");
                builder.setMessage(message);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
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

        Long sid = MyApplication.getInstance().getLoginData().getSid();
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
//                tType.setText(data.getTicketType());
                tName.setText(data.getGoodName());
                /**
                 * 这个地方服务端是反的，所以写反
                 */
                tPayPrice.setText(StringUtils.formatIntMoney(data.getOldAmount()));
                tOldPrice.setText(StringUtils.formatIntMoney(data.getPayAmount()));
//                tGet.setText(data.getGetWay());
                tStatus.setText(data.getStatusName());
//                status = data.getStatus();

                if (data.getStatus() == 2){
                    id_sure.setVisibility(View.GONE);
                }else{
                    id_sure.setVisibility(View.VISIBLE);
                }

                if (data.isCorrect()){
                    id_cz.setVisibility(View.GONE);
                }else{
                    id_cz.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(String errorEvent, String message) {
//                ToastUtils.CustomShow(VerificationActivity.this, message);
                MyDialog.Builder builder = new MyDialog.Builder(mContext);
                builder.setTitle("提示");
                builder.setMessage(message);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
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
