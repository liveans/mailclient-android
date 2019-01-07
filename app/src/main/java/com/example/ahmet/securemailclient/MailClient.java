package com.example.ahmet.securemailclient;


import android.content.Intent;
import android.os.AsyncTask;

import com.example.ahmet.securemailclient.database.DatabaseManager;
import com.example.ahmet.securemailclient.dummy.DummyContent;
import com.example.ahmet.securemailclient.model.Key;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

import javax.mail.AuthenticationFailedException;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailClient {
    Session mSendSession;
    Session mReceiveSession;
    Properties mSendProps;
    Properties mReceiveProps;
    Store mReceiveStore;
    String mUsername;
    String mPassword;
    public boolean isConnected=false;

    private static MailClient instance;

    private void setupSMTPConnection() {
        mSendProps = new Properties();
        mSendProps.put("mail.smtp.auth", "true");
        mSendProps.put("mail.smtp.starttls.enable", "true");
        mSendProps.put("mail.smtp.host", "smtp.gmail.com");
        mSendProps.put("mail.smtp.port", "587");

        mSendSession = Session.getInstance(mSendProps,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(mUsername, mPassword);
                    }
                });
    }

    private void setupIMAPConnection() throws MessagingException {
        mReceiveProps = new Properties();
        mReceiveProps.put("mail.imaps.auth", "true");
        mReceiveProps.put("mail.store.protocol", "imaps");
        mReceiveProps.put("mail.imaps.host", "imap.gmail.com");
        mReceiveProps.put("mail.imaps.port", "993");

        mReceiveSession = Session.getDefaultInstance(mReceiveProps, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mUsername, mPassword);
            }
        });
        mReceiveStore = mReceiveSession.getStore("imaps");
        System.out.println("Connection initiated......");
        mReceiveStore.connect(mUsername, mPassword);
        System.out.println("Connection is ready :)");
    }

    public void send(String subject,String encryptedText,String toWhom) {
        try {
            Message message = new MimeMessage(mSendSession);
            message.setFrom(new InternetAddress(Constants.email));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(toWhom));
            message.setSubject(subject);
            message.setText(encryptedText);

            Transport.send(message);

            System.out.println("Done");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public void receiveMore(int totalItems) throws MessagingException,IOException {
        setupIMAPConnection();
        DatabaseManager db=new DatabaseManager(SecureClientApplication.getAppContext());
        Folder inbox = mReceiveStore.getFolder("inbox");
        inbox.open(Folder.READ_WRITE);
        int messageCount = inbox.getMessageCount()-totalItems;
        System.out.println("Total Messages in INBOX:- " + messageCount);
        String subject="",fromWho="",content="";
        if (messageCount<0) {
            return;
        }

        if (messageCount>10)
        {
            for (int i = 0; i<10; i++) {
                Message message=inbox.getMessage(messageCount-i);
                //ExtractMail.writePart(message);
                subject=message.getSubject();
                fromWho=message.getFrom()[0].toString();
                content=message.getContent().toString();
                if (subject.equals(Constants.PUBLIC_KEY_SUBJECT_NAME)) {
                    Key key;
                    if (!db.checkIfExistsKey(fromWho)) {
                        key=new Key(fromWho,content);
                        db.getDatabase().insert(Key.TABLE_NAME,null,key.getContentValues());
                        message.setFlag(Flags.Flag.DELETED,true);
                    } else {
                        key=new Key(fromWho,content);
                        db.getDatabase().update(Key.TABLE_NAME,key.getContentValues(),Key.EMAIL.getName()+"=?",new String[]{fromWho});
                    }
                    continue;
                }
                DummyContent.addItem(new DummyContent.DummyItem(fromWho,subject,ExtractMail.writePart(message,true)));
            }
        } else {
            for (int i = 0; i<messageCount; i++) {
                Message message=inbox.getMessage(messageCount-i);
                //ExtractMail.writePart(message);
                subject=message.getSubject();
                fromWho=message.getFrom()[0].toString();
                content=message.getContent().toString();
                if (subject.equals(Constants.PUBLIC_KEY_SUBJECT_NAME)) {
                    Key key;
                    if (!db.checkIfExistsKey(fromWho)) {
                        key=new Key(fromWho,content);
                        db.getDatabase().insert(Key.TABLE_NAME,null,key.getContentValues());
                        message.setFlag(Flags.Flag.DELETED,true);
                    } else {
                        key=new Key(fromWho,content);
                        db.getDatabase().update(Key.TABLE_NAME,key.getContentValues(),Key.EMAIL.getName()+"=?",new String[]{fromWho});
                    }
                    continue;
                }
                DummyContent.addItem(new DummyContent.DummyItem(fromWho,subject,ExtractMail.writePart(message,true)));
            }
        }

        //inbox.close(true);
        //mReceiveStore.close();
    }

    public void receive(int items) throws MessagingException,IOException {
        setupIMAPConnection();
        DatabaseManager db=new DatabaseManager(SecureClientApplication.getAppContext());
        Folder inbox = mReceiveStore.getFolder("inbox");
        inbox.open(Folder.READ_WRITE);
        int messageCount = inbox.getMessageCount();
        System.out.println("Total Messages in INBOX:- " + messageCount);
        //DummyContent.ITEMS.clear();
        //DummyContent.ITEM_MAP.clear();
        String subject="",fromWho="",content="";
        if (messageCount>items)
        {
            for (int i = 0; i<items; i++) {
                Message message=inbox.getMessage(messageCount-i);
                //ExtractMail.writePart(message);
                subject=message.getSubject();
                fromWho=message.getFrom()[0].toString();
                content=message.getContent().toString();
                if (subject.equals(Constants.PUBLIC_KEY_SUBJECT_NAME)) {
                    Key key;
                    if (!db.checkIfExistsKey(fromWho)) {
                        key=new Key(fromWho,content);
                        db.getDatabase().insert(Key.TABLE_NAME,null,key.getContentValues());
                        message.setFlag(Flags.Flag.DELETED,true);
                    } else {
                        key=new Key(fromWho,content);
                        db.getDatabase().update(Key.TABLE_NAME,key.getContentValues(),Key.EMAIL.getName()+"=?",new String[]{fromWho});
                    }
                    continue;
                }
                DummyContent.addItem(new DummyContent.DummyItem(fromWho,subject,ExtractMail.writePart(message,true)));
            }
        } else {
            for (int i = 0; i<messageCount; i++) {
                Message message=inbox.getMessage(messageCount-i);
                //ExtractMail.writePart(message);
                subject=message.getSubject();
                fromWho=message.getFrom()[0].toString();
                content=message.getContent().toString();
                if (subject.equals(Constants.PUBLIC_KEY_SUBJECT_NAME)) {
                    Key key;
                    if (!db.checkIfExistsKey(fromWho)) {
                        key=new Key(fromWho,content);
                        db.getDatabase().insert(Key.TABLE_NAME,null,key.getContentValues());
                        message.setFlag(Flags.Flag.DELETED,true);
                    } else {
                        key=new Key(fromWho,content);
                        db.getDatabase().update(Key.TABLE_NAME,key.getContentValues(),Key.EMAIL.getName()+"=?",new String[]{fromWho});
                    }
                    continue;
                }
                DummyContent.addItem(new DummyContent.DummyItem(fromWho,subject,ExtractMail.writePart(message,true)));
            }
        }

        //inbox.close(true);
        //mReceiveStore.close();
    }

    public boolean connect(String mEmail,String mPassword) {
        this.mUsername=mEmail;
        this.mPassword=mPassword;
        try {
            setupSMTPConnection();
            setupIMAPConnection();
            mReceiveStore.close();
            isConnected=true;
            return true;
        } catch (AuthenticationFailedException e) {
            e.printStackTrace();
            return false;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static MailClient getInstance() {
        if (instance==null)
        {
            instance=new MailClient();
        }
        return instance;
    }

    private MailClient()  {
        if (instance==null)
        {
            instance=this;
        }
    }
}
