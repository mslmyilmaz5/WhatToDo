package com.example.whattodo.ui.theme

import DatabaseHelper
import android.util.Log
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
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
                                    ).clip(CircleShape)
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
        Header(habits.size,"Habits",{showDialog = true})
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
                }, databaseHelper = databaseHelper,
                whatToDoNotificationService = WhatToDoNotificationService(context,"WhatToDo_Notification"))
        }
    }
}

@Composable
fun HabitItem(
    habit: Habit,
    onDelete: (Habit) -> Unit,
    modifier: Modifier = Modifier,
    databaseHelper : DatabaseHelper,
    whatToDoNotificationService: WhatToDoNotificationService
) {
    var expanded by remember { mutableStateOf(false) }
    val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    var showDialog by remember { mutableStateOf(false) }
    Log.d("Habit Days",habit.days)

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
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF1F1F1))
                .padding(15.dp)
        ) {
            Text(
                text = habit.title,
                fontFamily = FontFamily.Monospace,
                fontSize = 18.sp,
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
                .background(Color(0xFFF1F1F1))){
                Divider(modifier = Modifier
                    .padding(vertical = 8.dp))

            }

            Column(modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF1F1F1))
                .padding(start = 12.dp, bottom = 10.dp))
            {
                Text(
                    text = if(habit.reminder && habit.reminderTime != null) "Reminder: ${habit.reminderTime}" else "Reminder is off",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black)
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Habit Days")
                Spacer(modifier = Modifier.height(16.dp))
                Row {
                    daysOfWeek.forEachIndexed { index, day ->
                        var color by remember { mutableStateOf(if (habit.days[index] == '1') Color(0xffb0f7c3) else Color.Gray) }
                        Box(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(35.dp)
                                .background(
                                    color = color,
                                    shape = CircleShape
                                ).clip(CircleShape).clickable {
                                    val updatedDays = buildString {
                                        append(habit.days.substring(0, index))
                                        append(if (habit.days[index] == '1') '0' else '1')
                                        append(habit.days.substring(index + 1))
                                    }
                                    habit.days = updatedDays
                                    databaseHelper.updateHabit(habit)
                                    color = if (habit.days[index] == '1')  Color(0xffb0f7c3) else Color.Gray
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
                    Icon(
                        imageVector = Icons.Sharp.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier
                            .padding(end = 5.dp)
                            .clickable {
                                onDelete(habit)
                            },
                        tint = Color.Red

                    )
                }
            }
        }
    }
}


