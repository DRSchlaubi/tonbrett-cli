package dev.schlaubi.tonbrett.cli.components

import androidx.compose.runtime.*
import com.jakewharton.mosaic.ui.Color
import com.jakewharton.mosaic.ui.Text
import com.jakewharton.mosaic.ui.TextStyle
import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * Text which scrolls horizontally to fit in smaller spaces.
 *
 * @param text the full text
 * @param color the [Color] of the text
 * @param style the [TextStyle] of the text
 * @param size the horizontal size of the text
 * @param speed the scrolling speed in frame length
 */
@Composable
fun ScrollingText(
    text: String,
    color: Color = Color.Unspecified,
    style: TextStyle = TextStyle.Unspecified,
    size: Int = 32,
    speed: Duration = 500.milliseconds
) {
    if (text.length <= size) {
        Text(text, color = color, textStyle = style)
    } else {
        var state by remember { mutableStateOf(0) }
        LaunchedEffect(state) {
            delay(speed)
            state = (state + 1) % text.length
        }
        val visibleText = text.substring(state)
        val scrollingText = if (visibleText.length < (size - 1)) {
            visibleText + ' ' + text.take(size - visibleText.length)
        } else {
            visibleText
        }

        Text(scrollingText, color = color, textStyle = style)
    }
}
