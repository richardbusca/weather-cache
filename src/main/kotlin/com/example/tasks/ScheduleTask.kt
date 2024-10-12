package com.example.tasks

import java.util.Timer
import java.util.TimerTask

/**
 * Schedules a [TimerTask] to be executed periodically.
 *
 * This function uses a [Timer] to schedule the provided task with an initial delay
 * and a specified period between successive executions.
 *
 * @param task The [TimerTask] to be scheduled for execution.
 * @param delay The initial delay in milliseconds before the task is executed for the first time.
 * @param period The period in milliseconds between successive executions of the task.
 */
fun scheduleTask(task: TimerTask, delay: Long, period: Long){
    Timer().schedule(task, delay, period)
}