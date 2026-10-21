
package app.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Base64;

public class GoogleTokenEncryption {

    private static final String PREFIX = "enc:v1:";
    private static final int IV_LENGTH = 12;
    private static final int TAG_LENGTH = 128;
    private static final SecureRandom RANDOM = new SecureRandom();

    private static final String ENCRYPTION_KEY =
            Utils.getPropertyValue(
                    "GOOGLE_TOKEN_ENCRYPTION_KEY",
                    "config.properties"
            );

    public static String encrypt(String value) {
        if (value == null || value.isBlank()) {
            return value;
        }

        if (value.startsWith(PREFIX)) {
            return value;
        }

        try {
            byte[] key = Base64.getDecoder().decode(ENCRYPTION_KEY);
            byte[] iv = new byte[IV_LENGTH];
            RANDOM.nextBytes(iv);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(
                    Cipher.ENCRYPT_MODE,
                    new SecretKeySpec(key, "AES"),
                    new GCMParameterSpec(TAG_LENGTH, iv)
            );

            byte[] encrypted = cipher.doFinal(
                    value.getBytes(java.nio.charset.StandardCharsets.UTF_8)
            );

            ByteBuffer buffer = ByteBuffer.allocate(
                    iv.length + encrypted.length
            );
            buffer.put(iv);
            buffer.put(encrypted);

            return PREFIX + Base64.getEncoder().encodeToString(
                    buffer.array()
            );

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Could not encrypt Google token",
                    e
            );
        }
    }

    public static String decrypt(String value) {
        if (value == null || value.isBlank()) {
            return value;
        }

        if (!value.startsWith(PREFIX)) {
            return value;
        }

        try {
            byte[] key = Base64.getDecoder().decode(ENCRYPTION_KEY);
            byte[] data = Base64.getDecoder().decode(
                    value.substring(PREFIX.length())
            );

            ByteBuffer buffer = ByteBuffer.wrap(data);
            byte[] iv = new byte[IV_LENGTH];
            buffer.get(iv);

            byte[] encrypted = new byte[buffer.remaining()];
            buffer.get(encrypted);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(
                    Cipher.DECRYPT_MODE,
                    new SecretKeySpec(key, "AES"),
                    new GCMParameterSpec(TAG_LENGTH, iv)
            );

            byte[] decrypted = cipher.doFinal(encrypted);

            return new String(
                    decrypted,
                    java.nio.charset.StandardCharsets.UTF_8
            );

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Could not decrypt Google token",
                    e
            );
        }
    }
}
