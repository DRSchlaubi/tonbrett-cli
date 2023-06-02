package dev.schlaubi.tonbrett.cli.io

import kotlinx.cinterop.*
import platform.posix.*

actual val pressedKeys = emptyList<Key>()

actual fun readByteFromStdin(): Key = memScoped {
    val term = alloc<termios>()
    tcgetattr(STDIN_FILENO, term.ptr)
    val termCopy = term
    term.c_lflag = termCopy.c_lflag and ECHO.inv().toULong() and ICANON.inv().toULong()
    tcsetattr(STDIN_FILENO, TCSANOW, term.ptr)

    val bytePointer = alloc<ByteVar>()
    val bytesRead = read(STDIN_FILENO, bytePointer.ptr, 1.convert())

    tcsetattr(STDIN_FILENO, TCSANOW, term.ptr)

    if (bytesRead == 1L) {
        val virtualId = bytePointer.value.toInt()
        Key(virtualId, null)
    } else {
        error("Could not read key")
    }
}
