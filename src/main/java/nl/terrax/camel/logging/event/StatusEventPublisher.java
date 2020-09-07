package nl.terrax.camel.logging.event;

import nl.terrax.camel.logging.model.StatusEvent;
import org.apache.camel.Exchange;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class StatusEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public StatusEventPublisher(final ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void create(final String type, final String state, final String info, final Exchange exchange) {
        final StatusEvent statusEvent = new StatusEvent(this, type, state, info, exchange);
        applicationEventPublisher.publishEvent(statusEvent);
    }

    public void createWithCustomTtl(final String type, final String state, final String info, final int ttlInMinutes, final Exchange exchange) {
        final StatusEvent statusEvent = new StatusEvent(this, type, state, info, ttlInMinutes, exchange);
        applicationEventPublisher.publishEvent(statusEvent);
    }

}
