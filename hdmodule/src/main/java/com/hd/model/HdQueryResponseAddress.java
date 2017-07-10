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


class HdQueryResponseAddress implements Serializable {
    private String uuid; //全局唯一标识
    private boolean defaultAddr; //地址
    private String contact; //联系人
    private String cellphone; //手机号
    private String memberId; //会员标识
    private int order; //序号
    private String province; //省市
    private String city; //市区
    private String district; //区县
    private String address; //详细地址
    private String postcode; //邮编
    private String mbrSource; //会员来源
    private String carplateNum; //车牌号1
    private String carplateNum2; //车牌号2

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public boolean isDefaultAddr() {
        return defaultAddr;
    }

    public void setDefaultAddr(boolean defaultAddr) {
        this.defaultAddr = defaultAddr;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getMbrSource() {
        return mbrSource;
    }

    public void setMbrSource(String mbrSource) {
        this.mbrSource = mbrSource;
    }

    public String getCarplateNum() {
        return carplateNum;
    }

    public void setCarplateNum(String carplateNum) {
        this.carplateNum = carplateNum;
    }

    public String getCarplateNum2() {
        return carplateNum2;
    }

    public void setCarplateNum2(String carplateNum2) {
        this.carplateNum2 = carplateNum2;
    }

    @Override
    public String toString() {
        return "HdQueryResponseAddress{" +
                "uuid='" + uuid + '\'' +
                ", defaultAddr=" + defaultAddr +
                ", contact='" + contact + '\'' +
                ", cellphone='" + cellphone + '\'' +
                ", memberId='" + memberId + '\'' +
                ", order=" + order +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", district='" + district + '\'' +
                ", address='" + address + '\'' +
                ", postcode='" + postcode + '\'' +
                ", mbrSource='" + mbrSource + '\'' +
                ", carplateNum='" + carplateNum + '\'' +
                ", carplateNum2='" + carplateNum2 + '\'' +
                '}';
    }
}
