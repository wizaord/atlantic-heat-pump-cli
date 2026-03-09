package fr.wizaord.atlanticheatpump.infrastructure.magellan

import fr.wizaord.atlanticheatpump.domain.model.AcConfig
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory

class MagellanClient(
    private val config: AcConfig,
    baseUrl: String? = null,
) {

    private val logger = LoggerFactory.getLogger(MagellanClient::class.java)

    private val resolvedBaseUrl = baseUrl ?: "https://apis.groupe-atlantic.com"

    // Base64-encoded clientId:clientSecret (public, embedded in official Cozytouch apps)
    private val clientId = "Q3RfMUpWeVRtSUxYOEllZkE3YVVOQmpGblpVYToyRWNORHpfZHkzNDJVSnFvMlo3cFNKTnZVdjBh"

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    private val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(json)
        }
        engine {
            requestTimeout = 15_000
        }
    }

    private var accessToken: String? = null

    private suspend fun authenticate() {
        logger.info("Authenticating via Atlantic Magellan API...")
        val response = httpClient.submitForm(
            url = "$resolvedBaseUrl/users/token",
            formParameters = parameters {
                append("grant_type", "password")
                append("scope", "openid")
                append("username", "GA-PRIVATEPERSON/${config.cloudLogin}")
                append("password", config.cloudPassword)
            }
        ) {
            header("Authorization", "Basic $clientId")
        }
        val body = response.bodyAsText()
        require(response.status.isSuccess()) {
            "Authentification Atlantic echouee (HTTP ${response.status}): $body"
        }
        val tokenResponse = json.decodeFromString<MagellanTokenResponse>(body)
        accessToken = tokenResponse.access_token
        require(accessToken!!.isNotBlank()) { "Access token vide recu d'Atlantic" }
        logger.info("Authentication successful")
    }

    private suspend fun ensureAuthenticated() {
        if (accessToken == null) authenticate()
    }

    suspend fun getSetup(): MagellanSetup {
        ensureAuthenticated()
        val response = httpClient.get("$resolvedBaseUrl/magellan/cozytouch/setupviewv2") {
            header("Authorization", "Bearer $accessToken")
            accept(ContentType.Application.Json)
        }
        require(response.status.isSuccess()) {
            "Erreur recuperation setup (HTTP ${response.status}): ${response.bodyAsText()}"
        }
        // Response is a JSON array, first element is the setup
        val setups = response.body<List<MagellanSetup>>()
        require(setups.isNotEmpty()) { "Aucun setup trouve" }
        return setups.first()
    }

    suspend fun getCapabilities(deviceId: Int): List<MagellanCapability> {
        ensureAuthenticated()
        val response = httpClient.get("$resolvedBaseUrl/magellan/capabilities/") {
            header("Authorization", "Bearer $accessToken")
            parameter("deviceId", deviceId)
            accept(ContentType.Application.Json)
        }
        require(response.status.isSuccess()) {
            "Erreur recuperation capabilities (HTTP ${response.status}): ${response.bodyAsText()}"
        }
        return response.body()
    }

    suspend fun writeCapability(deviceId: Int, capabilityId: Int, value: String): Int {
        ensureAuthenticated()
        val response = httpClient.post("$resolvedBaseUrl/magellan/executions/writecapability") {
            header("Authorization", "Bearer $accessToken")
            contentType(ContentType.Application.Json)
            setBody(WriteCapabilityRequest(capabilityId, deviceId, value))
        }
        require(response.status.isSuccess()) {
            "Erreur ecriture capability (HTTP ${response.status}): ${response.bodyAsText()}"
        }
        return response.body()
    }

    fun close() {
        httpClient.close()
    }
}
