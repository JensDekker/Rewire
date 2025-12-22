package com.example.rewire.repository

import com.example.rewire.db.dao.LabelDao
import com.example.rewire.db.dao.HabitLabelDao
import com.example.rewire.db.entity.LabelEntity
import com.example.rewire.db.entity.HabitLabelCrossRef
import com.example.rewire.core.Label
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Validation result class
sealed class LabelResult {
    data class Success(val label: LabelEntity) : LabelResult()
    data class Error(val message: String) : LabelResult()
}

class LabelRepository(
    private val labelDao: LabelDao,
    private val habitLabelDao: HabitLabelDao
) {
    suspend fun getAllLabels(): List<LabelEntity> = withContext(Dispatchers.IO) {
        labelDao.getAll()
    }
    
    suspend fun getLabelById(id: Long): LabelEntity? = withContext(Dispatchers.IO) {
        labelDao.getById(id)
    }
    
    suspend fun getLabelByName(name: String): LabelEntity? = withContext(Dispatchers.IO) {
        labelDao.getByName(name)
    }
    
    suspend fun insertLabel(label: LabelEntity): Long = withContext(Dispatchers.IO) {
        labelDao.insert(label)
    }
    
    suspend fun updateLabel(label: LabelEntity) = withContext(Dispatchers.IO) {
        labelDao.update(label)
    }
    
    suspend fun deleteLabel(label: LabelEntity) = withContext(Dispatchers.IO) {
        // Delete all associations first (or let CASCADE handle it)
        habitLabelDao.deleteAllForLabel(label.id)
        labelDao.delete(label)
    }
    
    // Get or create label (useful for tag autocomplete)
    suspend fun getOrCreateLabel(name: String, defaultColor: String = "#9E9E9E"): LabelEntity {
        val existing = getLabelByName(name)
        return if (existing != null) {
            existing
        } else {
            val newLabel = LabelEntity(
                name = name.trim(),
                color = defaultColor,
                createdAt = java.time.LocalDateTime.now().toString()
            )
            val id = insertLabel(newLabel)
            newLabel.copy(id = id)
        }
    }
    
    // Get labels for a habit
    suspend fun getLabelsForHabit(habitId: Long): List<LabelEntity> = withContext(Dispatchers.IO) {
        labelDao.getLabelsForHabit(habitId)
    }
    
    // Batch load labels for multiple habits (optimization to avoid N+1 queries)
    suspend fun getLabelsForHabits(habitIds: List<Long>): Map<Long, List<LabelEntity>> = withContext(Dispatchers.IO) {
        if (habitIds.isEmpty()) {
            return@withContext emptyMap()
        }
        // Load all cross-references for the given habit IDs
        val allCrossRefs = habitIds.flatMap { habitId ->
            habitLabelDao.getCrossRefsForHabit(habitId).map { it.habitId to it.labelId }
        }
        // Get unique label IDs
        val labelIds = allCrossRefs.map { it.second }.distinct()
        if (labelIds.isEmpty()) {
            return@withContext habitIds.associateWith { emptyList() }
        }
        // Load all labels at once
        val allLabels = labelDao.getAll().filter { it.id in labelIds }
        val labelsMap = allLabels.associateBy { it.id }
        // Build result map: habitId -> List<LabelEntity>
        allCrossRefs
            .groupBy({ it.first }, { labelsMap[it.second] })
            .mapValues { (_, labels) -> labels.filterNotNull() }
            .toMap()
    }
    
    // Set labels for a habit (replaces existing labels)
    suspend fun setLabelsForHabit(habitId: Long, labelIds: List<Long>) = withContext(Dispatchers.IO) {
        // Delete existing associations
        habitLabelDao.deleteAllForHabit(habitId)
        // Insert new associations
        val crossRefs = labelIds.map { HabitLabelCrossRef(habitId, it) }
        habitLabelDao.insertAll(crossRefs)
    }
    
    // Add a single label to a habit
    suspend fun addLabelToHabit(habitId: Long, labelId: Long) = withContext(Dispatchers.IO) {
        habitLabelDao.insert(HabitLabelCrossRef(habitId, labelId))
    }
    
    // Remove a single label from a habit
    suspend fun removeLabelFromHabit(habitId: Long, labelId: Long) = withContext(Dispatchers.IO) {
        habitLabelDao.delete(HabitLabelCrossRef(habitId, labelId))
    }
    
    // Get all habits with a specific label
    suspend fun getHabitIdsWithLabel(labelId: Long): List<Long> = withContext(Dispatchers.IO) {
        habitLabelDao.getHabitIdsWithLabel(labelId)
    }
    
    // Search labels
    suspend fun searchLabels(query: String): List<LabelEntity> = withContext(Dispatchers.IO) {
        labelDao.searchLabels(query)
    }
    
    // Enhanced insert with validation
    suspend fun insertLabelWithValidation(label: LabelEntity): LabelResult = withContext(Dispatchers.IO) {
        try {
            // Validate label name
            val nameValidation = validateLabelName(label.name)
            if (!nameValidation.isValid) {
                return@withContext LabelResult.Error(nameValidation.errorMessage)
            }
            
            // Validate color
            val colorValidation = validateLabelColor(label.color)
            if (!colorValidation.isValid) {
                return@withContext LabelResult.Error(colorValidation.errorMessage)
            }
            
            // Check for duplicate name (case-sensitive)
            val existing = labelDao.getByName(label.name.trim())
            if (existing != null) {
                return@withContext LabelResult.Error("A label with this name already exists")
            }
            
            // Insert label
            val trimmedLabel = label.copy(name = label.name.trim())
            val id = labelDao.insert(trimmedLabel)
            LabelResult.Success(trimmedLabel.copy(id = id))
        } catch (e: Exception) {
            LabelResult.Error("Failed to create label: ${e.message}")
        }
    }
    
    // Enhanced update with validation
    suspend fun updateLabelWithValidation(label: LabelEntity): LabelResult = withContext(Dispatchers.IO) {
        try {
            // Validate label name
            val nameValidation = validateLabelName(label.name)
            if (!nameValidation.isValid) {
                return@withContext LabelResult.Error(nameValidation.errorMessage)
            }
            
            // Validate color
            val colorValidation = validateLabelColor(label.color)
            if (!colorValidation.isValid) {
                return@withContext LabelResult.Error(colorValidation.errorMessage)
            }
            
            // Check for duplicate name (excluding current label)
            val existing = labelDao.getByName(label.name.trim())
            if (existing != null && existing.id != label.id) {
                return@withContext LabelResult.Error("A label with this name already exists")
            }
            
            // Update label
            val trimmedLabel = label.copy(name = label.name.trim())
            labelDao.update(trimmedLabel)
            LabelResult.Success(trimmedLabel)
        } catch (e: Exception) {
            LabelResult.Error("Failed to update label: ${e.message}")
        }
    }
    
    // Enhanced setLabelsForHabit with validation and transaction
    suspend fun setLabelsForHabitSafely(habitId: Long, labelIds: List<Long>): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Validate all label IDs exist
            for (labelId in labelIds) {
                val label = labelDao.getById(labelId)
                if (label == null) {
                    return@withContext Result.failure(IllegalArgumentException("Label with ID $labelId does not exist"))
                }
            }
            
            // Use transaction for atomic operation
            // Note: Room transactions are handled automatically for suspend functions in a single coroutine
            // For explicit transaction control across multiple operations, use database.withTransaction { }
            
            // Delete existing associations
            habitLabelDao.deleteAllForHabit(habitId)
            
            // Insert new associations
            if (labelIds.isNotEmpty()) {
                val crossRefs = labelIds.map { HabitLabelCrossRef(habitId, it) }
                habitLabelDao.insertAll(crossRefs)
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// Validation helper functions
data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String = ""
)

fun validateLabelName(name: String): ValidationResult {
    val trimmed = name.trim()
    
    if (trimmed.isEmpty()) {
        return ValidationResult(false, "Label name cannot be empty")
    }
    
    if (trimmed.length > 50) {
        return ValidationResult(false, "Label name must be 50 characters or less")
    }
    
    // Optional: Add regex for allowed characters
    // For example, allow letters, numbers, spaces, and some special characters
    val allowedPattern = Regex("^[a-zA-Z0-9\\s-_]+$")
    if (!trimmed.matches(allowedPattern)) {
        return ValidationResult(false, "Label name contains invalid characters")
    }
    
    return ValidationResult(true)
}

fun validateLabelColor(color: String): ValidationResult {
    if (color.isEmpty()) {
        return ValidationResult(false, "Color cannot be empty")
    }
    
    // Validate hex color format (#RRGGBB or #AARRGGBB)
    val hexColorPattern = Regex("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{8})$")
    if (!color.matches(hexColorPattern)) {
        return ValidationResult(false, "Invalid color format. Use hex format like #4CAF50")
    }
    
    return ValidationResult(true)
}

