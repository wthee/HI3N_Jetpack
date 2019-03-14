package cn.wthee.hi3njetpack.util

import android.webkit.WebView
import cn.wthee.hi3njetpack.data.NewsNetwork
import cn.wthee.hi3njetpack.data.NewsRepository
import cn.wthee.hi3njetpack.data.VideoNetwork
import cn.wthee.hi3njetpack.data.VideoRepository
import cn.wthee.hi3njetpack.viewmodels.NewsViewModelFactory
import cn.wthee.hi3njetpack.viewmodels.VideoViewModelFactory

object InjectorUtil {

    fun getNewsRepository() = NewsRepository.getInstance(NewsNetwork.getInstance())
    fun getNewsViewModelFactory() = NewsViewModelFactory(getNewsRepository())

    fun getVideoRepository() = VideoRepository.getInstance(VideoNetwork.getInstance())
    fun getVideoViewModelFactory(webView: WebView) = VideoViewModelFactory(webView,getVideoRepository())
}