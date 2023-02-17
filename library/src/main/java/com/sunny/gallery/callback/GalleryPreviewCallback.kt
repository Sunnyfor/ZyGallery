package com.sunny.gallery.callback

import com.sunny.gallery.select.bean.GalleryBean

interface GalleryPreviewCallback {
    fun onResult(flag: Boolean, resultList: ArrayList<GalleryBean>)
}