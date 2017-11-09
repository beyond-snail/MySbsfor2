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
import com.myokhttp.response.JsonResponseHandler;
import com.tool.utils.dialog.LoadingDialog;
import com.tool.utils.utils.LogUtils;
import com.tool.utils.utils.SPUtils;
import com.tool.utils.utils.StringUtils;
import com.zfsbs.common.CommonFunc;
import com.zfsbs.config.Config;
import com.zfsbs.config.Constants;
import com.zfsbs.core.myinterface.ActionCallbackListener;
import com.zfsbs.model.ActivateApiResponse;
import com.zfsbs.model.ApiResponse;
import com.zfsbs.model.CardId;
import com.zfsbs.model.ChargeBlance;
import com.zfsbs.model.CouponsResponse;
import com.zfsbs.model.LoginApiResponse;
import com.zfsbs.model.MemberTransAmountRequest;
import com.zfsbs.model.MemberTransAmountResponse;
import com.zfsbs.model.QueryScanReturn;
import com.zfsbs.model.RechargeMeal;
import com.zfsbs.model.RechargeUpLoad;
import com.zfsbs.model.ShiftRoom;
import com.zfsbs.model.StkPayRequest;
import com.zfsbs.model.TicektResponse;
import com.zfsbs.model.TransUploadRequest;
import com.zfsbs.model.TransUploadResponse;
import com.zfsbs.model.VipCardNo;
import com.zfsbs.model.YyTicektResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**********************************************************
 * *
 * Created by wucongpeng on 2016/11/6.        *
 **********************************************************/


public class SbsAction {

    private static final String TAG = "SbsAction";

    /**
     * 商博士-签到
     *
     * @param posNum   设备序列号
     * @param listener 回调
     */
    public void Login(final Context context, String posNum, String username, String password, final ActionCallbackListener<LoginApiResponse> listener) {

        final LoadingDialog dialog = new LoadingDialog(context);
        dialog.show("正在获取配置信息...");

        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("pos_number", posNum);
        paramsMap.put("operator_num", username);
        paramsMap.put("operator_password", password);
        String data = CommonFunc.getJsonStr("posSignIn", paramsMap, "verify", Config.md5_key);
        LogUtils.e(TAG, Config.SBS_URL);
        MyOkHttp.get().postJson(context, Config.SBS_URL, data, new GsonResponseHandler<ApiResponse<LoginApiResponse>>() {
            @Override
            public void onSuccess(int statusCode, ApiResponse<LoginApiResponse> response) {
                dialog.dismiss();
                if (response != null) {
                    if (response.getCode().equals("A00006")) {
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

    /**
     * 商博士-激活码反馈
     *
     * @param sid
     * @param isSuccess
     * @param activateCode
     * @param listener
     */
    public void active(final Context context, Long sid, int isSuccess, String activateCode,
                       final ActionCallbackListener<ActivateApiResponse> listener) {
        final LoadingDialog dialog = new LoadingDialog(context);
        dialog.show("正在激活反馈...");

        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("sid", sid + "");
        paramsMap.put("isSuccess", isSuccess);
        paramsMap.put("activateCode", activateCode);
        String data = CommonFunc.getJsonStr("feedback", paramsMap, "verify", Config.md5_key);
        LogUtils.e(TAG, "URL:" + Config.SBS_URL);
        MyOkHttp.get().postJson(context, Config.SBS_URL, data, new GsonResponseHandler<ApiResponse<ActivateApiResponse>>() {
            @Override
            public void onFailure(int statusCode, String error_msg) {
                dialog.dismiss();
                listener.onFailure("" + statusCode, error_msg);
            }

            @Override
            public void onSuccess(int statusCode, ApiResponse<ActivateApiResponse> response) {
                dialog.dismiss();
                if (response != null) {
                    if (response.getCode().equals("A00006")) {
                        listener.onSuccess(response.getResult());
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
     * 商博士-获取会员信息
     *
     * @param sid
     * @param mobile
     * @param tradeMoney
     * @param listener
     */
    public void getMemberInfo(final Context context, Long sid, String mobile, int tradeMoney, String icCardNo,
                              final ActionCallbackListener<CouponsResponse> listener) {
        final LoadingDialog dialog = new LoadingDialog(context);
        dialog.show("正在获取会员信息...");

        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("sid", sid);
        paramsMap.put("mobile", mobile);
        paramsMap.put("tradeMoney", tradeMoney);
        paramsMap.put("operator_num", SPUtils.get(context, Constants.USER_NAME, ""));
        paramsMap.put("serialNum", StringUtils.getSerial());
        paramsMap.put("icCardNo", icCardNo);


        String data = CommonFunc.getJsonStr("benefits", paramsMap, "verify", Config.md5_key);

        MyOkHttp.get().postJson(context, Config.SBS_URL, data, new GsonResponseHandler<ApiResponse<CouponsResponse>>() {
            @Override
            public void onFailure(int statusCode, String error_msg) {
                dialog.dismiss();
                listener.onFailure("" + statusCode, error_msg);
            }

            @Override
            public void onSuccess(int statusCode, ApiResponse<CouponsResponse> response) {
                dialog.dismiss();
                if (response != null) {
                    if (response.getCode().equals("A00006")) {
                        listener.onSuccess(response.getResult());
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
     * 商博士-会员交易金额计算
     *
     * @param request
     * @param listener
     */
    public void memberTransAmount(final Context context, MemberTransAmountRequest request,
                                  final ActionCallbackListener<MemberTransAmountResponse> listener) {

        final LoadingDialog dialog = new LoadingDialog(context);
        dialog.show("正在会员交易计算...");

        Map<String, Object> paramsMap = new HashMap<String, Object>();

        paramsMap.put("sid", request.getSid());
        paramsMap.put("memberCardNo", request.getMemberCardNo());
        paramsMap.put("password", request.getPassword());
        paramsMap.put("tradeMoney", request.getTradeMoney());
        paramsMap.put("point", request.getPoint());
        paramsMap.put("couponSn", request.getCouponSn());
        paramsMap.put("memberName", request.getMemberName());
        paramsMap.put("clientOrderNo", request.getClientOrderNo());

        String data = CommonFunc.getJsonStr("tranMoney", paramsMap, "verify", Config.md5_key);
        MyOkHttp.get().postJson(context, Config.SBS_URL, data, new GsonResponseHandler<ApiResponse<MemberTransAmountResponse>>() {
            @Override
            public void onFailure(int statusCode, String error_msg) {
                dialog.dismiss();
                listener.onFailure("" + statusCode, error_msg);
            }

            @Override
            public void onSuccess(int statusCode, ApiResponse<MemberTransAmountResponse> response) {
                dialog.dismiss();
                if (response != null) {
                    if (response.getCode().equals("A00006")) {
                        listener.onSuccess(response.getResult());
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
     * 商博士-异业优惠券锁定
     *
     * @param listener
     */
    public void otherCouponLock(final Context context, Long sid, String couponCode, String orderNo, int orderAmount,
                                  final ActionCallbackListener<MemberTransAmountResponse> listener) {

        final LoadingDialog dialog = new LoadingDialog(context);
        dialog.show("正在会员交易计算...");

        Map<String, Object> paramsMap = new HashMap<String, Object>();

        paramsMap.put("sid", sid);
        paramsMap.put("couponCode", couponCode);
        paramsMap.put("orderNo", orderNo);
        paramsMap.put("orderAmount",orderAmount);


        String data = CommonFunc.getJsonStr("otherCouponLock", paramsMap, "verify", Config.md5_key);
        MyOkHttp.get().postJson(context, Config.SBS_URL, data, new GsonResponseHandler<ApiResponse<MemberTransAmountResponse>>() {
            @Override
            public void onFailure(int statusCode, String error_msg) {
                dialog.dismiss();
                listener.onFailure("" + statusCode, error_msg);
            }

            @Override
            public void onSuccess(int statusCode, ApiResponse<MemberTransAmountResponse> response) {
                dialog.dismiss();
                if (response != null) {
                    if (response.getCode().equals("A00006")) {
                        listener.onSuccess(response.getResult());
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
     * 商博士-交易流水上送
     *
     * @param request
     * @param listener
     */
    public void transUpload(final Context context, TransUploadRequest request,
                            final ActionCallbackListener<TransUploadResponse> listener) {


        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("sid", request.getSid());
        paramsMap.put("cardNo", request.getCardNo());
        paramsMap.put("payCardNo", request.getPayCardNo());
        paramsMap.put("password", request.getPassword());
        paramsMap.put("cash", request.getCash());
        paramsMap.put("bankAmount", request.getBankAmount());
        paramsMap.put("couponCoverAmount", request.getCouponCoverAmount());
        paramsMap.put("pointCoverAmount", request.getPointCoverAmount());
        paramsMap.put("couponSns", request.getCouponSns());
        paramsMap.put("clientOrderNo", request.getClientOrderNo());
        paramsMap.put("activateCode", request.getActivateCode());
        paramsMap.put("merchantNo", request.getMerchantNo());
        paramsMap.put("t", request.getT());
        paramsMap.put("transNo", request.getTransNo());
        paramsMap.put("authCode", request.getAuthCode());
        paramsMap.put("serialNum", request.getSerialNum());
        paramsMap.put("payType", request.getPayType());
        paramsMap.put("pointAmount", request.getPointAmount());
        paramsMap.put("operator_num", SPUtils.get(context, Constants.USER_NAME, ""));


        String data = CommonFunc.getJsonStr("tranUploadPos", paramsMap, "verify", Config.md5_key);

        MyOkHttp.get().postJson(context, Config.SBS_URL, data, new GsonResponseHandler<ApiResponse<TransUploadResponse>>() {
            @Override
            public void onFailure(int statusCode, String error_msg) {
                listener.onFailure("" + statusCode, error_msg);
            }

            @Override
            public void onSuccess(int statusCode, ApiResponse<TransUploadResponse> response) {
                if (response != null) {
                    if (response.getCode().equals("A00006")) {
                        listener.onSuccess(response.getResult());
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
     * 商博士-获取打印信息
     *
     * @param sid
     * @param clientOrderNo
     * @param listener
     */
    public void getPrinterData(final Context context, Long sid, String clientOrderNo,
                               final ActionCallbackListener<TransUploadResponse> listener) {


        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("sid", sid);
        paramsMap.put("clientOrderNo", clientOrderNo);
        String data = CommonFunc.getJsonStr("getPrintData", paramsMap, "verify", Config.md5_key);

        MyOkHttp.get().postJson(context, Config.SBS_URL, data, new GsonResponseHandler<ApiResponse<TransUploadResponse>>() {
            @Override
            public void onFailure(int statusCode, String error_msg) {
                listener.onFailure("" + statusCode, error_msg);
            }

            @Override
            public void onSuccess(int statusCode, ApiResponse<TransUploadResponse> response) {
                if (response != null) {
                    if (response.getCode().equals("A00006")) {
                        if (response.getResult() != null) {
                            listener.onSuccess(response.getResult());
                        } else {
                            listener.onFailure("", "结果数据返回为空");
                        }
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
     * 商博士-富友扫码信息获取
     *
     * @param context
     * @param sid
     * @param listener
     */
    public void getSmData(final Context context, Long sid,
                          final ActionCallbackListener<QueryScanReturn> listener) {
        final LoadingDialog dialog = new LoadingDialog(context);
        dialog.show("正在获取支付通道...");
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("sid", sid);
        String data = CommonFunc.getJsonStr("queryScanType", paramsMap, "verify", Config.md5_key);
        LogUtils.e(TAG, data);
        LogUtils.e(TAG, Config.SBS_URL);
        MyOkHttp.get().postJson(context, Config.SBS_URL, data, new GsonResponseHandler<ApiResponse<QueryScanReturn>>() {
            @Override
            public void onFailure(int statusCode, String error_msg) {
                dialog.dismiss();
                listener.onFailure("" + statusCode, error_msg);
            }

            @Override
            public void onSuccess(int statusCode, ApiResponse<QueryScanReturn> response) {
                LogUtils.e(TAG, response.toString());
                dialog.dismiss();
                if (response != null) {
                    if (response.getCode().equals("A00006")) {
                        listener.onSuccess(response.getResult());
                    } else {
                        listener.onFailure(response.getCode(), response.getMsg());
                    }
                } else {
                    listener.onFailure("", "链接服务器异常");
                }
            }
        });
    }


    public void transCancelRefund(final Context context, TransUploadRequest request,
                                  final ActionCallbackListener<String> listener) {

        final LoadingDialog dialog = new LoadingDialog(context);
        dialog.show("正在上送退款信息...");

        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("sid", request.getSid());
        paramsMap.put("old_trade_order_num", request.getOld_trade_order_num());
        paramsMap.put("new_trade_order_num", request.getNew_trade_order_num());
        paramsMap.put("action", request.getAction());
        paramsMap.put("payType", request.getPayType());
        paramsMap.put("authCode", request.getAuthCode());
        paramsMap.put("t", request.getT());
        paramsMap.put("operator_num", SPUtils.get(context, Constants.USER_NAME, ""));

        String data = CommonFunc.getJsonStr("trade_cancel", paramsMap, "verify", Config.md5_key);

        MyOkHttp.get().postJson(context, Config.SBS_URL, data, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                dialog.dismiss();
                if (response == null) {
                    listener.onFailure("", "链接服务器异常");
                } else {
                    LogUtils.e(TAG, response.toString());
                    try {
                        String code = response.getString("code");
                        String msg = response.getString("msg");

                        if (code.equals("A00006")) {
                            listener.onSuccess(msg);
                        } else {
                            listener.onFailure("", msg);
                        }

                    } catch (JSONException e) {
                        listener.onFailure("", "数据解析失败");
                    }
                }

            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                dialog.dismiss();
                listener.onFailure("" + statusCode, error_msg);
            }


        });

    }

    public void transPacketCancelRefund(final Context context, TransUploadRequest request,
                                        final ActionCallbackListener<String> listener) {

        final LoadingDialog dialog = new LoadingDialog(context);
        dialog.show("正在上送退款信息...");

        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("sid", request.getSid());
        paramsMap.put("old_trade_order_num", request.getOld_trade_order_num());
        paramsMap.put("new_trade_order_num", request.getNew_trade_order_num());
        paramsMap.put("action", request.getAction());
        paramsMap.put("payType", request.getPayType());
//        paramsMap.put("authCode", request.getAuthCode());
        paramsMap.put("t", request.getT());
        paramsMap.put("operator_num", SPUtils.get(context, Constants.USER_NAME, ""));

        String data = CommonFunc.getJsonStr("trade_packet_cancel", paramsMap, "verify", Config.md5_key);
        MyOkHttp.get().postJson(context, Config.SBS_URL, data, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                dialog.dismiss();
                if (response == null) {
                    listener.onFailure("", "链接服务器异常");
                } else {
                    LogUtils.e(TAG, response.toString());
                    try {
                        String code = response.getString("code");
                        String msg = response.getString("msg");

                        if (code.equals("A00006")) {
                            listener.onSuccess(msg);
                        } else {
                            listener.onFailure("", msg);
                        }

                    } catch (JSONException e) {
                        listener.onFailure("", "数据解析失败");
                    }
                }

            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                dialog.dismiss();
                listener.onFailure("" + statusCode, error_msg);
            }


        });

    }


    /**
     * 商博士-取消订单
     */
    public void OrderCancel(final Context context, String orderNo) {//,  final ActionCallbackListener<String> listener) {

        final LoadingDialog dialog = new LoadingDialog(context);
//        dialog.show("正在取消订单...");

        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("orderNo", orderNo);
        String data = CommonFunc.getJsonStr("orderCancel", paramsMap, "verify", Config.md5_key);
        LogUtils.e(TAG, Config.SBS_URL);
        MyOkHttp.get().postJson(context, Config.SBS_URL, data, new GsonResponseHandler<ApiResponse<String>>() {
            @Override
            public void onSuccess(int statusCode, ApiResponse<String> response) {
//                dialog.dismiss();
                if (response != null) {
                    if (response.getCode().equals("A00006")) {
//                        listener.onSuccess(response.getResult());
                    } else {
//                        listener.onFailure(response.getCode(), response.getMsg());
                    }
                } else {
//                    listener.onFailure("", "链接服务器异常");
                }
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
//                dialog.dismiss();
//                listener.onFailure("" + statusCode, error_msg);
            }
        });
    }


    /**
     * 商博士-获取会员信息
     *
     * @param sid
     * @param start_time
     * @param end_time
     * @param listener
     */
    public void shift_room(final Context context, Long sid, long start_time, long end_time,
                           final ActionCallbackListener<ShiftRoom> listener) {
        final LoadingDialog dialog = new LoadingDialog(context);
        dialog.show("正在获取班接信息...");

        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("sid", sid);
        paramsMap.put("start_time", start_time);
        paramsMap.put("end_time", end_time);
        String data = CommonFunc.getJsonStr("shift_time", paramsMap, "verify", Config.md5_key);

        MyOkHttp.get().postJson(context, Config.SBS_URL, data, new GsonResponseHandler<ApiResponse<ShiftRoom>>() {
            @Override
            public void onFailure(int statusCode, String error_msg) {
                dialog.dismiss();
                listener.onFailure("" + statusCode, error_msg);
            }

            @Override
            public void onSuccess(int statusCode, ApiResponse<ShiftRoom> response) {
                dialog.dismiss();
                if (response != null) {
                    if (response.getCode().equals("A00006")) {
                        listener.onSuccess(response.getResult());
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
     * 券码核销 识别
     *
     * @param context
     * @param sid
     * @param cardNo
     * @param *       @param listener
     */
    public void ticketcheck(final Context context, Long sid, String cardNo, String sn, final ActionCallbackListener<TicektResponse> listener) {
        final LoadingDialog dialog = new LoadingDialog(context);
        dialog.show("加载中...");

        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("sid", sid);
        paramsMap.put("couponCode", cardNo);
        paramsMap.put("terminalSerial", sn);

        String data = CommonFunc.getJsonStr("couponInfo", paramsMap, "verify", Config.md5_key);
        LogUtils.e(TAG, "URL: " + Config.SBS_URL);

        MyOkHttp.get().postJson(context, Config.SBS_URL, data, new GsonResponseHandler<ApiResponse<TicektResponse>>() {
            @Override
            public void onSuccess(int statusCode, ApiResponse<TicektResponse> response) {
                dialog.dismiss();
                if (response != null) {
                    if (response.getCode().equals("A00006")) {
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

    /**
     * 异业券码 识别
     *
     * @param context
     * @param sid
     * @param cardNo
     * @param *       @param listener
     */
    public void yyticketcheck(final Context context, Long sid, String cardNo, final ActionCallbackListener<YyTicektResponse> listener) {
        final LoadingDialog dialog = new LoadingDialog(context);
        dialog.show("加载中...");

        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("sid", sid);
        paramsMap.put("couponCode", cardNo);

        String data = CommonFunc.getJsonStr("otherCouponInfo", paramsMap, "verify", Config.md5_key);
        LogUtils.e(TAG, "URL: " + Config.SBS_URL);

        MyOkHttp.get().postJson(context, Config.SBS_URL, data, new GsonResponseHandler<ApiResponse<YyTicektResponse>>() {
            @Override
            public void onSuccess(int statusCode, ApiResponse<YyTicektResponse> response) {
                dialog.dismiss();
                if (response != null) {
                    if (response.getCode().equals("A00006")) {
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

    /**
     * 券码核销
     *
     * @param context
     * @param sid
     * @param cardNo
     * @param sn
     * @param listener
     */
    public void ticketPay(final Context context, Long sid, String cardNo, String sn, String orderNo, final ActionCallbackListener<String> listener) {
        final LoadingDialog dialog = new LoadingDialog(context);
        dialog.show("加载中...");

        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("sid", sid);
        paramsMap.put("couponCode", cardNo);
        paramsMap.put("terminalSerial", sn);
        paramsMap.put("orderNo", orderNo);

        String data = CommonFunc.getJsonStr("couponVerify", paramsMap, "verify", Config.md5_key);
        LogUtils.e(TAG, "URL: " + Config.SBS_URL);

        MyOkHttp.get().postJson(context, Config.SBS_URL, data, new GsonResponseHandler<ApiResponse<Object>>() {
            @Override
            public void onSuccess(int statusCode, ApiResponse<Object> response) {
                dialog.dismiss();
                if (response != null) {
                    if (response.getCode().equals("A00006")) {
                        listener.onSuccess(response.getMsg());
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


    /**
     * 开卡、换卡
     *
     * @param context
     * @param sid
     * @param listener
     */
    public void openCard(final Context context, Long sid, String mobile, String icCardNo, String username, final ActionCallbackListener<ApiResponse<VipCardNo>> listener) {
        final LoadingDialog dialog = new LoadingDialog(context);
        dialog.show("加载中...");

        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("sid", sid);
        paramsMap.put("mobile", mobile);
        paramsMap.put("icCardNo", icCardNo);
        paramsMap.put("username", username);
        paramsMap.put("operator_num", SPUtils.get(context, Constants.USER_NAME, ""));

        String data = CommonFunc.getJsonStr("new_card", paramsMap, "verify", Config.md5_key);
        LogUtils.e(TAG, "URL: " + Config.SBS_URL);

        MyOkHttp.get().postJson(context, Config.SBS_URL, data, new GsonResponseHandler<ApiResponse<VipCardNo>>() {
            @Override
            public void onSuccess(int statusCode, ApiResponse<VipCardNo> response) {
                dialog.dismiss();
                if (response != null) {
                    if (response.getCode().equals("A00006")) {
                        if (response.getResult().getResCode() == 100 || response.getResult().getResCode() == 2){
                            listener.onSuccess(response);
                        }else{
                            listener.onFailure(""+response.getResult().getResCode(), response.getResult().getResMsg());
                        }

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


    public void confirmBindCard(final Context context, Long sid, String mobile, String icCardNo, final ActionCallbackListener<ApiResponse<VipCardNo>> listener){
        final LoadingDialog dialog = new LoadingDialog(context);
        dialog.show("加载中...");

        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("sid", sid);
        paramsMap.put("mobile", mobile);
        paramsMap.put("icCardNo", icCardNo);
        paramsMap.put("operator_num", SPUtils.get(context, Constants.USER_NAME, ""));

        String data = CommonFunc.getJsonStr("confirm_bind_card", paramsMap, "verify", Config.md5_key);
        LogUtils.e(TAG, "URL: " + Config.SBS_URL);

        MyOkHttp.get().postJson(context, Config.SBS_URL, data, new GsonResponseHandler<ApiResponse<VipCardNo>>() {
            @Override
            public void onSuccess(int statusCode, ApiResponse<VipCardNo> response) {
                dialog.dismiss();
                if (response != null) {
                    if (response.getCode().equals("A00006")) {

                       listener.onSuccess(response);


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


    /**
     * 换卡
     * @param context
     * @param sid
     * @param mobile
     * @param icCardNo
     * @param listener
     */
    public void changeCard(final Context context, Long sid, String mobile, String icCardNo,  final ActionCallbackListener<ApiResponse<VipCardNo>> listener){
        final LoadingDialog dialog = new LoadingDialog(context);
        dialog.show("加载中...");

        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("sid", sid);
        paramsMap.put("mobile", mobile);
        paramsMap.put("icCardNo", icCardNo);
        paramsMap.put("operator_num", SPUtils.get(context, Constants.USER_NAME, ""));

        String data = CommonFunc.getJsonStr("change_card", paramsMap, "verify", Config.md5_key);
        LogUtils.e(TAG, "URL: " + Config.SBS_URL);

        MyOkHttp.get().postJson(context, Config.SBS_URL, data, new GsonResponseHandler<ApiResponse<VipCardNo>>() {
            @Override
            public void onSuccess(int statusCode, ApiResponse<VipCardNo> response) {
                dialog.dismiss();
                if (response != null) {
                    if (response.getCode().equals("A00006")) {

                        listener.onSuccess(response);


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


    /**
     * 充值套餐获取
     *
     * @param context
     * @param sid
     * @param listener
     */
    public void recharge(final Context context, Long sid, final ActionCallbackListener<RechargeMeal> listener) {
        final LoadingDialog dialog = new LoadingDialog(context);
        dialog.show("正在获取充值套餐...");

        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("sid", sid);

        String data = CommonFunc.getJsonStr("get_recharge_info", paramsMap, "verify", Config.md5_key);
        LogUtils.e(TAG, "URL: " + Config.SBS_URL);

        MyOkHttp.get().postJson(context, Config.SBS_URL, data, new GsonResponseHandler<ApiResponse<RechargeMeal>>() {
            @Override
            public void onFailure(int statusCode, String error_msg) {
                dialog.dismiss();
                listener.onFailure("" + statusCode, error_msg);
            }

            @Override
            public void onSuccess(int statusCode, ApiResponse<RechargeMeal> response) {
                dialog.dismiss();
                if (response != null) {
                    if (response.getCode().equals("A00006")) {
                        listener.onSuccess(response.getResult());
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
     * 充值确认
     * @param context
     * @param sid
     * @param cardNo
     * @param listener
     */
    public void rechargeSure(final Context context, Long sid, long amount, String orderNo, String cardNo, final ActionCallbackListener<CardId> listener){
        final LoadingDialog dialog = new LoadingDialog(context);
        dialog.show("加载中...");

        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("sid", sid);
        paramsMap.put("orderNo", orderNo);
        paramsMap.put("payAmount", amount);
        paramsMap.put("mobile", cardNo);

        String data = CommonFunc.getJsonStr("rechargeAmountQuery", paramsMap, "verify", Config.md5_key);
        LogUtils.e(TAG, "URL: " + Config.SBS_URL);

        MyOkHttp.get().postJson(context, Config.SBS_URL, data, new GsonResponseHandler<ApiResponse<CardId>>() {
            @Override
            public void onSuccess(int statusCode, ApiResponse<CardId> response) {
                dialog.dismiss();
                if (response != null) {
                    if (response.getCode().equals("A00006")) {
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


    /**
     * 充值上送
     * @param context
     * @param request
     * @param listener
     */
    public void rechargePay(final Context context, RechargeUpLoad request, final ActionCallbackListener<ChargeBlance> listener){
        final LoadingDialog dialog = new LoadingDialog(context);
        dialog.show("加载中...");

        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("sid", request.getSid());
        paramsMap.put("payAmount", request.getPayAmount());
        paramsMap.put("orderNo", request.getOrderNo());
        paramsMap.put("activateCode", request.getActivateCode());
        paramsMap.put("merchantNo", request.getMerchantNo());
        paramsMap.put("t", request.getT());
        paramsMap.put("transNo", request.getTransNo());
        paramsMap.put("authCode", request.getAuthCode());
        paramsMap.put("serialNum", request.getSerialNum());
        paramsMap.put("payType", request.getPayType());
        paramsMap.put("operator_num", request.getOperator_num());
        paramsMap.put("promotion_num", request.getPromotion_num());

        String data = CommonFunc.getJsonStr("rechargeTradeUpload", paramsMap, "verify", Config.md5_key);
        LogUtils.e(TAG, "URL: " + Config.SBS_URL);

        MyOkHttp.get().postJson(context, Config.SBS_URL, data, new GsonResponseHandler<ApiResponse<ChargeBlance>>() {
            @Override
            public void onSuccess(int statusCode, ApiResponse<ChargeBlance> response) {
                dialog.dismiss();
                if (response != null) {
                    if (response.getCode().equals("A00006")) {
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


    /**
     * 会员卡支付
     * @param context
     * @param listener
     */
    public void qbPay(final Context context, StkPayRequest request, final ActionCallbackListener<TransUploadResponse> listener){


        Map<String, Object> paramsMap = new HashMap<String, Object>();

        paramsMap.put("sid", request.getSid());
        paramsMap.put("qrCode", request.getQrCode());
        paramsMap.put("cardNo", request.getCardNo());
        paramsMap.put("orderNo", request.getOrderNo());
        paramsMap.put("activateCode", request.getActivateCode());
        paramsMap.put("merchantNo", request.getMerchantNo());
        paramsMap.put("serialNum", request.getSerialNum());
        paramsMap.put("payPassword", request.getPayPassword());
        paramsMap.put("operator_num", request.getOperator_num());
        paramsMap.put("couponCoverAmount", request.getCouponCoverAmount());
        paramsMap.put("pointCoverAmount", request.getPointAmount());
        paramsMap.put("couponSns", request.getCouponSns());
        paramsMap.put("pointAmount", request.getPointAmount());
        paramsMap.put("payAmount", request.getPayAmount());
        paramsMap.put("t", request.getT());
        paramsMap.put("transNo", request.getTransNo());

        String data = CommonFunc.getJsonStr("qbPay", paramsMap, "verify", Config.md5_key);
        LogUtils.e(TAG, "URL: " + Config.SBS_URL);

        MyOkHttp.get().postJson(context, Config.SBS_URL, data, new GsonResponseHandler<ApiResponse<TransUploadResponse>>() {
            @Override
            public void onSuccess(int statusCode, ApiResponse<TransUploadResponse> response) {
//                dialog.dismiss();
                if (response != null) {
                    if (response.getCode().equals("A00006")) {
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
//                dialog.dismiss();
                listener.onFailure("" + statusCode, error_msg);
            }
        });
    }


    /**
     * 会员卡支付
     * @param context
     * @param listener
     */
    public void StkPay(final Context context, StkPayRequest request, final ActionCallbackListener<TransUploadResponse> listener){


        Map<String, Object> paramsMap = new HashMap<String, Object>();

        paramsMap.put("sid", request.getSid());
        paramsMap.put("cardNo", request.getCardNo());
        paramsMap.put("orderNo", request.getOrderNo());
        paramsMap.put("activateCode", request.getActivateCode());
        paramsMap.put("merchantNo", request.getMerchantNo());
        paramsMap.put("serialNum", request.getSerialNum());
        paramsMap.put("payPassword", request.getPayPassword());
        paramsMap.put("operator_num", request.getOperator_num());
        paramsMap.put("couponCoverAmount", request.getCouponCoverAmount());
        paramsMap.put("pointCoverAmount", request.getPointAmount());
        paramsMap.put("couponSns", request.getCouponSns());
        paramsMap.put("pointAmount", request.getPointAmount());
        paramsMap.put("payAmount", request.getPayAmount());
        paramsMap.put("t", request.getT());
        paramsMap.put("transNo", request.getTransNo());

        String data = CommonFunc.getJsonStr("icCardPay", paramsMap, "verify", Config.md5_key);
        LogUtils.e(TAG, "URL: " + Config.SBS_URL);

        MyOkHttp.get().postJson(context, Config.SBS_URL, data, new GsonResponseHandler<ApiResponse<TransUploadResponse>>() {
            @Override
            public void onSuccess(int statusCode, ApiResponse<TransUploadResponse> response) {
//                dialog.dismiss();
                if (response != null) {
                    if (response.getCode().equals("A00006")) {
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
//                dialog.dismiss();
                listener.onFailure("" + statusCode, error_msg);
            }
        });
    }





    /**
     * 修改实体卡支付密码
     * @param context
     * @param cardNo
     * @param newPass
     * @param oldPass
     * @param listener
     */
    public void changePass(final Context context, String cardNo, String newPass, String oldPass, final ActionCallbackListener<String> listener){
        final LoadingDialog dialog = new LoadingDialog(context);
        dialog.show("加载中...");

        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("icCardNo", cardNo);
        paramsMap.put("oldPassword", oldPass);
        paramsMap.put("newPassword", newPass);

        String data = CommonFunc.getJsonStr("changePassword", paramsMap, "verify", Config.md5_key);
        LogUtils.e(TAG, "URL: " + Config.SBS_URL);

        MyOkHttp.get().postJson(context, Config.SBS_URL, data, new GsonResponseHandler<ApiResponse<Object>>() {
            @Override
            public void onSuccess(int statusCode, ApiResponse<Object> response) {
                dialog.dismiss();
                if (response != null) {
                    if (response.getCode().equals("A00006")) {
                        listener.onSuccess(response.getMsg());
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
