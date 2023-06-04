package dev.schlaubi.tonbrett.cli.io

import kotlinx.cinterop.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import platform.posix.*

private const val firstArrowEscape = 27
private const val secondArrowEscape = 91

actual object Keys {
    actual val Enter = Key(13, null)
    actual val Tab = Key(9, null)
    actual val Space = Key(32, null)
    actual val Backspace = Key(127, null)
    actual val DirectionUp = Key(65, null)
    actual val DirectionDown = Key(66, null)
    actual val DirectionRight = Key(67, null)
    actual val DirectionLeft = Key(68, null)
}

@Suppress("SuspiciousCollectionReassignment")
actual val keyEvents: Flow<Key> = flow {
    var buffer = emptyList<Int>()
    while (true) {
        val currentChar = readByteFromStdin()
        if (currentChar == firstArrowEscape && buffer.isEmpty()) {
            buffer = listOf(currentChar)
            continue
        } else if (currentChar == secondArrowEscape && buffer.firstOrNull() == firstArrowEscape) {
            buffer += currentChar
            continue
        } else {
            emit(Key(currentChar, currentChar.toChar()))
        }
        if (buffer == listOf(firstArrowEscape, secondArrowEscape)) {
            emit(Key(currentChar, null))
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
