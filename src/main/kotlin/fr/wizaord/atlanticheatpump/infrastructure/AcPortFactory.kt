package fr.wizaord.atlanticheatpump.infrastructure

import fr.wizaord.atlanticheatpump.domain.model.AcConfig
import fr.wizaord.atlanticheatpump.domain.port.AcPort
import fr.wizaord.atlanticheatpump.infrastructure.magellan.MagellanAdapter
import fr.wizaord.atlanticheatpump.infrastructure.magellan.MagellanClient

object AcPortFactory {

    suspend fun create(config: AcConfig, magellanBaseUrl: String? = null): AcPort {
        require(config.cloudLogin.isNotBlank() && config.cloudPassword.isNotBlank()) {
            "Missing credentials. Use --login and --password or set ATLANTIC_LOGIN and ATLANTIC_PASSWORD."
        }
        val client = MagellanClient(config, magellanBaseUrl)
        val adapter = MagellanAdapter(client)
        adapter.listDevices() // validate connection
        return adapter
    }
}
