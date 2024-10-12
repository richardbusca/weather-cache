package com.example.taks

object TaskRegistry {
    private val tasks = mutableListOf<ScheduledTask>()

    fun register(task: ScheduledTask) {
        tasks.add(task)
    }

    fun getTasks(): List<ScheduledTask> = tasks
}