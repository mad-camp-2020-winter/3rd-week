package com.example.bongorghini.listener

import androidx.recyclerview.widget.RecyclerView
import com.example.bongorghini.adapter.ApplicationListAdapter

interface ItemDragListener {
    fun onStartDrag(viewHolder: RecyclerView.ViewHolder)
}