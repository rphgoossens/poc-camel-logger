package nl.terrax.camel.logging.model;

public interface IExchangeInfo {

    String getBody();

    void setBody(String body);

    String getContextName();

    String getDeadLetterChannel();

    long getDuration();

    Boolean getErrorhandlerHandled();

    String getEventType();

    String getExchangeId();

    Exception getException();

    Boolean getFailureHandled();

    String getFailureProcessor();

    String getFromEndpointName();

    String getFromEndpointUri();

    String getFromRouteId();

    Boolean getHasException();

    String getHeaders();

    Boolean getIsFailed();

    Boolean getIsRedelivered();

    Boolean getIsRedeliveryExhausted();

    String getNode();

    String getOriginatingUri();

    void setOriginatingUri(String originatingUri);

    String getProperties();

    Integer getRedeliveryAttempt();

    Integer getRedeliveryCounter();

    String getStackTrace();

    String getToEndpoint();


}
