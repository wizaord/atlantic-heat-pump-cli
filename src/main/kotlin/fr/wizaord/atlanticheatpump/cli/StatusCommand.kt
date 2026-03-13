package fr.wizaord.atlanticheatpump.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.options.option
import kotlinx.coroutines.runBlocking

class StatusCommand : CliktCommand(name = "status") {
    override fun help(context: Context) = "Show device status"

    private val ctx by requireObject<CliContext>()
    private val device by option("--device", "-d", help = "Device URL")

    override fun run() { runBlocking {
        val deviceUrl = device ?: ctx.resolveDevice()
        val acState = ctx.acPort.getStatus(deviceUrl)

        echo("Device: $deviceUrl")
        echo("  Power      : ${if (acState.isOn) "ON" else "OFF"}")
        echo("  Mode       : ${acState.mode.name.lowercase()}")
        acState.currentTemp?.let { echo("  Current    : $it C") }
        acState.targetTemp?.let { echo("  Target     : $it C") }
        acState.fanSpeed?.let { echo("  Fan speed  : ${it.label}") }
        acState.swingMode?.let { echo("  Swing      : ${it.label}") }
    } }
}
