package dev.schlaubi.tonbrett.cli.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.jakewharton.mosaic.ui.Color
import com.jakewharton.mosaic.ui.Row
import com.jakewharton.mosaic.ui.Text
import com.varabyte.kotter.foundation.input.CharKey
import com.varabyte.kotter.foundation.input.Keys
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
                if (it == Keys.BACKSPACE) {
                    updateValue(value.dropLast(1))
                } else if (it is CharKey) {
                    updateValue(value + it.code)
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
        Text(placeholder, color = Color.Disabled)
    }
}
