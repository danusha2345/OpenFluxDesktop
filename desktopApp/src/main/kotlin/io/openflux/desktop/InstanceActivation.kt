package io.openflux.desktop

import java.io.File
import java.util.UUID

/** A per-user request to reveal the existing window; no ports or profile data. */
internal class InstanceActivation(directory: File) {
    private val request = File(directory, "show-window.request")
    private var lastSeen: String? = null

    fun requestShow(): Boolean = runCatching {
        request.parentFile.mkdirs()
        request.writeText(UUID.randomUUID().toString())
        true
    }.getOrDefault(false)

    fun consumeShowRequest(): Boolean {
        val value = runCatching { request.readText() }.getOrNull()?.takeIf { it.isNotBlank() } ?: return false
        if (value == lastSeen) return false
        lastSeen = value
        return true
    }
}

internal fun hideOnClose(closeToTray: Boolean, traySupported: Boolean): Boolean = closeToTray && traySupported
