package com.kv.callrecorder;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.CredentialPickerConfig;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {

    private static final int RESOLVE_HINT = 100;
    private String TAG = "SMS_Retriever";
    AppCompatActivity activity;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity = this;
        textView = findViewById(R.id.tv_otp);

        getHashKey();
        requestHint();
        startSMSListener();
    }

    /**
     * for show Select Mobile number dialog
     */
    private void requestHint() {
        GoogleApiClient apiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.CREDENTIALS_API)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.e(TAG, "Client connection failed: " + connectionResult.getErrorMessage());
                    }
                }).build();

        HintRequest hintRequest = new HintRequest.Builder()
                .setHintPickerConfig(new CredentialPickerConfig.Builder().setShowCancelButton(true).build())
                .setPhoneNumberIdentifierSupported(true)
                .build();

        PendingIntent intent = Auth.CredentialsApi.getHintPickerIntent(apiClient, hintRequest);
        try {
            startIntentSenderForResult(intent.getIntentSender(), RESOLVE_HINT, null, 0, 0, 0);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESOLVE_HINT) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Credential cred = data.getParcelableExtra(Credential.EXTRA_KEY);
                    if (cred != null) {
                        String unformattedPhone = cred.getId();
                        textView.setText(unformattedPhone);
                        Log.d(TAG, unformattedPhone);
                    }
                }
            }
        }
    }

    private void startSMSListener() {
        SMSReceiver smsReceiver = new SMSReceiver();
        smsReceiver.setOTPListener(new OTPReceiveListener() {
            @Override
            public void onOTPReceived(String otp) {
                textView.setText(otp);
            }

            @Override
            public void onOTPTimeOut() {
                textView.setText("onOTPTimeOut");
            }

            @Override
            public void onOTPReceivedError(String error) {
                textView.setText(error);
            }
        });

        SmsRetrieverClient client = SmsRetriever.getClient(this);

        Task<Void> task = client.startSmsRetriever();
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // API successfully started
                //SMSBroadcastReceiver started listenting for sms
                Log.d(TAG, "API successfully started");
                textView.setText("Connected");
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Fail to start API
                e.printStackTrace();
                textView.setText("Failed to Connected");
            }
        });
    }

    private void getHashKey() {
        AppSignatureHashHelper appSignatureHashHelper = new AppSignatureHashHelper(this);

        // This code requires one time to get Hash keys do comment and share key
//        Log.d(TAG, "Apps Hash Key: " + appSignatureHashHelper.getAppSignatures().get(0));
    }
}
