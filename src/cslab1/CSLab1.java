package cslab1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class CSLab1
{

    byte[] encKey1 = new byte[128];
    byte[] encIV = new byte[128];
    byte[] encKey2 = new byte[128];
    byte[] encText;

    byte[] Key1;
    byte[] IV;
    byte[] Key2;
    byte[] plainText;

    public static void main(String[] args)
    {
        new CSLab1();
    }

    public CSLab1()
    {

        try {
            // read file enc file and put them inside 3 var
            readEncKeysAndText();

            //TEST
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
            System.out.println(encText.length);
            for (byte b : encText) {
                System.out.print(b);
            }
            //deEnc
            decKeys();

            //TEST
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

        } catch (Exception ex) {
            Logger.getLogger(CSLab1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void readEncKeysAndText()
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

            PrivateKey prK = getReciverPrivateKey();

            rsaDec.init(Cipher.DECRYPT_MODE, prK);
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

    private PrivateKey getReciverPrivateKey()
    {
        try {
            KeyStore s = KeyStore.getInstance("JCEKS");
            FileInputStream fis = new FileInputStream("lab1Store");
            s.load(fis, "lab1StorePass".toCharArray());
            fis.close();

            PrivateKey k = (PrivateKey) s.getKey("lab1EncKeys", "lab1KeyPass".toCharArray());
            return k;

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
        return null;
    }

    private void decryptText()
    {

    }
}
