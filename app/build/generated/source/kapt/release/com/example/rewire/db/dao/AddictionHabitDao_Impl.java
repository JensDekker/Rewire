package com.example.rewire.db.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.example.rewire.db.entity.AddictionHabitEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AddictionHabitDao_Impl implements AddictionHabitDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<AddictionHabitEntity> __insertionAdapterOfAddictionHabitEntity;

  private final EntityDeletionOrUpdateAdapter<AddictionHabitEntity> __deletionAdapterOfAddictionHabitEntity;

  private final EntityDeletionOrUpdateAdapter<AddictionHabitEntity> __updateAdapterOfAddictionHabitEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public AddictionHabitDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfAddictionHabitEntity = new EntityInsertionAdapter<AddictionHabitEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `addiction_habits` (`id`,`name`,`startDate`,`recurrence`,`preferredTime`,`estimatedMinutes`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AddictionHabitEntity entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getName());
        }
        if (entity.getStartDate() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getStartDate());
        }
        if (entity.getRecurrence() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getRecurrence());
        }
        if (entity.getPreferredTime() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getPreferredTime());
        }
        if (entity.getEstimatedMinutes() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getEstimatedMinutes());
        }
      }
    };
    this.__deletionAdapterOfAddictionHabitEntity = new EntityDeletionOrUpdateAdapter<AddictionHabitEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `addiction_habits` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AddictionHabitEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfAddictionHabitEntity = new EntityDeletionOrUpdateAdapter<AddictionHabitEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `addiction_habits` SET `id` = ?,`name` = ?,`startDate` = ?,`recurrence` = ?,`preferredTime` = ?,`estimatedMinutes` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AddictionHabitEntity entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getName());
        }
        if (entity.getStartDate() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getStartDate());
        }
        if (entity.getRecurrence() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getRecurrence());
        }
        if (entity.getPreferredTime() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getPreferredTime());
        }
        if (entity.getEstimatedMinutes() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getEstimatedMinutes());
        }
        statement.bindLong(7, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM addiction_habits";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final AddictionHabitEntity addictionHabit,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfAddictionHabitEntity.insertAndReturnId(addictionHabit);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final AddictionHabitEntity addictionHabit,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfAddictionHabitEntity.handle(addictionHabit);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final AddictionHabitEntity addictionHabit,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfAddictionHabitEntity.handle(addictionHabit);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAll(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAll.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteAll.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getAll(final Continuation<? super List<AddictionHabitEntity>> $completion) {
    final String _sql = "SELECT * FROM addiction_habits";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<AddictionHabitEntity>>() {
      @Override
      @NonNull
      public List<AddictionHabitEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfRecurrence = CursorUtil.getColumnIndexOrThrow(_cursor, "recurrence");
          final int _cursorIndexOfPreferredTime = CursorUtil.getColumnIndexOrThrow(_cursor, "preferredTime");
          final int _cursorIndexOfEstimatedMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "estimatedMinutes");
          final List<AddictionHabitEntity> _result = new ArrayList<AddictionHabitEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AddictionHabitEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final String _tmpStartDate;
            if (_cursor.isNull(_cursorIndexOfStartDate)) {
              _tmpStartDate = null;
            } else {
              _tmpStartDate = _cursor.getString(_cursorIndexOfStartDate);
            }
            final String _tmpRecurrence;
            if (_cursor.isNull(_cursorIndexOfRecurrence)) {
              _tmpRecurrence = null;
            } else {
              _tmpRecurrence = _cursor.getString(_cursorIndexOfRecurrence);
            }
            final String _tmpPreferredTime;
            if (_cursor.isNull(_cursorIndexOfPreferredTime)) {
              _tmpPreferredTime = null;
            } else {
              _tmpPreferredTime = _cursor.getString(_cursorIndexOfPreferredTime);
            }
            final Integer _tmpEstimatedMinutes;
            if (_cursor.isNull(_cursorIndexOfEstimatedMinutes)) {
              _tmpEstimatedMinutes = null;
            } else {
              _tmpEstimatedMinutes = _cursor.getInt(_cursorIndexOfEstimatedMinutes);
            }
            _item = new AddictionHabitEntity(_tmpId,_tmpName,_tmpStartDate,_tmpRecurrence,_tmpPreferredTime,_tmpEstimatedMinutes);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getById(final long id,
      final Continuation<? super AddictionHabitEntity> $completion) {
    final String _sql = "SELECT * FROM addiction_habits WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<AddictionHabitEntity>() {
      @Override
      @Nullable
      public AddictionHabitEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfRecurrence = CursorUtil.getColumnIndexOrThrow(_cursor, "recurrence");
          final int _cursorIndexOfPreferredTime = CursorUtil.getColumnIndexOrThrow(_cursor, "preferredTime");
          final int _cursorIndexOfEstimatedMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "estimatedMinutes");
          final AddictionHabitEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final String _tmpStartDate;
            if (_cursor.isNull(_cursorIndexOfStartDate)) {
              _tmpStartDate = null;
            } else {
              _tmpStartDate = _cursor.getString(_cursorIndexOfStartDate);
            }
            final String _tmpRecurrence;
            if (_cursor.isNull(_cursorIndexOfRecurrence)) {
              _tmpRecurrence = null;
            } else {
              _tmpRecurrence = _cursor.getString(_cursorIndexOfRecurrence);
            }
            final String _tmpPreferredTime;
            if (_cursor.isNull(_cursorIndexOfPreferredTime)) {
              _tmpPreferredTime = null;
            } else {
              _tmpPreferredTime = _cursor.getString(_cursorIndexOfPreferredTime);
            }
            final Integer _tmpEstimatedMinutes;
            if (_cursor.isNull(_cursorIndexOfEstimatedMinutes)) {
              _tmpEstimatedMinutes = null;
            } else {
              _tmpEstimatedMinutes = _cursor.getInt(_cursorIndexOfEstimatedMinutes);
            }
            _result = new AddictionHabitEntity(_tmpId,_tmpName,_tmpStartDate,_tmpRecurrence,_tmpPreferredTime,_tmpEstimatedMinutes);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
