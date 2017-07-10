package com.hd.enums;

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
 *                  Created by wucongpeng on 2017/3/6.        *
 **********************************************************/


public class EnumConsts {


    /**
     * 性别
     */
    public enum Gender{
        GENDER_UNKNOW("unknown", "未知"),
        GENDER_MALE("male", "男"),
        GENDER_FEMALE("female", "女");
        private String name;
        private String showName;
        Gender(String name, String showName){
            this.name = name;
            this.showName = showName;
        }

        public String getName() {
            return name;
        }

        public String getShowName() {
            return showName;
        }

        public static String getByName(String name){
            Gender[] genders = Gender.values();
            for (Gender gender : genders){
                if (gender.getShowName().equals(name)){
                    return gender.getName();
                }
            }
            return "";
        }
    }

    /**
     * 婚姻状态
     */
    public enum WedLock{
        WED_LOCK_SECRET("secret", "秘密"),
        WED_LOCK_MARRIED("married", "已婚"),
        WED_LOCK_SINGLE("single", "未婚");

        private String name;
        private String showName;

        WedLock(String name, String showName) {
            this.name = name;
            this.showName = showName;
        }

        public String getName() {
            return name;
        }

        public String getShowName() {
            return showName;
        }

        public static String getByName(String name){
            WedLock[] wedLocks = WedLock.values();
            for (WedLock wedLock : wedLocks){
                if (wedLock.getShowName().equals(name)){
                    return wedLock.getName();
                }
            }
            return "";
        }
    }



}
