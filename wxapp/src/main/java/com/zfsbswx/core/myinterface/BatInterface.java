package com.zfsbswx.core.myinterface;


public interface BatInterface {
//	public void success_bat(UpayResult result);
	public void failed_bat(String error_code, String error_msg);
	public void onLogin();
}
