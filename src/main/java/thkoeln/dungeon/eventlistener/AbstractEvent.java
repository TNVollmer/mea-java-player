package thkoeln.dungeon.eventlistener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor( access = AccessLevel.PROTECTED )
@ToString
public abstract class AbstractEvent {
    @Id
    @Setter( AccessLevel.NONE )
    protected UUID localId = UUID.randomUUID();

    @Transient
    protected Logger logger = LoggerFactory.getLogger( AbstractEvent.class );

    @Embedded
    protected EventHeader eventHeader;
    protected String messageBodyAsJson;

    @Getter ( AccessLevel.NONE ) // just because Lombok generates the ugly getProcessed()
    protected Boolean processed = Boolean.FALSE;
    public Boolean hasBeenProcessed() { return processed; }


    /**
     * @return true if the event was complete and consistent (enough) in order to be processed, false otherwise.
     * This is for the implementing concrete subclass to decide.
     */
    public abstract boolean isValid();

    public void fillWithPayload( String jsonString ) {
        try {
            ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
            objectMapper.readerForUpdating( this ).readValue( jsonString );
        }
        catch( JsonProcessingException conversionFailed ) {
            logger.error( "Error converting payload for event with jsonString " + jsonString );
        }
    }
}