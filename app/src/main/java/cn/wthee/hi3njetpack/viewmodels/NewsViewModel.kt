package cn.wthee.hi3njetpack.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import cn.wthee.hi3njetpack.data.News
import cn.wthee.hi3njetpack.data.NewsRepository

class NewsViewModel (private val repository: NewsRepository): ViewModel(){

    var news : MutableLiveData<List<News>> = repository.getNews()

    fun loadMore() {
        this.news = repository.loadNext()
    }

    var isGone: LiveData<Int> = repository.isGone()

}