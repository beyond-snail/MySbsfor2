package com.zfsbs.model;

import java.io.Serializable;

public class Coupons implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id; // 优惠券ID
	private String validStartTime;
	private String validEndTime;
	private String getTime;
	private int verifyReaddCount;
	private int money; // 可抵消金额
	private String sn; // 优惠劵劵号
	private String name; // 优惠券名称
	private String remark; // 说明信息
	private int validTimeType;
	private int days;
	private int couponType;

	private int canMultiChoose; // 是否可多选
	private boolean isChecked; // 是否使用


	public int getCouponType() {
		return couponType;
	}

	public void setCouponType(int couponType) {
		this.couponType = couponType;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int isCanMultiChoose() {
		return canMultiChoose;
	}

	public void setCanMultiChoose(int canMultiChoose) {
		this.canMultiChoose = canMultiChoose;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	public String getValidStartTime() {
		return validStartTime;
	}

	public void setValidStartTime(String validStartTime) {
		this.validStartTime = validStartTime;
	}

	public String getValidEndTime() {
		return validEndTime;
	}

	public void setValidEndTime(String validEndTime) {
		this.validEndTime = validEndTime;
	}

	public void setGetTime(String getTime) {
		this.getTime = getTime;
	}

	public int getDays() {
		return days;
	}

	public void setDays(int days) {
		this.days = days;
	}

	public int getCanMultiChoose() {
		return canMultiChoose;
	}



	public int getVerifyReaddCount() {
		return verifyReaddCount;
	}

	public void setVerifyReaddCount(int verifyReaddCount) {
		this.verifyReaddCount = verifyReaddCount;
	}

	public int getValidTimeType() {
		return validTimeType;
	}

	public void setValidTimeType(int validTimeType) {
		this.validTimeType = validTimeType;
	}

	@Override
	public String toString() {
		return "Coupons{" +
				"id=" + id +
				", validStartTime=" + validStartTime +
				", validEndTime=" + validEndTime +
				", getTime=" + getTime +
				", verifyReaddCount=" + verifyReaddCount +
				", money=" + money +
				", sn='" + sn + '\'' +
				", name='" + name + '\'' +
				", remark='" + remark + '\'' +
				", validTimeType=" + validTimeType +
				", canMultiChoose=" + canMultiChoose +
				", isChecked=" + isChecked +
				'}';
	}
}
