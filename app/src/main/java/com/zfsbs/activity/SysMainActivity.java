package com.zfsbs.activity;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hd.core.HdAction;
import com.hd.model.HdAdjustScoreResponse;
import com.tool.utils.activityManager.AppManager;
import com.tool.utils.dialog.LoadingDialog;
import com.tool.utils.dialog.SignDialog;
import com.tool.utils.utils.LogUtils;
import com.tool.utils.utils.SPUtils;
import com.tool.utils.utils.StringUtils;
import com.tool.utils.utils.ToastUtils;
import com.zfsbs.R;
import com.zfsbs.common.CommonFunc;
import com.zfsbs.config.Config;
import com.zfsbs.config.Constants;
import com.zfsbs.core.action.FyBat;
import com.zfsbs.core.action.Printer;
import com.zfsbs.core.action.RicherQb;
import com.zfsbs.core.myinterface.ActionCallbackListener;
import com.zfsbs.model.Couponsn;
import com.zfsbs.model.FailureData;
import com.zfsbs.model.FyMicropayRequest;
import com.zfsbs.model.FyMicropayResponse;
import com.zfsbs.model.FyQueryRequest;
import com.zfsbs.model.FyQueryResponse;
import com.zfsbs.model.FyRefundResponse;
import com.zfsbs.model.RechargeUpLoad;
import com.zfsbs.model.RicherGetMember;
import com.zfsbs.model.SbsPrinterData;
import com.zfsbs.model.TransUploadRequest;
import com.zfsbs.model.TransUploadResponse;
import com.zfsbs.model.ZfQbResponse;
import com.zfsbs.myapplication.MyApplication;

import org.litepal.crud.DataSupport;

import java.util.List;


public class SysMainActivity extends BaseActivity implements OnClickListener {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_system);
//		AppManager.getAppManager().addActivity(this);
		initTitle("系统设置");
		initView();
	}

	private void initView() {

		linearLayout(R.id.id_ll_login).setOnClickListener(this);
		linearLayout(R.id.id_ll_pay_info).setOnClickListener(this);
		linearLayout(R.id.id_ll_bj).setOnClickListener(this);
		linearLayout(R.id.id_ll_sm_end_query).setOnClickListener(this);
		linearLayout(R.id.id_ll_change_master_pass).setOnClickListener(this);
		linearLayout(R.id.id_ll_sale_manager).setOnClickListener(this);
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.id_ll_login:
				CommonFunc.startAction(this, GetLoginInfoActivity1.class, true);
				break;
			case R.id.id_ll_pay_info:
				CommonFunc.startAction(this, LoginInfoActivity.class, false);
				break;
//			case R.id.id_ll_change_pwd:
//				CommonFunc.startAction(this, ChangePassActivity.class, false);
//				break;
			case R.id.id_ll_bj:
				CommonFunc.startAction(this, ShiftRoomActivity.class, false);
				break;
			case R.id.id_ll_sm_end_query:
				endQuery1();
				break;
			case R.id.id_ll_change_master_pass:
				CommonFunc.startAction(this, MasterChangePass.class, false);
				break;
			case R.id.id_ll_sale_manager:
                CommonFunc.startAction(this, HsSaleManagerActivity.class, false);
                break;
			default:
				break;
		}
	}

	/**
	 * 末笔查询
	 */
	private void endQuery1() {

		if (CommonFunc.recoveryFailureInfo(this) == null) {
			ToastUtils.CustomShow(this, "暂无末笔，无需查询");
			return;
		}
		switch (CommonFunc.recoveryFailureInfo(this).getPay_type()) {
			case Constants.PAY_WAY_QB:
				ZfQbQuery();
				break;
			case Constants.PAY_WAY_ALY:
			case Constants.PAY_WAY_WX:
				if (CommonFunc.recoveryFailureInfo(this).getFaiureType() == Constants.FY_FAILURE_PAY) {
					ZfFyPayQuery();
				} else if (CommonFunc.recoveryFailureInfo(this).getFaiureType() == Constants.FY_FAILURE_QUERY) {
					ZfFyQuery();
				}
				break;
		}
	}

	/**
	 * 钱包末笔查询
	 */
	private void ZfQbQuery() {

		printerData = new SbsPrinterData();

		CommonFunc.ZfQbFailQuery(this, new ActionCallbackListener<ZfQbResponse>() {
			@Override
			public void onSuccess(ZfQbResponse data) {

				FailureData failureData = CommonFunc.recoveryFailureInfo(SysMainActivity.this);
				//流水上送
				setQbPay1(data, failureData.getOrderNo(),
						failureData.getTime(), failureData.getTraceNum(), failureData.getCardNo());
			}

			@Override
			public void onFailure(String errorEvent, String message) {
				ToastUtils.CustomShow(SysMainActivity.this, errorEvent + "#" + message);
			}

			@Override
			public void onFailurTimeOut(String s, String error_msg) {
				ToastUtils.CustomShow(SysMainActivity.this, s + "#" + error_msg);
			}

			@Override
			public void onLogin() {
				AppManager.getAppManager().finishAllActivity();
				if (Config.OPERATOR_UI_BEFORE) {
					CommonFunc.startAction(SysMainActivity.this, OperatorLoginActivity.class, false);
				} else {
					CommonFunc.startAction(SysMainActivity.this, OperatorLoginActivity1.class, false);
				}
			}
		});
	}


	private FyBat.FYPayResultEvent listener1 = new FyBat.FYPayResultEvent() {
		@Override
		public void onSuccess(FyMicropayResponse data) {


			setFySmPay1(data);
		}

		@Override
		public void onSuccess(FyQueryResponse data) {
			//先判断本地数据是否存在，防止从华尔街平台拿到的是上一笔成功的交易
			SbsPrinterData datas = DataSupport.findLast(SbsPrinterData.class);
			if (!StringUtils.isEmpty(datas.getAuthCode()) && datas.getAuthCode().equals(data.getMchnt_order_no())) {
				ToastUtils.CustomShow(SysMainActivity.this, "请确认消费者交易成功。");
				return;
			}
			setFySmPayQurey1(data);
		}

		@Override
		public void onSuccess(FyRefundResponse data) {

		}

		@Override
		public void onFailure(int statusCode, String error_msg, String pay_type, String amount) {

		}

		@Override
		public void onFailure(FyMicropayRequest data) {

		}

		@Override
		public void onFailure(FyQueryRequest data) {

		}

		@Override
		public void onLogin() {

		}
	};

	/**
	 * 富友扫码支付异常处理
	 */
	private void ZfFyPayQuery() {
		printerData = new SbsPrinterData();
		FyBat fybat = new FyBat(this, listener1);
		fybat.terminalQuery1(CommonFunc.recoveryFailureInfo(this).getOrder_type(), CommonFunc.recoveryFailureInfo(this).getAmount(), true,
				CommonFunc.recoveryFailureInfo(this).getOutOrderNo());
	}

	/**
	 * 富友扫码查询异常处理
	 */
	private void ZfFyQuery() {
		printerData = new SbsPrinterData();
		FyBat fybat = new FyBat(this, listener1);
		fybat.query1(this, CommonFunc.recoveryFailureInfo(this).getOrder_type(), CommonFunc.recoveryFailureInfo(this).getOrderNo(),
				CommonFunc.recoveryFailureInfo(this).getOutOrderNo());
	}


	/**
	 * 富友扫码支付参数设置
	 *
	 * @param data
	 */
	private void setFySmPay1(FyMicropayResponse data) {
		printerData.setMerchantName(MyApplication.getInstance().getLoginData().getFyMerchantName());
		printerData.setMerchantNo(MyApplication.getInstance().getLoginData().getFyMerchantNo());
		printerData.setTerminalId(StringUtils.getTerminalNo(StringUtils.getSerial()));
		printerData.setOperatorNo((String) SPUtils.get(this, Constants.USER_NAME, ""));
		printerData.setTransNo(data.getTransaction_id());
		printerData.setAuthCode(data.getMchnt_order_no());
		printerData.setDateTime(StringUtils.formatTime(data.getTxn_begin_ts()));
		printerData.setOrderAmount(CommonFunc.recoveryMemberInfo(this).getTradeMoney());
		printerData.setAmount(StringUtils.formatStrMoney(data.getTotal_amount()));
		printerData.setPointCoverMoney(CommonFunc.recoveryMemberInfo(this).getPointCoverMoney());
		printerData.setCouponCoverMoney(CommonFunc.recoveryMemberInfo(this).getCouponCoverMoney());
		printerData.setScanPayType(MyApplication.getInstance().getLoginData().getScanPayType());
		if (data.getOrder_type().equals(Constants.PAY_FY_ALY)) {
			printerData.setPayType(Constants.PAY_WAY_ALY);
		} else if (data.getOrder_type().equals(Constants.PAY_FY_WX)) {
			printerData.setPayType(Constants.PAY_WAY_WX);
		}

		if (CommonFunc.recoveryFailureInfo(this).getApp_type() == Config.APP_SBS) {
			TransUploadRequest request = CommonFunc.setTransUploadData(printerData, CommonFunc.recoveryMemberInfo(this),
					data.getOutOrderNum(), printerData.getTransNo(), printerData.getAuthCode()
			);
			printerData.setClientOrderNo(data.getOutOrderNum());
			transUploadAction1(request);
		}

	}


	/**
	 * 扫码支付查询异常
	 *
	 * @param data
	 */
	private void setFySmPayQurey1(FyQueryResponse data) {
		printerData.setMerchantName(MyApplication.getInstance().getLoginData().getFyMerchantName());
		printerData.setMerchantNo(MyApplication.getInstance().getLoginData().getFyMerchantNo());
		printerData.setTerminalId(StringUtils.getTerminalNo(StringUtils.getSerial()));
		printerData.setOperatorNo((String) SPUtils.get(this, Constants.USER_NAME, ""));
		printerData.setTransNo(data.getTransaction_id());
		printerData.setAuthCode(data.getMchnt_order_no());
		printerData.setDateTime(StringUtils.getCurTime());
		printerData.setOrderAmount(CommonFunc.recoveryMemberInfo(this).getTradeMoney());
		printerData.setAmount(StringUtils.formatStrMoney(data.getOrder_amt()));
		printerData.setPointCoverMoney(CommonFunc.recoveryMemberInfo(this).getPointCoverMoney());
		printerData.setCouponCoverMoney(CommonFunc.recoveryMemberInfo(this).getCouponCoverMoney());
		printerData.setScanPayType(MyApplication.getInstance().getLoginData().getScanPayType());
		if (data.getOrder_type().equals(Constants.PAY_FY_ALY)) {
			printerData.setPayType(Constants.PAY_WAY_ALY);
		} else if (data.getOrder_type().equals(Constants.PAY_FY_WX)) {
			printerData.setPayType(Constants.PAY_WAY_WX);
		}

		if (CommonFunc.recoveryFailureInfo(this).getApp_type() == Config.APP_SBS) {
			TransUploadRequest request = CommonFunc.setTransUploadData(printerData, CommonFunc.recoveryMemberInfo(this),
					data.getOutOrderNum(), printerData.getTransNo(), printerData.getAuthCode()
			);
			printerData.setClientOrderNo(data.getOutOrderNum());
			transUploadAction1(request);
		}

	}

	/**
	 * 设置钱包参数
	 *
	 * @param data
	 * @param orderNo
	 * @param time
	 * @param traceNum
	 */
	private void setQbPay1(ZfQbResponse data, String orderNo, String time, String traceNum, String cardNo) {
		printerData.setMerchantName(MyApplication.getInstance().getLoginData().getTerminalName());
		printerData.setMerchantNo(data.getGroupId());
		printerData.setTerminalId(StringUtils.getSerial());
		printerData.setOperatorNo((String) SPUtils.get(this, Constants.USER_NAME, ""));
		printerData.setCardNo(cardNo);
		printerData.setDateTime(time);
		printerData.setClientOrderNo(orderNo);
		printerData.setTransNo(traceNum);
		printerData.setAuthCode(data.getSystemOrderNo());
		printerData.setDateTime(StringUtils.formatTime(time));
		printerData.setOrderAmount(CommonFunc.recoveryMemberInfo(this).getTradeMoney());
		printerData.setAmount(StringUtils.formatIntMoney(CommonFunc.recoveryMemberInfo(this).getRealMoney()));
		printerData.setPointCoverMoney(CommonFunc.recoveryMemberInfo(this).getPointCoverMoney());
		printerData.setCouponCoverMoney(CommonFunc.recoveryMemberInfo(this).getCouponCoverMoney());
		printerData.setPayType(Constants.PAY_WAY_QB);

		if (CommonFunc.recoveryFailureInfo(this).getApp_type() == Config.APP_SBS) {
			TransUploadRequest request = CommonFunc.setTransUploadData(printerData, CommonFunc.recoveryMemberInfo(this),
					CommonFunc.getNewClientSn(), printerData.getTransNo(), printerData.getAuthCode()
			);
			//这个地方保持和支付的时候一直
			request.setClientOrderNo(orderNo);
			if (StringUtils.isEmpty(request.getCardNo())){
				request.setCardNo(cardNo);
			}
			transUploadAction1(request);
		} else if (CommonFunc.recoveryFailureInfo(this).getApp_type() == Config.APP_HD) {
			TransUploadRequest request = CommonFunc.setTransUploadData(printerData, CommonFunc.recoveryMemberInfo(this),
					CommonFunc.getNewClientSn(), printerData.getTransNo(), printerData.getAuthCode()
			);
			//这个地方保持和支付的时候一直
			request.setClientOrderNo(orderNo);
			transUploadAction2(request);
		}


	}

	/**
	 * 流水上送
	 *
	 * @param request
	 */
	private void transUploadAction1(final TransUploadRequest request) {
		final LoadingDialog dialog = new LoadingDialog(this);
		dialog.show("正在上传交易流水...");
		dialog.setCancelable(false);
		this.sbsAction.transUpload(this, request, new ActionCallbackListener<TransUploadResponse>() {
			@Override
			public void onSuccess(TransUploadResponse data) {

				setTransUpLoadData(request);
				// 设置流水返回的数据
				setTransUpdateResponse(data, dialog, true);
			}

			@Override
			public void onFailure(String errorEvent, String message) {
				dialog.dismiss();
				ToastUtils.CustomShow(SysMainActivity.this, errorEvent + "#" + message);


				setTransUpLoadData(request);
				// 设置当前交易流水需要上送
				printerData.setUploadFlag(true);
				printerData.setApp_type(CommonFunc.recoveryFailureInfo(SysMainActivity.this).getApp_type());
				// 保存打印的数据，不保存图片数据
				PrinterDataSave();
				// 打印
				Printer.print(printerData, SysMainActivity.this);
			}

			@Override
			public void onFailurTimeOut(String s, String error_msg) {

			}

			@Override
			public void onLogin() {
				dialog.dismiss();
				AppManager.getAppManager().finishAllActivity();
				if (Config.OPERATOR_UI_BEFORE) {
					CommonFunc.startAction(SysMainActivity.this, OperatorLoginActivity.class, false);
				} else {
					CommonFunc.startAction(SysMainActivity.this, OperatorLoginActivity1.class, false);
				}
			}
		});
	}

	/**
	 * 流水上送
	 *
	 * @param request
	 */
	private void transUploadAction2(final TransUploadRequest request) {
		final LoadingDialog dialog = new LoadingDialog(this);
		dialog.show("正在计算积分...");
		dialog.setCancelable(false);
		this.sbsAction.transUpload(this, request, new ActionCallbackListener<TransUploadResponse>() {
			@Override
			public void onSuccess(TransUploadResponse data) {
				dialog.dismiss();
				setTransUpLoadData(request);
				printerData.setApp_type(CommonFunc.recoveryFailureInfo(SysMainActivity.this).getApp_type());
				printerData.setPoint(data.getPoint());
				printerData.setPhoneNo(request.getPhone());
				// 上送积分
				HdAction.HdAdjustScore(SysMainActivity.this, request.getPhone(), data.getPoint(), new HdAction.HdCallResult() {
					@Override
					public void onSuccess(String data) {

						HdAdjustScoreResponse response = new Gson().fromJson(data, HdAdjustScoreResponse.class);

						//保存流水号和总积分
						printerData.setPointCurrent(Integer.parseInt(response.getResult().getScoreTotal()));
						printerData.setFlowNo(response.getResult().getFlowNo());

						// 保存打印的数据，不保存图片数据
						PrinterDataSave();
						// 打印
						Printer.print(printerData, SysMainActivity.this);
					}

					@Override
					public void onFailed(String errorCode, String message) {
						ToastUtils.CustomShow(SysMainActivity.this, errorCode + "#" + message);
						// 保存打印的数据，不保存图片数据
						PrinterDataSave();
						// 打印
						Printer.print(printerData, SysMainActivity.this);
					}
				});
			}

			@Override
			public void onFailure(String errorEvent, String message) {
				dialog.dismiss();
				ToastUtils.CustomShow(SysMainActivity.this, errorEvent + "#" + message);


				setTransUpLoadData(request);
				// 设置当前交易流水需要上送
				printerData.setUploadFlag(true);
				printerData.setApp_type(CommonFunc.recoveryFailureInfo(SysMainActivity.this).getApp_type());
				// 保存打印的数据，不保存图片数据
				PrinterDataSave();
				// 打印
				Printer.print(printerData, SysMainActivity.this);
			}

			@Override
			public void onFailurTimeOut(String s, String error_msg) {

			}

			@Override
			public void onLogin() {
				dialog.dismiss();
				AppManager.getAppManager().finishAllActivity();
				if (Config.OPERATOR_UI_BEFORE) {
					CommonFunc.startAction(SysMainActivity.this, OperatorLoginActivity.class, false);
				} else {
					CommonFunc.startAction(SysMainActivity.this, OperatorLoginActivity1.class, false);
				}
			}
		});
	}


	private void Richer_transUploadAction(final TransUploadRequest request) {
		final LoadingDialog dialog = new LoadingDialog(this);
		dialog.show("正在上传交易流水...");
		dialog.setCancelable(false);

		RicherQb.UploadTransInfo(SysMainActivity.this, request, new ActionCallbackListener<RicherGetMember>() {
			@Override
			public void onSuccess(RicherGetMember data) {
				dialog.dismiss();
				setTransUpLoadData(request);
				// 设置流水返回的数据
//                setTransUpdateResponse(data, dialog, true);
				// 设置当前交易流水需要上送
				printerData.setUploadFlag(false);

				if (Config.isSign){
					final SignDialog dialog = new SignDialog(SysMainActivity.this, new SignDialog.OnClickInterface() {
						@Override
						public void onClickSure(Bitmap bitmap) {
							printerData.setSign_bitmap(bitmap);
							PrinterDataSave();
							// 打印
							Printer.print(printerData, SysMainActivity.this);
						}

					});
					dialog.setCancelable(false);
					dialog.show();
				}else {

					PrinterDataSave();
					// 打印
					Printer.print(printerData, SysMainActivity.this);
				}
			}


			@Override
			public void onFailure(String errorEvent, String message) {
				dialog.dismiss();
				ToastUtils.CustomShow(SysMainActivity.this, errorEvent + "#" + message);


				setTransUpLoadData(request);
				// 设置当前交易流水需要上送
				printerData.setUploadFlag(true);
				// 保存打印的数据，不保存图片数据
				PrinterDataSave();
				// 打印
				Printer.print(printerData, SysMainActivity.this);
			}

			@Override
			public void onLogin() {
				AppManager.getAppManager().finishAllActivity();
				if (Config.OPERATOR_UI_BEFORE) {
					CommonFunc.startAction(SysMainActivity.this, OperatorLoginActivity.class, false);
				} else {
					CommonFunc.startAction(SysMainActivity.this, OperatorLoginActivity1.class, false);
				}
			}

			@Override
			public void onFailurTimeOut(String s, String error_msg) {

			}
		});
	}


	private void setTransUpLoadData(TransUploadRequest request) {
		Gson gson = new Gson();
		String data = gson.toJson(request);
		LogUtils.e(data);
		printerData.setTransUploadData(data);
	}


	private Handler mhandler = new Handler() {
		public void handleMessage(Message msg) {
			Bundle bundle = msg.getData();
			Bitmap point_bitmap = bundle.getParcelable("point_bitmap");
			Bitmap title_bitmap = bundle.getParcelable("title_bitmap");
			printerData.setPoint_bitmap(point_bitmap);
			printerData.setCoupon_bitmap(title_bitmap);


			// 打印
			Printer.print(printerData, SysMainActivity.this);

		}

		;
	};


	private void setCounponData(List<Couponsn> data){
		Gson gson = new GsonBuilder().serializeNulls().create();
		String counponStr = gson.toJson(data);
		printerData.setCouponData(counponStr);
	}

	protected void setTransUpdateResponse(final TransUploadResponse data, final LoadingDialog dialog, boolean flag) {
		printerData.setPoint_url(data.getPoint_url());
		printerData.setPoint(data.getPoint());
		printerData.setPointCurrent(data.getPointCurrent());
		setCounponData(data.getCoupon());
//        printerData.setCoupon(data.getCoupon());
//        printerData.setTitle_url(data.getTitle_url());
//        printerData.setMoney(data.getMoney());
		printerData.setBackAmt(data.getBackAmt());
		printerData.setApp_type(CommonFunc.recoveryFailureInfo(this).getApp_type());
		if (flag) {
			// 保存打印的数据，不保存图片数据
			PrinterDataSave();
		}
		new Thread(new Runnable() {

			@Override
			public void run() {

				Bitmap point_bitmap = Constants.ImageLoad(data.getPoint_url());
				Bitmap title_bitmap = Constants.ImageLoad(data.getCoupon_url());
//				LogUtils.e(point_bitmap.getByteCount()+"");
//				LogUtils.e(title_bitmap.getByteCount()+"");
				dialog.dismiss();

				Message msg = new Message();
				Bundle bundle = new Bundle();
				bundle.putParcelable("point_bitmap", point_bitmap);
				bundle.putParcelable("title_bitmap", title_bitmap);
				msg.setData(bundle);
				mhandler.sendMessage(msg);

			}
		}).start();

	}

	private void PrinterDataSave() {
		CommonFunc.ClearFailureInfo(this);
		CommonFunc.PrinterDataDelete();
		printerData.setStatus(true);
		if (printerData.save()) {
			LogUtils.e("打印数据存储成功");
		} else {
			LogUtils.e("打印数据存储失败");
		}
	}


	private void rechargeUpload(final RechargeUpLoad rechargeUpLoad) {
//		sbsAction.rechargePay(mContext, rechargeUpLoad, new ActionCallbackListener<ChargeBlance>() {
//			@Override
//			public void onSuccess(ChargeBlance data) {
////                ToastUtils.CustomShow(ZfPayRechargeActivity.this, data);
//				setRechargeUpLoadData(rechargeUpLoad);
//				printerData.setPromotion_num(rechargeUpLoad.getPromotion_num());
//				printerData.setPacektRemian(data.getPacket_remain());
//				printerData.setRealize_card_num(data.getRealize_card_num());
//				printerData.setSh_name(data.getSh_name());
//				printerData.setMember_name(data.getMember_name());
//				printerData.setRecharge_order_num(data.getRecharge_order_num());
//				PrinterDataSave();
//
//
//
//				// 打印
//				Printer.getInstance(mContext).print(printerData, mContext);
//			}
//
//			@Override
//			public void onFailure(String errorEvent, String message) {
//				ToastUtils.CustomShow(SysMainActivity.this, errorEvent + "#" + message);
//
//				setRechargeUpLoadData(rechargeUpLoad);
//				printerData.setUploadFlag(true);
//				printerData.setApp_type(CommonFunc.recoveryFailureInfo(SysMainActivity.this).getApp_type());
//				// 保存打印的数据，不保存图片数据
//				PrinterDataSave();
//				// 打印
//				Printer.print(printerData, SysMainActivity.this);
//			}
//
//			@Override
//			public void onFailurTimeOut(String s, String error_msg) {
//
//			}
//
//			@Override
//			public void onLogin() {
//
//			}
//		});
	}


}
