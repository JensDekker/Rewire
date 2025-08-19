package com.example.rewire.repository;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u001e\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0086@\u00a2\u0006\u0002\u0010\u000bJ\u001c\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000e0\r2\u0006\u0010\u0007\u001a\u00020\bH\u0086@\u00a2\u0006\u0002\u0010\u000fJ\u0016\u0010\u0010\u001a\u00020\u00062\u0006\u0010\u0011\u001a\u00020\u000eH\u0086@\u00a2\u0006\u0002\u0010\u0012R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0013"}, d2 = {"Lcom/example/rewire/repository/HabitCompletionRepository;", "", "habitCompletionDao", "Lcom/example/rewire/db/dao/HabitCompletionDao;", "(Lcom/example/rewire/db/dao/HabitCompletionDao;)V", "deleteCompletion", "", "habitId", "", "date", "", "(JLjava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getCompletionsForHabit", "", "Lcom/example/rewire/db/entity/HabitCompletion;", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insertCompletion", "completion", "(Lcom/example/rewire/db/entity/HabitCompletion;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_release"})
public final class HabitCompletionRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.example.rewire.db.dao.HabitCompletionDao habitCompletionDao = null;
    
    public HabitCompletionRepository(@org.jetbrains.annotations.NotNull()
    com.example.rewire.db.dao.HabitCompletionDao habitCompletionDao) {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object insertCompletion(@org.jetbrains.annotations.NotNull()
    com.example.rewire.db.entity.HabitCompletion completion, @org.jetbrains.annotations.NotNull()
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
}