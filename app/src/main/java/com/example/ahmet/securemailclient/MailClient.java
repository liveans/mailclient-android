package com.example.ahmet.securemailclient;


import android.content.Intent;
import android.os.AsyncTask;

import com.example.ahmet.securemailclient.dummy.DummyContent;

import java.io.IOException;
import java.util.Properties;

import javax.mail.AuthenticationFailedException;
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

    public void receive() throws MessagingException,IOException {
        Folder inbox = mReceiveStore.getFolder("inbox");
        inbox.open(Folder.READ_ONLY);
        int messageCount = inbox.getMessageCount();
        System.out.println("Total Messages in INBOX:- " + messageCount);
        DummyContent.ITEMS.clear();
        DummyContent.ITEM_MAP.clear();
        for (int i = 5; i>0; i--) {
            //System.out.println("Mail Subject:- " + inbox.getMessage(messageCount-i).getSubject());
            //System.out.println("Mail From:- " + inbox.getMessage(messageCount-i).getFrom()[0]);
            //System.out.println("Mail Content:- " + inbox.getMessage(messageCount-i).getContent().toString());
            //System.out.println("------------------------------------------------------------");
            DummyContent.addItem(new DummyContent.DummyItem(inbox.getMessage(messageCount-i).getFrom()[0].toString(),inbox.getMessage(messageCount-i).getSubject(),inbox.getMessage(messageCount-i).getContent().toString()));
        }
    }

    public boolean connect(String mEmail,String mPassword) {
        this.mUsername=mEmail;
        this.mPassword=mPassword;
        try {
            setupSMTPConnection();
            setupIMAPConnection();
            isConnected=true;
            return true;
        } catch (AuthenticationFailedException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } finally {
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
