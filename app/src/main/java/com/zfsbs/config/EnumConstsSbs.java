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



	// 支付方式
	public enum PaymentType {
		AliPay("支付宝", 1), WebChat("微信", 3), Baidu("百付宝", 4), Jd("京东", 5), BankCard("银行卡", 6), Cash("现金",
				7), NetBankCard("银行卡网银",
						8), NetBankCardQuick("银行卡网银快捷", 9), UnionBank("银联钱包", 10), Wallet("钱包", 13), Other("其他", 99);
		private String name;
		private int type;
		static Map<Integer, PaymentType> allPaymentTypes;

		private PaymentType(String name, int type) {
			this.name = name;
			this.type = type;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

		public static PaymentType fromType(int type) {
			PaymentType actType = allPaymentTypes.get(type);
			return actType;
		}

		public static boolean supported(int type) {
			return allPaymentTypes.containsKey(type);
		}

		static {
			allPaymentTypes = new HashMap<Integer, PaymentType>();
			PaymentType[] types = values();
			for (PaymentType type : types) {
				allPaymentTypes.put(type.getType(), type);
			}
		}
	}


}
