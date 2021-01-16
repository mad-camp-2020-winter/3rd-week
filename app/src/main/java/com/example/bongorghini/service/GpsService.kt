package com.example.bongorghini.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.*
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.bongorghini.MainActivity
import com.example.bongorghini.R
import com.example.bongorghini.utils.GpsTracker
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.timer

class GpsService(): Service(), LocationListener {
    val CHANNEL_ID = "GPSForegroundServiceChannel"
    val NOTIFICATION_ID = 102

    var formattedTime: String? = null

    private val myBinder = MyBinder()
    lateinit var myContext: Context
    private val locationManager: LocationManager by lazy {
        myContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }
    private lateinit var gpsTracker: GpsTracker

    private val dt: Long = 1000

    private var location_temp: Location? = null
    private var speed_kph: Double? = null

    private var temp = 0

    inner class MyBinder: Binder() {
        fun getService(): GpsService = this@GpsService
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        createNotificationChannel()
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_REDELIVER_INTENT
    }

    override fun onBind(intent: Intent?): IBinder? {
        return myBinder
    }

    override fun onLocationChanged(location: Location) {
        TODO("Not yet implemented")
    }

    fun startForeGroundService() {
        val notificationIntent = Intent(this, MainActivity::class.java)
        notificationIntent.putExtra("tabIndex", 2)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = createNotification(pendingIntent)
        startForeground(NOTIFICATION_ID, notification)


        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        timer(period = dt) {
            var mHandler = Handler(Looper.getMainLooper())
            mHandler.postDelayed(Runnable {
                gpsTracker = GpsTracker(myContext)
                val location_curr = gpsTracker.getLocation()

                if (location_curr != null) {
                    if (location_temp != null) {
                        val speed_mps: Double = (location_curr!!.distanceTo(location_temp).toDouble()) / (dt / 1000).toDouble()
                        speed_kph = mps_to_kph(speed_mps)

                        location_temp = location_curr
                        notificationManager.notify(NOTIFICATION_ID, createNotification(pendingIntent))
                        temp++
                    } else {
                        location_temp = location_curr
                    }

                }

                val simpleDateFormat = SimpleDateFormat("yy/MM/dd kk:mm:ss")
                simpleDateFormat.timeZone = TimeZone.getTimeZone("GMT+9")

                formattedTime = simpleDateFormat.format(location_curr!!.time)

                Log.d("Location curr", formattedTime!!)
                Log.d("Location temp", location_temp.toString())
                Log.d("Speed", speed_kph.toString())
            }, 0)

        }

    }

    fun stopForegroundService() {
        stopForeground(true)
    }

    private fun createNotification(pendingIntent: PendingIntent): Notification {
        val notification = androidx.core.app.NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Bongorghini")
            .setContentText("$speed_kph km/h" + temp.toString() + formattedTime)
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

    fun mps_to_kph (v_mps: Double): Double {
        return v_mps * 3.6
    }
}