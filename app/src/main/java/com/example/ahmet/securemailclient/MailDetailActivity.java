package com.example.ahmet.securemailclient;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ahmet.securemailclient.biometric.BiometricCallback;
import com.example.ahmet.securemailclient.biometric.BiometricManager;
import com.example.ahmet.securemailclient.biometric.BiometricUtils;
import com.example.ahmet.securemailclient.dummy.DummyContent;

public class MailDetailActivity extends AppCompatActivity {

    private final int LOCK_SCREEN_CODE=482;
    private int id=-1;
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        TextView subject=findViewById(R.id.subjectMailDetail);
        TextView fromWho=findViewById(R.id.fromWhoMailDetail);
        TextView content=findViewById(R.id.contentMailDetail);
        fab.setVisibility(FloatingActionButton.INVISIBLE);
        id = getIntent().getIntExtra("mailIndex",-1);
        DummyContent.DummyItem item;
        if (id!=-1) {
            item=DummyContent.ITEMS.get(id);
            if (item.subject.startsWith(Constants.ENCRYPTED_MESSAGE_HEADER_NAME)) {
                fab.setVisibility(FloatingActionButton.VISIBLE);
            }
            subject.setText(item.subject.replace(Constants.ENCRYPTED_MESSAGE_HEADER_NAME,""));
            fromWho.setText(item.fromWho);
            content.setText(item.content);

        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                  //      .setAction("Action", null).show();
                try {
                    KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
                    Intent intent=new Intent(MailDetailActivity.this,PatternActivity.class);
                    startActivity(intent);
                    //Intent screenLockIntent = keyguardManager.createConfirmDeviceCredentialIntent("SecureEmailClient", "You should enter your pattern to reach decrypted mail.");
                    //startActivityForResult(screenLockIntent, LOCK_SCREEN_CODE);
                } catch (Exception e) {
                    Toast.makeText(MailDetailActivity.this,"You should use your lock to open mail.",Toast.LENGTH_LONG);
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(LOCK_SCREEN_CODE == requestCode){
            if (resultCode == RESULT_OK) {

                new BiometricManager.BiometricBuilder(this)
                        .setTitle("SecureMailClient")
                        .setSubtitle("Biometric auth to read mail")
                        .setDescription("You should touch the sensor to read mail.")
                        .setNegativeButtonText("Cancel")
                        .build()
                        .authenticate(new BiometricCallback() {
                            @Override
                            public void onSdkVersionNotSupported() {
                                System.out.println("SdkNotSupported");
                            }

                            @Override
                            public void onBiometricAuthenticationNotSupported() {
                                System.out.println("Not Supported");
                            }

                            @Override
                            public void onBiometricAuthenticationNotAvailable() {
                                System.out.println("Auth Not Available");
                            }

                            @Override
                            public void onBiometricAuthenticationPermissionNotGranted() {
                                System.out.println("Permission Not Granted");
                            }

                            @Override
                            public void onBiometricAuthenticationInternalError(String error) {
                                System.out.println(error);
                            }

                            @Override
                            public void onAuthenticationFailed() {
                                System.out.println("Auth fail");
                            }

                            @Override
                            public void onAuthenticationCancelled() {
                                System.out.println("Auth cancel");
                            }

                            @Override
                            public void onAuthenticationSuccessful() {
                                Intent i = new Intent(MailDetailActivity.this, DecryptedMessage.class);
                                i.putExtra("mailData",id);
                                MailDetailActivity.this.startActivity(i);
                            }

                            @Override
                            public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                                System.out.println(helpString);
                            }

                            @Override
                            public void onAuthenticationError(int errorCode, CharSequence errString) {
                                System.out.println(errString);
                            }
                        });
                /*Intent intent=new Intent(MailDetailActivity.this,FingerprintActivity.class);
                intent.putExtra("mailData",id);
                startActivity(intent);*/
            } else {
                Toast.makeText(this,"Wrong..",Toast.LENGTH_LONG);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.P)
    private void displayBiometricPrompt(final BiometricCallback biometricCallback) {
        new BiometricPrompt.Builder(this)
                .setTitle("SecureEmailClient")
                .setSubtitle("SecureEmailClient biometric auth")
                .setDescription("You should use your finger which saved on your device.")
                .setNegativeButton("Cancel", this.getMainExecutor(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        biometricCallback.onAuthenticationCancelled();
                    }
                })
                .build();
    }
}
