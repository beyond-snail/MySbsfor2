package com.hd.core;

////////////////////////////////////////////////////////////////////
//                          _ooOoo_                               //
//                         o8888888o                              //
//                         88" . "88                              //
//                         (| ^_^ |)                              //
//                         O\  =  /O                              //
//                      ____/`---'\____                           //
//                    .'  \\|     |//  `.                         //
//                   /  \\|||  :  |||//  \                        //
//                  /  _||||| -:- |||||-  \                       //
//                  |   | \\\  -  /// |   |                       //
//                  | \_|  ''\---/''  |   |                       //
//                  \  .-\__  `-`  ___/-. /                       //
//                ___`. .'  /--.--\  `. . ___                     //
//              ."" '<  `.___\_<|>_/___.'  >'"".                  //
//            | | :  `- \`.;`\ _ /`;.`/ - ` : | |                 //
//            \  \ `-.   \_ __\ /__ _/   .-` /  /                 //
//      ========`-.____`-.___\_____/___.-`____.-'========         //
//                           `=---='                              //
//      ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^        //
//              佛祖保佑       永无BUG     永不修改                  //
//                                                                //
//          佛曰:                                                  //
//                  写字楼里写字间，写字间里程序员；                   //
//                  程序人员写程序，又拿程序换酒钱。                   //
//                  酒醒只在网上坐，酒醉还来网下眠；                   //
//                  酒醉酒醒日复日，网上网下年复年。                   //
//                  但愿老死电脑间，不愿鞠躬老板前；                   //
//                  奔驰宝马贵者趣，公交自行程序员。                   //
//                  别人笑我忒疯癫，我笑自己命太贱；                   //
//                  不见满街漂亮妹，哪个归得程序员？                   //
////////////////////////////////////////////////////////////////////

import android.content.Context;

import com.google.gson.Gson;
import com.hd.hdcofig.HdConfig;
import com.hd.model.Conditions;
import com.hd.model.HdAdjustScoreRequest;
import com.hd.model.HdAdjustScoreRequestOperCtx;
import com.hd.model.HdAdjustScoreRequestOperCtxOperator;
import com.hd.model.HdAdjustScoreRequestRequest;
import com.hd.model.HdAdjustScoreRequestRequestScoreRec;
import com.hd.model.HdAdjustScoreResponse;
import com.hd.model.HdQuery;
import com.hd.model.HdQueryResponse;
import com.hd.model.HdQueryResponseBirthday;
import com.hd.model.HdQueryResponseidCard;
import com.hd.model.HdRegisterRequest;
import com.hd.model.HdRegisterRequestExtMember;
import com.hd.model.HdRegisterRequestMember;
import com.hd.model.HdRegisterRequestOperCtx;
import com.hd.model.HdRegisterRequestOperCtxOperator;
import com.hd.model.HdRegisterRequestRegisterAddress;
import com.hd.model.HdRegisterRequestRequest;
import com.hd.model.HdRegisterResponse;
import com.hd.model.Orders;
import com.myokhttp.MyOkHttp;
import com.myokhttp.response.GsonResponseHandler;
import com.tool.utils.dialog.LoadingDialog;
import com.tool.utils.utils.Base64Utils;
import com.tool.utils.utils.LogUtils;
import com.tool.utils.utils.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**********************************************************
 * *
 * Created by wucongpeng on 2017/2/20.        *
 **********************************************************/


public class HdAction {

    private static final String TAG = "HdAction";



    public interface HdCallResult{
        public void onSuccess(String data);
        public void onFailed(String errorCode, String message);
    }



    /**
     * 查询
     */
    public static void hdQuery(Context context, final String phone, final HdCallResult listener) {


        List<String> parameters = new ArrayList<>();
        parameters.add(phone);

        Conditions conditions = new Conditions();
        conditions.setOperation("cellphoneEquals");
        conditions.setParameters(parameters);

//        Conditions conditions1 = new Conditions();
//        conditions.setOperation("cardNumEquals");
//        conditions.setParameters(parameters);

        List<Conditions> conditionsList = new ArrayList<>();
        conditionsList.add(conditions);
//        conditionsList.add(conditions1);


        Orders orders = new Orders();
        orders.setField("");
        orders.setDirection("asc");

        List<Orders> ordersList = new ArrayList<>();
        ordersList.add(orders);


        HdQuery hdQuery = new HdQuery();
        hdQuery.setConditions(conditionsList);
        hdQuery.setOrders(ordersList);
        hdQuery.setPage(0);
        hdQuery.setPageSize(0);
        hdQuery.setProbePages(0);


        String data = new Gson().toJson(hdQuery);
        LogUtils.e("tempMap", data);

        LogUtils.e("url", HdConfig.query_url);

        String auth = authorization();


        final LoadingDialog dialog = new LoadingDialog(context);
        dialog.show("正在查询会员信息...");

        MyOkHttp.get().postJsonHead(context, HdConfig.query_url, data, auth, new GsonResponseHandler<HdQueryResponse>(){

            @Override
            public void onSuccess(int statusCode, HdQueryResponse response) {
                dialog.dismiss();
                String data = new Gson().toJson(response);
                LogUtils.e(TAG, StringUtils.isEmpty(data) ? "" : data);
                if (response.getMessage().equals("ok") && response.getRecords() != null) {
                    listener.onSuccess(phone);
                }else {
                    listener.onFailed("", "查询不存在，请注册");
                }
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                dialog.dismiss();
                LogUtils.e(TAG, statusCode+error_msg);
                listener.onFailed(statusCode+"", error_msg);
            }
        });

    }


    /**
     * 会员注册
     * @param phone 手机号
     * @param memberName 会员名称
     * @param gender 性别
     * @param wedLock 婚姻状况
     */
    public static void HdRegister(Context context, String phone, String memberName, String gender,String wedLock, final HdCallResult listener){

        HdRegisterRequestOperCtxOperator operCtxOperator = new HdRegisterRequestOperCtxOperator();
        operCtxOperator.setNamespace("");
        operCtxOperator.setFullName("商博士");
        operCtxOperator.setId(StringUtils.getFormatCurTime());

        HdRegisterRequestOperCtx operCtx = new HdRegisterRequestOperCtx();
        operCtx.setTime(StringUtils.getDateFormateT());
        operCtx.setOperator(operCtxOperator);
        operCtx.setStore("1000000");
        operCtx.setTerminalId(StringUtils.getSerial());

        HdRegisterRequestExtMember extMember = new HdRegisterRequestExtMember();
        extMember.setOpenId(StringUtils.getFormatCurTime()+StringUtils.getSerial());
        extMember.setCardId(StringUtils.getFormatCurTime()+StringUtils.getSerial());

        HdQueryResponseidCard idCard = new HdQueryResponseidCard();
        idCard.setType("others");
        idCard.setId("123456");

        HdQueryResponseBirthday birthday = new HdQueryResponseBirthday();
        birthday.setDay("");
        birthday.setMonth("");
        birthday.setYear("");

        HdRegisterRequestRegisterAddress address = new HdRegisterRequestRegisterAddress();
        address.setCityCode("");
        address.setCityName("");
        address.setDistrictCode("");
        address.setDistrictName("");
        address.setProvinceCode("");
        address.setProvinceName("");

        HdRegisterRequestMember member = new HdRegisterRequestMember();
        member.setId("");
        member.setName(memberName);
        member.setGender(gender);
        member.setBirthday(birthday);
        member.setCellphone(phone);
        member.setIdCard(idCard);
        member.setBelongStore("1000000");
        member.setWedLock(wedLock);
        member.setGrade("");
        member.setAddress("");
        member.setMobileChecked(false);
        member.setLastUpdateTime("");
        member.setRegisterAddress(address);


        HdRegisterRequestRequest registerRequestRequest = new HdRegisterRequestRequest();
        registerRequestRequest.setExtMember(extMember);
        registerRequestRequest.setMember(member);
        registerRequestRequest.setCarplateNum("");
        registerRequestRequest.setCarplateNum2("");


        HdRegisterRequest request = new HdRegisterRequest();
        request.setOperCtx(operCtx);
        request.setRequest(registerRequestRequest);

        String data = new Gson().toJson(request);
        LogUtils.e("request", data);

        LogUtils.e("url", HdConfig.open_card_url);

        String auth = authorization();

        final LoadingDialog dialog = new LoadingDialog(context);
        dialog.show("正在注册会员信息...");

        MyOkHttp.get().postJsonHead(context, HdConfig.open_card_url, data, auth, new GsonResponseHandler<HdRegisterResponse>(){

            @Override
            public void onSuccess(int statusCode, HdRegisterResponse response) {
                dialog.dismiss();
                String data = new Gson().toJson(response);
                LogUtils.e(TAG, StringUtils.isEmpty(data) ? "" : data);
                if (response.getMessage().equals("ok")) {
                    if (response.getCard() != null && response.getCard().getState().equals("使用中")) {
                        listener.onSuccess(data);
                    } else {
                        listener.onFailed("", response.getCard().getState());
                    }
                }else {
                    listener.onFailed(response.getErrorCode()+"", response.getMessage());
                }
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                dialog.dismiss();
                LogUtils.e(TAG, statusCode+error_msg);
                listener.onFailed(statusCode+"", error_msg);
            }
        });


    }



    public static void HdAdjustScore(Context context, String phone, long score, final HdCallResult listener){

        HdAdjustScoreRequestOperCtxOperator operCtxOperator = new HdAdjustScoreRequestOperCtxOperator();
        operCtxOperator.setNamespace("");
        operCtxOperator.setFullName("商博士");
        operCtxOperator.setId(StringUtils.getFormatCurTime());

        HdAdjustScoreRequestOperCtx operCtx = new HdAdjustScoreRequestOperCtx();
        operCtx.setTime(StringUtils.getDateFormateT());
        operCtx.setOperator(operCtxOperator);
        operCtx.setStore("1000000");
        operCtx.setTerminalId(StringUtils.getSerial());


        HdQueryResponseidCard account = new HdQueryResponseidCard();
        account.setType("mobile");
        account.setId(phone);

        HdAdjustScoreRequestRequestScoreRec scoreRec = new HdAdjustScoreRequestRequestScoreRec();
        scoreRec.setScoreSubject("调整");
        scoreRec.setScoreType("-");
        scoreRec.setScore(score);

        HdAdjustScoreRequestRequest request = new HdAdjustScoreRequestRequest();
        request.setTranTime(StringUtils.getDateFormateT());
        request.setAccount(account);
        request.setScoreRec(scoreRec);
        request.setTranId(StringUtils.getFormatCurTime()+StringUtils.getSerial());
        request.setXid(StringUtils.getFormatCurTime()+StringUtils.getSerial());
//        request.setScoreSource("调整");
//        request.setRemark("");
//        request.setAction("调整");

        HdAdjustScoreRequest request1 = new HdAdjustScoreRequest();
        request1.setOperCtx(operCtx);
        request1.setRequest(request);

        String data = new Gson().toJson(request1);
        LogUtils.e("request", data);

        LogUtils.e("url", HdConfig.adjustScore_url);

        String auth = authorization();

        final LoadingDialog dialog = new LoadingDialog(context);
        dialog.show("正在上送积分信息...");


        MyOkHttp.get().postJsonHead(context, HdConfig.adjustScore_url, data, auth, new GsonResponseHandler<HdAdjustScoreResponse>(){

            @Override
            public void onSuccess(int statusCode, HdAdjustScoreResponse response) {
                dialog.dismiss();
                String data = new Gson().toJson(response);
                LogUtils.e(TAG, StringUtils.isEmpty(data) ? "" : data);
                if (response.getMessage().equals("ok")) {
                    if (response.getResult() != null) {
                        listener.onSuccess(data);
                    } else {
                        listener.onFailed("", response.getMessage());
                    }
                }else {
                    listener.onFailed(response.getErrorCode()+"", response.getMessage());
                }
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                dialog.dismiss();
                LogUtils.e(TAG, statusCode+error_msg);
                listener.onFailed(statusCode+"", error_msg);
            }
        });


    }






    public static String authorization(){
       byte[] b = null;
        String s = HdConfig.HD_USER_NAME + ":" + HdConfig.HD_PASSWORD;
        try {
            b = s.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            LogUtils.e("字串转byte出错");
        }
        String auth = "Basic "+ Base64Utils.encode(b);
        LogUtils.e("auth", auth);

        return auth;
    }
}


































