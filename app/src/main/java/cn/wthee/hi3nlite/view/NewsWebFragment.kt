package cn.wthee.hi3nlite.view

import android.os.Bundle
import android.os.Handler
import android.view.*
import androidx.fragment.app.Fragment
import cn.wthee.hi3njetpack.databinding.FragmentWebNewsBinding
import cn.wthee.hi3nlite.util.PreviewPicUtil
import android.os.Build
import android.webkit.*
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import cn.wthee.hi3nlite.MyApplication
import cn.wthee.hi3njetpack.R
import cn.wthee.hi3nlite.util.ShareUtil
import com.bumptech.glide.Glide


class NewsWebFragment : Fragment() {

    private lateinit var webView: WebView
    private lateinit var binding:FragmentWebNewsBinding
    private lateinit var mActivity: AppCompatActivity
    private lateinit var toolbar: Toolbar
    private lateinit var webpv: ImageView
    private lateinit var mLink: String
    private lateinit var mTitle: String
    private lateinit var imgurl: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mLink = NewsWebFragmentArgs.fromBundle(arguments!!).link
        mTitle = NewsWebFragmentArgs.fromBundle(arguments!!).title
        setHasOptionsMenu(true)
        mActivity =  (activity as AppCompatActivity)

        binding = FragmentWebNewsBinding.inflate(inflater,container,false)
        webView = binding.webView
        webpv = binding.webPv
        toolbar = mActivity.findViewById(R.id.toolbar)
        showWeb(webView,mLink)


        webView.setOnLongClickListener{
            var hitTestResult = webView.hitTestResult
            if (hitTestResult.type == WebView.HitTestResult.IMAGE_TYPE ||
                hitTestResult.type == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
                PreviewPicUtil.preview(binding.root.context,hitTestResult.extra)
                return@setOnLongClickListener true
            }
            return@setOnLongClickListener false;
        }

        webpv.setOnLongClickListener{
            PreviewPicUtil.preview(binding.root.context,imgurl)
            return@setOnLongClickListener true
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

    private fun showWeb(webView: WebView, url: String){
        webView.settings.javaScriptEnabled = true
        webView.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
        webView.addJavascriptInterface(InJavaScriptLocalObj(), "local_obj")
        webView.loadUrl(url)
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                if(newProgress == 100){
                    webView.loadUrl("javascript:" +
                            "window.local_obj.setBg(\$('#title_img_big').attr('src'));")
                    webView.loadUrl("javascript:" +
                            "\$('#nav-client-header').hide();" +
                            "\$('#title_img_big').hide();" +
                            "\$('.big_bg')[0].style.margin = '0px';" +
                            "\$('#main')[0].style.background = '#ffffff';" +
                            "\$('#main')[0].style.color = '#000000';" +
                            "\$('.page_control').hide();" +
                            "\$('.footer').hide();")
                    Handler().postDelayed({
                        webView.visibility = View.VISIBLE
                    }, 100.toLong())
                }
                super.onProgressChanged(view, newProgress)
            }


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
                ShareUtil.shareText("$mTitle——点击查看$mLink",binding.root.context)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    internal inner class InJavaScriptLocalObj {
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        @JavascriptInterface
        fun setBg(img: String){
            mActivity.runOnUiThread {
                imgurl = img
                Glide.with(MyApplication.context)
                    .load(img)
                    .into(webpv)
            }

        }
    }

}
