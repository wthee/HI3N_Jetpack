package cn.wthee.hi3njetpack.data

import android.webkit.WebView

class VideoRepository private constructor(private val videoNetwork: VideoNetwork){

    fun initVideo(webView: WebView,url: String) = videoNetwork.initVideo(webView,url)

    fun loadNext(webView: WebView,url: String) = videoNetwork.loadNext(webView,url)
    fun refresh(webView: WebView,url: String) = videoNetwork.refresh(webView,url)
    fun isGone() = videoNetwork.isGone()
    fun isRefresh() = videoNetwork.isRefresh()

    companion object {

        @Volatile private var instance: VideoRepository? = null

        fun getInstance(network: VideoNetwork) = instance ?: synchronized(this){
            instance ?: VideoRepository(network).also { instance = it }
        }
    }
}