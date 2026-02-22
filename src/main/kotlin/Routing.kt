package org.trivaris.tasks

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.plugins.di.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.div
import kotlinx.html.stream.appendHTML
import org.trivaris.tasks.controller.AuthController
import org.trivaris.tasks.controller.DatabaseController
import org.trivaris.tasks.controller.Keystore
import org.trivaris.tasks.controller.UserDataController
import org.trivaris.tasks.model.jwt.JWToken
import org.trivaris.tasks.view.components.loginStatus
import org.trivaris.tasks.view.components.taskItem
import org.trivaris.tasks.view.dashboard
import org.trivaris.tasks.view.login
import org.trivaris.tasks.view.register

fun Application.configureRouting() {
    val db: DatabaseController by dependencies
    val keystore: Keystore by dependencies

    routing {
        get("/") {
            if (handleLogin(keystore, db)?.loginResult?.success ?: false) call.respondRedirect("/dashboard")
        }

        get("/login") {
            val auth = handleLogin(keystore, db, false)
            val initialLoginResult = auth?.loginResult ?: AuthController.LoginResult.NO_INPUT
            if (initialLoginResult.success) call.respondRedirect("/dashboard")
            call.respondHtml { login(initialLoginResult) }
        }

        get("/register") {
            val auth = handleLogin(keystore, db, false)
            val initialLoginResult = auth?.loginResult ?: AuthController.LoginResult.NO_INPUT
            if (initialLoginResult.success) call.respondRedirect("/dashboard")
            call.respondHtml { register(initialLoginResult) }
        }

        post("/login") {
            handleAuthAction(keystore, db, AuthController::login)
        }

        post("/register") {
            handleAuthAction(keystore, db, AuthController::register)
        }

        get("/dashboard") {
            val auth = handleLogin(keystore, db) ?: return@get call.respond(HttpStatusCode.Unauthorized)
            val userData = UserDataController(auth.user, db)
            call.respondHtml(HttpStatusCode.OK) { dashboard(userData.tasks.values.toList()) }
        }

        patch("/tasks/{id}/toggle") {
            val auth = handleLogin(keystore, db) ?: return@patch call.respond(HttpStatusCode.Unauthorized)
            val userData = UserDataController(auth.user, db)

            val taskId = call.parameters["id"]?.toInt() ?: return@patch call.respond(HttpStatusCode.BadRequest)
            val status = userData.toggleTask(taskId)
            if (status.success) {
                val updatedTask =
                    userData.tasks[taskId] ?: return@patch call.respond(HttpStatusCode.InternalServerError)
                val htmlFragment = buildString { appendHTML().div { taskItem(updatedTask) } }
                return@patch call.respondText(htmlFragment, ContentType.Text.Html)
            }
            return@patch call.respond(status.httpStatusCode())
        }

        delete("/tasks/{id}") {
            val auth = handleLogin(keystore, db) ?: return@delete call.respond(HttpStatusCode.Unauthorized)
            val userData = UserDataController(auth.user, db)
            val taskId = call.parameters["id"]?.toInt() ?: return@delete call.respond(HttpStatusCode.BadRequest)
            return@delete call.respond(userData.toggleTask(taskId).httpStatusCode())
        }

        post("/tasks") {
            val auth = handleLogin(keystore, db) ?: return@post call.respond(HttpStatusCode.Unauthorized)

            val userData = UserDataController(auth.user, db)
            val taskName = call.parameters["name"] ?: return@post call.respond(HttpStatusCode.BadRequest)

            val newTask = userData.addTask(
                false, taskName, call.parameters["content"]
            ) ?: return@post call.respond(HttpStatusCode.BadRequest)

            val htmlFragment = buildString { appendHTML().div { taskItem(newTask) } }
            call.respondText(htmlFragment, ContentType.Text.Html)
        }
    }
}

suspend fun RoutingContext.handleLogin(
    keystore: Keystore, db: DatabaseController, autoRedirect: Boolean = true
): AuthController? {
    call.request.cookies["auth_token"]?.let {
        val auth = AuthController(keystore, db)
        auth.loginWithJWT(JWToken.ofTokenString(it)!!)
        return auth
    }

    if (autoRedirect) call.respondRedirect("/login")

    return null
}

suspend fun RoutingCall.receiveAuthParameters(): Triple<String?, String?, String?> {
    val params = receiveParameters()
    return Triple(params["email"], params["password"], params["username"])
}

suspend fun RoutingContext.handleAuthAction(
    keystore: Keystore, db: DatabaseController, action: (AuthController) -> Unit
) {
    val auth = AuthController(keystore, db)
    val (email, password, username) = call.receiveAuthParameters()

    email?.let { auth.setEmail(it) }
    password?.let { auth.setPassword(it) }
    username?.let { auth.setUsername(it) }

    action(auth)
    handleLoginResult(auth)
}

suspend fun RoutingContext.handleLoginResult(auth: AuthController) {
    if (!auth.loginResult.success) {
        val htmlFragment = buildString {
            appendHTML().div {
                loginStatus(auth.loginResult)
            }
        }
        call.respondText(htmlFragment, ContentType.Text.Html, HttpStatusCode.BadRequest)
        return
    }

    call.response.header("HX-Redirect", "/dashboard")
    call.response.cookies.append(
        name = "auth_token", value = auth.getJWT()!!.toTokenString(), httpOnly = true, secure = true, path = "/"
    )

    call.respond(HttpStatusCode.OK, "")
}

fun UserDataController.TaskCompletionType.httpStatusCode(): HttpStatusCode {
    return when (this) {
        UserDataController.TaskCompletionType.OK -> HttpStatusCode.OK
        UserDataController.TaskCompletionType.SERVER_ERROR -> HttpStatusCode.InternalServerError
        UserDataController.TaskCompletionType.UNAUTHORIZED -> HttpStatusCode.Unauthorized
        UserDataController.TaskCompletionType.BAD_REQUEST -> HttpStatusCode.BadRequest
    }
}