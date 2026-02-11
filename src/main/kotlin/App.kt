package org.trivaris.tasks

import javafx.application.Application
import javafx.application.Application.launch
import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.geometry.Insets
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.stage.Stage
import org.trivaris.tasks.model.Task

fun main(args: Array<String>) {
    launch(App::class.java, *args)
}

class App: Application() {
    val connectionManager = ConnectionManager()
    val databaseManager = DatabaseManager(connectionManager)
    val authManager = AuthManager(databaseManager)
    val userDataManager = UserDataManager(databaseManager, authManager)

    override fun start(stage: Stage) {
        setLoginScreen(stage)
        stage.show()
    }

    fun setLoginScreen(stage: Stage) {
        val root = Group()
        stage.scene = Scene(root)

        authManager.loginResultProperty().addListener { _, _, newValue ->
            if (newValue == AuthManager.LoginResult.OK)
                Platform.runLater { setTaskScreen(stage) }
        }

        val emailInput = TextField().apply { textProperty().addListener { _, _, newValue -> authManager.setEmail(newValue) } }
        val passwordInput = TextField().apply { textProperty().addListener { _, _, newValue -> authManager.setPassword(newValue) }}

        val loginResultLabel = Label(authManager.loginDescriptionProperty().get()).apply {
            textProperty().bind(authManager.loginDescriptionProperty())
        }

        val loginButton = Button("Login").apply { onAction = {
            authManager.login()
        } }

        val hbox = HBox(loginResultLabel, emailInput, passwordInput, loginButton).apply {
            spacing = 10.0
            padding = Insets(40.0)
        }

        root.children.clear()
        root.children.addAll(hbox)
    }

    fun setTaskScreen(stage: Stage) {
        if (!authManager.isLoggedIn) throw IllegalStateException("User is not logged in")
        val root = StackPane()
        stage.scene = Scene(root, 800.0, 600.0)

        val tasks = FXCollections.observableArrayList(userDataManager.getTasks())


        val taskTable = TableView<Task>()
        val label = Label("Tasks");

        val nameCol = TableColumn<Task, String>("Name").apply { cellValueFactory = PropertyValueFactory("name") }
        val descriptionCol = TableColumn<Task, String>("Content").apply { cellValueFactory = PropertyValueFactory("content") }
        val completedCol = TableColumn<Task, Boolean>("Is Completed?").apply { cellValueFactory = PropertyValueFactory("completed") }

        taskTable.items = tasks
        taskTable.columns.addAll(nameCol, descriptionCol, completedCol)

        val vbox = VBox(label, taskTable).apply {
            spacing = 5.0
            padding = Insets(10.0)
        }

        root.children.clear()
        root.children.add(vbox)
    }
}
