package fr.wizaord.atlanticheatpump.domain.model

enum class AcMode { HEATING, COOLING, AUTO }

data class AcState(
    val isOn: Boolean = false,
    val currentTemp: Double? = null,
    val targetTemp: Double? = null,
    val mode: AcMode = AcMode.AUTO,
)

data class AcDevice(
    val url: String,
    val label: String,
    val type: String,
)
