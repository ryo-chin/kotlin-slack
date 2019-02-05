class SlackConfig(
    val clientId : String,
    val clientSecret : String,
    val redirectUri : String = "http://localhost:8080/slack/auth"
)