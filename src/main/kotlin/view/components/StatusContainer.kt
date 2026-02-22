package org.trivaris.tasks.view.components

import kotlinx.html.FlowContent
import kotlinx.html.div
import kotlinx.html.id
import org.trivaris.tasks.controller.AuthController

fun FlowContent.statusContainer(initialStatus: AuthController.LoginResult, containerId: String) = div {
    id = "#$containerId"
    if (!initialStatus.success && initialStatus != AuthController.LoginResult.NO_INPUT) {
        loginStatus(initialStatus)
    }
}