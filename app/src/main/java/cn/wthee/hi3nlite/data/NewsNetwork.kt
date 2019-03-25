package cn.wthee.hi3nlite.data

import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import cn.wthee.hi3nlite.util.NewWebViewUtil
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.ArrayList
import java.util.regex.Pattern


class NewsNetwork{

    private var PRE_IMGURL = "https://webstatic.bh3.com/www_bh3_com/news_image/uploads/"
    private var PRE_LINK = "https://www.bh3.com/index.php/news/"

    private val linkRegex = "href=\"\\d{1,3}\">"
    private val titleRegex = "(h3|rt\")>(.*?)</h3"
    private val imgRegex = "uploads/[^\":<>]*\\.(jpg|bmp|gif|ico|pcx|jpeg|tif|png)"
    private val dateRegex = "\\d{4}-\\d{2}-\\d{2}"

    private val pattern1 = Pattern.compile(linkRegex, Pattern.CASE_INSENSITIVE)
    private val pattern2 = Pattern.compile(titleRegex, Pattern.CASE_INSENSITIVE)
    private val pattern3 = Pattern.compile(imgRegex, Pattern.CASE_INSENSITIVE)
    private val pattern4 = Pattern.compile(dateRegex, Pattern.CASE_INSENSITIVE)

    private var num = 0

    private var newData: MutableLiveData<List<News>> = MutableLiveData()
    private var newsList : ArrayList<News> = arrayListOf()
    private var webView = NewWebViewUtil.createWebView()

    private var isGone: MutableLiveData<Int> = MutableLiveData()
    private var isRefresh: MutableLiveData<Boolean> = MutableLiveData()


    fun isGone(): LiveData<Int>{
        return isGone
    }
    fun isRefresh(): LiveData<Boolean>{
        return isRefresh
    }
    fun getNews(): MutableLiveData<List<News>>{
        isGone.postValue(View.VISIBLE)
        webView.settings.javaScriptEnabled = true
        webView.addJavascriptInterface(InJavaScriptLocalObj(), "local_obj")
        webView.loadUrl("https://www.bh3.com/index.php/news/")
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                webView.loadUrl("javascript:window.local_obj.loadMore(document.getElementsByTagName('ul')[2].innerHTML);")
                super.onPageFinished(view, url)
            }
        }
        return newData
    }
    fun loadNext(): MutableLiveData<List<News>>{
        isGone.postValue(View.VISIBLE)
        webView.loadUrl("javascript:\$('#more_btn').click()")
        webView.loadUrl("javascript:window.local_obj.loadMore(document.getElementsByTagName('ul')[2].innerHTML);")
        return newData
    }

    fun refresh(): MutableLiveData<List<News>>{
        newsList.clear()
        isRefresh.postValue(true)
        num = 0
        webView.loadUrl("javascript:window.local_obj.loadMore(document.getElementsByTagName('ul')[2].innerHTML);")
        return newData
    }

    internal inner class InJavaScriptLocalObj {
        @JavascriptInterface
        fun loadMore(html: String) {
            val br = BufferedReader(InputStreamReader(html.byteInputStream()))
            var links: MutableList<String> = ArrayList()
            var titles: MutableList<String> = ArrayList()
            var imgs: MutableList<String> = ArrayList()
            var dates: MutableList<String> = ArrayList()


            br.forEachLine {
                val matcher1 = pattern1.matcher(it)
                val matcher2 = pattern2.matcher(it)
                val matcher3 = pattern3.matcher(it)
                val matcher4 = pattern4.matcher(it)

                while (matcher1.find()) {
                    val str = matcher1.group()
                    val indexStart = str.indexOf("\"") + 1
                    val endIndex = str.indexOf(">") - 1
                    val link = str.substring(indexStart, endIndex)
                    links.add(link)
                }
                while (matcher2.find()) {
                    val str = matcher2.group()
                    val indexStart = str.indexOf(">") + 1
                    val endIndex = str.indexOf("<")
                    val title = str.substring(indexStart, endIndex)
                    titles.add(title)
                }
                while (matcher3.find()) {
                    val str = matcher3.group()
                    val img = str.substring(8, str.length)
                    imgs.add(img)
                }
                while (matcher4.find()) {
                    val timee = matcher4.group()
                    dates.add(timee)
                }
            }

            while (num < titles.size) {
                var news =
                    News(titles[num], PRE_IMGURL + imgs[num], PRE_LINK + links[num], dates[num])
                newsList.add(news)
                num++
            }
            newData.postValue(newsList)
            isGone.postValue(View.GONE)
            isRefresh.postValue(false)
        }
    }

    companion object {

        @Volatile private var instance: NewsNetwork? = null

        fun getInstance()  = instance ?: synchronized(this){
            instance
                ?: NewsNetwork().also { instance = it }
        }
    }
}
