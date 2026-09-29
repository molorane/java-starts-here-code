package za.co.molorane.javastartshere.ch09;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.CompositeConverter;

/**
 * Custom Logback CompositeConverter that intercepts and sanitizes log event output.
 * Directly referenced in logback.xml conversion rules.
 */
public class PiiMaskingConverter extends CompositeConverter<ILoggingEvent> {

    @Override
    protected String transform(ILoggingEvent event, String in) {
        return SensitiveDataMasker.mask(in);
    }
}
