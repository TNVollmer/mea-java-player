package thkoeln.dungeon.monte.core.eventlistener.concreteevents.robot.reveal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.dungeon.monte.core.eventlistener.AbstractEvent;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
public class RobotsRevealedIntegrationEvent extends AbstractEvent {
    private RobotRevealedDto[] robots;

    @Override
    public boolean isValid() {
        if ( robots == null ) return true;
        for ( RobotRevealedDto robotRevealedDto : robots ) {
            if ( !robotRevealedDto.isValid() ) return false;
        }
        return true;
    }


    public Set<String> playerShortNames() {
        HashSet<String> playerShortNames = new HashSet<>();
        if ( robots != null ) {
            for ( RobotRevealedDto robotRevealedDto : robots ) {
                playerShortNames.add( robotRevealedDto.getPlayerNotion() );
            }
        }
        return playerShortNames;
    }


    public void updateEnemyChar( String playerShortName, Character enemyChar ) {
        if ( robots != null ) {
            for ( RobotRevealedDto robotRevealedDto : robots ) {
                if ( playerShortName.equals( robotRevealedDto.getPlayerNotion() ) ) {
                    robotRevealedDto.setEnemyChar( enemyChar );
                }
            }
        }
    }


    public List<RobotRevealedDto> enemyRobotsRevealedDtos() {
        if ( robots == null ) return new ArrayList<>();
        return Arrays.stream( robots ).filter( robotRevealedDto -> (robotRevealedDto.getEnemyChar() != null) ).toList();
    }


    public List<RobotRevealedDto> ownRobotsRevealedDtos() {
        if ( robots == null ) return new ArrayList<>();
        return Arrays.stream( robots ).filter( robotRevealedDto -> (robotRevealedDto.getEnemyChar() == null) ).toList();
    }


}