package za.co.molorane.javastartshere.ch09;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Enterprise PII (Personally Identifiable Information) masking engine.
 * Ensures regulatory compliance (PCI-DSS, GDPR, POPIA) by redacting credit cards,
 * national IDs, and authorization tokens before logs are emitted to streams or disk.
 */
public final class SensitiveDataMasker {

    // Matches standard 16-digit credit cards with optional hyphens or spaces
    private static final Pattern CARD_PATTERN =
            Pattern.compile("\\b(?:4[0-9]{12}(?:[0-9]{3})?|5[1-5][0-9]{14}|\\d{4}[-\\s]\\d{4}[-\\s]\\d{4}[-\\s]\\d{4})\\b");

    // Matches South African 13-digit National ID numbers
    private static final Pattern SA_ID_PATTERN =
            Pattern.compile("\\b[0-9]{2}(?:0[1-9]|1[0-2])(?:0[1-9]|[12][0-9]|3[01])[0-9]{7}\\b");

    // Matches Bearer / API Authorization tokens
    private static final Pattern AUTH_TOKEN_PATTERN =
            Pattern.compile("(?i)(bearer\\s+)[a-z0-9_.~+/=-]{10,}", Pattern.CASE_INSENSITIVE);

    private SensitiveDataMasker() {
    }

    /**
     * Sanitizes sensitive information from raw log messages.
     *
     * @param input raw message string
     * @return sanitized string with sensitive patterns masked
     */
    public static String mask(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        // Mask credit cards
        Matcher cardMatcher = CARD_PATTERN.matcher(input);
        String masked = cardMatcher.replaceAll("****-****-****-****");

        // Mask SA National IDs
        Matcher idMatcher = SA_ID_PATTERN.matcher(masked);
        masked = idMatcher.replaceAll("*************");

        // Mask Bearer tokens
        Matcher tokenMatcher = AUTH_TOKEN_PATTERN.matcher(masked);
        masked = tokenMatcher.replaceAll("$1[REDACTED_TOKEN]");

        return masked;
    }
}
