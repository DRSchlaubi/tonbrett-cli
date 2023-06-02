package dev.schlaubi.tonbrett.cli.components

import androidx.compose.runtime.Composable
import com.jakewharton.mosaic.ui.Text

const val space = " "

/**
 * Utility method creating a [width] characters wide
 */
@Composable
inline fun Spacer(width: Int = 1) = Text(space.repeat(width))
