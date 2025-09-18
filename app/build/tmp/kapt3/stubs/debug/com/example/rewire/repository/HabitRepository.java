package com.example.rewire.repository;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000N\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\r\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0016\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0086@\u00a2\u0006\u0002\u0010\u000bJ\u001c\u0010\f\u001a\u00020\b2\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\n0\u000eH\u0086@\u00a2\u0006\u0002\u0010\u000fJ\u0014\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\n0\u000eH\u0086@\u00a2\u0006\u0002\u0010\u0011J\u001c\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\n0\u000e2\u0006\u0010\u0013\u001a\u00020\u0014H\u0086@\u00a2\u0006\u0002\u0010\u0015J\u0018\u0010\u0016\u001a\u0004\u0018\u00010\n2\u0006\u0010\u0017\u001a\u00020\u0018H\u0086@\u00a2\u0006\u0002\u0010\u0019J\u0016\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u0018H\u0086@\u00a2\u0006\u0002\u0010\u0019J\u001c\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\n0\u000e2\u0006\u0010\u0013\u001a\u00020\u0014H\u0086@\u00a2\u0006\u0002\u0010\u0015J\u001c\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\n0\u000e2\u0006\u0010\u001f\u001a\u00020 H\u0086@\u00a2\u0006\u0002\u0010!J$\u0010\"\u001a\b\u0012\u0004\u0012\u00020\n0\u000e2\u0006\u0010#\u001a\u00020\u001b2\u0006\u0010$\u001a\u00020\u001bH\u0086@\u00a2\u0006\u0002\u0010%J\u0016\u0010&\u001a\u00020\u00182\u0006\u0010\t\u001a\u00020\nH\u0086@\u00a2\u0006\u0002\u0010\u000bJ\u001c\u0010\'\u001a\u00020\b2\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\n0\u000eH\u0086@\u00a2\u0006\u0002\u0010\u000fJ\u001c\u0010(\u001a\b\u0012\u0004\u0012\u00020\n0\u000e2\u0006\u0010)\u001a\u00020\u0014H\u0086@\u00a2\u0006\u0002\u0010\u0015J\u000e\u0010*\u001a\u00020\b2\u0006\u0010+\u001a\u00020\u0006J\u0016\u0010,\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0086@\u00a2\u0006\u0002\u0010\u000bR\u000e\u0010\u0005\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006-"}, d2 = {"Lcom/example/rewire/repository/HabitRepository;", "", "habitDao", "Lcom/example/rewire/db/dao/HabitDao;", "(Lcom/example/rewire/db/dao/HabitDao;)V", "habitCompletionDao", "Lcom/example/rewire/db/dao/HabitCompletionDao;", "deleteHabit", "", "habit", "Lcom/example/rewire/db/entity/HabitEntity;", "(Lcom/example/rewire/db/entity/HabitEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteHabits", "habits", "", "(Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllHabits", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getCompletedHabits", "date", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getHabitById", "id", "", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getHabitStreak", "", "habitId", "getHabitsByDate", "getHabitsByRecurrence", "recurrence", "Lcom/example/rewire/core/RecurrenceType;", "(Lcom/example/rewire/core/RecurrenceType;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getHabitsPaged", "offset", "limit", "(IILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insertHabit", "insertHabits", "searchHabits", "query", "setHabitCompletionDao", "dao", "updateHabit", "app_debug"})
public final class HabitRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.example.rewire.db.dao.HabitDao habitDao = null;
    private com.example.rewire.db.dao.HabitCompletionDao habitCompletionDao;
    
    public HabitRepository(@org.jetbrains.annotations.NotNull()
    com.example.rewire.db.dao.HabitDao habitDao) {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getAllHabits(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.example.rewire.db.entity.HabitEntity>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getHabitById(long id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.rewire.db.entity.HabitEntity> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object insertHabit(@org.jetbrains.annotations.NotNull()
    com.example.rewire.db.entity.HabitEntity habit, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object updateHabit(@org.jetbrains.annotations.NotNull()
    com.example.rewire.db.entity.HabitEntity habit, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object deleteHabit(@org.jetbrains.annotations.NotNull()
    com.example.rewire.db.entity.HabitEntity habit, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getHabitsByRecurrence(@org.jetbrains.annotations.NotNull()
    com.example.rewire.core.RecurrenceType recurrence, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.example.rewire.db.entity.HabitEntity>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getHabitsByDate(@org.jetbrains.annotations.NotNull()
    java.lang.String date, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.example.rewire.db.entity.HabitEntity>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object insertHabits(@org.jetbrains.annotations.NotNull()
    java.util.List<com.example.rewire.db.entity.HabitEntity> habits, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object deleteHabits(@org.jetbrains.annotations.NotNull()
    java.util.List<com.example.rewire.db.entity.HabitEntity> habits, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object searchHabits(@org.jetbrains.annotations.NotNull()
    java.lang.String query, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.example.rewire.db.entity.HabitEntity>> $completion) {
        return null;
    }
    
    public final void setHabitCompletionDao(@org.jetbrains.annotations.NotNull()
    com.example.rewire.db.dao.HabitCompletionDao dao) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getCompletedHabits(@org.jetbrains.annotations.NotNull()
    java.lang.String date, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.example.rewire.db.entity.HabitEntity>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getHabitStreak(long habitId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getHabitsPaged(int offset, int limit, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.example.rewire.db.entity.HabitEntity>> $completion) {
        return null;
    }
}