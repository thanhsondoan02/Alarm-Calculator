package com.example.alarmcalculator

class CycleData {

    companion object {
        const val MIN_CYCLE = 1
        const val MAX_CYCLE = 5
    }

    var cycle = 5

    fun plusCycle() {
        if (cycle < MAX_CYCLE) {
            cycle++
        }
    }

    fun minusCycle() {
        if (cycle > MIN_CYCLE) {
            cycle--
        }
    }

    override fun toString(): String {
        return "$cycle"
    }
}
