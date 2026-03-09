package fr.wizaord.atlanticheatpump.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice
import fr.wizaord.atlanticheatpump.domain.model.AcMode
import kotlinx.coroutines.runBlocking

class ModeCommand : CliktCommand(name = "mode") {
    override fun help(context: Context) = "Set operating mode (heat, cool, auto)"

    private val ctx by requireObject<CliContext>()
    private val device by option("--device", "-d", help = "Device URL")
    private val mode by argument(help = "heat, cool ou auto").choice("heat", "cool", "auto")

    override fun run() { runBlocking {
        val deviceUrl = device ?: ctx.resolveDevice()
        val acMode = when (mode) {
            "heat" -> AcMode.HEATING
            "cool" -> AcMode.COOLING
            else -> AcMode.AUTO
        }
        ctx.acPort.setMode(deviceUrl, acMode)
        echo("Mode '${mode}' applied.")
    } }
}
