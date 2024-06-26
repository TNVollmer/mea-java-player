package thkoeln.dungeon.player.core.events;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@Embeddable
@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventHeader {
    public static final String EVENT_ID_KEY = "eventId";
    public static final String TRANSACTION_ID_KEY = "transactionId";
    public static final String PLAYER_ID_KEY = "playerId";

    // Player can be the string "public" instead of a real player ID; this means that the event goes to
    // all players (a broadcast), instead of just to one specific player.
    public static final String BROADCAST_EVENT_KEY = "public";
    public static final String TYPE_KEY = "type";
    public static final String VERSION_KEY = "version";
    public static final String TIMESTAMP_KEY = "timestamp";

    @Transient
    private Logger logger = LoggerFactory.getLogger( EventHeader.class );

    private UUID eventId = null;
    private UUID transactionId = null;
    private UUID playerId = null;
    private boolean broadcast;
    private EventType eventType = EventType.UNKNOWN;
    private String eventTypeString = null;
    private String version = null;
    private String timestampString = null;


    public EventHeader(
            String type, String eventIdStr, String playerIdStr, String transactionIdStr,
            String timestampStr, String version ) {
        try {
            if ( eventIdStr != null ) setEventId( UUID.fromString( eventIdStr ) );
            if ( transactionIdStr != null && !"null".equals( transactionIdStr ) )
                setTransactionId( UUID.fromString( transactionIdStr ) );
            if ( BROADCAST_EVENT_KEY.equals( playerIdStr ) ) {
                setBroadcast( true );
            } else {
                setBroadcast( false );
                if ( playerIdStr != null && !"null".equals( transactionIdStr ) ) setPlayerId( UUID.fromString( playerIdStr ) );
            }
        } catch (IllegalArgumentException e) {
            logger.warn( "Unexpected problem with converting UUIDs in event header: " +
                    eventIdStr + ", " + transactionIdStr, ", " + playerIdStr );
        }
        setTimestampString( timestampStr );
        setVersion( version );
        if ( type != null ) {
            setEventType( EventType.findByStringValue( type ) );
        } else {
            setEventType( EventType.UNKNOWN );
        }
        setEventTypeString( type );
        logger.debug( "Created event " + this );
    }


    @Override
    public String toString() {
        String printString =
                "HEADER: <eventType=" + eventType +
                        ", eventTypeString=" + eventTypeString +
                        ", transactionId=" + transactionId +
                        ", eventId=" + eventId +
                        ", playerId=" + playerId +
                        ", isBroadcast=" + isBroadcast() +
                        ", version=" + version +
                        ", timestampString=" + timestampString + ">";
        // wrap lines after 150 chars
        printString = printString.replaceAll( "(.{150})", "$1\n\t" );
        return printString;
    }


}
