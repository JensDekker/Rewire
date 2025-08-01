// =====================
// Addiction Functions (CRUD)
// =====================

fun cliAddAddiction(manager: AddictionManager) {
    print("Enter addiction name: ")
    val name = readLine()?.takeIf { it.isNotBlank() } ?: return
    manager.addAddiction(AddictionHabit(name))
    println("✅ Addiction '$name' added.")
}

fun cliListAddictions(manager: AddictionManager) {
    val addictions = manager.listAddictions()
    if (addictions.isEmpty()) {
        println("No addictions found.")
        return
    }
    println("Addictions:")
    addictions.forEachIndexed { i, name -> println("${i + 1}. $name") }
}

fun cliViewAddiction(manager: AddictionManager) {
    val addictions = manager.listAddictions()
    if (addictions.isEmpty()) {
        println("No addictions to view.")
        return
    }
    println("Available addictions:")
    addictions.forEachIndexed { i, name -> println("${i + 1}. $name") }
    print("Select addiction to view (number): ")
    val index = readLine()?.toIntOrNull()?.takeIf { it in 1..addictions.size } ?: return
    manager.printAddictionStats(addictions[index - 1])
}

fun cliEditAddiction(manager: AddictionManager) {
    val addictions = manager.listAddictions()
    if (addictions.isEmpty()) {
        println("No addictions to edit.")
        return
    }
    println("Available addictions:")
    addictions.forEachIndexed { i, name -> println("${i + 1}. $name") }
    print("Select addiction to edit (number): ")
    val index = readLine()?.toIntOrNull()?.takeIf { it in 1..addictions.size } ?: return
    val oldName = addictions[index - 1]
    print("New name (leave blank to keep '$oldName'): ")
    val newName = readLine()?.takeIf { it.isNotBlank() }
    manager.renameAddiction(oldName, newName ?: oldName)
    println("✏️ Addiction updated.")
}

fun cliDeleteAddiction(manager: AddictionManager) {
    val addictions = manager.listAddictions()
    if (addictions.isEmpty()) {
        println("No addictions to delete.")
        return
    }
    println("Available addictions:")
    addictions.forEachIndexed { i, name -> println("${i + 1}. $name") }
    print("Select addiction to delete (number): ")
    val index = readLine()?.toIntOrNull()?.takeIf { it in 1..addictions.size } ?: return
    val nameToDelete = addictions[index - 1]
    manager.deleteAddiction(nameToDelete)
    println("🗑️ Addiction '$nameToDelete' deleted.")
}

fun cliLogAddictionUsage(manager: AddictionManager) {
    val addictions = manager.listAddictions()
    if (addictions.isEmpty()) {
        println("No addictions to log usage for.")
        return
    }
    println("Available addictions:")
    addictions.forEachIndexed { i, name -> println("${i + 1}. $name") }
    print("Select addiction (number): ")
    val index = readLine()?.toIntOrNull()?.takeIf { it in 1..addictions.size } ?: return
    val name = addictions[index - 1]
    print("Enter usage details (e.g., amount, time, notes): ")
    val details = readLine()?.takeIf { it.isNotBlank() } ?: return
    manager.logUsage(name, details)
    println("📋 Usage for '$name' logged.")
}

// =====================
// Addiction Note Functions (CRUD)
// =====================

fun cliAddAddictionNote(manager: AddictionManager) {
    val addictions = manager.listAddictions()
    if (addictions.isEmpty()) {
        println("No addictions to add notes to.")
        return
    }
    println("Available addictions:")
    addictions.forEachIndexed { i, name -> println("${i + 1}. $name") }
    print("Select addiction (number): ")
    val index = readLine()?.toIntOrNull()?.takeIf { it in 1..addictions.size } ?: return
    val name = addictions[index - 1]
    print("Enter note: ")
    val note = readLine()?.takeIf { it.isNotBlank() } ?: return
    manager.addNote(name, note)
    println("📝 Note added to '$name'.")
}

fun cliListAddictionNotes(manager: AddictionManager) {
    val addictions = manager.listAddictions()
    if (addictions.isEmpty()) {
        println("No addictions to list notes for.")
        return
    }
    println("Available addictions:")
    addictions.forEachIndexed { i, name -> println("${i + 1}. $name") }
    print("Select addiction (number): ")
    val index = readLine()?.toIntOrNull()?.takeIf { it in 1..addictions.size } ?: return
    val name = addictions[index - 1]
    val notes = manager.getNotes(name)
    if (notes.isEmpty()) {
        println("No notes for '$name'.")
        return
    }
    println("Notes for '$name':")
    notes.forEachIndexed { i, note -> println("${i + 1}. $note") }
}

fun cliEditAddictionNote(manager: AddictionManager) {
    val addictions = manager.listAddictions()
    if (addictions.isEmpty()) {
        println("No addictions to edit notes for.")
        return
    }
    println("Available addictions:")
    addictions.forEachIndexed { i, name -> println("${i + 1}. $name") }
    print("Select addiction (number): ")
    val index = readLine()?.toIntOrNull()?.takeIf { it in 1..addictions.size } ?: return
    val name = addictions[index - 1]
    val notes = manager.getNotes(name)
    if (notes.isEmpty()) {
        println("No notes for '$name'.")
        return
    }
    println("Notes:")
    notes.forEachIndexed { i, note -> println("${i + 1}. $note") }
    print("Select note to edit (number): ")
    val noteIndex = readLine()?.toIntOrNull()?.takeIf { it in 1..notes.size } ?: return
    print("Enter new note: ")
    val newNote = readLine()?.takeIf { it.isNotBlank() } ?: return
    manager.editNote(name, noteIndex - 1, newNote)
    println("✏️ Note updated.")
}

fun cliDeleteAddictionNote(manager: AddictionManager) {
    val addictions = manager.listAddictions()
    if (addictions.isEmpty()) {
        println("No addictions to delete notes from.")
        return
    }
    println("Available addictions:")
    addictions.forEachIndexed { i, name -> println("${i + 1}. $name") }
    print("Select addiction (number): ")
    val index = readLine()?.toIntOrNull()?.takeIf { it in 1..addictions.size } ?: return
    val name = addictions[index - 1]
    val notes = manager.getNotes(name)
    if (notes.isEmpty()) {
        println("No notes for '$name'.")
        return
    }
    println("Notes:")
    notes.forEachIndexed { i, note -> println("${i + 1}. $note") }
    print("Select note to delete (number): ")
    val noteIndex = readLine()?.toIntOrNull()?.takeIf { it in 1..notes.size } ?: return
    manager.deleteNote(name, noteIndex - 1)
    println("🗑️ Note deleted.")
}

// =====================
// Usage Plan Functions (CRUD)
// =====================

fun cliListUsagePlan(manager: AddictionManager) {
    val addictions = manager.listAddictions()
    if (addictions.isEmpty()) {
        println("No addictions found.")
        return
    }
    println("Available addictions:")
    addictions.forEachIndexed { i, name -> println("${i + 1}. $name") }
    print("Select addiction (number): ")
    val index = readLine()?.toIntOrNull()?.takeIf { it in 1..addictions.size } ?: return
    val name = addictions[index - 1]
    val plan = manager.getUsagePlan(name)
    if (plan.isEmpty()) {
        println("No usage plan for '$name'.")
        return
    }
    println("Usage plan for '$name':")
    plan.forEachIndexed { i, item -> println("${i + 1}. $item") }
}

fun cliAddUsagePlanItem(manager: AddictionManager) {
    val addictions = manager.listAddictions()
    if (addictions.isEmpty()) {
        println("No addictions found.")
        return
    }
    println("Available addictions:")
    addictions.forEachIndexed { i, name -> println("${i + 1}. $name") }
    print("Select addiction (number): ")
    val index = readLine()?.toIntOrNull()?.takeIf { it in 1..addictions.size } ?: return
    val name = addictions[index - 1]
    print("Enter usage plan item: ")
    val item = readLine()?.takeIf { it.isNotBlank() } ?: return
    manager.addUsagePlanItem(name, item)
    println("✅ Usage plan item added to '$name'.")
}

fun cliEditUsagePlanItem(manager: AddictionManager) {
    val addictions = manager.listAddictions()
    if (addictions.isEmpty()) {
        println("No addictions found.")
        return
    }
    println("Available addictions:")
    addictions.forEachIndexed { i, name -> println("${i + 1}. $name") }
    print("Select addiction (number): ")
    val index = readLine()?.toIntOrNull()?.takeIf { it in 1..addictions.size } ?: return
    val name = addictions[index - 1]
    val plan = manager.getUsagePlan(name)
    if (plan.isEmpty()) {
        println("No usage plan for '$name'.")
        return
    }
    println("Usage plan items:")
    plan.forEachIndexed { i, item -> println("${i + 1}. $item") }
    print("Select item to edit (number): ")
    val itemIndex = readLine()?.toIntOrNull()?.takeIf { it in 1..plan.size } ?: return
    print("Enter new usage plan item: ")
    val newItem = readLine()?.takeIf { it.isNotBlank() } ?: return
    manager.editUsagePlanItem(name, itemIndex - 1, newItem)
    println("✏️ Usage plan item updated.")
}

fun cliDeleteUsagePlanItem(manager: AddictionManager) {
    val addictions = manager.listAddictions()
    if (addictions.isEmpty()) {
        println("No addictions found.")
        return
    }
    println("Available addictions:")
    addictions.forEachIndexed { i, name -> println("${i + 1}. $name") }
    print("Select addiction (number): ")
    val index = readLine()?.toIntOrNull()?.takeIf { it in 1..addictions.size } ?: return
    val name = addictions[index - 1]
    val plan = manager.getUsagePlan(name)
    if (plan.isEmpty()) {
        println("No usage plan for '$name'.")
        return
    }
    println("Usage plan items:")
    plan.forEachIndexed { i, item -> println("${i + 1}. $item") }
    print("Select item to delete (number): ")
    val itemIndex = readLine()?.toIntOrNull()?.takeIf { it in 1..plan.size } ?: return
    manager.deleteUsagePlanItem(name, itemIndex - 1)
    println("🗑️ Usage plan item deleted.")
}

fun cliClearUsagePlan(manager: AddictionManager) {
    val addictions = manager.listAddictions()
    if (addictions.isEmpty()) {
        println("No addictions found.")
        return
    }
    println("Available addictions:")
    addictions.forEachIndexed { i, name -> println("${i + 1}. $name") }
    print("Select addiction (number): ")
    val index = readLine()?.toIntOrNull()?.takeIf { it in 1..addictions.size } ?: return
    val name = addictions[index - 1]
    manager.clearUsagePlan(name)
    println("🧹 Usage plan for '$name' cleared.")
}


// CLI functions for addiction habits, addiction notes, and usage plans
// Move all addiction-related CLI functions here from CliFunctions.kt

// Example stub
// fun cliAddAddiction(manager: AddictionManager) { /* ... */ }
// fun cliListAddictions(manager: AddictionManager) { /* ... */ }
// fun cliViewAddiction(manager: AddictionManager) { /* ... */ }
// fun cliEditAddiction(manager: AddictionManager) { /* ... */ }
// fun cliDeleteAddiction(manager: AddictionManager) { /* ... */ }
// fun cliAddAddictionNote(manager: AddictionManager) { /* ... */ }
// fun cliListAddictionNotes(manager: AddictionManager) { /* ... */ }
// fun cliEditAddictionNote(manager: AddictionManager) { /* ... */ }
// fun cliDeleteAddictionNote(manager: AddictionManager) { /* ... */ }
// fun cliListUsagePlan(manager: AddictionManager) { /* ... */ }
// fun cliAddUsagePlanItem(manager: AddictionManager) { /* ... */ }
// fun cliEditUsagePlanItem(manager: AddictionManager) { /* ... */ }
// fun cliDeleteUsagePlanItem(manager: AddictionManager) { /* ... */ }
// fun cliClearUsagePlan(manager: AddictionManager) { /* ... */ }
