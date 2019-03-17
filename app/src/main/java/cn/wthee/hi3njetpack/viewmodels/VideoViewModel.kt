package cn.wthee.hi3njetpack.viewmodels

import android.webkit.WebView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.wthee.hi3njetpack.data.Video
import cn.wthee.hi3njetpack.data.VideoRepository

class VideoViewModel (
    private val webView: WebView,
    private val repository: VideoRepository): ViewModel(){

    var video : MutableLiveData<List<Video>> = repository.initVideo(webView)

    fun loadMore() {
        this.video = repository.loadNext(webView)
    }

    fun refresh(){
        this.video = repository.refresh(webView)
    }

    var isGone: LiveData<Int> = repository.isGone()
    var isRefresh: LiveData<Boolean> = repository.isRefresh()
}