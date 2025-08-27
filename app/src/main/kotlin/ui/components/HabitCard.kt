package com.example.rewire.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.ui.res.painterResource
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.runtime.Composable
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
    onCardClicked: () -> Unit = {},
    onCheckClicked: () -> Unit = {},
    onAddNoteClicked: () -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 11.dp, vertical = 8.dp)
            .clickable { onCardClicked() },
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colors.surface,
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = habitName,
                style = MaterialTheme.typography.h6,
                modifier = Modifier.weight(1f),
                fontSize = 20.sp
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_add_notes),
                contentDescription = "Add Note",
                modifier = Modifier
                    .size(32.dp)
                    .clickable { onAddNoteClicked() }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Icon(
                imageVector = if (isComplete) Icons.Filled.CheckBox else Icons.Filled.CheckBoxOutlineBlank,
                contentDescription = if (isComplete) "Completed" else "Incomplete",
                modifier = Modifier
                    .size(32.dp)
                    .clickable { onCheckClicked() }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HabitCardPreview() {
    HabitCard(
        habitName = "Read a Book",
        isComplete = false,
        onCardClicked = {},
        onCheckClicked = {},
        onAddNoteClicked = {}
    )
}
