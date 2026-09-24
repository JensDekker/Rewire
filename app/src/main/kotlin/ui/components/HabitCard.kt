package com.example.rewire.ui.components

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
import androidx.compose.runtime.*
import androidx.compose.material.OutlinedTextField
import androidx.compose.ui.Alignment
import androidx.compose.ui.modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import com.example.rewire.R
import com.example.rewire.core.Label
import com.example.rewire.ui.theme.AppColors

/** Seeds a note field value with the caret at the end of the text. */
private fun noteFieldValueAtEnd(text: String): TextFieldValue =
    TextFieldValue(text = text, selection = TextRange(text.length))

@Composable
fun HabitCard(
    habitName: String,
    isComplete: Boolean,
    noteText: String,
    onNoteTextChange: (String) -> Unit,
    isNoteFieldVisible: Boolean,
    labels: List<Label> = emptyList(),
    onCardClicked: () -> Unit = {},
    onCheckClicked: () -> Unit = {},
    onAddNoteClicked: () -> Unit = {},
    onEditClicked: () -> Unit = {},
    /** Called after the note draft is committed and the field should collapse. */
    onNoteDismiss: () -> Unit = {}
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    // Draft edits while the field is open; commit on dismiss (tap-outside / focus loss / note icon).
    // Selection starts at end so the caret opens after existing note text.
    var draftNote by remember { mutableStateOf(noteFieldValueAtEnd(noteText)) }
    var noteHadFocus by remember { mutableStateOf(false) }
    var dismissInProgress by remember { mutableStateOf(false) }

    fun dismissKeyboardAndFocus() {
        focusManager.clearFocus()
        keyboardController?.hide()
    }

    fun commitAndDismiss() {
        if (!isNoteFieldVisible || dismissInProgress) return
        dismissInProgress = true
        onNoteTextChange(draftNote.text)
        dismissKeyboardAndFocus()
        onNoteDismiss()
    }

    LaunchedEffect(isNoteFieldVisible, noteText) {
        if (isNoteFieldVisible) {
            draftNote = noteFieldValueAtEnd(noteText)
            dismissInProgress = false
            noteHadFocus = false
            // Request focus so tap-elsewhere / clearFocus can dismiss cleanly.
            focusRequester.requestFocus()
        } else {
            noteHadFocus = false
            dismissInProgress = false
        }
    }

    // Determine card background color from first label, or use default
    val cardBackgroundColor = if (labels.isNotEmpty()) {
        parseLabelColor(labels.first().color)
    } else {
        MaterialTheme.colors.surface  // Use MaterialTheme for default since we're in composable context
    }
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(AppSpacing.cardPadding)
            .clickable {
                if (isNoteFieldVisible) commitAndDismiss()
                onCardClicked()
            },
        shape = AppShapes.cardShape,
        color = cardBackgroundColor,
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
                            .clickable {
                                if (isNoteFieldVisible) commitAndDismiss()
                                onEditClicked()
                            }
                    )
                    
                    Icon(
                        painter = painterResource(id = R.drawable.ic_add_notes),
                        contentDescription = if (isNoteFieldVisible) "Close Note" else "Add Note",
                        modifier = Modifier
                            .size(32.dp)
                            .clickable {
                                if (isNoteFieldVisible) {
                                    // Re-tap collapses and auto-saves (same as tap-elsewhere).
                                    commitAndDismiss()
                                } else {
                                    onAddNoteClicked()
                                }
                            }
                    )
                    
                    Icon(
                        imageVector = if (isComplete) Icons.Filled.CheckBox else Icons.Filled.CheckBoxOutlineBlank,
                        contentDescription = if (isComplete) "Completed" else "Incomplete",
                        modifier = Modifier
                            .size(32.dp)
                            .clickable {
                                if (isNoteFieldVisible) commitAndDismiss()
                                onCheckClicked()
                            }
                    )
                }
            }
            
            if (isNoteFieldVisible) {
                OutlinedTextField(
                    value = draftNote,
                    onValueChange = { draftNote = it },
                    label = { Text("Today's Notes") },
                    shape = AppShapes.inputShape,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(AppSpacing.standardSpacing)
                        .focusRequester(focusRequester)
                        .onFocusChanged { focusState ->
                            if (focusState.isFocused) {
                                noteHadFocus = true
                                // Keep caret at end when the field gains focus (e.g. after requestFocus).
                                if (draftNote.selection != TextRange(draftNote.text.length)) {
                                    draftNote = draftNote.copy(
                                        selection = TextRange(draftNote.text.length)
                                    )
                                }
                            } else if (noteHadFocus && isNoteFieldVisible) {
                                // Tap-elsewhere / clearFocus: auto-save and collapse.
                                noteHadFocus = false
                                commitAndDismiss()
                            }
                        }
                )
            }
        }
    }
}

/**
 * Helper function to parse hex color string to Compose Color.
 * Returns AppColors.surface as fallback if parsing fails.
 */
private fun parseLabelColor(colorHex: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(colorHex))
    } catch (e: Exception) {
        AppColors.surface  // Fallback to default surface color
    }
}

@Preview(showBackground = true)
@Composable
fun HabitCardPreview() {
    MaterialTheme {
        var note by remember { mutableStateOf("This is today's note.") }
        var isNoteFieldVisible by remember { mutableStateOf(true) }
        HabitCard(
            habitName = "Read a Book",
            isComplete = false,
            noteText = note,
            onNoteTextChange = { note = it },
            isNoteFieldVisible = isNoteFieldVisible,
            onCardClicked = {},
            onCheckClicked = {},
            onAddNoteClicked = { isNoteFieldVisible = true },
            onEditClicked = {},
            onNoteDismiss = { isNoteFieldVisible = false }
        )
    }
}
