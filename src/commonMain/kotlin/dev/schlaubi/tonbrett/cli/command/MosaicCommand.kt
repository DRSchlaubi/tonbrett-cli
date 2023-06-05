package dev.schlaubi.tonbrett.cli.command

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import com.github.ajalt.clikt.core.CliktCommand
import com.jakewharton.mosaic.runMosaicBlocking
import dev.schlaubi.tonbrett.cli.io.LocalKeyEvents
import dev.schlaubi.tonbrett.cli.io.ProvideKeyEvents
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

val LocalExitCode =
    compositionLocalOf<CompletableDeferred<Int>> { throw UnsupportedOperationException("No default value supported") }

@Composable
fun exitProcess(exitCode: Int = 0) = LocalExitCode.current.complete(exitCode)

@Composable
fun exitAfter(delay: Duration = 200.milliseconds, exitCode: Int = 0) {
    val exitCodeCompletable = LocalExitCode.current
    LaunchedEffect(Unit) {
        delay(delay)
        exitCodeCompletable.complete(exitCode)
    }
}

abstract class MosaicCommand(
    help: String = "",
    epilog: String = "",
    name: String? = null,
    invokeWithoutSubcommand: Boolean = false,
    printHelpOnEmptyArgs: Boolean = false,
    helpTags: Map<String, String> = emptyMap(),
    autoCompleteEnvvar: String? = "",
    allowMultipleSubcommands: Boolean = false,
    treatUnknownOptionsAsArgs: Boolean = false,
    hidden: Boolean = false,
) : CliktCommand(
    help,
    epilog,
    name,
    invokeWithoutSubcommand,
    printHelpOnEmptyArgs,
    helpTags,
    autoCompleteEnvvar,
    allowMultipleSubcommands,
    treatUnknownOptionsAsArgs,
    hidden
) {

    private val errorCode = CompletableDeferred<Int>()

    /**
     * The entry point for the command.
     */
    @Composable
    abstract fun Command()

    /**
     * Exits the process with [exitCode].
     */
    protected fun exitProcess(exitCode: Int = 0) = errorCode.complete(exitCode)

    final override fun run() = runMosaicBlocking {
        if (currentContext.invokedSubcommand == null) {
            setContent {
                CompositionLocalProvider(LocalExitCode provides errorCode) {
                    ProvideKeyEvents {
                        val keyEvents = LocalKeyEvents.current
                        LaunchedEffect(Unit) {
                            keyEvents
                                .filter { it.asciiChar == '' }
                                .collect {
                                    exitProcess(1)
                                }
                        }
                        Command()
                    }
                }
            }

            kotlin.system.exitProcess(errorCode.await())
        }
    }
}
