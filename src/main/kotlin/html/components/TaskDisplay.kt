package org.trivaris.tasks.html.components

import kotlinx.html.*
import org.trivaris.tasks.model.Task

fun FlowContent.taskList(tasks: List<Task>) = div {
    id = "task-list"
    classes = setOf("mt-6", "grid", "gap-4", "grid-cols-1")

    tasks.forEach { task ->
        taskItem(task)
    }
}

fun FlowContent.taskItem(task: Task) = div {
    id = "task-${task.id}"
    classes = setOf(
        "flex", "items-start", "justify-between", "p-4",
        "bg-white", "border", "border-gray-200", "rounded-lg", "shadow-sm",
        "hover:border-blue-300", "transition-colors"
    )

    div {
        classes = setOf("flex", "items-start", "space-x-3")

        // HTMX Toggle Checkbox
        input(type = InputType.checkBox) {
            classes = setOf("mt-1.5", "h-4", "w-4", "rounded", "border-gray-300", "text-blue-600", "focus:ring-blue-500")
            checked = task.completed
            attributes["hx-patch"] = "/tasks/${task.id}/toggle"
            attributes["hx-target"] = "#task-${task.id}"
            attributes["hx-swap"] = "outerHTML"
        }

        div {
            h3 {
                classes = setOf(
                    "text-sm", "font-medium",
                    if (task.completed) "text-gray-400 line-through" else "text-gray-900"
                )
                +task.name
            }
            task.content?.let {
                p {
                    classes = setOf("text-xs", "text-gray-500", "mt-1")
                    +it
                }
            }
            span {
                classes = setOf("inline-flex", "items-center", "mt-2", "px-2", "py-0.5", "rounded", "text-xs", "font-medium", "bg-gray-100", "text-gray-600")
                +"@${task.user.name}"
            }
        }
    }

    // HTMX Delete Button
    button {
        classes = setOf("text-gray-400", "hover:text-red-600", "transition-colors")
        attributes["hx-delete"] = "/tasks/${task.id}"
        attributes["hx-target"] = "#task-${task.id}"
        attributes["hx-swap"] = "outerHTML"
        attributes["hx-confirm"] = "Are you sure you want to delete this task?"

        unsafe { +"""<svg class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" /></svg>""" }
    }
}