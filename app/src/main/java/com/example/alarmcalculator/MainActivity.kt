package com.example.alarmcalculator

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

const val REP_DELAY = 100L

class MainActivity : AppCompatActivity() {

    interface IListener {
        fun onCycleChanged(group: RadioGroup, checkedId: Int)
        fun onSleepTimeChange()
        fun onSleepTimeClicked()
    }

    private lateinit var tvAlarmAt: TextView
    private lateinit var btnSleepNow: Button
    private lateinit var tvSleepTime: TextView
    private lateinit var tvAlarmTime: TextView
    private lateinit var rdGrpSleepCycle: RadioGroup

    private lateinit var listener: IListener
    private lateinit var startTime: Time
    private lateinit var cycleData: CycleData
    private lateinit var alarmAt: Time

    private val repeatUpdateHandler: Handler = Handler()
    private var mAutoIncrement = false
    private val dialog by lazy {
        getChooseDialog()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        initTimeVar()
        initView()
        setOnClick()
        updateTimeView()
        initListener()
    }

    private fun initTimeVar() {
        startTime = Time()
        cycleData = CycleData()
        alarmAt = Time()
        alarmAt.minutePlus(cycleData.cycle * 90 + 20)
    }

    private fun initView() {
        tvAlarmAt = findViewById(R.id.tvMainAlarmAt)
        btnSleepNow = findViewById(R.id.btnMainSleepNow)
        tvAlarmTime = findViewById(R.id.tvMainAlarmTime)
        tvSleepTime = findViewById(R.id.tvMainSleepTime)
        rdGrpSleepCycle = findViewById(R.id.rdGrpMainSleepCycle)
    }

    private fun setOnClick() {

        tvSleepTime.setOnClickListener {
            listener.onSleepTimeClicked()
        }

        rdGrpSleepCycle.setOnCheckedChangeListener { group, checkedId ->
            listener.onCycleChanged(group, checkedId)
        }

        btnSleepNow.setOnClickListener {
            startTime = Time()
            listener.onSleepTimeChange()
        }
    }

//    @SuppressLint("ClickableViewAccessibility")
//    private fun ImageButton.setClick(updateType: UPDATE_TYPE) {
//
//        this.setOnClickListener {
//            update(updateType)
//        }
//
//        this.setOnLongClickListener {
//            mAutoIncrement = true
//            repeatUpdateHandler.post(RptUpdater(updateType))
//            false
//        }
//
//        this.setOnTouchListener { _, event ->
//            if ((event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL)
//                && mAutoIncrement
//            ) {
//                mAutoIncrement = false
//            }
//            false
//        }
//    }

//    private fun Button.setClick(updateType: UPDATE_TYPE) {
//        this.setOnClickListener {
//            update(updateType)
//        }
//    }

//    private fun update(updateType: UPDATE_TYPE) {
//        when (updateType) {
//            UPDATE_TYPE.SLEEP_NOW -> {
//                startTime = Time()
//            }
//            UPDATE_TYPE.HOUR_PLUS -> {
//                startTime.plusHourWithoutMinuteChange()
//            }
//            UPDATE_TYPE.HOUR_MINUS -> {
//                startTime.minusHourWithoutMinuteChange()
//            }
//            UPDATE_TYPE.MINUTE_PLUS -> {
//                startTime.plusMinuteWithoutHourChange()
//            }
//            UPDATE_TYPE.MINUTE_MINUS -> {
//                startTime.minusMinuteWithoutHourChange()
//            }
//            UPDATE_TYPE.CYCLE_PLUS -> {
//                cycleData.plusCycle()
//            }
//            UPDATE_TYPE.CYCLE_MINUS -> {
//                cycleData.minusCycle()
//            }
//        }
//        updateAlarmAt()
//        updateTimeView()
//    }

    private fun updateAlarmAt() {
        alarmAt = startTime.copy()
        alarmAt.minutePlus(cycleData.cycle * 90 + 20)
        updateTimeView()
    }

    @SuppressLint("SetTextI18n")
    private fun updateTimeView() {
//        tvInNext.text = "You are going to sleep in next ${startTime.inNextToString()}"
//        tvAlarmAt.text = "You should set alarm at $alarmAt"

        tvSleepTime.text = startTime.toString()
        tvAlarmTime.text = alarmAt.toString()
    }

//    inner class RptUpdater(private val updateType: UPDATE_TYPE) : Runnable {
//        override fun run() {
//            if (mAutoIncrement) {
//                update(updateType)
//                repeatUpdateHandler.postDelayed(RptUpdater(updateType), REP_DELAY)
//            }
//        }
//    }

    private fun initListener() {
        listener = object : IListener {
            override fun onCycleChanged(group: RadioGroup, checkedId: Int) {
                val rdBtnChecked: RadioButton = group.findViewById(checkedId)
                cycleData.cycle = rdBtnChecked.text.toString().toInt()
                updateAlarmAt()
            }

            override fun onSleepTimeChange() {
                updateAlarmAt()
                updateTimeView()
            }

            override fun onSleepTimeClicked() {
                getChooseDialog().show()
            }


        }
    }

    @SuppressLint("InflateParams", "SetTextI18n")
    private fun getChooseDialog(): Dialog {
        val dialog = Dialog(this)

        val view = layoutInflater.inflate(R.layout.choose_layout, null)

        val npHour = view.findViewById<NumberPicker>(R.id.npChooseHourPicker)
        setNumberPicker(npHour, 0, 23, startTime.hour)

        val npMinute = view.findViewById<NumberPicker>(R.id.npChooseMinutePicker)
        setNumberPicker(npMinute, 0, 59, startTime.minute)

        val btnCancel = view.findViewById<Button>(R.id.btnChooseCancel)
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        val btnDone = view.findViewById<Button>(R.id.btnChooseDone)
        btnDone.setOnClickListener {
            startTime.hour = npHour.value
            startTime.minute = npMinute.value
            listener.onSleepTimeChange()
            dialog.dismiss()
        }

        val tvSleepTime = view.findViewById<TextView>(R.id.tvChooseSleepTime)
        val tvSleepIn = view.findViewById<TextView>(R.id.tvChooseSleepIn)
        tvSleepTime.text = startTime.toString()
        if (startTime.inNext() != 0)
            tvSleepIn.text = (getString(R.string.sleep_in) + " " + startTime.inNextToString())
        else
            tvSleepIn.text = "Sleep now"

        npHour.setOnValueChangedListener { picker, oldVal, newVal ->
            val time = Time().apply {
                hour = npHour.value
                minute = npMinute.value
            }
            tvSleepTime.text = time.toString()
            if (time.inNext() != 0)
                tvSleepIn.text = (getString(R.string.sleep_in) + " " + time.inNextToString())
            else
                tvSleepIn.text = "Sleep now"
        }

        npMinute.setOnValueChangedListener { picker, oldVal, newVal ->
            val time = Time().apply {
                hour = npHour.value
                minute = npMinute.value
            }
            tvSleepTime.text = time.toString()
            if (time.inNext() != 0)
                tvSleepIn.text = (getString(R.string.sleep_in) + " " + time.inNextToString())
            else
                tvSleepIn.text = "Sleep now"
        }

        dialog.setContentView(view)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        return dialog
    }

    private fun setNumberPicker(picker: NumberPicker, min: Int, max: Int, value: Int) {
        picker.displayedValues = Array(max-min+1) { i ->
            if (i < 10)
                "0$i"
            else
                "$i"
        }

        picker.minValue = min
        picker.maxValue = max
        picker.value = value
        picker.showDividers = NumberPicker.SHOW_DIVIDER_NONE
        picker.textSize *= 2f
        picker.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS;
    }

}
