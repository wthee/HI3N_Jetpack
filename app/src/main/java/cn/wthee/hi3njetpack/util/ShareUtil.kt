package cn.wthee.hi3njetpack.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.ShareCompat
import java.io.File

object ShareUtil{

    fun shareImg(file: File, context: Context){
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

    fun shareText(text: String, context: Context){
        val shareIntent = ShareCompat.IntentBuilder.from(ActivityUtil.instance.currentActivity)
            .setText(text)
            .setType("text/plain")
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
}