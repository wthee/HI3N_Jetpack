package cn.wthee.hi3njetpack.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.*
import androidx.fragment.app.Fragment
import cn.wthee.hi3njetpack.databinding.FragmentWebNewsBinding
import cn.wthee.hi3njetpack.util.ImgUtil
import android.os.Build
import android.webkit.*
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ShareCompat
import cn.wthee.hi3njetpack.MyApplication
import cn.wthee.hi3njetpack.R
import com.bumptech.glide.Glide


class NewsWebFragment : Fragment() {

    private lateinit var webView: WebView
    private lateinit var binding:FragmentWebNewsBinding
    private lateinit var mActivity: AppCompatActivity
    private lateinit var toolbar: Toolbar
    private lateinit var web_pv: ImageView
    private lateinit var mLink: String
    private lateinit var imgurl: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mLink = NewsWebFragmentArgs.fromBundle(arguments!!).link
        setHasOptionsMenu(true)
        mActivity =  (activity as AppCompatActivity)

        binding = FragmentWebNewsBinding.inflate(inflater,container,false)
        webView = binding.webView
        web_pv = binding.webPv
        toolbar = mActivity.findViewById(R.id.toolbar)
        showWeb(webView,mLink)


        webView.setOnLongClickListener{
            var hitTestResult = webView.hitTestResult
            if (hitTestResult.type == WebView.HitTestResult.IMAGE_TYPE ||
                hitTestResult.type == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
                ImgUtil.check(binding.root.context,hitTestResult.extra)
                return@setOnLongClickListener true
            }
            return@setOnLongClickListener false;
        }

        web_pv.setOnLongClickListener{
            ImgUtil.check(binding.root.context,imgurl)
            return@setOnLongClickListener true
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        toolbar.title = mActivity.getText(R.string.app_name)
    }

    private fun showWeb(webView: WebView, url: String){
        webView.settings.javaScriptEnabled = true
        webView.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
        webView.addJavascriptInterface(InJavaScriptLocalObj(), "local_obj")
        webView.loadUrl(url)
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                if(newProgress == 100){
//                    webView.loadUrl("javascript:" +
//                            "window.local_obj.setTitle(\$('h2').text());")
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
                val shareIntent = ShareCompat.IntentBuilder.from(activity)
                    .setText("点击查看"+mLink)
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
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
//        @JavascriptInterface
//        fun setTitle(title: String) {
//            mActivity.runOnUiThread {
//                toolbar.title = title
//            }
//        }
        @JavascriptInterface
        fun setBg(img: String){
            mActivity.runOnUiThread {
                imgurl = img
                Glide.with(MyApplication.context)
                    .load(img)
                    .into(web_pv)
            }

        }
    }

}
