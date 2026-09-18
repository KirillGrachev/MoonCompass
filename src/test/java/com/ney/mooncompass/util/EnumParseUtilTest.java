package com.ney.mooncompass.util;

import com.ney.mooncompass.config.type.CompassHand;
import com.ney.mooncompass.config.type.DisplayMode;
import org.bukkit.Material;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EnumParseUtilTest {

    @Test
    void parseIgnoresCaseAndSpaces() {
        assertEquals(DisplayMode.ACTION_BAR, EnumParseUtil.parse(" action_bar ", DisplayMode.class, DisplayMode.CHAT));
        assertEquals(CompassHand.OFF_HAND, EnumParseUtil.parse("Off_Hand", CompassHand.class, CompassHand.BOTH));
    }

    @Test
    void parseFallsBackOnInvalidOrNull() {

        assertEquals(DisplayMode.CHAT, EnumParseUtil.parse("teleport", DisplayMode.class, DisplayMode.CHAT));
        assertEquals(DisplayMode.CHAT, EnumParseUtil.parse(null, DisplayMode.class, DisplayMode.CHAT));
        assertEquals(CompassHand.BOTH, EnumParseUtil.parse("", CompassHand.class, CompassHand.BOTH));

    }

    @Test
    void parseMaterialResolvesAndFallsBack() {

        assertEquals(Material.COMPASS, EnumParseUtil.parseMaterial("compass", Material.COMPASS));
        assertEquals(Material.CLOCK, EnumParseUtil.parseMaterial("not_a_material", Material.CLOCK));
        assertEquals(Material.CLOCK, EnumParseUtil.parseMaterial(null, Material.CLOCK));

    }
}
