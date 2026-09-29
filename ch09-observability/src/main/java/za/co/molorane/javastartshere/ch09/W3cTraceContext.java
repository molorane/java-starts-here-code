package za.co.molorane.javastartshere.ch09;

import java.security.SecureRandom;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Encapsulates the W3C TraceContext standard (RFC / W3C Recommendation):
 * Format: {@code version-traceId-spanId-traceFlags}
 * Example: {@code 00-4bf92f3577b34da6a3ce929d0e0e4736-00f067aa0ba902b7-01}
 */
public record W3cTraceContext(
        String version,
        String traceId,
        String spanId,
        String flags
) {

    public static final String TRACEPARENT_HEADER = "traceparent";
    public static final String CURRENT_VERSION = "00";
    public static final String SAMPLED_FLAG = "01";
    public static final String NOT_SAMPLED_FLAG = "00";

    private static final Pattern HEX_32_PATTERN = Pattern.compile("^[0-9a-f]{32}$");
    private static final Pattern HEX_16_PATTERN = Pattern.compile("^[0-9a-f]{16}$");
    private static final Pattern HEX_2_PATTERN = Pattern.compile("^[0-9a-f]{2}$");
    private static final String ALL_ZEROS_32 = "00000000000000000000000000000000";
    private static final String ALL_ZEROS_16 = "0000000000000000";

    private static final SecureRandom RANDOM = new SecureRandom();

    public W3cTraceContext {
        Objects.requireNonNull(version, "version cannot be null");
        Objects.requireNonNull(traceId, "traceId cannot be null");
        Objects.requireNonNull(spanId, "spanId cannot be null");
        Objects.requireNonNull(flags, "flags cannot be null");

        if (!CURRENT_VERSION.equals(version)) {
            throw new IllegalArgumentException("Unsupported W3C traceparent version: " + version);
        }
        if (!HEX_32_PATTERN.matcher(traceId).matches() || ALL_ZEROS_32.equals(traceId)) {
            throw new IllegalArgumentException("Invalid W3C traceId: " + traceId);
        }
        if (!HEX_16_PATTERN.matcher(spanId).matches() || ALL_ZEROS_16.equals(spanId)) {
            throw new IllegalArgumentException("Invalid W3C spanId: " + spanId);
        }
        if (!HEX_2_PATTERN.matcher(flags).matches()) {
            throw new IllegalArgumentException("Invalid W3C flags: " + flags);
        }
    }

    /**
     * Parses an incoming HTTP {@code traceparent} header.
     *
     * @param headerValue the raw header value
     * @return the parsed {@link W3cTraceContext}
     */
    public static W3cTraceContext parse(String headerValue) {
        if (headerValue == null || headerValue.isBlank()) {
            throw new IllegalArgumentException("traceparent header must not be null or blank");
        }
        String[] parts = headerValue.trim().split("-");
        if (parts.length < 4) {
            throw new IllegalArgumentException("Invalid traceparent header format: " + headerValue);
        }
        return new W3cTraceContext(parts[0], parts[1], parts[2], parts[3]);
    }

    /**
     * Creates a brand-new root trace context with sampled flag enabled.
     */
    public static W3cTraceContext createRoot() {
        return createRoot(true);
    }

    public static W3cTraceContext createRoot(boolean sampled) {
        return new W3cTraceContext(
                CURRENT_VERSION,
                randomHex(16), // 16 bytes = 32 hex chars
                randomHex(8),  // 8 bytes = 16 hex chars
                sampled ? SAMPLED_FLAG : NOT_SAMPLED_FLAG
        );
    }

    /**
     * Creates a child span context under this parent trace.
     * The {@code traceId} and {@code flags} are preserved, while a new {@code spanId} is allocated.
     */
    public W3cTraceContext createChildSpan() {
        return new W3cTraceContext(
                this.version,
                this.traceId,
                randomHex(8),
                this.flags
        );
    }

    /**
     * Whether the sampled flag (0x01) is set.
     */
    public boolean isSampled() {
        try {
            int flagInt = Integer.parseInt(flags, 16);
            return (flagInt & 0x01) == 1;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Formats this context according to W3C {@code traceparent} header specification.
     */
    public String toHeaderValue() {
        return String.format("%s-%s-%s-%s", version, traceId, spanId, flags);
    }

    private static String randomHex(int byteCount) {
        byte[] bytes = new byte[byteCount];
        RANDOM.nextBytes(bytes);
        StringBuilder sb = new StringBuilder(byteCount * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
