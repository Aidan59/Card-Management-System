package com.example.card_management_system.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

@Component
public class AESUtil {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int KEY_SIZE = 128;
    private static final int TAG_LENGTH_BIT = 128;
    private static final int IV_LENGTH_BYTE = 12;

    @Value("${encryption.secret}")
    private static final String secret = "y5pLHbptU4y19ROPqHQuVXu344ANc28E";

    private final SecretKey secretKey;

    public AESUtil() {
        this.secretKey = new SecretKeySpec(secret.getBytes(), "AES");
    }

    public String encrypt(String data) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);

        byte[] iv = new byte[IV_LENGTH_BYTE];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);

        GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);

        cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec);

        byte[] encrypted = cipher.doFinal(data.getBytes());

        byte[] encryptedIvAndText = new byte[IV_LENGTH_BYTE + encrypted.length];
        System.arraycopy(iv, 0, encryptedIvAndText, 0, IV_LENGTH_BYTE);
        System.arraycopy(encrypted, 0, encryptedIvAndText, IV_LENGTH_BYTE, encrypted.length);

        return Base64.getEncoder().encodeToString(encryptedIvAndText);
    }

    public String decrypt(String encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);

        byte[] decoded = Base64.getDecoder().decode(encryptedData);

        byte[] iv = Arrays.copyOfRange(decoded, 0, IV_LENGTH_BYTE);
        byte[] cipherText = Arrays.copyOfRange(decoded, IV_LENGTH_BYTE, decoded.length);

        GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);

        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);

        byte[] decrypted = cipher.doFinal(cipherText);

        return new String(decrypted);
    }
}
