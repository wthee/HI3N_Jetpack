package cn.wthee.hi3nlite.data

import android.webkit.WebView

class VideoRepository private constructor(private val videoNetwork: VideoNetwork){

    fun initVideo(webView: WebView,url: String) = videoNetwork.initVideo(webView,url)

    fun loadNext(url: String) = videoNetwork.loadNext(url)
    fun refresh(url: String) = videoNetwork.refresh(url)
    fun isGone() = videoNetwork.isGone()
    fun isRefresh() = videoNetwork.isRefresh()

    companion object {

        @Volatile private var instance: VideoRepository? = null

        fun getInstance(network: VideoNetwork) = instance
            ?: synchronized(this){
            instance
                ?: VideoRepository(network).also { instance = it }
        }
    }
}