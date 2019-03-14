package cn.wthee.hi3njetpack.viewmodels

import android.webkit.WebView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cn.wthee.hi3njetpack.data.VideoRepository

class VideoViewModelFactory(
    private val webView: WebView,
    private val repository: VideoRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) = VideoViewModel(webView,repository) as T
}