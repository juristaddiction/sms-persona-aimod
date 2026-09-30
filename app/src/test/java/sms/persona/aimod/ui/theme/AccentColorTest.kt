package sms.persona.aimod.ui.theme

import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AccentColorTest {

    @Test
    fun lightAccentReplacesPrimaryWithSeed() {
        val seed = 0xFF6D4AFF.toInt()
        val scheme = lightColorScheme().withAccent(seed, dark = false)
        assertEquals(Color(seed), scheme.primary)
    }

    @Test
    fun lightAccentKeepsSurfacesErrorAndBackground() {
        val base = lightColorScheme()
        val scheme = base.withAccent(0xFF146C2E.toInt(), dark = false)
        assertEquals(base.surface, scheme.surface)
        assertEquals(base.background, scheme.background)
        assertEquals(base.error, scheme.error)
        assertEquals(base.outline, scheme.outline)
    }

    @Test
    fun lightAccentDerivesContainersFromSeed() {
        val seed = 0xFF6D4AFF.toInt()
        val scheme = lightColorScheme().withAccent(seed, dark = false)
        assertNotEquals(lightColorScheme().primaryContainer, scheme.primaryContainer)
        // Container is the seed mixed toward white: every channel moves up.
        val seedColor = Color(seed)
        assertTrue(scheme.primaryContainer.red >= seedColor.red)
        assertTrue(scheme.primaryContainer.green >= seedColor.green)
        assertTrue(scheme.primaryContainer.blue >= seedColor.blue)
    }

    @Test
    fun darkAccentLightensPrimaryAwayFromSeed() {
        val seed = 0xFF0B57D0.toInt()
        val scheme = lightColorScheme().withAccent(seed, dark = true)
        assertNotEquals(Color(seed), scheme.primary)
        assertTrue(scheme.primary.red > Color(seed).red)
    }

    @Test
    fun darkAccentKeepsSurfacesAndError() {
        val base = lightColorScheme()
        val scheme = base.withAccent(0xFFB4005E.toInt(), dark = true)
        assertEquals(base.surface, scheme.surface)
        assertEquals(base.error, scheme.error)
    }

    @Test
    fun seedsAreDistinctAndNamed() {
        val argbs = AccentSeeds.map { it.second }
        assertEquals(argbs.size, argbs.toSet().size)
        assertTrue(AccentSeeds.all { it.first.isNotBlank() })
    }

    @Test
    fun defaultAccentMatchesFirstSeed() {
        assertEquals(AccentSeeds.first().second, DefaultAccent)
    }
}
