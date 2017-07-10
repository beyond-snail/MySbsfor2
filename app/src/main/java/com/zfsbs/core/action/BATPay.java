package com.zfsbs.core.action;

import android.content.Context;

import com.tool.utils.dialog.LoadingDialog;
import com.tool.utils.utils.LogUtils;
import com.tool.utils.utils.SPUtils;
import com.tool.utils.utils.StringUtils;
import com.wosai.upay.bean.UpayOrder;
import com.wosai.upay.bean.UpayOrder.PayModel;
import com.wosai.upay.bean.UpayResult;
import com.wosai.upay.common.DebugConfig;
import com.wosai.upay.common.UpayCallBack;
import com.wosai.upay.common.UpayTask;
import com.wosai.upay.http.Env;
import com.zfsbs.common.CommonFunc;
import com.zfsbs.config.Constants;
import com.zfsbs.core.myinterface.BatInterface;
import com.zfsbs.myapplication.MyApplication;

public class BATPay {

	private static Context mContext;

	// 收钱吧
	private String myOrderId = "";
	private static String sn = "";
	private static String myRefundNo = "";

	private static UpayTask upayTask;
	private static LoadingDialog dialog;
	private static boolean isOpen;

	public BATPay(Context context) {
		mContext = context;

		initSqbSDK();
	}

	/**
	 * initUpay 初始化 UpayTask
	 *
	 */
	private void initSqbSDK() {
		upayTask = UpayTask.getInstance();
		DebugConfig.setDebug(true);// 默认为非调试模式,如果需要调试,请设置为 true,打印和保存相关日志
		upayTask.initUpay(mContext, true, Env.UrlType.PRO);
	}

	private static void customDialogDismiss() {
		if (dialog != null) {
			dialog.dismiss();
			dialog = null;
		}
	}

	/** 激活 */
	public void activite(final BatInterface batResult) {
		String code = MyApplication.getInstance().getLoginData().getActiveCode();
		String vendor_id = (String) SPUtils.get(mContext, Constants.SQB_VENDOR_ID, Constants.DEFALULT_SQB_VENDOR_ID);// 服务商
		String vendor_key = (String) SPUtils.get(mContext, Constants.SQB_VENDOR_KEY, Constants.DEFALULT_SQB_VENDOR_KEY);// 服务商
		LogUtils.e("code=" + code);
		LogUtils.e("vendor_id=" + vendor_id);
		LogUtils.e("vendor_key=" + vendor_key);
		PayModel payModel = PayModel.NO_UI;

		dialog = new LoadingDialog(mContext);
		dialog.show("正在激活bat...");

		/**
		 * activate 激活
		 *
		 * @param code
		 *            激活码内容
		 * @param vendor_id
		 *            服务商 ID
		 * @param vendor_key
		 *            服务商 KEY
		 * @param payModel
		 *            支付模式
		 * @param callBack
		 *            请求回调
		 */
		upayTask.activate(code, vendor_id, vendor_key, payModel, new UpayCallBack() {
			@Override
			public void onExecuteResult(UpayResult result) {
				customDialogDismiss();
				LogUtils.e("返回结果：" + result);
				if (!StringUtils.isEmpty(result.getResult_code())) {
					if (result.getResult_code().equals("ACTIVATE_SUCCESS")) {
						LogUtils.e("激活成功");
						batResult.success_bat(result);
					} else {
						LogUtils.e("激活失败：" + result.getError_message());
						batResult.failed_bat("", result.getError_message());
					}
				} else {
					LogUtils.e("激活失败：" + result.getError_message());
					batResult.failed_bat("", result.getError_message());
				}

			}
		});
	}

	/** 付款 */
	public void pay(int PayWay, int amount, final BatInterface batResult) {
		//判断时间
		if (CommonFunc.isLogin(mContext, Constants.SBS_LOGIN_TIME, Constants.DEFAULT_SBS_LOGIN_TIME)) {
			batResult.onLogin();
			return;
		}

		UpayOrder order = new UpayOrder();

		// myOrderId = "P" + StringUtils.getFormatCurTime();

		order.setClient_sn(myOrderId);// 商户订单号
		order.setTotal_amount(amount + "");// 交易总金额
		order.setPayway("" + PayWay);// 支付方式
		order.setSubject((String) SPUtils.get(mContext, Constants.SQB_SUBJECT, Constants.DEFALULT_SQB_SUBJECT));// 交易简介
		order.setOperator(
				(String) SPUtils.get(mContext, Constants.SQB_ACTIVATE_CODE, Constants.DEFALULT_SQB_ACTIVATE_CODE));// 门店操作员
		order.setDescription(
				(String) SPUtils.get(mContext, Constants.SQB_DESCRIPTION, Constants.DEFALULT_SQB_DESCRIPTION));// 商品详情
		order.setReflect("测试反射参数");// 反射参数

		/**
		 * pay 付款
		 *
		 * @param order
		 *            业务订单类
		 * @param callBack
		 *            请求回调
		 */
		upayTask.pay(order, new UpayCallBack() {
			@Override
			public void onExecuteResult(final UpayResult result) {
//				LogUtils.e("返回结果：" + result);
//				if (!StringUtils.isEmpty(result.getResult_code())) {
//					if (result.getResult_code().equals("PAY_SUCCESS") && result != null) {
//						batResult.success_bat(result);
//					} else {
//						batResult.failed_bat("", result.getError_message());
//					}
//				}else {
//					batResult.failed_bat("", "交易失败");
//				}

//				if (result != null && !StringUtils.isEmpty(result.getResult_code()) && result.getResult_code().equals("PAY_SUCCESS")){
//					batResult.success_bat(result);
//				}else {
//
//					batResult.failed_bat("", "交易失败");
//				}

				if (result != null){
					if (!StringUtils.isEmpty(result.getResult_code())){
						if (result.getResult_code().equals("PAY_SUCCESS")) {
							batResult.success_bat(result);
						}else {
							if (!StringUtils.isEmpty(result.getError_code()) && !StringUtils.isEmpty(result.getError_message())) {
								batResult.failed_bat(result.getError_code(), result.getError_message());
							}else {
								batResult.failed_bat(result.getResult_code(), "");
							}
						}
					}else{
						batResult.failed_bat(result.getError_code(), result.getError_message());
					}
				}else {
					batResult.failed_bat("", "交易未知");
				}


			}

		});
	}

	/** 退款 */
	public void refund(String sn, String orderId, String amount, final BatInterface batResult) {
		//判断时间
		if (CommonFunc.isLogin(mContext, Constants.SBS_LOGIN_TIME, Constants.DEFAULT_SBS_LOGIN_TIME)) {
			batResult.onLogin();
			return;
		}

		UpayOrder order = new UpayOrder();
		order.setSn(sn);// 收钱吧订单号
		order.setClient_sn(orderId);// 商户订单号

		myRefundNo = "R" + StringUtils.getFormatCurTime();

		order.setRefund_request_no(myRefundNo);// 退款序列号
		order.setOperator((String) SPUtils.get(mContext, Constants.SQB_ACTIVATE_CODE, Constants.DEFALULT_SQB_ACTIVATE_CODE));// 操作员
		order.setRefund_amount(amount);// 退款金额
		order.setReflect("测试反射参数");// 反射参数
		order.setRefundModel(UpayOrder.RefundModel.CLIENT_SN);// 指定退款模式为商户订单号退款
																// RefundModel.SN/UpayOrder.RefundModel.CLIENT_SN
		order.setPayModel(PayModel.NO_UI);//

		final LoadingDialog dialog = new LoadingDialog(mContext);
		dialog.show("正在退款...");



		/**
		 * refund 退款
		 *
		 * @param order
		 *            业务订单类
		 * @param callBack
		 *            请求回调
		 */
		upayTask.refund(order, new UpayCallBack() {

			@Override
			public void onExecuteResult(UpayResult result) {
				dialog.dismiss();
//				LogUtils.e("返回结果：" + result.toString());
//				if (!StringUtils.isEmpty(result.getResult_code())) {
//					if (result.getResult_code().equals("REFUND_SUCCESS") && result != null) {
//						batResult.success_bat(result);
//					} else {
//						batResult.failed_bat("", result.getError_message());
//					}
//				}else {
//					batResult.failed_bat("", "交易失败");
//				}

				if (result != null){
					if (!StringUtils.isEmpty(result.getResult_code())){
						if (result.getResult_code().equals("REFUND_SUCCESS")) {
							batResult.success_bat(result);
						}else {
							if (!StringUtils.isEmpty(result.getError_code()) && !StringUtils.isEmpty(result.getError_message())) {
								batResult.failed_bat(result.getError_code(), result.getError_message());
							}else {
								batResult.failed_bat(result.getResult_code(), "");
							}
						}
					}else{
						batResult.failed_bat(result.getError_code(), result.getError_message());
					}
				}else {
					batResult.failed_bat("", "交易未知");
				}
			}

		});
	}

	/**
	 * 查询
	 * */
	public void query(String sn, String orderId,  final BatInterface batResult) {

		UpayOrder order = new UpayOrder();
		order.setSn(sn);// 收钱吧订单号
		order.setClient_sn(orderId);// 商户订单号


		final LoadingDialog dialog = new LoadingDialog(mContext);
		dialog.show("正在查询...");

		/**
		 * query 查询
		 *
		 * @param order
		 *            业务订单类
		 * @param callBack
		 *            请求回调
		 */
		upayTask.query(order, new UpayCallBack() {

			@Override
			public void onExecuteResult(UpayResult result) {
				if (result != null){
					if (!StringUtils.isEmpty(result.getResult_code())){
						if (result.getResult_code().equals("REFUND_SUCCESS")) {
							batResult.success_bat(result);
						}else {
							if (!StringUtils.isEmpty(result.getError_code()) && !StringUtils.isEmpty(result.getError_message())) {
								batResult.failed_bat(result.getError_code(), result.getError_message());
							}else {
								batResult.failed_bat(result.getResult_code(), "");
							}
						}
					}else{
						batResult.failed_bat(result.getError_code(), result.getError_message());
					}
				}else {
					batResult.failed_bat("", "交易未知");
				}
			}
		});
	}

	/** 撤单 */
	public void revoke() {

		UpayOrder order = new UpayOrder();
		order.setSn(sn);// 收钱吧订单号
		order.setClient_sn(myOrderId);// 商户订单号
		order.setReflect("测试反射参数");// 反射参数
		order.setRevokeModel(UpayOrder.RevokeModel.CLIENT_SN);// 指定退款模式为商户订单号退款

		/**
		 * revoke 撤单(退货)
		 *
		 * @param order
		 *            业务订单类
		 * @param callBack
		 *            请求回调
		 */
		upayTask.revoke(order, new UpayCallBack() {
			@Override
			public void onExecuteResult(UpayResult result) {
				System.out.println("result: " + result);
			}
		});
	}

	/** 预下单 */
	public void preCreate() {

		UpayOrder order = new UpayOrder();

		// myOrderId = "Y" + StringUtils.getFormatCurTime();

		order.setClient_sn(myOrderId);// 商户订单号
		order.setTotal_amount("1");// 交易总金额 (分)
		order.setPayway("1");// 支付方式 1支付宝 3 微信 4百付宝 5 京东
		order.setSubject("预付款测试");// 交易简介
		order.setOperator("预付款操作员 ");// 门店操作员
		order.setDescription("餐饮");// 商品详情
		order.setReflect("测试反射参数");// 反射参数

		/**
		 * preCreate 预下单
		 *
		 * @param order
		 *            业务订单类
		 * @param callBack
		 *            请求回调
		 */
		upayTask.preCreate(order, new UpayCallBack() {
			@Override
			public void onExecuteResult(UpayResult result) {
				System.out.println("result: " + result);
				sn = result.getSn();
			}
		});
	}

	public String getMyOrderId() {
		return myOrderId;
	}

	public void setMyOrderId(String myOrderId) {
		this.myOrderId = myOrderId;
	}

}
