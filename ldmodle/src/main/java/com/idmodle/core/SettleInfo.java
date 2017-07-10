package com.idmodle.core;

/**
 * Created by zf on 2017/3/20.
 */

public class SettleInfo {
    private int settleFlag = 0;
    private int batchNumber = 1;
    private String batchDate = "";
    private String batchTime = "";
    private String tid = "";
    private String mid = "";
    private int cupSaleCount = 0;
    private long cupSaleAmount = 0L;
    private int cupRefundCount = 0;
    private long cupRefundAmount = 0L;
    private int cupDebitCount = 0;
    private long cupDebitAmount = 0L;
    private int cupCreditCount = 0;
    private long cupCreditAmount = 0L;
    private int abrSaleCount = 0;
    private long abrSaleAmount = 0L;
    private int abrRefundCount = 0;
    private long abrRefundAmount = 0L;
    private int abrDebitCount = 0;
    private long abrDebitAmount = 0L;
    private int abrCreditCount = 0;
    private long abrCreditAmount = 0L;
    private String respCode = "";
    private String respDesc = "";

    public SettleInfo() {
    }

    public int getSettleFlag() {
        return this.settleFlag;
    }

    public void setSettleFlag(int var1) {
        this.settleFlag = var1;
    }

    public int getBatchNumber() {
        return this.batchNumber;
    }

    public void setBatchNumber(int var1) {
        this.batchNumber = var1;
    }

    public int getCupSaleCount() {
        return this.cupSaleCount;
    }

    public void setCupSaleCount(int var1) {
        this.cupSaleCount = var1;
    }

    public long getCupSaleAmount() {
        return this.cupSaleAmount;
    }

    public void setCupSaleAmount(long var1) {
        this.cupSaleAmount = var1;
    }

    public int getCupRefundCount() {
        return this.cupRefundCount;
    }

    public void setCupRefundCount(int var1) {
        this.cupRefundCount = var1;
    }

    public long getCupRefundAmount() {
        return this.cupRefundAmount;
    }

    public void setCupRefundAmount(long var1) {
        this.cupRefundAmount = var1;
    }

    public int getCupDebitCount() {
        return this.cupDebitCount;
    }

    public void setCupDebitCount(int var1) {
        this.cupDebitCount = var1;
    }

    public long getCupDebitAmount() {
        return this.cupDebitAmount;
    }

    public void setCupDebitAmount(long var1) {
        this.cupDebitAmount = var1;
    }

    public int getCupCreditCount() {
        return this.cupCreditCount;
    }

    public void setCupCreditCount(int var1) {
        this.cupCreditCount = var1;
    }

    public long getCupCreditAmount() {
        return this.cupCreditAmount;
    }

    public void setCupCreditAmount(long var1) {
        this.cupCreditAmount = var1;
    }

    public int getAbrSaleCount() {
        return this.abrSaleCount;
    }

    public void setAbrSaleCount(int var1) {
        this.abrSaleCount = var1;
    }

    public long getAbrSaleAmount() {
        return this.abrSaleAmount;
    }

    public void setAbrSaleAmount(long var1) {
        this.abrSaleAmount = var1;
    }

    public int getAbrRefundCount() {
        return this.abrRefundCount;
    }

    public void setAbrRefundCount(int var1) {
        this.abrRefundCount = var1;
    }

    public long getAbrRefundAmount() {
        return this.abrRefundAmount;
    }

    public void setAbrRefundAmount(long var1) {
        this.abrRefundAmount = var1;
    }

    public int getAbrDebitCount() {
        return this.abrDebitCount;
    }

    public void setAbrDebitCount(int var1) {
        this.abrDebitCount = var1;
    }

    public long getAbrDebitAmount() {
        return this.abrDebitAmount;
    }

    public void setAbrDebitAmount(long var1) {
        this.abrDebitAmount = var1;
    }

    public int getAbrCreditCount() {
        return this.abrCreditCount;
    }

    public void setAbrCreditCount(int var1) {
        this.abrCreditCount = var1;
    }

    public long getAbrCreditAmount() {
        return this.abrCreditAmount;
    }

    public void setAbrCreditAmount(long var1) {
        this.abrCreditAmount = var1;
    }

    public String getBatchDate() {
        return this.batchDate;
    }

    public void setBatchDate(String var1) {
        this.batchDate = var1;
    }

    public String getBatchTime() {
        return this.batchTime;
    }

    public void setBatchTime(String var1) {
        this.batchTime = var1;
    }

    public String getTid() {
        return this.tid;
    }

    public void setTid(String var1) {
        this.tid = var1;
    }

    public String getMid() {
        return this.mid;
    }

    public void setMid(String var1) {
        this.mid = var1;
    }

    public String getRespCode() {
        return this.respCode;
    }

    public void setRespCode(String var1) {
        this.respCode = var1;
    }

    public String getRespDesc() {
        return this.respDesc;
    }

    public void setRespDesc(String var1) {
        this.respDesc = var1;
    }
}
