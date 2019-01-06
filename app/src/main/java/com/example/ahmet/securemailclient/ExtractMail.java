package com.example.ahmet.securemailclient;

import java.io.IOException;

import javax.mail.*;

public class ExtractMail {
    public static void writePart(Part p) throws MessagingException,IOException {
        if (p instanceof Message){
            if (p.isMimeType("text/plain")) {
                System.out.println("This is plain text");
                System.out.println("---------------------------");
                System.out.println((String) p.getContent());
            }
            //check if the content has attachment
            else if (p.isMimeType("multipart/*")) {
                System.out.println("This is a Multipart");
                System.out.println("---------------------------");
                Multipart mp = (Multipart) p.getContent();
                int count = mp.getCount();
                for (int i = 0; i < count; i++)
                    writePart(mp.getBodyPart(i));
            }
            //check if the content is a nested message
            else if (p.isMimeType("message/rfc822")) {
                System.out.println("This is a Nested Message");
                System.out.println("---------------------------");
                writePart((Part) p.getContent());
            }
            //check if the content is an inline image
        /*else if (p.isMimeType("image/jpeg")) {
            System.out.println("--------> image/jpeg");
            Object o = p.getContent();

            InputStream x = (InputStream) o;
            // Construct the required byte array
            System.out.println("x.length = " + x.available());
            while ((i = (int) ((InputStream) x).available()) > 0) {
                int result = (int) (((InputStream) x).read(bArray));
                if (result == -1)
                    int i = 0;
                byte[] bArray = new byte[x.available()];

                break;
            }
            FileOutputStream f2 = new FileOutputStream("/tmp/image.jpg");
            f2.write(bArray);
        }
        /*else if (p.getContentType().contains("image/")) {
            System.out.println("content type" + p.getContentType());
            File f = new File("image" + new Date().getTime() + ".jpg");
            DataOutputStream output = new DataOutputStream(
                    new BufferedOutputStream(new FileOutputStream(f)));
            com.sun.mail.util.BASE64DecoderStream test =
                    (com.sun.mail.util.BASE64DecoderStream) p
                            .getContent();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = test.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
        }*/
            else {
                Object o = p.getContent();
                if (o instanceof String) {
                    System.out.println("This is a string");
                    System.out.println("---------------------------");
                    System.out.println((String) o);
                }
            /*else if (o instanceof InputStream) {
                System.out.println("This is just an input stream");
                System.out.println("---------------------------");
                InputStream is = (InputStream) o;
                is = (InputStream) o;
                int c;
                while ((c = is.read()) != -1)
                    System.out.write(c);
            }*/
                else {
                    System.out.println("This is an unknown type");
                    System.out.println("---------------------------");
                    System.out.println(o.toString());
                }
            }
        }
    }
    public static String writePart(Part p,boolean stringOnly) throws MessagingException,IOException {
        if (p instanceof Message){
            if (p.isMimeType("text/plain")) {
                System.out.println("This is plain text");
                System.out.println("---------------------------");
                System.out.println((String) p.getContent());
                return (String) p.getContent();
            }
            else {
                return "Null";
            }
        }
        return "Null en dış";
    }
}
