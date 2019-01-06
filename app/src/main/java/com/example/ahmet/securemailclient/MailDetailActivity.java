package com.example.ahmet.securemailclient;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.example.ahmet.securemailclient.dummy.DummyContent;

import java.io.IOException;

import javax.mail.MessagingException;

public class MailDetailActivity extends AppCompatActivity {

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
        final int id = getIntent().getIntExtra("mailIndex",-1);
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
                Intent intent=new Intent(MailDetailActivity.this,DecryptedMessage.class);
                intent.putExtra("mailData",id);
                startActivity(intent);
            }
        });



    }

}
