package com.example.bongorghini.listener

interface ItemActionListener {
    fun onItemMoved(from:Int, to:Int)
    fun onItemSwiped(position:Int)
}