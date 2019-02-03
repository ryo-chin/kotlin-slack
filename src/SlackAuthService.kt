import io.ktor.http.URLBuilder

class SlackAuthService(
    private val config: SlackConfig
) {

    fun authorizeUri(): String = URLBuilder("https://slack.com/oauth/authorize").apply {
            parameters.append("scope", "identity.basic,identity.email,identity.team,identity.avatar")
            parameters.append("client_id", config.clientId)
            parameters.append("state", "kotlin-slack")
        }.buildString()
}