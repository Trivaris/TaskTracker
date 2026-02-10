package org.trivaris.tasks

import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.table.table
import com.github.ajalt.mordant.terminal.Terminal
import org.trivaris.tasks.model.Task

class DisplayManager() {
    private val terminal = Terminal()

    fun displayTasks(tasks: List<Task>) {
        val taskTable = table {
            header { row("ID", "Status", "Name", "User") }
            body {
                tasks.forEach { task ->
                    val status = if (task.completed) TextColors.green("✔") else TextColors.red("✘")
                    row(task.id, status, task.name, task.user.name)
                }
            }
        }
        terminal.println(taskTable)
    }
}