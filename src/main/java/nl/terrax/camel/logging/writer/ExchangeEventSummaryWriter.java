package nl.terrax.camel.logging.writer;

import nl.terrax.camel.logging.model.IExchangeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public final class ExchangeEventSummaryWriter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExchangeEventSummaryWriter.class);

    private ExchangeEventSummaryWriter() {
    }

    public static void writeToLog(final IExchangeInfo exchangeEventInfo) {

        final String eventType = exchangeEventInfo.getEventType();

        if (eventType.contains(".") || "ExchangeSentEvent".equals(eventType) || "CustomLog".equals(eventType)) {

            final String info;
            if ("CustomLog".equals(eventType)) {
                info = exchangeEventInfo.getBody();
            } else if (eventType.contains(".")) {
                info = String.format("[%s] %s", exchangeEventInfo.getOriginatingUri(), exchangeEventInfo.getBody());
            } else {
                info = exchangeEventInfo.getNode();
            }

            final String sb = String.format("%-35s|%5d| %-35s| %s",
                    exchangeEventInfo.getExchangeId(),
                    exchangeEventInfo.getDuration(),
                    info,
                    exchangeEventInfo.getFromEndpointUri());

            LOGGER.info(sb);
        }
    }
}
