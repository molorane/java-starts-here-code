package za.co.molorane.javastartshere.ch03.spi;

/**
 * Service Provider Interface (SPI) demonstrating decoupled architecture in modern Java.
 * <p>
 * Key Concepts (Chapter 3 Topic 6):
 * <ul>
 *   <li>Higher-level business code depends solely on this SPI abstraction.</li>
 *   <li>Concrete provider implementations are decoupled and discovered dynamically at runtime
 *       via {@link java.util.ServiceLoader}.</li>
 *   <li>In JPMS modular applications, this corresponds to {@code uses} and {@code provides ... with}.</li>
 * </ul>
 */
public interface EncryptionService {

    /**
     * Algorithm identifier provided by this service.
     */
    String getAlgorithm();

    /**
     * Encrypts the plaintext payload.
     *
     * @param plaintext sensitive input string
     * @return encrypted representation
     */
    String encrypt(String plaintext);

    /**
     * Decrypts the encrypted payload.
     *
     * @param ciphertext encrypted input string
     * @return original plaintext
     */
    String decrypt(String ciphertext);
}
