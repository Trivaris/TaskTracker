import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.server.testing.*
import kotlin.test.Test

class ConfigureRoutingTest {

    @Test
    fun testGet() = testApplication {
        application {
            TODO("Add the Ktor module for the test")
        }
        client.get("/").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testGetLogin() = testApplication {
        application {
            TODO("Add the Ktor module for the test")
        }
        client.get("/login").apply {
            TODO("Please write your test here")
        }
    }
}