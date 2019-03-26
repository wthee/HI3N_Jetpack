package cn.wthee.hi3nlite.view


import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import cn.wthee.hi3njetpack.R
import cn.wthee.hi3nlite.util.PreviewPicUtil
import kotlinx.android.synthetic.main.fragment_book.*


class BookFragment : Fragment() {

    private lateinit var webView: WebView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).findViewById<ProgressBar>(cn.wthee.hi3njetpack.R.id.web_pb).visibility = View.VISIBLE
        var view = inflater.inflate(R.layout.fragment_book, container, false)
        webView = view.findViewById<WebView>(R.id.bookWeb)
        webView.settings.javaScriptEnabled = true
        webView.settings.useWideViewPort = true
        webView.settings.domStorageEnabled = true
        webView.settings.loadWithOverviewMode = true
        webView.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
        webView.loadUrl("https://comic.benghuai.com/book")
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                (activity as AppCompatActivity).findViewById<ProgressBar>(cn.wthee.hi3njetpack.R.id.web_pb).visibility = View.GONE
               super.onPageFinished(view, url)
            }
        }
        webView.setOnKeyListener { _, keyCode, _ ->
            if ((keyCode == KeyEvent.KEYCODE_BACK)) {
                if(webView.canGoBack()) {
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
                PreviewPicUtil.preview(view.context, hitTestResult.extra)
                return@setOnLongClickListener true
            }
            return@setOnLongClickListener false;
        }
        return view
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

}
