package cn.wthee.hi3nlite.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cn.wthee.hi3nlite.data.NewsRepository

class NewsViewModelFactory(
    private val repository: NewsRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) = NewsViewModel(repository) as T
}