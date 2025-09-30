package com.example.rewire.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import com.example.rewire.ui.theme.AppShapes
import com.example.rewire.ui.theme.AppSpacing
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.Edit
import androidx.compose.ui.res.painterResource
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.runtime.*
import androidx.compose.material.OutlinedTextField
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rewire.R

@Composable
fun HabitCard(
    habitName: String,
    isComplete: Boolean,
    noteText: String,
    onNoteTextChange: (String) -> Unit,
    isNoteFieldVisible: Boolean,
    onCardClicked: () -> Unit = {},
    onCheckClicked: () -> Unit = {},
    onAddNoteClicked: () -> Unit = {},
    onEditClicked: () -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(AppSpacing.cardPadding)
            .clickable { onCardClicked() },
        shape = AppShapes.cardShape,
        color = MaterialTheme.colors.surface,
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(AppSpacing.standardRowHeight)
                    .padding(horizontal = AppSpacing.standardSpacing),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = habitName,
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.weight(1f),
                    fontSize = 20.sp
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.smallSpacing),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Edit",
                        modifier = Modifier
                            .size(28.dp)
                            .clickable { onEditClicked() }
                    )
                    
                    Icon(
                        painter = painterResource(id = R.drawable.ic_add_notes),
                        contentDescription = "Add Note",
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { onAddNoteClicked() }
                    )
                    
                    Icon(
                        imageVector = if (isComplete) Icons.Filled.CheckBox else Icons.Filled.CheckBoxOutlineBlank,
                        contentDescription = if (isComplete) "Completed" else "Incomplete",
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { onCheckClicked() }
                    )
                }
            }
            if (isNoteFieldVisible) {
                OutlinedTextField(
                    value = noteText,
                    onValueChange = onNoteTextChange,
                    label = { Text("Today's Notes") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(AppSpacing.standardSpacing)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HabitCardPreview() {
    MaterialTheme {
        var note by remember { mutableStateOf("This is today's note.") }
        var isNoteFieldVisible by remember { mutableStateOf(false) }
        HabitCard(
            habitName = "Read a Book",
            isComplete = false,
            noteText = note,
            onNoteTextChange = { note = it },
            isNoteFieldVisible = isNoteFieldVisible,
            onCardClicked = {},
            onCheckClicked = {},
            onAddNoteClicked = { isNoteFieldVisible = !isNoteFieldVisible },
            onEditClicked = {}
        )
    }
}
