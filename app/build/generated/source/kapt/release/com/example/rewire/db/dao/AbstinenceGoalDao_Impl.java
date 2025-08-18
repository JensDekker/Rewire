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
import com.example.rewire.core.RecurrenceType;
import com.example.rewire.db.converter.RecurrenceTypeConverter;
import com.example.rewire.db.entity.AbstinenceGoalEntity;
import java.lang.Class;
import java.lang.Exception;
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
public final class AbstinenceGoalDao_Impl implements AbstinenceGoalDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<AbstinenceGoalEntity> __insertionAdapterOfAbstinenceGoalEntity;

  private final RecurrenceTypeConverter __recurrenceTypeConverter = new RecurrenceTypeConverter();

  private final EntityDeletionOrUpdateAdapter<AbstinenceGoalEntity> __deletionAdapterOfAbstinenceGoalEntity;

  private final EntityDeletionOrUpdateAdapter<AbstinenceGoalEntity> __updateAdapterOfAbstinenceGoalEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public AbstinenceGoalDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfAbstinenceGoalEntity = new EntityInsertionAdapter<AbstinenceGoalEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `abstinence_goals` (`id`,`addictionId`,`recurrence`,`value`,`repeatCount`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AbstinenceGoalEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getAddictionId());
        final String _tmp = __recurrenceTypeConverter.fromRecurrenceType(entity.getRecurrence());
        if (_tmp == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, _tmp);
        }
        statement.bindLong(4, entity.getValue());
        statement.bindLong(5, entity.getRepeatCount());
      }
    };
    this.__deletionAdapterOfAbstinenceGoalEntity = new EntityDeletionOrUpdateAdapter<AbstinenceGoalEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `abstinence_goals` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AbstinenceGoalEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfAbstinenceGoalEntity = new EntityDeletionOrUpdateAdapter<AbstinenceGoalEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `abstinence_goals` SET `id` = ?,`addictionId` = ?,`recurrence` = ?,`value` = ?,`repeatCount` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AbstinenceGoalEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getAddictionId());
        final String _tmp = __recurrenceTypeConverter.fromRecurrenceType(entity.getRecurrence());
        if (_tmp == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, _tmp);
        }
        statement.bindLong(4, entity.getValue());
        statement.bindLong(5, entity.getRepeatCount());
        statement.bindLong(6, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM abstinence_goals";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final AbstinenceGoalEntity goal,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfAbstinenceGoalEntity.insertAndReturnId(goal);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final AbstinenceGoalEntity goal,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfAbstinenceGoalEntity.handle(goal);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final AbstinenceGoalEntity goal,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfAbstinenceGoalEntity.handle(goal);
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
  public Object getAll(final Continuation<? super List<AbstinenceGoalEntity>> $completion) {
    final String _sql = "SELECT * FROM abstinence_goals";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<AbstinenceGoalEntity>>() {
      @Override
      @NonNull
      public List<AbstinenceGoalEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAddictionId = CursorUtil.getColumnIndexOrThrow(_cursor, "addictionId");
          final int _cursorIndexOfRecurrence = CursorUtil.getColumnIndexOrThrow(_cursor, "recurrence");
          final int _cursorIndexOfValue = CursorUtil.getColumnIndexOrThrow(_cursor, "value");
          final int _cursorIndexOfRepeatCount = CursorUtil.getColumnIndexOrThrow(_cursor, "repeatCount");
          final List<AbstinenceGoalEntity> _result = new ArrayList<AbstinenceGoalEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AbstinenceGoalEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpAddictionId;
            _tmpAddictionId = _cursor.getLong(_cursorIndexOfAddictionId);
            final RecurrenceType _tmpRecurrence;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfRecurrence)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfRecurrence);
            }
            _tmpRecurrence = __recurrenceTypeConverter.toRecurrenceType(_tmp);
            final int _tmpValue;
            _tmpValue = _cursor.getInt(_cursorIndexOfValue);
            final int _tmpRepeatCount;
            _tmpRepeatCount = _cursor.getInt(_cursorIndexOfRepeatCount);
            _item = new AbstinenceGoalEntity(_tmpId,_tmpAddictionId,_tmpRecurrence,_tmpValue,_tmpRepeatCount);
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
      final Continuation<? super AbstinenceGoalEntity> $completion) {
    final String _sql = "SELECT * FROM abstinence_goals WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<AbstinenceGoalEntity>() {
      @Override
      @Nullable
      public AbstinenceGoalEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAddictionId = CursorUtil.getColumnIndexOrThrow(_cursor, "addictionId");
          final int _cursorIndexOfRecurrence = CursorUtil.getColumnIndexOrThrow(_cursor, "recurrence");
          final int _cursorIndexOfValue = CursorUtil.getColumnIndexOrThrow(_cursor, "value");
          final int _cursorIndexOfRepeatCount = CursorUtil.getColumnIndexOrThrow(_cursor, "repeatCount");
          final AbstinenceGoalEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpAddictionId;
            _tmpAddictionId = _cursor.getLong(_cursorIndexOfAddictionId);
            final RecurrenceType _tmpRecurrence;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfRecurrence)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfRecurrence);
            }
            _tmpRecurrence = __recurrenceTypeConverter.toRecurrenceType(_tmp);
            final int _tmpValue;
            _tmpValue = _cursor.getInt(_cursorIndexOfValue);
            final int _tmpRepeatCount;
            _tmpRepeatCount = _cursor.getInt(_cursorIndexOfRepeatCount);
            _result = new AbstinenceGoalEntity(_tmpId,_tmpAddictionId,_tmpRecurrence,_tmpValue,_tmpRepeatCount);
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
