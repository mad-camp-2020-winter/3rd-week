package com.example.bongorghini.utils

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class NonSwipeViewPager(context: Context) : ViewPager(context) {

    //ViewPager의 Swipe 기능 막기 위함
    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return false
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return false
    }


}