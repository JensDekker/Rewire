package com.example.rewire.manager;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000L\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\f\n\u0002\u0010 \n\u0002\b\u0006\u0018\u00002\u00020\u0001B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bJ\u0016\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fH\u0086@\u00a2\u0006\u0002\u0010\rJ\u0016\u0010\u000e\u001a\u00020\n2\u0006\u0010\u000f\u001a\u00020\u0010H\u0086@\u00a2\u0006\u0002\u0010\u0011J\u0016\u0010\u0012\u001a\u00020\n2\u0006\u0010\u0013\u001a\u00020\u0014H\u0086@\u00a2\u0006\u0002\u0010\u0015J\u0016\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u000b\u001a\u00020\fH\u0086@\u00a2\u0006\u0002\u0010\rJ\u0016\u0010\u0018\u001a\u00020\u00172\u0006\u0010\u000f\u001a\u00020\u0010H\u0086@\u00a2\u0006\u0002\u0010\u0011J\u0016\u0010\u0019\u001a\u00020\u00172\u0006\u0010\u0013\u001a\u00020\u0014H\u0086@\u00a2\u0006\u0002\u0010\u0015J\u000e\u0010\u001a\u001a\u00020\u0017H\u0086@\u00a2\u0006\u0002\u0010\u001bJ\u000e\u0010\u001c\u001a\u00020\u0017H\u0086@\u00a2\u0006\u0002\u0010\u001bJ\u000e\u0010\u001d\u001a\u00020\u0017H\u0086@\u00a2\u0006\u0002\u0010\u001bJ\u0018\u0010\u001e\u001a\u0004\u0018\u00010\f2\u0006\u0010\u001f\u001a\u00020\nH\u0086@\u00a2\u0006\u0002\u0010 J\u0018\u0010!\u001a\u0004\u0018\u00010\u00102\u0006\u0010\u001f\u001a\u00020\nH\u0086@\u00a2\u0006\u0002\u0010 J\u0018\u0010\"\u001a\u0004\u0018\u00010\u00142\u0006\u0010\u001f\u001a\u00020\nH\u0086@\u00a2\u0006\u0002\u0010 J\u0014\u0010#\u001a\b\u0012\u0004\u0012\u00020\f0$H\u0086@\u00a2\u0006\u0002\u0010\u001bJ\u0014\u0010%\u001a\b\u0012\u0004\u0012\u00020\u00100$H\u0086@\u00a2\u0006\u0002\u0010\u001bJ\u0014\u0010&\u001a\b\u0012\u0004\u0012\u00020\u00140$H\u0086@\u00a2\u0006\u0002\u0010\u001bJ\u0016\u0010\'\u001a\u00020\u00172\u0006\u0010\u000b\u001a\u00020\fH\u0086@\u00a2\u0006\u0002\u0010\rJ\u0016\u0010(\u001a\u00020\u00172\u0006\u0010\u000f\u001a\u00020\u0010H\u0086@\u00a2\u0006\u0002\u0010\u0011J\u0016\u0010)\u001a\u00020\u00172\u0006\u0010\u0013\u001a\u00020\u0014H\u0086@\u00a2\u0006\u0002\u0010\u0015R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006*"}, d2 = {"Lcom/example/rewire/manager/AddictionManager;", "", "abstinenceGoalRepository", "Lcom/example/rewire/repository/AbstinenceGoalRepository;", "addictionHabitRepository", "Lcom/example/rewire/repository/AddictionHabitRepository;", "addictionNoteRepository", "Lcom/example/rewire/repository/AddictionNoteRepository;", "(Lcom/example/rewire/repository/AbstinenceGoalRepository;Lcom/example/rewire/repository/AddictionHabitRepository;Lcom/example/rewire/repository/AddictionNoteRepository;)V", "addAbstinenceGoal", "", "goal", "Lcom/example/rewire/db/entity/AbstinenceGoalEntity;", "(Lcom/example/rewire/db/entity/AbstinenceGoalEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "addAddictionHabit", "habit", "Lcom/example/rewire/db/entity/AddictionHabitEntity;", "(Lcom/example/rewire/db/entity/AddictionHabitEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "addAddictionNote", "note", "Lcom/example/rewire/db/entity/AddictionNoteEntity;", "(Lcom/example/rewire/db/entity/AddictionNoteEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteAbstinenceGoal", "", "deleteAddictionHabit", "deleteAddictionNote", "deleteAllAbstinenceGoals", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteAllAddictionHabits", "deleteAllAddictionNotes", "getAbstinenceGoalById", "id", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAddictionHabitById", "getAddictionNoteById", "getAllAbstinenceGoals", "", "getAllAddictionHabits", "getAllAddictionNotes", "updateAbstinenceGoal", "updateAddictionHabit", "updateAddictionNote", "app_release"})
public final class AddictionManager {
    @org.jetbrains.annotations.NotNull()
    private final com.example.rewire.repository.AbstinenceGoalRepository abstinenceGoalRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.rewire.repository.AddictionHabitRepository addictionHabitRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.rewire.repository.AddictionNoteRepository addictionNoteRepository = null;
    
    public AddictionManager(@org.jetbrains.annotations.NotNull()
    com.example.rewire.repository.AbstinenceGoalRepository abstinenceGoalRepository, @org.jetbrains.annotations.NotNull()
    com.example.rewire.repository.AddictionHabitRepository addictionHabitRepository, @org.jetbrains.annotations.NotNull()
    com.example.rewire.repository.AddictionNoteRepository addictionNoteRepository) {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getAllAbstinenceGoals(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.example.rewire.db.entity.AbstinenceGoalEntity>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getAbstinenceGoalById(long id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.rewire.db.entity.AbstinenceGoalEntity> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object addAbstinenceGoal(@org.jetbrains.annotations.NotNull()
    com.example.rewire.db.entity.AbstinenceGoalEntity goal, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object updateAbstinenceGoal(@org.jetbrains.annotations.NotNull()
    com.example.rewire.db.entity.AbstinenceGoalEntity goal, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object deleteAbstinenceGoal(@org.jetbrains.annotations.NotNull()
    com.example.rewire.db.entity.AbstinenceGoalEntity goal, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object deleteAllAbstinenceGoals(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getAllAddictionHabits(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.example.rewire.db.entity.AddictionHabitEntity>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getAddictionHabitById(long id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.rewire.db.entity.AddictionHabitEntity> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object addAddictionHabit(@org.jetbrains.annotations.NotNull()
    com.example.rewire.db.entity.AddictionHabitEntity habit, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object updateAddictionHabit(@org.jetbrains.annotations.NotNull()
    com.example.rewire.db.entity.AddictionHabitEntity habit, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object deleteAddictionHabit(@org.jetbrains.annotations.NotNull()
    com.example.rewire.db.entity.AddictionHabitEntity habit, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object deleteAllAddictionHabits(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getAllAddictionNotes(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.example.rewire.db.entity.AddictionNoteEntity>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getAddictionNoteById(long id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.rewire.db.entity.AddictionNoteEntity> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object addAddictionNote(@org.jetbrains.annotations.NotNull()
    com.example.rewire.db.entity.AddictionNoteEntity note, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object updateAddictionNote(@org.jetbrains.annotations.NotNull()
    com.example.rewire.db.entity.AddictionNoteEntity note, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object deleteAddictionNote(@org.jetbrains.annotations.NotNull()
    com.example.rewire.db.entity.AddictionNoteEntity note, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object deleteAllAddictionNotes(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
}