package com.idmodle.core;

/**
 * Created by zf on 2017/3/10.
 */



public class ParamInfo {
    private String mid = "";
    private String tid = "";
    private String merchantName = "";
    private String serverIP = "";
    private int serverPort = -1;
    private String tpdu = "";
    private int commTimeout = -1;
    private int keyIndex = -1;
    private int cardInVoidSale = -1;
    private int PinInVoidSale = -1;
    private boolean signOn = false;
    private String termCap = "";
    private String mid2 = "";
    private String tid2 = "";
    private int keyIndex2 = -1;
    private boolean signOn2 = false;

    public ParamInfo() {
    }

    public String getMid() {
        return this.mid;
    }

    public void setMid(String var1) {
        this.mid = var1;
    }

    public String getTid() {
        return this.tid;
    }

    public void setTid(String var1) {
        this.tid = var1;
    }

    public String getMerchantName() {
        return this.merchantName;
    }

    public void setMerchantName(String var1) {
        this.merchantName = var1;
    }

    public String getServerIP() {
        return this.serverIP;
    }

    public void setServerIP(String var1) {
        this.serverIP = var1;
    }

    public int getServerPort() {
        return this.serverPort;
    }

    public void setServerPort(int var1) {
        this.serverPort = var1;
    }

    public String getTpdu() {
        return this.tpdu;
    }

    public void setTpdu(String var1) {
        this.tpdu = var1;
    }

    public int getCommTimeout() {
        return this.commTimeout;
    }

    public void setCommTimeout(int var1) {
        this.commTimeout = var1;
    }

    public int getKeyIndex() {
        return this.keyIndex;
    }

    public void setKeyIndex(int var1) {
        this.keyIndex = var1;
    }

    public int getCardInVoidSale() {
        return this.cardInVoidSale;
    }

    public void setCardInVoidSale(int var1) {
        this.cardInVoidSale = var1;
    }

    public int getPinInVoidSale() {
        return this.PinInVoidSale;
    }

    public void setPinInVoidSale(int var1) {
        this.PinInVoidSale = var1;
    }

    public boolean isSignOn() {
        return this.signOn;
    }

    public void setSignOn(boolean var1) {
        this.signOn = var1;
    }

    public String getTermCap() {
        return this.termCap;
    }

    public void setTermCap(String var1) {
        this.termCap = var1;
    }

    public String getMid2() {
        return this.mid2;
    }

    public void setMid2(String var1) {
        this.mid2 = var1;
    }

    public String getTid2() {
        return this.tid2;
    }

    public void setTid2(String var1) {
        this.tid2 = var1;
    }

    public int getKeyIndex2() {
        return this.keyIndex2;
    }

    public void setKeyIndex2(int var1) {
        this.keyIndex2 = var1;
    }

    public boolean isSignOn2() {
        return this.signOn2;
    }

    public void setSignOn2(boolean var1) {
        this.signOn2 = var1;
    }
}

