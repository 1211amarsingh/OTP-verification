package com.kv.callrecorder;

public interface OTPReceiveListener {

    void onOTPReceived(String otp);

    void onOTPTimeOut();
}