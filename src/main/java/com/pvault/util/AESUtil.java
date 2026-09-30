package com.pvault.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;

public final class AESUtil {

    private static final String TRANSFORM = "AES/GCM/NoPadding";
    private static final int IV_BYTES = 12;
    private static final int TAG_BITS = 128;
    private static final int KEY_BITS = 256;
    private static final int SALT_BYTES = 16;
    private static final int PBKDF2_ITERATIONS = 210_000;
    private static final SecureRandom RANDOM = new SecureRandom();

    private AESUtil() { }

    public static byte[] generateSalt() {
        byte[] salt = new byte[SALT_BYTES];
        RANDOM.nextBytes(salt);
        return salt;
    }

    /** Derives a 256-bit AES key from the master password + salt (PBKDF2-HMAC-SHA256). */
    public static SecretKey deriveKey(char[] masterPassword, byte[] salt)
            throws GeneralSecurityException {
        PBEKeySpec spec = new PBEKeySpec(masterPassword, salt, PBKDF2_ITERATIONS, KEY_BITS);
        try {
            byte[] keyBytes = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec).getEncoded();
            return new SecretKeySpec(keyBytes, "AES");
        } finally {
            spec.clearPassword();
        }
    }

    /** Plain text -> Base64(IV + ciphertext). A fresh random IV is used every call. */
    public static String encrypt(String plainText, SecretKey key)
            throws GeneralSecurityException {
        byte[] iv = new byte[IV_BYTES];
        RANDOM.nextBytes(iv);

        Cipher cipher = Cipher.getInstance(TRANSFORM);
        cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_BITS, iv));
        byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        return Base64.getEncoder().encodeToString(
                ByteBuffer.allocate(iv.length + cipherText.length).put(iv).put(cipherText).array());
    }

    /** Reverses encrypt(). Throws if the key is wrong or the data was tampered with. */
    public static String decrypt(String encryptedBase64, SecretKey key)
            throws GeneralSecurityException {
        byte[] data = Base64.getDecoder().decode(encryptedBase64);

        byte[] iv = new byte[IV_BYTES];
        byte[] cipherText = new byte[data.length - IV_BYTES];
        System.arraycopy(data, 0, iv, 0, IV_BYTES);
        System.arraycopy(data, IV_BYTES, cipherText, 0, cipherText.length);

        Cipher cipher = Cipher.getInstance(TRANSFORM);
        cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_BITS, iv));
        return new String(cipher.doFinal(cipherText), StandardCharsets.UTF_8);
    }
}