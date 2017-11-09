package com.zfsbs.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.tool.utils.msrcard.MsrCard;
import com.tool.utils.utils.AlertUtils;
import com.tool.utils.utils.StringUtils;
import com.tool.utils.utils.ToastUtils;
import com.zfsbs.R;
import com.zfsbs.common.CommonFunc;
import com.zfsbs.core.myinterface.ActionCallbackListener;
import com.zfsbs.model.ApiResponse;
import com.zfsbs.model.VipCardNo;
import com.zfsbs.myapplication.MyApplication;


public class OpenCardActivity extends BaseActivity implements View.OnClickListener {

    private EditText etName;
    private EditText etPhone;
    private EditText etCard;


    private static final int USER_SEARCH = 0;
    private static final int USER_ADD = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_open_card);
//        AppManager.getAppManager().addActivity(this);

        initTitle("开卡/绑卡");
        if (findViewById(R.id.add) != null) {
            findViewById(R.id.add).setVisibility(View.VISIBLE);
            findViewById(R.id.add).setOnClickListener(this);
        }


        initView();

        MsrCard.getMsrCard(mContext).openMsrCard(listener);

    }





    private MsrCard.TrackData listener = new MsrCard.TrackData() {
        @Override
        public void onSuccess(String track2Data) {
//            if (track2Data.length() < 5){
//                return;
//            }
//            String cardNumber = track2Data.substring(0, track2Data.indexOf("="));
            etCard.setText(track2Data);
            etCard.setSelection(etCard.length());

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

    private void initView() {

        etName = editText(R.id.id_member_name);
        etName.setSelection(etName.getText().length());
        etPhone = editText(R.id.id_memberphoneNo);
        etPhone.setSelection(etPhone.getText().length());
        etCard = editText(R.id.id_memberCardNo);
        etCard.setSelection(etCard.getText().length());


        button(R.id.id_bind_card).setOnClickListener(this);
        button(R.id.id_change_card).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_bind_card:
                openRealizeCard();
                break;
            case R.id.id_change_card:
                changeCard();
                break;
            case R.id.add:
                CommonFunc.startAction(this, CardChangeActivity.class, false);


                break;
            default:
                break;
        }
    }

    private void changeCard() {
        Long sid = MyApplication.getInstance().getLoginData().getSid();
//        String memberName = etName.getText().toString().trim();
        final String memberCard = etCard.getText().toString().trim();
        final String memberPhone = etPhone.getText().toString().trim();

        if (StringUtils.isBlank(memberCard)) {
            ToastUtils.CustomShow(mContext, "会员卡号不为空");
            return;
        }

        sbsAction.changeCard(mContext, sid, memberPhone, memberCard, new ActionCallbackListener<ApiResponse<VipCardNo>>() {

            @Override
            public void onSuccess(ApiResponse<VipCardNo> response) {
                AlertUtils.alert2("提示", response.getResult().getResMsg(), mContext, "确定", "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();
                        onBackPressed();

                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        onBackPressed();

                    }
                }, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        onBackPressed();

                    }
                }, null, false, false, 1);
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        MsrCard.getMsrCard(mContext).closeMsrCard();
    }

    private void openRealizeCard() {

        Long sid = MyApplication.getInstance().getLoginData().getSid();
        String memberName = etName.getText().toString().trim();
        final String memberCard = etCard.getText().toString().trim();
        final String memberPhone = etPhone.getText().toString().trim();

        if (StringUtils.isBlank(memberCard)) {
            ToastUtils.CustomShow(mContext, "会员卡号不为空");
            return;
        }


        sbsAction.openCard(mContext, sid, memberPhone, memberCard, memberName, new ActionCallbackListener<ApiResponse<VipCardNo>>() {
            @Override
            public void onSuccess(final ApiResponse<VipCardNo> response) {

                AlertUtils.alert2("提示", response.getResult().getResMsg(), mContext, "确定", "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();
                        if (response.getResult().getResCode() == 2) {
                            onBackPressed();
                        } else if (response.getResult().getResCode() == 100) {
                            //确认是否绑定
                            ComfirmBindCard(memberCard, memberPhone);
                        }
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        if (response.getResult().getResCode() == 20) {
                            onBackPressed();
                        }
                    }
                }, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        if (response.getResult().getResCode() == 20) {
                            onBackPressed();
                        }
                    }
                }, null, false, false, 1);
            }

            @Override
            public void onFailure(String errorEvent, String message) {
                ToastUtils.CustomShow(mContext, message);
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

            @Override
            public void onFailurTimeOut(String s, String error_msg) {

            }

            @Override
            public void onLogin() {

            }
        });
    }

    private void ComfirmBindCard(String memberCard, String memberPhone) {
        Long sid = MyApplication.getInstance().getLoginData().getSid();
        sbsAction.confirmBindCard(mContext, sid, memberPhone, memberCard, new ActionCallbackListener<ApiResponse<VipCardNo>>() {

            @Override
            public void onSuccess(ApiResponse<VipCardNo> response) {
                AlertUtils.alert2("提示", response.getResult().getResMsg(), mContext, "确定", "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();
                        onBackPressed();

                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        onBackPressed();

                    }
                }, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        onBackPressed();

                    }
                }, null, false, false, 1);
            }

            @Override
            public void onFailure(String errorEvent, String message) {
                ToastUtils.CustomShow(mContext, message);
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

            @Override
            public void onFailurTimeOut(String s, String error_msg) {

            }

            @Override
            public void onLogin() {

            }
        });
    }

}
