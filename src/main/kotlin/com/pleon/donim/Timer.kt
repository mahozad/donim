package com.pleon.donim

import java.time.Duration

class Timer(duration: Duration) {

    var isStarted = false

    fun start() {
        isStarted = true
    }
}
