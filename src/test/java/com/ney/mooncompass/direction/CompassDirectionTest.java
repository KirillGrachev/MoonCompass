package com.ney.mooncompass.direction;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CompassDirectionTest {

    @ParameterizedTest
    @CsvSource({
            "0, NORTH",
            "22, NORTH",
            "23, NORTH_EAST",
            "45, NORTH_EAST",
            "90, EAST",
            "135, SOUTH_EAST",
            "180, SOUTH",
            "225, SOUTH_WEST",
            "270, WEST",
            "315, NORTH_WEST",
            "359, NORTH",
            "-45, NORTH_WEST",
            "-90, WEST",
            "720, NORTH"
    })
    void fromYawResolvesSector(float yaw, CompassDirection expected) {
        assertEquals(expected, CompassDirection.fromYaw(yaw));
    }

    @Test
    void normalizeKeepsAngleInRange() {

        assertEquals(350.0, CompassDirection.normalize(-10));
        assertEquals(0.0, CompassDirection.normalize(360));
        assertEquals(90.0, CompassDirection.normalize(450));

    }

    @Test
    void configKeyIsLowerSnakeCase() {

        assertEquals("north", CompassDirection.NORTH.getConfigKey());
        assertEquals("north_east", CompassDirection.NORTH_EAST.getConfigKey());
        assertEquals("south_west", CompassDirection.SOUTH_WEST.getConfigKey());

    }

    @Test
    void fromConfigKeyResolvesEveryConstant() {

        for (CompassDirection direction : CompassDirection.values()) {
            assertEquals(direction, CompassDirection.fromConfigKey(direction.getConfigKey()));
        }

    }

    @Test
    void fromConfigKeyIgnoresCaseAndUnknownKeys() {

        assertEquals(CompassDirection.NORTH_EAST, CompassDirection.fromConfigKey("North_East"));
        assertNull(CompassDirection.fromConfigKey("up"));
        assertNull(CompassDirection.fromConfigKey(""));

    }
}
