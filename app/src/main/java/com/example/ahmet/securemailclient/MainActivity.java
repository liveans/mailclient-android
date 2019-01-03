package com.example.ahmet.securemailclient;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.example.ahmet.securemailclient.dummy.DummyContent;

import org.spongycastle.openpgp.PGPException;
import org.spongycastle.openpgp.PGPKeyRingGenerator;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.mail.MessagingException;

public class MainActivity extends AppCompatActivity implements MailFragment.OnListFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.mainActivity_view, new MailFragment());
        transaction.commit();

        ExecutorService executorService=Executors.newSingleThreadExecutor();
        final ProgressDialog dialog=ProgressDialog.show(MainActivity.this, "Connecting",
                "Connecting. Please wait...", true);
        if (!MailClient.getInstance().isConnected) {
            executorService.submit(new Thread(new Runnable() {
                @Override
                public void run() {
                    MailClient.getInstance().connect(Constants.email,Constants.password);
                }
            }));
        }
        executorService.submit(new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MailClient.getInstance().receive();
                } catch (MessagingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                finally {
                    if (dialog!=null) {
                        dialog.setIndeterminate(false);
                        dialog.dismiss();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (MailFragment.getInstance()!=null) {
                                    MailFragment.getInstance().refreshData();
                                }
                            }
                        });
                    }
                }
            }
        }));
        executorService.shutdown();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,SendMail.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        Intent menuIntent=null;
        switch (id) {
            case R.id.action_share_key:
                menuIntent=new Intent(MainActivity.this,ShareActivity.class);
                break;
            case R.id.action_read_qr_code:
                menuIntent=new Intent(MainActivity.this,QrCodeScanner.class);
                break;
            default:
                break;
        }
        if (menuIntent!=null) {
            startActivity(menuIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {
        //TODO : list clicked action.
    }
}
