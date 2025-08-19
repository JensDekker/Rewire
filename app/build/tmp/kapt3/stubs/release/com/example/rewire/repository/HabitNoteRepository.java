package com.example.rewire.repository;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0000\n\u0002\u0010\t\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0016\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0086@\u00a2\u0006\u0002\u0010\tJ\u0016\u0010\n\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0086@\u00a2\u0006\u0002\u0010\tJ\u001c\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\b0\f2\u0006\u0010\r\u001a\u00020\u000eH\u0086@\u00a2\u0006\u0002\u0010\u000fJ\u0016\u0010\u0010\u001a\u00020\u000e2\u0006\u0010\u0007\u001a\u00020\bH\u0086@\u00a2\u0006\u0002\u0010\tR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0011"}, d2 = {"Lcom/example/rewire/repository/HabitNoteRepository;", "", "habitNoteDao", "Lcom/example/rewire/db/dao/HabitNoteDao;", "(Lcom/example/rewire/db/dao/HabitNoteDao;)V", "deleteNote", "", "note", "Lcom/example/rewire/db/entity/HabitNoteEntity;", "(Lcom/example/rewire/db/entity/HabitNoteEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "editNote", "getNotesForHabit", "", "habitId", "", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insertNote", "app_release"})
public final class HabitNoteRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.example.rewire.db.dao.HabitNoteDao habitNoteDao = null;
    
    public HabitNoteRepository(@org.jetbrains.annotations.NotNull()
    com.example.rewire.db.dao.HabitNoteDao habitNoteDao) {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object editNote(@org.jetbrains.annotations.NotNull()
    com.example.rewire.db.entity.HabitNoteEntity note, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object insertNote(@org.jetbrains.annotations.NotNull()
    com.example.rewire.db.entity.HabitNoteEntity note, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion) {
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
}