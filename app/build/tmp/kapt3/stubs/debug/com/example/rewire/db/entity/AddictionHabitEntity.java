package com.example.rewire.db.entity;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0015\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0087\b\u0018\u00002\u00020\u0001B;\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\b\u0010\t\u001a\u0004\u0018\u00010\u0005\u0012\b\u0010\n\u001a\u0004\u0018\u00010\u000b\u00a2\u0006\u0002\u0010\fJ\t\u0010\u0018\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0019\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u001a\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u001b\u001a\u00020\bH\u00c6\u0003J\u000b\u0010\u001c\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\u0010\u0010\u001d\u001a\u0004\u0018\u00010\u000bH\u00c6\u0003\u00a2\u0006\u0002\u0010\u000eJN\u0010\u001e\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\u0007\u001a\u00020\b2\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\u00052\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u000bH\u00c6\u0001\u00a2\u0006\u0002\u0010\u001fJ\u0013\u0010 \u001a\u00020!2\b\u0010\"\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010#\u001a\u00020\u000bH\u00d6\u0001J\t\u0010$\u001a\u00020\u0005H\u00d6\u0001R\u0015\u0010\n\u001a\u0004\u0018\u00010\u000b\u00a2\u0006\n\n\u0002\u0010\u000f\u001a\u0004\b\r\u0010\u000eR\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0013\u0010\t\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0013R\u0011\u0010\u0007\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0011\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0013\u00a8\u0006%"}, d2 = {"Lcom/example/rewire/db/entity/AddictionHabitEntity;", "", "id", "", "name", "", "startDate", "recurrence", "Lcom/example/rewire/core/RecurrenceType;", "preferredTime", "estimatedMinutes", "", "(JLjava/lang/String;Ljava/lang/String;Lcom/example/rewire/core/RecurrenceType;Ljava/lang/String;Ljava/lang/Integer;)V", "getEstimatedMinutes", "()Ljava/lang/Integer;", "Ljava/lang/Integer;", "getId", "()J", "getName", "()Ljava/lang/String;", "getPreferredTime", "getRecurrence", "()Lcom/example/rewire/core/RecurrenceType;", "getStartDate", "component1", "component2", "component3", "component4", "component5", "component6", "copy", "(JLjava/lang/String;Ljava/lang/String;Lcom/example/rewire/core/RecurrenceType;Ljava/lang/String;Ljava/lang/Integer;)Lcom/example/rewire/db/entity/AddictionHabitEntity;", "equals", "", "other", "hashCode", "toString", "app_debug"})
@androidx.room.Entity(tableName = "addiction_habits")
@androidx.room.TypeConverters(value = {com.example.rewire.db.converter.RecurrenceTypeConverter.class})
public final class AddictionHabitEntity {
    @androidx.room.PrimaryKey(autoGenerate = true)
    private final long id = 0L;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String name = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String startDate = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.rewire.core.RecurrenceType recurrence = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String preferredTime = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Integer estimatedMinutes = null;
    
    public AddictionHabitEntity(long id, @org.jetbrains.annotations.NotNull()
    java.lang.String name, @org.jetbrains.annotations.NotNull()
    java.lang.String startDate, @org.jetbrains.annotations.NotNull()
    com.example.rewire.core.RecurrenceType recurrence, @org.jetbrains.annotations.Nullable()
    java.lang.String preferredTime, @org.jetbrains.annotations.Nullable()
    java.lang.Integer estimatedMinutes) {
        super();
    }
    
    public final long getId() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getName() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getStartDate() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rewire.core.RecurrenceType getRecurrence() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getPreferredTime() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer getEstimatedMinutes() {
        return null;
    }
    
    public final long component1() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rewire.core.RecurrenceType component4() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component5() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer component6() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rewire.db.entity.AddictionHabitEntity copy(long id, @org.jetbrains.annotations.NotNull()
    java.lang.String name, @org.jetbrains.annotations.NotNull()
    java.lang.String startDate, @org.jetbrains.annotations.NotNull()
    com.example.rewire.core.RecurrenceType recurrence, @org.jetbrains.annotations.Nullable()
    java.lang.String preferredTime, @org.jetbrains.annotations.Nullable()
    java.lang.Integer estimatedMinutes) {
        return null;
    }
    
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object other) {
        return false;
    }
    
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String toString() {
        return null;
    }
}