package dev.schlaubi.tonbrett.cli.commands

import androidx.compose.runtime.*
import com.github.ajalt.clikt.parameters.options.option
import com.jakewharton.mosaic.ui.Text
import dev.schlaubi.tonbrett.cli.command.MosaicCommand
import dev.schlaubi.tonbrett.cli.components.Error
import dev.schlaubi.tonbrett.cli.components.Loading
import dev.schlaubi.tonbrett.cli.components.Success
import dev.schlaubi.tonbrett.cli.util.Config
import dev.schlaubi.tonbrett.cli.util.mainCommand
import dev.schlaubi.tonbrett.cli.util.saveConfig
import dev.schlaubi.tonbrett.client.Tonbrett
import dev.schlaubi.tonbrett.client.href
import dev.schlaubi.tonbrett.common.Route
import io.ktor.client.plugins.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlin.system.exitProcess

expect fun openUrl(url: String)

class LoginCommand : MosaicCommand(
    help = "Provides authentication information",
    name = "login"
) {
    private val authToken by option(help = "The authentication to save")

    @Composable
    override fun Command() {
        if (authToken == null) {
            StartAuthorization()
        } else {
            VerifyToken(authToken!!, mainCommand.apiUrl)
        }
    }
}

@Composable
private fun StartAuthorization() {
    LaunchedEffect(Unit) {
        openUrl(href(Route.Auth(type = Route.Auth.Type.CLI), Url("https://musikus.gutikus.schlau.bi")))
        exitProcess(0)
    }

    Text("Please sign in through your browser")
}

private sealed interface State {
    data object Loading : State
    data object Success : State
    data class Failure(val message: String, val status: HttpStatusCode) : State
}

@Composable
fun VerifyToken(token: String, url: Url) {
    var state by remember { mutableStateOf<State>(State.Loading) }

    LaunchedEffect(state) {
        if (state != State.Loading) {
            exitProcess(if (state == State.Success) 0 else 1)
        }
    }

    LaunchedEffect(token) {
        val api = Tonbrett(token, url)
        try {
            api.getMe()
            saveConfig(Config(token))
            state = State.Success
        } catch (e: ResponseException) {
            state = State.Failure(e.response.bodyAsText(), e.response.status)
        }
    }

    when (val currentState = state) {
        is State.Failure -> Error { Text("Could not validate authentication information: ${currentState.message} ${currentState.status}") }
        State.Loading -> Loading { Text("Validating authentication information") }
        State.Success -> Success { Text("Successfully signed in") }
    }
}


