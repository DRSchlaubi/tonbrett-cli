package dev.schlaubi.tonbrett.cli.util

import com.github.ajalt.clikt.core.BadParameterValue
import com.github.ajalt.clikt.parameters.options.NullableOption
import com.github.ajalt.clikt.parameters.options.convert
import io.ktor.http.*

private fun valueToUrl(url: String): Url = runCatching {
    Url(url)
}.getOrNull() ?: throw BadParameterValue("$url is not a valid url")

fun NullableOption<String, String>.url() = convert { valueToUrl(it) }
