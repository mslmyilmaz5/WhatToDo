package com.example.whattodo.ui.theme

import DatabaseHelper
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.example.whattodo.MainActivity
import com.example.whattodo.getFilesContainingString
import com.example.whattodo.model.Task
import java.io.File
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun DayCompletedScreen(databaseHelper: DatabaseHelper,
                     modifier: Modifier = Modifier) {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ -> }
    val context = LocalContext.current
    var index by remember { mutableStateOf(0)}
    val allTasks = databaseHelper.getAllTasks(getCurrentDateTime())
    val completedTasks = allTasks.filter { it.isDone }

    var imageFile = LocalContext.current.getFilesContainingString("WhatToDo#"+completedTasks[index].id+".jpg")
    var imageResource = if (imageFile.isEmpty()) null else rememberImagePainter(imageFile[0])
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        DayCompletedHeader(allTasks.count(),completedTasks.count())
        Box(
            modifier = Modifier
                .weight(1f)
                .background(
                    Color.White,
                    shape = RoundedCornerShape(
                        topStart = 45.dp,
                        topEnd = 45.dp,
                        bottomStart = 0.dp,
                        bottomEnd = 0.dp
                    )
                )
        ) {
            DayCompletedContent({index = if(index < completedTasks.count()-1) index + 1 else 0}
                ,imageResource,completedTasks[index])
            FloatingActionButton(
                onClick = {
                    val intent = Intent(context, MainActivity::class.java)
                    launcher.launch(intent)
                    }
                ,
                modifier = Modifier
                    .padding(16.dp)
                    .size(56.dp)
                    .align(Alignment.BottomEnd),
                containerColor = Color(0xfff1f1f1),
                contentColor = Color(0xFF4044C9)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Go to tasks page"
                )
            }
        }
        DayCompletedNavbar({index = if(index > 0) index - 1 else completedTasks.count()-1},
            {index = if(index < completedTasks.count()-1) index + 1 else 0}
            ,imageFile)
    }
}

@Composable
fun DayCompletedContent(onClick : () -> Unit,imageResource : Painter?,task : Task,modifier : Modifier = Modifier){
    Column(modifier.fillMaxWidth().padding(top = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if(imageResource != null){
            Image(
                painter = imageResource,
                contentDescription = null,
                modifier = Modifier
                    .width(360.dp)
                    .height(360.dp)
                    .clickable { onClick() }
            )
        }
        Text(
            text = task.title,Modifier.padding(20.dp),
            fontSize = 16.sp
        )
    }
}

@Composable
fun DayCompletedHeader(allTaskCount : Int,completedTaskCount : Int,
           modifier: Modifier = Modifier){
    Column(modifier = modifier
        .background(Color(0xFF4044C9))
        .padding(16.dp)
    ) {


        val calendar = Calendar.getInstance()
        val formattedDate = SimpleDateFormat("d MMMM", Locale.getDefault()).format(calendar.time)
        Row{
            Text(

                text = "WhatToDo",
                color = Color.White,
                fontSize = 25.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .weight(1f)
                    .padding(7.dp))
            Text(
                text = formattedDate,
                color = Color.White,
                textAlign = TextAlign.Right,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier
                    .weight(1f)
                    .padding(10.dp))
        }
        Row(verticalAlignment = Alignment.Bottom){
            Column(modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp, bottom = 10.dp)){

                Text(
                    text = "Day  Summary ${completedTaskCount}/${allTaskCount} Tasks Completed",
                    color = Color.White,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(start = 10.dp))
            }
        }
    }
}

@Composable
fun DayCompletedNavbar(onBackClick : () -> Unit,onForwardClick : () -> Unit,imageFile: List<File>,modifier: Modifier = Modifier){
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .background(Color(0xFF4044C9))
            .fillMaxWidth()
            .padding(15.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { onBackClick()  },
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp, end = 10.dp)
                .height(50.dp).background(Color.White,shape = CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBackIos,
                contentDescription = "Back",
                tint = Color(0xFF4044C9), // Customize the color as needed
            )
        }
        IconButton(
            onClick = {
                if(imageFile.isNotEmpty()){
                    saveImageToGallery(imageFile[0],context)
                }
            },
            modifier = Modifier
                .weight(0.5f)
                .padding(start = 10.dp, end = 10.dp)
                .height(50.dp).background(Color.White,shape =  RoundedCornerShape(15.dp))
        ) {
            Icon(
                imageVector = Icons.Default.Save,
                contentDescription = "Forward",
                tint = Color(0xFF4044C9), // Customize the color as needed
            )
        }
        IconButton(
            onClick = { onForwardClick() },
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp, end = 10.dp)
                .height(50.dp).background(Color.White,shape = CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowForwardIos,
                contentDescription = "Forward",
                tint = Color(0xFF4044C9), // Customize the color as needed
            )
        }
    }
}
private fun getCurrentDateTime(): String {
    val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return sdf.format(Date())
}

private fun saveImageToGallery(imageFile: File?, context: Context) {

    imageFile?.let {

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "Image_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }

        val contentResolver = context.contentResolver
        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let { imageUri ->
            contentResolver.openOutputStream(imageUri)?.use { outputStream ->
                it.inputStream().use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
                outputStream.flush()
            }

            Toast.makeText(context, "Image saved to Gallery", Toast.LENGTH_SHORT).show()
        } ?: run {
            Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show()
        }
    }
}
