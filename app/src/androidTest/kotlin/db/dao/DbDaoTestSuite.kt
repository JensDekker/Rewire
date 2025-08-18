package com.example.rewire.db.dao

import org.junit.runner.RunWith
import org.junit.runners.Suite
import org.junit.runners.Suite.SuiteClasses

@RunWith(Suite::class)
@SuiteClasses(
    HabitDaoTest::class,
    AddictionHabitDaoTest::class,
    AbstinenceGoalDaoTest::class,
    HabitNoteDaoTest::class,
    AddictionNoteDaoTest::class,
    HabitCompletionDaoTest::class
)
class DbDaoTestSuite
