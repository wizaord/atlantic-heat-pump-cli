package fr.wizaord.atlanticheatpump.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.requireObject
import kotlinx.coroutines.runBlocking

class DevicesCommand : CliktCommand(name = "devices") {
    override fun help(context: Context) = "List heat pump devices"

    private val ctx by requireObject<CliContext>()

    override fun run() { runBlocking {
        val devices = ctx.acPort.listDevices()

        if (devices.isEmpty()) {
            echo("No devices found.")
            return@runBlocking
        }

        echo("%-50s %-30s %s".format("URL", "Label", "Type"))
        echo("-".repeat(110))
        for (device in devices) {
            echo(
                "%-50s %-30s %s".format(
                    device.url,
                    device.label.take(28),
                    device.type,
                )
            )
        }
        echo("\n${devices.size} device(s) found.")
    } }
}
