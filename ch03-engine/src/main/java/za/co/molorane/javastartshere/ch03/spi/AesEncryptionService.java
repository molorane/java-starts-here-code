package za.co.molorane.javastartshere.ch03.spi;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Concrete provider implementation of the {@link EncryptionService} SPI.
 * <p>
 * Implements a lightweight mock AES transformation for architectural demonstration.
 */
public class AesEncryptionService implements EncryptionService {

    private static final String PREFIX = "AES-ENC:";

    @Override
    public String getAlgorithm() {
        return "AES-256-GCM";
    }

    @Override
    public String encrypt(String plaintext) {
        if (plaintext == null) {
            return null;
        }
        String encoded = Base64.getEncoder().encodeToString(plaintext.getBytes(StandardCharsets.UTF_8));
        return PREFIX + encoded;
    }

    @Override
    public String decrypt(String ciphertext) {
        if (ciphertext == null || !ciphertext.startsWith(PREFIX)) {
            throw new IllegalArgumentException("Invalid ciphertext format for AES service");
        }
        String raw = ciphertext.substring(PREFIX.length());
        byte[] decoded = Base64.getDecoder().decode(raw);
        return new String(decoded, StandardCharsets.UTF_8);
    }
}
