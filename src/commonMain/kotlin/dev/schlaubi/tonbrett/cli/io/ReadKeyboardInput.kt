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

object Keys {
    val Ctrl = Key(17, null)
    val Enter = Key(10, null)
    val Tab = Key(9, null)
    val Space = Key(32, null)
    val Backspace = Key(127, null)
    val DirectionLeft = Key(37, null)
    val DirectionUp = Key(38, null)
    val DirectionRight = Key(39, null)
    val DirectionDown = Key(40, null)
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
