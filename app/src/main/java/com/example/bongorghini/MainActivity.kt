package com.example.bongorghini

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.viewpager.widget.ViewPager
import com.example.bongorghini.ui.main.SectionsPagerAdapter
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar =
            findViewById<Toolbar>(R.id.user_tool_bar)
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar!!.title = "Bongorghini"

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter

        // 권한 획득 _______________________________________________________________________________
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//            && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // 없을 경우 권한 재요청
//            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,  android.Manifest.permission.ACCESS_COARSE_LOCATION), 100)
//        } else {
//            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,  android.Manifest.permission.ACCESS_COARSE_LOCATION), 100)
//        }

        val permissionAccessCoarseLocationApproved = ActivityCompat
            .checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
        val permissionAccessFineLocationApproved = ActivityCompat
            .checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
        val backgroundLocationPermissionApproved = ActivityCompat
            .checkSelfPermission(this, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
        val permissionForegroundServiceApproved = ActivityCompat
                .checkSelfPermission(this, android.Manifest.permission.FOREGROUND_SERVICE) ==
                PackageManager.PERMISSION_GRANTED

        if (!permissionAccessCoarseLocationApproved) {
            Log.d("Permission", "CoarseLocation not approved")
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION), 100)
        }

        if (!permissionAccessFineLocationApproved) {
            Log.d("Permission", "FineLocation not approved")
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 100)
        }

        if (!backgroundLocationPermissionApproved) {
            Log.d("Permission", "BackgroundLocation not approved")
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION), 100)
        }

        if (!permissionForegroundServiceApproved) {
            Log.d("Permission", "ForegroundService not approved")
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.FOREGROUND_SERVICE), 100)
        }

//        if (permissionAccessCoarseLocationApproved) {
//            if (permissionAccessFineLocationApproved) {
//                if (backgroundLocationPermissionApproved) {
//                    if (permissionForegroundServiceApproved) {
//
//                    } else {
//                        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.FOREGROUND_SERVICE), 100)
//                    }
//
//                } else {
//                    ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION), 100)
//                }
//            } else {
//                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 100)
//            }
//        } else {
//            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION), 100)
//        }
    }

}