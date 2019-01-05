package com.example.ahmet.securemailclient;

import com.example.ahmet.securemailclient.model.Account;

import org.spongycastle.bcpg.ArmoredInputStream;
import org.spongycastle.bcpg.ArmoredOutputStream;
import org.spongycastle.bcpg.BCPGOutputStream;
import org.spongycastle.bcpg.HashAlgorithmTags;
import org.spongycastle.bcpg.SymmetricKeyAlgorithmTags;
import org.spongycastle.bcpg.sig.Features;
import org.spongycastle.bcpg.sig.KeyFlags;
import org.spongycastle.crypto.generators.RSAKeyPairGenerator;
import org.spongycastle.crypto.params.RSAKeyGenerationParameters;
import org.spongycastle.openpgp.PGPCompressedData;
import org.spongycastle.openpgp.PGPCompressedDataGenerator;
import org.spongycastle.openpgp.PGPEncryptedData;
import org.spongycastle.openpgp.PGPEncryptedDataGenerator;
import org.spongycastle.openpgp.PGPEncryptedDataList;
import org.spongycastle.openpgp.PGPException;
import org.spongycastle.openpgp.PGPKeyPair;
import org.spongycastle.openpgp.PGPKeyRingGenerator;
import org.spongycastle.openpgp.PGPLiteralData;
import org.spongycastle.openpgp.PGPLiteralDataGenerator;
import org.spongycastle.openpgp.PGPObjectFactory;
import org.spongycastle.openpgp.PGPPrivateKey;
import org.spongycastle.openpgp.PGPPublicKey;
import org.spongycastle.openpgp.PGPPublicKeyEncryptedData;
import org.spongycastle.openpgp.PGPPublicKeyRing;
import org.spongycastle.openpgp.PGPSecretKey;
import org.spongycastle.openpgp.PGPSecretKeyRing;
import org.spongycastle.openpgp.PGPSignature;
import org.spongycastle.openpgp.PGPSignatureGenerator;
import org.spongycastle.openpgp.PGPSignatureList;
import org.spongycastle.openpgp.PGPSignatureSubpacketGenerator;
import org.spongycastle.openpgp.PGPUtil;
import org.spongycastle.openpgp.operator.PBESecretKeyDecryptor;
import org.spongycastle.openpgp.operator.PBESecretKeyEncryptor;
import org.spongycastle.openpgp.operator.PGPDigestCalculator;
import org.spongycastle.openpgp.operator.bc.BcPBESecretKeyDecryptorBuilder;
import org.spongycastle.openpgp.operator.bc.BcPBESecretKeyEncryptorBuilder;
import org.spongycastle.openpgp.operator.bc.BcPGPContentSignerBuilder;
import org.spongycastle.openpgp.operator.bc.BcPGPDigestCalculatorProvider;
import org.spongycastle.openpgp.operator.bc.BcPGPKeyPair;
import org.spongycastle.openpgp.operator.bc.BcPublicKeyDataDecryptorFactory;
import org.spongycastle.openpgp.operator.jcajce.JcaPGPContentSignerBuilder;
import org.spongycastle.openpgp.operator.jcajce.JcaPGPContentVerifierBuilderProvider;
import org.spongycastle.openpgp.operator.jcajce.JcaPGPDigestCalculatorProviderBuilder;
import org.spongycastle.openpgp.operator.jcajce.JcePBESecretKeyDecryptorBuilder;
import org.spongycastle.openpgp.operator.jcajce.JcePBESecretKeyEncryptorBuilder;
import org.spongycastle.openpgp.operator.jcajce.JcePGPDataEncryptorBuilder;
import org.spongycastle.openpgp.operator.jcajce.JcePublicKeyKeyEncryptionMethodGenerator;
import org.spongycastle.util.encoders.Hex;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class PgpUtils {

    private static final String PROVIDER = "SC";

    private static PgpUtils mInstance;

    private PGPSecretKey pgpSec = null;
    private PGPSecretKeyRing skr = null;
    private PGPPublicKeyRing pkr = null;
    private String pgpPassword=null;

    private final static String keyId = "test@test.test";

    private PgpUtils () { }

    public static synchronized  PgpUtils getInstance() {
        if (mInstance == null) {
            mInstance = new PgpUtils();
            mInstance.initCrypto(Constants.email);
        }

        return mInstance;
    }

    public static synchronized PgpUtils getInstance (String mEmail) {
        if (mInstance == null) {
            mInstance = new PgpUtils();
            mInstance.initCrypto(mEmail);
        }

        return mInstance;
    }

    public String getPublicKeyFingerprint ()
    {
        PGPPublicKey key = pkr.getPublicKey();
        String fullKey = new String(Hex.encode(key.getFingerprint()));
        return fullKey.substring(fullKey.length()-16);
    }

    public String decrypt(String encryptedText, String password) throws Exception {

        byte[] encrypted = encryptedText.getBytes();
        InputStream in = new ByteArrayInputStream(encrypted);
        in = PGPUtil.getDecoderStream(in);
        PGPObjectFactory pgpF = new PGPObjectFactory(in);
        PGPEncryptedDataList enc;
        Object o = pgpF.nextObject();
        if (o instanceof PGPEncryptedDataList) {
            enc = (PGPEncryptedDataList) o;
        } else {
            enc = (PGPEncryptedDataList) pgpF.nextObject();
        }
        PGPPrivateKey sKey = null;
        PGPPublicKeyEncryptedData pbe = null;
        while (sKey == null && enc.getEncryptedDataObjects().hasNext()) {
            pbe = (PGPPublicKeyEncryptedData)enc.getEncryptedDataObjects().next();
            sKey = getPrivateKey(skr, pbe.getKeyID(), password.toCharArray());
        }
        if (pbe != null) {
            InputStream clear = pbe.getDataStream(new BcPublicKeyDataDecryptorFactory(sKey));
            PGPObjectFactory pgpFact = new PGPObjectFactory(clear);
            PGPCompressedData cData = (PGPCompressedData) pgpFact.nextObject();
            pgpFact = new PGPObjectFactory(cData.getDataStream());
            PGPLiteralData ld = (PGPLiteralData) pgpFact.nextObject();
            InputStream unc = ld.getInputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int ch;
            while ((ch = unc.read()) >= 0) {
                out.write(ch);
            }
            byte[] returnBytes = out.toByteArray();
            out.close();
            return new String(returnBytes);
        }
        return null;
    }

    private static PGPPublicKey getPublicKey(PGPPublicKeyRing publicKeyRing) {
        Iterator<?> kIt = publicKeyRing.getPublicKeys();
        while (kIt.hasNext()) {
            PGPPublicKey k = (PGPPublicKey) kIt.next();
            if (k.isEncryptionKey()) {
                return k;
            }
        }
        return null;
    }

    private static PGPPrivateKey getPrivateKey(PGPSecretKeyRing keyRing, long keyID, char[] pass) throws PGPException {
        PGPSecretKey secretKey = keyRing.getSecretKey(keyID);
        PBESecretKeyDecryptor decryptor = new BcPBESecretKeyDecryptorBuilder(new BcPGPDigestCalculatorProvider()).build(pass);
        return secretKey.extractPrivateKey(decryptor);
    }

    public String encrypt(String msgText) throws IOException, PGPException {
        byte[] clearData = msgText.getBytes();
        PGPPublicKey encKey = getPublicKey(pkr);
        ByteArrayOutputStream encOut = new ByteArrayOutputStream();
        OutputStream out = new ArmoredOutputStream(encOut);
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        PGPCompressedDataGenerator comData = new PGPCompressedDataGenerator(PGPCompressedDataGenerator.ZIP);
        OutputStream cos = comData.open(bOut);
        PGPLiteralDataGenerator lData = new PGPLiteralDataGenerator();
        OutputStream pOut = lData.open(cos, PGPLiteralData.BINARY, PGPLiteralData.CONSOLE, clearData.length, new Date());
        pOut.write(clearData);
        lData.close();
        comData.close();
        PGPEncryptedDataGenerator encGen =
                new PGPEncryptedDataGenerator(
                        new JcePGPDataEncryptorBuilder(PGPEncryptedData.AES_256).setWithIntegrityPacket(true).setSecureRandom(
                                new SecureRandom()).setProvider(PROVIDER));
        if (encKey != null) {
            encGen.addMethod(new JcePublicKeyKeyEncryptionMethodGenerator(encKey).setProvider(PROVIDER));
            byte[] bytes = bOut.toByteArray();
            OutputStream cOut = encGen.open(out, bytes.length);
            cOut.write(bytes);
            cOut.close();
        }
        out.close();
        return new String(encOut.toByteArray());
    }

    public final static PGPKeyRingGenerator generateKeyRingGenerator (String keyId, char[] pass) throws PGPException{
        RSAKeyPairGenerator kpg = new RSAKeyPairGenerator();
        kpg.init(new RSAKeyGenerationParameters(BigInteger.valueOf(0x10001), new SecureRandom(), 4096, 12));
        PGPKeyPair rsakp_sign = new BcPGPKeyPair(PGPPublicKey.RSA_SIGN, kpg.generateKeyPair(), new Date());
        PGPKeyPair rsakp_enc = new BcPGPKeyPair(PGPPublicKey.RSA_ENCRYPT, kpg.generateKeyPair(), new Date());
        PGPSignatureSubpacketGenerator signhashgen = new PGPSignatureSubpacketGenerator();
        signhashgen.setKeyFlags(false, KeyFlags.SIGN_DATA|KeyFlags.CERTIFY_OTHER|KeyFlags.SHARED);
        signhashgen.setPreferredSymmetricAlgorithms(false, new int[]{SymmetricKeyAlgorithmTags.AES_256, SymmetricKeyAlgorithmTags.AES_192, SymmetricKeyAlgorithmTags.AES_128});
        signhashgen.setPreferredHashAlgorithms(false, new int[]{HashAlgorithmTags.SHA256, HashAlgorithmTags.SHA1, HashAlgorithmTags.SHA384, HashAlgorithmTags.SHA512, HashAlgorithmTags.SHA224});
        signhashgen.setFeature(false, Features.FEATURE_MODIFICATION_DETECTION);
        PGPSignatureSubpacketGenerator enchashgen = new PGPSignatureSubpacketGenerator();
        enchashgen.setKeyFlags(false, KeyFlags.ENCRYPT_COMMS | KeyFlags.ENCRYPT_STORAGE);
        PGPDigestCalculator sha1Calc = new BcPGPDigestCalculatorProvider().get(HashAlgorithmTags.SHA1);
        PGPDigestCalculator sha256Calc = new BcPGPDigestCalculatorProvider().get(HashAlgorithmTags.SHA256);
        PBESecretKeyEncryptor pske = (new BcPBESecretKeyEncryptorBuilder(PGPEncryptedData.AES_256, sha256Calc, 0xc0)).build(pass);
        PGPKeyRingGenerator keyRingGen = new PGPKeyRingGenerator (PGPSignature.POSITIVE_CERTIFICATION, rsakp_sign,
                keyId, sha1Calc, signhashgen.generate(), null, new BcPGPContentSignerBuilder(rsakp_sign.getPublicKey().getAlgorithm(),
                HashAlgorithmTags.SHA1), pske);
        keyRingGen.addSubKey(rsakp_enc, enchashgen.generate(), null);
        return keyRingGen;
    }



    public String getPublicKey () throws IOException {
        ByteArrayOutputStream baosPkr = new ByteArrayOutputStream();
        ArmoredOutputStream armoredStreamPkr = new ArmoredOutputStream(baosPkr);
        pkr.encode(armoredStreamPkr);
        armoredStreamPkr.close();
        return new String(baosPkr.toByteArray(), Charset.defaultCharset());
    }


    public final static String genPGPPrivKey (PGPKeyRingGenerator krgen) throws IOException {
        ByteArrayOutputStream baosPriv = new ByteArrayOutputStream ();
        PGPSecretKeyRing skr = krgen.generateSecretKeyRing();
        ArmoredOutputStream armoredStreamPriv = new ArmoredOutputStream(baosPriv);
        skr.encode(armoredStreamPriv);
        armoredStreamPriv.close();
        return new String(baosPriv.toByteArray(), Charset.defaultCharset());
    }

    private static void exportKeyPair(
            OutputStream    secretOut,
            OutputStream    publicOut,
            PublicKey publicKey,
            PrivateKey privateKey,
            String          identity,
            char[]          passPhrase,
            boolean         armor)
            throws IOException, InvalidKeyException, NoSuchProviderException, SignatureException, PGPException
    {
        if (armor)
        {
            secretOut = new ArmoredOutputStream(secretOut);
        }

        PGPDigestCalculator sha1Calc = new JcaPGPDigestCalculatorProviderBuilder().build().get(HashAlgorithmTags.SHA1);
        PGPKeyPair          keyPair = new PGPKeyPair(PGPPublicKey.RSA_GENERAL, publicKey, privateKey, new Date());
        PGPSecretKey        secretKey = new PGPSecretKey(PGPSignature.DEFAULT_CERTIFICATION, keyPair, identity, sha1Calc, null, null, new JcaPGPContentSignerBuilder(keyPair.getPublicKey().getAlgorithm(), HashAlgorithmTags.SHA1), new JcePBESecretKeyEncryptorBuilder(PGPEncryptedData.CAST5, sha1Calc).setProvider("SC").build(passPhrase));

        secretKey.encode(secretOut);

        secretOut.close();

        if (armor)
        {
            publicOut = new ArmoredOutputStream(publicOut);
        }

        PGPPublicKey    key = secretKey.getPublicKey();

        key.encode(publicOut);

        publicOut.close();
    }

    private static PGPSecretKeyRing getPGPSecretKeyRing(String secretKey) throws IOException {
        ArmoredInputStream ais = new ArmoredInputStream(new ByteArrayInputStream(secretKey.getBytes()));
        return (PGPSecretKeyRing) new PGPObjectFactory(ais).nextObject();
    }

    private static PGPPublicKeyRing getPGPPublicKeyRing(String publicKey) throws IOException {
        ArmoredInputStream ais = new ArmoredInputStream(new ByteArrayInputStream(publicKey.getBytes()));
        return (PGPPublicKeyRing) new PGPObjectFactory(ais).nextObject();
    }

    public synchronized void initCrypto (String mEmail)
    {
        if (pgpSec == null) {
            try {
                String publicKey="",secretKey="",password="";
                //TODO:find email in database
                if (mEmail.equals("") && !secretKey.equals("N/A"))
                {
                    InputStream in=new ByteArrayInputStream(secretKey.getBytes());
                    skr = getPGPSecretKeyRing(secretKey);
                    in.close();

                    in = new ByteArrayInputStream(publicKey.getBytes());
                    pkr = getPGPPublicKeyRing(publicKey);
                    in.close();

                    pgpPassword=password;
                }
                else {
                    Account account=new Account();
                    account.setEmail(mEmail);
                    pgpPassword=UUID.randomUUID().toString();
                    account.setPasswordKey(pgpPassword);
                    final PGPKeyRingGenerator krgen = generateKeyRingGenerator(keyId, pgpPassword.toCharArray());
                    skr = krgen.generateSecretKeyRing();

                    ByteArrayOutputStream baos=new ByteArrayOutputStream();
                    ArmoredOutputStream sout = new ArmoredOutputStream(baos);
                    skr.encode(sout);
                    sout.close();
                    account.setSecretKey(new String(baos.toByteArray(),Charset.defaultCharset()));
                    //System.out.println(new String(baos.toByteArray(),Charset.defaultCharset()));
                    baos.close();

                    pkr = krgen.generatePublicKeyRing();
                    baos=new ByteArrayOutputStream();
                    sout = new ArmoredOutputStream(baos);
                    pkr.encode(sout);
                    sout.close();
                    account.setPublicKey(new String(baos.toByteArray(),Charset.defaultCharset()));
                    //System.out.println(new String(baos.toByteArray(),Charset.defaultCharset()));
                    baos.close();

                }

                pgpSec = skr.getSecretKey();


            } catch (PGPException pgpe) {
                pgpe.printStackTrace();
            } catch (Exception pgpe) {
                pgpe.printStackTrace();

            }
        }
    }

    public String createSignature(String data, boolean armor) throws GeneralSecurityException, IOException, PGPException
    {

        //  PGPPrivateKey            prKey = skey.extractPrivateKey(new JcePBESecretKeyDecryptorBuilder().setProvider("SC").build(pass));
        InputStream in = new ByteArrayInputStream(data.getBytes());
        OutputStream out=null;
        ByteArrayOutputStream bout=new ByteArrayOutputStream();
        if (armor)
        {
            out = new ArmoredOutputStream(bout);
        }

        PGPPrivateKey            pgpPrivKey = pgpSec.extractPrivateKey(new JcePBESecretKeyDecryptorBuilder().setProvider("SC").build(pgpPassword.toCharArray()));
        PGPSignatureGenerator sGen = new PGPSignatureGenerator(new JcaPGPContentSignerBuilder(pgpSec.getPublicKey().getAlgorithm(), PGPUtil.SHA256).setProvider("SC"));

        sGen.init(PGPSignature.BINARY_DOCUMENT, pgpPrivKey);
        BCPGOutputStream bOut;
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

    public boolean verifySignature(
            String          data,
            String     signature)
            throws GeneralSecurityException, IOException, PGPException
    {
        InputStream in2 = new ByteArrayInputStream(signature.getBytes());
        InputStream in= PGPUtil.getDecoderStream(in2);

        PGPObjectFactory    pgpFact = new PGPObjectFactory(in);
        PGPSignatureList p3;

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
        PGPPublicKey                key = pkr.getPublicKey();

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

    private static final char PARAMETER_DELIMITER = '&';
    private static final char PARAMETER_EQUALS_CHAR = '=';
    public static String createQueryStringForParameters(Map<String, String> parameters) {
        StringBuilder parametersAsQueryString = new StringBuilder();
        if (parameters != null) {
            boolean firstParameter = true;

            for (String parameterName : parameters.keySet()) {
                if (!firstParameter) {
                    parametersAsQueryString.append(PARAMETER_DELIMITER);
                }

                parametersAsQueryString.append(parameterName)
                        .append(PARAMETER_EQUALS_CHAR)
                        .append(URLEncoder.encode(
                                parameters.get(parameterName)));

                firstParameter = false;
            }
        }
        return parametersAsQueryString.toString();
    }


}