package nl.terrax.camel.logging.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.camel.Exchange;
import org.springframework.context.ApplicationEvent;

@Data
@EqualsAndHashCode(callSuper = true)
public class StatusEvent extends ApplicationEvent {

    private static final int DEFAULT_TTL_IN_MINUTES = 60;
    private String nameOfEvent;
    private String stateOfEvent;
    private String eventInfo;
    private transient Exchange exchange;
    private long ttlInMinutes;


    public StatusEvent(final Object source, final String nameOfEvent, final String stateOfEvent, final String eventInfo, final Exchange exchange) {
        this(source, nameOfEvent, stateOfEvent, eventInfo, DEFAULT_TTL_IN_MINUTES, exchange);
    }

    public StatusEvent(final Object source, final String nameOfEvent, final String stateOfEvent, final String eventInfo, final int ttlInMinutes, final Exchange exchange) {
        super(source);
        this.eventInfo = eventInfo;
        this.stateOfEvent = stateOfEvent;
        this.nameOfEvent = nameOfEvent;
        this.exchange = exchange;
        this.ttlInMinutes = ttlInMinutes;
    }

}
