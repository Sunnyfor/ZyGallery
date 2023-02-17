package com.sunny.gallery.preview.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.sunny.gallery.R
import com.sunny.gallery.select.bean.GalleryBean
import com.sunny.gallery.widget.photoview.PhotoView
import com.sunny.zy.base.BaseRecycleAdapter
import com.sunny.zy.base.BaseRecycleViewHolder


/**
 * Desc
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2021/9/27 15:43
 */
class PhotoPreviewPageAdapter(data: ArrayList<GalleryBean>) :
    BaseRecycleAdapter<GalleryBean>(data) {

    var onPhotoCallback: (() -> Unit)? = null

    override fun onBindViewHolder(holder: BaseRecycleViewHolder, position: Int) {

        when (holder.itemView) {
            is PhotoView -> {
                val photoView = holder.itemView as PhotoView
                photoView.setImageURI(getData(position).uri)
//                Glide.with(context)
//                    .load()
//                    .into(photoView as ImageView)
            }
            is ConstraintLayout -> {
                val ivPhoto: ImageView = holder.getView(R.id.ivPhoto)
                Glide.with(context)
                    .load(getData(position).uri)
                    .into(ivPhoto)
                holder.getView<View>(R.id.vPlay).setOnClickListener {
//                    VideoPlayActivity.intent(
//                        context as BaseActivity, null, getData(position).uri ?: return@setOnClickListener,
//                    )
                }
            }
        }
        holder.itemView.setOnClickListener {
            onPhotoCallback?.invoke()
        }
    }

    override fun initLayout(parent: ViewGroup, viewType: Int): View {
        return LayoutInflater.from(context).inflate(viewType, parent, false)
    }

    override fun getItemViewType(position: Int): Int {
        if (getData(position).type.contains("video")) {
            return R.layout.zy_layout_videoview
        }
        return R.layout.zy_layout_photoview
    }
}