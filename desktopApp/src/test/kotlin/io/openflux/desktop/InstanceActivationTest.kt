package io.openflux.desktop

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.io.path.createTempDirectory

class InstanceActivationTest {
    @Test fun closeWithoutTrayExitsEvenWhenCloseToTrayIsEnabled() {
        assertFalse(hideOnClose(true, false))
        assertFalse(hideOnClose(false, false))
        assertFalse(hideOnClose(false, true))
        assertTrue(hideOnClose(true, true))
    }

    @Test fun secondLaunchRequestSurvivesUntilWindowIsReady() {
        val directory = createTempDirectory("openflux-activation").toFile()
        try {
            val first = InstanceActivation(directory)
            val second = InstanceActivation(directory)
            assertFalse(first.consumeShowRequest())
            assertTrue(second.requestShow())
            assertTrue(first.consumeShowRequest())
            assertFalse(first.consumeShowRequest())
            assertTrue(second.requestShow())
            assertTrue(first.consumeShowRequest())
        } finally { directory.deleteRecursively() }
    }
}
