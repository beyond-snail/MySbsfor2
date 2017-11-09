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
import android.os.Handler;

import com.myokhttp.MyOkHttp;
import com.myokhttp.response.GsonResponseHandler;
import com.tool.utils.dialog.CustomTimingProgressDialog;
import com.tool.utils.dialog.LoadingDialog;
import com.tool.utils.utils.SPUtils;
import com.tool.utils.utils.StringUtils;
import com.tool.utils.utils.ToastUtils;
import com.zfsbs.common.CommonFunc;
import com.zfsbs.config.Config;
import com.zfsbs.config.Constants;
import com.zfsbs.core.myinterface.ActionCallbackListener;
import com.zfsbs.model.ApiResponse;
import com.zfsbs.model.TransUploadResponse;
import com.zfsbs.model.ZfQbResponse;

import java.util.HashMap;
import java.util.Map;

/**********************************************************
 * *
 * Created by wucongpeng on 2016/11/15.        *
 **********************************************************/


public class ZfQbAction {

    private QbPayResultEvent QbPayResultEvent;

    private CustomTimingProgressDialog Timedialog;
    ActionCallbackListener<ZfQbResponse> mListener;

    private Context mContext;
    private boolean isPaying = false; // 查询标识次数
    private String order_no; //订单号
    private String old_time; //原交易时间
    private int old_sid;

    public interface QbPayResultEvent{
        void onSuccess(ZfQbResponse data);
        void onFailure(int statusCode, String error_msg);
    }

    public ZfQbAction(Context context) {
        this.mContext = context;
    }


    private void CloseTimeDialog(){
        if (Timedialog != null){
            Timedialog.dialogDismiss();
        }
    }


    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // do something
            if (isPaying) {
//                query(old_sid, order_no, old_time, mListener);
            } else {
                ToastUtils.CustomShow(mContext, "交易失败");
                CloseTimeDialog();
//                handler.removeCallbacks(this);
//                mListener.onFailure("", "交易失败");
            }
        }
    };




    /**
     * 商博士-钱包支付
     * @param sid
     * @param orderNo
     * @param amount
     * @param qrCode
     * @param listener
     */
    public void qbAction1(final int sid, final String orderNo, int amount, final String time, String traceNum, String qrCode,String cardNo, final ActionCallbackListener<ZfQbResponse> listener){



        Map<String, Object> paramsMap = new HashMap<String, Object>();

        paramsMap.put("TranCode", "9448");
        paramsMap.put("qrCode", qrCode);
        paramsMap.put("cardNo", cardNo);
        paramsMap.put("MerchantId", sid);
        paramsMap.put("TerminalNo", StringUtils.getSerial());
        paramsMap.put("OrgOrderNum", orderNo);
        paramsMap.put("OrgTranDateTime", time);
        paramsMap.put("SysTraceNum", traceNum);
        paramsMap.put("TranAmt", amount);
        paramsMap.put("OrderCurrency", "156");
        paramsMap.put("operator_num", SPUtils.get(mContext, Constants.USER_NAME, ""));

        String data = CommonFunc.getJsonStr("qbPay", paramsMap, "verify", Config.md5_key);

        isPaying = true;


        Timedialog = new CustomTimingProgressDialog(mContext, new CustomTimingProgressDialog.DialogDismissEvent() {
            @Override
            public void dialogDismiss() {
                isPaying = false;
            }
        });
        Timedialog.show();
        Timedialog.setCancelable(false);


        MyOkHttp.get().postJson(mContext, Config.SBS_URL_QB, data, new GsonResponseHandler<ApiResponse<ZfQbResponse>>() {
            @Override
            public void onSuccess(int statusCode, ApiResponse<ZfQbResponse> response) {
                CloseTimeDialog();
                if (response != null) {
                    if (response.getCode().equals("A00006")) {
//                        if (StringUtils.isEquals(response.getResult().getTransStates(), "0000")){
//
//                            listener.onSuccess(response.getResult());
//                        }else{
////                            order_no = orderNo;
////                            old_time = time;
////                            old_sid = sid;
////                            mListener = listener;
////                            handler.postDelayed(runnable, 5000);
//                            listener.onFailure(response.getResult().getTransStates(), response.getResult().getMsgTxt());
//                        }
                        listener.onSuccess(response.getResult());
                    } else {

                        listener.onFailure(response.getCode(), response.getMsg());
                    }
                } else {

                    listener.onFailure("", "链接服务器异常");
                }
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                CloseTimeDialog();
                listener.onFailurTimeOut("" + statusCode, error_msg);
            }

        });
    }


//    public void query(int sid, String orderNo, String time, final ActionCallbackListener<ZfQbResponse> listener){
//
//        Map<String, Object> paramsMap = new HashMap<String, Object>();
//
//        paramsMap.put("MerchantId", sid);
//        paramsMap.put("TerminalNo", StringUtils.getSerial());
//        paramsMap.put("OrgOrderNum", orderNo);
//        paramsMap.put("OrgTranDateTime", time);
//        paramsMap.put("operator_num", SPUtils.get(mContext, Constants.USER_NAME, ""));
//
//
//        String data = CommonFunc.getJsonStr("qbQuery", paramsMap, "verify", Config.md5_key);
//
//
//
//        MyOkHttp.get().postJson(mContext, Config.SBS_URL_QB, data, new GsonResponseHandler<ApiResponse<ZfQbResponse>>() {
//            @Override
//            public void onSuccess(int statusCode, ApiResponse<ZfQbResponse> response) {
//                if (response != null) {
//                    if (response.getCode().equals("A00006")) {
//                        if (StringUtils.isEquals(response.getResult().getTransStates(), "0000")){
//                            CloseTimeDialog();
//                            listener.onSuccess(response.getResult());
//                        }else{
//
//                            handler.postDelayed(runnable, 5000);
//                        }
//
//                    } else {
//                        CloseTimeDialog();
//                        listener.onFailure(response.getCode(), response.getMsg());
//                    }
//                } else {
//                    CloseTimeDialog();
//                    listener.onFailure("", "链接服务器异常");
//                }
//            }
//
//            @Override
//            public void onFailure(int statusCode, String error_msg) {
//                CloseTimeDialog();
//                listener.onFailure("" + statusCode, error_msg);
//            }
//
//        });
//    }


    public void query1(Context context, Long sid, String orderNo, String time, final ActionCallbackListener<TransUploadResponse> listener){

        final LoadingDialog dialog = new LoadingDialog(context);
        dialog.show("正在查询...");
        dialog.setCancelable(false);

        Map<String, Object> paramsMap = new HashMap<String, Object>();

        paramsMap.put("MerchantId", sid);
        paramsMap.put("TerminalNo", StringUtils.getSerial());
        paramsMap.put("OrgOrderNum", orderNo);
        paramsMap.put("OrgTranDateTime", time);
        paramsMap.put("operator_num", SPUtils.get(context, Constants.USER_NAME, ""));

        String data = CommonFunc.getJsonStr("qbQuery", paramsMap, "verify", Config.md5_key);



        MyOkHttp.get().postJson(mContext, Config.SBS_URL_QB, data, new GsonResponseHandler<ApiResponse<TransUploadResponse>>() {
            @Override
            public void onSuccess(int statusCode, ApiResponse<TransUploadResponse> response) {
                dialog.dismiss();
                if (response != null) {
                    if (response.getCode().equals("A00006")) {
//                        if (StringUtils.isEquals(response.getResult().getTransStates(), "0000")) {
//                            listener.onSuccess(response.getResult());
//                        }else {
//                            listener.onFailure(response.getCode(), response.getMsg());
//                        }
                        listener.onSuccess(response.getResult());
                    } else {
                        listener.onFailure(response.getCode(), response.getMsg());
                    }
                } else {
                    listener.onFailure("", "链接服务器异常");
                }
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                dialog.dismiss();
                listener.onFailure("" + statusCode, error_msg);
            }

        });
    }




}
