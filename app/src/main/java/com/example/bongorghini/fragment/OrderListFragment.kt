package com.example.bongorghini.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bongorghini.R
import com.example.bongorghini.adapter.ApplicationListAdapter
import com.example.bongorghini.model.Application

class OrderListFragment : Fragment() {

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

        var mAdapter = ApplicationListAdapter(list)
        listView.adapter = mAdapter

        var mLinearLayoutManager : LinearLayoutManager = LinearLayoutManager(this.context)
        listView.layoutManager = mLinearLayoutManager
        return v
    }


}