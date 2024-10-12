package com.example.tasks

import java.util.TimerTask

abstract class ScheduledTask : TimerTask() {
    abstract fun execute()
    abstract fun getDelay(): Long
    abstract fun getPeriod(): Long
    override fun run() {
        execute()
    }
}