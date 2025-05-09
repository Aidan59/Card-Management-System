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

/**
 * Utility class for encryption and decryption using AES in GCM mode with NoPadding.
 * This class provides methods to securely encrypt and decrypt data using a secret key.
 * <p>
 * The secret key is injected from the application properties (through the `encryption.secret` property) and is used
 * for both encryption and decryption. It uses AES with GCM (Galois/Counter Mode) to provide authenticated encryption.
 * GCM mode ensures both confidentiality and integrity of the encrypted data.
 */
@Component
public class AESUtil {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int KEY_SIZE = 128;
    private static final int TAG_LENGTH_BIT = 128;
    private static final int IV_LENGTH_BYTE = 12;

    @Value("${encryption.secret}")
    private static final String secret = "y5pLHbptU4y19ROPqHQuVXu344ANc28E";

    private final SecretKey secretKey;

    /**
     * Constructs an instance of AESUtil using the secret key loaded from application properties.
     * The key is used for AES encryption and decryption.
     */
    public AESUtil() {
        this.secretKey = new SecretKeySpec(secret.getBytes(), "AES");
    }

    /**
     * Encrypts the provided data using AES/GCM/NoPadding encryption.
     * <p>
     * This method generates a random initialization vector (IV) for each encryption operation to ensure
     * that the same data encrypted multiple times results in different ciphertexts. The IV is prepended
     * to the encrypted data and both are base64-encoded for easy storage and transmission.
     *
     * @param data the plaintext data to be encrypted
     * @return the base64-encoded ciphertext with the IV prepended
     * @throws Exception if an encryption error occurs
     */
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

    /**
     * Decrypts the provided base64-encoded encrypted data using AES/GCM/NoPadding decryption.
     * <p>
     * The encrypted data must have the IV prepended to the ciphertext. This method extracts the IV and
     * uses it for decryption to retrieve the original plaintext data.
     *
     * @param encryptedData the base64-encoded encrypted data (including the IV)
     * @return the decrypted plaintext data
     * @throws Exception if a decryption error occurs
     */
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
