package com.hd.model;

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

/**********************************************************
 *                                                        *
 *                  Created by wucongpeng on 2017/3/7.        *
 **********************************************************/


public class HdAdjustScoreRequestOperCtx {

    private String time; //操作发生时间，通常来说是用户（人）通过界面发起操作请求的时刻。默认取值为对象创建时间。 时间格式: yyyy-MM-dd'T'HH:mm:ss.SSSZ,

    private HdAdjustScoreRequestOperCtxOperator operator; //
    private String terminalId; //终端标识,
    private String store; //门店标识需要项目工程人员告知调用方, 后台是hdcrm系统时, 这里是门店GID; 后台是hdcard系统时, 这里是门店code

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public HdAdjustScoreRequestOperCtxOperator getOperator() {
        return operator;
    }

    public void setOperator(HdAdjustScoreRequestOperCtxOperator operator) {
        this.operator = operator;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    @Override
    public String toString() {
        return "HdAdjustScoreRequestOperCtx{" +
                "time='" + time + '\'' +
                ", operator=" + operator +
                ", terminalId='" + terminalId + '\'' +
                ", store='" + store + '\'' +
                '}';
    }
}
