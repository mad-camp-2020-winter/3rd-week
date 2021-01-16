package com.example.bongorghini.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bongorghini.R
import com.example.bongorghini.adapter.ApplicationListAdapter
import com.example.bongorghini.listener.ItemDragListener
import com.example.bongorghini.model.Application
import com.example.bongorghini.utils.ItemTouchHelperCallback

class OrderListFragment : Fragment(), ItemDragListener {

    private lateinit var itemTouchHelper : ItemTouchHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var v = inflater.inflate(R.layout.fragment_order_list, container, false)
        var listView = v.findViewById<RecyclerView>(R.id.order_list)

        var list = ArrayList<Application>()

        //test용 - application factory
        var newApplication = Application()
        newApplication.name = "카카오톡"
        newApplication.delay = 4
        newApplication.path = "root/main"
        list.add(newApplication)

        var newApplication1 = Application()
        newApplication1.name = "유튜브"
        newApplication1.delay = 5
        newApplication1.path = "root/main"
        list.add(newApplication1)

        var newApplication2 = Application()
        newApplication2.name = "T MAP"
        newApplication2.delay = 10
        newApplication2.path = "root/main"
        list.add(newApplication2)

        var mAdapter = ApplicationListAdapter(list, this)
        listView.adapter = mAdapter

        var mLinearLayoutManager : LinearLayoutManager = LinearLayoutManager(this.context)
        listView.layoutManager = mLinearLayoutManager

        //drag 및 swipe삭제 위해 ItemTouchHelper와 연결
        itemTouchHelper = ItemTouchHelper(ItemTouchHelperCallback(mAdapter))
        itemTouchHelper.attachToRecyclerView(listView)
        return v

    }

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder){
        itemTouchHelper.startDrag(viewHolder)
    }


}