package com.example.whattodo.ui.theme

import DatabaseHelper
import android.app.TimePickerDialog
import android.content.Intent
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddTask
import androidx.compose.material.icons.filled.AlarmOff
import androidx.compose.material.icons.filled.AlarmOn
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.sharp.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whattodo.HabitsActivity
import com.example.whattodo.MainActivity
import com.example.whattodo.WhatToDoNotificationService
import com.example.whattodo.model.Habit

import com.example.whattodo.model.Task
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
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

                    // Adjust the layout here to ensure proper visibility of components
                    if (reminder) {
                        Spacer(modifier = Modifier.height(16.dp))
                        TimePickerDialogComponent(
                            onTimeSelected = { reminderTime = it }
                        )


                    } else {
                        // Add an empty box to reserve space when reminder is false
                        Spacer(modifier = Modifier.height(50.dp))
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
            })
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
            .padding(10.dp)
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
    var photoString by remember { mutableStateOf("") }

    Card(
        modifier = modifier.padding(bottom = 20.dp)
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


                val infiniteTransition = rememberInfiniteTransition()

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
                        .clickable { { /*TODO*/ } }
                        .graphicsLayer(scaleX = pulseMagnitude, scaleY = pulseMagnitude)
                )

            }

            Icon(
                imageVector = Icons.Default.ExpandMore,
                tint = if (expanded) Color.Black else Color.Gray,
                contentDescription = "Expand Status",
                modifier = Modifier
                    .padding(end = 5.dp)
                    .clickable { expanded = !expanded }
            )
        }

        if (task.isDone) photoString = "Wonderful! Take a selfie!" else photoString = ""

        if(expanded){
            Box(modifier = Modifier
                .background(cardColor)){
                Divider(modifier = Modifier
                    .padding(vertical = 8.dp))

            }

            Column(modifier = Modifier
                .fillMaxWidth()
                .background(cardColor)
                .padding(start = 12.dp, bottom = 10.dp))
            {
                Text(
                    text = if(task.reminder) "Reminder: ${task.reminderTime}" else "Reminder is off",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black)

                Row{
                    Text(
                        text = photoString,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black)
                    Spacer(modifier = Modifier.weight(1f))
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