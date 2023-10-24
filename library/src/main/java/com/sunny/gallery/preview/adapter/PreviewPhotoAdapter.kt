package com.sunny.gallery.preview.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.documentfile.provider.DocumentFile
import com.sunny.gallery.R
import com.sunny.gallery.select.bean.GalleryBean
import com.sunny.kit.ZyKit
import com.sunny.kit.utils.GlideUtil
import com.sunny.zy.base.BaseRecycleAdapter
import com.sunny.zy.base.BaseRecycleViewHolder

/**
 * Desc
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2021/9/28 11:47
 */
class PreviewPhotoAdapter(data: ArrayList<GalleryBean>) : BaseRecycleAdapter<GalleryBean>(data) {

    var selectIndex: Int = -1

    override fun onBindViewHolder(holder: BaseRecycleViewHolder, position: Int) {
        GlideUtil.loadImage(holder.getView(R.id.ivPhoto), getData(position).uri ?: "")

        val vBorder = holder.getView<View>(R.id.vBorder)
        vBorder.visibility = if (selectIndex == position) {
            View.VISIBLE
        } else {
            View.GONE
        }
        playViewVisibility(holder.getView(R.id.vPlay), getData(position).uri)
    }

    override fun initLayout(parent: ViewGroup, viewType: Int): View {
        return LayoutInflater.from(context).inflate(R.layout.zy_item_gallery_preview, parent, false)
    }

    private fun playViewVisibility(view: View, uri: Uri?) {
        if (uri == null) {
            return
        }
        val type = DocumentFile.fromSingleUri(ZyKit.getContext(), uri)?.type ?: ""
        if (type.contains("video")) {
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.GONE
        }
    }
}