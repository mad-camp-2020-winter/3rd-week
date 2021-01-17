package com.example.bongorghini.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import java.lang.Exception

class PowerConnectionReceiver (batteryResultCallback: BatteryResultCallback?) : BroadcastReceiver() {

    private var batteryResultCallback: BatteryResultCallback

    init {
        this.batteryResultCallback = batteryResultCallback!!
    }

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.\

        try {
            var status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            var isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING
                    || status == BatteryManager.BATTERY_STATUS_FULL

            batteryResultCallback.callDelegate(isCharging)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}