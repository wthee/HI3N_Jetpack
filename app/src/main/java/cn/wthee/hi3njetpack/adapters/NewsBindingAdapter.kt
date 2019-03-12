package cn.wthee.hi3njetpack.adapters

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions

@BindingAdapter("imageFromUrl")
fun bindImageFromUrl(view: ImageView, imageUrl: String?) {
    if (!imageUrl.isNullOrEmpty()) {
        var requestOptions = RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL)
        Glide.with(view.context)
            .load(imageUrl)
            .apply(requestOptions)
            .into(view)
    }
}
