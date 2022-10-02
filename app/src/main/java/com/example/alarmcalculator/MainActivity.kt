package com.example.alarmcalculator

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.view.MotionEvent
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

const val REP_DELAY = 100L

class MainActivity : AppCompatActivity() {

    private lateinit var tvStartHour: TextView
    private lateinit var tvStartMinute: TextView
    private lateinit var tvCycle: TextView
    private lateinit var tvInNext: TextView
    private lateinit var tvAlarmAt: TextView
    private lateinit var ivStartHourPlus: ImageButton
    private lateinit var ivStartHourMinus: ImageButton
    private lateinit var ivStartMinutePlus: ImageButton
    private lateinit var ivStartMinuteMinus: ImageButton
    private lateinit var ivCyclePlus: ImageButton
    private lateinit var ivCycleMinus: ImageButton
    private lateinit var btnSleepNow: Button

    private lateinit var startTime: Time
    private lateinit var cycleData: CycleData
    private lateinit var alarmAt: Time

    private val repeatUpdateHandler: Handler = Handler()
    private var mAutoIncrement = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initTimeVar()
        initView()
        setOnClick()
        updateTimeView()
    }

    private fun initTimeVar() {
        startTime = Time()
        cycleData = CycleData()
        alarmAt = Time()
        alarmAt.minutePlus(cycleData.cycle * 90 + 20)
    }

    private fun initView() {
        tvStartHour = findViewById(R.id.tvMainStartHour)
        tvStartMinute = findViewById(R.id.tvMainStartMinute)
        tvCycle = findViewById(R.id.tvMainCycle)
        tvInNext = findViewById(R.id.tvMainInNext)
        tvAlarmAt = findViewById(R.id.tvMainAlarmAt)
        ivStartHourPlus = findViewById(R.id.ivMainStartHourPlus)
        ivStartHourMinus = findViewById(R.id.ivMainStartHourMinus)
        ivStartMinutePlus = findViewById(R.id.ivMainStartMinutePlus)
        ivStartMinuteMinus = findViewById(R.id.ivMainStartMinuteMinus)
        ivCyclePlus = findViewById(R.id.ivMainCyclePlus)
        ivCycleMinus = findViewById(R.id.ivMainCycleMinus)
        btnSleepNow = findViewById(R.id.btnMainSleepNow)
    }

    private fun setOnClick() {
        btnSleepNow.setClick(UPDATE_TYPE.SLEEP_NOW)
        ivStartHourPlus.setClick(UPDATE_TYPE.HOUR_PLUS)
        ivStartHourMinus.setClick(UPDATE_TYPE.HOUR_MINUS)
        ivStartMinutePlus.setClick(UPDATE_TYPE.MINUTE_PLUS)
        ivStartMinuteMinus.setClick(UPDATE_TYPE.MINUTE_MINUS)
        ivCyclePlus.setClick(UPDATE_TYPE.CYCLE_PLUS)
        ivCycleMinus.setClick(UPDATE_TYPE.CYCLE_MINUS)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun ImageButton.setClick(updateType: UPDATE_TYPE) {

        this.setOnClickListener {
            update(updateType)
        }

        this.setOnLongClickListener {
            mAutoIncrement = true
            repeatUpdateHandler.post(RptUpdater(updateType))
            false
        }

        this.setOnTouchListener { _, event ->
            if ((event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL)
                && mAutoIncrement
            ) {
                mAutoIncrement = false
            }
            false
        }
    }

    private fun Button.setClick(updateType: UPDATE_TYPE) {
        this.setOnClickListener {
            update(updateType)
        }
    }

    private fun update(updateType: UPDATE_TYPE) {
        when (updateType) {
            UPDATE_TYPE.SLEEP_NOW -> {
                startTime = Time()
            }
            UPDATE_TYPE.HOUR_PLUS -> {
                startTime.plusHourWithoutMinuteChange()
            }
            UPDATE_TYPE.HOUR_MINUS -> {
                startTime.minusHourWithoutMinuteChange()
            }
            UPDATE_TYPE.MINUTE_PLUS -> {
                startTime.plusMinuteWithoutHourChange()
            }
            UPDATE_TYPE.MINUTE_MINUS -> {
                startTime.minusMinuteWithoutHourChange()
            }
            UPDATE_TYPE.CYCLE_PLUS -> {
                cycleData.plusCycle()
            }
            UPDATE_TYPE.CYCLE_MINUS -> {
                cycleData.minusCycle()
            }
        }
        updateAlarmAt()
        updateTimeView()
    }

    private fun updateAlarmAt() {
        alarmAt = startTime.copy()
        alarmAt.minutePlus(cycleData.cycle * 90 + 20)
    }

    @SuppressLint("SetTextI18n")
    private fun updateTimeView() {
        tvStartHour.text = startTime.getHourString()
        tvStartMinute.text = startTime.getMinuteString()
        tvCycle.text = cycleData.toString()
        tvInNext.text = "You are going to sleep in next ${startTime.inNextToString()})"
        tvAlarmAt.text = "You should set alarm at $alarmAt"
    }

    inner class RptUpdater(private val updateType: UPDATE_TYPE) : Runnable {
        override fun run() {
            if (mAutoIncrement) {
                update(updateType)
                repeatUpdateHandler.postDelayed(RptUpdater(updateType), REP_DELAY)
            }
        }
    }
}
