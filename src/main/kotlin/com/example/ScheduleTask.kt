package com.example

import java.util.Timer
import java.util.TimerTask

fun scheduleTask(task: TimerTask, delay: Long, period: Long){
    Timer().schedule(task, delay, period)
}