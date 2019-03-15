package cn.wthee.hi3njetpack.view


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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


class VideoFragment : Fragment() {

    private lateinit var viewModel: VideoViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var swip: SwipeRefreshLayout
    private lateinit var binding: FragmentVideoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVideoBinding.inflate(inflater,container,false)
        val factory = InjectorUtil.getVideoViewModelFactory(binding.myWeb)
        viewModel = VideoViewModel(binding.myWeb,InjectorUtil.getVideoRepository())
        recyclerView = binding.videoList
        swip = binding.videoSwipe
        val adapter = VideoAdapter()
        binding.videoList.adapter = adapter
        subscribeUi(adapter)
        addListener()
        return binding.root
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
            swip.isRefreshing = it
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
        })

        swip.setOnRefreshListener {
            viewModel.refresh()
        }
    }

}
