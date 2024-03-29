package com.example.whattodo.ui.theme

import DatabaseHelper
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlarmOff
import androidx.compose.material.icons.filled.AlarmOn
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.sharp.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whattodo.WhatToDoNotificationService
import com.example.whattodo.model.Habit
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun WhatToDoAppHabit(databaseHelper: DatabaseHelper,
                     whatToDoNotificationService: WhatToDoNotificationService,
                     modifier: Modifier = Modifier) {
    var showDialog by remember { mutableStateOf(false) }
    var habitTitle by remember { mutableStateOf(TextFieldValue()) }
    var reminder by remember { mutableStateOf(false) }
    var reminderTime by remember { mutableStateOf<String?>(null) }
    val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    var selectedDays by remember {
        mutableStateOf(List(daysOfWeek.size) { false })
    }
    var notificationId by remember { mutableStateOf(Random.nextInt()) }
    var habits by remember { mutableStateOf(databaseHelper.getAllHabits().toMutableList()) }
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
                        val days = selectedDays.map { if (it) '1' else '0' }.joinToString(separator = "")
                        if(reminderTime == null) reminder = false
                        val habit = Habit(Random.nextInt(),habitTitle.text,reminder,reminderTime,days,notificationId)
                        habit.id = databaseHelper.addHabit(habit).toInt()
                        habits.add(habit)
                        showDialog = false
                        habitTitle = TextFieldValue() // Reset form state
                        reminder = false // Reset reminder state
                        reminderTime = null // Reset selected time state
                        notificationId = Random.nextInt()
                        selectedDays = List(daysOfWeek.size) { false }
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
                        selectedDays = List(daysOfWeek.size) { false }
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
                    Text(text = "Select Days")
                    Spacer(modifier = Modifier.height(16.dp))
                    Row {
                        daysOfWeek.forEachIndexed { index, day ->
                            Box(
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .size(30.dp)
                                    .background(
                                        color = if (selectedDays[index]) Color(0xffb0f7c3) else Color.Gray,
                                        shape = CircleShape
                                    )
                                    .clip(CircleShape)
                                    .clickable {
                                        selectedDays = selectedDays
                                            .toMutableList()
                                            .also {
                                                it[index] = !it[index]
                                            }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = day.first().toString(),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
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
        Header({showDialog = true})
        Box(
            modifier = Modifier
                .padding(start = 10.dp, end = 10.dp)
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
            HabitContent( habits,{ deletedHabit ->
                whatToDoNotificationService.cancelNotification(deletedHabit.notificationId)
                habits = habits.filterNot { it == deletedHabit }.toMutableList()
                databaseHelper.deleteHabit(deletedHabit.id)
                databaseHelper.deleteTaskByHabitId(deletedHabit.id)
            },databaseHelper = databaseHelper)
        }
        Navbar(1)
    }
}
@Composable
fun HabitContent(habitList : List<Habit>,
                 onDeleteHabit : (deletedHabit: Habit) -> Unit,
                 modifier: Modifier = Modifier,databaseHelper: DatabaseHelper
){
    val context = LocalContext.current
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
                }, databaseHelper = databaseHelper,)
        }
    }
}
@Composable
fun HabitItem(
    habit: Habit,
    onDelete: (Habit) -> Unit,
    modifier: Modifier = Modifier,
    databaseHelper : DatabaseHelper,

) {
    var expanded by remember { mutableStateOf(false) }
    val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    var showDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

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
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFCFCCCC))
                .padding(12.dp)
        ) {

            Spacer(modifier = Modifier.width(8.dp))
            val title = habit.title
            val reminder = if (habit.reminder && habit.reminderTime != null) {
                AnnotatedString(" (${habit.reminderTime.toString()})", spanStyle = SpanStyle(color = Color.Red, fontWeight = FontWeight.Normal))
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
                modifier =  Modifier
                    .weight(if (expanded) 10f else 5f)
            )
            Spacer(modifier = Modifier.weight(1f))

            var AlarmIcon by remember {mutableStateOf(Icons.Default.AlarmOff)}
            if (habit.reminder) AlarmIcon = Icons.Default.AlarmOn

            Icon(
                imageVector = AlarmIcon,
                tint = Color.Black,
                contentDescription = "Reminder Status",
                modifier = Modifier
                    .padding(end = 5.dp)
                    .clickable {
                        if (!habit.reminder && habit.reminderTime == null) {
                            showDialog = true
                        } else if (!habit.reminder && habit.reminderTime != null) {
                            AlarmIcon = Icons.Default.AlarmOn
                        } else {
                            AlarmIcon = Icons.Default.AlarmOff
                        }
                        habit.reminder = !habit.reminder
                        databaseHelper.updateHabit(habit)
                    }
            )
            ReminderDialog(
                showDialog = showDialog,
                onDialogDismiss = { showDialog = false },
                onReminderSet = { reminderTime ->
                    val reminderSet = reminderTime != null
                    AlarmIcon = if (reminderSet)Icons.Default.AlarmOn else Icons.Default.AlarmOff
                    habit.reminder = reminderSet
                    habit.reminderTime = reminderTime
                    showDialog = false
                    databaseHelper.updateHabit(habit)
                }
            )

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
                                text = "Delete your habit."
                            )
                        }
                    },
                    text = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Are you sure to delete habit titled \"${habit.title}\"?",
                                textAlign = TextAlign.Center
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                onDelete(habit)
                                showDeleteDialog = false
                                Toast.makeText(context, "Habit deleted successfully.", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .padding(1.dp)
                                .size(120.dp, 48.dp)
                        ) {
                            Text("Delete")
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = { showDeleteDialog = false },
                            modifier = Modifier
                                .padding(1.dp)
                                .size(120.dp, 48.dp)
                        ) {
                            Text("Cancel")
                        }
                    }
                )
            }

                Icon(
                    imageVector = Icons.Sharp.Delete,
                    contentDescription = "Delete",
                    modifier = Modifier
                        .padding(end = 5.dp)
                        .clickable {
                            showDeleteDialog = true
                        },
                    tint = Color.Red.copy(0.7f)

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
                Box(modifier = Modifier
                    .background(Color(0xFFCFCCCC))){
                    Divider(modifier = Modifier
                        .padding(vertical = 8.dp))

                }

                Column(modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFCFCCCC))
                    .padding(start = 12.dp, bottom = 10.dp))
                {

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Adjust your habbits.",
                        color = Color.Black,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(5.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row {
                        daysOfWeek.forEachIndexed { index, day ->
                            var color by remember { mutableStateOf(if (habit.days[index] == '1') Color(0xffb0f7c3) else Color.Gray) }
                            Box(
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .size(32.dp)
                                    .background(
                                        color = color,
                                        shape = CircleShape
                                    )
                                    .clip(CircleShape)
                                    .clickable {
                                        val updatedDays = buildString {
                                            append(habit.days.substring(0, index))
                                            append(if (habit.days[index] == '1') '0' else '1')
                                            append(habit.days.substring(index + 1))
                                        }
                                        habit.days = updatedDays
                                        databaseHelper.updateHabit(habit)
                                        color =
                                            if (habit.days[index] == '1') Color(0xffb0f7c3) else Color.Gray
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = day.first().toString(),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()){

                    }
                }
            }
    }
}


