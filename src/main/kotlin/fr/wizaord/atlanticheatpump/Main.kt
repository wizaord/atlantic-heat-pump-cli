package fr.wizaord.atlanticheatpump

import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.core.subcommands
import fr.wizaord.atlanticheatpump.cli.*
import fr.wizaord.atlanticheatpump.infrastructure.AcPortFactory

fun main(args: Array<String>) {
    AtlanticHeatPumpCli(AcPortFactory::create)
        .subcommands(
            DevicesCommand(),
            StatusCommand(),
            PowerCommand(),
            ModeCommand(),
            TempCommand(),
            FanCommand(),
        )
        .main(args)
}
