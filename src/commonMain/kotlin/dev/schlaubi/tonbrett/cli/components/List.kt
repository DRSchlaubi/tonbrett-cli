package dev.schlaubi.tonbrett.cli.components

import androidx.compose.runtime.*
import com.jakewharton.mosaic.ui.Column
import com.jakewharton.mosaic.ui.Row
import com.varabyte.kotter.foundation.input.Keys
import dev.schlaubi.tonbrett.cli.io.LocalKeyEvents
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class ListItemScope(val selected: Boolean)

typealias NavigableListItemContent<T> = @Composable ListItemScope.(T) -> Unit

@Composable
fun <T> NavigableList(
    items: List<T>, height: Int = 5,
    content: NavigableListItemContent<T>
) {
    val indexedItems = remember(items) { items.withIndex().toList() }
    var currentHead by remember(items.size) { mutableStateOf(0) }
    var currentSelection by remember(items.size) { mutableStateOf(0) }
    val keyEvents = LocalKeyEvents.current

    LaunchedEffect(items) {
        withContext(Dispatchers.IO) {
            keyEvents.collect {
                when (it) {
                    Keys.DOWN -> {
                        currentSelection = (currentSelection + 1).coerceAtMost(items.lastIndex)
                    }

                    Keys.UP -> {
                        currentSelection = (currentSelection - 1).coerceAtLeast(0)
                    }
                }
                if (currentHead + height == currentSelection) {
                    currentHead++
                } else if (currentHead - 1 == currentSelection) {
                    currentHead = (currentHead - 1).coerceAtLeast(0)
                }
            }
        }
    }

    val itemsToRender = indexedItems.subList(currentHead, (currentHead + height).coerceAtMost(items.size))

    Column {
        itemsToRender.forEach { (index, item) ->
            Row {
                content(ListItemScope(index == currentSelection), item)
            }
        }
    }
}
