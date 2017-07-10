package com.myhslib.core;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;

import com.myhslib.myinterface.Emv;
import com.tool.utils.dialog.LoadingDialog;
import com.tool.utils.utils.GsonUtils;
import com.tool.utils.utils.LogUtils;
import com.tool.utils.utils.StringUtils;
import com.tool.utils.utils.ToastUtils;
import com.wizarpos.hspos.api.EnumCommand;
import com.wizarpos.hspos.api.EnumProgressCode;
import com.wizarpos.hspos.api.HuashiApi;
import com.wizarpos.hspos.api.ParamInfo;
import com.wizarpos.hspos.api.SettleInfo;
import com.wizarpos.hspos.api.TradeListener;
import com.wizarpos.hspos.api.TransInfo;

import static com.wizarpos.hspos.api.EnumCommand.DownloadAID;

public class EmvImpl implements Emv {

    private static final String TAG = "EmvImpl";
    private static EmvImpl emvImpl;

    private TransInfo transInfo;
    private ParamInfo paramInfo;
    private SettleInfo settleInfo;

    // 状态信息
    private int errorCode;
    private String errorMessage;
    private int curProcessCode;
    private String curProcessMessage;

    private final int PROGRESS_NOTIFIER = 1;
    private final int SUCCESS_NOTIFIER = 2;
    private final int FAIL_NOTIFIER = 3;
    private EnumCommand curCommand;

    private HuashiApi posApi;
    private Context mContext;
    private LoadingDialog dialog;
    private int trans_amount;
    private int oldTraceNo;
    private String oldRrn;
    private String oldDate;


    private TransResult transResult;

    public interface TransResult<T> {
        public void success(T transInfo);

        public void failed(String error);
    }

    public EmvImpl(Context context) {
        posApi = HuashiApi.getInstance();
        String tmpEmvLibDir = context.getApplicationContext().getDir("", 0).getAbsolutePath();
        tmpEmvLibDir = tmpEmvLibDir.substring(0, tmpEmvLibDir.lastIndexOf('/')) + "/lib/libEMVKernal.so";
        posApi.setContext(context.getApplicationContext(), tmpEmvLibDir);
        posApi.setTradeListener(tradeListener);
    }

    public synchronized static EmvImpl getInstance(Context context) {
        if (emvImpl == null) {
            emvImpl = new EmvImpl(context);
        }
        return emvImpl;
    }

    private LoadingDialog CustomDialog(Context context, String message) {
        if (dialog == null) {
            dialog = new LoadingDialog(context);
            dialog.setOnKeyListener(KeyListener);
        }
        mContext = context;
        dialog.show(message);
        return dialog;
    }

    public void customDialogDismiss() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    private OnKeyListener KeyListener = new OnKeyListener() {

        @Override
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                LogUtils.e("================onkey===========");
                if (curProcessCode != 13) {
                    customDialogDismiss();
                }
                if (curProcessCode == 12) {
                    LogUtils.e("================onkey 12===========");
                    posApi.endTrans();
                }
            }
            return false;
        }
    };

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PROGRESS_NOTIFIER:
                    LogUtils.e(TAG + ":onProgress", "onProgress[" + curCommand.getCmdCode() + "][" + curProcessCode + "]["
                            + curProcessMessage + "]");

                    if (curProcessCode == EnumProgressCode.InputTransAmount.getCode()) {
                        transInfo.setTransAmount(trans_amount);//(manager.getTrans_amount());
                    } else if (curProcessCode == EnumProgressCode.InputAuthCode.getCode()) {
//					transInfo.setAuthCode(
//							(String) SPUtils.get(mContext, Constants.HS_AUTH1, Constants.DEFAULT_HS_AUTH1));
                    } else if (curProcessCode == EnumProgressCode.InputOldRRN.getCode()) {
                        transInfo.setOldRRN(oldRrn);
                    } else if (curProcessCode == EnumProgressCode.InputOldTicket.getCode()) {
                        transInfo.setOldTrace(oldTraceNo);
                    } else if (curProcessCode == EnumProgressCode.InputOldTransDate.getCode()) {
                        transInfo.setOldTransDate(oldDate); // MMDD
                    } else if (curProcessCode == EnumProgressCode.ShowTransTotal.getCode()) {
                        LogUtils.e("交易累计：\r\n内卡借记 " + settleInfo.getCupDebitCount() + "/" + settleInfo.getCupDebitAmount()
                                + "\r\n外卡借记 " + settleInfo.getAbrDebitCount() + "/"
                                + settleInfo.getAbrDebitAmount());
                    } else if (curProcessCode == EnumProgressCode.InputPIN.getCode()) {
                        LogUtils.e("EnumProgressCode.InputPIN.getCode()");
                        ToastUtils.CustomShowLong(mContext, "请在密码框输入密码!");

                    }

                    if (curProcessCode == 13){
                        dialog.setCancelable(false);
                    }

                    switch (curCommand) {
                        case Balance:
                            posApi.balance(transInfo);
                            break;
                        case Sale:
                            LogUtils.e("<<<<Sale>>>>");
                            if (curProcessCode == EnumProgressCode.ProcessOnline.getCode()) {
                                dialog.show("正在连接服务器...");
                            } else {
//						dialog.show(curProcessMessage);
                            }
                            posApi.sale(transInfo);

                            break;
                        case VoidSale:
                            if (curProcessCode == EnumProgressCode.ProcessOnline.getCode()) {
                                dialog.show("正在连接服务器...");
                            } else {
//						dialog.show(curProcessMessage);
                            }
                            posApi.voidSale(transInfo);


                            break;
                        case Refund:
                            posApi.refund(transInfo);
                            break;
                        case Login:
                            posApi.login(transInfo);
                            break;
                        case Settle:
                            posApi.settle(settleInfo.getTid(), settleInfo.getMid());
                            break;
                        case InitKey:
                            posApi.initKey(transInfo);
                            break;
                        case DownloadAID:
//					dialog.show("正在接收数据...");
                            posApi.downloadAID();
                            break;
                        case DownloadCAPK:
                            posApi.downloadCAPK();
                            break;

                    }
                    break;
                case SUCCESS_NOTIFIER:
                    switch (curCommand) {
                        // 管理类
                        // 1.获取参数
                        case GetParam:
                            ToastUtils.CustomShow(mContext, "获取参数 完成");
                            break;
                        // 2.设置参数
                        case SetParam:
//					ToastUtils.CustomShow(mContext, "设置参数 完成");
                            if (transResult != null) {
                                transResult.success(null);
                            }
                            break;
                        // 3.下载主密钥
                        case InitKey:
                            ToastUtils.CustomShow(mContext,
                                    "下载主密钥 完成, \r\nrespCode[" + transInfo.getRespCode() + "]\r\nrespDesc["
                                            + transInfo.getRespDesc() + "]\r\nrespMid[" + transInfo.getMid() + "]\r\nrespTid["
                                            + transInfo.getTid() + "]");

                            LogUtils.e(TAG, "InitKey mid :" + transInfo.getMid() + " tid: " + transInfo.getTid());
                            if (transInfo.getRespCode().equals("00")) {
//						SaveMidTid(transInfo.getMid(), transInfo.getTid());
                                transResult.success(transInfo);
                            }
                            break;
                        // 4.签到
                        case Login:

                            if (transInfo.getRespCode().equals("00")) {
                                ToastUtils.CustomShow(mContext, "签到 成功, \r\nrespCode[" + transInfo.getRespCode()
                                        + "]\r\nrespDesc[" + transInfo.getRespDesc() + "]");
                                //如果签到成功说明主密钥下载过了，不用在下载了，直接设置为true
                                transResult.success(transInfo);
                            } else {
                                ToastUtils.CustomShow(mContext, "签到 失败, \r\nrespCode[" + transInfo.getRespCode()
                                        + "]\r\nrespDesc[" + transInfo.getRespDesc() + "]");
                            }

                            break;
                        // 5.结算
                        case Settle:
//					ToastUtils.CustomShow(mContext, "结算 完成, \r\n内卡消费[" + settleInfo.getCupDebitCount() + ", "
//							+ settleInfo.getCupDebitAmount() + "]");
//                            String settleData = GsonUtils.parseObjToJson(settleInfo);
//                            LogUtils.e(StringUtils.isEmpty(settleData) ? "数据为空" : settleData);
//                            transResult.success(settleInfo);


//                            if (settleInfo.getRespCode().equals("00")){
                                String settleData = GsonUtils.parseObjToJson(settleInfo);
                                LogUtils.e(StringUtils.isEmpty(settleData) ? "数据为空" : settleData);
                                transResult.success(settleInfo);
                                ToastUtils.CustomShow(mContext, "结算 完成");
//                            }else {
//                                transResult.failed(settleInfo.getRespCode() + "#" + settleInfo.getRespDesc());
//                            }


                            break;
                        case DownloadAID:
                            LogUtils.e("TIME", StringUtils.getFormatCurTime());
                            ToastUtils.CustomShow(mContext, "下载AID参数 完成, \r\nrespCode[" + transInfo.getRespCode()
                                    + "]\r\nrespDesc[" + transInfo.getRespDesc() + "]");
                            if (transInfo.getRespCode().equals("00")) {
                                transResult.success(transInfo);
                            }
                            break;
                        case DownloadCAPK:
                            LogUtils.e("TIME", StringUtils.getFormatCurTime());
                            ToastUtils.CustomShow(mContext, "下载公钥参数 完成, \r\nrespCode[" + transInfo.getRespCode()
                                    + "]\r\nrespDesc[" + transInfo.getRespDesc() + "]");
                            if (transInfo.getRespCode().equals("00")) {
                                transResult.success(transInfo);
                            }
                            break;
                        // 金融交易类
                        // 1.查询余额
                        case Balance:
                            ToastUtils.CustomShow(mContext, "查询余额 完成, \r\nrespCode[" + transInfo.getRespCode()
                                    + "]\r\nrespDesc[" + transInfo.getRespDesc() + "]");
                            break;
                        // 2.消费
                        case Sale:
//					ToastUtils.CustomShow(mContext,
//							"消费 完成, \r\nrespCode[" + transInfo.getRespCode() + "]\r\nrespDesc["
//									+ transInfo.getRespDesc() + "]\r\n卡号[" + transInfo.getPan() + "]\r\n日期["
//									+ transInfo.getTransDate() + "]\r\n时间[" + transInfo.getTransTime() + "]");

                            if (transInfo.getRespCode().equals("00")) {
                                transResult.success(transInfo);
                                posApi.endTrans();

                            } else {
                                transResult.failed("消费失败, \r\nrespCode[" + transInfo.getRespCode() + "]\r\nrespDesc["
                                        + transInfo.getRespDesc() + "]\r\n卡号[" + transInfo.getPan() + "]\r\n日期["
                                        + transInfo.getTransDate() + "]\r\n时间[" + transInfo.getTransTime() + "]");
                                posApi.endTrans();

                            }
                            break;
                        // 3.消费撤销
                        case VoidSale:

                            if (transInfo.getRespCode().equals("00")) {
                                LogUtils.e("消费撤销 完成 , \r\nrespCode[" + transInfo.getRespCode()
                                        + "]\r\nrespDesc[" + transInfo.getRespDesc() + "]");
                                transResult.success(transInfo);
                            } else {
                                transResult.failed("respCode[" + transInfo.getRespCode()
                                        + "]\r\nrespDesc[" + transInfo.getRespDesc() + "]");
                                posApi.endTrans();
                            }
                            break;
                        case QueryLastTrans:
                            if (transInfo.getRespCode().equals("00")) {
                                transResult.success(transInfo);
//                                posApi.endTrans();

                            } else {
                                ToastUtils.CustomShow(mContext, "消费 完成, \r\nrespCode[" + transInfo.getRespCode() + "]\r\nrespDesc["
                                        + transInfo.getRespDesc() + "]\r\n卡号[" + transInfo.getPan() + "]\r\n日期["
                                        + transInfo.getTransDate() + "]\r\n时间[" + transInfo.getTransTime() + "]");
//                                posApi.endTrans();

                            }
                            break;
                        // 4.退货
                        case Refund:
                            LogUtils.e("退货 完成 , \r\nrespCode[" + transInfo.getRespCode()
                                    + "]\r\nrespDesc[" + transInfo.getRespDesc() + "]");
                            if (transInfo.getRespCode().equals("00")) {
                                transResult.success(transInfo);
                            } else {
                                transResult.failed("respCode[" + transInfo.getRespCode()
                                        + "]\r\nrespDesc[" + transInfo.getRespDesc() + "]");
                                posApi.endTrans();
                            }
                            break;
                        default:
                            break;
                    }
                    break;
                case FAIL_NOTIFIER:
                    if (curCommand == EnumCommand.GetParam || curCommand == EnumCommand.SetParam
                            || curCommand == EnumCommand.InitKey || curCommand == EnumCommand.Login
                            || curCommand == EnumCommand.Settle || curCommand == DownloadAID
                            || curCommand == EnumCommand.DownloadCAPK) {

                    } else {

                    }
                    if (curCommand == EnumCommand.SetParam) {
                        LogUtils.e("错误[" + errorCode + "][" + errorMessage + "]");
                    } else {
                        ToastUtils.CustomShow(mContext, "错误[" + errorCode + "][" + errorMessage + "]");
                        LogUtils.e("错误[" + errorCode + "][" + errorMessage + "]");
                        transResult.failed("错误[" + errorCode + "][" + errorMessage + "]");
                    }
                    break;

                default:
                    break;
            }
        }

        ;
    };

    private TradeListener tradeListener = new TradeListener() {

        @Override
        public void onTransSucceed(EnumCommand cmd, Object params) {
            LogUtils.e(TAG, "onTransSucceed");
            customDialogDismiss();
            curCommand = cmd;
            if (cmd == EnumCommand.GetParam) {
                paramInfo = (ParamInfo) params;
            } else if (cmd == EnumCommand.SetParam) {

            } else if (cmd == EnumCommand.Settle) {
                settleInfo = (SettleInfo) params;
            } else {
                transInfo = (TransInfo) params;
            }
            Message msg = new Message();
            msg.what = SUCCESS_NOTIFIER;
            handler.sendMessage(msg);
        }

        @Override
        public void onTransFailed(EnumCommand cmd, int error, String message) {
            LogUtils.e(TAG, "onTransFailed");
            customDialogDismiss();
            curCommand = cmd;
            errorCode = error;
            errorMessage = message;
            Message msg = new Message();
            msg.what = FAIL_NOTIFIER;
            handler.sendMessage(msg);
        }

        @Override
        public void onProgress(EnumCommand cmd, int progressCode, String message, Object params) {
            LogUtils.e(TAG, "onProgress");
            if (cmd == EnumCommand.Settle) {
                settleInfo = (SettleInfo) params;
            } else {
                transInfo = (TransInfo) params;
            }
            Message msg = new Message();
            msg.what = PROGRESS_NOTIFIER;
            curCommand = cmd;
            curProcessCode = progressCode;
            curProcessMessage = message;
            handler.sendMessage(msg);
        }
    };

    @Override
    public void onCommand(EnumCommand cmd) {

        switch (cmd) {
            // 管理类
            // 1.获取参数
            case GetParam:
                posApi.getParam();
                break;
            // 2.设置参数
            case SetParam:
//			posApi.setParam(setParmInfo());
                break;
            // 3.下载主密钥
            case InitKey:

//			posApi.initKey(setInitkey());
                break;
            // 4.签到
            case Login:
//			posApi.login(setTransInfo());
                break;
            // 5.结算
            case Settle:
//			setTransSettle();
                break;
            case DownloadAID:
                LogUtils.e("TIME", StringUtils.getFormatCurTime());
                posApi.downloadAID();
                break;
            case DownloadCAPK:
                LogUtils.e("TIME", StringUtils.getFormatCurTime());
                posApi.downloadCAPK();
                break;
            // 金融交易类
            // 1.查询余额
            case Balance:
//			posApi.balance(setTransInfo());
                break;
            // 2.消费
            case Sale:
//			posApi.sale(setTransInfo());
                break;
            // 3.消费撤销
            case VoidSale:
//			posApi.voidSale(setTransInfo());
                break;
            // 4.退货
            case Refund:
//			posApi.refund(setTransInfo());
                break;
            default:
                break;
        }
    }

//	protected void SaveMidTid(String mid, String tid) {
//		LoginApiResponse loginData = MyApplication.getInstance().getLoginData();
//		loginData.setMerchantNo(transInfo.getMid());
//		loginData.setTerminalNo(transInfo.getTid());
//		loginData.setDownMasterKey(true);
////		loginData.setTerminalName(transInfo.getMerchantName());
//
//		//更新数据库
//		ContentValues values = new ContentValues();
//		values.put("merchantNo", transInfo.getMid());
//		values.put("terminalNo", transInfo.getTid());
//		values.put("isDownMasterKey", true);
//		DataSupport.update(LoginApiResponse.class, values, loginData.getId());
//	}

//	protected void SaveLoginMasterKey() {
//		LoginApiResponse loginData = MyApplication.getInstance().getLoginData();
//		loginData.setDownMasterKey(true);
//
//		//更新数据库
//		ContentValues values = new ContentValues();
//		values.put("isDownMasterKey", true);
//		DataSupport.update(LoginApiResponse.class, values, loginData.getId());
//	}

//	private void setTransSettle() {
//		LoginApiResponse loginData = MyApplication.getInstance().getLoginData();
//		posApi.settle(loginData.getTerminalNo(), loginData.getMerchantNo());
//	}

//	private TransInfo setInitkey() {
//		transInfo = new TransInfo();
//
//		LoginApiResponse loginData = MyApplication.getInstance().getLoginData();
//		transInfo.setAuthCode(loginData.getOther());
//		transInfo.setMid(loginData.getMerchantNo());
//		transInfo.setTid(loginData.getTerminalNo());
//		LogUtils.e("授权码： "+transInfo.getAuthCode());
//		LogUtils.e("商户号： "+transInfo.getMid());
//		LogUtils.e("终端号： "+transInfo.getTid());
//
//		return transInfo;
//	}

//	private ParamInfo setParmInfo() {
//		ParamInfo param = new ParamInfo();
//
//		LoginApiResponse loginData = MyApplication.getInstance().getLoginData();
//		param.setMid(loginData.getMerchantNo());
//		param.setTid(loginData.getTerminalNo());
//		param.setServerIP((String) SPUtils.get(mContext, Constants.HS_IP, Constants.DEFAULT_HS_IP));
//		param.setServerPort(
//				Integer.parseInt((String) SPUtils.get(mContext, Constants.HS_PORT, Constants.DEFAULT_HS_PORT)));
//		param.setTpdu((String) SPUtils.get(mContext, Constants.HS_TPDU, Constants.DEFAULT_HS_TPDU));
//		param.setKeyIndex(loginData.getKeyIndex());
//		return param;
//	}

//	public TransInfo setTransInfo() {
//		transInfo = new TransInfo();
//
//		LoginApiResponse loginData = MyApplication.getInstance().getLoginData();
//		transInfo.setMid(loginData.getMerchantNo());
//		transInfo.setTid(loginData.getTerminalNo());
//		LogUtils.e("商户号： "+transInfo.getMid()+" 终端号： "+transInfo.getTid());
//		return transInfo;
//	}

//	public TransInfo setTransUndoInfo(int oldTraceNo) {
//		transInfo = new TransInfo();
//
//		LoginApiResponse loginData = MyApplication.getInstance().getLoginData();
//		transInfo.setMid(loginData.getMerchantNo());
//		transInfo.setTid(loginData.getTerminalNo());
//		transInfo.setOldTrace(oldTraceNo);
//		LogUtils.e("商户号： "+transInfo.getMid()+" 终端号： "+transInfo.getTid());
//		return transInfo;
//	}

    /**
     * 获取参数
     */
    public void getParam(Context context, TransResult result) {
//		mContext = context;
//		transResult = result;
//		onCommand(EnumCommand.GetParam);
    }


    public static final String DEFAULT_HS_IP = "58.246.226.173";// 测试环境："180.168.215.67";
    public static final String DEFAULT_HS_PORT = "8888";// "40004";//"10004";
    public static final String DEFAULT_HS_TPDU = "6000030000";

    /**
     * 设置参数
     */
    public void setParam(Context context, int keyIndex, String mid, String tid, TransResult result) {
        mContext = context;
        transResult = result;
//		onCommand(EnumCommand.SetParam);

        ParamInfo param = new ParamInfo();
        param.setMid(mid);
        param.setTid(tid);
        param.setServerIP(DEFAULT_HS_IP);
        param.setServerPort(Integer.parseInt(DEFAULT_HS_PORT));
        param.setTpdu(DEFAULT_HS_TPDU);
        param.setKeyIndex(keyIndex);

        posApi.setParam(param);
    }

    /**
     * 下载参数
     *
     * @param context
     * @param result
     */
    public void downLoadParams(Context context, TransResult result) {

    }

    /**
     * 下载主密钥
     */
    public void InitKey(Context context, TransResult result) {//, int mid) {
        CustomDialog(context, "正在下载主密钥,请耐心等待...");
//		manager.setMid(mid);
        transResult = result;
        onCommand(EnumCommand.InitKey);
    }

    /**
     * 下载主密钥
     */
    public void DownTmk(Context context, String other, String mid, String tid, TransResult result) {
        if (StringUtils.isEmpty(StringUtils.replaceBlank(other))) {
            ToastUtils.CustomShow(context, "授权码为空");
            return;
        }
        CustomDialog(context, "正在下载主密钥,请耐心等待...");
        transResult = result;


        transInfo = new TransInfo();
        transInfo.setAuthCode(StringUtils.replaceBlank(other));
        transInfo.setMid(mid);
        transInfo.setTid(tid);
        LogUtils.e("授权码： " + transInfo.getAuthCode());
        LogUtils.e("商户号： " + transInfo.getMid());
        LogUtils.e("终端号： " + transInfo.getTid());
        posApi.initKey(transInfo);
    }

    /**
     * 下载黑名单
     */
    public void DownBlackList(Context context, TransResult result) {

    }

    /**
     * 下载AID
     */
    public void downloadAid(Context context, TransResult result) {
        CustomDialog(context, "正在下载AID参数,请耐心等待...");
        transResult = result;
        onCommand(DownloadAID);
    }

    /**
     * 下载公钥
     */
    public void downloadCapk(Context context, TransResult result) {
        CustomDialog(context, "正在下载公钥参数,请耐心等待...");
        transResult = result;
        onCommand(EnumCommand.DownloadCAPK);
    }

    /**
     * 签到
     */
    public void login(Context context, String mid, String tid, TransResult result) {//, int mid) {
        CustomDialog(context, "正在签到...");
        transResult = result;
//		onCommand(EnumCommand.Login);
        transInfo = new TransInfo();
        transInfo.setMid(mid);
        transInfo.setTid(tid);
        LogUtils.e("商户号： " + transInfo.getMid() + " 终端号： " + transInfo.getTid());
        posApi.login(transInfo);
    }

    /**
     * 结算
     */
    public void settle(Context context, String mid, String tid, TransResult result) {//, int mid) {
        CustomDialog(context, "正在请求...");
        transResult = result;
//		onCommand(EnumCommand.Settle);
        posApi.settle(mid, tid);
    }

    /**
     * 查询余额
     */
    public void balance(Context context, String mid, String tid) {//, int mid) {
        CustomDialog(context, "正在查询余额...");
//		onCommand(EnumCommand.Balance);
        transInfo = new TransInfo();
        transInfo.setMid(mid);
        transInfo.setTid(tid);
        LogUtils.e("商户号： " + transInfo.getMid() + " 终端号： " + transInfo.getTid());
        posApi.balance(transInfo);
    }

    /**
     * 消费
     */
    public void sale(Context context, int amount, String mid, String tid, TransResult result) {
        CustomDialog(context, "请刷卡或者插卡");
        trans_amount = amount;
        transResult = result;
//		onCommand(EnumCommand.Sale);
        transInfo = new TransInfo();
        transInfo.setMid(mid);
        transInfo.setTid(tid);
        LogUtils.e("商户号： " + transInfo.getMid() + " 终端号： " + transInfo.getTid());
        posApi.sale(transInfo);
    }

    /**
     * 消费撤销
     */
    public void voidSale(Context context, int trace_no, String mid, String tid, TransResult result) {//, int mid) {
        CustomDialog(context, "请刷卡或者插卡");
        oldTraceNo = trace_no;
        transResult = result;
//		onCommand(EnumCommand.VoidSale);
        transInfo = new TransInfo();
        transInfo.setMid(mid);
        transInfo.setTid(tid);
        LogUtils.e("商户号： " + transInfo.getMid() + " 终端号： " + transInfo.getTid());
        posApi.voidSale(transInfo);
    }

    /**
     * 末笔查询
     */
    public void QueryLastTrans(Context context, int trace_no, String mid, String tid, TransResult result) {//, int mid) {
        CustomDialog(context, "正在请求...");
        transResult = result;
//        oldTraceNo = trace_no;
//        onCommand(EnumCommand.QueryLastTrans);
        transInfo = new TransInfo();
        transInfo.setOldTrace(trace_no);
//        LogUtils.e(TAG,"商户号： " + transInfo.getMid() + " 终端号： " + transInfo.getTid());
        posApi.queryLastTrans(transInfo);
    }

    /**
     * 退货
     */
    public void refund(Context context, int amount, String old_rrn, String old_date, String mid, String tid, TransResult result) {//, int mid) {
        CustomDialog(context, "请刷卡或者插卡");
        trans_amount = amount;
        oldRrn = old_rrn;
        oldDate = old_date;
        transResult = result;
//		onCommand(EnumCommand.Refund);
        transInfo = new TransInfo();
        transInfo.setMid(mid);
        transInfo.setTid(tid);
        LogUtils.e("商户号： " + transInfo.getMid() + " 终端号： " + transInfo.getTid());
        posApi.refund(transInfo);

    }

    public void bindService(Context context, TransResult result) {

    }

    public void unBindService(Context context) {

    }

}
