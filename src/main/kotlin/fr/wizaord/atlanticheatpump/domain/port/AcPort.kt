package fr.wizaord.atlanticheatpump.domain.port

import fr.wizaord.atlanticheatpump.domain.model.AcDevice
import fr.wizaord.atlanticheatpump.domain.model.AcMode
import fr.wizaord.atlanticheatpump.domain.model.AcState

interface AcPort {
    suspend fun listDevices(): List<AcDevice>
    suspend fun getStatus(deviceUrl: String): AcState
    suspend fun turnOn(deviceUrl: String)
    suspend fun turnOff(deviceUrl: String)
    suspend fun setMode(deviceUrl: String, mode: AcMode)
    suspend fun setTemperature(deviceUrl: String, temperature: Double)
}
