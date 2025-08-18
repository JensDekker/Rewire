package com.example.rewire.app.src.main.kotlin.db.dao;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0005\bg\u0018\u00002\u00020\u0001J\u001e\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u00a7@\u00a2\u0006\u0002\u0010\bJ\u001c\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\n2\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\fJ\u0016\u0010\r\u001a\u00020\u00032\u0006\u0010\u000e\u001a\u00020\u000bH\u00a7@\u00a2\u0006\u0002\u0010\u000f\u00a8\u0006\u0010"}, d2 = {"Lcom/example/rewire/app/src/main/kotlin/db/dao/HabitCompletionDao;", "", "deleteCompletion", "", "habitId", "", "date", "", "(JLjava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getCompletionsForHabit", "", "Lcom/example/rewire/app/src/main/kotlin/db/entity/HabitCompletion;", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insertCompletion", "completion", "(Lcom/example/rewire/app/src/main/kotlin/db/entity/HabitCompletion;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
@androidx.room.Dao()
public abstract interface HabitCompletionDao {
    
    @androidx.room.Insert()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertCompletion(@org.jetbrains.annotations.NotNull()
    com.example.rewire.app.src.main.kotlin.db.entity.HabitCompletion completion, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM habit_completions WHERE habitId = :habitId AND date = :date")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteCompletion(long habitId, @org.jetbrains.annotations.NotNull()
    java.lang.String date, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM habit_completions WHERE habitId = :habitId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getCompletionsForHabit(long habitId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.example.rewire.app.src.main.kotlin.db.entity.HabitCompletion>> $completion);
}