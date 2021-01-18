package com.example.bongorghini.service

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.bongorghini.MainActivity
import com.example.bongorghini.R
import com.example.bongorghini.fragment.OrderListFragment
import com.example.bongorghini.utils.BatteryResultCallback
import com.example.bongorghini.utils.PowerConnectionReceiver


class AutoExecuteService() : Service() {

    private val myBinder = MyBinder()
    lateinit var mList: ArrayList<com.example.bongorghini.model.Application>
    private lateinit var mCaller : OrderListFragment
    lateinit var myContext: Context
    var powerConnectionReceiver: PowerConnectionReceiver

    var serviceActivated: Boolean = false

    var isChargingGlobal = true
    val CHANNEL_ID = "AutoExecute Service Activated"

    lateinit var batteryStatus: Intent

    inner class MyBinder: Binder() {
        fun getService(caller : OrderListFragment) : AutoExecuteService {
            mCaller = caller
            return this@AutoExecuteService
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return myBinder
    }

    init {
        powerConnectionReceiver =
            PowerConnectionReceiver(object : BatteryResultCallback {
                override fun callDelegate(isCharging: Boolean) {

//                    Toast.makeText(myContext,
//                            "serviceActivated $serviceActivated" + if (isCharging) "충전중" else "Cable 연결안됨",
//                            Toast.LENGTH_SHORT).show()
                    if (serviceActivated) {
                        Log.d("AUtoExecuteService", "autostart $isCharging")
                        if (isCharging && !isChargingGlobal) autoStart()
//                        else turnOffScreen()
                    }
                    isChargingGlobal = isCharging

//                    startForegroundService()
                }
            })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onDestroy() {
        serviceActivated = false
        stopForeground(true)
        super.onDestroy()
    }

    fun autoStart() {
//        Toast.makeText(this, mList.toString(), Toast.LENGTH_SHORT).show()
        Log.d("AutoExecuteService", "autostart")

        for (i in 0..mList.size - 1) {
            if (serviceActivated) {
                val pm = packageManager
                val intent = pm.getLaunchIntentForPackage(mList.get(i).path!!)
                intent!!.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)

                Log.d("test", mList.get(i).path!!)
                Thread.sleep(5000)
            }
        }


    }

    fun stopForegroundService() {
        Toast.makeText(myContext,
                "stopForegroundService",
                Toast.LENGTH_SHORT).show()
        serviceActivated = false
        stopForeground(true)
    }

    fun startForegroundService() {
        //Notification 설정
        Toast.makeText(myContext,
                "startForegroundService",
                Toast.LENGTH_SHORT).show()
        val notificationIntent = Intent(this, MainActivity::class.java)
        notificationIntent.putExtra("tabIndex", 2)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = createNotification(pendingIntent)
        val NOTIFICATION_ID = 103
        startForeground(NOTIFICATION_ID, notification)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID,notification)

        batteryStatus = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let {
                intentFilter -> myContext.registerReceiver(powerConnectionReceiver, intentFilter)
        }!!

        serviceActivated = true
    }

    @SuppressLint("InvalidWakeLockTag")
    private fun turnOffScreen() {
        Thread.sleep(3000)
        if(!isChargingGlobal){
            var manager = getSystemService(Context.POWER_SERVICE) as PowerManager
            val wl = manager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Your Tag")
            wl.acquire()
            wl.release()
        }
    }

    private fun createNotification(pendingIntent: PendingIntent): Notification {
        val notification = androidx.core.app.NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Bongorghini")
            .setContentText("Auto Execution activated")
            .setSmallIcon(R.drawable.bongorghini_logo)
            .setOngoing(true)
            .setNotificationSilent()
            .setContentIntent(pendingIntent)
            .setOnlyAlertOnce(true)
            .build()
        return notification
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "name"
            val descriptionText = "description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}