package thkoeln.dungeon.player.core.events;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.Embedded;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractEvent {
    @Id
    @Setter(AccessLevel.NONE)
    protected UUID localId = UUID.randomUUID();

    @Transient
    protected Logger logger = LoggerFactory.getLogger(AbstractEvent.class);

    @Embedded
    protected EventHeader eventHeader;
    protected String messageBodyAsJson;

    @Getter(AccessLevel.NONE) // just because Lombok generates the ugly getProcessed()
    protected Boolean processed = Boolean.FALSE;

    public Boolean hasBeenProcessed() {
        return processed;
    }

    /**
     * @return true if the event was complete and consistent (enough) in order to be processed, false otherwise.
     * This is for the implementing concrete subclass to decide.
     */
    public abstract boolean isValid();

    public void fillWithPayload(String jsonString) {
        messageBodyAsJson = jsonString;
        try {
            ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
            objectMapper.configure( MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
            objectMapper.readerForUpdating(this).readValue(jsonString);
        } catch (JsonProcessingException conversionFailed) {
            logger.warn("Cannot convert payload for event with jsonString " + jsonString);
        }
    }


    public boolean isUnknown() {
        return (eventHeader == null || (eventHeader.getEventType() == EventType.UNKNOWN));
    }

    @Override
    public String toString() {
        String payloadString = "PAYLOAD: " + messageBodyAsJson;
        // wrap lines after 150 chars
        payloadString = payloadString.replaceAll("(.{150})", "$1\n\t");

        return this.getClass().getSimpleName() + " - " +
                eventHeader + "\n" + payloadString;
    }

    public String toStringShort() {
        if (isUnknown()) return toString();
        return eventHeader.getEventType().toString();
    }
}
