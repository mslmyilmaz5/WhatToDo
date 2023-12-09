package com.example.whattodo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whattodo.ui.theme.WhatToDoTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WhatToDoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WhatToDoApp()
                }
            }
        }
    }
}

@Composable
fun WhatToDoApp(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Header("Today",8,"Tasks")
        Box(
            modifier = Modifier
                .weight(1f)
        ) {
            Content(generateTaskList(15))
        }
        Navbar(0)
    }
}
//Header is also used in HabitsActivity
@Composable
fun Header(title: String,count : Int,tasksOrHabits : String,modifier: Modifier = Modifier){
    Column(Modifier.background(Color(0xFF4044C9))) {
        Row{
            Text(
                text = "WhatToDo",
                color = Color.White,
                fontSize = 20.sp,
                fontFamily = FontFamily.Serif,
                modifier = Modifier
                    .weight(1f)
                    .padding(20.dp))
            Text(
                text = "5 May",
                color = Color.White,
                textAlign = TextAlign.Right,
                fontFamily = FontFamily.Serif,
                fontSize = 20.sp,
                modifier = Modifier
                    .weight(1f)
                    .padding(20.dp))
        }
        Row(verticalAlignment = Alignment.Bottom){
            Column(modifier = Modifier
                .weight(1f)
                .padding(start = 20.dp, bottom = 20.dp)){
                Text(
                    text = "$title",
                    color = Color.White,
                    fontFamily = FontFamily.Serif,
                    fontSize = 30.sp)
                Text(
                    text = "$count $tasksOrHabits",
                    color = Color(0xFFA9A9A9),
                    modifier = Modifier.padding(start = 10.dp))
            }
            Button(
                onClick = { /*TODO*/ },
                shape = RoundedCornerShape(size=15.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF4044C9)),
                    modifier = Modifier
                        .weight(0.7f)
                        .padding(end = 20.dp, bottom = 20.dp)
                        .height(IntrinsicSize.Min)) {
                Text(
                    text = "Add New",
                    fontSize = 18.sp
                )
            }
        }
    }
}
@Composable
fun Content(taskList : List<TaskView>,modifier: Modifier = Modifier){
    var tasks by remember { mutableStateOf(taskList) }
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth().padding(20.dp)
    ) {
        items(tasks) { task ->
            TaskItem(
                task = task,
                onDelete = { deletedTask ->
                    tasks = tasks.filterNot { it == deletedTask }
                }
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
                containerColor = Color.White,
                contentColor = Color(0xFF4044C9)
            ),
            modifier = Modifier
                .weight(1f)
                .padding(start = 20.dp, end = 15.dp)
                .height(50.dp)
        ) {
            Row{
                Icon(
                    imageVector = Icons.Default.TaskAlt,
                    contentDescription = "Tasks",
                    Modifier.padding(end = 5.dp)
                )
                Text(
                    text = "TASKS",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    style = TextStyle(
                        textDecoration = if (focus == 0) TextDecoration.Underline else TextDecoration.None
                    )
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
                containerColor = Color.White,
                contentColor = Color(0xFF4044C9)
            ),
            modifier = Modifier
                .weight(1f)
                .padding(start = 15.dp, end = 20.dp)
                .height(50.dp)
        ) {
            Row{
                Icon(
                    imageVector = Icons.Default.Checklist,
                    contentDescription = "Habits",
                    Modifier.padding(end = 5.dp)
                )
                Text(
                    text = "HABITS",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    style = TextStyle(
                        textDecoration = if (focus != 0) TextDecoration.Underline else TextDecoration.None
                    )
                )
            }
        }
    }
}

@Composable
fun TaskItem(
    task: TaskView,
    onDelete: (TaskView) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        modifier = modifier.padding(bottom = 20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF1F1F1))
                .padding(top = 10.dp, bottom = 10.dp)
        ) {
            Checkbox(checked = task.isDone, onCheckedChange = { /*TODO*/ })
            Text(
                text = task.title,
                fontFamily = FontFamily.Serif,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.Alarm,
                tint = if (task.reminder) Color.Black else Color.Gray,
                contentDescription = "Reminder Status",
                modifier = Modifier
                    .padding(end = 5.dp)
                    .clickable { { /*TODO*/ } }
            )
            Icon(
                imageVector = Icons.Default.CameraAlt,
                tint = if (task.photo) Color.Black else Color.Gray,
                contentDescription = "Photograph",
                modifier = Modifier
                    .padding(end = 5.dp)
                    .clickable { { /*TODO*/ } }
            )
            Icon(
                imageVector = Icons.Default.ExpandMore,
                tint = if (expanded) Color.Black else Color.Gray,
                contentDescription = "Expand Status",
                modifier = Modifier
                    .padding(end = 5.dp)
                    .clickable { expanded = !expanded }
            )
        }
        if(expanded){
            Column(modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF1F1F1))
                .padding(start = 48.dp, bottom = 10.dp))
            {
                Text(
                    text = "Reminder: ${task.reminderTime}",
                    fontFamily = FontFamily.Serif)
                Row{
                    Text(
                        text = "Photo: ",
                        fontFamily = FontFamily.Serif)
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier
                            .padding(end = 5.dp)
                            .clickable { onDelete(task) }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WhatToDoAppPreview() {
    WhatToDoTheme {
        WhatToDoApp()
    }
}

fun generateTaskList(size: Int): List<TaskView> {
    val titles = listOf("Daily Workout", "Meeting", "Study Session", "Walk the Dog", "Shopping")
    val times = listOf("10:00", "11:00", "14:30", "16:00", "18:45")
    var id = 0
    return List(size) {
        val titleIndex = Random.nextInt(titles.size)
        val timeIndex = Random.nextInt(times.size)
        TaskView(
            id = id++,
            title = titles[titleIndex],
            isDone = Random.nextBoolean(),
            reminder = Random.nextBoolean(),
            reminderTime = times[timeIndex],
            photo = Random.nextBoolean()
        )
    }
}
data class TaskView(
    val id : Int,
    val title : String,
    val isDone : Boolean,
    val reminder : Boolean,
    val reminderTime : String,
    val photo : Boolean
)