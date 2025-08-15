package ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun HabitCard(
	habitName: String,
	isComplete: Boolean,
	onCompletionToggle: () -> Unit,
	onAddNote: () -> Unit,
	onCardClick: () -> Unit
) {
	Surface(
		modifier = Modifier
			.fillMaxWidth()
			.padding(8.dp),
		shape = RoundedCornerShape(16.dp),
		elevation = 4.dp,
		color = Color.White
	) {
		Row(
			modifier = Modifier
				.clickable(onClick = onCardClick)
				.padding(horizontal = 16.dp, vertical = 20.dp),
			verticalAlignment = Alignment.CenterVertically
		) {
			Text(
				text = habitName,
				style = MaterialTheme.typography.h6,
				modifier = Modifier.weight(1f)
			)
			IconButton(
				onClick = {
					onCompletionToggle()
				}
			) {
				Icon(
					imageVector = if (isComplete) Icons.Filled.CheckBox else Icons.Filled.CheckBoxOutlineBlank,
					contentDescription = if (isComplete) "Mark incomplete" else "Mark complete"
				)
			}
			IconButton(
				onClick = {
					onAddNote()
				}
			) {
				Icon(
					imageVector = Icons.Filled.NoteAdd,
					contentDescription = "Add note"
				)
			}
		}
	}
}
