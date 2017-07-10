package com.zfsbs.activity.hd;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.hd.R;
import com.hd.core.HdAction;
import com.hd.enums.EnumConsts;
import com.tool.utils.utils.PhoneFormatCheckUtils;
import com.tool.utils.utils.StringUtils;
import com.tool.utils.utils.ToastUtils;
import com.zfsbs.activity.InputAmountActivity;
import com.zfsbs.activity.ZfPayActivity;
import com.zfsbs.common.CommonFunc;
import com.zfsbs.model.MemberTransAmountResponse;


public class HdRegisterActivity extends Activity implements View.OnClickListener {

    private EditText etPhone;
    private EditText etUserName;
    private EditText etIdCard;
    private RadioGroup rgGender;
    private RadioButton rgFemale;
    private RadioButton rgMale;

    private RadioGroup rgWedLock;
    private RadioButton rgMarried;
    private RadioButton rgSingle;
    private RadioButton rgSercet;
    private Button btnRegister;

    private String gender = "";
    private String wedLock = "";
    private int amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hd_register1);

        initView();
        addListener();

        amount = getIntent().getExtras().getInt("amount");

    }


    private void initView() {
        etPhone = (EditText) findViewById(R.id.id_phone);
        etUserName = (EditText) findViewById(R.id.id_username);
        etIdCard = (EditText) findViewById(R.id.id_idCard);
        rgGender = (RadioGroup) findViewById(R.id.id_gender);
        rgFemale = (RadioButton) findViewById(R.id.id_female);
        rgMale = (RadioButton) findViewById(R.id.id_male);
        rgWedLock = (RadioGroup) findViewById(R.id.id_wedLock);
        rgMarried = (RadioButton) findViewById(R.id.id_married);
        rgSingle = (RadioButton) findViewById(R.id.id_single);
        rgSercet = (RadioButton) findViewById(R.id.id_secret);

        btnRegister = (Button) findViewById(R.id.id_register);
    }

    private void addListener() {

        rgGender.setOnCheckedChangeListener(new GenderRadioGroupListener());
        rgWedLock.setOnCheckedChangeListener(new WedLockRadioGroupListener());
        btnRegister.setOnClickListener(this);
    }



    private class GenderRadioGroupListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checkedId) {
            if (checkedId == rgFemale.getId()) {
                System.out.println("选中了female!");
                gender = EnumConsts.Gender.GENDER_FEMALE.getName();
            } else if (checkedId == rgMale.getId()) {
                System.out.println("选中了male!");
                gender = EnumConsts.Gender.GENDER_MALE.getName();
            }
        }
    }

    private class WedLockRadioGroupListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checkedId) {
            if (checkedId == rgMarried.getId()) {
                System.out.println("选中了Married!");
                wedLock = EnumConsts.WedLock.WED_LOCK_MARRIED.getName();
            } else if (checkedId == rgSingle.getId()) {
                System.out.println("选中了Single!");
                wedLock = EnumConsts.WedLock.WED_LOCK_SINGLE.getName();
            } else if (checkedId == rgSercet.getId()) {
                System.out.println("选中了Sercet!");
                wedLock = EnumConsts.WedLock.WED_LOCK_SECRET.getName();
            }
        }
    }



    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.id_register) {
            doRegister();

        }
    }


    /**
     * 注册
     */
    private void doRegister() {

        if (StringUtils.isEmpty(etPhone.getText().toString())){
            ToastUtils.CustomShow(HdRegisterActivity.this, "手机号为空");
            return;
        }
        if (StringUtils.isEmpty(etUserName.getText().toString())){
            ToastUtils.CustomShow(HdRegisterActivity.this, "用户名为空");
            return;
        }
//        if (StringUtils.isEmpty(etIdCard.getText().toString())){
//            ToastUtils.CustomShow(HdRegisterActivity.this, "身份证为空");
//            return;
//        }
        if (StringUtils.isEmpty(gender)){
            ToastUtils.CustomShow(HdRegisterActivity.this, "性别没选");
            return;
        }
        if (StringUtils.isEmpty(wedLock)){
            ToastUtils.CustomShow(HdRegisterActivity.this, "婚姻状态没选");
            return;
        }



        final String phone = etPhone.getText().toString();
        if (!PhoneFormatCheckUtils.isPhoneLegal(phone)){
            ToastUtils.CustomShow(HdRegisterActivity.this, "手机号格式不对");
            return;
        }
        String username = etUserName.getText().toString();
        String idCard = etIdCard.getText().toString();


        HdAction.HdRegister(this, phone, username, gender, wedLock, new HdAction.HdCallResult() {
            @Override
            public void onSuccess(String data) {

//                HdRegisterResponse response = new Gson().fromJson(data, HdRegisterResponse.class);

                MemberTransAmountResponse member = new MemberTransAmountResponse();
                member.setRealMoney(amount);
                member.setTradeMoney(amount);
                member.setPhone(phone);
                CommonFunc.setBackMemberInfo(HdRegisterActivity.this, member);
                CommonFunc.startAction(HdRegisterActivity.this, ZfPayActivity.class, true);
            }

            @Override
            public void onFailed(String errorCode, String message) {
                ToastUtils.CustomShow(HdRegisterActivity.this, errorCode+"#"+message);
                CommonFunc.startAction(HdRegisterActivity.this, InputAmountActivity.class, true);
            }
        });
    }
}
