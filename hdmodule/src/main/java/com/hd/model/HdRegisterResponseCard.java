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

import java.io.Serializable;

/**********************************************************
 *                                                        *
 *                  Created by wucongpeng on 2017/3/6.        *
 **********************************************************/


public class HdRegisterResponseCard implements Serializable{
    private String cardNum; //卡号,
    private String bytime; //有效期,
    private String cardType; //卡类型代码,
    private String carrier; //持卡人标识,
    private String state; // ['使用中' or '已挂失' or '已作废' or '已补发卡' or '已退卡' or '已冻结' or '未激活' or '已坏卡登记' or '已销毁' or '已制卡' or '已发卡' or '已核对' or '已回收' or '空卡' or '异常' or '已损卡']: 卡状态


    public String getCardNum() {
        return cardNum;
    }

    public void setCardNum(String cardNum) {
        this.cardNum = cardNum;
    }

    public String getBytime() {
        return bytime;
    }

    public void setBytime(String bytime) {
        this.bytime = bytime;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "HdRegisterResponseCard{" +
                "cardNum='" + cardNum + '\'' +
                ", bytime='" + bytime + '\'' +
                ", cardType='" + cardType + '\'' +
                ", carrier='" + carrier + '\'' +
                ", state='" + state + '\'' +
                '}';
    }
}
