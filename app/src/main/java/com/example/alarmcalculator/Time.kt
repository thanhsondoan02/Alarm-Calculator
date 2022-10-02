package com.example.alarmcalculator

import java.util.*

val TAG = "Peswoc"

fun main() {
    testOnNext()
}

fun testMinuteMinus() {
    for (i in 0..120 step 10) {
        val time = Time()
        print("Minus $time with $i = ")
        time.minuteMinus(i)
        println(time)
    }
}

fun testOnNext() {
    for (i in 60..60*24 step 60) {
        val time = Time()
        print("Current time is $time and next time is ")
        time.minutePlus(i)
        println("$time => In next ${time.inNextToString()}")
    }
}

fun Int.getTimeForm(): String {
    val number = this.plus(0)
    return if (number in 0..9) {
        "0$number"
    } else {
        number.toString()
    }
}

class Time {
    private var hour: Int
    private var minute: Int

    init {
        hour = getCurrentHour()
        minute = getCurrentMinute()
    }

    override fun toString(): String {
        return "${getHourString()}:${getMinuteString()}"
    }

    fun copy(): Time {
        val ans = Time()
        ans.hour = this.hour
        ans.minute = this.minute
        return ans
    }

    fun getHourString(): String {
        return hour.getTimeForm()
    }

    fun getMinuteString(): String {
        return minute.getTimeForm()
    }

    fun hourPlus(number: Int) {
        hour = (hour + number) % 24
    }

    fun hourMinus(number: Int) {
        hour = (hour - number + 24) % 24
    }

    fun minutePlus(number: Int) {
        hourPlus((minute + number) / 60)
        minute = (minute + number) % 60
    }

    fun minuteMinus(number: Int) {
        hourMinus(number / 60)
        if (this.minute - number % 60 < 0) {
            hourMinus(1)
        }

        this.minute = (this.minute - number % 60 + 60) % 60
    }

    fun getCurrentHour(): Int {
        val calendar = Calendar.getInstance()
        return calendar.get(Calendar.HOUR_OF_DAY)
    }

    fun getCurrentMinute(): Int {
        val calendar = Calendar.getInstance()
        return calendar.get(Calendar.MINUTE)
    }

    fun plusMinuteWithoutHourChange() {
        minute = (minute + 1) % 60
    }

    fun minusMinuteWithoutHourChange() {
        minute = (minute + 59) % 60
    }

    fun plusHourWithoutMinuteChange() {
        hour = (hour + 1) % 24
    }

    fun minusHourWithoutMinuteChange() {
        hour = (hour + 23) % 24
    }

//    private fun getTimeString(number: Int) : String {
//        return if (number.toString().length == 1) {
//            "0$number"
//        } else {
//            number.toString()
//        }
//    }

    fun inNext(): Int {
        val currentTime = Time()
        val currentTimeToMinute = currentTime.hour * 60 + currentTime.minute
        val thisTimeToMinute = hour * 60 + minute
        return if (thisTimeToMinute >= currentTimeToMinute) {
            thisTimeToMinute - currentTimeToMinute
        } else {
            thisTimeToMinute + 24 * 60 - currentTimeToMinute
        }
    }

    fun inNextToString(): String {
        val inNext = inNext()
        return "${(inNext/60).getTimeForm()}h${(inNext%60).getTimeForm()}m"
    }
}
