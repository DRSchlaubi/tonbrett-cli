package dev.schlaubi.tonbrett.cli.ui

import androidx.compose.runtime.*
import com.jakewharton.mosaic.ui.*
import dev.schlaubi.tonbrett.cli.components.Loading
import dev.schlaubi.tonbrett.cli.components.NavigableList
import dev.schlaubi.tonbrett.cli.components.Spacer
import dev.schlaubi.tonbrett.cli.components.TextInputField
import dev.schlaubi.tonbrett.cli.io.Keys
import dev.schlaubi.tonbrett.cli.io.LocalKeyEvents
import dev.schlaubi.tonbrett.client.Tonbrett
import dev.schlaubi.tonbrett.common.Sound
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
@Composable
fun SoundContainer(api: Tonbrett, state: State.Running, updateState: (State) -> Unit) {
    var loading by remember { mutableStateOf(true) }
    var search by remember { mutableStateOf("") }
    var onlyMine by remember { mutableStateOf(false) }
    val searchFlow = remember { MutableStateFlow("") }
    val keyEvents = LocalKeyEvents.current

    fun updateSearch(to: String) {
        search = to
        runBlocking {
            searchFlow.emit(to)
        }
    }

    LaunchedEffect(Unit) {
        updateState(state.copy(sounds = api.getSounds(useUnicode = true)))
        loading = false
    }

    LaunchedEffect(Unit) {
        searchFlow.debounce(300.milliseconds).onEach {
            updateState(state.copy(sounds = api.getSounds(query = it, onlyMine = onlyMine, useUnicode = true)))
        }.launchIn(this)
    }

    LaunchedEffect(onlyMine) {
        keyEvents.filter { it == Keys.Tab }.collect {
            onlyMine = !onlyMine

            updateState(state.copy(sounds = api.getSounds(query = search, onlyMine = onlyMine, useUnicode = true)))
        }
    }

    if (loading) {
        Loading { Text("Loading sounds") }
    } else {
        Column {
            NavigableList(state.sounds) {
                SoundRow(api, it, selected, it.id == state.playingSound)
            }
            TextInputField(search, ::updateSearch, { Text("Search", style = TextStyle.Bold) }, "Search for sounds")
        }
    }
}

private data class SoundRowKey(val selected: Boolean, val playing: Boolean)

@Composable
private fun SoundRow(
    api: Tonbrett, sound: Sound, selected: Boolean, playing: Boolean
) = Row {
    val keyEvents = LocalKeyEvents.current

    LaunchedEffect(SoundRowKey(selected, playing)) {
        if (selected) {
            keyEvents.filter { it == Keys.Enter || it == Keys.Space }.collect {
                if (playing) {
                    api.stop()
                } else {
                    api.play(sound.id.toString())
                }
            }
        }
    }

    if (sound.emoji is Sound.DiscordEmoji) {
        Text((sound.emoji as Sound.DiscordEmoji).value)
        Spacer(2)
    }

    val (color, textStyle) = when {
        selected && playing -> Color.BrightGreen to TextStyle.Bold
        selected -> Color.BrightBlue to null
        playing -> Color.BrightGreen to null
        else -> null to null
    }

    if (sound.description != null) {
        //TODO: Make this dynamic
        Text("${sound.name} - ${sound.description?.take(32)}", color, style = textStyle)
    } else {
        Text(sound.name, color, style = textStyle)
    }
}
