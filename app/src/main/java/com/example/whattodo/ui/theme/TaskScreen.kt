package com.example.whattodo.ui.theme

import DatabaseHelper
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddTask
import androidx.compose.material.icons.filled.AlarmOff
import androidx.compose.material.icons.filled.AlarmOn
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.sharp.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.rememberImagePainter
import com.example.whattodo.DayCompletedActivity
import com.example.whattodo.HabitsActivity
import com.example.whattodo.MainActivity
import com.example.whattodo.R
import com.example.whattodo.WhatToDoNotificationService
import com.example.whattodo.model.Habit

import com.example.whattodo.model.Task
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.Objects
import kotlin.random.Random



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WhatToDoAppTask(databaseHelper: DatabaseHelper,
                    whatToDoNotificationService: WhatToDoNotificationService,
                    modifier: Modifier = Modifier,) {
    var showDialog by remember { mutableStateOf(false) }
    var taskTitle by remember { mutableStateOf(TextFieldValue()) }
    var reminder by remember { mutableStateOf(false) }
    var reminderTime by remember { mutableStateOf<String?>(null) }
    var notificationId by remember { mutableStateOf(Random.nextInt()) }
    var tasks by remember { mutableStateOf(databaseHelper.getAllTasks(
        getCurrentDateTime()).toMutableList()) }
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ -> }



    var showConfirmation by remember { mutableStateOf(false) }

    if (showConfirmation) {
        AlertDialog(
            onDismissRequest = { showConfirmation = false },
            title = { Text(text = "Finishing the Day") },
            text = { Text(text = "Are you sure to finish the day?") },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmation= false
                        val intent = Intent(context, DayCompletedActivity::class.java)
                        launcher.launch(intent)
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                Button(onClick = { showConfirmation= false }) {
                    Text("No")
                }
            }
        )
    }

    for (task in tasks) {
        Log.d("Task Reminder Status", task.reminder.toString())
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
                taskTitle = TextFieldValue() // Reset form state
                reminder = false // Reset reminder state
                reminderTime = null // Reset selected time state
            },
            title = {
                Text(text = "Add New Task")
            },
            confirmButton = {
                Button(
                    onClick = {
                        if(reminderTime == null){
                            reminder = false
                        }
                        val task = Task(Random.nextInt(),taskTitle.text,false, reminder,reminderTime,false, notificationId,"",-1)
                        task.id = databaseHelper.addTask(task).toInt()
                        tasks.add(task)
                        if (reminder) whatToDoNotificationService.scheduleNotification(taskTitle.text,reminderTime,notificationId)
                        showDialog = false
                        taskTitle = TextFieldValue()
                        reminder = false
                        reminderTime = null
                        notificationId = Random.nextInt()
                    }
                ) {
                    Text(text = "Add")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDialog = false
                        taskTitle = TextFieldValue() // Reset form state
                        reminder = false // Reset reminder state
                        reminderTime = null // Reset selected time state
                    }
                ) {
                    Text(text = "Cancel")
                }
            },


            text = {
                Column {
                    TextField(
                        value = taskTitle,
                        onValueChange = { taskTitle = it },
                        label = { Text("Task Name") },
                        modifier = Modifier.fillMaxWidth()
                    )



                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = reminder,
                            onCheckedChange = { reminder = it },
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = "Remind Me",
                            fontSize = 18.sp
                        )
                    }
                    if (reminder) {
                        TimePickerDialogComponent(
                            onTimeSelected = { reminderTime = it }
                        )
                    }
                }
            }
        )
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {

        Header(tasks.size,"Tasks",{ showDialog = true})
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
            Content(tasks,{ deletedTask ->
                whatToDoNotificationService.cancelNotification(deletedTask.notificationId)
                tasks = tasks.filterNot { it == deletedTask }.toMutableList()
                databaseHelper.deleteTask(deletedTask.id)
                if (deletedTask.photo) {

                    val timeStamp = SimpleDateFormat("MM_dd").format(Date())
                    context.deleteImageByName("WhatToDo${timeStamp}#${deletedTask.id}.jpg")
                }
            })
            FloatingActionButton(
                onClick = {
                        showConfirmation = true }
,
                modifier = Modifier
                    .padding(16.dp)
                    .size(56.dp)
                    .align(Alignment.BottomEnd),
                containerColor = Color(0xFFFF7373),
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Finish the Day"
                )
            }
        }
        Navbar(0)
    }
}

@Composable
fun Header(count : Int,tasksOrHabits : String,
           onAddNewClicked: () -> Unit,
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
                    text = "$count $tasksOrHabits",
                    color = Color.White,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(start = 10.dp))
            }

            Icon(
                imageVector = Icons.Default.AddTask,
                contentDescription = "Photograph",
                modifier = Modifier
                    .padding(end = 15.dp)
                    .size(40.dp)
                    .clickable { onAddNewClicked() }
            )

        }
    }
}
@Composable
fun Content(taskList : List<Task>,
            onDeleteTask : (deletedTask : Task)->Unit ,
            modifier: Modifier = Modifier){

    val context = LocalContext.current
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp, bottom = 10.dp, top = 30.dp)
    ) {
        items(taskList) { task->
            TaskItem(
                task = task,
                onDelete = { deletedTask ->
                    onDeleteTask(deletedTask)
                },
                dbHelper = DatabaseHelper(context),
                whatToDoNotificationService = WhatToDoNotificationService(context,"WhatToDo_Notification")
            )
        }
        item {
            Spacer(modifier = Modifier.height(56.dp))
        }
    }
}
//Navbar is also used in HabitsActivity
//If focus is 0 Tasks is underlined otherwise habits underlined.
@Composable
fun Navbar(focus : Int,modifier: Modifier = Modifier){
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ -> }
    Row(
        modifier = Modifier
            .background(Color(0xFF4044C9))
            .fillMaxWidth()
            .padding(15.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = {
                if (focus != 0) {
                    val intent = Intent(context, MainActivity::class.java)
                    launcher.launch(intent)
                } else { }
            },
            shape = RoundedCornerShape(size = 15.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (focus == 0) Color.White else Color.LightGray,
                contentColor = Color(0xFF4044C9)
            ),
            modifier = Modifier
                .weight(1f)
                .padding(start = 20.dp, end = 15.dp)
                .height(50.dp)
        ) {
            Row{

                Text(
                    text = "TASKS",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                )
            }
        }
        Button(
            onClick = {
                if (focus == 0) {
                    val intent = Intent(context, HabitsActivity::class.java)
                    launcher.launch(intent)
                } else { }
            },
            shape = RoundedCornerShape(size = 15.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (focus != 0) Color.White else Color.LightGray,
                contentColor = Color(0xFF4044C9)
            ),
            modifier = Modifier
                .weight(1f)
                .padding(start = 15.dp, end = 20.dp)
                .height(50.dp)
        ) {
            Row{

                Text(
                    text = "HABITS",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,

                )
            }
        }
    }
}



@Composable
fun TaskItem(
    task: Task,
    onDelete: (Task) -> Unit,
    modifier: Modifier = Modifier,
    dbHelper: DatabaseHelper,
    whatToDoNotificationService: WhatToDoNotificationService,
    ) {
    var expanded by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var isTaskDone by remember { mutableStateOf(task.isDone) }
    val context = LocalContext.current

    var photoString by remember {
        mutableStateOf(if (task.isDone) "Wonderful! Take a selfie!" else "")
    }

    val file = context.createImageFile(task.id)

    val uri = FileProvider.getUriForFile(
        Objects.requireNonNull(context),
        context.packageName + ".provider", file
    )

    var capturedImageUri by remember {
        mutableStateOf<Uri>(Uri.EMPTY)
    }

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()){
            capturedImageUri = uri
        }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){
        if (it)
        {
            Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
            cameraLauncher.launch(uri)
        }
        else
        {
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }



    Card(
        modifier = modifier.padding(bottom = 20.dp)
            .animateContentSize()
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                expanded = !expanded
            }
    ) {
        var cardColor by remember { mutableStateOf(if (task.isDone) {
            Color(0xff4044c9)
        } else {
            if (task.habitId != -1) {
                Color(0xffb0f7c3) // Set the color to green if habitId is not -1
            } else {
                Color(0xfff1f1f1) // Set the default color
            }
        })}
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(cardColor)
                .padding(top = 15.dp, bottom = 10.dp)
        ) {
            Checkbox(checked = isTaskDone,
                     onCheckedChange = {
                    isTaskDone = it
                    task.isDone = it
                    if (task.isDone) {
                        expanded = true
                        cardColor = Color(0xFF4044C9)
                    }
                    if (!task.isDone){
                        expanded = false
                        cardColor = Color(0xFFF1F1F1)
                    }
                    photoString = if (task.isDone) "Wonderful! Take a selfie!" else ""
                    dbHelper.changeIsDone(task.id,task.isDone) },

            )
            Text(
                text = task.title,
                fontFamily = FontFamily.Monospace,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.weight(1f))

            var AlarmIcon by remember {mutableStateOf(Icons.Default.AlarmOff)}
            if (task.reminder) AlarmIcon = Icons.Default.AlarmOn

            Icon(
                imageVector = AlarmIcon,
                tint = Color.Black,
                contentDescription = "Reminder Status",
                modifier = Modifier
                    .padding(end = 5.dp)
                    .clickable {
                        if (!task.reminder && task.reminderTime == null) {
                            showDialog = true
                        } else if (!task.reminder && task.reminderTime != null) {
                            whatToDoNotificationService.scheduleNotification(
                                task.title,
                                task.reminderTime,
                                task.notificationId
                            )
                            AlarmIcon = Icons.Default.AlarmOn
                        } else {
                            whatToDoNotificationService.cancelNotification(task.notificationId)
                            AlarmIcon = Icons.Default.AlarmOff
                        }

                        task.reminder = !task.reminder
                        dbHelper.updateReminder(task.id, task)
                    }
            )

            ReminderDialog(
                showDialog = showDialog,
                onDialogDismiss = { showDialog = false },
                onReminderSet = { reminderTime ->
                    val reminderSet = reminderTime != null
                    AlarmIcon = if (reminderSet)Icons.Default.AlarmOn else Icons.Default.AlarmOff
                    task.reminder = reminderSet
                    task.reminderTime = reminderTime
                    whatToDoNotificationService.scheduleNotification(
                        task.title,
                        task.reminderTime,
                        task.notificationId
                    )
                    showDialog = false
                    dbHelper.updateReminder(task.id, task)
                }
            )

            if ( task.isDone ) {


                val infiniteTransition = rememberInfiniteTransition(label = "infinite_trans")

                val pulseMagnitude by infiniteTransition.animateFloat(
                    initialValue = 1f,
                    targetValue = 1.2f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    ), label = "photo"
                )

                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    tint = Color.Black ,
                    contentDescription = "Photograph",
                    modifier = Modifier
                        .padding(end = 5.dp)
                        .clickable {
                            val permissionCheckResult =
                                ContextCompat.checkSelfPermission(
                                    context,
                                    android.Manifest.permission.CAMERA
                                )

                            if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                                cameraLauncher.launch(uri)
                                task.photo = true
                                dbHelper.changeIsPhoto(task.id,task.photo)
                            } else {
                                permissionLauncher.launch(android.Manifest.permission.CAMERA)
                            }
                        }
                        .graphicsLayer(scaleX = pulseMagnitude, scaleY = pulseMagnitude)

                )

            }

            Icon (
                imageVector = Icons.Default.ExpandMore,
                tint = if (expanded) Color.Black else Color.Gray,
                contentDescription = "Expand Status",
                modifier = Modifier
                    .padding(end = 5.dp)
                    .clickable { expanded = !expanded }
            )
        }
        if (expanded) {
            Box(modifier = Modifier
                .background(cardColor)){
                Divider(modifier = Modifier
                    .padding(vertical = 8.dp))

            Column(modifier = Modifier
                .fillMaxWidth()
                .background(cardColor)
                .padding(start = 12.dp, bottom = 10.dp))
            {
                Row{
                    Text(
                        text = if(task.reminder && task.reminderTime != null) "Reminder: ${task.reminderTime}" else "Reminder is off",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black)
                    Spacer(modifier = Modifier.weight(1f))
                    if(task.habitId == -1){
                        Icon(
                            imageVector = Icons.Sharp.Delete,
                            contentDescription = "Delete",
                            modifier = Modifier
                                .padding(end = 5.dp)
                                .clickable {
                                    onDelete(task)
                                },

                            tint = Color.Red
                        )
                    }
                }

                    Text(
                        text = photoString,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black)


                if (task.isDone && task.photo) {

                    val timeStamp = SimpleDateFormat("MM_dd").format(Date())
                    val image = context.getImageByName("WhatToDo${timeStamp}#${task.id}.jpg")
                    showImage(image = image)
                    photoString = ""

                }
            }
            }
        }
    }
}

@Composable
fun showImage(
     image: File?,
){
    val rainbowColorsBrush = remember {
        Brush.sweepGradient(
            listOf(
                Color(0xFF9575CD),
                Color(0xFFBA68C8),
                Color(0xFFE57373),
                Color(0xFFFFB74D),
                Color(0xFFFFF176),
                Color(0xFFAED581),
                Color(0xFF4DD0E1),
                Color(0xFF9575CD)
            )
        )
    }


    if (image != null){
        Image(
            painter = rememberImagePainter(image),
            contentDescription = "image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .padding(end = 12.dp, top = 10.dp,)
                .clip(RoundedCornerShape(5.dp))
                .width(325.dp)
                .height(325.dp)
                .border(
                    BorderStroke(10.dp, rainbowColorsBrush),
                    RectangleShape
                )
        )

    }



}

@Composable
fun ReminderDialog(
    showDialog: Boolean,
    onDialogDismiss: () -> Unit,
    onReminderSet: (String?) -> Unit
) {
    var showTimePickerDialog by remember { mutableStateOf(false) }
    var key by remember { mutableStateOf(0) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                if (showTimePickerDialog) {
                    showTimePickerDialog = false
                    onDialogDismiss()
                } else {
                    onDialogDismiss()
                }
            },
            title = { Text(text = "Reminder") },
            text = { Text(text = "Would you like to set a reminder?") },
            confirmButton = {
                Button(
                    onClick = {
                        key++
                        showTimePickerDialog = true
                    }
                ) {
                    Text(text = "Yes")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        onReminderSet(null)
                        showTimePickerDialog = false
                        onDialogDismiss()
                    }
                ) {
                    Text(text = "No")
                }
            }
        )

        key(key) {
            if (showTimePickerDialog) {
                TimePickerDialogComponent(
                    onTimeSelected = { time ->
                        onReminderSet(time)
                    }
                )
            }
        }
    }
}
@Composable
fun TimePickerDialogComponent(
    onTimeSelected: (String) -> Unit,
) {
    val context = LocalContext.current
    var time by remember { mutableStateOf("") }
    val calendar = remember { Calendar.getInstance() }
    val timePickerDialog = remember {
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                val formattedTime = String.format("%02d:%02d", hourOfDay, minute)
                time = formattedTime
                onTimeSelected(formattedTime)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true // 24-hour format
        )
    }

    BackHandler(onBack = {
        timePickerDialog.dismiss()
    })

    DisposableEffect(Unit) {
        timePickerDialog.show()
        onDispose {
            timePickerDialog.dismiss()
        }
    }
}
private fun getCurrentDateTime(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return sdf.format(Date())
}



private fun Context.createImageFile(
    taskId: Int
): File {

    val timeStamp = SimpleDateFormat("MM_dd").format(Date())
    val imageFileName = "WhatToDo${timeStamp}#${taskId}.jpg"

    val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File(storageDir, imageFileName)

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

fun Context.deleteImageByName(fileName: String): Boolean {
    val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val directory = File(storageDir?.absolutePath ?: "")

    if (directory.exists() && directory.isDirectory) {
        val files = directory.listFiles()
        if (files != null) {
            for (file in files) {
                Log.d("XXXXXXXX", file.name.equals(fileName, ignoreCase = true).toString())
                if (file.isFile && file.exists() && file.name.equals(fileName, ignoreCase = true)) {

                    try {
                        val deleted = file.delete()
                        if (deleted) {
                            // Debug için log eklenebilir
                             Log.d("DeleteImage", "File deleted: ${file.absolutePath}")
                            return true
                        }
                    } catch (e: Exception) {
                        // Hata durumunu logla
                         Log.e("DeleteImage", "Error deleting file: ${e.message}")
                        return false
                    }
                }
            }
        }
    }
    return false
}