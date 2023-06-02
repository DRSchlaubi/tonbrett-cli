package dev.schlaubi.tonbrett.cli.io

import kotlinx.cinterop.*
import platform.windows.*


actual fun readByteFromStdin(): Key = memScoped {
    val handle = GetStdHandle(STD_INPUT_HANDLE)
    val modePtr = alloc<DWORDVar>()
    if (GetConsoleMode(handle, modePtr.ptr) == 0) {
        error("Failed to get console mode.")
    }

    val previousMode = modePtr.value.toInt()
    val newMode =
        previousMode and ENABLE_ECHO_INPUT.inv() and ENABLE_LINE_INPUT.inv() and ENABLE_PROCESSED_INPUT.inv() and ENABLE_MOUSE_INPUT.inv()
    if (SetConsoleMode(handle, newMode.toUInt()) == 0) {
        error("Failed to set console mode.")
    }

    val inputRecord = alloc<INPUT_RECORD>()
    val eventsReadPtr = alloc<DWORDVar>()

    while (true) {
        if (ReadConsoleInputW(
                handle,
                inputRecord.ptr,
                1.convert(),
                eventsReadPtr.ptr
            ) == 0 || eventsReadPtr.value == 0U
        ) {
            continue
        }

        if (inputRecord.EventType.toInt() == KEY_EVENT) {
            val keyEvent = inputRecord.Event.KeyEvent
            val keyId = keyEvent.wVirtualKeyCode.toInt()
            val asciiChar = keyEvent.uChar.AsciiChar.toInt().takeIf { it > 0 }?.toChar()
            val key = Key(keyId, asciiChar)
            if (keyEvent.bKeyDown != 0) {
                continue
            } else {
                return@memScoped key
            }
        }
    }

    @Suppress("UNREACHABLE_CODE")
    error("This should never happen")
}

