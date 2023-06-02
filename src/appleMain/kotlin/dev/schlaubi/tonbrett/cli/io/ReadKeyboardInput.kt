package dev.schlaubi.tonbrett.cli.io

import kotlinx.cinterop.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import platform.posix.*

private const val firstArrowEscape = 27
private const val secondArrowEscape = 91
private const val arrowUp = 65
private const val arrowDown = 66
private const val arrowLeft = 68
private const val arrowRight = 67

@Suppress("SuspiciousCollectionReassignment")
actual val keyEvents: Flow<Key> = flow {
    var buffer = emptyList<Int>()
    while (true) {
        val currentChar = readByteFromStdin()
        if (currentChar == firstArrowEscape && buffer.isEmpty()) {
            buffer = listOf(currentChar)
            continue
        }
        if(currentChar == secondArrowEscape && buffer.firstOrNull() == firstArrowEscape ) {
            buffer += currentChar
            continue
        }

        if (buffer == listOf(firstArrowEscape, secondArrowEscape)) {
            val key = when(currentChar) {
                arrowUp -> Keys.DirectionUp
                arrowDown -> Keys.DirectionDown
                arrowRight -> Keys.DirectionRight
                arrowLeft -> Keys.DirectionLeft
                else -> error("Unknown arrow type: $currentChar")
            }
            emit(key)
        } else {
            buffer.forEach {
                emit(Key(it, it.toChar()))
                emit(Key(currentChar, currentChar.toChar()))
            }
        }
        buffer = emptyList()
    }
}

private fun readByteFromStdin(): Int = memScoped {
    val term = alloc<termios>()
    tcgetattr(STDIN_FILENO, term.ptr)
    val termCopy = term
    term.c_lflag = termCopy.c_lflag and ECHO.inv().toULong() and ICANON.inv().toULong()
    tcsetattr(STDIN_FILENO, TCSANOW, term.ptr)

    val bytePointer = alloc<ByteVar>()
    val bytesRead = read(STDIN_FILENO, bytePointer.ptr, 1.convert())

    tcsetattr(STDIN_FILENO, TCSANOW, term.ptr)

    if (bytesRead == 1L) {
        bytePointer.value.toInt()
    } else {
        error("Could not read key")
    }
}
