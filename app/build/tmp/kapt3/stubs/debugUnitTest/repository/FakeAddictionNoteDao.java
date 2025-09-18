package repository;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0010 \n\u0002\b\u0006\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0016\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u0007H\u0096@\u00a2\u0006\u0002\u0010\u000bJ\u000e\u0010\f\u001a\u00020\tH\u0096@\u00a2\u0006\u0002\u0010\rJ\u0014\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00070\u000fH\u0096@\u00a2\u0006\u0002\u0010\rJ\u0018\u0010\u0010\u001a\u0004\u0018\u00010\u00072\u0006\u0010\u0011\u001a\u00020\u0004H\u0096@\u00a2\u0006\u0002\u0010\u0012J\u0016\u0010\u0013\u001a\u00020\u00042\u0006\u0010\n\u001a\u00020\u0007H\u0096@\u00a2\u0006\u0002\u0010\u000bJ\u0016\u0010\u0014\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u0007H\u0096@\u00a2\u0006\u0002\u0010\u000bR\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0015"}, d2 = {"Lrepository/FakeAddictionNoteDao;", "Lcom/example/rewire/db/dao/AddictionNoteDao;", "()V", "idCounter", "", "notes", "", "Lcom/example/rewire/db/entity/AddictionNoteEntity;", "delete", "", "note", "(Lcom/example/rewire/db/entity/AddictionNoteEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteAll", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAll", "", "getById", "id", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insert", "update", "app_debugUnitTest"})
public final class FakeAddictionNoteDao implements com.example.rewire.db.dao.AddictionNoteDao {
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.example.rewire.db.entity.AddictionNoteEntity> notes = null;
    private long idCounter = 1L;
    
    public FakeAddictionNoteDao() {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object getAll(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.example.rewire.db.entity.AddictionNoteEntity>> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object getById(long id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.rewire.db.entity.AddictionNoteEntity> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object insert(@org.jetbrains.annotations.NotNull()
    com.example.rewire.db.entity.AddictionNoteEntity note, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object update(@org.jetbrains.annotations.NotNull()
    com.example.rewire.db.entity.AddictionNoteEntity note, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object delete(@org.jetbrains.annotations.NotNull()
    com.example.rewire.db.entity.AddictionNoteEntity note, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object deleteAll(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
}