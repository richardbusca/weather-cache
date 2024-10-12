package com.example.tasks

/**
 * Registry for managing scheduled tasks.
 *
 * This object provides functionality to register and retrieve scheduled tasks.
 */
object TaskRegistry {
    private val tasks = mutableListOf<ScheduledTask>()

    /**
     * Registers a new scheduled task in the registry.
     *
     * @param task The [ScheduledTask] to be registered.
     */
    fun register(task: ScheduledTask) {
        tasks.add(task)
    }

    /**
     * Retrieves the list of registered scheduled tasks.
     *
     * @return A list of [ScheduledTask] objects currently registered in the registry.
     */
    fun getTasks(): List<ScheduledTask> = tasks
}