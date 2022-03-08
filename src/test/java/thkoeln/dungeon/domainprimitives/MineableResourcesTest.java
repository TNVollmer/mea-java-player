package thkoeln.dungeon.domainprimitives;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MineableResourcesTest {
    private MineableResource gold12_1, gold12_2, platin12;

    @BeforeEach
    public void setUp() {
        gold12_1 = MineableResource.fromTypeAndAmount( MineableResourceType.GOLD, 12 );
        gold12_2 = MineableResource.fromTypeAndAmount( MineableResourceType.GOLD, 12 );
        platin12 = MineableResource.fromTypeAndAmount( MineableResourceType.PLATIN, 12 );
    }

    @Test
    public void testTwoMonetenEqualAndUnequal() {
        assertEquals( gold12_1, gold12_2 );
        assertNotEquals( gold12_2, platin12 );
    }

    @Test
    public void testValidation() {
        assertThrows( MineableResourceException.class, () -> {
            MineableResource.fromTypeAndAmount( null, 12 );
        });
        assertThrows( MineableResourceException.class, () -> {
            MineableResource.fromTypeAndAmount( MineableResourceType.GOLD, 0 );
        });
        assertThrows( MineableResourceException.class, () -> {
            MineableResource.fromTypeAndAmount( MineableResourceType.GOLD, -1 );
        });
    }

}