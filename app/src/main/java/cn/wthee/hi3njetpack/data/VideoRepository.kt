package cn.wthee.hi3njetpack.data

import android.webkit.WebView

class VideoRepository private constructor(val videoNetwork: VideoNetwork){

    fun getVideo(webView: WebView) = videoNetwork.getVideo(webView)

    fun loadNext(webView: WebView) = videoNetwork.loadNext(webView)
    fun isGone() = videoNetwork.isGone()

    companion object {

        @Volatile private var instance: VideoRepository? = null

        fun getInstance(network: VideoNetwork) = instance ?: synchronized(this){
            instance ?: VideoRepository(network).also { instance = it }
        }
    }
}