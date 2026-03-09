package fr.wizaord.atlanticheatpump.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice
import kotlinx.coroutines.runBlocking

class PowerCommand : CliktCommand(name = "power") {
    override fun help(context: Context) = "Turn device on or off"

    private val ctx by requireObject<CliContext>()
    private val device by option("--device", "-d", help = "Device URL")
    private val state by argument(help = "on ou off").choice("on", "off")

    override fun run() { runBlocking {
        val deviceUrl = device ?: ctx.resolveDevice()
        when (state) {
            "on" -> ctx.acPort.turnOn(deviceUrl)
            "off" -> ctx.acPort.turnOff(deviceUrl)
        }
        echo("Power '$state' command sent.")
    } }
}
