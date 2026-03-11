package fr.wizaord.atlanticheatpump

import fr.wizaord.atlanticheatpump.infrastructure.magellan.WriteCapabilityRequest
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import java.net.ServerSocket

class FakeMagellanServer {

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    val port: Int = ServerSocket(0).use { it.localPort }
    val baseUrl: String = "http://localhost:$port"

    val capturedCommands = mutableListOf<WriteCapabilityRequest>()

    private val setupJson = """
        [
            {
                "id": 1,
                "name": "My Home",
                "devices": [
                    {
                        "deviceId": 100,
                        "name": "Clim Salon",
                        "gatewaySerialNumber": "1751-1195-5624",
                        "modelId": 557,
                        "productId": 1,
                        "zoneId": 1,
                        "capabilities": []
                    },
                    {
                        "deviceId": 101,
                        "name": "Clim Chambre",
                        "gatewaySerialNumber": "1751-1195-5624",
                        "modelId": 558,
                        "productId": 1,
                        "zoneId": 2,
                        "capabilities": []
                    }
                ]
            }
        ]
    """.trimIndent()

    private val capabilitiesJson = """
        [
            {"capabilityId": 7, "value": "4"},
            {"capabilityId": 40, "value": "24.0"},
            {"capabilityId": 117, "value": "22.5"},
            {"capabilityId": 177, "value": "28.0"}
        ]
    """.trimIndent()

    private val server = embeddedServer(CIO, port = port) {
        install(ContentNegotiation) {
            json(json)
        }
        routing {
            post("/users/token") {
                call.respondText(
                    """{"access_token":"fake-token","token_type":"Bearer","expires_in":3600}""",
                    ContentType.Application.Json,
                )
            }
            get("/magellan/cozytouch/setupviewv2") {
                call.respondText(setupJson, ContentType.Application.Json)
            }
            get("/magellan/capabilities/") {
                call.respondText(capabilitiesJson, ContentType.Application.Json)
            }
            post("/magellan/executions/writecapability") {
                val body = call.receiveText()
                val request = json.decodeFromString<WriteCapabilityRequest>(body)
                capturedCommands.add(request)
                call.respondText("1", ContentType.Application.Json)
            }
        }
    }

    fun start() {
        server.start(wait = false)
    }

    fun stop() {
        server.stop(0, 0)
    }

    fun reset() {
        capturedCommands.clear()
    }
}
