package thkoeln.dungeon.monte.planet.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thkoeln.dungeon.monte.domainprimitives.*;

import javax.persistence.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static java.lang.Boolean.TRUE;
import static thkoeln.dungeon.monte.domainprimitives.CompassDirection.*;

@Entity
@Getter
@NoArgsConstructor
public class Planet {
    @Id
    private final UUID id = UUID.randomUUID();

    // this is the EXTERNAL id that we receive from MapService. We could use this also as our own id, but then
    // we'll run into problems in case MapService messes up their ids. So, better we better keep these two apart.
    private UUID planetId;

    @Getter ( AccessLevel.NONE ) // just because Lombok generates the ugly getSpacestation()
    private Boolean spacestation = Boolean.FALSE;


    @Getter ( AccessLevel.NONE ) // just because Lombok generates the ugly getVisited()
    @Setter
    private Boolean visited = Boolean.FALSE;


    // Flag needed for recursive output of all planets ... I know, this is not ideal, but couldn't yet
    // think of a better solution.
    @Setter
    private Boolean temporaryProcessingFlag;


    @OneToOne ( cascade = CascadeType.MERGE)
    @Setter ( AccessLevel.PROTECTED )
    private Planet northNeighbour = null;
    @OneToOne ( cascade = CascadeType.MERGE)
    @Setter ( AccessLevel.PROTECTED )
    private Planet eastNeighbour = null;
    @OneToOne ( cascade = CascadeType.MERGE)
    @Setter ( AccessLevel.PROTECTED )
    private Planet southNeighbour = null;
    @OneToOne ( cascade = CascadeType.MERGE)
    @Setter ( AccessLevel.PROTECTED )
    private Planet westNeighbour = null;

    @Embedded
    @Setter
    private MineableResource mineableResource;

    @Embedded
    @Setter
    private MovementDifficulty movementDifficulty;

    @Transient
    private Logger logger = LoggerFactory.getLogger( Planet.class );

    public Planet( UUID planetId ) {
        this.planetId = planetId;
    }



    /**
     * A neighbour relationship is always set on BOTH sides.
     * @param otherPlanet
     * @param direction
     */
    public void defineNeighbour( Planet otherPlanet, CompassDirection direction ) {
        if ( otherPlanet == null ) throw new PlanetException( "Cannot establish neighbouring relationship with null planet!" ) ;
        try {
            Method otherGetter = neighbouringGetter( direction.getOppositeDirection() );
            Method setter = neighbouringSetter( direction );
            setter.invoke(this, otherPlanet );
            Planet remoteNeighbour = (Planet) otherGetter.invoke( otherPlanet );
            if ( !this.equals( remoteNeighbour ) ) {
                Method otherSetter = neighbouringSetter( direction.getOppositeDirection() );
                otherSetter.invoke( otherPlanet, this );
            }
        }
        catch ( IllegalAccessException | InvocationTargetException | NoSuchMethodException e ) {
            throw new PlanetException( "Something went wrong that should not have happened ..." + e );
        }
        logger.info( "Established neighbouring relationship between planet '" + this + "' and '" + otherPlanet + "'." );
        closeNeighbouringCycleForAllDirectionsBut( direction );
    }


    public void closeNeighbouringCycleForAllDirectionsBut( CompassDirection notInThisDirection ) {
        for ( CompassDirection compassDirection: CompassDirection.values() ) {
            if ( compassDirection.equals( notInThisDirection ) ) continue;
            Planet neighbour = getNeighbour( compassDirection );
            if ( neighbour != null ) {
                for ( CompassDirection ninetyDegrees: compassDirection.ninetyDegrees() ) {
                    if( this.getNeighbour( ninetyDegrees ) != null &&
                            neighbour.getNeighbour( ninetyDegrees ) != null &&
                            this.getNeighbour( ninetyDegrees ).getNeighbour( compassDirection ) == null ) {
                        this.getNeighbour( ninetyDegrees ).defineNeighbour(
                                neighbour.getNeighbour( ninetyDegrees ), compassDirection );
                        logger.info( "Closed cycle ..." );
                    }
                }
            }
        }
    }


    public void resetAllNeighbours() {
        setNorthNeighbour( null );
        setWestNeighbour( null );
        setEastNeighbour( null );
        setSouthNeighbour( null );
    }


    public Planet getNeighbour( CompassDirection compassDirection ) {
        try {
            Method getter = neighbouringGetter( compassDirection );
            return (Planet) getter.invoke( this );
        }
        catch ( IllegalAccessException | InvocationTargetException | NoSuchMethodException e ) {
            throw new PlanetException( "Something went wrong that should not have happened ..." + e.getStackTrace() );
        }
    }

    protected Method neighbouringGetter(CompassDirection direction ) throws NoSuchMethodException {
        String name = "get" + WordUtils.capitalize( WordUtils.swapCase( String.valueOf( direction ) ) ) + "Neighbour";
        return this.getClass().getDeclaredMethod( name );
    }


    protected Method neighbouringSetter( CompassDirection direction ) throws NoSuchMethodException {
        String name = "set" + WordUtils.capitalize( WordUtils.swapCase( String.valueOf( direction ) ) ) + "Neighbour";
        return this.getClass().getDeclaredMethod( name, new Class[]{ this.getClass() } );
    }


    public Map<CompassDirection, Planet> allNeighbours() {
        Map<CompassDirection, Planet> allNeighboursMap = new HashMap<>();
        if ( getNorthNeighbour() != null ) allNeighboursMap.put( NORTH, getNorthNeighbour() );
        if ( getWestNeighbour() != null ) allNeighboursMap.put( WEST, getWestNeighbour() );
        if ( getEastNeighbour() != null ) allNeighboursMap.put( EAST, getEastNeighbour() );
        if ( getSouthNeighbour() != null ) allNeighboursMap.put( SOUTH, getSouthNeighbour() );
        return allNeighboursMap;
    }


    public boolean hasNeighbours() {
        return allNeighbours().size() > 0;
    }

    public Boolean isSpaceStation() { return spacestation; }

    public void setSpacestation( Boolean isSpaceStation ) {
        if ( isSpaceStation != null && isSpaceStation ) {
            spacestation = TRUE;
            visited = TRUE;
        }
    }

    public Boolean hasBeenVisited() { return visited; }


    /**
     * Add the neighbours to an existing 2d array of planets - grow the array if needed.
     * @param existingLocalIsland
     * @param localCoordinate - position where this planet is in the array
     * @return
     */
    public TwoDimDynamicArray<Planet> constructLocalCluster(
            TwoDimDynamicArray<Planet> existingLocalIsland, Coordinate localCoordinate ) {
        if ( getTemporaryProcessingFlag() ) return existingLocalIsland;

        setTemporaryProcessingFlag( TRUE );
        TwoDimDynamicArray<Planet> localIsland = existingLocalIsland;
        Map<CompassDirection, Planet> allNeighbours = allNeighbours();
        for (Map.Entry<CompassDirection, Planet> entry : allNeighbours.entrySet()) {
            CompassDirection direction = entry.getKey();
            Planet neighbour = entry.getValue();
            if ( !neighbour.getTemporaryProcessingFlag() ) {
                Coordinate newCoordinate = localIsland.putAndEnhance( localCoordinate, direction, neighbour );
                localIsland = neighbour.constructLocalCluster( localIsland, newCoordinate );
            }
        }
        return localIsland;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Planet)) return false;
        Planet planet = (Planet) o;
        return Objects.equals(id, planet.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


    @Override
    public String toString() {
        String whoAmI = isSpaceStation() ? "#" : "_";
        return whoAmI + String.valueOf( planetId ).substring( 0, 3 );
    }

    public String toStringDetailed() {
        String printString =  toString() + " (";
        List<String> attributeStrings = new ArrayList<>();
        if ( !hasBeenVisited() ) attributeStrings.add( "??" );
        if ( mineableResource != null ) attributeStrings.add( mineableResource.toString() );

        Map<CompassDirection, Planet> allNeighbours = allNeighbours();
        for ( CompassDirection direction : CompassDirection.values() ) {
            if ( allNeighbours.containsKey( direction) ) {
                attributeStrings.add( direction.toStringShort() + ": " + allNeighbours.get( direction ) );
            }
        }
        printString += String.join( ", ", attributeStrings.toArray( new String[attributeStrings.size()] ) ) + ")";
        return printString;
    }
}
