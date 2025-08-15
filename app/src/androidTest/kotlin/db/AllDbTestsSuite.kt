package com.example.rewire.db

import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(
    AbstinenceGoalDaoTest::class,
    AddictionHabitDaoTest::class,
    HabitCompletionDaoTest::class,
    HabitDaoTest::class,
    HabitNoteDaoTest::class,
    IntegrationTest::class,
    RecurrenceTypeConverterTest::class,
    RewireDatabaseTest::class
)
class AllDbTestsSuite
