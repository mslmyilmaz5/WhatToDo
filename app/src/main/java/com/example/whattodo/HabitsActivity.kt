package com.example.whattodo

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
import androidx.compose.foundation.layout.windowInsetsEndWidth
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whattodo.ui.theme.WhatToDoTheme
import java.util.Calendar
import kotlin.random.Random

class HabitsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WhatToDoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Habits(generateHabitList(5))
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Habits(habitList: List<HabitView>, modifier: Modifier = Modifier) {
    var showDialog by remember { mutableStateOf(false) }
    var habitTitle by remember { mutableStateOf(TextFieldValue()) }
    var reminder by remember { mutableStateOf(false) }
    var reminderTime by remember { mutableStateOf<String?>(null) }
    var habits by remember { mutableStateOf<MutableList<HabitView>>(habitList.toMutableList()) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
                habitTitle = TextFieldValue() // Reset form state
                reminder = false // Reset reminder state
                reminderTime = null // Reset selected time state
            },
            title = {
                Text(text = "Add New Habit")
            },
            confirmButton = {
                Button(
                    onClick = {
                        habits.add(HabitView(Random.nextInt(),habitTitle.text,reminder,reminderTime))
                        showDialog = false
                        habitTitle = TextFieldValue() // Reset form state
                        reminder = false // Reset reminder state
                        reminderTime = null // Reset selected time state
                    }
                ) {
                    Text(text = "Add")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDialog = false
                        habitTitle = TextFieldValue() // Reset form state
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
                        value = habitTitle,
                        onValueChange = { habitTitle = it },
                        label = { Text("Habit Name") },
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
                            selectedTime = reminderTime,
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
        Header("Habits",5,"Habits",{showDialog = true})
        Box(
            modifier = Modifier
                .weight(1f)
        ) {
            HabitContent( habits,{ deletedHabit ->
                habits = habits.filterNot { it == deletedHabit }.toMutableList()
            })
        }
        Navbar(1)
    }
}

@Composable
fun HabitContent(habitList : List<HabitView>,
                 onDeleteHabit : (deletedHabit:HabitView) -> Unit,
                 modifier: Modifier = Modifier){
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        items(habitList) { habit ->
            HabitItem(
                habit = habit,
                onDelete = { deletedHabit ->
                    onDeleteHabit(deletedHabit)
                })
        }
    }
}

@Composable
fun HabitItem(
    habit: HabitView,
    onDelete: (HabitView) -> Unit,
    modifier: Modifier = Modifier
) {
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
            Text(
                text = habit.title,
                fontFamily = FontFamily.Serif,
                fontSize = 18.sp,
                modifier = Modifier.padding(start = 20.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                modifier = Modifier
                    .padding(end = 5.dp)
                    .clickable { onDelete(habit) }
            )
        }
    }
}

fun generateHabitList(size: Int): List<HabitView> {
    var id = 0
    val titles = listOf("Daily Workout", "Meeting", "Study Session", "Walk the Dog", "Shopping")
    return List(size) {
        val titleIndex = Random.nextInt(titles.size)
        HabitView(
            id = id++,
            title = titles[titleIndex],
            reminder = Random.nextBoolean(),
            reminderTime = "10.00"
        )
    }
}
data class HabitView(
    val id: Int,
    val title : String,
    val reminder : Boolean,
    val reminderTime : String?
)