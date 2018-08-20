package com.example.voja.master01;

/**
 * Created by voja on 5.12.17..
 */

import android.util.Base64;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Arrays;

public class Encryptor {

    public String encrypt(String password, String plaintext) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, IOException {

        byte [] bytePlaintext = plaintext.getBytes();

        // Use password hash as a key
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] key = (md.digest(password.getBytes()));

        // Create Initialisation vector
        SecureRandom sr = new SecureRandom();
        byte[] values = new byte[16];
        sr.nextBytes(values);
        byte[] encIV = values;

        // Initialise cipher
        Cipher aes = null;
        aes = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec k = new SecretKeySpec(key,"AES_256");
        aes.init(Cipher.ENCRYPT_MODE, k, new IvParameterSpec(encIV));
        byte [] tempByteCiphertext = aes.doFinal(bytePlaintext);

        // Concatenate IV
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(encIV);
        outputStream.write(tempByteCiphertext);
        byte[] byteCiphertext = outputStream.toByteArray();

        return Base64.encodeToString(byteCiphertext, Base64.DEFAULT);
    }

    public String decrypt(String password, String ciphertext) throws IllegalArgumentException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {

        // Extract IV
        byte[] byteCiphertext = Base64.decode(ciphertext, Base64.DEFAULT);
        byte[] DecIV = Arrays.copyOfRange(byteCiphertext, 0, 16);
        byte[] tempCiphertext = Arrays.copyOfRange(byteCiphertext, 16, byteCiphertext.length);

        // Use password hash as a key
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] key = (md.digest(password.getBytes()));

        // Initialise cipher
        Cipher aes = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec k = new SecretKeySpec(key,"AES_256");
        aes.init(Cipher.DECRYPT_MODE, k, new IvParameterSpec(DecIV));
        byte [] tmpPlaintext = aes.doFinal(tempCiphertext);

        String plaintext = new String(tmpPlaintext, StandardCharsets.UTF_8);
        return plaintext;
    }
}
