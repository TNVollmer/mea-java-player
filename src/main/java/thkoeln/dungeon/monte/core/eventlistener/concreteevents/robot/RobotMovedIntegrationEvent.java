package thkoeln.dungeon.monte.core.eventlistener.concreteevents.robot;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.dungeon.monte.core.eventlistener.AbstractEvent;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class RobotMovedIntegrationEvent extends AbstractEvent {
    private UUID robotId;
    private Integer remainingEnergy;
    private RobotMovePlanetDto fromPlanet;
    private RobotMovePlanetDto toPlanet;

    @Override
    public boolean isValid() {
        if ( eventHeader == null ) return false;
        if ( remainingEnergy == null ) return false;
        if ( fromPlanet == null ) return false;
        if ( toPlanet == null ) return false;
        return ( fromPlanet.isValid() && toPlanet.isValid() );
    }
}