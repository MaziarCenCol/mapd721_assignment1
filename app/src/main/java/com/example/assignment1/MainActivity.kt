package com.example.assignment1

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

val Context.dataStore by preferencesDataStore(name = "user_prefs")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AssignmentScreen(context = this)
        }
    }
}

@Composable
fun AssignmentScreen(context: Context) {
    val coroutineScope = rememberCoroutineScope()

    var id by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var courseName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            "MAPD721 Android Assignment 1",
            fontSize = 20.sp,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(20.dp))

        // Input Fields
        InputField("ID", id) { id = it }
        InputField("User Name", username) { username = it }
        InputField("Course Name", courseName) { courseName = it }

        Spacer(modifier = Modifier.height(20.dp))

        // Buttons
        Row {
            Button(
                onClick = {
                    coroutineScope.launch {
                        loadData(context) { loadedId, loadedUsername, loadedCourse ->
                            id = loadedId
                            username = loadedUsername
                            courseName = loadedCourse
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
                modifier = Modifier.width(120.dp)
            ) {
                Text("Load", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    coroutineScope.launch {
                        saveData(context, id, username, courseName)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                modifier = Modifier.width(120.dp)
            ) {
                Text("Store", fontSize = 18.sp)
            }

        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                id = ""
                username = ""
                courseName = ""
                coroutineScope.launch {
                    clearData(context)
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500)), // Orange
            modifier = Modifier.width(240.dp) // 2x Width
        ) {
            Text("Reset", fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(32.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Maziar Hassanzadeh",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "301064337",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
fun InputField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column {
        Text(label, fontSize = 16.sp, color = Color.Gray)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

// Save Data
suspend fun saveData(context: Context, id: String, username: String, courseName: String) {
    val dataStore = context.dataStore
    Toast.makeText(context, "Data Saving .....", Toast.LENGTH_SHORT).show()
    dataStore.edit { settings ->
        Toast.makeText(context, "Data Saved Successfully", Toast.LENGTH_SHORT).show()
        settings[stringPreferencesKey("id")] = id
        settings[stringPreferencesKey("username")] = username
        settings[stringPreferencesKey("courseName")] = courseName
    }
}

// Load Data
suspend fun loadData(context: Context, onLoad: (String, String, String) -> Unit) {
    val dataStore = context.dataStore
    val preferences = dataStore.data.first()
    val id = preferences[stringPreferencesKey("id")] ?: ""
    val username = preferences[stringPreferencesKey("username")] ?: ""
    val courseName = preferences[stringPreferencesKey("courseName")] ?: ""
    onLoad(id, username, courseName)
}

// Clear Data
suspend fun clearData(context: Context) {
    val dataStore = context.dataStore
    dataStore.edit { it.clear() }
}
