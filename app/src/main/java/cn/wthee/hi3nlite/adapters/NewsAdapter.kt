package cn.wthee.hi3nlite.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.hi3nlite.MyApplication
import cn.wthee.hi3njetpack.R
import cn.wthee.hi3nlite.data.News
import cn.wthee.hi3njetpack.databinding.ItemNewsBinding
import cn.wthee.hi3nlite.ui.news.NewsFragmentDirections
import cn.wthee.hi3nlite.util.PreviewPicUtil

class NewsAdapter : ListAdapter<News, NewsAdapter.ViewHolder>(NewsDiffCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_news, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val news = getItem(position)
        val params = holder.newsImage.layoutParams
        val metrics = MyApplication.context.resources.displayMetrics
        params.width = metrics.widthPixels
        val scale = params.width * 1.0 / 912
        params.height = (188 * scale).toInt()
        holder.newsImage.layoutParams = params
        holder.apply {
            bind(createOnClickListener(news!!.link,news.title), createOnLongClickListener(news.imgUrl),news)
            itemView.tag = news
        }
    }

    private fun createOnClickListener(link: String, title: String): View.OnClickListener {
        return View.OnClickListener {
            val direction = NewsFragmentDirections.actionNewsFragmentToWebFragment(link,title)
            it.findNavController().navigate(direction)
        }
    }

    private fun createOnLongClickListener(link: String): View.OnLongClickListener {
        return View.OnLongClickListener {
            PreviewPicUtil.preview(it.context, link)
            return@OnLongClickListener true
        }
    }


    class ViewHolder(
        private val binding: ItemNewsBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        var newsImage = binding.newsImage
        fun bind(listener: View.OnClickListener, longClickListener: View.OnLongClickListener, item: News) {
            binding.apply {
                clickListener = listener
                longClick = longClickListener
                news = item
                executePendingBindings()
            }
        }
    }
}

private class NewsDiffCallback : DiffUtil.ItemCallback<News>() {

    override fun areItemsTheSame(oldItem: News, newItem: News): Boolean {
        return oldItem.title == newItem.title
    }

    override fun areContentsTheSame(oldItem: News, newItem: News): Boolean {
        return oldItem == newItem
    }
}