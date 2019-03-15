package cn.wthee.hi3njetpack.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import cn.wthee.hi3njetpack.databinding.FragmentWebVideoBinding
import cn.wthee.hi3njetpack.util.ImgUtil
import android.os.Build
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ShareCompat
import cn.wthee.hi3njetpack.R
import android.widget.FrameLayout
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebSettings
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient


class VideoWebFragment : Fragment() {


    private lateinit var webView: com.tencent.smtt.sdk.WebView
    private lateinit var binding: FragmentWebVideoBinding
    private lateinit var mActivity: AppCompatActivity
    private lateinit var toolbar: Toolbar
    private lateinit var mLink: String
    private lateinit var mTitle: String
    private var customView: View? = null
    private var fullscreenContainer: FrameLayout? = null
    protected val COVER_SCREEN_PARAMS =
        FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    private var customViewCallback: IX5WebChromeClient.CustomViewCallback? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mLink = VideoWebFragmentArgs.fromBundle(arguments!!).link
        mTitle = VideoWebFragmentArgs.fromBundle(arguments!!).title
        setHasOptionsMenu(true)
        mActivity = (activity as AppCompatActivity)
        binding = FragmentWebVideoBinding.inflate(inflater, container, false)
        webView = binding.webView
        toolbar = mActivity.findViewById(R.id.toolbar)
        //showWeb(webView, mLink)
        initWebView()
        webView.setOnLongClickListener {
            var hitTestResult = webView.hitTestResult
            if (hitTestResult.type == WebView.HitTestResult.IMAGE_TYPE ||
                hitTestResult.type == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE
            ) {
                ImgUtil.check(binding.root.context, hitTestResult.extra)
                return@setOnLongClickListener true
            }
            return@setOnLongClickListener false;
        }

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        //toolbar.title = mActivity.getText(R.string.app_name)
    }

    private fun showWeb(webView: WebView, url: String) {

        webView.loadUrl(url)
    }


    private fun initWebView() {
        val wvcc = com.tencent.smtt.sdk.WebChromeClient()
        val webSettings = webView.settings
        setDesktopMode(true)
        webSettings.javaScriptEnabled = true
        webSettings.useWideViewPort = true // 关键点
        webSettings.allowFileAccess = true // 允许访问文件
        webSettings.setSupportZoom(true) // 支持缩放
        webSettings.loadWithOverviewMode = true
        webSettings.cacheMode = WebSettings.LOAD_NO_CACHE // 不加载缓存内容

        webView.webChromeClient = wvcc
        val wvc = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                webView.loadUrl(url)
                return true
            }
        }
        webView.webViewClient = wvc

        webView.webChromeClient = object : WebChromeClient() {

            /*** 视频播放相关的方法  */
            override fun getVideoLoadingProgressView(): View? {
                val frameLayout = binding.videoWebFragment
                frameLayout.layoutParams =
                    ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                return frameLayout
            }

            override fun onShowCustomView(view: View, callback: IX5WebChromeClient.CustomViewCallback) {
                showCustomView(view, callback)
            }

            override fun onHideCustomView() {
                hideCustomView()
            }
        }

        // 加载Web地址
        webView.loadUrl(mLink)
    }

    fun setDesktopMode(enabled: Boolean) {
        val webSettings = webView.getSettings()

        val newUserAgent: String
        if (enabled) {
            newUserAgent = webSettings.getUserAgentString().replace("Mobile", "eliboM").replace("Android", "diordnA")
        } else {
            newUserAgent = webSettings.getUserAgentString().replace("eliboM", "Mobile").replace("diordnA", "Android")
        }

        webSettings.setUserAgentString(newUserAgent)
        webSettings.setUseWideViewPort(enabled)
        webSettings.setLoadWithOverviewMode(enabled)
        webSettings.setSupportZoom(enabled)
        webSettings.setBuiltInZoomControls(enabled)
    }

    /** 视频播放全屏  */
    private fun showCustomView(view: View, callback: IX5WebChromeClient.CustomViewCallback) {
        // if a view already exists then immediately terminate the new one
        if (customView != null) {
            callback.onCustomViewHidden()
            return
        }

        mActivity.getWindow().getDecorView()

        val decor = mActivity.getWindow().getDecorView() as FrameLayout
        fullscreenContainer = FullscreenHolder(binding.root.context)
        (fullscreenContainer as FullscreenHolder).addView(view, COVER_SCREEN_PARAMS)
        decor.addView(fullscreenContainer, COVER_SCREEN_PARAMS)
        customView = view
        setStatusBarVisibility(false)
        customViewCallback = callback
    }

    /** 隐藏视频全屏  */
    private fun hideCustomView() {
        if (customView == null) {
            return
        }

        setStatusBarVisibility(true)
        val decor = mActivity.getWindow().getDecorView() as FrameLayout
        decor.removeView(fullscreenContainer)
        fullscreenContainer = null
        customView = null
        customViewCallback!!.onCustomViewHidden()
        webView.visibility = View.VISIBLE
    }

    /** 全屏容器界面  */
    internal class FullscreenHolder(ctx: Context) : FrameLayout(ctx) {

        init {
            setBackgroundColor(ctx.getResources().getColor(android.R.color.black))
        }

        override fun onTouchEvent(evt: MotionEvent): Boolean {
            return true
        }
    }

    private fun setStatusBarVisibility(visible: Boolean) {
        val flag = if (visible) 0 else WindowManager.LayoutParams.FLAG_FULLSCREEN
        mActivity.getWindow().setFlags(flag, WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }


//    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
//        when (keyCode) {
//            KeyEvent.KEYCODE_BACK -> {
//                /** 回退键 事件处理 优先级:视频播放全屏-网页回退-关闭页面  */
//                if (customView != null) {
//                    hideCustomView()
//                } else if (webView.canGoBack()) {
//                    webView.goBack()
//                } else {
//
//                }
//                return true
//            }
//            else -> return  false
//        }
//    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_web_fragment, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    @Suppress("DEPRECATION")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_share -> {
                val shareIntent = ShareCompat.IntentBuilder.from(activity)
                    .setText(mTitle + mLink + "点击查看")
                    .setType("text/plain")
                    .createChooserIntent()
                    .apply {
                        // https://android-developers.googleblog.com/2012/02/share-with-intents.html
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            // If we're on Lollipop, we can open the intent as a document
                            addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
                        } else {
                            // Else, we will use the old CLEAR_WHEN_TASK_RESET flag
                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
                        }
                    }
                startActivity(shareIntent)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    internal inner class InJavaScriptLocalObj {
//        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
//        @JavascriptInterface
//        fun setTitle(title: String) {
//            mActivity.runOnUiThread {
//                toolbar.title = title
//            }
//        }
    }

}
