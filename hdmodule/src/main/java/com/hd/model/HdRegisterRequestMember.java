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


public class HdRegisterRequestMember implements Serializable{
    private String id; //会员标识新增会员时为空,
    private String name; //会员名称
    private String gender; //['male' or 'female' or 'unknown']: 性别
    private HdQueryResponseBirthday birthday; //生日
    private String cellphone; //手机号
    private String email; //电子邮箱,
    private HdQueryResponseidCard idCard; //证件
    private String belongStore; //所属门号标识,
    private String wedLock; //婚姻状况,married' or 'single' or 'secret'
    private String grade; //会员等级代码,
    private String address;//家庭常住地址,
    private boolean mobileChecked; //手机是否校验,
    private String lastUpdateTime; //最后更新时间,
    private HdRegisterRequestRegisterAddress registerAddress; //注册地址


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

    public String getWedLock() {
        return wedLock;
    }

    public void setWedLock(String wedLock) {
        this.wedLock = wedLock;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public HdRegisterRequestRegisterAddress getRegisterAddress() {
        return registerAddress;
    }

    public void setRegisterAddress(HdRegisterRequestRegisterAddress registerAddress) {
        this.registerAddress = registerAddress;
    }

    @Override
    public String toString() {
        return "HdRegisterRequestMember{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", birthday=" + birthday +
                ", cellphone='" + cellphone + '\'' +
                ", email='" + email + '\'' +
                ", idCard=" + idCard +
                ", belongStore='" + belongStore + '\'' +
                ", wedLock='" + wedLock + '\'' +
                ", grade='" + grade + '\'' +
                ", address='" + address + '\'' +
                ", mobileChecked=" + mobileChecked +
                ", lastUpdateTime='" + lastUpdateTime + '\'' +
                ", registerAddress=" + registerAddress +
                '}';
    }
}
