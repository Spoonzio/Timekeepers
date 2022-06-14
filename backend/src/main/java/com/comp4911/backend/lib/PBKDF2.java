package com.comp4911.backend.lib;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class PBKDF2 {

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int FINAL_KEY_LENGTH = 128;

    public static String hashPassword(String password, String salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        char[] passCharAry = password.toCharArray();
        byte[] hashedSalt = getSalt(salt);
        PBEKeySpec keySpec = new PBEKeySpec(passCharAry, hashedSalt, 10000, FINAL_KEY_LENGTH);
        SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);;

        SecretKey secretKey = skf.generateSecret(keySpec);
        byte[] passByteAry = secretKey.getEncoded();
        return Base64.getEncoder().encodeToString(passByteAry);
    }

    public static boolean verifyPassword(String password, String salt, String hashedPassword) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String hashedPasswordAttempt = hashPassword(password, salt);
        return hashedPassword.equals(hashedPasswordAttempt);
    }

    private static byte[] getSalt(String salt) throws NoSuchAlgorithmException {
        byte[] saltBytes = null;
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");;
        messageDigest.update(salt.getBytes());
        saltBytes = messageDigest.digest();
        return saltBytes;
    }

}
