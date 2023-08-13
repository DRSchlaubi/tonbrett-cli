package dev.schlaubi.tonbrett.cli.components

import androidx.compose.runtime.*
import com.jakewharton.mosaic.ui.Color
import com.jakewharton.mosaic.ui.Row
import com.jakewharton.mosaic.ui.Text
import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

private val chars = listOf('⠁', '⠂', '⠄', '⡀', '⢀', '⠠', '⠐', '⠈')
private const val checkMark = '✔'
private const val cross = '✘'
private const val information = 'ⓘ'

typealias Content = @Composable () -> Unit

/**
 * Composable displaying a status message.
 *
 * @param icon the Icon
 * @param content the status message
 */
@Composable
fun Status(icon: Content, content: Content) = Row {
    icon()
    Spacer(2)
    content()
}

@Composable
fun Loading(
    delay: Duration = 100.milliseconds,
    color: Color = Color.BrightBlue, content: Content
) =
    Status({ Spinner(delay, color) }, content)

@Composable
fun Error(color: Color = Color.Red, content: Content) =
    Status({ Cross(color) }, content)

@Composable
fun Success(color: Color = Color.BrightGreen, content: Content) =
    Status({ CheckMark(color) }, content)

@Composable
fun Information(color: Color = Color.Blue, content: Content) =
    Status({ InformationIcon(color) }, content)

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

    Text(chars[currentChar].toString(), color = color)
}

/**
 * Creates a checkmark
 * @param color the [Color]
 */
@Composable
fun CheckMark(color: Color = Color.BrightGreen) {
    Text(checkMark.toString(), color = color)
}

/**
 * Creates a cross
 * @param color the [Color]
 */
@Composable
fun Cross(color: Color = Color.Red) {
    Text(cross.toString(), color = color)
}

/**
 * Creates a cross
 * @param color the [Color]
 */
@Composable
fun InformationIcon(color: Color = Color.Blue) {
    Text(information.toString(), color = color)
}
