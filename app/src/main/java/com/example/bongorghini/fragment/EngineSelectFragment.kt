package com.example.bongorghini.fragment

import android.annotation.SuppressLint
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.bongorghini.R
import com.example.bongorghini.service.GpsService
import com.example.bongorghini.utils.GpsTracker
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.GooglePlayServicesUtil.isGooglePlayServicesAvailable
import java.lang.Exception


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


/**
 * A simple [Fragment] subclass.
 * Use the [EngineSelectFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EngineSelectFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val PLAY_SERVICES_RESOLUTION_REQUEST = 9000
    private val MIN_DISTANCE_CHANGE_FOR_UPDATES: Float = 1F
    private val MIN_TIME_BW_UPDATES: Long = (1000 * 1)

    private lateinit var myContext: FragmentActivity
    private lateinit var viewOfLayout: View

    private lateinit var locationManager: LocationManager
    private lateinit var gpsTracker: GpsTracker

    lateinit var myService: GpsService
    private var myBound: Boolean = true

    private val connection = object: ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as GpsService.MyBinder
            myService = binder.getService()

            myService.myContext = myContext

            myBound = true
            Log.d("Service", "Initialized")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            myBound = false
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myContext = context as FragmentActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewOfLayout = inflater.inflate(R.layout.fragment_engine_select, container, false)
        locationManager = myContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (ActivityCompat.checkSelfPermission(
                myContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
            Log.d("Permission", "Location access permission denied")
            return viewOfLayout
        }
        if (ActivityCompat.checkSelfPermission(
                myContext,
                android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
            Log.d("Permission", "Background Location access permission denied")
            return viewOfLayout
        }

        val locationLog = viewOfLayout.findViewById<TextView>(R.id.locationLog)

        val gpsButton = viewOfLayout.findViewById<Button>(R.id.gpsButton)

        gpsButton.setOnClickListener {
            myService.startForeGroundService()

//            gpsTracker = GpsTracker(myContext)
//            val latitude = gpsTracker.getLatitude()
//            val longitude = gpsTracker.getLongitude()
//            val speed = gpsTracker.getSpeed()
//
//            locationLog.text = locationLog.text.toString() + "\nLat: ${latitude}, Lng: ${longitude}, Spd: ${speed}"


//            Toast.makeText(myContext, "Lat: ${latitude}, Lng: ${longitude}, Spd: ${speed}", Toast.LENGTH_SHORT).show()

//            Log.d("location", "Lat: ${latitude}, Lng: ${longitude}")
        }

        val stopButton = viewOfLayout.findViewById<Button>(R.id.stopButton)

        stopButton.setOnClickListener{
            myService.stopForegroundService()
        }

        Log.d("Service", "requireActivity")
        requireActivity().startService(Intent(requireContext(), GpsService::class.java))
        requireActivity().bindService(Intent(requireContext(), GpsService::class.java), connection, Context.BIND_AUTO_CREATE)


        return viewOfLayout
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EngineSelectFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EngineSelectFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}