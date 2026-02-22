package org.trivaris.tasks.view.components

import kotlinx.html.FlowContent
import kotlinx.html.InputType
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.input
import kotlinx.html.label

fun FlowContent.formField(type: InputType, name: String, label: String) {
    div {
        label { classes = setOf("block", "text-sm", "font-medium", "text-gray-700"); +label }
        input(type = type, name = "name") {
            classes = setOf("mt-1", "block", "w-full", "rounded-md", "border-gray-300", "shadow-sm", "focus:border-blue-500", "focus:ring-blue-500", "py-2", "px-3")
            required = true
        }
    }
}