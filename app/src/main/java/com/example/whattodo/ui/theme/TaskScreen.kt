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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddTask
import androidx.compose.material.icons.filled.AlarmOff
import androidx.compose.material.icons.filled.AlarmOn
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Sort
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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

    //(task.reminder && task.reminderTime != null) "Reminder set to ${task.reminderTime}." else "Remember your task! Set a reminder.",

    var showConfirmation by remember { mutableStateOf(false) }

    if (showConfirmation) {
        AlertDialog(
            onDismissRequest = { showConfirmation = false },
            title = { Text(text = "Finishing the Day") },
            text = { Text(text = "Are you sure to finish the day?") },
            confirmButton = {
                Button(
                    onClick = {
                        if(tasks.any { it.isDone }){
                            showConfirmation= false
                            val intent = Intent(context, DayCompletedActivity::class.java)
                            launcher.launch(intent)
                        }
                        else{
                            Toast.makeText(context, "Finish some tasks before you finish the day :)", Toast.LENGTH_SHORT).show()
                        }
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
                        if (reminder) whatToDoNotificationService.scheduleNotification("\"${taskTitle.text}\"",reminderTime,notificationId)
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

        Header(tasks.size,"tasks.",{ showDialog = true})
        Box(
            modifier = Modifier
                .padding(start = 10.dp,end=10.dp)
                .weight(1f)
                .background(
                    Color.White,
                    shape = RoundedCornerShape(
                        topStart = 45.dp,
                        topEnd = 45.dp,
                        bottomStart = 45.dp,
                        bottomEnd = 45.dp
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
        val formattedDate = SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()).format(calendar.time)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "WhatToDo",
                    color = Color.White,
                    fontSize = 25.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(5.dp)
                )
                Text(
                    text = formattedDate,
                    color = Color.White,
                    textAlign = TextAlign.Right,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(5.dp)
                )
            }

            Box(
                modifier = Modifier
                    .padding(end = 15.dp)
                    .size(60.dp)
                    .clickable { onAddNewClicked() }
                    .background(color = Color.White, shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add task",
                    tint = Color.Black,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(50.dp,50.dp)
                )
            }
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
            .padding(start = 10.dp, end = 10.dp, bottom = 30.dp, top = 30.dp)
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
            .padding(10.dp),
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
                .padding(start = 7.dp, end = 7.dp)
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
                .padding(start = 7.dp, end = 7.dp)
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
    var showDeleteDialog by remember { mutableStateOf(false) }
    var isTaskDone by remember { mutableStateOf(task.isDone) }
    var text by remember {mutableStateOf("")}
    val context = LocalContext.current


    val file = context.createImageFile(task.id)

    val uri = FileProvider.getUriForFile(
        Objects.requireNonNull(context),
        context.packageName + ".provider", file
    )

    var capturedImageUri by remember {
        mutableStateOf<Uri>(Uri.EMPTY)
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { isPictureTaken ->
        if (isPictureTaken) {
            dbHelper.changeIsPhoto(task.id, true)
            capturedImageUri = uri
            task.photo = true

        }

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
        modifier = modifier
            .padding(bottom = 15.dp)
            .animateContentSize()
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                expanded = !expanded
            }
    ) {
        var cardColor by remember { mutableStateOf(if (task.isDone) { Color(0xffb0f7c3) }

        else {
            if (task.habitId != -1) {
                Color(0xFF9E9E9E)// Set the color to green if habitId is not -1
            } else {
                Color(0xfff1f1f1) // Set the default color
            }
        })}
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(cardColor)
                .padding(top = 5.dp, bottom = 5.dp)
        ) {
            Checkbox(checked = isTaskDone,
                     onCheckedChange = {
                    isTaskDone = it
                    task.isDone = it
                    if (task.isDone) {
                        expanded = true
                        cardColor = Color(0xffb0f7c3)
                    }
                    if (!task.isDone){
                        expanded = false
                        cardColor = if (task.habitId != -1) {
                            Color(0xFF9E9E9E)// Set the color to green if habitId is not -1
                        } else {
                            Color(0xFFF1F1F1) // Set the default color
                        }
                    }

                    dbHelper.changeIsDone(task.id,task.isDone) },
            )
            Spacer(modifier = Modifier.width(8.dp))

            val title = task.title
            val reminder = if (task.reminder && task.reminderTime != null) {
                AnnotatedString(" (${task.reminderTime.toString()})", spanStyle = SpanStyle(color = Color.Red, fontWeight = FontWeight.Normal))
            } else {
                AnnotatedString("")
            }

            Text(
                text = buildAnnotatedString {
                    append(title)
                    append(reminder)
                },
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                maxLines = if (expanded) Int.MAX_VALUE else 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(if (expanded) 10f else 5f)
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
                                "\"${task.title}\"",
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
                        "\"${task.title}\"",
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
                if (!task.photo){

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
                                } else {
                                    permissionLauncher.launch(android.Manifest.permission.CAMERA)
                                }
                            }
                            .graphicsLayer(scaleX = pulseMagnitude, scaleY = pulseMagnitude)

                    )


                } else {

                    Icon(
                        imageVector = Icons.Default.RestartAlt,
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
                                } else {
                                    permissionLauncher.launch(android.Manifest.permission.CAMERA)
                                }


                            }

                    )
                    }

            }
            if (showDeleteDialog) {
                AlertDialog(
                    modifier = Modifier.fillMaxWidth(),
                    onDismissRequest = { showDeleteDialog = false },
                    title = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Delete your task."
                            )
                        }
                    },
                    text = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Are you sure to delete task titled \"${task.title}\"?",
                                textAlign = TextAlign.Center
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                onDelete(task)
                                showDeleteDialog = false
                                Toast.makeText(context, "Task deleted successfully.", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.padding(1.dp)
                                .size(120.dp, 48.dp)
                        ) {
                            Text("Delete")
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = { showDeleteDialog = false },
                            modifier = Modifier.padding(1.dp)
                                .size(120.dp, 48.dp)
                        ) {
                            Text("Cancel")
                        }
                    }
                )
            }

            if (task.habitId == -1) {

                Icon(
                    imageVector = Icons.Sharp.Delete,
                    contentDescription = "Delete",
                    modifier = Modifier
                        .padding(end = 5.dp)
                        .clickable {
                            showDeleteDialog = true
                        },
                    tint = Color.Red.copy(0.5f)

                )

            }
        }
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



fun Context.createImageFile(
    taskId: Int
): File {

    val imageFileName = "WhatToDo#${taskId}.jpg"

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
                if (file.isFile && file.exists() && file.name.equals(fileName, ignoreCase = true)) {

                    try {
                        val deleted = file.delete()
                        if (deleted) {
                            return true
                        }
                    } catch (e: Exception) {
                        return false
                    }
                }
            }
        }
    }
    return false
}