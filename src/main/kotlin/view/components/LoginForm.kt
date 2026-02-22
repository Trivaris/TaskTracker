package org.trivaris.tasks.view.components

import kotlinx.html.*
import org.trivaris.tasks.controller.AuthController

fun FlowContent.loginForm(initialStatus: AuthController.LoginResult = AuthController.LoginResult.NO_INPUT) {
    val containerId = "login-status-container"
    form {
        attributes["hx-post"] = "/login"
        attributes["hx-target"] = "#$containerId"
        attributes["hx-swap"] = "innerHTML"

        div {
            classes = setOf("space-y-4")

            formField(InputType.email, "email", "Email")
            formField(InputType.password, "password", "Password")

            statusContainer(initialStatus, containerId)

            loginButton()
            registerRedirectButton()
        }
    }
}

fun FlowContent.loginStatus(resultState: AuthController.LoginResult) {
    if (resultState == AuthController.LoginResult.NO_INPUT) return

    div {
        classes = setOf(
            "text-sm", "p-2", "rounded", "mt-2",
            if (resultState.success) "text-green-600 bg-green-50" else "text-red-600 bg-red-50"
        )
        +resultState.description
    }
}