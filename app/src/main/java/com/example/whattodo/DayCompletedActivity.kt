package com.example.whattodo

import android.content.Context
import android.os.Bundle
import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.whattodo.ui.theme.WhatToDoTheme
import com.example.whattodo.ui.theme.showAlbum
import java.io.File

class DayCompletedActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WhatToDoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    showAlbum()
                }
            }
        }
    }
}


fun Context.getImageByName(fileName: String): File? {
    val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val directory = File(storageDir?.absolutePath ?: "")

    if (directory.exists() && directory.isDirectory) {
        val files = directory.listFiles()
        if (files != null) {
            for (file in files) {
                if (file.isFile && file.exists() && file.name.equals(fileName, ignoreCase = true)) {
                    return file
                }
            }
        }
    }

    return null
}

fun Context.getFilesContainingString(substring: String): List<File> {
    val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val directory = File(storageDir?.absolutePath ?: "")
    val foundFiles = mutableListOf<File>()

    if (directory.exists() && directory.isDirectory) {
        val files = directory.listFiles()
        if (files != null) {
            for (file in files) {
                if (file.isFile && file.exists() && file.name.contains(substring, ignoreCase = true)) {
                    foundFiles.add(file)
                }
            }
        }
    }

    return foundFiles
}