package DigitalSigner;


import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.List;


    public class VerifyDigitalSign {
        private List<byte[]> list;

       /* @SuppressWarnings("unchecked")
        //The constructor of VerifyMessage class retrieves the byte arrays from the File and prints the message only if the signature is verified.
        public VerifyDigitalSign(String filename, String keyFile) throws Exception {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename));
            this.list = (List<byte[]>) in.readObject();
            in.close();

            System.out.println(verifySignature(list.get(0), list.get(1), keyFile) ? "VERIFIED MESSAGE" + "\n----------------\n" + new String(list.get(0)) : "Could not verify the signature.");
        }*/
        @SuppressWarnings("unchecked")
        //The constructor of VerifyMessage class retrieves the byte arrays from the File and prints the message only if the signature is verified.
        public boolean verify(String filename, String keyFile) throws Exception {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename));
            this.list = (List<byte[]>) in.readObject();
            in.close();

            //System.out.println(verifySignature(list.get(0), list.get(1), keyFile) ? "VERIFIED MESSAGE" + "\n----------------\n" + new String(list.get(0)) : "Could not verify the signature.");
            return verifySignature(list.get(0), list.get(1), keyFile);
        }

        @SuppressWarnings("unchecked")
        public String dContent(String filename, String keyFile) throws Exception {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename));
            this.list = (List<byte[]>) in.readObject();
            in.close();

            return (verifySignature(list.get(0), list.get(1), keyFile) ? new String(list.get(0)) : "Not able to verify digital sign");
        }

        //Method for signature verification that initializes with the Public Key, updates the data to be verified and then verifies them using the signature
        private boolean verifySignature(byte[] data, byte[] signature, String keyFile) throws Exception {
            Signature sig = Signature.getInstance("SHA1withRSA");
            sig.initVerify(getPublic(keyFile));
            sig.update(data);

            return sig.verify(signature);
        }

        //Method to retrieve the Public Key from a file
        private PublicKey getPublic(String filename) throws Exception {
            byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePublic(spec);
        }



    }
