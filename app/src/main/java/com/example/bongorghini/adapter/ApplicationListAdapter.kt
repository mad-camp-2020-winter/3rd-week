package com.example.bongorghini.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bongorghini.R
import com.example.bongorghini.model.Application
import java.io.ByteArrayInputStream
import java.util.*


class ApplicationListAdapter(list: ArrayList<Application>?) : RecyclerView.Adapter<ApplicationListAdapter.CustomViewHolder>() {
//    private val mLongListener: OnListItemLongSelectedInterface
//    private val mListener: OnListItemSelectedInterface

    private lateinit var context: Context

    private var mList: ArrayList<Application>? = ArrayList<Application>()

    //누를 때, 길게 누를때 상황에 맞춰 override
    interface OnListItemLongSelectedInterface {
        fun onItemLongSelected(v: View?, position: Int)
    }

    interface OnListItemSelectedInterface {
        fun onItemSelected(v: View?, position: Int)
    }


    inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView
        var delay: TextView
        var icon: ImageView


        init {
            name = view.findViewById<View>(R.id.list_name) as TextView
            delay = view.findViewById(R.id.list_delay) as TextView
            icon = view.findViewById(R.id.list_icon) as ImageView
//            view.setOnClickListener { v -> mListener.onItemSelected(v, adapterPosition) }
//            view.setOnLongClickListener { v ->
//                mLongListener.onItemLongSelected(v, adapterPosition)
//                false
//            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): CustomViewHolder {
        val view: View = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.recyclerview_application, viewGroup, false)
        context = view.context
        return CustomViewHolder(view)
//        if (this::context.isInitialized)
    }

    override fun onBindViewHolder(viewholder: CustomViewHolder, position: Int) {

        //화면 크기 맞춰 사진크기 조정
//        val displayMetrics = context!!.resources.displayMetrics
//        val width = displayMetrics.widthPixels / 3 - 10
//        val height = displayMetrics.heightPixels / 5 - 20
//        val params = LinearLayout.LayoutParams(width, height)
//        viewholder.name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
//        viewholder.delay.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
//        viewholder.icon.layoutParams = params
//        viewholder.name.gravity = Gravity.CENTER
        viewholder.name.setText(mList!![position].name)
        viewholder.delay.text = "delay : " + mList!![position].delay
        var data: String? = mList!![position].icon
        if (data == null) {
            viewholder.icon.setImageResource(R.drawable.tools) // 테스트용 임의 설정
        } else {
            //bitmap -> stream -> image 변환
            val bytePlainOrg = Base64.decode(data, 0)
            val inputStream = ByteArrayInputStream(bytePlainOrg)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            viewholder.icon.setImageBitmap(bitmap)
        }
    }

    override fun getItemCount(): Int {
        return mList?.size ?: 0
    }

    fun addItem(item: Application) {
        mList!!.add(item)
        notifyDataSetChanged()
    }

    fun getItem(position: Int): Application {
        return mList!![position]
    }

    fun removeItem(position: Int) {
        mList!!.removeAt(position)
    }

    init {
        mList = list
    }
}
