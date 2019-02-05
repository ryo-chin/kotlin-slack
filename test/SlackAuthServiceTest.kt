import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import org.junit.Test
import java.util.*

class SlackAuthServiceTest {
    @Test
    fun authorizeUri() {
        val service = SlackAuthService(SlackConfig("dummy_client_id", "dummy_client_secret"), HttpClient(Apache))
        val state = UUID.randomUUID().toString()
        println(service.authorizeUri(state))
    }
}
