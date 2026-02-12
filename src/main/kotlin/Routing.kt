package org.trivaris.tasks

import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import io.ktor.server.application.Application
import io.ktor.server.html.respondHtml
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.response.header
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.html.body
import kotlinx.html.classes
import kotlinx.html.h2
import kotlinx.html.head
import org.trivaris.tasks.html.index
import org.trivaris.tasks.html.loadTailwind
import org.trivaris.tasks.html.loginForm
import org.trivaris.tasks.html.loginStatus

fun Application.configureRouting() {
    val db: DatabaseManager by dependencies

    routing {
        get("/") {
            call.respondHtml(HttpStatusCode.OK) { }
        }

        get("/login") {
            val auth = AuthManager(db)
            val params: Parameters = call.parameters
            val email = params["email"]
            val password = params["password"]

            System.out.printf("Logging in with email: %s, password %s %n", email, password)

            auth.setPassword(password)
            auth.setEmail(email)
            auth.login()

            if (auth.loginResult.success) {
                call.response.header("HX-Redirect", "/dashboard")
                call.respond(HttpStatusCode.OK, "")
                return@get
            }

            call.respondHtml {
                head { loadTailwind() }
                if (auth.loginResult == AuthManager.LoginResult.NO_INPUT) body {
                    classes = setOf("w-full", "max-w-md", "bg-white", "p-8", "rounded-xl", "shadow-lg", "border", "border-gray-100")
                    h2 {
                        classes = setOf("text-2xl", "font-bold", "text-center", "text-gray-900", "mb-8")
                        +"Sign in to TaskTracker"
                    }
                    loginForm()
                }
                else body {
                    loginStatus(auth.loginResult)
                }
            }
        }

        get("/dashboard") {
            val auth = AuthManager(db)
            call.respondHtml {
                index()
            }
        }
    }
}
