package thkoeln.dungeon.player.core.events.concreteevents.planet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.dungeon.player.core.events.AbstractEvent;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlanetDiscoveredEvent extends AbstractEvent {
    @JsonProperty("planet")
    private UUID planetId;

    private Integer movementDifficulty;

    private PlanetNeighboursDto[] neighbours = new PlanetNeighboursDto[0];
    private PlanetResourceDto resource;

    @Override
    public boolean isValid() {
        if ( eventHeader == null ) return false;
        if ( planetId == null ) return false;
        if ( movementDifficulty == null ) return false;
        if ( movementDifficulty < 0 ) return false;
        if ( neighbours == null ) return false;
        for ( PlanetNeighboursDto neighbour : neighbours ) {
            if ( !neighbour.isValid() ) return false;
        }
        return resource == null || resource.isValid();
    }


}
