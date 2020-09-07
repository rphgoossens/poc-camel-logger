package nl.terrax.camel.logging.event;

import nl.terrax.camel.logging.model.IExchangeInfo;
import nl.terrax.camel.logging.model.StatusEvent;
import nl.terrax.camel.logging.transformation.ExchangeToExchangeInfoTransformation;
import nl.terrax.camel.logging.writer.ExchangeEventSummaryWriter;
import nl.terrax.camel.logging.writer.ExchangeEventWriter;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class LoggingStatusEventListener {

    @Async
    @EventListener
    public void onStatusEvent(final StatusEvent statusEvent) {

        final String eventType = statusEvent.getNameOfEvent();
        final Exchange exchange = statusEvent.getExchange();
        final IExchangeInfo exchangeInfo;

        if (exchange != null) {
            exchangeInfo = new ExchangeToExchangeInfoTransformation(exchange, eventType);

            exchangeInfo.setBody(statusEvent.getEventInfo());
            exchangeInfo.setOriginatingUri(statusEvent.getStateOfEvent());

            Logger customLogger = exchange.getIn().getHeader("CustomLogger", Logger.class);

            if (Objects.nonNull(customLogger)) {
                ExchangeEventWriter.writeToLog(customLogger, exchangeInfo);
                ExchangeEventSummaryWriter.writeToLog(exchangeInfo);

            } else{
                ExchangeEventWriter.writeToLog(exchangeInfo);
                ExchangeEventSummaryWriter.writeToLog(exchangeInfo);
            }
        }
    }
}
