package com.example.ahmet.securemailclient;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.Security;
import java.util.Iterator;

import org.spongycastle.bcpg.ArmoredInputStream;
import org.spongycastle.bcpg.ArmoredOutputStream;
import org.spongycastle.bcpg.BCPGOutputStream;
import org.spongycastle.jce.provider.BouncyCastleProvider;
import org.spongycastle.openpgp.PGPCompressedData;
import org.spongycastle.openpgp.PGPException;
import org.spongycastle.openpgp.PGPObjectFactory;
import org.spongycastle.openpgp.PGPPrivateKey;
import org.spongycastle.openpgp.PGPPublicKey;
import org.spongycastle.openpgp.PGPPublicKeyRing;
import org.spongycastle.openpgp.PGPPublicKeyRingCollection;
import org.spongycastle.openpgp.PGPSecretKey;
import org.spongycastle.openpgp.PGPSignature;
import org.spongycastle.openpgp.PGPSignatureGenerator;
import org.spongycastle.openpgp.PGPSignatureList;
import org.spongycastle.openpgp.PGPSignatureSubpacketGenerator;
import org.spongycastle.openpgp.PGPUtil;
import org.spongycastle.openpgp.operator.jcajce.JcaPGPContentSignerBuilder;
import org.spongycastle.openpgp.operator.jcajce.JcaPGPContentVerifierBuilderProvider;
import org.spongycastle.openpgp.operator.jcajce.JcePBESecretKeyDecryptorBuilder;

/**
 * A simple utility class that creates seperate signatures for files and verifies them.
 * <p>
 * To sign a file: DetachedSignatureProcessor -s [-a] fileName secretKey passPhrase.<br>
 * If -a is specified the output file will be "ascii-armored".
 * <p>
 * To decrypt: DetachedSignatureProcessor -v  fileName signatureFile publicKeyFile.
 * <p>
 * Note: this example will silently overwrite files.
 * It also expects that a single pass phrase
 * will have been used.
 */
public class DetachedSignatureProcessor
{
    public static boolean verifySignature(
            String          data,
            String     signature,
            String     publicKey)
            throws GeneralSecurityException, IOException, PGPException
    {
        InputStream in2 = new ByteArrayInputStream(signature.getBytes());
        InputStream in= PGPUtil.getDecoderStream(in2);

        PGPObjectFactory    pgpFact = new PGPObjectFactory(in);
        PGPSignatureList    p3;

        Object    o = pgpFact.nextObject();
        if (o instanceof PGPCompressedData)
        {
            PGPCompressedData             c1 = (PGPCompressedData)o;

            pgpFact = new PGPObjectFactory(c1.getDataStream());

            p3 = (PGPSignatureList)pgpFact.nextObject();
        }
        else
        {
            p3 = (PGPSignatureList)o;
        }


        InputStream                 dIn = new ByteArrayInputStream(data.getBytes());

        PGPSignature                sig = p3.get(0);
        PGPPublicKey                key = getPGPPublicKeyRing(publicKey).getPublicKey();

        sig.init(new JcaPGPContentVerifierBuilderProvider().setProvider("SC"), key);

        int ch;
        while ((ch = dIn.read()) >= 0)
        {
            sig.update((byte)ch);
        }

        dIn.close();

        if (sig.verify())
        {
            System.out.println("signature verified.");
            return true;
        }
        else
        {
            System.out.println("signature verification failed.");
            return false;
        }
    }

    private static PGPPublicKeyRing getPGPPublicKeyRing(String publicKey) throws IOException {
        ArmoredInputStream ais = new ArmoredInputStream(new ByteArrayInputStream(publicKey.getBytes()));
        return (PGPPublicKeyRing) new PGPObjectFactory(ais).nextObject();
    }

    public static String createSignature(PGPSecretKey skey, String data, String pass, boolean armor) throws GeneralSecurityException, IOException, PGPException
    {

        //  PGPPrivateKey            prKey = skey.extractPrivateKey(new JcePBESecretKeyDecryptorBuilder().setProvider("SC").build(pass));
        InputStream in = new ByteArrayInputStream(data.getBytes());
        OutputStream out=null;
        ByteArrayOutputStream bout=new ByteArrayOutputStream();
        if (armor)
        {
            out = new ArmoredOutputStream(bout);
        }

        PGPPrivateKey            pgpPrivKey = skey.extractPrivateKey(new JcePBESecretKeyDecryptorBuilder().setProvider("SC").build(pass.toCharArray()));
        PGPSignatureGenerator    sGen = new PGPSignatureGenerator(new JcaPGPContentSignerBuilder(skey.getPublicKey().getAlgorithm(), PGPUtil.SHA256).setProvider("SC"));

        sGen.init(PGPSignature.BINARY_DOCUMENT, pgpPrivKey);
        BCPGOutputStream         bOut;
        if (armor) {
            bOut = new BCPGOutputStream(out);
        }
        else {
            bOut = new BCPGOutputStream(bout);
        }
        InputStream              fIn = new BufferedInputStream(in);

        int n = -1;
        byte[] buffer = new byte[2048];
        while ((n = fIn.read(buffer)) >= 0)
        {
            sGen.update(buffer,0,n);
        }

        fIn.close();

        sGen.generate().encode(bOut);

        if (armor)
        {
            out.close();
        }

        return new String(bout.toByteArray());
    }

/**
 PGPSignatureGenerator sGen = new PGPSignatureGenerator(new JcaPGPContentSignerBuilder(skey.getPublicKey().getAlgorithm(), PGPUtil.SHA256).setProvider("BC"));
 PGPSignatureSubpacketGenerator  spGen = new PGPSignatureSubpacketGenerator();

 sGen.init(PGPSignature.CANONICAL_TEXT_DOCUMENT, prKey);
 Iterator userIDs = skey.getPublicKey().getUserIDs();
 if (userIDs.hasNext()) {
 spGen.setSignerUserID(false, (String)userIDs.next());
 sGen.setHashedSubpackets(spGen.generate());
 }

 ArmoredOutputStream aos = new ArmoredOutputStream(out);
 aos.beginClearText(PGPUtil.SHA256);

 InputStream              fIn = new BufferedInputStream(in);

 int ch;
 while ((ch = fIn.read()) >= 0)
 {
 sGen.update((byte)ch);
 aos.write((byte)ch);
 }

 fIn.close();


 aos.endClearText();

 BCPGOutputStream bOut = new BCPGOutputStream(aos);
 sGen.generate().encode(bOut);

 aos.flush();
 aos.close();
 **/
}