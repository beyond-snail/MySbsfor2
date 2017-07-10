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


public class HdAdjustScoreRequestRequest {

    private String tranId; //交易标识，要求系统内唯一，用于唯一确认一笔交易,
    private String xid; //外部交易标识该标识为调用方系统交易的唯一标识, 标识的规则可在对接时确定,如 系统标识-门店-流水号. 该标识用于系统间对账或冲账用,
    private String tranTime; //交易时间
    private HdQueryResponseidCard account; //账户
    private HdAdjustScoreRequestRequestScoreRec scoreRec;//扣减积分明细,
    private String scoreSource; //积分来源，业务系统自定义，不能为空
    private String remark; //备注,
    private String action; //

    public String getTranId() {
        return tranId;
    }

    public void setTranId(String tranId) {
        this.tranId = tranId;
    }

    public String getXid() {
        return xid;
    }

    public void setXid(String xid) {
        this.xid = xid;
    }

    public String getTranTime() {
        return tranTime;
    }

    public void setTranTime(String tranTime) {
        this.tranTime = tranTime;
    }

    public HdQueryResponseidCard getAccount() {
        return account;
    }

    public void setAccount(HdQueryResponseidCard account) {
        this.account = account;
    }

    public HdAdjustScoreRequestRequestScoreRec getScoreRec() {
        return scoreRec;
    }

    public void setScoreRec(HdAdjustScoreRequestRequestScoreRec scoreRec) {
        this.scoreRec = scoreRec;
    }

    public String getScoreSource() {
        return scoreSource;
    }

    public void setScoreSource(String scoreSource) {
        this.scoreSource = scoreSource;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return "HdAdjustScoreRequestRequest{" +
                "tranId='" + tranId + '\'' +
                ", xid='" + xid + '\'' +
                ", tranTime='" + tranTime + '\'' +
                ", account=" + account +
                ", scoreRec=" + scoreRec +
                ", scoreSource='" + scoreSource + '\'' +
                ", remark='" + remark + '\'' +
                ", action='" + action + '\'' +
                '}';
    }
}
