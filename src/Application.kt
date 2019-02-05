
import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.features.CORS
import io.ktor.features.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.sessions.*
import java.util.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.Authorization)
        header("MyCustomHeader")
        allowCredentials = true
        anyHost() // @TODO: Don't do this in production if possible. Try to limit it.
    }

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }
    install(Sessions) {
        cookie<KotlinSlackSession>( "KOTLIN_SLACK_SESSION", SessionStorageMemory())
    }

    val client = HttpClient(Apache) {
        install(JsonFeature) {
            serializer = GsonSerializer {
                // Setting for POST json. (ref. https://ktor.io/clients/http-client/calls/requests.html#specifying-a-body-for-requests )
                serializeNulls()
                disableHtmlEscaping()
            }
        }
    }
    val slackConfig = SlackConfig(clientId = System.getenv("SLACK_CLIENT_ID"), clientSecret = System.getenv("SLACK_CLIENT_SECRET"))
    val slackAuthService = SlackAuthService(slackConfig, client)

    routing {
        get("/signin") {
            val state = UUID.randomUUID().toString()
            call.sessions.set(KotlinSlackSession(state))
            call.respondRedirect(slackAuthService.authorizeUri(state))
        }

        get("/slack/auth") {
            val session : KotlinSlackSession = call.sessions.get<KotlinSlackSession>() ?: return@get call.respond(HttpStatusCode.Unauthorized, "invalid session")
            val code = call.request.queryParameters["code"] ?: return@get call.respond(HttpStatusCode.Unauthorized, "invalid code")
            val state = call.request.queryParameters["state"]?.takeIf { it == session.state } ?: return@get call.respond(HttpStatusCode.Unauthorized, "invalid state")

            val oauthAccess = slackAuthService.fetchOauthAccess(code, state)

            call.respond(oauthAccess)
        }
    }
}

data class KotlinSlackSession(
    val state: String
)
