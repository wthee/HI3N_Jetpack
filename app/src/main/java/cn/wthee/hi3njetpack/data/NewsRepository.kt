package cn.wthee.hi3njetpack.data

class NewsRepository private constructor(val newsNetwork: NewsNetwork){

    fun getNews() = newsNetwork.getNews()

    fun loadNext() = newsNetwork.loadNext()

    fun isGone() = newsNetwork.isGone()

    companion object {

        @Volatile private var instance: NewsRepository? = null

        fun getInstance(network: NewsNetwork) = instance ?: synchronized(this){
            instance ?: NewsRepository(network).also { instance = it }
        }
    }
}