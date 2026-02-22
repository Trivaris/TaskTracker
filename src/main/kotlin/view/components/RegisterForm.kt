package org.trivaris.tasks.view.components

import kotlinx.html.*
import org.trivaris.tasks.controller.AuthController

fun FlowContent.registerForm(initialStatus: AuthController.LoginResult = AuthController.LoginResult.NO_INPUT) {
    val containerId = "register-status-container"
    form {
        attributes["hx-post"] = "/register"
        attributes["hx-target"] = "#$containerId"
        attributes["hx-swap"] = "innerHTML"

        div {
            classes = setOf("space-y-4")

            formField(InputType.text, "username", "Username")
            formField(InputType.email, "email", "Email")
            formField(InputType.password, "password", "Password")

            statusContainer(initialStatus, containerId)

            registerButton()
        }
    }
}
