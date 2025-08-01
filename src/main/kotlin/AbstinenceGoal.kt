enum class GoalPeriod {
    WEEKLY, MONTHLY
}

data class AbstinenceGoal(val period: GoalPeriod, val value: Int, val repeatCount: Int = 1)
