package com.example.ahmet.securemailclient;

import android.app.ProgressDialog;
import android.content.res.Resources;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.example.ahmet.securemailclient.database.DatabaseManager;
import com.example.ahmet.securemailclient.model.Key;

import org.spongycastle.openpgp.PGPException;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;

public class SendMail extends AppCompatActivity {
    EditText subject;
    EditText toWhom;
    EditText message;
    CheckBox encrypt;
    HashMap<String,String> hashMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_mail);
        getSupportActionBar().setSubtitle("Sender :("+Constants.email+")");
        hashMap=new HashMap<>();
        subject=findViewById(R.id.subjectEditText);
        toWhom=findViewById(R.id.thoWhomEditText);
        message=findViewById(R.id.messageEditText);
        encrypt=findViewById(R.id.encrypt_checkbox);
        encrypt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String email=toWhom.getText().toString();
                String publicKey="";
                if (isChecked) {
                    encrypt.setText("Encryption Enabled");
                    if (!hashMap.containsKey(email)) {
                        if ((publicKey=searchPublicKeyInKeyTable(toWhom.getText().toString()))!=null) {
                            //Saving state.
                            hashMap.put(email,publicKey);
                            return;
                        }
                    }
                } else {
                    encrypt.setText("Encryption Disabled");
                    if (hashMap.containsKey(email)) {
                        hashMap.remove(email);
                    }
                }
            }
        });
        toWhom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (getCurrentFocus()==toWhom) {
                    if (!s.toString().contains("@")) {
                        encrypt.setEnabled(false);
                        encrypt.setChecked(false);
                        return;
                    }

                    if (hashMap.containsKey(s.toString())) {
                        encrypt.setEnabled(true);
                        encrypt.setChecked(true);
                        return;
                    }

                    if (searchPublicKeyInKeyTable(s.toString())!=null) {
                        encrypt.setEnabled(true);
                    } else {
                        encrypt.setEnabled(false);
                        if (encrypt.isChecked()) {
                            encrypt.setChecked(false);
                        }
                    }
                }
            }
        });
    }

    private String searchPublicKeyInKeyTable(String email) {
        DatabaseManager db=new DatabaseManager(SecureClientApplication.getAppContext());
        Cursor cursor=db.getDatabase().rawQuery("select * from "+Key.TABLE_NAME+" where "+Key.EMAIL.getName()+"=?",new String[] {email});
        if (cursor.getCount()>0 && cursor !=null) {
            cursor.moveToFirst();
            return cursor.getString(cursor.getColumnIndex(Key.PUBLIC_KEY.getName()));
        }
        return null;
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
                final ProgressDialog dialog=ProgressDialog.show(SendMail.this, "Sending",
                        "Sending mail, please wait...", true);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String text=message.getText().toString();
                        try {
                            if (toWhom.getText().toString().contains("@")) {
                                text=message.getText().toString()+Constants.DELIMITER_KEY+PgpUtils.getInstance().createSignature(text,true);
                                text=PgpUtils.getInstance().encrypt(text);
                                MailClient.getInstance().send(Constants.ENCRYPTED_MESSAGE_HEADER_NAME+subject.getText().toString(),text,toWhom.getText().toString());
                                System.out.println("sended to:"+toWhom.getText().toString());
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (dialog!=null) {
                                            dialog.setIndeterminate(false);
                                            dialog.dismiss();
                                        }
                                        finish();
                                    }
                                });
                            } else {
                                //TODO : Invalid mail address
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (PGPException e) {
                            e.printStackTrace();
                        } catch (GeneralSecurityException e) {
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
