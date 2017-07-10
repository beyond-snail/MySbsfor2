package com.zfsbs.activity.liandong;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.mycommonlib.core.PayCommon;
import com.mycommonlib.model.ComTransInfo;
import com.tool.utils.utils.ToastUtils;
import com.zfsbs.R;
import com.zfsbs.activity.BaseActivity;
import com.zfsbs.myapplication.MyApplication;

import java.util.HashMap;
import java.util.Map;



/**
 * Created by zf on 2017/3/16.
 */

public class TerminalSetterActivity extends BaseActivity implements View.OnClickListener,View.OnTouchListener{
    private EditText terminal;
    private EditText merchant;
    private EditText port;
    private IPEdit IP_Address;
    private Button makeSure;






    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.terminalsettle_layout);
//        AppManager.getAppManager().addActivity(this);
        initView();
        addClickLisenter();




    }



    void initView(){
        terminal=(EditText)findViewById(R.id.EditText1);
        merchant=(EditText)findViewById(R.id.EditText2);
        IP_Address=(IPEdit)findViewById(R.id.EditText3);

        port=(EditText)findViewById(R.id.EditText4);
        makeSure=(Button)findViewById(R.id.id_Ok);
        String tid=MyApplication.getInstance().getLoginData().getTerminalNo();
        String mid=MyApplication.getInstance().getLoginData().getMerchantNo();
        terminal.setText(tid);
        merchant.setText(mid);

    }

    void addClickLisenter(){
        makeSure.setOnClickListener(this);
        terminal.setOnTouchListener(this);
        merchant.setOnTouchListener(this);
        IP_Address.setOnTouchListener(this);
        port.setOnTouchListener(this);



    }


    @Override
    public void onClick(View view) {


        }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(terminal.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        imm.hideSoftInputFromWindow(merchant.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        imm.hideSoftInputFromWindow(IP_Address.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        imm.hideSoftInputFromWindow(port.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        return false;
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
            paramsSetter();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    void paramsSetter(){
        if ((port.getText().equals(""))&&(IP_Address.getText().equals(""))&&(terminal.getText().equals(""))&&(merchant.getText().equals(""))){
            ToastUtils.CustomShow(this,"参数不能都为空");
            return;
        }
        Map<String,String> params=new HashMap<String, String>();
//                    param.put("priIP","106.120.215.234");
//                    param.put("priPort","4008");
//                    param.put("mid","829393115200089");
//                    param.put("tid","122010000011");
        if (terminal.getText().length()!=8){
            ToastUtils.CustomShow(this,"终端有效8位");
            return;
        }else{
            params.put("tid",terminal.getText().toString());
        }

        if (merchant.getText().length()!=15){
            ToastUtils.CustomShow(this,"商户有效15位");
            return;
        }else {
            params.put("mid",merchant.getText().toString());
        }
        if (IP_Address.getText().toString().trim().length()>7){
            params.put("priIP",IP_Address.getText().toString()).trim();
        }else {
            params.put("priIP","");
        }
        if (port.getText().toString().length()>0){
            params.put("priPort",port.getText().toString());
        }else {
            params.put("priPort","");
        }

        PayCommon.setParams(this,0,"","",new PayCommon.ComTransResult<ComTransInfo>(){
            @Override
            public void success(ComTransInfo transInfo) {
                ToastUtils.CustomShow(TerminalSetterActivity.this,"设置成功");

            }

            @Override
            public void failed(String error) {

            }
        });
    }
}