package cn.wthee.hi3njetpack.data

import android.webkit.WebView

class VideoRepository private constructor(val videoNetwork: VideoNetwork){

    fun initVideo(webView: WebView) = videoNetwork.initVideo(webView)

    fun loadNext(webView: WebView) = videoNetwork.loadNext(webView)
    fun refresh(webView: WebView) = videoNetwork.refresh(webView)
    fun isGone() = videoNetwork.isGone()
    fun isRefresh() = videoNetwork.isRefresh()

    companion object {

        @Volatile private var instance: VideoRepository? = null

        fun getInstance(network: VideoNetwork) = instance ?: synchronized(this){
            instance ?: VideoRepository(network).also { instance = it }
        }
    }
}