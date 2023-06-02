package dev.schlaubi.tonbrett.cli.components

import androidx.compose.runtime.*
import com.jakewharton.mosaic.ui.Color
import com.jakewharton.mosaic.ui.Text
import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

private val chars = listOf('⠁', '⠂', '⠄', '⡀', '⢀', '⠠', '⠐', '⠈')

/**
 * Animated loading spinner.
 *
 * @param delay the [Duration] between animation steps
 * @param color the [Color] of the spinner
 */
@Composable
fun Spinner(delay: Duration = 100.milliseconds, color: Color = Color.BrightBlue) {
    var currentChar by remember { mutableStateOf(0) }

    LaunchedEffect(currentChar) {
        delay(delay)
        currentChar = (currentChar + 1) % chars.size
    }

    Text(chars[currentChar].toString(), color)
}
