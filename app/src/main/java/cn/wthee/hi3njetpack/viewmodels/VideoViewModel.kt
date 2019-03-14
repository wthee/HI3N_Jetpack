package cn.wthee.hi3njetpack.viewmodels

import android.webkit.WebView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import cn.wthee.hi3njetpack.data.News
import cn.wthee.hi3njetpack.data.NewsRepository
import cn.wthee.hi3njetpack.data.Video
import cn.wthee.hi3njetpack.data.VideoRepository

class VideoViewModel (
    private val webView: WebView,
    private val repository: VideoRepository): ViewModel(){

    var video : MutableLiveData<List<Video>> = repository.getVideo(webView)

    fun loadMore() {
        this.video = repository.loadNext(webView)
    }

    var isGone: LiveData<Int> = repository.isGone()
}