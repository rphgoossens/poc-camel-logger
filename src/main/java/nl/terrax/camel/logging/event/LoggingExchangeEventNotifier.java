package nl.terrax.camel.logging.event;

import nl.terrax.camel.logging.model.IExchangeInfo;
import nl.terrax.camel.logging.transformation.ExchangeToExchangeInfoTransformation;
import nl.terrax.camel.logging.writer.ExchangeEventSummaryWriter;
import nl.terrax.camel.logging.writer.ExchangeEventWriter;
import org.apache.camel.Exchange;
import org.apache.camel.impl.event.AbstractExchangeEvent;
import org.apache.camel.spi.CamelEvent;
import org.apache.camel.support.EventNotifierSupport;
import org.springframework.stereotype.Component;

@Component
public class LoggingExchangeEventNotifier extends EventNotifierSupport {

    @Override
    public boolean isEnabled(final CamelEvent event) {
        return event instanceof AbstractExchangeEvent;
    }

    @Override
    public void notify(final CamelEvent event) {

        final String eventType = event.getClass().getSimpleName();
        final Exchange exchange = (Exchange) event.getSource();
        final IExchangeInfo exchangeInfo;

        if (exchange != null) {
            exchangeInfo = new ExchangeToExchangeInfoTransformation(exchange, eventType);
            ExchangeEventWriter.writeToLog(exchangeInfo);
            ExchangeEventSummaryWriter.writeToLog(exchangeInfo);
        }

    }

}
