package com.example.ahmet.securemailclient;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.ahmet.securemailclient.database.DatabaseManager;
import com.example.ahmet.securemailclient.dummy.DummyContent;
import com.example.ahmet.securemailclient.model.Account;

import org.spongycastle.jce.provider.BouncyCastleProvider;

import java.io.IOException;
import java.security.Security;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.mail.MessagingException;

public class MainActivity extends AppCompatActivity implements MailFragment.OnListFragmentInteractionListener {

    private final int PATTERN_SET_CODE=254;

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
        final ProgressDialog dialog=ProgressDialog.show(MainActivity.this, "Connected",
                "Receiving your emails. Please wait...", true);
        /*if (!MailClient.getInstance().isConnected) {
            executorService.submit(new Thread(new Runnable() {
                @Override
                public void run() {
                    if(!MailClient.getInstance().connect(Constants.email,Constants.password)) {

                    }
                }
            }));
        }*/

        executorService.submit(new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Security.addProvider(new BouncyCastleProvider());
                    PgpUtils.getInstance(Constants.email);
                    MailClient.getInstance().receive(15);
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
                                    if (Constants.patternAsMD5.equals("N/A")) {
                                        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                                        alert.setTitle("Pattern Not Found!");
                                        alert.setMessage("You should set your pattern to decrypt your encrypted emails.");
                                        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent = new Intent(MainActivity.this,PatternSetActivity.class);
                                                startActivityForResult(intent,PATTERN_SET_CODE);
                                            }
                                        });
                                        alert.show();
                                    }
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==PATTERN_SET_CODE) {
            if (resultCode==RESULT_OK) {
                DatabaseManager db=new DatabaseManager(SecureClientApplication.getAppContext());
                Account account=db.getAccount(Constants.email);
                Constants.patternAsMD5=data.getStringExtra("pattern");
                account.setPattern(Constants.patternAsMD5);
                db.getDatabase().update(Account.TABLE_NAME,account.getContentValues(),Account.EMAIL.getName()+"=?",new String[] {Constants.email});
            }
        }
    }

    private void loadMoreData(final int totalItems) {
        ExecutorService executorService=Executors.newSingleThreadExecutor();
        final ProgressDialog dialog=ProgressDialog.show(MainActivity.this, "Loading",
                "Receiving your emails to load more mail. Please wait...", true);

        executorService.submit(new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MailClient.getInstance().receiveMore(totalItems);
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
    }

    private void generateNewKey() {
        ExecutorService executorService=Executors.newSingleThreadExecutor();
        final ProgressDialog dialog=ProgressDialog.show(MainActivity.this, "Generating..",
                "Your keys are changing. Please wait...", true);

        executorService.submit(new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    PgpUtils.getInstance().generateNewKey();
                    System.out.println("generated in New Key");
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                finally {
                    if (dialog!=null) {
                        dialog.setIndeterminate(false);
                        dialog.dismiss();
                    }
                }
            }
        }));
        executorService.shutdown();
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
            /*case R.id.action_share_key:
                menuIntent=new Intent(MainActivity.this,ShareActivity.class);
                break;
            case R.id.action_read_qr_code:
                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {

                        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                                Manifest.permission.CAMERA)) {
                            //TODO : Explanation
                        } else {
                            ActivityCompat.requestPermissions(this,
                                    new String[]{Manifest.permission.CAMERA},
                                    14232);
                        }
                    } else {
                        menuIntent=new Intent(MainActivity.this,QRCodeScanner.class);
                    }
                } else {
                    menuIntent=new Intent(MainActivity.this,QRCodeScanner.class);
                }
                break;*/
            case R.id.change_keys:
                generateNewKey();
                System.out.println("generated.");
                break;
            case R.id.load_more:
                loadMoreData(DummyContent.ITEMS.size());
                break;
            case R.id.send_public_key:
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                alertDialog.setTitle("Email");
                alertDialog.setMessage("Enter Email");

                final EditText input = new EditText(MainActivity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                alertDialog.setView(input);
                alertDialog.setIcon(android.R.drawable.ic_menu_send);
                alertDialog.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, int which) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (input.getText().toString().contains("@")) {
                                            System.out.println("sending.");
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    dialog.cancel();
                                                }
                                            });
                                            MailClient.getInstance().send(Constants.PUBLIC_KEY_SUBJECT_NAME, Constants.pgpPublicKey, input.getText().toString());
                                            System.out.println("sent.");
                                        } else {
                                            System.out.println("invalid.");
                                            //TODO : Invalid mail address.
                                        }
                                    }
                                }).start();
                            }
                        });

                        alertDialog.setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });

                alertDialog.show();
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
        Intent intent=new Intent(MainActivity.this,MailDetailActivity.class);
        intent.putExtra("mailIndex",DummyContent.ITEMS.indexOf(item));
        startActivity(intent);
    }
}
