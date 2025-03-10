package com.example.simpledictionaryapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SimpleDictionaryApp(context = this)
        }
    }
}

@Composable
fun SimpleDictionaryApp(context: ComponentActivity) {
    var macedonianWord by remember { mutableStateOf("") }
    var englishWord by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf(listOf<String>()) }
    val dictionary = remember { mutableStateMapOf<String, String>() }

    // Load the dictionary from the raw resource
    LaunchedEffect(Unit) {
        val file = File(context.filesDir, "dictionary.txt")
        if (!file.exists()) {
            // If the file doesn't exist, create it and add initial entries
            file.createNewFile()
            file.writeText("здраво=hello\nкога=when\nавтомобил=car\n")
        }

        // Load the dictionary from the file
        dictionary.clear()
        file.forEachLine {
            val parts = it.split("=")
            if (parts.size == 2) {
                dictionary[parts[0].trim().lowercase()] = parts[1].trim().lowercase()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color(0xFFF5F5F5))
    ) {
        Text(
            text = "Macedonian-English Dictionary",
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFF00796B),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Input fields for Macedonian and English words
        OutlinedTextField(
            value = macedonianWord,
            onValueChange = { macedonianWord = it },
            label = { Text("Macedonian Word") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = englishWord,
            onValueChange = { englishWord = it },
            label = { Text("English Word") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )

        // Save button to add new words to the dictionary
        Button(
            onClick = {
                if (macedonianWord.isNotEmpty() && englishWord.isNotEmpty()) {
                    dictionary[macedonianWord.lowercase()] = englishWord.lowercase()
                    val file = File(context.filesDir, "dictionary.txt")
                    file.appendText("${macedonianWord.lowercase()}=${englishWord.lowercase()}\n")
                    macedonianWord = ""
                    englishWord = ""
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00796B)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("Save", color = Color.White)
        }

        // Search field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search Word") },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        )

        // Search button
        Button(
            onClick = {
                searchResults = if (searchQuery.isNotEmpty()) {
                    val query = searchQuery.trim().lowercase()
                    if (dictionary.containsKey(query)) {
                        listOf("$query → ${dictionary[query]}")
                    } else if (dictionary.containsValue(query)) {
                        val result = dictionary.entries.find { it.value == query }?.key
                        listOf("$result → $query")
                    } else {
                        listOf("Word not found.")
                    }
                } else {
                    emptyList()
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00796B)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text("Search", color = Color.White)
        }

        // Remove button
        Button(
            onClick = {
                if (searchQuery.isNotEmpty()) {
                    val query = searchQuery.trim().lowercase()
                    if (dictionary.containsKey(query)) {
                        dictionary.remove(query)
                        val file = File(context.filesDir, "dictionary.txt")
                        file.writeText(dictionary.entries.joinToString("\n") { "${it.key}=${it.value}" })
                        searchQuery = ""
                        searchResults = emptyList()
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFD32F2F) // Red color for the remove button
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text("Remove Word", color = Color.White)
        }

        // Display search results
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            items(searchResults) { result ->
                Text(
                    text = result,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF00796B),
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}