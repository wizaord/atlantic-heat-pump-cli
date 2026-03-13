package fr.wizaord.atlanticheatpump.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice
import fr.wizaord.atlanticheatpump.domain.model.FanSpeed
import kotlinx.coroutines.runBlocking

class FanCommand : CliktCommand(name = "fan") {
    override fun help(context: Context) = "Set fan speed (quiet, 2, 3, 4, auto)"

    private val ctx by requireObject<CliContext>()
    private val device by option("--device", "-d", help = "Device URL")
    private val speed by argument(help = "quiet, 2, 3, 4 ou auto").choice("quiet", "2", "3", "4", "auto")

    override fun run() { runBlocking {
        val deviceUrl = device ?: ctx.resolveDevice()
        val fanSpeed = when (speed) {
            "quiet" -> FanSpeed.QUIET
            "2" -> FanSpeed.SPEED_2
            "3" -> FanSpeed.SPEED_3
            "4" -> FanSpeed.SPEED_4
            else -> FanSpeed.AUTO
        }
        ctx.acPort.setFanSpeed(deviceUrl, fanSpeed)
        echo("Fan speed set to '${fanSpeed.label}'.")
    } }
}
