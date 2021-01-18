package com.example.bongorghini.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.*
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.bongorghini.MainActivity
import com.example.bongorghini.R
import com.example.bongorghini.utils.BatteryResultCallback
import com.example.bongorghini.utils.GpsTracker
import com.example.bongorghini.utils.PowerConnectionReceiver
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.timer
import kotlin.math.abs
import kotlin.math.max


class GpsService(): Service(), LocationListener {
    val CHANNEL_ID = "GPSForegroundServiceChannel"
    val NOTIFICATION_ID = 102

    var formattedTime: String? = null

    private val myBinder = MyBinder()
    lateinit var myContext: Context
//    private val locationManager: LocationManager by lazy {
//        myContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
//    }
    private lateinit var gpsTracker: GpsTracker

    private val dt: Long = 500

    private var location_temp: Location? = null

    private var speed_kph_temp: Double? = null
    private var speed_kph: Double? = null

    lateinit var timerTask: Timer

    private var temp = 0

    lateinit var mediaPlayerOnStart: MediaPlayer
    lateinit var mediaPlayerOnAccel: MediaPlayer
    lateinit var mediaPlayerOnDecel: MediaPlayer
    var currentSound = "Null"

//    var powerConnectionReceiver: PowerConnectionReceiver
    lateinit var batteryStatus: Intent

    val debugVelocity = listOf<Double>(
        0.0, 1.0, 2.0,
        3.0, 4.0, 5.0, 36.0, 37.0, 38.0, 79.0, 80.0, 81.0, 92.0, 103.0,
        104.0, 95.0, 76.0,
        0.0,
        77.0, 38.0, 39.0, 38.0, 27.0, 16.0, 15.0, 14.0, 13.0, 12.0, 11.0, 10.0, 10.1, 10.2, 10.3, 10.4,
            10.5, 10.6, 10.7, 10.8, 10.9,
        9.0, 8.0, 7.0, 6.0, 5.0, 4.0, 3.0, 2.0, 1.0, 0.0
    )

    var maxVolumeIndex: Int? = null

    var volumeControlActive = false

    inner class MyBinder: Binder() {
        fun getService(): GpsService = this@GpsService
    }

//    init {
//        powerConnectionReceiver =
//            PowerConnectionReceiver(object : BatteryResultCallback {
//                override fun callDelegate(isCharging: Boolean) {
//                    Toast.makeText(myContext, if (isCharging) "충전중" else "Cable 연결안됨", Toast.LENGTH_SHORT).show()
//                }
//            })
//    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        createNotificationChannel()

        mediaPlayerOnStart = MediaPlayer.create(this, R.raw.start_revised)
        mediaPlayerOnAccel = MediaPlayer.create(this, R.raw.acceleration)
        mediaPlayerOnDecel = MediaPlayer.create(this, R.raw.deceleration)

        mediaPlayerOnStart.isLooping = true
        mediaPlayerOnAccel.isLooping = true
        mediaPlayerOnDecel.isLooping = true

//        mediaPlayerOnStart.start()
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
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = createNotification(pendingIntent)
        startForeground(NOTIFICATION_ID, notification)


        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        maxVolumeIndex = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

//        batteryStatus = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let {
//                intentFilter -> myContext.registerReceiver(powerConnectionReceiver, intentFilter)
//        }!!

        timerTask = timer(period = dt) {
            var mHandler = Handler(Looper.getMainLooper())
            mHandler.postDelayed(Runnable {
                gpsTracker = GpsTracker(myContext)
                val location_curr = gpsTracker.getLocation()

                if (location_curr != null) {
                    val speed_mps = location_curr.speed.toDouble()
//                    speed_kph = mps_to_kph(speed_mps)
                    speed_kph = debugVelocity[temp]

                    val status = getSpeedStatus(speed_kph_temp, speed_kph!!, dt)
                    setSound(status)

                    notificationManager.notify(
                        NOTIFICATION_ID,
                        createNotification(pendingIntent)
                    )

                    speed_kph_temp = speed_kph
                }

                // 속도로 볼륨 컨트롤
                if (volumeControlActive) {

                    if (speed_kph!! < 10.0 && speed_kph!! != 0.0) {
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolumeIndex!! / 2, 0)
                    } else if (speed_kph!! < 20.0) {
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolumeIndex!! / 2 + 1, 0)
                    } else if (speed_kph!! < 30.0) {
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolumeIndex!! / 2 + 2, 0)
                    } else if (speed_kph!! < 40.0) {
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolumeIndex!! / 2 + 3, 0)
                    } else if (speed_kph!! < 60.0) {
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolumeIndex!! / 2 + 4, 0)
                    } else if (speed_kph!! < 80.0) {
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolumeIndex!! / 2 + 5, 0)
                    } else if (speed_kph!! < 100.0) {
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolumeIndex!! / 2 + 6, 0)
                    }else if (speed_kph!! >= 100.0) {
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolumeIndex!! / 2 + 7, 0)
                    }
                }


                // 로그 찍기
                val simpleDateFormat = SimpleDateFormat("yy/MM/dd kk:mm:ss")
                simpleDateFormat.timeZone = TimeZone.getTimeZone("GMT+9")


                formattedTime = simpleDateFormat.format(location_curr!!.time)

                Log.d("Location curr", formattedTime!!)
                Log.d("Location temp", location_temp.toString())
                Log.d("Speed", speed_kph.toString())
                temp++
            }, 0)

        }

    }

    fun stopForegroundService() {
        mediaPlayerOnDecel.stop()
        mediaPlayerOnAccel.stop()
        mediaPlayerOnStart.stop()
        mediaPlayerOnDecel.prepare()
        mediaPlayerOnAccel.prepare()
        mediaPlayerOnStart.prepare()

        currentSound = "Null"

        timerTask.cancel()
        stopForeground(true)
    }

    private fun createNotification(pendingIntent: PendingIntent): Notification {
        val notification = androidx.core.app.NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Bongorghini")
            .setContentText(String.format("%.1f km/h, ", speed_kph_temp) + String.format("%.1f km/h, ", speed_kph) + temp.toString() + formattedTime)
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

    fun mps_to_kph(v_mps: Double): Double {
        return v_mps * 3.6
    }

    fun carStartSound() {
        Log.d("Sound", "carStartSound")
        when (currentSound) {
            "Accel" -> {
                mediaPlayerOnAccel.stop()
                mediaPlayerOnAccel.prepare()
                mediaPlayerOnStart.start()
            }
            "Decel" -> {
                mediaPlayerOnDecel.stop()
                mediaPlayerOnDecel.prepare()
                mediaPlayerOnStart.start()
            }
            "Null" -> {
                mediaPlayerOnStart.start()
            }
        }
        currentSound = "Start"
    }

    fun carAccelSound() {
        when (currentSound) {
            "Start" -> {
                mediaPlayerOnStart.stop()
                mediaPlayerOnStart.prepare()
                mediaPlayerOnAccel.start()
            }
            "Decel" -> {
                mediaPlayerOnDecel.stop()
                mediaPlayerOnDecel.prepare()
                mediaPlayerOnAccel.start()
            }
            "Null" -> {
            mediaPlayerOnAccel.start()
            }
        }
        currentSound = "Accel"
    }

    fun carDecelSound() {
        when (currentSound) {
            "Start" -> {
                mediaPlayerOnStart.stop()
                mediaPlayerOnStart.prepare()
                mediaPlayerOnDecel.start()
            }
            "Accel" -> {
                mediaPlayerOnAccel.stop()
                mediaPlayerOnAccel.prepare()
                mediaPlayerOnDecel.start()
            }
            "Null" -> {
                mediaPlayerOnDecel.start()
            }
        }
        currentSound = "Decel"
    }

    fun getSpeedStatus(speed1: Double?, speed2: Double, deltaT: Long): Int {
//        0: 정지중, 1: 가속중, 2: 감속중
        if (speed1 == null || speed2 == 0.0) {
            return -1
        } else if (speed2 < 10 || abs(speed2 - speed1) * 1000 / dt < 0.7) { // 정지중이거나 유지중
            return 0
        } else if (speed2 > speed1!!){ // 가속중
            return 1
        } else { // 감속중
            return 2
        }
    }

    fun setSound(status: Int) {
        when (status) {
            -1 -> {
//                when (currentSound) {
//                    "Start" -> {
//                        mediaPlayerOnStart.stop()
//                    }
//                    "Accel" -> {
//                        mediaPlayerOnAccel.stop()
//                    }
//                    "Decel" -> {
//                        mediaPlayerOnDecel.stop()
//                    }
//                }
//                currentSound = "Null"
            }
            0 -> {
                carStartSound()
            }
            1 -> {
                carAccelSound()
            }
            2 -> {
                carDecelSound()
            }
        }
    }
}