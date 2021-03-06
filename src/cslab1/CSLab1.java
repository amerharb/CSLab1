package cslab1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.cert.X509Certificate;

public class CSLab1
{

    //fill data from ciphertext.enc file into these var
    byte[] encKey1 = new byte[128];
    byte[] encIV = new byte[128];
    byte[] encKey2 = new byte[128];
    byte[] encText;

    //ReciverPrivateKey
    PrivateKey reciverPrivateKey;

    //value after decryption values from ciphertext.enc
    byte[] Key1;
    byte[] IV;
    byte[] Key2;
    byte[] plainText;

    //fill value from ciphertext.mac1.txt and ciphertext.mac2.txt file
    byte[] mac1 = new byte[16];
    byte[] mac2 = new byte[16];

    //calc the HMacMD5 from plainText and Key2
    byte[] HMacMD5;

    //Public Key
    PublicKey publicKey;

    //Signature
    byte[] sig1File = new byte[128];
    byte[] sig2File = new byte[128];

    Signature mySig;

    public static void main(String[] args)
    {
        new CSLab1();
    }

    public CSLab1()
    {
        final boolean debug = true;
        try {
            // read file enc file and put them inside 4 var
            fillEncKeysAndText();

            //TEST: print Enc the values
            if (debug) {
                printEnc();
            }

            //fill the private key from file to public var called ReciverPrivateKey
            fillReciverPrivateKey();

            //TEST: print Enc the values
            if (debug) {
                printReciverPrivateKey();
            }

            //Decryption the values of the Keys and IV
            decKeys();

            //TEST: print the Keys and IV
            if (debug) {
                printKeys();
            }

            //decrytion the text and fill plainText var
            decryptText();

            //TEST: print the plain text
            if (debug) {
                System.out.println(new String(plainText));
            }

            //fill mac from files
            readMac();

            //TEST: print the mac1 and mac2
            if (debug) {
                printMac();
            }

            //TASK 3: Verify the Message Authentication Code
            //calc HmacMD5 and fill var HmacMD5 with its value
            fillHmacMD5();

            //TEST: print HmacMD5
            if (debug) {
                printHMacMD5();
            }

            //check which mac is correct
            System.out.println("");
            if (compairArray(mac1, HMacMD5)) {
                System.out.println("MAC1 is the correct one");
            } else if (compairArray(mac2, HMacMD5)) {
                System.out.println("MAC2 is the correct one");
            } else {
                System.out.println("no match found!!!");
            }
            System.out.println("");

            //TASK 4: Verify the Digital Signature
            fillPublicKey();
            //print the public key
            
            //TEST
            if (debug) {
                System.out.println(publicKey.toString());
            }
            
            //fill the 2 var sig1File, sig2File
            fillSignatures();

            
            System.out.println("Verify Signature 1 : " + mySig.verify(sig1File));
            System.out.println("Verify Signature 2 : " + mySig.verify(sig2File));

        } catch (Exception ex) {
            Logger.getLogger(CSLab1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void fillPublicKey()
    {
        FileInputStream fin = null;
        try {
            fin = new FileInputStream("lab1Sign.cert");
            CertificateFactory f = CertificateFactory.getInstance("X.509");
            X509Certificate certificate = (X509Certificate) f.generateCertificate(fin);
            publicKey = certificate.getPublicKey();
        } catch (CertificateException ex) {
            Logger.getLogger(CSLab1.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CSLab1.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fin.close();
            } catch (IOException ex) {
                Logger.getLogger(CSLab1.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void fillSignatures()
    {
        FileInputStream fis1 = null;
        FileInputStream fis2 = null;
        try {
            // read file enc file and put them inside 3 var
            File f1 = new File("ciphertext.enc.sig1");
            File f2 = new File("ciphertext.enc.sig2");

            fis1 = new FileInputStream(f1);
            fis2 = new FileInputStream(f2);

            fis1.read(sig1File);
            fis2.read(sig2File);

            mySig = Signature.getInstance("SHA1withRSA");
            mySig.initVerify(publicKey);
            mySig.update(plainText);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(CSLab1.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CSLab1.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SignatureException ex) {
            Logger.getLogger(CSLab1.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(CSLab1.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(CSLab1.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fis1.close();
                fis2.close();
            } catch (IOException ex) {
                Logger.getLogger(CSLab1.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    private void fillEncKeysAndText()
    {
        try {
            // read file enc file and put them inside 3 var
            File f = new File("ciphertext.enc");
            FileInputStream fis;
            fis = new FileInputStream(f);
            fis.read(encKey1);
            fis.read(encIV);
            fis.read(encKey2);

            int r = 0;
            int size = 0;
            byte[] all = new byte[0];
            final int BUFFER_SIZE = 1024;
            byte[] b = new byte[BUFFER_SIZE];
            while ((r = fis.read(b)) != -1) {
                size += r;

                //keep final array in temp array before expand it
                byte[] temp = new byte[all.length];
                System.arraycopy(all, 0, temp, 0, all.length);
                //expand the final array
                all = new byte[size]; //final array
                System.arraycopy(temp, 0, all, 0, temp.length);
                System.arraycopy(b, 0, all, temp.length, r);
            }
            encText = all;
            fis.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(CSLab1.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CSLab1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void decKeys()
    {
        try {

            Cipher rsaDec = Cipher.getInstance("RSA");

            rsaDec.init(Cipher.DECRYPT_MODE, reciverPrivateKey);
            Key1 = rsaDec.doFinal(encKey1);
            IV = rsaDec.doFinal(encIV);
            Key2 = rsaDec.doFinal(encKey2);

        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(CSLab1.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(CSLab1.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(CSLab1.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(CSLab1.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(CSLab1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void fillReciverPrivateKey()
    {
        try {
            KeyStore s = KeyStore.getInstance("JCEKS");
            FileInputStream fis = new FileInputStream("lab1Store");
            s.load(fis, "lab1StorePass".toCharArray());
            fis.close();

            reciverPrivateKey = (PrivateKey) s.getKey("lab1EncKeys", "lab1KeyPass".toCharArray());

        } catch (FileNotFoundException ex) {
            Logger.getLogger(CSLab1.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CSLab1.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KeyStoreException ex) {
            Logger.getLogger(CSLab1.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(CSLab1.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CertificateException ex) {
            Logger.getLogger(CSLab1.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnrecoverableKeyException ex) {
            Logger.getLogger(CSLab1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void decryptText()
    {
        try {
            Cipher aesDec = Cipher.getInstance("aes/cbc/pkcs5padding");
            Key k = new SecretKeySpec(Key1, "AES");
            IvParameterSpec v = new IvParameterSpec(IV);

            aesDec.init(Cipher.DECRYPT_MODE, k, v);
            plainText = aesDec.doFinal(encText);

        } catch (InvalidKeyException ex) {
            Logger.getLogger(CSLab1.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(CSLab1.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(CSLab1.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(CSLab1.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(CSLab1.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidAlgorithmParameterException ex) {
            Logger.getLogger(CSLab1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void readMac()
    {
        try {
            String mac1Str = new String(Files.readAllBytes(Paths.get("ciphertext.mac1.txt")));
            for (int i = 0; i < 16; i++) {
                mac1[i] = (byte) Integer.parseInt(mac1Str.substring(i * 2, i * 2 + 2), 16);
            }

            String mac2Str = new String(Files.readAllBytes(Paths.get("ciphertext.mac2.txt")));
            for (int i = 0; i < 16; i++) {
                mac2[i] = (byte) Integer.parseInt(mac2Str.substring(i * 2, i * 2 + 2), 16);
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(CSLab1.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CSLab1.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(CSLab1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void fillHmacMD5()
    {

        try {
            SecretKeySpec k = new SecretKeySpec(Key2, "HmacMD5");
            Mac mac = Mac.getInstance("HmacMD5");
            mac.init(k);

            HMacMD5 = mac.doFinal(plainText);

        } catch (InvalidKeyException | NoSuchAlgorithmException ex) {
            Logger.getLogger(CSLab1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void printEnc()
    {
        for (byte b : encKey1) {
            System.out.print(b);
        }
        System.out.println("");
        for (byte b : encIV) {
            System.out.print(b);
        }
        System.out.println("");
        for (byte b : encKey2) {
            System.out.print(b);
        }
        System.out.println("");
        System.out.println("Enc data Length: " + encText.length);
    }

    private void printKeys()
    {
        for (byte b : Key1) {
            System.out.print(b);
        }
        System.out.println("");
        for (byte b : IV) {
            System.out.print(b);
        }
        System.out.println("");
        for (byte b : Key2) {
            System.out.print(b);
        }
        System.out.println("");
    }

    private void printMac()
    {
        System.out.println("");
        System.out.print("MAC1 Length: " + mac1.length);
        System.out.println("");
        System.out.print("MAC1: ");
        for (byte b : mac1) {
            System.out.print(b);
        }
        System.out.println("");
        System.out.print("MAC2 Length: " + mac2.length);
        System.out.println("");
        System.out.print("MAC2: ");
        for (byte b : mac2) {
            System.out.print(b);
        }
    }

    private void printHMacMD5()
    {
        System.out.println("");
        System.out.print("HmacMD5 Length: " + HMacMD5.length);
        System.out.println("");
        System.out.print("HmacMD5: ");
        for (byte b : HMacMD5) {
            System.out.print(b);
        }
    }

    private boolean compairArray(byte[] byteArray1, byte[] byteArray2)
    {
        if (byteArray1 == null || byteArray2 == null) {           // even if both is null this will be consider not equal
            return false;
        } else if (byteArray1.length != byteArray2.length) {
            return false;
        } else {
            for (int i = 0; i < byteArray1.length; i++) {
                if (byteArray1[i] != byteArray2[i]) {
                    return false;
                }
            }
            return true;
        }
    }

    private void printReciverPrivateKey()
    {
        System.out.println(reciverPrivateKey.toString());
    }

}
