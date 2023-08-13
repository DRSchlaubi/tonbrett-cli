package dev.schlaubi.tonbrett.cli.commands

import androidx.compose.runtime.*
import com.jakewharton.mosaic.ui.Text
import com.varabyte.kotter.foundation.input.CharKey
import com.varabyte.kotter.foundation.input.IsoControlKey
import com.varabyte.kotter.foundation.input.Key
import com.varabyte.kotter.foundation.input.Keys
import dev.schlaubi.tonbrett.cli.command.MosaicCommand
import dev.schlaubi.tonbrett.cli.io.LocalKeyEvents

class TestInputsCommand : MosaicCommand(help = "Test command to test internal input api") {
    @Composable
    override fun Command() {
        var lastKey by remember { mutableStateOf<Key?>(null) }
        val keyEvents = LocalKeyEvents.current
        LaunchedEffect(Unit) {
            keyEvents.collect {
                lastKey = it
            }
        }

        Text("Last selected key: ${lastKey?.sanitize()}")
    }
}

private fun Key.sanitize() = if (this is IsoControlKey) {
    when(this) {
        Keys.ESC -> "ESC"
        Keys.ENTER -> "ENTER"
        Keys.BACKSPACE -> "BACKSPACE"
        Keys.DELETE -> "DELETE"
        Keys.EOF -> "EOF"
        Keys.UP -> "UP"
        Keys.DOWN -> "DOWN"
        Keys.LEFT -> "LEFT"
        Keys.RIGHT -> "RIGHT"
        Keys.HOME -> "HOME"
        Keys.END -> "END"
        Keys.INSERT -> "INSERT"
        Keys.PAGE_UP -> "PAGE_UP"
        Keys.PAGE_DOWN -> "PAGE_DOWN"
        Keys.TAB -> "TAB"
        else -> error("Unknown key: $this")
    }
} else (this as? CharKey)?.code.toString()
