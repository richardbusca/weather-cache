package com.example.tasks

import java.util.TimerTask

/**
 * Abstract class representing a scheduled task that can be executed periodically.
 *
 * This class extends [TimerTask] and requires implementation of the task's execution logic,
 * as well as the delay and period for scheduling.
 */
abstract class ScheduledTask : TimerTask() {

    /**
     * Executes the task logic.
     *
     * This method should contain the code to be executed when the task runs.
     */
    abstract fun execute()

    /**
     * Returns the initial delay before the task is executed for the first time.
     *
     * @return The delay in milliseconds.
     */
    abstract fun getDelay(): Long

    /**
     * Returns the period between successive executions of the task.
     *
     * @return The period in milliseconds.
     */
    abstract fun getPeriod(): Long

    /**
     * Runs the task by calling the [execute] method.
     * This method is overridden from [TimerTask] to define the task's execution logic.
     */
    override fun run() {
        execute()
    }
}