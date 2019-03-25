package cn.wthee.hi3nlite.view

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import cn.wthee.hi3njetpack.R
import cn.wthee.hi3nlite.adapters.VideoAdapter
import cn.wthee.hi3njetpack.databinding.FragmentVideoBinding
import cn.wthee.hi3nlite.util.InjectorUtil
import cn.wthee.hi3nlite.util.RecyclerViewUtil
import cn.wthee.hi3nlite.viewmodels.VideoViewModel
import cn.wthee.hi3nlite.viewmodels.VideoViewModelFactory
import com.google.android.material.tabs.TabLayout
import android.view.ViewGroup
import cn.wthee.hi3nlite.util.OCAnim
import com.google.android.material.appbar.AppBarLayout


class VideoFragment : Fragment() {

    private val urlDefault ="https://search.bilibili.com/all?keyword=%E5%B4%A9%E5%9D%8F3"
    private var mUrl: String = urlDefault+
            "&order=pubdate" +
            "&duration=0" +
            "&tids_1=0" +
            "&page="
    private val order = arrayListOf("totalrank","click","pubdate","dm","stow")
    private val orderText = arrayListOf("综合排序","最多点击","最新发布","最多弹幕","最多收藏")
    private var orderN = 2
    private val duration = arrayListOf("0","1","2","3","4")
    private val durationText = arrayListOf("全部时长","0-10分钟","10-30分钟","30-60分钟","60+分钟")
    private var durationN = 0

    private lateinit var viewModel: VideoViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipe: SwipeRefreshLayout
    private lateinit var top: ImageView
    private lateinit var filter: AppBarLayout
    private var filterIsEXPANDED: Boolean = false
    private lateinit var tabOrder: TabLayout
    private lateinit var tabDura: TabLayout
    private lateinit var binding: FragmentVideoBinding
    private lateinit var factory: VideoViewModelFactory

    private var tabLayoutMeasuredHeight: Int = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        binding = FragmentVideoBinding.inflate(inflater,container,false)
        factory = InjectorUtil.getVideoViewModelFactory(binding.myWeb, mUrl)
        viewModel = ViewModelProviders.of(this,factory).get(VideoViewModel::class.java)
        initView()


        var density = resources.displayMetrics.density
        //TabLayout高度
        tabLayoutMeasuredHeight =  (density * 50 * 2 + 0.5).toInt()

        //绑定adapter
        val adapter = VideoAdapter()
        binding.videoList.adapter = adapter
        subscribeUi(adapter)

        //添加监听事件
        addListener()

        return binding.root
    }

    private fun initView(){
        recyclerView = binding.videoList
        swipe = binding.videoSwipe
        top = binding.videoGoTop
        filter = binding.videoAppBar
        tabOrder = binding.tabO
        tabDura  =binding.tabD

        orderText.forEach {
            tabOrder.addTab(tabOrder.newTab().setText(it))
        }
        durationText.forEach {
            tabDura.addTab(tabDura.newTab().setText(it))
        }
        tabOrder.getTabAt(orderN)!!.select()
        tabDura.getTabAt(durationN)!!.select()
        recyclerView.setItemViewCacheSize(20)
    }


    private fun subscribeUi(adapter: VideoAdapter) {
        viewModel.video.observe(viewLifecycleOwner, Observer { video ->
            if (video != null) {
                adapter.submitList(video)
                adapter.notifyDataSetChanged()
            }
        })
        viewModel.isGone.observe(viewLifecycleOwner, Observer {
            (activity as AppCompatActivity).findViewById<ProgressBar>(cn.wthee.hi3njetpack.R.id.web_pb).visibility = it
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
                    viewModel.loadMore(mUrl)
                }
            }
            var mScrollThreshold: Int = 0
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val isSignificantDelta = Math.abs(dy) > mScrollThreshold
                if (isSignificantDelta) {
                    if (dy > 0) {
                        top.visibility = View.GONE
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
            viewModel.refresh(mUrl)
        }

        top.setOnClickListener {
            recyclerView.scrollToPosition(0)
        }

        tabOrder.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabReselected(p0: TabLayout.Tab?) {

            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {

            }

            override fun onTabSelected(p0: TabLayout.Tab?) {
                orderN = p0!!.position
                mUrl = urlDefault+
                        "&order=${order[orderN]}" +
                        "&duration=${duration[durationN]}" +
                        "&tids_1=0" +
                        "&page="
                viewModel.refresh(mUrl)
            }
        })

        tabDura.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabReselected(p0: TabLayout.Tab?) {

            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {

            }

            override fun onTabSelected(p0: TabLayout.Tab?) {
                durationN = p0!!.position
                mUrl = urlDefault+
                        "&order=${order[orderN]}" +
                        "&duration=${duration[durationN]}" +
                        "&tids_1=0" +
                        "&page="
                viewModel.refresh(mUrl)
            }
        })

        filter.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { p0, p1 ->
            if( p1 == 0 ) {

            }else if(p1 <= -250){
                //折叠状态
                filterIsEXPANDED = false
            }else {
                //中间状态
            }
        })

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_video, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.filter -> {
                if(!filterIsEXPANDED) {
                    OCAnim.animateOpen(filter,tabLayoutMeasuredHeight)
                    filterIsEXPANDED = true
                }else{
                    filter.setExpanded(false)
                    filterIsEXPANDED = false
                }
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
