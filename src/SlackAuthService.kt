
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.URLBuilder
import io.ktor.http.contentType

class SlackAuthService(
    private val config: SlackConfig,
    private val client: HttpClient
) {

    fun authorizeUri(state: String): String = URLBuilder("https://slack.com/oauth/authorize").apply {
        parameters.append("scope", "identity.basic,identity.email,identity.team,identity.avatar")
        parameters.append("client_id", config.clientId)
        parameters.append("state", state)
        parameters.append("redirect_uri", config.redirectUri)
    }.buildString()

    // Slack API requests contentType : application/x-www-form-urlencoded
    suspend fun fetchOauthAccess(code: String, state: String): SlackOAuthAccess =
        client.submitForm("https://slack.com/api/oauth.access") {
            parameter("client_id", config.clientId)
            parameter("client_secret", config.clientSecret)
            parameter("code", code)
            parameter("state", state)
            parameter("redirect_uri", config.redirectUri)
        }

    // Example of POST json. (Required client serializer setup and contentType application/json.)
    suspend fun postExample(code: String, state: String): SlackOAuthAccess =
        client.post("https://slack.com/api/oauth.access") {
            contentType(ContentType.Application.Json)
            body = SlackOAuthAccessBody(config.clientId, config.clientSecret, code, state, config.redirectUri)
        }
}

data class SlackOAuthAccessBody(
    val client_id: String,
    val client_secret: String,
    val code: String,
    val state: String,
    val redirect_uri: String
)