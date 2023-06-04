package dev.schlaubi.tonbrett.cli.io

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.rememberCoroutineScope
import dev.schlaubi.tonbrett.cli.components.Content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.plus

val LocalKeyEvents = compositionLocalOf<Flow<Key>> { throw UnsupportedOperationException("No default") }

@Composable
fun ProvideKeyEvents(content: Content) {
    val coroutineScope = rememberCoroutineScope()
    CompositionLocalProvider(
        LocalKeyEvents provides keyEvents.shareIn(coroutineScope + Dispatchers.IO, SharingStarted.Lazily),
        content = content
    )
}

expect object Keys {
    val Enter: Key
    val Tab: Key
    val Space: Key
    val Backspace: Key
    val DirectionLeft: Key
    val DirectionUp: Key
    val DirectionRight: Key
    val DirectionDown: Key
}

data class Key(val virtualId: Int?, val asciiChar: Char?) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Key) return false

        return virtualId == other.virtualId
    }

    override fun hashCode(): Int {
        return virtualId?.hashCode() ?: 0
    }
}

expect val keyEvents: Flow<Key>
