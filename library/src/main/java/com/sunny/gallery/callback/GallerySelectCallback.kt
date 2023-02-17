package com.sunny.gallery.callback

import com.sunny.gallery.select.bean.GalleryBean

interface GallerySelectCallback {
    fun onSelectResult(selectList: ArrayList<GalleryBean>)
}