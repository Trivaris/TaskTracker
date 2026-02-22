package org.trivaris.tasks.view

import kotlinx.html.*
import org.trivaris.tasks.view.components.navbar
import org.trivaris.tasks.view.components.taskList
import org.trivaris.tasks.model.Task

fun HTML.dashboard(tasks: List<Task>) {
    classes = setOf("h-full", "bg-gray-100")
    head {
        title { +"TaskTracker" }
        loadTailwind()
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