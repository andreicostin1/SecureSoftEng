package service.vaxapp.service;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionService {
    private static final String PASSWORD = "kjhgJHGUIloi3478OJKHsgdMSGF346ASD@$%^&*gfdjgkA,@££?fdgIMNGDF43jqzpp;ghJGS--=3!";
    private static final String SALT = "PPsqC2319M?@@??$£?asdger";
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";

    public static SecretKey getSecretKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec keySpec = new PBEKeySpec(PASSWORD.toCharArray(), SALT.getBytes(), 65536, 256);
        SecretKey secret = new SecretKeySpec(factory.generateSecret(keySpec).getEncoded(), "AES");
        return secret;
    }

    public static IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
        return new IvParameterSpec(iv);
    }

    public static String encrypt(String input)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidKeySpecException {

        SecretKey key;
        key = getSecretKey();
        IvParameterSpec iv = generateIv();
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        byte[] cipherText = cipher.doFinal(input.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(cipherText);
    }

    public static String decrypt(String cipherText)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidKeySpecException {

        SecretKey key = getSecretKey();
        IvParameterSpec iv = generateIv();
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        try {
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
        } catch (Exception e) {
            // TODO: Add logging
            System.out.println("An error occurred while initialising cipher on decrypt. Error: " + e);
        }
        try {
            String decryptedString = new String(cipher.doFinal(Base64.getDecoder().decode(cipherText)));
            System.out.println("Plaintext decoded string: " + decryptedString);
            return decryptedString;
        } catch (Exception e) {
            // TODO: Add Logging
            System.out.println("An error occurred while decoding with base64. Error + " + e);
        }
        return new String("");
    }
}
