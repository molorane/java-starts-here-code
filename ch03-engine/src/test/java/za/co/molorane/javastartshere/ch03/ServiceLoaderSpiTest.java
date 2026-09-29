package za.co.molorane.javastartshere.ch03;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import za.co.molorane.javastartshere.ch03.spi.CryptoServiceRegistry;
import za.co.molorane.javastartshere.ch03.spi.EncryptionService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Chapter 3: Service Provider Interface (SPI) & ServiceLoader Tests")
class ServiceLoaderSpiTest {

    @Test
    @DisplayName("Should dynamically discover registered AesEncryptionService via ServiceLoader")
    void shouldDiscoverRegisteredProviderViaServiceLoader() {
        CryptoServiceRegistry registry = new CryptoServiceRegistry();

        assertThat(registry.getAllProviders())
                .isNotEmpty()
                .hasSizeGreaterThanOrEqualTo(1);

        Optional<EncryptionService> aesService = registry.findByAlgorithm("AES-256-GCM");
        assertThat(aesService).isPresent();

        EncryptionService service = aesService.get();
        String secret = "SensitiveBankingData2026";
        String encrypted = service.encrypt(secret);

        assertThat(encrypted)
                .isNotNull()
                .isNotEqualTo(secret)
                .startsWith("AES-ENC:");

        String decrypted = service.decrypt(encrypted);
        assertThat(decrypted).isEqualTo(secret);
    }
}
