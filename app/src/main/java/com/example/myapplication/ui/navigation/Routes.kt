package com.example.myapplication.ui.navigation

object Routes {
    const val HOME = "home"
    const val EDITOR = "editor/{noteId}"
    const val SETTINGS = "settings"
    const val LOOK_AND_FEEL = "look_and_feel"
    const val PROFILE = "profile"
    const val FOLDERS = "folders"

    fun editor(noteId: Long) = "editor/$noteId"
}
