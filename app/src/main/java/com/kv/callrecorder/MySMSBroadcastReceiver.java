package com.kv.callrecorder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

public class MySMSBroadcastReceiver extends BroadcastReceiver {

    private static OTPReceiveListener otpListener;

    public static void setOTPListener(OTPReceiveListener otpReceiveListener) {
        otpListener = otpReceiveListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
            Bundle extras = intent.getExtras();
            Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);
            switch (status.getStatusCode()) {
                case CommonStatusCodes.SUCCESS:
                    String full_message = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);

                    /*
                    <#> 123456 is your secret one time password (OTP)
                     to login to your Account. 7bhnjAR5gBE
                     */
                    Log.d("Sms_receiver", "message " + full_message);
                    //Extract the OTP code and send to the listener
                    String otp = full_message.substring(4, 10);
                    Log.d("Sms_receiver", "otp " + otp);

                    if (otpListener != null) {
                        otpListener.onOTPReceived(otp);
                    } else {
                        Log.d("Sms_receiver", "otpListener is null");
                    }
                    break;
                case CommonStatusCodes.TIMEOUT:
                    // Waiting for SMS timed out (5 minutes)
                    if (otpListener != null) {
                        otpListener.onOTPTimeOut();
                    }
                    break;
            }
        }
    }
}