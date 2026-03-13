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

enum class SwingMode(val label: String, val positionValue: Int) {
    POSITION_1("position 1 (highest)", 1),
    POSITION_2("position 2", 2),
    POSITION_3("position 3", 3),
    POSITION_4("position 4 (lowest)", 4),
    SWING("swing", -1),
    UNKNOWN("unknown", -1);

    companion object {
        fun fromValues(swingOn: Boolean, position: Int): SwingMode {
            if (swingOn) return SWING
            return entries.find { it != UNKNOWN && it != SWING && it.positionValue == position } ?: UNKNOWN
        }
    }
}

data class AcState(
    val isOn: Boolean = false,
    val currentTemp: Double? = null,
    val targetTemp: Double? = null,
    val mode: AcMode = AcMode.AUTO,
    val fanSpeed: FanSpeed? = null,
    val swingMode: SwingMode? = null,
)

data class AcDevice(
    val url: String,
    val label: String,
    val type: String,
)
