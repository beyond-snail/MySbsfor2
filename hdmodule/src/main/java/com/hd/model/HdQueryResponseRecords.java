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


public class HdQueryResponseRecords implements Serializable {
    private String id; //会员标识
    private String name; //会员名称
    private String gender; //会员性别
    private HdQueryResponseBirthday birthday; //会员生日（年月日）
    private String cellphone; //手机号
    private String email; //电子邮箱
    private HdQueryResponseidCard idCard; //证件信息
    private String belongStore; //会员所属的门店
    private String grade; //会员等级的代码
    private String wedLock;//会员婚姻状况
    private boolean mobileChecked;//手机是否校验
    private String lastUpdateTime; //最后更新时间
    private HdQueryResponseRegisterAddress registerAddress; //注册地址
    private HdQueryResponseAddress Address; //地址信息


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public HdQueryResponseBirthday getBirthday() {
        return birthday;
    }

    public void setBirthday(HdQueryResponseBirthday birthday) {
        this.birthday = birthday;
    }

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public HdQueryResponseidCard getIdCard() {
        return idCard;
    }

    public void setIdCard(HdQueryResponseidCard idCard) {
        this.idCard = idCard;
    }

    public String getBelongStore() {
        return belongStore;
    }

    public void setBelongStore(String belongStore) {
        this.belongStore = belongStore;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getWedLock() {
        return wedLock;
    }

    public void setWedLock(String wedLock) {
        this.wedLock = wedLock;
    }

    public boolean isMobileChecked() {
        return mobileChecked;
    }

    public void setMobileChecked(boolean mobileChecked) {
        this.mobileChecked = mobileChecked;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public HdQueryResponseRegisterAddress getRegisterAddress() {
        return registerAddress;
    }

    public void setRegisterAddress(HdQueryResponseRegisterAddress registerAddress) {
        this.registerAddress = registerAddress;
    }

    public HdQueryResponseAddress getAddress() {
        return Address;
    }

    public void setAddress(HdQueryResponseAddress address) {
        Address = address;
    }

    @Override
    public String toString() {
        return "HdQueryResponseRecords{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", birthday=" + birthday +
                ", cellphone='" + cellphone + '\'' +
                ", email='" + email + '\'' +
                ", idCard=" + idCard +
                ", belongStore='" + belongStore + '\'' +
                ", grade='" + grade + '\'' +
                ", wedLock='" + wedLock + '\'' +
                ", mobileChecked=" + mobileChecked +
                ", lastUpdateTime='" + lastUpdateTime + '\'' +
                ", registerAddress=" + registerAddress +
                ", Address=" + Address +
                '}';
    }
}
