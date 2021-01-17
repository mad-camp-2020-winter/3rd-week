package com.example.bongorghini.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ListView
import com.example.bongorghini.R
import com.example.bongorghini.model.Application
import java.io.ByteArrayOutputStream
import java.util.*


class AddApplicationActivity : Activity() {
    lateinit var returnApplication : Application

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_application)

        val arGeneral = ArrayList<String>()
        val pacageNm = ArrayList<String>()
        val iconList = ArrayList<Drawable>()
        val pm = packageManager
        val mainIntent = Intent(Intent.ACTION_MAIN, null)
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        val list = pm.getInstalledApplications(0)

        for (applicationInfo in list) {
            if (applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0) { // 시스템 패키지가 아니면 추가
                val name = applicationInfo.loadLabel(pm).toString() // 앱 이름
                val pName = applicationInfo.packageName // 앱 패키지
                val iconDrawable: Drawable = applicationInfo.loadIcon(pm)
                arGeneral.add("$name")
                pacageNm.add(pName)
                iconList.add(iconDrawable)
            }
        }

        val adapter: ArrayAdapter<String>
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, arGeneral)
        val listView =
            findViewById<View>(R.id.listview) as ListView
        listView.adapter = adapter
        listView.onItemClickListener =
            OnItemClickListener { parent, view, position, id ->
                //누르면 해당 앱 실행
//                val mes: String
//                mes = "Select Item = " + arGeneral[position]
//                Toast.makeText(this, mes, Toast.LENGTH_SHORT).show()
//                val pm = packageManager
//                val i = pm.getLaunchIntentForPackage(pacageNm[position]) //이걸로 앱실행 - 나중에 백그라운드에서 이걸로 실행할 예정
//                i!!.flags = Intent.FLAG_ACTIVITY_NEW_TASK //이거도!!!
//                startActivity(i)

//                //Application 이전 fragment로 선택 Application 전달
//                returnApplication= Application()
//                returnApplication.name = arGeneral[position]
//                returnApplication.path = pacageNm[position]

                //사진 변환 후 전달
                var sendBitmap = getBitmapFromDrawable(iconList[position])
                val outStream = ByteArrayOutputStream()

                sendBitmap?.compress(Bitmap.CompressFormat.PNG, 100, outStream)

                val image: ByteArray = outStream.toByteArray()
                val icon = Base64.encodeToString(image, 0)

                val i = Intent()
                i.putExtra("name", arGeneral[position])
                i.putExtra("path", pacageNm[position])
                i.putExtra("icon", icon)
                setResult(200, i)
                finish()
            }
    }

    private fun getBitmapFromDrawable(drawable: Drawable): Bitmap? {
        val bmp = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bmp)
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight())
        drawable.draw(canvas)
        return bmp
    }
}
