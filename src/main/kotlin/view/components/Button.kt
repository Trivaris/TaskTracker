package org.trivaris.tasks.view.components

import kotlinx.html.ButtonType
import kotlinx.html.FlowContent
import kotlinx.html.a
import kotlinx.html.button
import kotlinx.html.classes
import kotlinx.html.div

fun FlowContent.registerButton() = button(type = ButtonType.submit) {
    classes = setOf("w-full", "flex", "justify-center", "border", "border-transparent", "rounded-md", "shadow-sm", "text-sm", "font-medium", "text-white", "bg-blue-600", "hover:bg-blue-700", "focus:outline-none", "focus:ring-2", "focus:ring-offset-2", "focus:ring-blue-500", "py-2", "px-3")
    +"Register"
}

fun FlowContent.registerRedirectButton() = div {
    a {
        href = "/register"
        classes = setOf("w-full", "flex", "justify-center", "border", "border-transparent", "rounded-md", "shadow-sm", "text-sm", "font-medium", "text-white", "bg-blue-400", "hover:bg-blue-700", "focus:outline-none", "focus:ring-2", "focus:ring-offset-2", "focus:ring-blue-500", "py-2", "px-3")
        +"Sign Up"
    }
}

fun FlowContent.loginButton() = button(type = ButtonType.submit) {
    classes = setOf("w-full", "flex", "justify-center", "border", "border-transparent", "rounded-md", "shadow-sm", "text-sm", "font-medium", "text-white", "bg-blue-300", "hover:bg-blue-700", "focus:outline-none", "focus:ring-2", "focus:ring-offset-2", "focus:ring-blue-500", "py-2", "px-3")
    +"Login"
}
