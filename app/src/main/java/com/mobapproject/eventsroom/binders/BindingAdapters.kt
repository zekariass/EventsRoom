package com.mobapproject.eventsroom.binders

import android.view.View
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.mobapproject.eventsroom.R
import com.mobapproject.eventsroom.adapters.EventsRecyclerAdapter
import com.mobapproject.eventsroom.data.Event
import com.mobapproject.eventsroom.modelviews.EventLoadingStatus

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<Event>?) {
    val adapter = recyclerView.adapter as EventsRecyclerAdapter
    adapter.submitList(data)
}

@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
        Glide.with(imgView.context)
            .load(imgUri)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.ic_gerald_g_boy_face_cartoon)
                    .error(R.drawable.ic_gerald_g_boy_face_cartoon)
            )
            .into(imgView)
    }

}

@BindingAdapter("iconUrl")
fun bindIcon(iconView: ImageView, iconUrl: String?) {
    iconUrl?.let {
        val iconUri = iconUrl.toUri().buildUpon().scheme("http").build()
        Glide.with(iconView.context)
            .load(iconUri)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.ic_connection_error)
                    .error(R.drawable.ic_connection_error)
            )
            .into(iconView)
    }
}

@BindingAdapter("eventLoadingStatus")
fun eventLoading(loadingStatusImageView: ImageView, status: EventLoadingStatus?) {
    when (status) {
        EventLoadingStatus.LOADING -> {
            loadingStatusImageView.visibility = View.VISIBLE
            loadingStatusImageView.setImageResource(R.drawable.loading_animation)
        }
        EventLoadingStatus.DONE -> {
            loadingStatusImageView.visibility = View.GONE
        }
        EventLoadingStatus.ERROR -> {
            loadingStatusImageView.visibility = View.VISIBLE
            loadingStatusImageView.setImageResource(R.drawable.ic_connection_error)
        }
    }
}