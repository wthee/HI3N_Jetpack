package cn.wthee.hi3njetpack.util

import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import cn.wthee.hi3njetpack.MyApplication

object NetWorkUtil {

    fun createWebView(): WebView {
        var web = WebView(MyApplication.context)
        web.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        web.visibility = View.INVISIBLE
        return web
    }

}