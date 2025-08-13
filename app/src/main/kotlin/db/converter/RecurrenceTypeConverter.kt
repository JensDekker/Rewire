package com.example.rewire.db.converter

import androidx.room.TypeConverter
import com.example.rewire.core.RecurrenceType
import com.example.rewire.core.DayOfWeek
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Serializable
private data class RecurrenceTypeSurrogate(
    val type: String,
    val dayOfMonth: Int? = null,
    val weekOfMonth: Int? = null,
    val dayOfWeek: String? = null,
    val monthOffset: Int? = null,
    val daysOfWeek: List<String>? = null
)

class RecurrenceTypeConverter {
    @TypeConverter
    fun fromRecurrenceType(type: RecurrenceType): String {
        val surrogate = when (type) {
            is RecurrenceType.Daily -> RecurrenceTypeSurrogate("Daily")
            is RecurrenceType.Weekly -> RecurrenceTypeSurrogate("Weekly")
            is RecurrenceType.MonthlyByDate -> RecurrenceTypeSurrogate("MonthlyByDate", dayOfMonth = type.dayOfMonth)
            is RecurrenceType.MonthlyByWeekday -> RecurrenceTypeSurrogate("MonthlyByWeekday", weekOfMonth = type.weekOfMonth, dayOfWeek = type.dayOfWeek.name)
            is RecurrenceType.QuarterlyByDate -> RecurrenceTypeSurrogate("QuarterlyByDate", dayOfMonth = type.dayOfMonth, monthOffset = type.monthOffset)
            is RecurrenceType.QuarterlyByWeekday -> RecurrenceTypeSurrogate("QuarterlyByWeekday", weekOfMonth = type.weekOfMonth, dayOfWeek = type.dayOfWeek.name, monthOffset = type.monthOffset)
            is RecurrenceType.CustomWeekly -> RecurrenceTypeSurrogate("CustomWeekly", daysOfWeek = type.daysOfWeek.map { it.name })
        }
        return Json.encodeToString(surrogate)
    }

    @TypeConverter
    fun toRecurrenceType(data: String): RecurrenceType {
        val surrogate = Json.decodeFromString<RecurrenceTypeSurrogate>(data)
        return when (surrogate.type) {
            "Daily" -> RecurrenceType.Daily
            "Weekly" -> RecurrenceType.Weekly
            "MonthlyByDate" -> RecurrenceType.MonthlyByDate(surrogate.dayOfMonth ?: 1)
            "MonthlyByWeekday" -> RecurrenceType.MonthlyByWeekday(
                surrogate.weekOfMonth ?: 1,
                DayOfWeek.valueOf(surrogate.dayOfWeek ?: "MONDAY")
            )
            "QuarterlyByDate" -> RecurrenceType.QuarterlyByDate(
                surrogate.dayOfMonth ?: 1,
                surrogate.monthOffset ?: 0
            )
            "QuarterlyByWeekday" -> RecurrenceType.QuarterlyByWeekday(
                surrogate.weekOfMonth ?: 1,
                DayOfWeek.valueOf(surrogate.dayOfWeek ?: "MONDAY"),
                surrogate.monthOffset ?: 0
            )
            "CustomWeekly" -> RecurrenceType.CustomWeekly(
                surrogate.daysOfWeek?.map { DayOfWeek.valueOf(it) } ?: listOf(DayOfWeek.MONDAY)
            )
            else -> RecurrenceType.Daily
        }
    }
}
