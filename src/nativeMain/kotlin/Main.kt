package dev.schlaubi.tonbrett.cli

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.jakewharton.mosaic.runMosaicBlocking
import com.jakewharton.mosaic.ui.Column
import com.jakewharton.mosaic.ui.Row
import com.jakewharton.mosaic.ui.Text
import dev.schlaubi.tonbrett.cli.components.Spacer
import dev.schlaubi.tonbrett.cli.components.Spinner
import dev.schlaubi.tonbrett.cli.io.Keys
import dev.schlaubi.tonbrett.cli.io.keyEvents
import dev.schlaubi.tonbrett.cli.io.pressedKeys
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.system.exitProcess
import kotlin.time.Duration.Companion.seconds

fun main() = runMosaicBlocking {
    var lastPressedKey by mutableStateOf<String?>(null)
    setContent {
        LaunchedEffect(Unit) {
            withContext(Dispatchers.IO) {
                keyEvents.collect {
                    if (it.asciiChar == '') {
                        exitProcess(0)
                    } else {
                        lastPressedKey = if (it.asciiChar == null) {
                            it.virtualId.toString()
                        } else {
                            it.asciiChar.toString()
                        }
                    }
                }
            }
        }

        Column {
            Row {
                Spinner()
                Spacer(2)
                Text("Laden ...")

            }
            if (lastPressedKey != null) {
                Text("Last pressedKey: $lastPressedKey")
            }
        }
    }

    delay(50000.seconds)
}
