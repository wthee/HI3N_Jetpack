package cn.wthee.hi3nlite.ui.news

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import cn.wthee.hi3njetpack.R
import cn.wthee.hi3nlite.adapters.NewsAdapter
import cn.wthee.hi3njetpack.databinding.FragmentNewsBinding
import cn.wthee.hi3nlite.util.InjectorUtil
import cn.wthee.hi3nlite.util.RecyclerViewUtil

class NewsFragment : Fragment() {

    private lateinit var viewModel: NewsViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipe: SwipeRefreshLayout
    private lateinit var top: ImageView
    private lateinit var binding: FragmentNewsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewsBinding.inflate(inflater,container,false)
        val factory = InjectorUtil.getNewsViewModelFactory()
        viewModel = ViewModelProviders.of(this,factory).get(NewsViewModel::class.java)

        recyclerView = binding.root.findViewById(R.id.news_list)
        swipe = binding.newsSwipe
        top = binding.newsGoTop
        val adapter = NewsAdapter()
        binding.newsList.adapter = adapter
        subscribeUi(adapter)
        addListener()
        return binding.root
    }

    private fun subscribeUi(adapter: NewsAdapter) {
        viewModel.news.observe(viewLifecycleOwner, Observer { news ->
            if (news != null) {
                adapter.submitList(news)
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

}
