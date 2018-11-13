package ServerSide;


import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;
import java.util.Scanner;

public class DecryptPM {

    private static final String AES = "AES";

    /**
     * encrypt a value and generate a keyfile
     * if the keyfile is not found then a new one is created
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public static String encrypt(String value, File keyFile)
            throws GeneralSecurityException, IOException{
        if (!keyFile.exists()) {
            KeyGenerator keyGen = KeyGenerator.getInstance(DecryptPM.AES);
            keyGen.init(128);
            SecretKey sk = keyGen.generateKey();
            FileWriter fw = new FileWriter(keyFile);
            fw.write(byteArrayToHexString(sk.getEncoded()));
            fw.flush();
            fw.close();
        }

        SecretKeySpec sks = getSecretKeySpec(keyFile);
        Cipher cipher = Cipher.getInstance(DecryptPM.AES);
        cipher.init(Cipher.ENCRYPT_MODE, sks, cipher.getParameters());
        byte[] encrypted = cipher.doFinal(value.getBytes());
        return byteArrayToHexString(encrypted);
    }

    /**
     * decrypt a value
     * @throws GeneralSecurityException
     * @throws IOException
     */
    private static String decrypt(String message, File keyFile)
            throws GeneralSecurityException, IOException{
        SecretKeySpec sks = getSecretKeySpec(keyFile);
        Cipher cipher = Cipher.getInstance(DecryptPM.AES);
        cipher.init(Cipher.DECRYPT_MODE, sks);
        byte[] decrypted = cipher.doFinal(hexStringToByteArray(message));
        return new String(decrypted);
    }

    private static SecretKeySpec getSecretKeySpec(File keyFile)
            throws NoSuchAlgorithmException, IOException{
        byte [] key = readKeyFile(keyFile);
        return new SecretKeySpec(key, DecryptPM.AES);
    }

    private static byte [] readKeyFile(File keyFile)
            throws FileNotFoundException {
        Scanner scanner =
                new Scanner(keyFile).useDelimiter("\\Z");
        String keyValue = scanner.next();
        scanner.close();
        return hexStringToByteArray(keyValue);
    }


    private static String byteArrayToHexString(byte[] b){
        StringBuffer sb = new StringBuffer(b.length * 2);
        for (int i = 0; i < b.length; i++){
            int v = b[i] & 0xff;
            if (v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString().toUpperCase();
    }

    private static byte[] hexStringToByteArray(String s) {
        byte[] b = new byte[s.length() / 2];
        for (int i = 0; i < b.length; i++){
            int index = i * 2;
            int v = Integer.parseInt(s.substring(index, index + 2), 16);
            b[i] = (byte)v;
        }
        return b;
    }

    private static String readFile(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            return sb.toString();
        }
        finally {
            br.close();
        }
    }
    /***
    NOTE:
    For Decrypt to work the expected requirement is availability of Key and Enc data file in resource folder for server side
     ***/

    public static void main(String[] args) throws IOException, GeneralSecurityException {
        String path = System.getProperty("user.dir");
        String resourcePath = path+"\\src\\main\\resources\\";
       // path = path + "\\src\\main\\resources\\licenseInfo.properties";
            final String KEY_FILE = resourcePath+"EncKeyFile.key";
            final String PWD_FILE = resourcePath+"encrypted.properties";

            /*String mainFile = readFile(path);
            Properties p1 = new Properties();

            String encryptedFile = DecryptPM.encrypt(mainFile, new File(KEY_FILE));
            p1.put("LicenseData:", encryptedFile);
            p1.store(new FileWriter(PWD_FILE), "Encrypted data's Properties file is now created...!");
            System.out.println("Encrypted DATA OF PD1 via P1: \n"+p1.getProperty("LicenseData:"));*/

        Properties p2 = new Properties();

        p2.load(new FileReader(PWD_FILE));
        String encryptedFile = p2.getProperty("LicenseData:");
        System.out.println("Encrypted DATA OF PD1 via P2: \n"+encryptedFile);
        System.out.println("Decrypted value of PD1 is : \n"+ DecryptPM.decrypt(encryptedFile, new File(KEY_FILE)));

    }
}
