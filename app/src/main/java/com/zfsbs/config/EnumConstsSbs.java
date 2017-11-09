package com.zfsbs.config;

import com.zfsbs.R;

import java.util.HashMap;
import java.util.Map;


public class EnumConstsSbs {

	public enum MenuType {

		MENU_1(1, R.color.menu1,"收银"),
		MENU_2(2, R.color.menu2,"交易记录"),
		MENU_3(3, R.color.menu3,"会员充值"),
		MENU_4(4, R.color.menu4,"开卡/绑卡"),
		MENU_5(5, R.color.menu5,"券码核销"),
		MENU_6(6, R.color.menu6,"系统设置"),;

		private int code;
		private int bg;
		private String name;

		MenuType(int code, int bg, String name) {
			this.code = code;
			this.bg = bg;
			this.name = name;
		}

		public int getCode() {
			return code;
		}

		public int getBg() {
			return bg;
		}

		public String getName() {
			return name;
		}

		public static MenuType getByCode(int code) {
			MenuType[] timeZoneTypes = MenuType.values();
			for (MenuType timeZoneType : timeZoneTypes) {
				if (timeZoneType.getCode() == code) {
					return timeZoneType;
				}
			}
			return null;
		}

		public static int getCodeByName(String name) {
			MenuType[] timeZoneTypes = MenuType.values();
			for (MenuType timeZoneType : timeZoneTypes) {
				if (timeZoneType.getName() == name) {
					return timeZoneType.getCode();
				}
			}
			return -1;
		}

	}

	public enum SystemMenuType {

		SYS_MENU_1(1, R.color.menu1,"签到"),
		SYS_MENU_2(2, R.color.menu2,"收款信息"),
		SYS_MENU_7(3, R.color.menu3,"预授权"),
		SYS_MENU_4(4, R.color.menu4,"扫码末笔查询"),
		SYS_MENU_5(5, R.color.menu5,"主管理员密码修改"),
		SYS_MENU_6(6, R.color.menu6,"消费管理"),
		SYS_MENU_3(7, R.color.menu1,"班结"),
		;

		private int code;
		private int bg;
		private String name;

		SystemMenuType(int code, int bg, String name) {
			this.code = code;
			this.bg = bg;
			this.name = name;
		}

		public int getCode() {
			return code;
		}

		public int getBg() {
			return bg;
		}

		public String getName() {
			return name;
		}

		public static SystemMenuType getByCode(int code) {
			SystemMenuType[] timeZoneTypes = SystemMenuType.values();
			for (SystemMenuType timeZoneType : timeZoneTypes) {
				if (timeZoneType.getCode() == code) {
					return timeZoneType;
				}
			}
			return null;
		}

		public static int getCodeByName(String name) {
			SystemMenuType[] timeZoneTypes = SystemMenuType.values();
			for (SystemMenuType timeZoneType : timeZoneTypes) {
				if (timeZoneType.getName() == name) {
					return timeZoneType.getCode();
				}
			}
			return -1;
		}

	}



	// 支付方式
	public enum PaymentType {
		BankCard(1, R.mipmap.icon_payflot, "刷卡"),
		AliPay(2, R.mipmap.icon_aly, "支付宝"),
		WebChat(3, R.mipmap.icon_weixin, "微信"),
		UnionBank(4, R.mipmap.pay_union_bg, "银联"),
		Cash(5, R.mipmap.icon_paycash,"现金"),
		Wallet(6, R.mipmap.icon_qb, "钱包"),
		STK(7, R.mipmap.stk_card, "会员卡");


		private int code;
		private int bg;
		private String name;

		PaymentType(int code, int bg, String name) {
			this.code = code;
			this.bg = bg;
			this.name = name;
		}

		public int getCode() {
			return code;
		}

		public int getBg() {
			return bg;
		}

		public String getName() {
			return name;
		}

		public static PaymentType getByCode(int code) {
			PaymentType[] timeZoneTypes = PaymentType.values();
			for (PaymentType timeZoneType : timeZoneTypes) {
				if (timeZoneType.getCode() == code) {
					return timeZoneType;
				}
			}
			return null;
		}

		public static int getCodeByName(String name) {
			PaymentType[] timeZoneTypes = PaymentType.values();
			for (PaymentType timeZoneType : timeZoneTypes) {
				if (timeZoneType.getName() == name) {
					return timeZoneType.getCode();
				}
			}
			return -1;
		}
	}



	public enum CouponUseStatus{
		Verified("已核销", 1),
		//UnVerify("未核销", 2),
		Received("已领取", 3),
		locked("已锁定", 4),
		Expire("已过期", 5),
		Sent("已转赠", 99),
		Refunded("已退回", 101),
		;
		private String name;
		private int type;
		static Map<Integer, CouponUseStatus> allCouponUseStatuss;
		private CouponUseStatus(String name, int type){
			this.name

					= name;
			this.type = type;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name

					= name;
		}
		public int getType() {
			return type;
		}
		public void setType(int type) {
			this.type = type;
		}

		public static CouponUseStatus fromType(int type){
			CouponUseStatus actType = allCouponUseStatuss.get(type);
			return actType;
		}
		public static boolean supported(int type){
			return allCouponUseStatuss.containsKey(type);
		}
		static {
			allCouponUseStatuss = new HashMap<Integer, CouponUseStatus>();
			CouponUseStatus[] types = values();
			for (CouponUseStatus type : types){
				allCouponUseStatuss.put(type.getType(), type);
			}
		}
	}

}
