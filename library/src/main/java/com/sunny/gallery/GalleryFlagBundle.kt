package com.sunny.gallery

import android.os.Bundle
import com.sunny.gallery.select.view.GallerySelectActivity

/**
 * Desc
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2021/10/18 16:16
 */
class GalleryFlagBundle {

    private val bundle = Bundle()


    fun setMaxSize(size: Int): GalleryFlagBundle {
        bundle.putInt(GallerySelectActivity.MAX_SIZE_INT, size)
        return this
    }

    fun setFileType(fileType: Int): GalleryFlagBundle {
        bundle.putInt(GallerySelectActivity.FILE_TYPE_INT, fileType)
        return this
    }

    fun isCrop(isCrop: Boolean): GalleryFlagBundle {
        bundle.putBoolean(GallerySelectActivity.IS_CROP_BOOLEAN, isCrop)
        return this
    }

    fun setAspectX(aspectX: Int): GalleryFlagBundle {
        bundle.putInt(GallerySelectActivity.ASPECT_X_INT, aspectX)
        return this
    }

    fun setAspectY(aspectY: Int): GalleryFlagBundle {
        bundle.putInt(GallerySelectActivity.ASPECT_X_INT, aspectY)
        return this
    }

    fun build() = bundle
}