package com.example.ahmet.securemailclient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ShareActivity extends AppCompatActivity {
    ListView shareListView;
    String[] shareList={"QR Code"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        shareListView=(ListView) findViewById(R.id.shareListView);
        ArrayAdapter<String> veriAdaptoru=new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, android.R.id.text1, shareList);
        shareListView.setAdapter(veriAdaptoru);
        shareListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=null;
                switch (shareList[position]) {
                    /*case "Whatsapp":
                        String text=Constants.email+Constants.DELIMITER_KEY+Constants.pgpPublicKey;
                        File path=generateKeyOnSD(getApplicationContext(),Constants.email+".keypgp",text);
                        openWhatsAppConversation(path);
                        break;*/
                    case "QR Code":
                        intent=new Intent(ShareActivity.this,QRCodeActivity.class);
                        break;
                    default:
                        break;
                }
                startActivity(intent);
            }
        });
    }

    public File generateKeyOnSD(Context context, String sFileName, String sBody) {
        try {
            File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "Keys");

            System.out.println(root.getAbsolutePath());
            if (!root.exists()) {
                root.getParentFile().mkdirs();
            }

            File gpxfile = new File(root.getAbsolutePath(), sFileName);
            if (gpxfile.exists()) {
                return gpxfile;
            }
            gpxfile.createNewFile();
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
            //Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
            return gpxfile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void openWhatsAppConversation(File path) {
        File outputFile = path;//new File(Environment.getExternalStorageDirectory(), "");
        Uri uri = Uri.fromFile(outputFile);

        Intent share = new Intent();
        share.setAction(Intent.ACTION_SEND);
        share.putExtra(Intent.EXTRA_STREAM, uri);
        share.setPackage("com.whatsapp");

        startActivity(share);
    }
}
