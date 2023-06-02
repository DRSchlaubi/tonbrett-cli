package dev.schlaubi.tonbrett.cli.io

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

object Keys {
    val Ctrl = Key(17, null)
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

val keyEvents: Flow<Key> = flow {
    while (true) {
        emit(readByteFromStdin())
    }
}

expect fun readByteFromStdin(): Key
