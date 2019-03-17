package cn.wthee.hi3njetpack.data


class NewsRepository private constructor(val newsNetwork: NewsNetwork){

    fun getNews() = newsNetwork.getNews()

    fun loadNext() = newsNetwork.loadNext()

    fun refresh() = newsNetwork.refresh()

    fun isGone() = newsNetwork.isGone()

    fun isRefresh() = newsNetwork.isRefresh()

    companion object {

        @Volatile private var instance: NewsRepository? = null

        fun getInstance(network: NewsNetwork) = instance ?: synchronized(this){
            instance ?: NewsRepository(network).also { instance = it }
        }
    }
}