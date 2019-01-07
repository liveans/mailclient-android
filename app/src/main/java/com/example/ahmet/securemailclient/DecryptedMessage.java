package com.example.ahmet.securemailclient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.ahmet.securemailclient.database.DatabaseManager;
import com.example.ahmet.securemailclient.dummy.DummyContent;

public class DecryptedMessage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decrypted_message);
        TextView decryptedMessageTextView=findViewById(R.id.decryptedMessageTextView);
        DatabaseManager db=new DatabaseManager(SecureClientApplication.getAppContext());
        DummyContent.DummyItem item=DummyContent.ITEMS.get(getIntent().getIntExtra("mailData",-1));
        try {
            getSupportActionBar().setTitle(item.subject.replace(Constants.ENCRYPTED_MESSAGE_HEADER_NAME,""));
            getSupportActionBar().setSubtitle(item.fromWho);
            String decryptedText=PgpUtils.getInstance().decrypt(item.content,Constants.pgpPassword);
            String[] items=decryptedText.split(Constants.DELIMITER_KEY);
            String publicKeyFromEmail=db.getPublicKeyFromEmail(item.fromWho);
            if (PgpUtils.getInstance().verifySignature(items[0],items[1],publicKeyFromEmail)) {
                decryptedMessageTextView.setText(items[0]);
            } else {
                decryptedMessageTextView.setText("Unfortunately, the text and signature aren't verified with each other.");
            }

        } catch (Exception e) {
            //TODO : Alert Dialog show.
            decryptedMessageTextView.setText("This text signature and public key aren't verified with each other. Try to send your public key and let it send again.");
            e.printStackTrace();
        }
    }
}
