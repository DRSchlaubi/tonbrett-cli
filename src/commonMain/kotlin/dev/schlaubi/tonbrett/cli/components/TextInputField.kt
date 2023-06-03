package dev.schlaubi.tonbrett.cli.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.jakewharton.mosaic.ui.Color
import com.jakewharton.mosaic.ui.Row
import com.jakewharton.mosaic.ui.Text
import dev.schlaubi.tonbrett.cli.io.Keys
import dev.schlaubi.tonbrett.cli.io.LocalKeyEvents

@Composable
fun TextInputField(
    value: String,
    updateValue: (String) -> Unit,
    label: Content? = null,
    placeholder: String? = null
) = Row {
    val keyEvents = LocalKeyEvents.current
    LaunchedEffect(value) {
        keyEvents
            .collect {
                if (it == Keys.Backspace) {
                    updateValue(value.dropLast(1))
                } else if (it !in listOf(Keys.Enter, Keys.Tab)) {
                    if (it.asciiChar == null) {
                        updateValue(value)
                    } else {
                        updateValue(value + it.asciiChar)
                    }
                }
            }
    }
    if (label != null) {
        label()
        Spacer(2)
    }

    if (value.isNotEmpty()) {
        Text(value)
    } else if (placeholder != null) {
        Text(placeholder, color = Color.BrightBlack)
    }
}
