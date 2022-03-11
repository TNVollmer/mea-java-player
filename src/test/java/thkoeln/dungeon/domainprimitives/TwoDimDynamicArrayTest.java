package thkoeln.dungeon.domainprimitives;

import org.junit.Test;
import thkoeln.dungeon.domainprimitives.*;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TwoDimDynamicArrayTest {

    @Test
    public void testCreationValidation() {
        assertThrows( TwoDimDynamicArrayException.class, () -> {
            TwoDimDynamicArray<String> arr = new TwoDimDynamicArray( -1, 5 );
        });
        assertThrows( TwoDimDynamicArrayException.class, () -> {
            TwoDimDynamicArray<String> arr = new TwoDimDynamicArray( 3, -12 );
        });
        assertThrows( TwoDimDynamicArrayException.class, () -> {
            TwoDimDynamicArray<String> arr = new TwoDimDynamicArray( 0, 5 );
        });
        assertThrows( TwoDimDynamicArrayException.class, () -> {
            TwoDimDynamicArray<String> arr = new TwoDimDynamicArray( 3, 0 );
        });
    }

    @Test
    public void testCreation() {
        // given
        // when
        assertThrows( TwoDimDynamicArrayException.class, () -> {
            TwoDimDynamicArray<String> arr = new TwoDimDynamicArray( -1, 5 );
        });
        assertThrows( TwoDimDynamicArrayException.class, () -> {
            TwoDimDynamicArray<String> arr = new TwoDimDynamicArray( 3, -1 );
        });
        assertThrows( TwoDimDynamicArrayException.class, () -> {
            TwoDimDynamicArray<String> arr = new TwoDimDynamicArray( 0, 0 );
        });
        TwoDimDynamicArray<String> arr = new TwoDimDynamicArray( 3, 5 );
        TwoDimDynamicArray<String> arr1 = new TwoDimDynamicArray( 1, 1 );

        // then
        assertEquals( 3, arr.sizeX() );
        assertEquals( 5, arr.sizeY() );
        assertEquals( Coordinate.fromInteger( 2, 4 ), arr.getMaxCoordinate() );
        assertEquals( 1, arr1.sizeX() );
        assertEquals( 1, arr1.sizeY() );
        assertEquals( Coordinate.fromInteger( 0, 0 ), arr1.getMaxCoordinate() );
    }

    @Test
    public void testPutGet() {
        // given
        TwoDimDynamicArray<String> arr = new TwoDimDynamicArray( 3, 5 );

        // when
        assertThrows( TwoDimDynamicArrayException.class, () -> {
            arr.put( null, "hallo" );
        });
        assertThrows( TwoDimDynamicArrayException.class, () -> {
            arr.put( Coordinate.fromInteger( 2, 6 ), "hallo" );
        });
        assertThrows( TwoDimDynamicArrayException.class, () -> {
            arr.put( Coordinate.fromInteger( 3, 5 ), "hallo" );
        });
        assertThrows( TwoDimDynamicArrayException.class, () -> {
            arr.put( Coordinate.fromInteger( 3, 4 ), "hallo" );
        });
        arr.put( Coordinate.fromInteger( 1, 2 ), "yeah" );

        // then
        assertThrows( TwoDimDynamicArrayException.class, () -> {
            arr.get( null );
        });
        assertThrows( TwoDimDynamicArrayException.class, () -> {
            arr.get( Coordinate.fromInteger( 2, 6 ) );
        });
        assertThrows( TwoDimDynamicArrayException.class, () -> {
            arr.get( Coordinate.fromInteger( 3, 5 ) );
        });
        assertThrows( TwoDimDynamicArrayException.class, () -> {
            arr.get( Coordinate.fromInteger( 3, 4 ) );
        });
        assertEquals( "yeah", arr.get( Coordinate.fromInteger( 1, 2 ) ) );
        assertNull( arr.get( Coordinate.fromInteger( 1, 1 ) ) );
        assertNull( arr.get( Coordinate.fromInteger( 0, 2 ) ) );
    }

    @Test
    public void testAddColOrRow() {
        // given
        TwoDimDynamicArray<String> arr = new TwoDimDynamicArray(3, 3);
        arr.put( Coordinate.fromInteger( 0, 1 ), "Penny");
        arr.put( Coordinate.fromInteger( 1, 1 ), "Sheldon");
        arr.put( Coordinate.fromInteger( 2, 1 ), "Cooper");
        arr.put( Coordinate.fromInteger( 1, 2 ), "Leonard");
        for (int y = 0; y < arr.sizeY(); y++) {
            for (int x = 0; x < arr.sizeX(); x++) {
                System.out.print( arr.get( Coordinate.fromInteger( x, y ) ) + " | ");
            }
            System.out.println( "" );
        }
        System.out.println( "--------------------------" );
        // |       |         |        |
        // | Penny | Sheldon | Cooper |
        // |       | Leonard |        |

        // when
        assertThrows(TwoDimDynamicArrayException.class, () -> {
            arr.addRowAt(-1);
        });
        assertThrows(TwoDimDynamicArrayException.class, () -> {
            arr.addRowAt(5);
        });
        assertThrows(TwoDimDynamicArrayException.class, () -> {
            arr.addColumnAt(-1);
        });
        assertThrows(TwoDimDynamicArrayException.class, () -> {
            arr.addColumnAt(4);
        });
        arr.addRowAt(2);
        arr.addColumnAt(1);
        arr.addRowAt(4);
        // |       |   |          |        |
        // | Penny |   |  Sheldon | Cooper |
        // |       |   |          |        |
        // |       |   |  Leonard |        |
        // |       |   |          |        |

        // then
        for (int y = 0; y < arr.sizeY(); y++) {
            for (int x = 0; x < arr.sizeX(); x++) {
                System.out.print( arr.get( Coordinate.fromInteger( x, y ) ) + " | " );
            }
            System.out.println( "" );
        }
        assertEquals( "Penny", arr.get( Coordinate.fromInteger(0, 1 ) ) );
        assertNull( arr.get( Coordinate.fromInteger(1, 1) ) );
        assertNull( arr.get( Coordinate.fromInteger(1, 2) ) );
        assertEquals( "Sheldon", arr.get( Coordinate.fromInteger(2, 1 ) ) );
        assertEquals( "Cooper", arr.get( Coordinate.fromInteger(3, 1 ) ) );
        assertEquals( "Leonard", arr.get( Coordinate.fromInteger(2, 3 ) ) );
        assertNull( arr.get( Coordinate.fromInteger(2, 4) ) );
    }


    @Test
    public void testEnhanceIfNeeded_maxPoint() {
        // given
        TwoDimDynamicArray<String> arrNorth = new TwoDimDynamicArray(2, 3);
        TwoDimDynamicArray<String> arrEast = new TwoDimDynamicArray(2, 3);
        TwoDimDynamicArray<String> arrSouth = new TwoDimDynamicArray(2, 3);
        TwoDimDynamicArray<String> arrWest = new TwoDimDynamicArray(2, 3);

        // when
        Coordinate c12 = Coordinate.fromInteger(1, 2);
        arrNorth.enhanceIfNeededAt( c12, CompassDirection.NORTH );
        arrEast.enhanceIfNeededAt( c12, CompassDirection.EAST );
        arrSouth.enhanceIfNeededAt( c12, CompassDirection.SOUTH );
        arrWest.enhanceIfNeededAt( c12, CompassDirection.WEST );

        // then
        assertEquals(2, arrNorth.sizeX());
        assertEquals(4, arrNorth.sizeY());
        assertEquals(3, arrEast.sizeX());
        assertEquals(3, arrEast.sizeY());
        assertEquals(2, arrSouth.sizeX());
        assertEquals(3, arrSouth.sizeY());
        assertEquals(2, arrWest.sizeX());
        assertEquals(3, arrWest.sizeY());
    }

    @Test
    public void testEnhanceIfNeeded_00() {
        // given
        TwoDimDynamicArray<String> arrNorth = new TwoDimDynamicArray(2, 3);
        TwoDimDynamicArray<String> arrEast = new TwoDimDynamicArray(2, 3);
        TwoDimDynamicArray<String> arrSouth = new TwoDimDynamicArray(2, 3);
        TwoDimDynamicArray<String> arrWest = new TwoDimDynamicArray(2, 3);

        // when
        Coordinate c00 = Coordinate.fromInteger(0, 0 );
        arrNorth.enhanceIfNeededAt( c00, CompassDirection.NORTH );
        arrEast.enhanceIfNeededAt( c00, CompassDirection.EAST );
        arrSouth.enhanceIfNeededAt( c00, CompassDirection.SOUTH );
        arrWest.enhanceIfNeededAt( c00, CompassDirection.WEST );

        // then
        assertEquals( 2, arrNorth.sizeX() );
        assertEquals( 4, arrNorth.sizeY() );
        assertEquals( 2, arrEast.sizeX() );
        assertEquals( 3, arrEast.sizeY() );
        assertEquals( 2, arrSouth.sizeX() );
        assertEquals( 3, arrSouth.sizeY() );
        assertEquals( 3, arrWest.sizeX() );
        assertEquals( 3, arrWest.sizeY() );
    }
}
