package com.example.alarmcalculator

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Dialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.util.*


const val REP_DELAY = 100L

@RequiresApi(Build.VERSION_CODES.S)
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
    private lateinit var btnSetAlarm: Button

    private lateinit var listener: IListener
    private lateinit var startTime: Time
    private lateinit var cycleData: CycleData
    private lateinit var alarmAt: Time

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
        btnSetAlarm = findViewById(R.id.btnMainSetAlarm)
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

        btnSetAlarm.setOnClickListener {
            setAlarm()
        }
    }

    private fun updateAlarmAt() {
        alarmAt = startTime.copy()
        alarmAt.minutePlus(cycleData.cycle * 90 + 20)
        updateTimeView()
    }

    @SuppressLint("SetTextI18n")
    private fun updateTimeView() {
        tvSleepTime.text = startTime.toString()
        tvAlarmTime.text = alarmAt.toString()
    }

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
        picker.displayedValues = Array(max - min + 1) { i ->
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

    private fun setAlarm() {
//        // Get AlarmManager instance
//        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        // Intent part
//        val intent = Intent(this, AlarmReceiver::class.java).apply {
//            action = "ALARM_ACTION"
//            putExtra("check_alarm", "Setting alarm successfully")
//        }
//        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_MUTABLE)
//        // Alarm time
//        val ALARM_DELAY_IN_SECOND = 1
//        val alarmTimeAtUTC = System.currentTimeMillis() + ALARM_DELAY_IN_SECOND * 1_000L
//        // Set with system Alarm Service
//        // Other possible functions: setExact() / setRepeating() / setWindow(), etc
//        alarmManager.setExactAndAllowWhileIdle(
//            AlarmManager.RTC_WAKEUP,
//            alarmTimeAtUTC,
//            pendingIntent
//        )
//
        createAlarm()
    }

    private lateinit var alarmManager: AlarmManager
    private fun getAlarmIntent(): Intent {
        val intent = Intent(this, AlarmReceiver::class.java)
        intent.action = ALARM_RECEIVER_ACTION
        return intent
    }
    fun createAlarm() {
//        val sender = PendingIntent.getBroadcast(this, REQUEST_CODE, getAlarmIntent(), 0)
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = PendingIntent.getBroadcast(this, REQUEST_CODE, intent, PendingIntent.FLAG_MUTABLE)
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmAt.toMillis(), pendingIntent)
    }
    fun cancelAlarm(){
        val sender = PendingIntent.getBroadcast(this, REQUEST_CODE, getAlarmIntent(), 0)
        alarmManager.cancel(sender)
    }

}
const val REQUEST_CODE = 5122
const val ALARM_RECEIVER_ACTION = "ALARM_RECEIVER_ACTION"
