package com.zfsbs.model;

import java.io.Serializable;
import java.util.List;

public class TransUploadResponse implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8922398259200576828L;
	private String point_url; // 积分领取二维码路径
	private int point; // 本次产生的积分额度
	private int pointCurrent; // 当前剩余积分
	private String coupon_url; //红包领取二维码路径
	private List<Couponsn> coupon; // 优惠券领取的路径
	private int backAmt; //返利金额
	private String responseMsg;

	public String getPoint_url() {
		return point_url;
	}

	public void setPoint_url(String point_url) {
		this.point_url = point_url;
	}

	public int getPoint() {
		return point;
	}

	public void setPoint(int point) {
		this.point = point;
	}

	public int getPointCurrent() {
		return pointCurrent;
	}

	public void setPointCurrent(int pointCurrent) {
		this.pointCurrent = pointCurrent;
	}

	public String getCoupon_url() {
		return coupon_url;
	}

	public void setCoupon_url(String coupon_url) {
		this.coupon_url = coupon_url;
	}

	public List<Couponsn> getCoupon() {
		return coupon;
	}

	public void setCoupon(List<Couponsn> coupon) {
		this.coupon = coupon;
	}

	public int getBackAmt() {
		return backAmt;
	}

	public void setBackAmt(int backAmt) {
		this.backAmt = backAmt;
	}

	public String getResponseMsg() {
		return responseMsg;
	}

	public void setResponseMsg(String responseMsg) {
		this.responseMsg = responseMsg;
	}
}
