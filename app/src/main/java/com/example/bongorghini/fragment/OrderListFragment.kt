package com.example.bongorghini.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bongorghini.R
import com.example.bongorghini.activity.AddApplicationActivity
import com.example.bongorghini.adapter.ApplicationListAdapter
import com.example.bongorghini.listener.ItemDragListener
import com.example.bongorghini.model.Application
import com.example.bongorghini.utils.ItemTouchHelperCallback

class OrderListFragment : Fragment(), ItemDragListener {

    private lateinit var itemTouchHelper : ItemTouchHelper
    private lateinit var list : ArrayList<Application>
    private lateinit var mAdapter : ApplicationListAdapter
    private lateinit var listView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var v = inflater.inflate(R.layout.fragment_order_list, container, false)
        listView = v.findViewById<RecyclerView>(R.id.order_list)
        var addApplicationButton = v.findViewById<ImageView>(R.id.add_application)

        list = ArrayList<Application>()

        //test용 - application factory
        var newApplication = Application()
        newApplication.name = "카카오톡"
        newApplication.delay = 4
        list.add(newApplication)

        var newApplication1 = Application()
        newApplication1.name = "유튜브"
        newApplication1.delay = 5
        list.add(newApplication1)

        var newApplication2 = Application()
        newApplication2.name = "T MAP"
        newApplication2.delay = 10
        list.add(newApplication2)

        mAdapter = ApplicationListAdapter(list, this)
        listView.adapter = mAdapter

        var mLinearLayoutManager : LinearLayoutManager = LinearLayoutManager(this.context)
        listView.layoutManager = mLinearLayoutManager

        //drag 및 swipe삭제 위해 ItemTouchHelper와 연결
        itemTouchHelper = ItemTouchHelper(ItemTouchHelperCallback(mAdapter))
        itemTouchHelper.attachToRecyclerView(listView)

        //우측하단 버튼 클릭시 add activity 실행
        addApplicationButton.setOnClickListener { v ->
            val intent = Intent(this.context, AddApplicationActivity::class.java)
            startActivityForResult(intent, 200) //임의숫자  requestCode로 설정
        }

        return v

    }

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder){
        itemTouchHelper.startDrag(viewHolder)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val returnApplication= Application()
        returnApplication.name = data?.getStringExtra("name")
        returnApplication.path = data?.getStringExtra("path")
        returnApplication.icon = data?.getStringExtra("icon")

        mAdapter.addItem(returnApplication)
        listView.adapter = mAdapter
    }

}