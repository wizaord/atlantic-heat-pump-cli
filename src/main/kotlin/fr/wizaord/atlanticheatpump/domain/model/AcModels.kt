package fr.wizaord.atlanticheatpump.domain.model

enum class AcMode { HEATING, COOLING, AUTO }

enum class FanSpeed(val label: String, val apiValue: Int) {
    AUTO("auto", 5),
    QUIET("quiet", 1),
    SPEED_2("speed 2", 2),
    SPEED_3("speed 3", 3),
    SPEED_4("speed 4", 4),
    UNKNOWN("unknown", -1);

    companion object {
        fun fromValue(value: Int): FanSpeed = entries.find { it.apiValue == value } ?: UNKNOWN
    }
}

data class AcState(
    val isOn: Boolean = false,
    val currentTemp: Double? = null,
    val targetTemp: Double? = null,
    val mode: AcMode = AcMode.AUTO,
    val fanSpeed: FanSpeed? = null,
)

data class AcDevice(
    val url: String,
    val label: String,
    val type: String,
)
