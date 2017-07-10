package com.zfsbs.core.action;
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

import com.myokhttp.MyOkHttp;
import com.myokhttp.response.GsonResponseHandler;
import com.tool.utils.dialog.LoadingDialog;
import com.tool.utils.utils.LogUtils;
import com.tool.utils.utils.StringUtils;
import com.zfsbs.common.CommonFunc;
import com.zfsbs.core.myinterface.ActionCallbackListener;
import com.zfsbs.model.RicherGetMember;
import com.zfsbs.model.TransUploadRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zf on 2017/2/23.
 */

public class RicherQb {
    private static final String TAG = "RicherQb";
    public static final String md5_key = "safdsfYkH59qZAl";
    private final static String SERVER_URL = "http://www.hhjr88.com/mobile/payment/po";
    private final static String upload_URL = "http://www.hhjr88.com/mobile/payment/posi";
    /**
     * 富人E钱包-获取会员信息
     *
     * @param mobile
     * @param listener
     */
    public static void getMemberInfo(final Context context, String mobile,
                              final ActionCallbackListener<RicherGetMember> listener) {
        final LoadingDialog dialog = new LoadingDialog(context);
        dialog.show("正在获取会员信息...");

        Map<String, Object> paramsMap = new HashMap<String, Object>();

        paramsMap.put("account", mobile);
        String data = CommonFunc.Richer_getJsonStr("hh", paramsMap, "verify", md5_key);
        LogUtils.e(TAG, "request: " + data);
        LogUtils.e(TAG, "URL: " + SERVER_URL);
        MyOkHttp.get().postJson(context, SERVER_URL, data, new GsonResponseHandler<RicherGetMember>() {
            @Override
            public void onFailure(int statusCode, String error_msg) {
                dialog.dismiss();
                listener.onFailure("" + statusCode, error_msg);
            }

            @Override
            public void onSuccess(int statusCode, RicherGetMember response) {
                LogUtils.e(TAG, "response:" + response.toString());
                dialog.dismiss();
                if (response != null) {
                    String tempStr=response.getCode();
                    if ((response.getCode().equals("200"))&&(response.getMsg().equals("YES"))) {
                        listener.onSuccess(response);
                    } else {
                        listener.onFailure(response.getCode(), response.getMsg());
                    }
                } else {
                    listener.onFailure("", "链接服务器异常");
                }
            }
        });
    }

    /**
     * 富人E钱包-获取会员信息
     *
     * @param listener
     */
    public static void UploadTransInfo(final Context context, TransUploadRequest request,
                                     final ActionCallbackListener<RicherGetMember> listener) {
        final LoadingDialog dialog = new LoadingDialog(context);
        dialog.show("正在上传交易信息...");

        Map<String, Object> paramsMap = new HashMap<String, Object>();


        if (request.getPayType()==6){
            paramsMap.put("pos_shop_id", StringUtils.getSerial());
        }else{
            paramsMap.put("pos_shop_id", StringUtils.getSerial());
        }

        if (request.getPayType()==6){
            paramsMap.put("pos_pay_id", request.getClientOrderNo());
        }else{
            paramsMap.put("pos_pay_id", request.getAuthCode());
        }
        paramsMap.put("account", request.getCardNo());
        if (request.getCash()==0){
            paramsMap.put("need_pay", request.getBankAmount());
        }else{
            paramsMap.put("need_pay", request.getCash());
        }

        paramsMap.put("pay_time", request.getT());
        if ((request.getPayType()==10)||(request.getPayType()==11)||(request.getPayType()==12)||(request.getPayType()==14)){
            paramsMap.put("pos_type", "0");
        } else{
            paramsMap.put("pos_type", "1");
        }






        String data = CommonFunc.Richer_getJsonStr("hh", paramsMap, "verify", md5_key);
        LogUtils.e(TAG, "request: " + data);
        LogUtils.e(TAG, "URL: " + upload_URL);
        MyOkHttp.get().postJson(context, upload_URL, data, new GsonResponseHandler<RicherGetMember>() {
            @Override
            public void onFailure(int statusCode, String error_msg) {
                dialog.dismiss();
                listener.onFailure("" + statusCode, error_msg);
            }

            @Override
            public void onSuccess(int statusCode, RicherGetMember response) {
                LogUtils.e(TAG, "response:" + response.toString());
                dialog.dismiss();
                if (response != null) {
                    String tempStr=response.getCode();
                    if ((response.getCode().equals("200"))) {
                        listener.onSuccess(response);
                    } else {
                        listener.onFailure(response.getCode(), response.getMsg());
                    }
                } else {
                    listener.onFailure("", "链接服务器异常");
                }
            }
        });
    }
}
