package com.example.rewire.core

import java.time.LocalDate

/**
 * Represents a note attached to either a habit or an addiction.
 * @param id Optional unique identifier (for future DB use)
 * @param parentType Either "HABIT" or "ADDICTION" (or an enum if you prefer)
 * @param parentNameOrId Name or ID of the parent habit/addiction
 * @param date The date the note is associated with
 * @param text The note content
 */
data class Note(
    val id: Int? = null,
    val parentType: String, // "HABIT" or "ADDICTION"
    val parentNameOrId: String,
    val date: LocalDate,
    val text: String
)
