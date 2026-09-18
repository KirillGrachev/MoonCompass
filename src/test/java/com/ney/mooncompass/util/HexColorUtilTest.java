package com.ney.mooncompass.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HexColorUtilTest {

    @Test
    void nullAndEmptyBecomeEmptyString() {

        assertEquals("", HexColorUtil.color(null));
        assertEquals("", HexColorUtil.color(""));

    }

    @Test
    void legacyCodesAreTranslated() {
        assertEquals("§aТекст", HexColorUtil.color("&aТекст"));
    }

    @Test
    void hexCodesAreExpandedToBukkitFormat() {
        assertEquals("§x§f§f§0§0§0§0x", HexColorUtil.color("#ff0000x"));
    }

    @Test
    void invalidHexCodesStayUntouched() {

        String invalid = "#gggggg text";
        assertTrue(HexColorUtil.color(invalid).contains("#gggggg text"));

    }

    @Test
    void plainTextStaysUntouched() {
        assertEquals("обычный текст", HexColorUtil.color("обычный текст"));
    }
}
