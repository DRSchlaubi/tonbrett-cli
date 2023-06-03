package dev.schlaubi.tonbrett.cli.ui

import androidx.compose.runtime.*
import com.jakewharton.mosaic.ui.Text
import com.jakewharton.mosaic.ui.TextStyle
import dev.schlaubi.tonbrett.cli.command.exitProcess
import dev.schlaubi.tonbrett.cli.components.Error
import dev.schlaubi.tonbrett.cli.components.Information
import dev.schlaubi.tonbrett.cli.components.Loading
import dev.schlaubi.tonbrett.cli.components.Spacer
import dev.schlaubi.tonbrett.client.Tonbrett
import dev.schlaubi.tonbrett.common.*
import io.ktor.client.plugins.*
import io.ktor.http.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

sealed interface State {
    object Loading : State
    object Offline : State
    object ChannelMismatch : State
    object PlayerUnavailable : State
    object SessionExpired : State

    data class Running(
        val sounds: List<Sound> = emptyList(),
        val playingSound: Id<Sound>? = null
    ) : State
}

@Composable
fun SoundList(api: Tonbrett) {
    var state by remember { mutableStateOf<State>(State.Loading) }

    fun updateState(voiceState: User.VoiceState?) {
        if (voiceState != null && voiceState.playerAvailable && !voiceState.channelMismatch && state is State.Running) return
        state = when {
            voiceState == null -> State.Offline
            voiceState.channelMismatch -> State.ChannelMismatch
            !voiceState.playerAvailable -> State.PlayerUnavailable
            else -> State.Running(playingSound = voiceState.playingSound)
        }
    }

    fun updateState(to: State) {
        state = to
    }

    LaunchedEffect(Unit) {
        try {
            updateState(api.getMe().voiceState)
        } catch (e: ClientRequestException) {
            if (e.response.status == HttpStatusCode.Unauthorized) {
                state = State.SessionExpired
            } else {
                throw e
            }
        }
    }

    LaunchedEffect(Unit) {
        launch { api.connect() }
        api.events.onEach {
            val currentState = state
            when (it) {
                is VoiceStateUpdateEvent -> updateState(it.voiceState)
                is InterfaceAvailabilityChangeEvent -> {
                    state = if (it.available) {
                        if (currentState is State.Running) {
                            currentState.copy(playingSound = it.playingSongId)
                        } else {
                            State.Running()
                        }
                    } else {
                        State.PlayerUnavailable
                    }
                }

                is SoundCreatedEvent -> {
                    if (currentState is State.Running) {
                        state = currentState.copy(sounds = currentState.sounds + it.sound)
                    }
                }

                is SoundDeletedEvent -> {
                    if (currentState is State.Running) {
                        state = currentState.copy(sounds = currentState.sounds.filter { sound -> sound.id == it.id })
                    }
                }

                is SoundUpdatedEvent -> {
                    if (currentState is State.Running) {
                        val mutableList = currentState.sounds.toMutableList()
                        val index = mutableList.indexOfFirst { sound -> sound.id == it.sound.id }
                        mutableList[index] = it.sound

                        state = currentState.copy(sounds = mutableList.toList())
                    }
                }
            }
        }.launchIn(this)
    }

    when (val currentState = state) {
        State.Loading -> Loading { Text("Connecting to server") }
        State.Offline -> Information { Text("You are currently not connected to a voice channel") }
        State.ChannelMismatch -> Information { Text("You are connected to a different voice channel than the bot") }
        State.PlayerUnavailable -> Information { Text("The player is currently busy") }
        State.SessionExpired -> {
            Error {
                Text("Your session expired, please login again using")
                Spacer()
                Text("tonbrett login", style = TextStyle.Bold)
            }
            exitProcess(1)
        }

        is State.Running -> {
            SoundContainer(api, currentState, ::updateState)
        }
    }
}
