package cn.wthee.hi3njetpack.view


import android.os.Bundle
import android.os.Handler
import android.view.*
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import cn.wthee.hi3njetpack.R
import cn.wthee.hi3njetpack.adapters.VideoAdapter
import cn.wthee.hi3njetpack.databinding.FragmentVideoBinding
import cn.wthee.hi3njetpack.util.InjectorUtil
import cn.wthee.hi3njetpack.util.RecyclerViewUtil
import cn.wthee.hi3njetpack.viewmodels.VideoViewModel
import android.view.animation.LayoutAnimationController
import android.view.animation.ScaleAnimation
import android.view.animation.TranslateAnimation
import com.google.android.material.tabs.TabLayout


class VideoFragment : Fragment() {

    private val urlDefault ="https://search.bilibili.com/all?keyword=%E5%B4%A9%E5%9D%8F3"
    private val order = arrayListOf("totalrank","click","pubdate","dm","stow")
    private val orderText = arrayListOf("综合排序","最多点击","最新发布","最多弹幕","最多收藏")
    private val orderN = 0
    private val duration = arrayListOf("0","1","2","3","4")
    private val durationText = arrayListOf("全部时长","10分钟以下","10-30分钟","30-60分钟","60分钟以上")
    private val durationN = 0

    private lateinit var viewModel: VideoViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipe: SwipeRefreshLayout
    private lateinit var top: ImageView
    private lateinit var filter: LinearLayout
    private lateinit var tabOrder: TabLayout
    private lateinit var tabDura: TabLayout
    private lateinit var binding: FragmentVideoBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        binding = FragmentVideoBinding.inflate(inflater,container,false)
        val factory = InjectorUtil.getVideoViewModelFactory(binding.myWeb, urlDefault+
                    "&order=${order[2]}" +
                    "&duration=${duration[durationN]}" +
                    "&tids_1=0" +
                    "&page=")
        viewModel = ViewModelProviders.of(this,factory).get(VideoViewModel::class.java)
        bindView()
        setTab()
        val adapter = VideoAdapter()
        binding.videoList.adapter = adapter
        subscribeUi(adapter)
        addListener()
        return binding.root
    }

    private fun bindView(){
        recyclerView = binding.videoList
        swipe = binding.videoSwipe
        top = binding.videoGoTop
        filter = binding.filterLayout
        tabOrder = binding.tabO
        tabDura  =binding.tabD
    }

    private fun setTab(){
        orderText.forEach {
            tabOrder.addTab(tabOrder.newTab().setText(it))
        }
        durationText.forEach {
            tabDura.addTab(tabDura.newTab().setText(it))
        }
    }

    private fun subscribeUi(adapter: VideoAdapter) {
        viewModel.video.observe(viewLifecycleOwner, Observer { video ->
            if (video != null) {
                adapter.submitList(video)
                adapter.notifyDataSetChanged()
            }
        })
        viewModel.isGone.observe(viewLifecycleOwner, Observer {
            (activity as AppCompatActivity).findViewById<ProgressBar>(R.id.web_pb).visibility = it
        })
        viewModel.isRefresh.observe(viewLifecycleOwner, Observer {
            swipe.isRefreshing = it
        })
    }

    private fun addListener(){
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE && RecyclerViewUtil.isBottom(recyclerView)) {
                    viewModel.loadMore()
                }
            }
            var mScrollThreshold: Int = 0
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val isSignificantDelta = Math.abs(dy) > mScrollThreshold
                if (isSignificantDelta) {
                    if (dy > 0) {
                        top.visibility = View.GONE
                        filter.visibility = View.GONE
                    } else {
                        top.visibility = View.VISIBLE
                    }
                }
                if (!recyclerView.canScrollVertically(-1)) {
                    top.visibility = View.GONE
                }
            }
        })

        recyclerView.setOnTouchListener(object : View.OnTouchListener{
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                return swipe.isRefreshing
            }
        })

        swipe.setOnRefreshListener {
            viewModel.refresh()
        }

        top.setOnClickListener {
            recyclerView.scrollToPosition(0)
        }
    }

    private fun setAnim(isVisible: Boolean){
        val translateAnimation:TranslateAnimation =
            if(isVisible)
                TranslateAnimation(0f,0f,0f, -100f)
            else
                TranslateAnimation(0f,0f, -100f,0f)
        translateAnimation.duration = 800
        val controller = LayoutAnimationController(translateAnimation, 0f)
        filter.layoutAnimation = controller
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_video, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.filter -> {
                filter.visibility = if(filter.visibility==View.GONE) {
                    setAnim(false)
                    View.VISIBLE
                } else {
                    setAnim(true)
                    View.GONE
                }
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
