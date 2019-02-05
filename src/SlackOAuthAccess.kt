data class SlackOAuthAccess(
    val ok: Boolean,
    val error: String,
    val access_token: String,
    val scope: String,
    val user: OAuthUser,
    val team: OAuthTeam

) {
    data class OAuthUser(
        val name: String,
        val id: String,
        val email: String
    )

    data class OAuthTeam(
        val id: String,
        val name: String,
        val domain: String
    )
}