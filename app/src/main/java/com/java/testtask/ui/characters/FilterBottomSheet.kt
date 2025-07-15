package com.java.testtask.ui.characters

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FilterBottomSheet(
    currentFilters: CharacterFilters,
    onApplyFilters: (CharacterFilters) -> Unit,
    onDismiss: () -> Unit
) {
    var tempStatus by remember(currentFilters.status) { mutableStateOf(currentFilters.status) }
    var tempGender by remember(currentFilters.gender) { mutableStateOf(currentFilters.gender) }
    var tempSpecies by remember(currentFilters.species) { mutableStateOf(currentFilters.species) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Filters", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))

        Text("Status", style = MaterialTheme.typography.titleMedium)
        RadioGroup(
            options = listOf("Alive", "Dead", "unknown"),
            selectedOption = tempStatus,
            onOptionSelected = { tempStatus = it }
        )
        Spacer(Modifier.height(16.dp))

        Text("Gender", style = MaterialTheme.typography.titleMedium)
        RadioGroup(
            options = listOf("Female", "Male", "Genderless", "unknown"),
            selectedOption = tempGender,
            onOptionSelected = { tempGender = it }
        )
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = tempSpecies,
            onValueChange = { tempSpecies = it },
            label = { Text("Species (e.g., Human, Alien)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = {
                // Сброс фильтров
                onApplyFilters(CharacterFilters())
                onDismiss()
            }) {
                Text("Reset")
            }
            Spacer(Modifier.width(8.dp))
            Button(onClick = {
                val newFilters = CharacterFilters(
                    status = tempStatus,
                    gender = tempGender,
                    species = tempSpecies
                )
                onApplyFilters(newFilters)
                onDismiss()
            }) {
                Text("Apply")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RadioGroup(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    Column {
        val allOptions = listOf("") + options

        allOptions.forEach { text ->
            val label = if (text.isEmpty()) "Any" else text
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (text == selectedOption),
                    onClick = { onOptionSelected(text) }
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}