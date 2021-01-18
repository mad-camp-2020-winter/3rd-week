package com.example.bongorghini.adapter

import android.content.Context
import android.database.DataSetObserver
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.util.Base64
import android.util.TypedValue
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.bongorghini.R
import com.example.bongorghini.listener.ItemActionListener
import com.example.bongorghini.listener.ItemDragListener
import com.example.bongorghini.model.Application
import java.io.ByteArrayInputStream
import java.util.*
import kotlin.collections.ArrayList


class ApplicationListAdapter(list: ArrayList<Application>?, private val listener : ItemDragListener) : RecyclerView.Adapter<ApplicationListAdapter.CustomViewHolder>(), ItemActionListener,
    Adapter {
//    private val mLongListener: OnListItemLongSelectedInterface
//    private val mListener: OnListItemSelectedInterface

    private lateinit var context: Context

    private var mList: ArrayList<Application>? = ArrayList<Application>()

//    //누를 때, 길게 누를때 상황에 맞춰 override
//    interface OnListItemLongSelectedInterface {
//        fun onItemLongSelected(v: View?, position: Int)
//    }
//
//    interface OnListItemSelectedInterface {
//        fun onItemSelected(v: View?, position: Int)
//    }

    fun returnList() : ArrayList<Application>? {
        return mList
    }

    //ItemActionListener 재정의
    override fun onItemMoved(from: Int, to: Int) {
        if (from == to) return

        val fromItem = mList!!.removeAt(from)
        mList?.add(to, fromItem)
        notifyItemMoved(from, to)
    }

    override fun onItemSwiped(position: Int) {
        mList!!.removeAt(position)
        notifyItemRemoved(position)
    }


    class CustomViewHolder(view: View, listener: ItemDragListener) : RecyclerView.ViewHolder(view) {
        var name: TextView
        var delay: TextView
        var icon: ImageView


        init {
            name = view.findViewById<View>(R.id.list_name) as TextView
            delay = view.findViewById(R.id.list_delay) as TextView
            icon = view.findViewById(R.id.list_icon) as ImageView
            var button = view.findViewById(R.id.order_button) as ImageButton
            button.setOnTouchListener { v, event ->
                if(event.action == MotionEvent.ACTION_DOWN) {
                    listener.onStartDrag(this)
                }
                false
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): CustomViewHolder {
        val view: View = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.recyclerview_application, viewGroup, false)
        context = view.context
        return CustomViewHolder(view, listener)
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
//            viewholder.icon.setImageDrawable(data)
        }
    }

    override fun getItemCount(): Int {
        return mList?.size ?: 0
    }

    fun addItem(item: Application) {
        mList!!.add(item)
        notifyDataSetChanged()
    }

    override fun isEmpty(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        TODO("Not yet implemented")
    }

    override fun registerDataSetObserver(observer: DataSetObserver?) {
        TODO("Not yet implemented")
    }

    override fun getItem(position: Int): Application {
        return mList!![position]
    }

    override fun getViewTypeCount(): Int {
        TODO("Not yet implemented")
    }

    override fun unregisterDataSetObserver(observer: DataSetObserver?) {
        TODO("Not yet implemented")
    }

    override fun getCount(): Int {
        TODO("Not yet implemented")
    }

    fun removeItem(position: Int) {
        mList!!.removeAt(position)
    }

    init {
        mList = list
    }
}
