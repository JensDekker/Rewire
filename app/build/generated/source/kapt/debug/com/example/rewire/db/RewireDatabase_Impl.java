package com.example.rewire.db;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.example.rewire.db.dao.AbstinenceGoalDao;
import com.example.rewire.db.dao.AbstinenceGoalDao_Impl;
import com.example.rewire.db.dao.AddictionHabitDao;
import com.example.rewire.db.dao.AddictionHabitDao_Impl;
import com.example.rewire.db.dao.AddictionNoteDao;
import com.example.rewire.db.dao.AddictionNoteDao_Impl;
import com.example.rewire.db.dao.HabitCompletionDao;
import com.example.rewire.db.dao.HabitCompletionDao_Impl;
import com.example.rewire.db.dao.HabitDao;
import com.example.rewire.db.dao.HabitDao_Impl;
import com.example.rewire.db.dao.HabitNoteDao;
import com.example.rewire.db.dao.HabitNoteDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class RewireDatabase_Impl extends RewireDatabase {
  private volatile HabitDao _habitDao;

  private volatile AddictionHabitDao _addictionHabitDao;

  private volatile AbstinenceGoalDao _abstinenceGoalDao;

  private volatile HabitNoteDao _habitNoteDao;

  private volatile AddictionNoteDao _addictionNoteDao;

  private volatile HabitCompletionDao _habitCompletionDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `habits` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `recurrence` TEXT NOT NULL, `preferredTime` TEXT NOT NULL, `estimatedMinutes` INTEGER NOT NULL, `startDate` TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `addiction_habits` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `startDate` TEXT NOT NULL, `recurrence` TEXT NOT NULL, `preferredTime` TEXT, `estimatedMinutes` INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `abstinence_goals` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `addictionId` INTEGER NOT NULL, `recurrence` TEXT NOT NULL, `value` INTEGER NOT NULL, `repeatCount` INTEGER NOT NULL, FOREIGN KEY(`addictionId`) REFERENCES `addiction_habits`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE TABLE IF NOT EXISTS `habit_notes` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `habitId` INTEGER NOT NULL, `content` TEXT NOT NULL, `timestamp` TEXT NOT NULL, FOREIGN KEY(`habitId`) REFERENCES `habits`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_habit_notes_habitId` ON `habit_notes` (`habitId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `addiction_notes` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `addictionId` INTEGER NOT NULL, `content` TEXT NOT NULL, `timestamp` TEXT NOT NULL, FOREIGN KEY(`addictionId`) REFERENCES `addiction_habits`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE TABLE IF NOT EXISTS `habit_completions` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `habitId` INTEGER NOT NULL, `date` TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '87ec6815dab94ee54ddce466ca23e47a')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `habits`");
        db.execSQL("DROP TABLE IF EXISTS `addiction_habits`");
        db.execSQL("DROP TABLE IF EXISTS `abstinence_goals`");
        db.execSQL("DROP TABLE IF EXISTS `habit_notes`");
        db.execSQL("DROP TABLE IF EXISTS `addiction_notes`");
        db.execSQL("DROP TABLE IF EXISTS `habit_completions`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsHabits = new HashMap<String, TableInfo.Column>(6);
        _columnsHabits.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHabits.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHabits.put("recurrence", new TableInfo.Column("recurrence", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHabits.put("preferredTime", new TableInfo.Column("preferredTime", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHabits.put("estimatedMinutes", new TableInfo.Column("estimatedMinutes", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHabits.put("startDate", new TableInfo.Column("startDate", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysHabits = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesHabits = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoHabits = new TableInfo("habits", _columnsHabits, _foreignKeysHabits, _indicesHabits);
        final TableInfo _existingHabits = TableInfo.read(db, "habits");
        if (!_infoHabits.equals(_existingHabits)) {
          return new RoomOpenHelper.ValidationResult(false, "habits(com.example.rewire.db.entity.HabitEntity).\n"
                  + " Expected:\n" + _infoHabits + "\n"
                  + " Found:\n" + _existingHabits);
        }
        final HashMap<String, TableInfo.Column> _columnsAddictionHabits = new HashMap<String, TableInfo.Column>(6);
        _columnsAddictionHabits.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAddictionHabits.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAddictionHabits.put("startDate", new TableInfo.Column("startDate", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAddictionHabits.put("recurrence", new TableInfo.Column("recurrence", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAddictionHabits.put("preferredTime", new TableInfo.Column("preferredTime", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAddictionHabits.put("estimatedMinutes", new TableInfo.Column("estimatedMinutes", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysAddictionHabits = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesAddictionHabits = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoAddictionHabits = new TableInfo("addiction_habits", _columnsAddictionHabits, _foreignKeysAddictionHabits, _indicesAddictionHabits);
        final TableInfo _existingAddictionHabits = TableInfo.read(db, "addiction_habits");
        if (!_infoAddictionHabits.equals(_existingAddictionHabits)) {
          return new RoomOpenHelper.ValidationResult(false, "addiction_habits(com.example.rewire.db.entity.AddictionHabitEntity).\n"
                  + " Expected:\n" + _infoAddictionHabits + "\n"
                  + " Found:\n" + _existingAddictionHabits);
        }
        final HashMap<String, TableInfo.Column> _columnsAbstinenceGoals = new HashMap<String, TableInfo.Column>(5);
        _columnsAbstinenceGoals.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAbstinenceGoals.put("addictionId", new TableInfo.Column("addictionId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAbstinenceGoals.put("recurrence", new TableInfo.Column("recurrence", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAbstinenceGoals.put("value", new TableInfo.Column("value", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAbstinenceGoals.put("repeatCount", new TableInfo.Column("repeatCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysAbstinenceGoals = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysAbstinenceGoals.add(new TableInfo.ForeignKey("addiction_habits", "CASCADE", "NO ACTION", Arrays.asList("addictionId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesAbstinenceGoals = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoAbstinenceGoals = new TableInfo("abstinence_goals", _columnsAbstinenceGoals, _foreignKeysAbstinenceGoals, _indicesAbstinenceGoals);
        final TableInfo _existingAbstinenceGoals = TableInfo.read(db, "abstinence_goals");
        if (!_infoAbstinenceGoals.equals(_existingAbstinenceGoals)) {
          return new RoomOpenHelper.ValidationResult(false, "abstinence_goals(com.example.rewire.db.entity.AbstinenceGoalEntity).\n"
                  + " Expected:\n" + _infoAbstinenceGoals + "\n"
                  + " Found:\n" + _existingAbstinenceGoals);
        }
        final HashMap<String, TableInfo.Column> _columnsHabitNotes = new HashMap<String, TableInfo.Column>(4);
        _columnsHabitNotes.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHabitNotes.put("habitId", new TableInfo.Column("habitId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHabitNotes.put("content", new TableInfo.Column("content", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHabitNotes.put("timestamp", new TableInfo.Column("timestamp", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysHabitNotes = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysHabitNotes.add(new TableInfo.ForeignKey("habits", "CASCADE", "NO ACTION", Arrays.asList("habitId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesHabitNotes = new HashSet<TableInfo.Index>(1);
        _indicesHabitNotes.add(new TableInfo.Index("index_habit_notes_habitId", false, Arrays.asList("habitId"), Arrays.asList("ASC")));
        final TableInfo _infoHabitNotes = new TableInfo("habit_notes", _columnsHabitNotes, _foreignKeysHabitNotes, _indicesHabitNotes);
        final TableInfo _existingHabitNotes = TableInfo.read(db, "habit_notes");
        if (!_infoHabitNotes.equals(_existingHabitNotes)) {
          return new RoomOpenHelper.ValidationResult(false, "habit_notes(com.example.rewire.db.entity.HabitNoteEntity).\n"
                  + " Expected:\n" + _infoHabitNotes + "\n"
                  + " Found:\n" + _existingHabitNotes);
        }
        final HashMap<String, TableInfo.Column> _columnsAddictionNotes = new HashMap<String, TableInfo.Column>(4);
        _columnsAddictionNotes.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAddictionNotes.put("addictionId", new TableInfo.Column("addictionId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAddictionNotes.put("content", new TableInfo.Column("content", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAddictionNotes.put("timestamp", new TableInfo.Column("timestamp", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysAddictionNotes = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysAddictionNotes.add(new TableInfo.ForeignKey("addiction_habits", "CASCADE", "NO ACTION", Arrays.asList("addictionId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesAddictionNotes = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoAddictionNotes = new TableInfo("addiction_notes", _columnsAddictionNotes, _foreignKeysAddictionNotes, _indicesAddictionNotes);
        final TableInfo _existingAddictionNotes = TableInfo.read(db, "addiction_notes");
        if (!_infoAddictionNotes.equals(_existingAddictionNotes)) {
          return new RoomOpenHelper.ValidationResult(false, "addiction_notes(com.example.rewire.db.entity.AddictionNoteEntity).\n"
                  + " Expected:\n" + _infoAddictionNotes + "\n"
                  + " Found:\n" + _existingAddictionNotes);
        }
        final HashMap<String, TableInfo.Column> _columnsHabitCompletions = new HashMap<String, TableInfo.Column>(3);
        _columnsHabitCompletions.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHabitCompletions.put("habitId", new TableInfo.Column("habitId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHabitCompletions.put("date", new TableInfo.Column("date", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysHabitCompletions = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesHabitCompletions = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoHabitCompletions = new TableInfo("habit_completions", _columnsHabitCompletions, _foreignKeysHabitCompletions, _indicesHabitCompletions);
        final TableInfo _existingHabitCompletions = TableInfo.read(db, "habit_completions");
        if (!_infoHabitCompletions.equals(_existingHabitCompletions)) {
          return new RoomOpenHelper.ValidationResult(false, "habit_completions(com.example.rewire.db.entity.HabitCompletion).\n"
                  + " Expected:\n" + _infoHabitCompletions + "\n"
                  + " Found:\n" + _existingHabitCompletions);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "87ec6815dab94ee54ddce466ca23e47a", "326dc02b9d94ab28cde57c1176d5b6e2");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "habits","addiction_habits","abstinence_goals","habit_notes","addiction_notes","habit_completions");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `habits`");
      _db.execSQL("DELETE FROM `addiction_habits`");
      _db.execSQL("DELETE FROM `abstinence_goals`");
      _db.execSQL("DELETE FROM `habit_notes`");
      _db.execSQL("DELETE FROM `addiction_notes`");
      _db.execSQL("DELETE FROM `habit_completions`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(HabitDao.class, HabitDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(AddictionHabitDao.class, AddictionHabitDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(AbstinenceGoalDao.class, AbstinenceGoalDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(HabitNoteDao.class, HabitNoteDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(AddictionNoteDao.class, AddictionNoteDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(HabitCompletionDao.class, HabitCompletionDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public HabitDao habitDao() {
    if (_habitDao != null) {
      return _habitDao;
    } else {
      synchronized(this) {
        if(_habitDao == null) {
          _habitDao = new HabitDao_Impl(this);
        }
        return _habitDao;
      }
    }
  }

  @Override
  public AddictionHabitDao addictionHabitDao() {
    if (_addictionHabitDao != null) {
      return _addictionHabitDao;
    } else {
      synchronized(this) {
        if(_addictionHabitDao == null) {
          _addictionHabitDao = new AddictionHabitDao_Impl(this);
        }
        return _addictionHabitDao;
      }
    }
  }

  @Override
  public AbstinenceGoalDao abstinenceGoalDao() {
    if (_abstinenceGoalDao != null) {
      return _abstinenceGoalDao;
    } else {
      synchronized(this) {
        if(_abstinenceGoalDao == null) {
          _abstinenceGoalDao = new AbstinenceGoalDao_Impl(this);
        }
        return _abstinenceGoalDao;
      }
    }
  }

  @Override
  public HabitNoteDao habitNoteDao() {
    if (_habitNoteDao != null) {
      return _habitNoteDao;
    } else {
      synchronized(this) {
        if(_habitNoteDao == null) {
          _habitNoteDao = new HabitNoteDao_Impl(this);
        }
        return _habitNoteDao;
      }
    }
  }

  @Override
  public AddictionNoteDao addictionNoteDao() {
    if (_addictionNoteDao != null) {
      return _addictionNoteDao;
    } else {
      synchronized(this) {
        if(_addictionNoteDao == null) {
          _addictionNoteDao = new AddictionNoteDao_Impl(this);
        }
        return _addictionNoteDao;
      }
    }
  }

  @Override
  public HabitCompletionDao habitCompletionDao() {
    if (_habitCompletionDao != null) {
      return _habitCompletionDao;
    } else {
      synchronized(this) {
        if(_habitCompletionDao == null) {
          _habitCompletionDao = new HabitCompletionDao_Impl(this);
        }
        return _habitCompletionDao;
      }
    }
  }
}
