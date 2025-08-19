package com.example.rewire.db;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\'\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H&J\b\u0010\u0005\u001a\u00020\u0006H&J\b\u0010\u0007\u001a\u00020\bH&J\b\u0010\t\u001a\u00020\nH&J\b\u0010\u000b\u001a\u00020\fH&J\b\u0010\r\u001a\u00020\u000eH&\u00a8\u0006\u000f"}, d2 = {"Lcom/example/rewire/db/RewireDatabase;", "Landroidx/room/RoomDatabase;", "()V", "abstinenceGoalDao", "Lcom/example/rewire/db/dao/AbstinenceGoalDao;", "addictionHabitDao", "Lcom/example/rewire/db/dao/AddictionHabitDao;", "addictionNoteDao", "Lcom/example/rewire/db/dao/AddictionNoteDao;", "habitCompletionDao", "Lcom/example/rewire/db/dao/HabitCompletionDao;", "habitDao", "Lcom/example/rewire/db/dao/HabitDao;", "habitNoteDao", "Lcom/example/rewire/db/dao/HabitNoteDao;", "app_debug"})
@androidx.room.Database(entities = {com.example.rewire.db.entity.HabitEntity.class, com.example.rewire.db.entity.AddictionHabitEntity.class, com.example.rewire.db.entity.AbstinenceGoalEntity.class, com.example.rewire.db.entity.HabitNoteEntity.class, com.example.rewire.db.entity.AddictionNoteEntity.class, com.example.rewire.db.entity.HabitCompletion.class}, version = 1, exportSchema = false)
public abstract class RewireDatabase extends androidx.room.RoomDatabase {
    
    public RewireDatabase() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.example.rewire.db.dao.HabitDao habitDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.example.rewire.db.dao.AddictionHabitDao addictionHabitDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.example.rewire.db.dao.AbstinenceGoalDao abstinenceGoalDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.example.rewire.db.dao.HabitNoteDao habitNoteDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.example.rewire.db.dao.AddictionNoteDao addictionNoteDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.example.rewire.db.dao.HabitCompletionDao habitCompletionDao();
}