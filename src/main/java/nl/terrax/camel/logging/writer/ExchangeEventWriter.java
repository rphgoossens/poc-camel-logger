package nl.terrax.camel.logging.writer;

import nl.terrax.camel.logging.model.IExchangeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

import static java.time.ZoneOffset.UTC;

@Component
public final class ExchangeEventWriter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExchangeEventWriter.class);
    static final String SEPARATOR = "|___|";

    private ExchangeEventWriter() {
    }

    public static void writeToLog(final Logger logger, final IExchangeInfo exchangeEventInfo) {

        final StringBuilder logLine = new StringBuilder();
        logLine.append(appendEventBaseInfo(exchangeEventInfo));
        logLine.append(appendExchangeInfo(exchangeEventInfo));
        logLine.append(appendExchangeExceptionInfo(exchangeEventInfo));

        if (exchangeEventInfo.getException() != null) {
            writeError(logger, logLine.toString());
        } else {
            writeInfo(logger, logLine.toString());
        }
    }

    public static void writeToLog(final IExchangeInfo exchangeEventInfo) {

        final StringBuilder logLine = new StringBuilder();
        logLine.append(appendEventBaseInfo(exchangeEventInfo));
        logLine.append(appendExchangeInfo(exchangeEventInfo));
        logLine.append(appendExchangeExceptionInfo(exchangeEventInfo));

        if (exchangeEventInfo.getException() != null) {
            writeError(logLine.toString());
        } else {
            writeInfo(logLine.toString());
        }
    }

    private static String appendEventBaseInfo(final IExchangeInfo exchangeEventInfo) {
        final StringBuilder sb = new StringBuilder();

        final OffsetDateTime currentTimeInUtc = OffsetDateTime.now(UTC);
        sb.append(SEPARATOR).append(currentTimeInUtc.toInstant().toEpochMilli());
        sb.append(SEPARATOR).append(exchangeEventInfo.getEventType());
        return sb.toString();
    }

    private static String appendExchangeExceptionInfo(final IExchangeInfo exchangeEventInfo) {
        final StringBuilder sb = new StringBuilder();

        if (exchangeEventInfo != null) {
            sb.append(SEPARATOR).append(exchangeEventInfo.getHeaders());
            sb.append(SEPARATOR).append(exchangeEventInfo.getProperties());
            sb.append(SEPARATOR).append(exchangeEventInfo.getBody());
            sb.append(SEPARATOR).append(exchangeEventInfo.getException());
            sb.append(SEPARATOR).append(exchangeEventInfo.getStackTrace());
        }

        return sb.toString();
    }

    private static String appendExchangeInfo(IExchangeInfo exchangeEventInfo) {
        if (exchangeEventInfo == null) {
            return "";
        }

        return SEPARATOR + exchangeEventInfo.getContextName() +
                SEPARATOR + exchangeEventInfo.getDeadLetterChannel() +
                SEPARATOR + exchangeEventInfo.getDuration() +
                SEPARATOR + exchangeEventInfo.getExchangeId() +
                SEPARATOR + exchangeEventInfo.getFailureProcessor() +
                SEPARATOR + exchangeEventInfo.getFromEndpointUri() +
                SEPARATOR + exchangeEventInfo.getFromEndpointName() +
                SEPARATOR + exchangeEventInfo.getFromRouteId() +
                SEPARATOR + exchangeEventInfo.getHasException() +
                SEPARATOR + exchangeEventInfo.getIsFailed() +
                SEPARATOR + exchangeEventInfo.getNode() +
                SEPARATOR + exchangeEventInfo.getOriginatingUri() +
                SEPARATOR + exchangeEventInfo.getRedeliveryAttempt() +
                SEPARATOR + exchangeEventInfo.getToEndpoint() +
                SEPARATOR + exchangeEventInfo.getFailureHandled() +
                SEPARATOR + exchangeEventInfo.getErrorhandlerHandled() +
                SEPARATOR + exchangeEventInfo.getRedeliveryCounter() +
                SEPARATOR + exchangeEventInfo.getIsRedelivered() +
                SEPARATOR + exchangeEventInfo.getIsRedeliveryExhausted();
    }


    private static void writeError(final String logLine) {
        LOGGER.error(logLine);
    }

    private static void writeError(final Logger logger, final String logLine) {
        logger.error(logLine);
    }

    private static void writeInfo(final String logLine) {
        LOGGER.info(logLine);
    }

    private static void writeInfo(final Logger logger, final String logLine) {
        logger.info(logLine);
    }

}
