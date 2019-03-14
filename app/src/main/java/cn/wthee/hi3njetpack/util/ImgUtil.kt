package cn.wthee.hi3njetpack.util

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.view.KeyEvent
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ShareCompat
import cn.wthee.hi3njetpack.R
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import okhttp3.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


object ImgUtil {

    //查看图片
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun check(context: Context, url: String){
        val list = arrayOf("查看原图","保存图片","分享图片")
        var alertDialog = AlertDialog.Builder(context)
        var dialog = alertDialog.setItems(list) { _, i ->
            when (i) {
                0 ->{
                    Toast.makeText(context,"长按任意位置保存图片",Toast.LENGTH_SHORT).show()
                    var dialog = Dialog(context)
                    dialog.setContentView(getPhotoView(context,url))
                    dialog.show()
                    dialog.window.setBackgroundDrawable(context.getDrawable(R.drawable.bg))
                    dialog.window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                }
                1 -> {
                    ImgUtil.save(context,url,object : GetUri{
                        override fun imgUri(file: File) {
                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        }
                    })
                    Toast.makeText(context,"图片已保存",Toast.LENGTH_SHORT).show()
                }
                2 ->{
                    ImgUtil.save(context,url,object : GetUri{
                        override fun imgUri(file: File) {
                            val shareIntent = ShareCompat.IntentBuilder.from(ActivityUtil.instance.currentActivity)
                                .setStream(Uri.fromFile(file))
                                .setType("image/plain")
                                .createChooserIntent()
                                .apply {
                                    // https://android-developers.googleblog.com/2012/02/share-with-intents.html
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        // If we're on Lollipop, we can open the intent as a document
                                        addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
                                    } else {
                                        // Else, we will use the old CLEAR_WHEN_TASK_RESET flag
                                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
                                    }
                                }
                            context.startActivity(shareIntent)
                        }
                    })
                }
            }
        }.create()
        dialog.show()
    }

    //保存图片
    private fun save(context: Context,url: String, getUri: GetUri){

        var okHttpClient = OkHttpClient()
        var request = Request.Builder()
            .get()
            .url(url)
            .build()
        var call = okHttpClient.newCall(request)
        call.enqueue(object : Callback{
            override fun onFailure(call: Call, e: IOException) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
            override fun onResponse(call: Call, response: Response) {
                var storePath = Environment.getExternalStorageDirectory ().absolutePath + File.separator + "img"
                var appDir =  File(storePath)
                if (!appDir.exists()) {
                    appDir.mkdir()
                }
                var fileName  = System.currentTimeMillis().toString() + ".png";
                var file = File(appDir, fileName)
                file.createNewFile()
                var inputStream = response.body()!!.byteStream()
                var bitmap = BitmapFactory.decodeStream(inputStream)
                var out = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.PNG,100,out)
                out.flush()
                out.close()
                var uri = Uri.fromFile (file);
                context.sendBroadcast( Intent (Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
                getUri.imgUri(file)
            }
        })
    }

    interface GetUri{
        fun imgUri(file: File)
    }

    //动态的ImageView
    private fun getPhotoView(context: Context,url: String): PhotoView {
        var pv = PhotoView(context)
        pv.layoutParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        Glide.with(context).load(url).into(pv)
        pv.setOnClickListener {
            Thread {
                Instrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_BACK)
            }.start()
        }
        pv.setOnLongClickListener {
            var bmp = pv.drawable as BitmapDrawable
            ImgUtil.save(context,url,object : GetUri{
                override fun imgUri(file: File) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
            })
            Toast.makeText(context,"图片已保存",Toast.LENGTH_SHORT).show()
            return@setOnLongClickListener true
        }
        return pv
    }

}