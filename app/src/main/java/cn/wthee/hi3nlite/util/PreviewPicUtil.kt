package cn.wthee.hi3nlite.util

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
import android.os.Looper
import android.view.KeyEvent
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import cn.wthee.hi3njetpack.R
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import okhttp3.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


object PreviewPicUtil {

    var storePath = Environment.getExternalStorageDirectory ().absolutePath + File.separator + "hi3n"
    var sharePath = Environment.getExternalStorageDirectory ().absolutePath + File.separator + "hi3nshare"
    var file = File(sharePath)
    //查看图片
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun preview(context: Context, url: String){
        val list = arrayOf("查看原图","保存图片","分享图片")
        var alertDialog = AlertDialog.Builder(context)
        var dialog = alertDialog.setItems(list) { _, i ->
            when (i) {
                0 ->{
                    Toast.makeText(context,"长按任意位置保存图片",Toast.LENGTH_SHORT).show()
                    var dialog = Dialog(context)
                    dialog.setContentView(getPhotoView(context, url))
                    dialog.window.setBackgroundDrawable(context.getDrawable(R.drawable.bg))
                    dialog.window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                    dialog.show()
                }
                1 -> {
                    save(
                        context,
                        url,
                        storePath,
                        true,
                        object : GetUri {
                            override fun imgUri(file: File) {
                                Looper.prepare()
                                Toast.makeText(context, "图片已保存", Toast.LENGTH_SHORT).show()
                                Looper.loop()
                            }
                        })
                }
                2 ->{
                    save(
                        context,
                        url,
                        sharePath,
                        false,
                        object : GetUri {
                            override fun imgUri(file: File) {
                                ShareUtil.shareImg(file, context)
                            }
                        })
                }
            }
        }.create()
        dialog.show()
    }

    //保存图片
    private fun save(context: Context,url: String, savePath: String, isRefresh: Boolean, getUri: GetUri){

        var okHttpClient = OkHttpClient()
        var request = if(url[0]!='h'){
            Request.Builder()
                .get()
                .url("https://$url")
                .build()
        }else{
            Request.Builder()
                .get()
                .url(url)
                .build()
        }
        var call = okHttpClient.newCall(request)
        call.enqueue(object : Callback{
            override fun onFailure(call: Call, e: IOException) {
                return
            }
            override fun onResponse(call: Call, response: Response) {
                var appDir =  File(savePath)
                if (!appDir.exists()) {
                    appDir.mkdir()
                }
                var fileName  = System.currentTimeMillis().toString() + ".jpg";
                var file = File(appDir, fileName)
                file.createNewFile()
                var inputStream = response.body()!!.byteStream()
                var bitmap = BitmapFactory.decodeStream(inputStream)
                var out = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.PNG,100,out)
                out.flush()
                out.close()
                if(isRefresh){
                    var uri = Uri.fromFile (file)
                    context.sendBroadcast( Intent (Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
                }
                getUri.imgUri(file)
            }
        })
    }
    interface GetUri{
        fun imgUri(file: File)
    }
    //分享后删除
    fun deleteFile(file: File){
        if (file.isDirectory) {
            var files = file.listFiles()
            files.forEach {
                deleteFile(it)
            }
            file.delete();//如要保留文件夹，只删除文件，请注释这行
        } else if (file.exists()) {
            file.delete();
        }
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
            save(
                context,
                url,
                storePath,
                true,
                object : GetUri {
                    override fun imgUri(file: File) {
                        Looper.prepare()
                        Toast.makeText(context, "图片已保存", Toast.LENGTH_SHORT).show()
                        Looper.loop()
                    }
                })
            return@setOnLongClickListener true
        }
        return pv
    }


}