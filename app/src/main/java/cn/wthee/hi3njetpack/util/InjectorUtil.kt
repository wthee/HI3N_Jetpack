package cn.wthee.hi3njetpack.util

import cn.wthee.hi3njetpack.data.NewsNetwork
import cn.wthee.hi3njetpack.data.NewsRepository
import cn.wthee.hi3njetpack.viewmodels.NewsViewModelFactory

object InjectorUtil {

    private fun getNewsRepository() = NewsRepository.getInstance(NewsNetwork.getInstance())
    fun getNewsViewModelFactory() = NewsViewModelFactory(getNewsRepository())
}