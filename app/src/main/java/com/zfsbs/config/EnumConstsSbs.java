package com.zfsbs.config;

import com.zfsbs.R;


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


}
