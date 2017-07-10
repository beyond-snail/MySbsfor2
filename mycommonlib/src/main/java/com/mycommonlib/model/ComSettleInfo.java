package com.mycommonlib.model;

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
 *                  Created by wucongpeng on 2017/3/20.        *
 **********************************************************/


public class ComSettleInfo {

    private int settleFlag = 0;
    private int batchNumber = 1;
    private String batchDate = "";
    private String batchTime = "";
    private String tid = "";
    private String mid = "";
    private int cupSaleCount = 0;
    private long cupSaleAmount = 0L;
    private int cupRefundCount = 0;
    private long cupRefundAmount = 0L;
    private int cupDebitCount = 0;
    private long cupDebitAmount = 0L;
    private int cupCreditCount = 0;
    private long cupCreditAmount = 0L;
    private int abrSaleCount = 0;
    private long abrSaleAmount = 0L;
    private int abrRefundCount = 0;
    private long abrRefundAmount = 0L;
    private int abrDebitCount = 0;
    private long abrDebitAmount = 0L;
    private int abrCreditCount = 0;
    private long abrCreditAmount = 0L;

    public int getSettleFlag() {
        return settleFlag;
    }

    public void setSettleFlag(int settleFlag) {
        this.settleFlag = settleFlag;
    }

    public int getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(int batchNumber) {
        this.batchNumber = batchNumber;
    }

    public String getBatchDate() {
        return batchDate;
    }

    public void setBatchDate(String batchDate) {
        this.batchDate = batchDate;
    }

    public String getBatchTime() {
        return batchTime;
    }

    public void setBatchTime(String batchTime) {
        this.batchTime = batchTime;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public int getCupSaleCount() {
        return cupSaleCount;
    }

    public void setCupSaleCount(int cupSaleCount) {
        this.cupSaleCount = cupSaleCount;
    }

    public long getCupSaleAmount() {
        return cupSaleAmount;
    }

    public void setCupSaleAmount(long cupSaleAmount) {
        this.cupSaleAmount = cupSaleAmount;
    }

    public int getCupRefundCount() {
        return cupRefundCount;
    }

    public void setCupRefundCount(int cupRefundCount) {
        this.cupRefundCount = cupRefundCount;
    }

    public long getCupRefundAmount() {
        return cupRefundAmount;
    }

    public void setCupRefundAmount(long cupRefundAmount) {
        this.cupRefundAmount = cupRefundAmount;
    }

    public int getCupDebitCount() {
        return cupDebitCount;
    }

    public void setCupDebitCount(int cupDebitCount) {
        this.cupDebitCount = cupDebitCount;
    }

    public long getCupDebitAmount() {
        return cupDebitAmount;
    }

    public void setCupDebitAmount(long cupDebitAmount) {
        this.cupDebitAmount = cupDebitAmount;
    }

    public int getCupCreditCount() {
        return cupCreditCount;
    }

    public void setCupCreditCount(int cupCreditCount) {
        this.cupCreditCount = cupCreditCount;
    }

    public long getCupCreditAmount() {
        return cupCreditAmount;
    }

    public void setCupCreditAmount(long cupCreditAmount) {
        this.cupCreditAmount = cupCreditAmount;
    }

    public int getAbrSaleCount() {
        return abrSaleCount;
    }

    public void setAbrSaleCount(int abrSaleCount) {
        this.abrSaleCount = abrSaleCount;
    }

    public long getAbrSaleAmount() {
        return abrSaleAmount;
    }

    public void setAbrSaleAmount(long abrSaleAmount) {
        this.abrSaleAmount = abrSaleAmount;
    }

    public int getAbrRefundCount() {
        return abrRefundCount;
    }

    public void setAbrRefundCount(int abrRefundCount) {
        this.abrRefundCount = abrRefundCount;
    }

    public long getAbrRefundAmount() {
        return abrRefundAmount;
    }

    public void setAbrRefundAmount(long abrRefundAmount) {
        this.abrRefundAmount = abrRefundAmount;
    }

    public int getAbrDebitCount() {
        return abrDebitCount;
    }

    public void setAbrDebitCount(int abrDebitCount) {
        this.abrDebitCount = abrDebitCount;
    }

    public long getAbrDebitAmount() {
        return abrDebitAmount;
    }

    public void setAbrDebitAmount(long abrDebitAmount) {
        this.abrDebitAmount = abrDebitAmount;
    }

    public int getAbrCreditCount() {
        return abrCreditCount;
    }

    public void setAbrCreditCount(int abrCreditCount) {
        this.abrCreditCount = abrCreditCount;
    }

    public long getAbrCreditAmount() {
        return abrCreditAmount;
    }

    public void setAbrCreditAmount(long abrCreditAmount) {
        this.abrCreditAmount = abrCreditAmount;
    }
}
