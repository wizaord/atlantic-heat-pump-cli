package fr.wizaord.atlanticheatpump.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.obj
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import fr.wizaord.atlanticheatpump.domain.model.AcConfig
import fr.wizaord.atlanticheatpump.domain.port.AcPort

data class CliContext(
    val verbose: Boolean,
    val acPortFactory: suspend (AcConfig) -> AcPort,
    private val config: AcConfig,
) {
    val acPort: AcPort by lazy {
        kotlinx.coroutines.runBlocking { acPortFactory(config) }
    }

    suspend fun resolveDevice(): String {
        return acPort.listDevices().firstOrNull()?.url
            ?: error("Aucun appareil trouve. Specifiez --device.")
    }
}

class AtlanticHeatPumpCli(
    private val acPortFactory: suspend (AcConfig) -> AcPort,
) : CliktCommand(name = "atlantic-heat-pump") {

    override fun help(context: Context) = "CLI to control Atlantic/Fujitsu heat pumps via Magellan API"

    private val verbose by option("--verbose", "-v", help = "Enable verbose mode").flag()
    private val login by option("--login", "-l", help = "Atlantic cloud login (email)", envvar = "ATLANTIC_LOGIN").required()
    private val password by option("--password", "-p", help = "Atlantic cloud password", envvar = "ATLANTIC_PASSWORD").required()

    override fun run() {
        currentContext.obj = CliContext(
            config = AcConfig(cloudLogin = login, cloudPassword = password),
            verbose = verbose,
            acPortFactory = acPortFactory,
        )
    }
}
