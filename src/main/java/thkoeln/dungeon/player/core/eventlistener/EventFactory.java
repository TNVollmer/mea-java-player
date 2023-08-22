package thkoeln.dungeon.player.core.eventlistener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.player.core.eventlistener.concreteevents.game.GameStatusEvent;
import thkoeln.dungeon.player.core.eventlistener.concreteevents.game.RoundStatusEvent;
import thkoeln.dungeon.player.core.eventlistener.concreteevents.planet.PlanetDiscoveredEvent;
import thkoeln.dungeon.player.core.eventlistener.concreteevents.robot.mine.RobotResourceMinedEvent;
import thkoeln.dungeon.player.core.eventlistener.concreteevents.robot.move.RobotMovedEvent;
import thkoeln.dungeon.player.core.eventlistener.concreteevents.robot.RobotRegeneratedEvent;
import thkoeln.dungeon.player.core.eventlistener.concreteevents.robot.reveal.RobotsRevealedEvent;
import thkoeln.dungeon.player.core.eventlistener.concreteevents.robot.spawn.RobotSpawnedEvent;
import thkoeln.dungeon.player.core.eventlistener.concreteevents.trading.BankAccountTransactionBookedEvent;
import thkoeln.dungeon.player.core.eventlistener.concreteevents.trading.BankInitializedEvent;
import thkoeln.dungeon.player.core.eventlistener.concreteevents.trading.TradeablePricesEvent;
import thkoeln.dungeon.player.core.eventlistener.concreteevents.ErrorEvent;
import thkoeln.dungeon.player.core.eventlistener.concreteevents.UnknownEvent;

@Service
public class EventFactory {
    private Logger logger = LoggerFactory.getLogger( EventFactory.class );

    public AbstractEvent fromHeaderAndPayload( EventHeader eventHeader, String payload ) {
        if ( eventHeader == null || payload == null )
            throw new DungeonEventException( "eventHeader == null || payload == null" );
        AbstractEvent newEvent = null;
        switch ( eventHeader.getEventType() ) {
            case GAME_STATUS:
                newEvent = new GameStatusEvent();
                break;
            case BANK_INITIALIZED:
                newEvent = new BankInitializedEvent();
                break;
            case BANK_ACCOUNT_TRANSACTION_BOOKED:
                newEvent = new BankAccountTransactionBookedEvent();
                break;
            case ROUND_STATUS:
                newEvent = new RoundStatusEvent();
                break;
            case TRADABLE_PRICES:
                newEvent = new TradeablePricesEvent();
                break;
            case ROBOT_SPAWNED:
                newEvent = new RobotSpawnedEvent();
                break;
            case ROBOT_MOVED:
                newEvent = new RobotMovedEvent();
                break;
            case ROBOT_REGENERATED:
                newEvent = new RobotRegeneratedEvent();
                break;
            case ROBOT_REVEALED:
                newEvent = new RobotsRevealedEvent();
                break;
            case ROBOT_RESOURCE_MINED:
                newEvent = new RobotResourceMinedEvent();
                break;
            case PLANET_DISCOVERED:
                newEvent = new PlanetDiscoveredEvent();
                break;
            case ERROR:
                newEvent = new ErrorEvent();
                break;
            default:
                newEvent = new UnknownEvent();
        }
        newEvent.setEventHeader( eventHeader );
        newEvent.fillWithPayload( payload );
        logger.debug( "Created event: " + newEvent );
        return newEvent;
    }

}