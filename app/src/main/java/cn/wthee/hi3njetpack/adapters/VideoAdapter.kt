package cn.wthee.hi3njetpack.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.hi3njetpack.MyApplication
import cn.wthee.hi3njetpack.R
import cn.wthee.hi3njetpack.data.Video
import cn.wthee.hi3njetpack.databinding.ItemVideoBinding
import cn.wthee.hi3njetpack.util.PreviewPicUtil
import cn.wthee.hi3njetpack.view.VideoFragmentDirections

class VideoAdapter : ListAdapter<Video, VideoAdapter.ViewHolder>(VideoDiffCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_video, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val video = getItem(position)
        val params = holder.videoImg.layoutParams
        val metrics = MyApplication.context.resources.displayMetrics
        params.width = metrics.widthPixels
        val scale = params.width * 1.0 / 168
        params.height = (100 * scale).toInt()
        holder.videoImg.layoutParams = params
        holder.apply {
            bind(createOnClickListener(video!!.url,video.title), createOnLongClickListener(video.imgurl),video)
            itemView.tag = video
        }

    }

    private fun createOnClickListener(link: String,title: String): View.OnClickListener {
        return View.OnClickListener {
            val direction = VideoFragmentDirections.actionVideoFragmentToVideoWebFragment(link,title)
            it.findNavController().navigate(direction)
        }
    }

    private fun createOnLongClickListener(imgurl: String): View.OnLongClickListener {
        return View.OnLongClickListener {
            PreviewPicUtil.check(it.context, imgurl)
            return@OnLongClickListener true
        }
    }


    class ViewHolder(
        private val binding: ItemVideoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        var videoImg = binding.videoImage
        fun bind(listener: View.OnClickListener, longClickListener: View.OnLongClickListener, item: Video) {
            binding.apply {
                clickListener = listener
                longClick = longClickListener
                video = item
                executePendingBindings()
            }
        }
    }
}

private class VideoDiffCallback : DiffUtil.ItemCallback<Video>() {

    override fun areItemsTheSame(oldItem: Video, newItem: Video): Boolean {
        return oldItem.title == newItem.title
    }

    override fun areContentsTheSame(oldItem: Video, newItem: Video): Boolean {
        return oldItem == newItem
    }
}