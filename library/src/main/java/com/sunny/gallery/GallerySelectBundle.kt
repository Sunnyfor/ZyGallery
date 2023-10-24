package com.sunny.gallery

import android.os.Bundle
import com.sunny.gallery.select.view.GallerySelectActivity
import com.sunny.zy.base.BaseBundle

/**
 * Desc
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2021/10/18 16:16
 */
class GallerySelectBundle: BaseBundle() {

    companion object{

        const val MAX_SIZE_INT = "maxSize"
        const val IS_CROP_BOOLEAN = "isCrop"
        const val ASPECT_X_INT = "aspectX"
        const val ASPECT_Y_INT = "aspectY"
        const val FILE_TYPE_INT = "fileType"

        const val FILE_TYPE_ALL = 0
        const val File_TYPE_IMAGE = 1
        const val File_TYPE_VIDEO = 2
    }


    fun setMaxSize(size: Int): GallerySelectBundle {
        bundle.putInt(MAX_SIZE_INT, size)
        return this
    }

    fun setFileType(fileType: Int): GallerySelectBundle {
        bundle.putInt(FILE_TYPE_INT, fileType)
        return this
    }

    fun isCrop(isCrop: Boolean): GallerySelectBundle {
        bundle.putBoolean(IS_CROP_BOOLEAN, isCrop)
        return this
    }

    fun setAspectX(aspectX: Int): GallerySelectBundle {
        bundle.putInt(ASPECT_X_INT, aspectX)
        return this
    }

    fun setAspectY(aspectY: Int): GallerySelectBundle {
        bundle.putInt(ASPECT_Y_INT, aspectY)
        return this
    }

    override fun clear(): GallerySelectBundle {
        super.clear()
        return this
    }

}