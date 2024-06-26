package thkoeln.dungeon.player.game.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import thkoeln.dungeon.player.core.events.concreteevents.game.GameStatusEvent;
import thkoeln.dungeon.player.core.events.concreteevents.game.RoundStatusEvent;
import thkoeln.dungeon.player.core.events.concreteevents.game.RoundStatusType;
import thkoeln.dungeon.player.game.domain.GameStatus;
import thkoeln.dungeon.player.robot.domain.RobotRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class GameEventListener {
    private final GameApplicationService gameApplicationService;
    private final RobotRepository robotRepository;

    @EventListener(GameStatusEvent.class)
    void handleGameStatusEvent( GameStatusEvent gameStatusEvent ) {
        if ( GameStatus.STARTED.equals( gameStatusEvent.getStatus() ) ) {
            gameApplicationService.startGame( gameStatusEvent.getGameId() );
        } else if (GameStatus.ENDED.equals(gameStatusEvent.getStatus())) {
            gameApplicationService.endGame(gameStatusEvent.getGameId());
        }
    }

    @EventListener(RoundStatusEvent.class)
    void handleRoundStatusEvent( RoundStatusEvent event ) {
        if ( event.getRoundStatus() == RoundStatusType.STARTED ) {
            gameApplicationService.roundStarted( event.getRoundNumber() );
        }
    }
}
