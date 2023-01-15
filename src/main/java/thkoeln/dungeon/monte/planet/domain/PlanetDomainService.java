package thkoeln.dungeon.monte.planet.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.monte.core.domainprimitives.location.CompassDirection;
import thkoeln.dungeon.monte.core.domainprimitives.status.Energy;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * This service primarily aims at making sure that new planets our player learns about are properly connected to
 * each other, i.e. that a real interconnected map is created.
 * Please make sure not to use the PlanetRepository directly, but use the methods of this service instead.
 */
@Service
public class PlanetDomainService {
    private Logger logger = LoggerFactory.getLogger( PlanetDomainService.class );
    private PlanetRepository planetRepository;

    @Autowired
    public PlanetDomainService( PlanetRepository planetRepository ) {
        this.planetRepository = planetRepository;
    }


    /**
     * Add a new planet (may be space station) we learn about from an external event,
     * without having any information about its neighbours. That could be e.g. when
     * new space stations are declared.
     * @param newPlanetId
     */
    public void addPlanetWithoutNeighbours( UUID newPlanetId, boolean isSpaceStation ) {
        Planet newPlanet = null;
        List<Planet> foundPlanets = planetRepository.findAll();
        if( foundPlanets.isEmpty() ) {
            // no planets yet. Assign (0,0) to this first one.
            newPlanet = new Planet( newPlanetId );
        }
        else {
            Optional<Planet> foundOptional = planetRepository.findByPlanetId( newPlanetId );
            if( foundOptional.isPresent() ) {
                // not sure if this can happen ... but just to make sure, all the same.
                newPlanet = foundOptional.get();
            }
            else {
                newPlanet = new Planet( newPlanetId );
            }
        }
        newPlanet.setSpawnPoint( isSpaceStation );
        planetRepository.save( newPlanet );
    }


    public void visitPlanet( UUID planetId, Integer movementDifficulty ) {
        logger.info( "Visit planet " + planetId + " with movement difficulty " + movementDifficulty );
        Planet planet = planetRepository.findByPlanetId( planetId )
                .orElseThrow( () -> new PlanetException( "Planet with UUID " + planetId + " not found!" ) );
        planet.setVisited( true );
        planet.setMovementDifficulty( Energy.from( movementDifficulty ) );
        planetRepository.save( planet );
    }


    public void addNeighbourToPlanet( UUID planetId, UUID neighbourId, CompassDirection direction, Integer movementDifficulty ) {
        logger.info( "Add neighbour " + neighbourId + " with movement difficulty " + movementDifficulty
                + " in " + direction + " to planet " + planetId );
        Planet planet = planetRepository.findByPlanetId( planetId )
                .orElseThrow( () -> new PlanetException( "Planet with UUID " + planetId + " not found!" ) );
        Optional<Planet> neighbourOpt = planetRepository.findByPlanetId( neighbourId );
        Planet neighbour = neighbourOpt.isPresent() ? neighbourOpt.get() : new Planet( neighbourId );
        neighbour.setMovementDifficulty( Energy.from( movementDifficulty ) );
        planet.defineNeighbour( neighbour, direction );
        planetRepository.save( planet );
        planetRepository.save( neighbour );
    }

    public void saveAll() {
        List<Planet> allPlanets = planetRepository.findAll();
        for ( Planet planet: allPlanets ) planetRepository.save( planet );
    }
}
