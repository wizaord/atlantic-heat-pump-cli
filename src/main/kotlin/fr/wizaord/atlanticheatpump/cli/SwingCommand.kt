package fr.wizaord.atlanticheatpump.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice
import fr.wizaord.atlanticheatpump.domain.model.SwingMode
import kotlinx.coroutines.runBlocking

class SwingCommand : CliktCommand(name = "swing") {
    override fun help(context: Context) = "Set vane orientation (1, 2, 3, 4, swing)"

    private val ctx by requireObject<CliContext>()
    private val device by option("--device", "-d", help = "Device URL")
    private val position by argument(help = "1, 2, 3, 4 ou swing").choice("1", "2", "3", "4", "swing")

    override fun run() { runBlocking {
        val deviceUrl = device ?: ctx.resolveDevice()
        val swingMode = when (position) {
            "1" -> SwingMode.POSITION_1
            "2" -> SwingMode.POSITION_2
            "3" -> SwingMode.POSITION_3
            "4" -> SwingMode.POSITION_4
            else -> SwingMode.SWING
        }
        ctx.acPort.setSwingMode(deviceUrl, swingMode)
        echo("Swing mode set to '${swingMode.label}'.")
    } }
}
