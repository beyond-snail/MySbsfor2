package com.mycommonlib.core;

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

import com.mycommonlib.model.ComSettleInfo;
import com.mycommonlib.model.ComTransInfo;
import com.myhslib.core.EmvImpl;
import com.tool.utils.utils.GsonUtils;
import com.tool.utils.utils.LogUtils;
import com.tool.utils.utils.StringUtils;
import com.wizarpos.hspos.api.ParamInfo;
import com.wizarpos.hspos.api.SettleInfo;
import com.wizarpos.hspos.api.TransInfo;


/**********************************************************
 * *
 * Created by wucongpeng on 2017/2/26.        *
 **********************************************************/


public class PayCommon {


    public interface ComTransResult<T> {
        public void success(T transInfo);

        public void failed(String error);
    }


    /**
     * 获取参数
     *
     * @param context
     * @param comTransResult
     */
    public static void getParams(Context context, ComTransResult comTransResult) {
        EmvImpl.getInstance(context).getParam(context, new EmvImpl.TransResult<ParamInfo>() {
            @Override
            public void success(ParamInfo transInfo) {
            }

            @Override
            public void failed(String error) {

            }
        });
    }


    /**
     * 设置收单初始化
     *
     * @param context
     * @param keyIndex
     * @param comTransResult
     */
    public static void setParams(Context context, int keyIndex, String mid, String tid, final ComTransResult comTransResult) {
        LogUtils.e("再次设置参数");
        LogUtils.e("keyIndex = " + keyIndex +  " mid = "+ mid + " tid = "+tid);
        EmvImpl.getInstance(context).setParam(context, keyIndex, mid, tid, new EmvImpl.TransResult<TransInfo>() {
            @Override
            public void success(TransInfo transInfo) {
                if (comTransResult != null) {
                    comTransResult.success(null);
                }
            }

            @Override
            public void failed(String error) {
                if (comTransResult != null) {
                    comTransResult.failed("设置失败");
                }
            }
        });
    }


    /**
     * 下载参数
     *
     * @param context
     * @param comTransResult
     */
    public static void DownParams(final Context context, final ComTransResult comTransResult) {
        EmvImpl.getInstance(context).downLoadParams(context, new EmvImpl.TransResult<TransInfo>() {
            @Override
            public void success(TransInfo transInfo) {
                if (comTransResult != null) {
                    comTransResult.success(null);
                }
            }

            @Override
            public void failed(String error) {
                if (comTransResult != null) {
                    comTransResult.failed("下载失败");
                }
            }
        });
    }

    /**
     * 下载主密钥
     *
     * @param context
     * @param comTransResult
     */
    public static void DownMasterKey(Context context, String other, String mid, String tid, final ComTransResult comTransResult) {



        EmvImpl.getInstance(context).DownTmk(context, other, mid, tid, new EmvImpl.TransResult<TransInfo>() {

            @Override
            public void success(TransInfo transInfo) {
                if (comTransResult != null) {
                    comTransResult.success(null);
                }
            }

            @Override
            public void failed(String error) {
                if (comTransResult != null) {
                    comTransResult.failed("下载主密钥失败");
                }
            }
        });
    }


    /**
     * 下载AID
     *
     * @param context
     * @param comTransResult
     */
    public static void DownloadAid(Context context, final ComTransResult comTransResult) {
        EmvImpl.getInstance(context).downloadAid(context, new EmvImpl.TransResult<TransInfo>() {
            @Override
            public void success(TransInfo transInfo) {
                if (comTransResult != null) {
                    comTransResult.success(null);
                }
            }

            @Override
            public void failed(String error) {
                if (comTransResult != null) {
                    comTransResult.failed("下载AID失败");
                }
            }
        });
    }

    /**
     * 下载公钥
     *
     * @param context
     * @param comTransResult
     */
    public static void DownloadCapk(Context context, final ComTransResult comTransResult) {

        EmvImpl.getInstance(context).downloadCapk(context, new EmvImpl.TransResult<TransInfo>() {

            @Override
            public void success(TransInfo transInfo) {
                if (comTransResult != null) {
                    comTransResult.success(null);
                }
            }

            @Override
            public void failed(String error) {
                if (comTransResult != null) {
                    comTransResult.failed("下载Capk失败");
                }
            }
        });
    }


    /**
     * 下载黑名单
     *
     * @param context
     * @param comTransResult
     */
    public static void DownloadBlackList(Context context, final ComTransResult comTransResult) {
        EmvImpl.getInstance(context).DownBlackList(context, new EmvImpl.TransResult<TransInfo>() {
            @Override
            public void success(TransInfo transInfo) {
                if (comTransResult != null) {
                    comTransResult.success(null);
                }
            }

            @Override
            public void failed(String error) {
                if (comTransResult != null) {
                    comTransResult.failed("下载黑名单失败");
                }
            }
        });
    }


    /**
     * 签到
     *
     * @param context
     * @param comTransResult
     */
    public static void login(Context context, String mid, String tid, final ComTransResult comTransResult) {

        EmvImpl.getInstance(context).login(context, mid, tid, new EmvImpl.TransResult<TransInfo>() {

            @Override
            public void success(TransInfo transInfo) {
                if (comTransResult != null) {
                    comTransResult.success(null);
                }
            }

            @Override
            public void failed(String error) {
                if (comTransResult != null) {
                    comTransResult.failed("签到失败");
                }
            }

        });
    }


    /**
     * 交易
     *
     * @param context
     * @param amount
     * @param comTransResult
     */
    public static void sale(Context context, int amount, String mid, String tid, final ComTransResult comTransResult) {
        EmvImpl.getInstance(context).sale(context, amount, mid, tid, new EmvImpl.TransResult<TransInfo>() {

            @Override
            public void success(TransInfo transInfo) {
                ComTransInfo comTransInfo = GsonUtils.modelA2B(transInfo, ComTransInfo.class);
                comTransResult.success(comTransInfo);

            }

            @Override
            public void failed(String error) {
                comTransResult.failed(error);
            }

        });
    }

    /**
     * 末笔查询
     *
     * @param context
     * @param comTransResult
     */
    public static void QueryLastTrans(Context context, int trace_no, String mid, String tid, final ComTransResult comTransResult) {
        EmvImpl.getInstance(context).QueryLastTrans(context,trace_no, mid, tid, new EmvImpl.TransResult<TransInfo>() {

            @Override
            public void success(TransInfo transInfo) {
                String data = GsonUtils.parseObjToJson(transInfo);
                LogUtils.e(StringUtils.isEmpty(data) ? "数据为空" : data);
                ComTransInfo comTransInfo = GsonUtils.modelA2B(transInfo, ComTransInfo.class);
                comTransResult.success(comTransInfo);

            }

            @Override
            public void failed(String error) {
                comTransResult.failed(error);
            }

        });
    }


    /**
     * 结算
     *
     * @param context
     * @param mid
     * @param tid
     * @param comTransResult
     */
    public static void settle(Context context, String mid, String tid, final ComTransResult comTransResult) {

        EmvImpl.getInstance(context).settle(context, mid, tid, new EmvImpl.TransResult<SettleInfo>() {
            @Override
            public void success(SettleInfo settleInfo) {
                String settleData = GsonUtils.parseObjToJson(settleInfo);
                LogUtils.e(StringUtils.isEmpty(settleData) ? "数据为空" : settleData);
                if (comTransResult != null) {
                    if (StringUtils.isEmpty(settleData)){
                        comTransResult.failed("数据为空");
                    }else {
                        ComSettleInfo comSettleInfo = GsonUtils.modelA2B(settleInfo, ComSettleInfo.class);
                        comTransResult.success(comSettleInfo);
                    }
                }
            }

            @Override
            public void failed(String error) {
                if (comTransResult != null) {
                    comTransResult.failed("结算失败");
                }
            }
        });
    }


    public static void voidSale(Context context, int trace_no, String mid, String tid, final ComTransResult comTransResult) {

        EmvImpl.getInstance(context).voidSale(context, trace_no, mid, tid, new EmvImpl.TransResult<TransInfo>() {
            @Override
            public void success(TransInfo transInfo) {
//                ComTransInfo comTransInfo = new ComTransInfo();
//                try {
//                    BeanUtils.copyProperties(comTransInfo, transInfo);
//                    comTransResult.success(comTransInfo);
//                } catch (IllegalAccessException e) {
//                    comTransResult.failed("撤销交易对象转换失败");
//                } catch (InvocationTargetException e) {
//                    comTransResult.failed("撤销交易对象转换失败");
//                }

                ComTransInfo comTransInfo = GsonUtils.modelA2B(transInfo, ComTransInfo.class);
                comTransResult.success(comTransInfo);
            }

            @Override
            public void failed(String error) {
                comTransResult.failed(error);
            }
        });
    }

    public static void bindService(Context context,final ComTransResult comTransResult) {
        EmvImpl.getInstance(context).bindService(context,new EmvImpl.TransResult() {
            @Override
            public void success(Object transInfo) {

                ComTransInfo comTransInfo = GsonUtils.modelA2B(transInfo, ComTransInfo.class);
                comTransResult.success(comTransInfo);
            }

            @Override
            public void failed(String error) {
                comTransResult.failed(error);
            }
        });
    }

    public static void unBindService(Context context) {
        EmvImpl.getInstance(context).unBindService(context);
    }
}
