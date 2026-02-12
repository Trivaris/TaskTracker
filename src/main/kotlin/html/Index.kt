package org.trivaris.tasks.html

import kotlinx.html.*
import org.trivaris.tasks.AuthManager
import org.trivaris.tasks.html.components.navbar
import org.trivaris.tasks.html.components.taskList
import org.trivaris.tasks.model.Task

fun HTML.index(tasks: List<Task>) {
    classes = setOf("h-full", "bg-gray-100")
    head {
        title {
            +"TaskTracker"
        }
        script { src = "https://cdn.tailwindcss.com" }
        script { src = "https://unpkg.com/htmx.org@1.9.10" }
    }
    body {
        classes = setOf("h-full")
        div {
            classes = setOf("min-h-full")
            navbar("Tasks")
            taskList(tasks)
        }
    }
}