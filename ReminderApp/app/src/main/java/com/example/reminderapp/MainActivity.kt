package com.example.reminderapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.reminderapp.ui.theme.ReminderAppTheme
import java.text.SimpleDateFormat
import java.util.*

data class Reminder(val message: String, val date: String, val time: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ReminderAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ReminderApp(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderApp(modifier: Modifier = Modifier) {
    var reminderMessage by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }
    var remindersList by remember { mutableStateOf(listOf<Reminder>()) }
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }

    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current // Used for dialogs

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }, // Pass SnackbarHostState
        modifier = modifier,
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // TextField for entering reminder message
                TextField(
                    value = reminderMessage,
                    onValueChange = { reminderMessage = it },
                    label = { Text("Enter Reminder Message") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Button to select date
                Button(onClick = {
                    DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            calendar.set(year, month, dayOfMonth)
                            selectedDate = dateFormat.format(calendar.time)
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }) {
                    Text("Select Date")
                }
                Text(text = "Selected Date: $selectedDate")
                Spacer(modifier = Modifier.height(16.dp))

                // Button to select time
                Button(onClick = {
                    TimePickerDialog(
                        context,
                        { _, hourOfDay, minute ->
                            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                            calendar.set(Calendar.MINUTE, minute)
                            selectedTime = timeFormat.format(calendar.time)
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true
                    ).show()
                }) {
                    Text("Select Time")
                }
                Text(text = "Selected Time: $selectedTime")
                Spacer(modifier = Modifier.height(16.dp))

                // Button to set reminder
                Button(onClick = {
                    if (reminderMessage.isNotEmpty() && selectedDate.isNotEmpty() && selectedTime.isNotEmpty()) {
                        remindersList = remindersList + Reminder(reminderMessage, selectedDate, selectedTime)
                        snackbarMessage = "Reminder set for $selectedDate at $selectedTime"
                        showSnackbar = true
                    }
                }) {
                    Text("Set Reminder")
                }

                // Button to clear all reminders
                Button(onClick = {
                    remindersList = listOf()
                    snackbarMessage = "All reminders cleared"
                    showSnackbar = true
                }) {
                    Text("Clear All Reminders")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // LazyColumn to display all reminders, with scrolling enabled
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f) // Take up remaining space for scrolling
                ) {
                    items(remindersList) { reminder ->
                        ReminderCard(reminder)
                        Spacer(modifier = Modifier.height(8.dp)) // Space between reminders
                    }
                }

                // Show Snackbar when reminder is set or cleared
                if (showSnackbar) {
                    LaunchedEffect(snackbarHostState) {
                        snackbarHostState.showSnackbar(snackbarMessage)
                        showSnackbar = false
                    }
                }
            }
        }
    )
}

@Composable
fun ReminderCard(reminder: Reminder) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Message: ${reminder.message}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Date: ${reminder.date}", style = MaterialTheme.typography.bodySmall)
            Text(text = "Time: ${reminder.time}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReminderAppPreview() {
    ReminderAppTheme {
        ReminderApp()
    }
}
