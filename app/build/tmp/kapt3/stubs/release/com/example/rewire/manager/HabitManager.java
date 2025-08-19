package com.example.rewire.manager;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000N\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\b\u0018\u00002\u00020\u0001B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bJ \u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\b\b\u0002\u0010\r\u001a\u00020\u000eH\u0086@\u00a2\u0006\u0002\u0010\u000fJ\u0016\u0010\u0010\u001a\u00020\n2\u0006\u0010\u0011\u001a\u00020\u0012H\u0086@\u00a2\u0006\u0002\u0010\u0013J\u001e\u0010\u0014\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eH\u0086@\u00a2\u0006\u0002\u0010\u000fJ\u0016\u0010\u0015\u001a\u00020\n2\u0006\u0010\u0016\u001a\u00020\u0017H\u0086@\u00a2\u0006\u0002\u0010\u0018J\u0016\u0010\u0019\u001a\u00020\n2\u0006\u0010\u0016\u001a\u00020\u0017H\u0086@\u00a2\u0006\u0002\u0010\u0018J\u001c\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u001c0\u001b2\u0006\u0010\u000b\u001a\u00020\fH\u0086@\u00a2\u0006\u0002\u0010\u001dJ\u0014\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u00120\u001bH\u0086@\u00a2\u0006\u0002\u0010\u001fJ\u001c\u0010 \u001a\b\u0012\u0004\u0012\u00020\u00120\u001b2\u0006\u0010\r\u001a\u00020\u000eH\u0086@\u00a2\u0006\u0002\u0010!J\u001c\u0010\"\u001a\b\u0012\u0004\u0012\u00020\u00170\u001b2\u0006\u0010\u000b\u001a\u00020\fH\u0086@\u00a2\u0006\u0002\u0010\u001dJ\u0016\u0010#\u001a\u00020\n2\u0006\u0010\u0016\u001a\u00020\u0017H\u0086@\u00a2\u0006\u0002\u0010\u0018R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006$"}, d2 = {"Lcom/example/rewire/manager/HabitManager;", "", "habitRepository", "Lcom/example/rewire/repository/HabitRepository;", "habitCompletionRepository", "Lcom/example/rewire/repository/HabitCompletionRepository;", "habitNoteRepository", "Lcom/example/rewire/repository/HabitNoteRepository;", "(Lcom/example/rewire/repository/HabitRepository;Lcom/example/rewire/repository/HabitCompletionRepository;Lcom/example/rewire/repository/HabitNoteRepository;)V", "completeHabit", "", "habitId", "", "date", "", "(JLjava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "createHabit", "habit", "Lcom/example/rewire/db/entity/HabitEntity;", "(Lcom/example/rewire/db/entity/HabitEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteCompletion", "deleteNote", "note", "Lcom/example/rewire/db/entity/HabitNoteEntity;", "(Lcom/example/rewire/db/entity/HabitNoteEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "editNote", "getCompletionsForHabit", "", "Lcom/example/rewire/db/entity/HabitCompletion;", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getHabits", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getHabitsDueOn", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getNotesForHabit", "insertNote", "app_release"})
public final class HabitManager {
    @org.jetbrains.annotations.NotNull()
    private final com.example.rewire.repository.HabitRepository habitRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.rewire.repository.HabitCompletionRepository habitCompletionRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.rewire.repository.HabitNoteRepository habitNoteRepository = null;
    
    public HabitManager(@org.jetbrains.annotations.NotNull()
    com.example.rewire.repository.HabitRepository habitRepository, @org.jetbrains.annotations.NotNull()
    com.example.rewire.repository.HabitCompletionRepository habitCompletionRepository, @org.jetbrains.annotations.NotNull()
    com.example.rewire.repository.HabitNoteRepository habitNoteRepository) {
        super();
    }
    
    /**
     * Find all habits due on a given day.
     * Supports all recurrence types defined in RecurrenceType.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getHabitsDueOn(@org.jetbrains.annotations.NotNull()
    java.lang.String date, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.example.rewire.db.entity.HabitEntity>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object createHabit(@org.jetbrains.annotations.NotNull()
    com.example.rewire.db.entity.HabitEntity habit, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object completeHabit(long habitId, @org.jetbrains.annotations.NotNull()
    java.lang.String date, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object deleteCompletion(long habitId, @org.jetbrains.annotations.NotNull()
    java.lang.String date, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getCompletionsForHabit(long habitId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.example.rewire.db.entity.HabitCompletion>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object insertNote(@org.jetbrains.annotations.NotNull()
    com.example.rewire.db.entity.HabitNoteEntity note, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object editNote(@org.jetbrains.annotations.NotNull()
    com.example.rewire.db.entity.HabitNoteEntity note, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object deleteNote(@org.jetbrains.annotations.NotNull()
    com.example.rewire.db.entity.HabitNoteEntity note, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getNotesForHabit(long habitId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.example.rewire.db.entity.HabitNoteEntity>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getHabits(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.example.rewire.db.entity.HabitEntity>> $completion) {
        return null;
    }
}