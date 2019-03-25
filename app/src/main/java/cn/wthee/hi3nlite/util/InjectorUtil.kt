package cn.wthee.hi3nlite.util

import android.webkit.WebView
import cn.wthee.hi3nlite.data.NewsNetwork
import cn.wthee.hi3nlite.data.NewsRepository
import cn.wthee.hi3nlite.data.VideoNetwork
import cn.wthee.hi3nlite.data.VideoRepository
import cn.wthee.hi3nlite.viewmodels.NewsViewModelFactory
import cn.wthee.hi3nlite.viewmodels.VideoViewModelFactory

object InjectorUtil {

    private fun getNewsRepository() = NewsRepository.getInstance(NewsNetwork.getInstance())
    fun getNewsViewModelFactory() =
        NewsViewModelFactory(getNewsRepository())

    private fun getVideoRepository() = VideoRepository.getInstance(VideoNetwork.getInstance())
    fun getVideoViewModelFactory(webView: WebView, url: String) = VideoViewModelFactory(
        webView,
        url,
        getVideoRepository()
    )
}