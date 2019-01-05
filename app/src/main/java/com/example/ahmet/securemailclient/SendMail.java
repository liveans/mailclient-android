package com.example.ahmet.securemailclient;

import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import org.spongycastle.openpgp.PGPException;

import java.io.IOException;

public class SendMail extends AppCompatActivity {
    EditText subject;
    EditText toWhom;
    EditText message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_mail);
        subject=findViewById(R.id.subjectEditText);
        toWhom=findViewById(R.id.thoWhomEditText);
        message=findViewById(R.id.messageEditText);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem menuItem=menu.add(0,Constants.SEND_MAIL_MENU_ITEM_ID,0,"Ekle");
        menuItem.setIcon(android.R.drawable.ic_menu_send);
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case Constants.SEND_MAIL_MENU_ITEM_ID:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String encryptedText;
                        try {
                            encryptedText=PgpUtils.encrypt(message.getText().toString(),Constants.pgpPublicKey);
                            MailClient.getInstance().send(subject.getText().toString(),encryptedText,toWhom.getText().toString());
                            System.out.println("sended to:"+toWhom.getText().toString());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    finish();
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (PGPException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
