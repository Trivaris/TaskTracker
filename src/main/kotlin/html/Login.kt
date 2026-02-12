package org.trivaris.tasks.html

import kotlinx.html.*
import org.trivaris.tasks.AuthManager

fun FlowContent.loginForm() = form {
    attributes["hx-get"] = "/login"
    attributes["hx-target"] = "#login-feedback"
    attributes["hx-swap"] = "outerHTML"

    div {
        classes = setOf("space-y-4")

        div {
            label { classes = setOf("block", "text-sm", "font-medium", "text-gray-700"); +"Email" }
            input(type = InputType.email, name = "email") {
                classes = setOf("mt-1", "block", "w-full", "rounded-md", "border-gray-300", "shadow-sm", "focus:border-blue-500", "focus:ring-blue-500")
                required = true
            }
        }

        div {
            label { classes = setOf("block", "text-sm", "font-medium", "text-gray-700"); +"Password" }
            input(type = InputType.password, name = "password") {
                classes = setOf("mt-1", "block", "w-full", "rounded-md", "border-gray-300", "shadow-sm", "focus:border-blue-500", "focus:ring-blue-500")
                required = true
            }
        }

        div {
            id = "login-feedback"
        }

        button(type = ButtonType.submit) {
            classes = setOf("w-full", "flex", "justify-center", "py-2", "px-4", "border", "border-transparent", "rounded-md", "shadow-sm", "text-sm", "font-medium", "text-white", "bg-blue-600", "hover:bg-blue-700", "focus:outline-none", "focus:ring-2", "focus:ring-offset-2", "focus:ring-blue-500")
            +"Sign In"
        }
    }
}

fun FlowContent.loginStatus(resultState: AuthManager.LoginResult) {
    div {
        id = "login-status"
        classes = setOf(
            "text-sm", "p-2", "rounded",
            if (resultState.success) "text-green-600 bg-green-50" else "text-red-600 bg-red-50"
        )
        +resultState.description

        if (resultState.success) {
            attributes["hx-trigger"] = "load"
            attributes["hx-get"] = "/dashboard"
            attributes["hx-push-url"] = "true"
            attributes["hx-target"] = "body"
        }
    }
}