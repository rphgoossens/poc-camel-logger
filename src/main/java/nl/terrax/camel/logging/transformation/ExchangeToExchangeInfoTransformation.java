package nl.terrax.camel.logging.transformation;

import nl.terrax.camel.logging.model.IExchangeInfo;
import org.apache.camel.Exchange;
import org.apache.camel.ExtendedExchange;
import org.apache.camel.spi.CamelEvent;
import org.apache.camel.support.DefaultMessageHistory;
import org.apache.camel.support.MessageHelper;
import org.apache.camel.support.processor.DefaultExchangeFormatter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static org.apache.camel.util.URISupport.sanitizeUri;

public class ExchangeToExchangeInfoTransformation implements IExchangeInfo {

    private final Exchange exchange;
    private final String eventType;
    private String body = "";

    private String originationUri;


    public ExchangeToExchangeInfoTransformation(final Exchange exchange, final String eventType) {
        this.exchange = exchange;
        this.eventType = eventType;
    }

    static Object filterHeader(final Map.Entry<String, Object> header) {

        final String key = header.getKey();
        Object value = header.getValue();

        //Mask BSN
        if (key.equalsIgnoreCase("bsn") && value != null) {

            final String bsn = value.toString();
            if (bsn.length() == 9) {
                value = String.format("*****%s*%s%s", bsn.charAt(5), bsn.charAt(7), bsn.charAt(8));
            } else if (bsn.length() > 3) {
                value = String.format("%s*%s****", bsn.charAt(0), bsn.charAt(3));
            } else {
                value = "****";
            }
        }

        //Mask JWT
        if (key.equalsIgnoreCase("Authorization") && value != null) {

            final String authorizationHeader = value.toString();
            if (authorizationHeader.matches(".*\\..*$")) {
                value = authorizationHeader.replaceAll("(.*)\\..*$", "$1.<signature>");
            }
        }

        return value;
    }

    @Override
    public String getBody() {
        final StringBuilder sb = new StringBuilder();

        if (this.getEventType().contains(".")) {
            sb.append(this.body);
        }
        if (this.exchange.getException() != null) {
            sb.append("\n");
            sb.append("Exception caught:\n");
            sb.append("-----------------\n");
            sb.append("Body:\n");
            sb.append(MessageHelper.extractBodyForLogging(this.exchange.getMessage(), "", false, false, 1000));
            sb.append("\n-----------------\n");
        }
        return sb.toString();

    }

    @Override
    public void setBody(final String body) {
        this.body = body;
    }

    @Override
    public String getContextName() {
        return this.exchange.getContext() == null ? "" : this.exchange.getContext().getName();
    }

    @Override
    public String getDeadLetterChannel() {
        String deadLetterChannel = "";
        if (this.exchange instanceof CamelEvent.ExchangeFailureHandledEvent && ((CamelEvent.ExchangeFailureHandledEvent) this.exchange).isDeadLetterChannel()) {
            deadLetterChannel = sanitizeUri(((CamelEvent.ExchangeFailureHandledEvent) this.exchange).getDeadLetterUri());
        }
        return deadLetterChannel;
    }

    @Override
    public long getDuration() {
        return System.currentTimeMillis() - this.exchange.getCreated();
    }

    @Override
    public Boolean getErrorhandlerHandled() {
        return this.exchange.adapt(ExtendedExchange.class).getErrorHandlerHandled();
    }

    @Override
    public String getEventType() {
        return this.eventType;
    }

    @Override
    public String getExchangeId() {
        return this.exchange == null ? "" : this.exchange.getExchangeId();
    }

    @Override
    public Exception getException() {
        return this.exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
    }

    @Override
    public Boolean getFailureHandled() {
        return this.exchange.getProperty(Exchange.FAILURE_HANDLED, false, Boolean.class);

    }

    @Override
    public String getFailureProcessor() {
        return this.exchange instanceof CamelEvent.ExchangeFailureHandledEvent ? ((CamelEvent.ExchangeFailureHandledEvent) this.exchange).getFailureHandler().toString() : "";
    }

    @Override
    public String getFromEndpointName() {
        return this.exchange.getFromEndpoint() == null ? "" : this.exchange.getFromEndpoint().getClass().getSimpleName();
    }

    @Override
    public String getFromEndpointUri() {
        return this.exchange.getFromEndpoint() == null ? "" : this.exchange.getFromEndpoint().getEndpointUri();
    }

    @Override
    public String getFromRouteId() {
        return this.exchange.getFromRouteId();
    }

    @Override
    public Boolean getHasException() {
        return this.exchange.getProperty(Exchange.EXCEPTION_CAUGHT) != null;
    }

    @Override
    public String getHeaders() {
        final Map<String, Object> filteredHeaders = new HashMap<>();
        if (this.exchange.getIn() != null && this.exchange.getIn().getHeaders() != null) {
            for (final Map.Entry<String, Object> header : this.exchange.getIn().getHeaders().entrySet()) {
                filteredHeaders.put(header.getKey(), filterHeader(header));
            }
        }
        return filteredHeaders.toString();
    }

    @Override
    public Boolean getIsFailed() {
        return this.exchange.getProperty(Exchange.FAILURE_HANDLED) != null;
    }

    @Override
    public Boolean getIsRedelivered() {
        return this.exchange.getProperty(Exchange.REDELIVERED, false, Boolean.class);
    }

    @Override
    public Boolean getIsRedeliveryExhausted() {
        return this.exchange.adapt(ExtendedExchange.class).isRedeliveryExhausted();
    }

    @Override
    @SuppressWarnings("unchecked")
    public String getNode() {
        String node;
        try {
            if (this.exchange.getProperties().containsKey(Exchange.MESSAGE_HISTORY)) {
                final LinkedList<DefaultMessageHistory> history;
                history = this.exchange.getProperty(Exchange.MESSAGE_HISTORY, LinkedList.class);
                node = history.get(history.size() - 1).getNode().toString();
            } else {
                node = "-";
            }
        } catch (
                final Exception ex) {
            node = "unresolvable: " + ex.getMessage();
        }
        return node;
    }

    @Override
    public String getOriginatingUri() {
        if (!StringUtils.isEmpty(this.originationUri)) {
            return this.originationUri;
        }
        return sanitizeUri(this.exchange.getFromEndpoint() == null ? "" : this.exchange.getFromEndpoint().getEndpointUri());
    }

    @Override
    public void setOriginatingUri(final String originatingUri) {
        this.originationUri = originatingUri;
    }

    @Override
    public String getProperties() {
        //Only return properties with exceptions
        return this.exchange.getException() == null ? "{}" : this.exchange.getProperties().toString();
    }

    @Override
    public Integer getRedeliveryAttempt() {
        if (!(this.exchange instanceof CamelEvent.ExchangeRedeliveryEvent)) {
            return 0;
        }
        final CamelEvent.ExchangeRedeliveryEvent exchangeRedeliveryEvent = (CamelEvent.ExchangeRedeliveryEvent) this.exchange;

        return exchangeRedeliveryEvent.getAttempt();
    }

    @Override
    public Integer getRedeliveryCounter() {
        return this.exchange.getProperty(Exchange.REDELIVERY_COUNTER, 0L, Integer.class);
    }

    @Override
    public String getStackTrace() {
        final StringBuilder sb = new StringBuilder();
        if (this.exchange.getException() != null) {
            String stackTrace = ExceptionUtils.getStackTrace(this.exchange.getException());

            final DefaultExchangeFormatter formatter = new DefaultExchangeFormatter();
            formatter.setShowExchangeId(true);
            formatter.setMultiline(true);
            formatter.setShowHeaders(true);
            formatter.setStyle(DefaultExchangeFormatter.OutputStyle.Fixed);
            final String messageHistory = MessageHelper.dumpMessageHistoryStacktrace(this.exchange, formatter, false);

            sb.append(messageHistory);

            stackTrace = "\r\nStackTrace\r\n"
                    + "---------------------------------------------------------------------------------------------------------------------------------------"
                    + "\r\n"
                    + stackTrace;

            sb.append(stackTrace);
        }

        return sb.toString();
    }

    @Override
    public String getToEndpoint() {
        String toEndpoint = "";

        if (this.exchange instanceof CamelEvent.ExchangeSentEvent) {
            final CamelEvent.ExchangeSentEvent exchangeSentEvent = (CamelEvent.ExchangeSentEvent) this.exchange;
            toEndpoint = exchangeSentEvent.getEndpoint().getEndpointUri();
        } else if (this.exchange instanceof CamelEvent.ExchangeSendingEvent) {
            final CamelEvent.ExchangeSendingEvent exchangeSendingEvent = (CamelEvent.ExchangeSendingEvent) this.exchange;
            toEndpoint = exchangeSendingEvent.getEndpoint().getEndpointUri();
        } else if (this.exchange instanceof CamelEvent.ExchangeCompletedEvent) {
            toEndpoint = this.exchange.getProperty(Exchange.TO_ENDPOINT, String.class);
        }

        return sanitizeUri(toEndpoint);
    }

}

