package org.trivaris.tasks.view

import kotlinx.html.HTML
import kotlinx.html.body
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.h2
import kotlinx.html.head
import org.trivaris.tasks.controller.AuthController
import org.trivaris.tasks.view.components.loginForm

fun HTML.login(initialLoginResult: AuthController.LoginResult) {
    head { loadTailwind() }
    body {
        classes = setOf("bg-gray-100", "h-screen", "flex", "items-center", "justify-center")
        div {
            classes = setOf("w-full", "max-w-md", "bg-white", "p-8", "rounded-xl", "shadow-lg", "border", "border-gray-200")

            h2 {
                classes = setOf("text-2xl", "font-bold", "text-center", "text-gray-900", "mb-8")
                +"Sign in to TaskTracker"
            }
            loginForm(initialLoginResult)
        }
    }
}