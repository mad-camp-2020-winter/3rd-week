package com.example.bongorghini.fragment

import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bongorghini.R
import com.example.bongorghini.activity.AddApplicationActivity
import com.example.bongorghini.adapter.ApplicationListAdapter
import com.example.bongorghini.listener.ItemDragListener
import com.example.bongorghini.model.Application
import com.example.bongorghini.service.AutoExecuteService
import com.example.bongorghini.utils.ItemTouchHelperCallback
import com.kyleduo.switchbutton.SwitchButton

class OrderListFragment : Fragment(), ItemDragListener {

    private lateinit var itemTouchHelper : ItemTouchHelper
    private lateinit var list : ArrayList<Application>
    lateinit var mAdapter : ApplicationListAdapter
    private lateinit var listView: RecyclerView

    private lateinit var myService : AutoExecuteService

    private lateinit var myContext: FragmentActivity
    private lateinit var fragManager: FragmentManager


    private lateinit var sharedPreferences: SharedPreferences

    var serviceEnabled = true

    lateinit var mList: ArrayList<com.example.bongorghini.model.Application>

    private val connection = object: ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as AutoExecuteService.MyBinder
            myService = binder.getService(this@OrderListFragment)
            myService.myContext = myContext

            var serviceState = sharedPreferences.getBoolean("AutoExecRunning", false)
            if (serviceState && serviceEnabled) {
                if (myService.serviceActivated) {
                    mList = myService.mList
                    Log.d("OrderListFragment", "get mlist $mList")
                    mAdapter.mList = mList

                    // 새로 받았으니 새로고침 하자
                    mAdapter.notifyDataSetChanged()

//                    val fragTransaction = fragManager.beginTransaction()
//                    fragTransaction.detach(this@OrderListFragment)
//                        .attach(this@OrderListFragment).commit()

                }
                myService.stopForegroundService()
            }

            Log.d("Service", "Connected")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d("Service", "Disconnected")
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myContext = context as FragmentActivity
        sharedPreferences = myContext.getPreferences(Context.MODE_PRIVATE)
        serviceEnabled = sharedPreferences.getBoolean("serviceEnabled", true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var viewOfLayout = inflater.inflate(R.layout.fragment_order_list, container, false)

        fragManager = myContext.supportFragmentManager

        listView = viewOfLayout.findViewById<RecyclerView>(R.id.order_list)
        var addApplicationButton = viewOfLayout.findViewById<ImageView>(R.id.add_application)

        list = ArrayList<Application>()

        mAdapter = ApplicationListAdapter(list, this)
        listView.adapter = mAdapter

        var mLinearLayoutManager : LinearLayoutManager = LinearLayoutManager(this.context)
        listView.layoutManager = mLinearLayoutManager

        //drag 및 swipe삭제 위해 ItemTouchHelper와 연결
        itemTouchHelper = ItemTouchHelper(ItemTouchHelperCallback(mAdapter))
        itemTouchHelper.attachToRecyclerView(listView)

        //우측하단 버튼 클릭시 add activity 실행
        addApplicationButton.setOnClickListener {
            val intent = Intent(this.context, AddApplicationActivity::class.java)
            startActivityForResult(intent, 200) //임의숫자  requestCode로 설정

        }

        val intent = Intent(this.context, AutoExecuteService::class.java)

        requireActivity().startService(intent)
        requireActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE)

        Log.d("OrderListFragment", "Service initialized?")

        //Service 활성화 스위치
        var serviceButton = viewOfLayout.findViewById<SwitchButton>(R.id.service_control)
        serviceButton.isChecked = serviceEnabled
        serviceButton.setOnCheckedChangeListener { buttonView, isChecked ->
            serviceEnabled = isChecked
        }

//        var autoExecution: ImageView = viewOfLayout.findViewById(R.id.auto_execution)
//        autoExecution.setOnClickListener { v->
//            myService.autoStart()
//        }

        return viewOfLayout
    }

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder){
        itemTouchHelper.startDrag(viewHolder)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(data != null) {
            val returnApplication= Application()
            returnApplication.name = data?.getStringExtra("name")
            returnApplication.path = data?.getStringExtra("path")
            returnApplication.icon = data?.getStringExtra("icon")

            mAdapter.addItem(returnApplication)
            listView.adapter = mAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("OrderListFragment", "OnResume")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("onDestroy", "destroyed: AutoexecuteService start")
        with (sharedPreferences.edit()) {
            putBoolean("AutoExecRunning", true)
            putBoolean("serviceEnabled", serviceEnabled)
            commit()
        }
        if (serviceEnabled) {
            myService.mList = mAdapter.returnList()!!
            myService.startForegroundService()
        }

    }

}