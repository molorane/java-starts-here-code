package za.co.molorane.javastartshere.ch03.spi;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;

/**
 * Service registry demonstrating dynamic runtime provider discovery via {@link ServiceLoader}.
 */
public class CryptoServiceRegistry {

    private final List<EncryptionService> providers = new ArrayList<>();

    public CryptoServiceRegistry() {
        reload();
    }

    /**
     * Discovers all available EncryptionService providers registered on the module/class path.
     */
    public void reload() {
        providers.clear();
        ServiceLoader<EncryptionService> loader = ServiceLoader.load(EncryptionService.class);
        for (EncryptionService service : loader) {
            providers.add(service);
        }
    }

    public List<EncryptionService> getAllProviders() {
        return List.copyOf(providers);
    }

    public Optional<EncryptionService> findByAlgorithm(String algorithm) {
        return providers.stream()
                .filter(p -> p.getAlgorithm().equalsIgnoreCase(algorithm))
                .findFirst();
    }
}
