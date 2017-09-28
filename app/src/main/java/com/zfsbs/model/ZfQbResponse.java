package com.zfsbs.model;

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

import java.util.List;

/**********************************************************
 * *
 * Created by wucongpeng on 2016/11/15.        *
 **********************************************************/


public class ZfQbResponse {
//    private String transStates; //交易结果码
//    private String SystemOrderNo; //交易订单号
//    private String MsgTxt;  //交易结果
//    private String groupId; //大商户号


    private String point_url; // 积分领取二维码路径
    private int point; // 本次产生的积分额度
    private int pointCurrent; // 当前剩余积分
    private String coupon_url; //红包领取二维码路径
    private List<Couponsn> coupon; // 优惠券领取的路径
    private int backAmt; //返利金额
    private String responseMsg;

    public String getPoint_url() {
        return point_url;
    }

    public void setPoint_url(String point_url) {
        this.point_url = point_url;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public int getPointCurrent() {
        return pointCurrent;
    }

    public void setPointCurrent(int pointCurrent) {
        this.pointCurrent = pointCurrent;
    }

    public String getCoupon_url() {
        return coupon_url;
    }

    public void setCoupon_url(String coupon_url) {
        this.coupon_url = coupon_url;
    }

    public List<Couponsn> getCoupon() {
        return coupon;
    }

    public void setCoupon(List<Couponsn> coupon) {
        this.coupon = coupon;
    }

    public int getBackAmt() {
        return backAmt;
    }

    public void setBackAmt(int backAmt) {
        this.backAmt = backAmt;
    }

    public String getResponseMsg() {
        return responseMsg;
    }

    public void setResponseMsg(String responseMsg) {
        this.responseMsg = responseMsg;
    }

    //    public String getTransStates() {
//        return transStates;
//    }
//
//    public void setTransStates(String transStates) {
//        this.transStates = transStates;
//    }
//
//    public String getSystemOrderNo() {
//        return SystemOrderNo;
//    }
//
//    public void setSystemOrderNo(String systemOrderNo) {
//        SystemOrderNo = systemOrderNo;
//    }
//
//    public String getMsgTxt() {
//        return MsgTxt;
//    }
//
//    public void setMsgTxt(String msgTxt) {
//        MsgTxt = msgTxt;
//    }
//
//    public String getGroupId() {
//        return groupId;
//    }
//
//    public void setGroupId(String groupId) {
//        this.groupId = groupId;
//    }
}
