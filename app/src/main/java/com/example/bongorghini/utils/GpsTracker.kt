package com.example.bongorghini.utils

import android.Manifest
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat

class GpsTracker(private val mContext: Context) : Service(), LocationListener {
    var location_curr: Location? = null
    var latitude = 0.0
    var longitude = 0.0
    var speed = 0F

    protected var locationManager: LocationManager? = null
    fun getLocation(): Location? {
        try {
            locationManager = mContext.getSystemService(LOCATION_SERVICE) as LocationManager
            val isGPSEnabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled =
                locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if (!isGPSEnabled && !isNetworkEnabled) {
            } else {
                val hasFineLocationPermission = ContextCompat.checkSelfPermission(
                    mContext,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
                    mContext,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
                if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                    hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED
                ) {
                    Log.d("permission","Finelocation ${hasFineLocationPermission == PackageManager.PERMISSION_GRANTED}" +
                            "CoarseLocation ${hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED}")
                } else {
                    Log.d("permission","Finelocation ${hasFineLocationPermission == PackageManager.PERMISSION_GRANTED}, " +
                            "CoarseLocation ${hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED}")
                    return null
                }
//                else return null

                if (isGPSEnabled) {
                    Log.d("GPStracker", "GPS enabled")
                    locationManager!!.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        1000,
                        0F,
                        (this as LocationListener)
                    )
                    if (locationManager != null) {
                        Log.d("GPStracker", "LocationManager is not null")
                        location_curr =
                            locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                        if (location_curr != null) {
                            Log.d("GPStracker", "Location is not null")
                            latitude = location_curr!!.latitude
                            longitude = location_curr!!.longitude
                            speed = location_curr!!.speed
                        }
                    }
                }
//                else
                if (isNetworkEnabled) {
                    locationManager!!.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            1000,
                            0F,
                            (this as LocationListener)
                    )
                    if (locationManager != null && location_curr == null) {
                        location_curr =
                                locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                        if (location_curr != null) {
                            latitude = location_curr!!.latitude
                            longitude = location_curr!!.longitude
                            speed = location_curr!!.speed
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.d("@@@", "" + e.toString())
        }
        return location_curr
    }

    @JvmName("getLatitude1")
    fun getLatitude(): Double {
        if (location_curr != null) {
            latitude = location_curr!!.latitude
            return latitude
        }
        return 0.0
    }

    @JvmName("getLongitude1")
    fun getLongitude(): Double {
        if (location_curr != null) {
            longitude = location_curr!!.longitude
        }
        return longitude
    }

    fun getSpeed(): Float? {
        if (location_curr != null) {
            speed = location_curr!!.speed
        }
        return speed
    }

    override fun onLocationChanged(location: Location) {
//        Toast.makeText(mContext, "Lat: ${latitude}, Lng: ${longitude}", Toast.LENGTH_SHORT).show()
        Log.d("GPStracker", "location changed!")
    }

    override fun onBind(arg0: Intent): IBinder? {
        return null
    }

    fun stopUsingGPS() {
        if (locationManager != null) {
            locationManager!!.removeUpdates((this@GpsTracker as android.location.LocationListener))
        }
    }

    companion object {
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 0
        private const val MIN_TIME_BW_UPDATES = (0).toLong()
    }

    init {
        getLocation()
    }
}