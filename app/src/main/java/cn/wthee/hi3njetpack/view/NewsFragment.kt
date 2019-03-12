package cn.wthee.hi3njetpack.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.hi3njetpack.R
import cn.wthee.hi3njetpack.adapters.NewsAdapter
import cn.wthee.hi3njetpack.databinding.FragmentNewsBinding
import cn.wthee.hi3njetpack.util.InjectorUtil
import cn.wthee.hi3njetpack.util.RecyclerViewUtil
import cn.wthee.hi3njetpack.viewmodels.NewsViewModel

class NewsFragment : Fragment() {

    private lateinit var viewModel: NewsViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var binding: FragmentNewsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewsBinding.inflate(inflater,container,false)
        val factory = InjectorUtil.getNewsViewModelFactory()
        viewModel = ViewModelProviders.of(this,factory).get(NewsViewModel::class.java)
        recyclerView = binding.root.findViewById(R.id.news_list)
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
    }

}
