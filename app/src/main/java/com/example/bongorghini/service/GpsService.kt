package com.example.bongorghini.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.example.bongorghini.MainActivity
import com.example.bongorghini.R
import java.lang.Exception
import kotlin.concurrent.timer

class GpsService(context: Context): Service() {
    val CHANNEL_ID = "GPSForegroundServiceChannel"
    val NOTIFICATION_ID = 102


    private val myBinder = MyBinder()
    private var myContext = context
    private var locationManager: LocationManager = myContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private lateinit var gpsTracker: GpsTracker

    private val dt: Long = 1000

    private var location1: Location? = null
    private var location2: Location? = null
    private var speed_kph: Double? = null

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

    fun startForeGroundService() {
        val notificationIntent = Intent(this, MainActivity::class.java)
        notificationIntent.putExtra("tabIndex", 2)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = createNotification(pendingIntent)
        startForeground(NOTIFICATION_ID, notification)

        gpsTracker = GpsTracker(myContext)

        timer(period = dt) {
            location1 = location2
            location2 = gpsTracker.getGPSLocation()

            if (location1 != null && location2 != null) {
                val speed_mps = location2!!.distanceTo(location1) / (dt / 1000) as Double
                speed_kph = mps_to_kph(speed_mps)

                Toast.makeText(myContext, "speed: $speed_kph", Toast.LENGTH_SHORT).show()

            }
        }



    }

    fun stopForegroundService() {
        stopForeground(true)
    }

    private fun createNotification(pendingIntent: PendingIntent): Notification {
        val notification = androidx.core.app.NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Bongorghini")
            .setContentText("Range mode activated")
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

    inner class GpsTracker(context: Context): Service(), LocationListener {
        var mcontext: Context
        var location: Location? = null
        var lat: Double? = null
        var lng: Double? = null

        private val MIN_DISTANCE_CHANGE_FOR_UPDATES: Float = 1F
        private val MIN_TIME_BW_UPDATES: Long = (1000 * 1)


        init {
            mcontext = context
            getGPSLocation()
        }

        override fun onBind(intent: Intent?): IBinder? {
            TODO("Not yet implemented")
        }

        override fun onLocationChanged(location: Location) {
            Log.d("Location", "location changed!")
        }

        fun getGPSLocation(): Location? {
            try {
                val isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

                if (isGPSEnabled || isNetworkEnabled) {
                    Log.d("Location", "Gps enabled")
                    val hasFineLocationPermission = ContextCompat.checkSelfPermission(myContext, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(myContext, android.Manifest.permission.ACCESS_COARSE_LOCATION)

                    if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED
                            || hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED) {
                        Log.d("Location", "permission denied")
                        return null
                    }

                    if (isGPSEnabled) {

                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                0F,
                                (this as LocationListener)
                        )
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                        if (location != null) {
                            lat = location!!.latitude
                            lng = location!!.longitude
                            Log.d("Location", "location is not null")
                        } else {
                            Log.d("Location", "location is null")
                        }
                    }
                    if (isNetworkEnabled && location == null) {
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES,
                                (this as LocationListener))

                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                        if (location != null) {
                            lat = location!!.latitude
                            lng = location!!.longitude
                        }
                    }

                    return location

                } else {
                    Log.d("GpsTracker.getLocation", "Gps is not enabled")
                    return null
                }
            } catch (e: Exception) {
                Log.d("GpsTracker.getLocation", e.toString())
                return null
            }
        }

        fun getLatitude(): Double? {
            if (location != null) {
                lat = location!!.latitude
            }
            return lat
        }

        fun getLongitude(): Double? {
            if (location != null) {
                lng = location!!.longitude
            }
            return lng
        }
    }

    fun mps_to_kph (v_mps: Double): Double {
        return v_mps * 3.6
    }
}