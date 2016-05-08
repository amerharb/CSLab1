package cslab1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;

public class CSLab1
{

    public static void main(String[] args)
    {
        try {
            // read file enc file and put them inside 3 var
            byte[] encKey1 = new byte[128];
            byte[] encIV = new byte[128];
            byte[] encKey2 = new byte[128];

            readEncKeys(encKey1, encIV, encKey2);

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


        } catch (Exception ex) {
            Logger.getLogger(CSLab1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void readEncKeys(byte[] encKey1, byte[] encIV, byte[] encKey2)
    {

        try {
            // read file enc file and put them inside 3 var
            File f = new File("ciphertext.enc");
            FileInputStream fis;
            fis = new FileInputStream(f);
            fis.read(encKey1);
            fis.read(encIV);
            fis.read(encKey2);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(CSLab1.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CSLab1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
