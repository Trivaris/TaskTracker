package org.trivaris.tasks.view

import kotlinx.html.*

fun HEAD.loadTailwind() {
    script { src = "https://cdn.tailwindcss.com" }
    script { src = "https://unpkg.com/htmx.org@1.9.10" }
}