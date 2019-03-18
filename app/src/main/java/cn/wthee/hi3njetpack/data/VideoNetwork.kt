package cn.wthee.hi3njetpack.data

import android.view.View
import android.webkit.*
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.ArrayList
import java.util.regex.Pattern


class VideoNetwork {


    private val urlRegex = "href=\".*? target=\"_blank\" class=\"title\""
    private val titleRegex = "target=\"_blank\" title=.*?class=\"img-anchor\""
    private val imgRegex = "<img alt=\"\" src=\".*?.(jpg|bmp|gif|ico|pcx|jpeg|tif|png)"
    private val dateRegex = "\\d{4}-\\d{2}-\\d{2}"
    private val uperRegex = "class=\"up-name\">.*?</a>"
    private val watchNumRegex = "^\\s*\\d+\$|^.*?万\$"
    private val lengthRegex = "^\\s*\\d+(:\\d+){1,}\$"

    private val pattern1 = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE)
    private val pattern2 = Pattern.compile(titleRegex, Pattern.CASE_INSENSITIVE)
    private val pattern3 = Pattern.compile(imgRegex, Pattern.CASE_INSENSITIVE)
    private val pattern4 = Pattern.compile(dateRegex, Pattern.CASE_INSENSITIVE)
    private val pattern5 = Pattern.compile(uperRegex, Pattern.CASE_INSENSITIVE)
    private val pattern6 = Pattern.compile(watchNumRegex, Pattern.CASE_INSENSITIVE)
    private val pattern7 = Pattern.compile(lengthRegex, Pattern.CASE_INSENSITIVE)

    private var num = 0
    private var page: Int = 1

    private var newData: MutableLiveData<List<Video>> = MutableLiveData()
    private var videoList: ArrayList<Video> = arrayListOf()

    private var isGone: MutableLiveData<Int> = MutableLiveData()
    private var isRefresh: MutableLiveData<Boolean> = MutableLiveData()


    fun isGone(): LiveData<Int> {
        return isGone
    }

    fun isRefresh(): LiveData<Boolean> {
        return isRefresh
    }

    private fun load(webView: WebView,url: String) {
        webView.loadUrl(url + page)
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                webView.loadUrl("javascript:window.local_obj.loadMore(document.getElementsByClassName('video-contain')[0].innerHTML);")
            }
        }
    }

    fun initVideo(webView: WebView,url: String): MutableLiveData<List<Video>> {
        isGone.postValue(View.VISIBLE)
        webView.settings.javaScriptEnabled = true
        webView.settings.useWideViewPort = true
        webView.settings.domStorageEnabled = true
        webView.settings.loadWithOverviewMode = true
        webView.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
        webView.addJavascriptInterface(InJavaScriptLocalObj(), "local_obj")
        load(webView,url)
        return newData
    }

    fun loadNext(webView: WebView,url: String): MutableLiveData<List<Video>> {
        isGone.postValue(View.VISIBLE)
        page++
        load(webView,url)
        return newData
    }

    fun refresh(webView: WebView,url: String): MutableLiveData<List<Video>> {
        page = 1
        videoList.clear()
        isRefresh.postValue(true)
        load(webView,url)
        return newData
    }


    internal inner class InJavaScriptLocalObj {
        @JavascriptInterface
        fun loadMore(html: String) {
            val br = BufferedReader(InputStreamReader(html.byteInputStream()))
            var urls: MutableList<String> = ArrayList()
            var titles: MutableList<String> = ArrayList()
            var imgs: MutableList<String> = ArrayList()
            var dates: MutableList<String> = ArrayList()
            var upers: MutableList<String> = ArrayList()
            var watchNums: MutableList<String> = ArrayList()
            var danmuNums: MutableList<String> = ArrayList()
            var lengths: MutableList<String> = ArrayList()
            var flag = true

            br.forEachLine {
                val matcher1 = pattern1.matcher(it)
                val matcher2 = pattern2.matcher(it)
                val matcher3 = pattern3.matcher(it)
                val matcher4 = pattern4.matcher(it)
                val matcher5 = pattern5.matcher(it)
                val matcher6 = pattern6.matcher(it)
                val matcher7 = pattern7.matcher(it)

                while (matcher1.find()) {
                    val str = matcher1.group()
                    val indexStart = str.indexOf("\"") + 1
                    val endIndex = str.indexOf(" ") - 2
                    val link = str.substring(indexStart, endIndex)
                    urls.add(link)
                }
                while (matcher2.find()) {
                    val str = matcher2.group()
                    val indexStart = str.indexOf("i") + 6
                    val endIndex = str.indexOf("\" c")
                    val title = str.substring(indexStart, endIndex)
                    titles.add(title)
                }
                while (matcher3.find()) {
                    val str = matcher3.group()
                    val img = str.substring(19, str.length)
                    imgs.add(img)
                }
                while (matcher4.find()) {
                    val date = matcher4.group()
                    dates.add(date)
                }
                while (matcher5.find()) {
                    val str = matcher5.group()
                    val indexStart = str.indexOf(">") + 1
                    val endIndex = str.indexOf("<")
                    val uper = str.substring(indexStart, endIndex)
                    upers.add(uper)
                }
                while (matcher6.find()) {
                    val str = matcher6.group()
                    val times = str.trim()
                    if (flag) {
                        watchNums.add(times)
                        flag = false
                    } else {
                        danmuNums.add(times)
                        flag = true
                    }
                }
                while (matcher7.find()) {
                    val str = matcher7.group()
                    val length = str.trim()
                    lengths.add(length)
                }
            }

            while (num < urls.size) {
                var video = Video(
                    "https:" + urls[num],
                    titles[num],
                    "https://" + imgs[num],
                    dates[num],
                    upers[num] + "·",
                    watchNums[num] + "次播放·",
                    danmuNums[num] + "弹幕",
                    lengths[num]
                )
                if (!videoList.contains(video)) {
                    videoList.add(video)
                }
                num++
            }
            num = 0
            newData.postValue(videoList)
            isGone.postValue(View.GONE)
            isRefresh.postValue(false)
        }
    }

    companion object {

        @Volatile
        private var instance: VideoNetwork? = null

        fun getInstance() = instance ?: synchronized(this) {
            instance ?: VideoNetwork().also { instance = it }
        }
    }
}
