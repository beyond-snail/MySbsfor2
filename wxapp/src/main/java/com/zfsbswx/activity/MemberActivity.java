package com.zfsbswx.activity;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.tool.utils.activityManager.AppManager;
import com.tool.utils.dialog.MemberDialog;
import com.tool.utils.dialog.PassWordDialog;
import com.tool.utils.utils.Arith;
import com.tool.utils.utils.Base64Utils;
import com.tool.utils.utils.LogUtils;
import com.tool.utils.utils.StringUtils;
import com.tool.utils.utils.ToastUtils;
import com.zfsbswx.R;
import com.zfsbswx.common.CommonFunc;
import com.zfsbswx.config.Config;
import com.zfsbswx.core.myinterface.ActionCallbackListener;
import com.zfsbswx.model.Coupons;
import com.zfsbswx.model.CouponsResponse;
import com.zfsbswx.model.MemberTransAmountRequest;
import com.zfsbswx.model.MemberTransAmountResponse;
import com.zfsbswx.myapplication.MyApplication;
import com.zfsbswx.view.ConponsDialog;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MemberActivity extends BaseActivity implements View.OnClickListener {

    private TextView tMemberName;
    private TextView tMemberCardNo;
    private TextView tMemberPhoneNo;
    private TextView tDoPoint;
    private EditText etUsedPoint;
    private TextView tPointAmount;
    private TextView tIsUsedPoint;
    private TextView tUseCouponsNum;
    private TextView tCouponsInfo;
    private TextView tShowUsedCoupons;
    private Button btNext;
    private Button btNoUsed;

    private CouponsResponse couponResponse;
    private int amount;
    private int pointChangeRate;
    private int point;
    private int frequency_min; //积分最小使用下限
    private int pointMin; // 用来判断最小的使用积分
    private String pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_info);
//        AppManager.getAppManager().addActivity(this);
        initView();
        initData();
        addListener();
    }

    private void initView() {
        tMemberName = (TextView) findViewById(R.id.id_member_name);
        tMemberCardNo = (TextView) findViewById(R.id.id_memberCardNo);
        tMemberPhoneNo = (TextView) findViewById(R.id.id_phoneNo);
        tDoPoint = (TextView) findViewById(R.id.id_do_point);
        etUsedPoint = (EditText) findViewById(R.id.id_use_point);
        etUsedPoint.setCursorVisible(false);// 隐藏光标
        tPointAmount = (TextView) findViewById(R.id.id_point_amount);
        tIsUsedPoint = (TextView) findViewById(R.id.id_isUsed_point);
        tUseCouponsNum = (TextView) findViewById(R.id.id_coupon_num);
        tCouponsInfo = (TextView) findViewById(R.id.id_coupon_info);
        tShowUsedCoupons = (TextView) findViewById(R.id.id_showCouponNum);
        btNext = (Button) findViewById(R.id.id_next);
        btNoUsed = (Button) findViewById(R.id.id_no_used);
    }

    private void initData() {
        Bundle bundle = new Bundle();
        bundle = getIntent().getExtras();
        BigDecimal big = new BigDecimal(bundle.getString("amount"));
        amount = big.multiply(new BigDecimal(100)).intValue();
        couponResponse = (CouponsResponse) bundle.getSerializable("member");
        pass = ""; // 设置默认为空

        if (couponResponse != null) {
            tMemberName.setText(couponResponse.getMemberName());
            tMemberCardNo.setText(couponResponse.getMemberCardNo());
            tMemberPhoneNo.setText(couponResponse.getMobile());
            tDoPoint.setText(couponResponse.getPoint() + "点积分");
            tUseCouponsNum.setText(couponResponse.getCouponNum() + "张");
            tPointAmount.setText("可抵扣0元");
            pointChangeRate = couponResponse.getPointChangeRate();
            frequency_min = couponResponse.getFrequency_min();

            // 将金额转换成积分值
//            int amountToPoint = (int) ((long) amount * pointChangeRate / 100);
//            double amountBig = Arith.mul(amount, pointChangeRate);


//                String amountToPoint = Arith.div2Int(amountBig, 100, 0);
            double amountBig = Arith.mul(amount, pointChangeRate);
            double amountToPoint = Arith.divide(amountBig, 100);
            LogUtils.e("amountToPoint:" + amountToPoint);
//                pointMin = StringUtils.min(amountToPoint, couponResponse.getPoint(), couponResponse.getPointUseMax());
//            LogUtils.e("pointMin:" + pointMin);


            pointMin = (int) Math.floor(StringUtils.min(amountToPoint, (double) couponResponse.getPoint(), (double) couponResponse.getPointUseMax()));

            LogUtils.e("pointMin:" + pointMin);
            showCouponsChecked();
        }
    }


    private void addListener() {
        btNext.setOnClickListener(MemberActivity.this);
        btNoUsed.setOnClickListener(this);
        tIsUsedPoint.setOnClickListener(this);
        etUsedPoint.addTextChangedListener(new TextWatcherImpl());
        tCouponsInfo.setOnClickListener(this);
    }

    private class TextWatcherImpl implements TextWatcher {
        private boolean isChanged = false;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            LogUtils.e("beforeTextChanged:" + s.toString());
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            tIsUsedPoint.setSelected(false);
            point = 0;

//            LogUtils.e("onTextChanged:" + s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {
            double showPoint = 0;
            if (isChanged) {// ----->如果字符未改变则返回
                return;
            }
            isChanged = true;
            if (pointChangeRate != 0 && s.toString().length() > 0 && StringUtils.isNumeric(s.toString())) {
                showPoint = (Double.parseDouble(s.toString()));
                LogUtils.e("showPoint:" + showPoint);
                if (showPoint > pointMin) {
                    ToastUtils.CustomShow(MemberActivity.this, "最大使用积分:" + pointMin + "积分");
                    etUsedPoint.setText(pointMin + "");
                    etUsedPoint.setSelection(etUsedPoint.getText().toString().length());

                    double temp = Arith.mul(pointMin, 100);
                    double temp1 = Arith.divide(temp, pointChangeRate);

                    tPointAmount.setText("可抵用" + StringUtils.formatIntMoney((int) temp1) + "元");


                    tIsUsedPoint.setSelected(true);
                    point = pointMin;//(int) (Double.parseDouble(etUsedPoint.getText().toString()));
                    isChanged = false;
                    return;
                } else {

                    double temp = Arith.mul(showPoint, 100);
                    double temp1 = Arith.divide(temp, pointChangeRate);

                    tPointAmount.setText("可抵用" + StringUtils.formatIntMoney((int) temp1) + "元");
                    tIsUsedPoint.setSelected(true);
                    point = (int) (Double.parseDouble(etUsedPoint.getText().toString()));
                }
            } else {
                tIsUsedPoint.setSelected(false);
                tPointAmount.setText("可抵用" + 0 + "元");
            }
            isChanged = false;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_next:
                if (tIsUsedPoint.isSelected()) {
                    if (point < frequency_min) {
                        ToastUtils.CustomShow(MemberActivity.this, "最小使用积分:" + frequency_min + "积分");
                        break;
                    }
                }
                if (IsInputPass()) {
                    memberTransAmountAction();
                }
//                if (IsMember()) {
//                    if (IsInputPass()) {
//                        memberTransAmountAction();
//                    }
//                }
                break;
            case R.id.id_no_used:
//                Bundle bundle = new Bundle();
//                bundle.putString("amount", StringUtils.formatIntMoney(amount));
//                if (couponResponse.isMember()) {
//                    bundle.putString("member", couponResponse.getMemberCardNo());
//                }
//                startAction(this, ZfPayActivity.class, bundle, true);


                MemberTransAmountResponse member = new MemberTransAmountResponse();
                member.setRealMoney(amount);
                member.setTradeMoney(amount);
                member.setMemberCardNo(couponResponse.isMember() ? couponResponse.getMemberCardNo() : "");
                CommonFunc.setBackMemberInfo(this, member);
                CommonFunc.startAction(this, ZfPayActivity.class, true);
                break;
            case R.id.id_isUsed_point:
                if (tIsUsedPoint.isSelected()) {
                    tIsUsedPoint.setSelected(false);
                    point = 0;
                } else {
                    if (etUsedPoint.getText().toString().length() > 0) {
                        tIsUsedPoint.setSelected(true);
                        point = (int) (Double.parseDouble(etUsedPoint.getText().toString()));
                    }
                }
                break;
            case R.id.id_coupon_info:
                showCouponsInfo();
                break;
            default:
                break;
        }
    }


    private void memberTransAmountAction() {
        MemberTransAmountRequest request = new MemberTransAmountRequest();
        request.setSid(MyApplication.getInstance().getLoginData().getSid());
        request.setMemberCardNo(couponResponse.getMemberCardNo());
        request.setPassword(pass);
        request.setTradeMoney(amount);
        request.setPoint(point);
        request.setCouponSn(getSn());
        request.setMemberName(couponResponse.getMemberName());

        this.sbsAction.memberTransAmount(MemberActivity.this, request, new ActionCallbackListener<MemberTransAmountResponse>() {
            @Override
            public void onSuccess(MemberTransAmountResponse data) {
//                Bundle bundle = new Bundle();
//                data.setPoint((int) point);
//                data.setPass(pass);
//                bundle.putSerializable("memberTransAmount", data);
//                startAction(MemberActivity.this, ZfPayActivity.class, bundle, true);

                data.setPoint(point);
                data.setPass(pass);
                CommonFunc.setBackMemberInfo(MemberActivity.this, data);
                CommonFunc.startAction(MemberActivity.this, ZfPayActivity.class, true);
            }

            @Override
            public void onFailure(String errorEvent, String message) {
                ToastUtils.CustomShow(MemberActivity.this, message);
            }

            @Override
            public void onFailurTimeOut(String s, String error_msg) {

            }

            @Override
            public void onLogin() {
                AppManager.getAppManager().finishAllActivity();
                if (Config.OPERATOR_UI_BEFORE) {
                    CommonFunc.startAction(MemberActivity.this, OperatorLoginActivity.class, false);
                } else {
                    CommonFunc.startAction(MemberActivity.this, OperatorLoginActivity1.class, false);
                }
            }
        });
    }


    private Handler mhandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    for (int i = 0; i < couponResponse.getCouponNum(); i++) {
                        if (couponResponse.getCoupons().get(i).isChecked()) {
                            tShowUsedCoupons.setText("当前默认使用了1张优惠券，面额为："
                                    + StringUtils.formatIntMoney(couponResponse.getCoupons().get(i).getMoney()) + "元");
                            break;
                        }
                    }

                    break;
                case 1:
                    int num = 0;
                    int money = 0;
                    for (int i = 0; i < couponResponse.getCouponNum(); i++) {
                        if (couponResponse.getCoupons().get(i).isChecked()) {
                            money+=couponResponse.getCoupons().get(i).getMoney();
                            num++;
                        }
                    }
//                    tShowUsedCoupons.setText("当前使用了 " + num + " 张优惠券,请通过列表查看");
                    tShowUsedCoupons.setText("当前使用了 " + num + " 张优惠券,面额为："+StringUtils.formatIntMoney(money) + "元");
                    break;
                default:
                    break;
            }
        }


    };


    // 默认选中最大金额的
    private void CheckMaxAmount() {
        int max = 0;
        // 找出最大金额
        for (int i = 0; i < couponResponse.getCouponNum(); i++) {
            if (couponResponse.getCoupons().get(i).getMoney() > max) {
                max = couponResponse.getCoupons().get(i).getMoney();
            }
        }
        LogUtils.e("max= " + max);
        // 判断是哪个选项
        for (int i = 0; i < couponResponse.getCouponNum(); i++) {
            if (couponResponse.getCoupons().get(i).getMoney() == max) {
                couponResponse.getCoupons().get(i).setChecked(true);
                break;
            }
        }
    }

    // 有优惠券显示默认最大选中的优惠券
    private void showCouponsChecked() {
        if (couponResponse.getCouponNum() <= 0) {
            return;
        }
        CheckMaxAmount();
        mhandler.sendEmptyMessage(0);
    }

    private void showCouponsInfo() {
        if (couponResponse.getCouponNum() <= 0) {
            return;
        }


        ConponsDialog dialog = new ConponsDialog(this, R.layout.activity_coupons_list, couponResponse.getCoupons());
        dialog.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                LogUtils.e("setOnDismissListener");
                mhandler.sendEmptyMessage(1);
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    private String getSn() {
        List<Coupons> list = couponResponse.getCoupons();
        StringBuilder sn = new StringBuilder();
        for (int i = 0; i < couponResponse.getCouponNum(); i++) {
            if (list.get(i).isChecked()) {
                sn.append(list.get(i).getSn());
                sn.append(",");
            }
        }
        return (sn.toString().length() > 0 ? sn.substring(0, sn.toString().length() - 1) : "");
    }

    private boolean IsInputPass() {
        if (couponResponse.isFreePassword()) {
            return true;
        }
        if (point <= 0 && StringUtils.isEmpty(getSn())) {
            return true;
        }
        final PassWordDialog dialog = new PassWordDialog(MemberActivity.this, R.layout.activity_psw, new PassWordDialog.OnResultInterface() {

            @Override
            public void onResult(String data) {
                LogUtils.e(data);
                pass = Base64Utils.getBase64(data);
                memberTransAmountAction();
            }
        });
        dialog.setCancelable(true);
        dialog.show();
        return false;
    }

    private boolean IsMember() {
        // 根据姓名来判断是否输入会员
        if (StringUtils.isEmpty(couponResponse.getMemberName())) {
            MemberDialog dialog = new MemberDialog(MemberActivity.this, R.layout.activity_member_name,
                    new MemberDialog.onClickLeftListener() {

                        @Override
                        public void onClickLeft(MemberDialog dialog, String result) {
                            dialog.dismiss();
                            couponResponse.setMemberName(result);
                            memberTransAmountAction();
                        }

                    }, new MemberDialog.onClickRightListener() {
                @Override
                public void onClickRight(MemberDialog dialog) {
                    dialog.dismiss();
                }
            });
            dialog.show();
            return false;
        }
        return true;
    }

    private void testData() {
        List<Coupons> data = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            Coupons coupons = new Coupons();
            coupons.setMoney(12);
            coupons.setName("优惠券名称" + i);
            data.add(coupons);
        }
        ConponsDialog dialog = new ConponsDialog(this, R.layout.activity_coupons_list, data);
        dialog.show();
    }

}
