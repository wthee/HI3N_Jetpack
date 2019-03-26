package cn.wthee.hi3nlite.view

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import cn.wthee.hi3njetpack.R
import android.widget.FrameLayout
import cn.wthee.hi3njetpack.databinding.FragmentWebVideoBinding
import cn.wthee.hi3nlite.util.PreviewPicUtil
import cn.wthee.hi3nlite.util.ShareUtil
import im.delight.android.webview.AdvancedWebView
import android.util.Log
import android.webkit.*
import cn.wthee.hi3nlite.MainActivity


class VideoWebFragment : Fragment() {

    private lateinit var webView: AdvancedWebView
    private lateinit var binding: FragmentWebVideoBinding
    private lateinit var mActivity: AppCompatActivity
    private lateinit var toolbar: Toolbar
    private lateinit var mLink: String
    private lateinit var mTitle: String
    private var customView: View? = null
    private var fullscreenContainer: FrameLayout? = null
    private val COVER_SCREEN_PARAMS =
        FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    private var customViewCallback: WebChromeClient.CustomViewCallback? = null

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
        initWebView()
        binding.swipWeb.setOnRefreshListener {
            if (binding.swipWeb.isRefreshing) {
                webView.reload()
                binding.swipWeb.isRefreshing = false
            }
        }
        return binding.root
    }

    override fun onPause() {
        webView.onPause()
        super.onPause()
    }

    override fun onResume() {
        webView.onResume()
        super.onResume()
    }

    override fun onDestroyView() {
        webView.destroy()
        super.onDestroyView()
    }

    private fun initWebView() {

        setDesktopMode(true)

        val wvc = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                webView.loadUrl(url)
                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                val cookieManager = CookieManager.getInstance()
                cookieManager.getCookie(url)
                super.onPageFinished(view, url)
            }
        }
        webView.webViewClient = wvc

        webView.webChromeClient = object : WebChromeClient() {
            /*** 视频播放相关的方法  */
            override fun getVideoLoadingProgressView(): View? {
                val frameLayout = binding.videoWebFragment
                frameLayout.layoutParams = COVER_SCREEN_PARAMS
                return frameLayout
            }

            override fun onShowCustomView(view: View, callback: WebChromeClient.CustomViewCallback) {
                showCustomView(view, callback)
            }

            override fun onHideCustomView() {
                hideCustomView()
            }
        }

        webView.setOnKeyListener { v, keyCode, event ->
            if ((keyCode == KeyEvent.KEYCODE_BACK)) {
                if (customView != null) {
                    hideCustomView()
                } else if (webView.canGoBack()) {
                    webView.goBack()
                } else {
                    return@setOnKeyListener false
                }
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
        webView.setOnLongClickListener {
            var hitTestResult = webView.hitTestResult
            if (hitTestResult.type == WebView.HitTestResult.IMAGE_TYPE ||
                hitTestResult.type == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE
            ) {
                PreviewPicUtil.preview(binding.root.context, hitTestResult.extra)
                return@setOnLongClickListener true
            }
            return@setOnLongClickListener false;
        }
        // 加载Web地址
        webView.loadUrl(mLink)
    }

    private fun setDesktopMode(enabled: Boolean) {
        val webSettings = webView.settings
        webSettings.userAgentString =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.109 Safari/537.36"
        webSettings.loadWithOverviewMode = enabled
        webSettings.builtInZoomControls = enabled
        webSettings.javaScriptEnabled = enabled
        webSettings.useWideViewPort = enabled // 关键点
        webSettings.allowFileAccess = enabled // 允许访问文件
        webSettings.setSupportZoom(enabled) // 支持缩放
        webSettings.displayZoomControls = false
        webSettings.cacheMode = WebSettings.LOAD_NO_CACHE // 不加载缓存内容
        webView.setInitialScale(150)
    }

    /** 视频播放全屏  */
    private fun showCustomView(view: View, callback: WebChromeClient.CustomViewCallback) {
        // if a view already exists then immediately terminate the new one
        if (customView != null) {
            callback.onCustomViewHidden()
            return
        }

        val decor = mActivity.window.decorView as FrameLayout
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
        val decor = mActivity.window.decorView as FrameLayout
        decor.removeView(fullscreenContainer)
        fullscreenContainer = null
        customView = null
        customViewCallback!!.onCustomViewHidden()
        webView.visibility = View.VISIBLE
    }

    /** 全屏容器界面  */
    internal class FullscreenHolder(ctx: Context) : FrameLayout(ctx) {

        init {
            setBackgroundColor(ctx.resources.getColor(android.R.color.black))
        }

        override fun onTouchEvent(evt: MotionEvent): Boolean {
            return true
        }
    }

    private fun setStatusBarVisibility(visible: Boolean) {
        if (visible) {
            //竖屏
            mActivity.window.setFlags(0, WindowManager.LayoutParams.FLAG_FULLSCREEN)
            mActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            //横屏
            mActivity.window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
            mActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_web_fragment, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    @Suppress("DEPRECATION")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_share -> {
                ShareUtil.shareText("$mTitle——点击查看$mLink", binding.root.context)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
