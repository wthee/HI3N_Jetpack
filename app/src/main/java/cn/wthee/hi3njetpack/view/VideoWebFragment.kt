package cn.wthee.hi3njetpack.view

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import cn.wthee.hi3njetpack.databinding.FragmentWebVideoBinding
import cn.wthee.hi3njetpack.util.ImgUtil
import android.os.Build
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ShareCompat
import cn.wthee.hi3njetpack.R
import im.delight.android.webview.AdvancedWebView


class VideoWebFragment : Fragment(), AdvancedWebView.Listener {


    private lateinit var webView: AdvancedWebView
    private lateinit var binding: FragmentWebVideoBinding
    private lateinit var mActivity: AppCompatActivity
    private lateinit var toolbar: Toolbar
    private lateinit var mLink: String
    private lateinit var mTitle: String
    private lateinit var imgurl: String

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
        showWeb(webView, mLink)

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

    override fun onPageFinished(url: String?) {

    }

    override fun onPageError(errorCode: Int, description: String?, failingUrl: String?) {
    }

    override fun onDownloadRequested(
        url: String?,
        suggestedFilename: String?,
        mimeType: String?,
        contentLength: Long,
        contentDisposition: String?,
        userAgent: String?
    ) {
    }

    override fun onExternalPageRequest(url: String?) {}

    override fun onPageStarted(url: String?, favicon: Bitmap?) {}

    override fun onDestroyView() {
        super.onDestroyView()
        toolbar.title = mActivity.getText(R.string.app_name)
    }

    private fun showWeb(webView: AdvancedWebView, url: String) {
        webView.setListener(mActivity, this);
        webView.setDesktopMode(true)
        webView.settings.javaScriptEnabled = true
        webView.settings.setUseWideViewPort(false)
        webView.settings.setLoadWithOverviewMode(false)
        webView.settings.setSupportZoom(false)
        webView.settings.setBuiltInZoomControls(false)
        webView.setInitialScale(150)
        webView.loadUrl(url)
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
                val shareIntent = ShareCompat.IntentBuilder.from(activity)
                    .setText(mTitle +  mLink+"点击查看")
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
