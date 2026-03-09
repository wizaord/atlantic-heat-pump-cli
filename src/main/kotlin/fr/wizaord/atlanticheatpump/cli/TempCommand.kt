package fr.wizaord.atlanticheatpump.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.double
import kotlinx.coroutines.runBlocking

class TempCommand : CliktCommand(name = "temp") {
    override fun help(context: Context) = "Set target temperature"

    private val ctx by requireObject<CliContext>()
    private val device by option("--device", "-d", help = "Device URL")
    private val target by option("--set", "-s", help = "Target temperature in degrees").double().required()

    override fun run() { runBlocking {
        val deviceUrl = device ?: ctx.resolveDevice()
        ctx.acPort.setTemperature(deviceUrl, target)
        echo("Target temperature set to ${target} C.")
    } }
}
