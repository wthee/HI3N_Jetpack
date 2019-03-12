package cn.wthee.hi3njetpack.util

import android.app.AlertDialog
import android.app.Dialog
import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Environment
import android.view.KeyEvent
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception

object ImgUtil {
        //查看图片
        fun check(context: Context,url: String){
            val list = arrayOf("查看原图","保存图片")
            var alertDialog = AlertDialog.Builder(context)
            var dialog = alertDialog.setItems(list) { _, i ->
                when (i) {
                    0 ->{
                        Toast.makeText(context,"长按任意位置保存图片",Toast.LENGTH_SHORT).show()
                        var dialog = Dialog(context)
                        dialog.setContentView(getPhotoView(context,url))
                        dialog.show()
                        //dialog.window.setBackgroundDrawable(context.getDrawable(R.drawable.bg))
                        dialog.window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                    }
                    1 -> {
                        var bmp = getPhotoView(context,url).drawable as BitmapDrawable
                        ImgUtil.save(context,bmp.bitmap)
                    }
                }
            }.create()
            dialog.show()
        }

        //保存图片
        fun save(context: Context, bitmap: Bitmap){
            var storePath = Environment.getExternalStorageDirectory ().absolutePath + File.separator + "img"
            var appDir =  File(storePath)
            if (!appDir.exists()) {
                appDir.mkdir()
            }
            var fileName  = System.currentTimeMillis().toString() + ".png";
            var file = File(appDir, fileName)
            try {
                var fos = FileOutputStream(file)
                //通过io流的方式来压缩保存图片
                var isSuccess = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                if(isSuccess){
                    Toast.makeText(context,"图片已保存", Toast.LENGTH_SHORT).show()
                }
                fos.flush()
                fos.close()
                //保存图片后发送广播通知更新数据库
                var uri = Uri.fromFile (file);
                context.sendBroadcast( Intent (Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
            } catch ( e: Exception) {
                e.printStackTrace();
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
                ImgUtil.save(context,bmp.bitmap)
                return@setOnLongClickListener true
            }
            return pv
        }

}