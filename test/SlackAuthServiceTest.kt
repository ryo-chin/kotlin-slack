import org.junit.Test

class SlackAuthServiceTest {
    @Test
    fun authorizeUri() {
        val service = SlackAuthService(SlackConfig("dummy_client_id"))
        println(service.authorizeUri())
    }
}
