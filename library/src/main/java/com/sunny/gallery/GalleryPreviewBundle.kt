package com.sunny.gallery

import android.os.Parcel
import android.os.Parcelable
import com.sunny.gallery.select.bean.GalleryBean
import com.sunny.zy.ZyFrameStore
import com.sunny.zy.base.BaseBundle

/**
 * Desc
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2021/10/18 16:16
 */
class GalleryPreviewBundle : BaseBundle() {

    companion object {
        const val TYPE_SELECT = 0
        const val TYPE_PREVIEW = 1
        const val TYPE_CAMERA = 2

        const val PREVIEW_TYPE = "previewType"
        const val INDEX = "index"
        const val SELECT_DATA = "selectData"
        const val URL_DATA = "urlData"
        const val IS_DELETE = "isDelete"
    }


    fun setUrlDataList(list: ArrayList<String>): GalleryPreviewBundle {
        bundle.putStringArrayList(URL_DATA, list)
        return this
    }

    fun setDataList(list: ArrayList<GalleryBean>): GalleryPreviewBundle {
        bundle.putParcelableArrayList(ZyFrameStore.DATA, list)
        return this
    }

    fun setIndex(index: Int): GalleryPreviewBundle {
        bundle.putInt(INDEX, index)
        return this
    }

    fun setMaxSize(size: Int): GalleryPreviewBundle {
        bundle.putInt(GallerySelectBundle.MAX_SIZE_INT, size)
        return this
    }

    fun setPreviewType(previewType: Int): GalleryPreviewBundle {
        bundle.putInt(PREVIEW_TYPE, previewType)
        return this
    }


    fun setSelectList(list: ArrayList<GalleryBean>): GalleryPreviewBundle {
        bundle.putParcelableArrayList(SELECT_DATA, list)
        return this
    }

    fun setIsDelete(isDelete: Boolean): GalleryPreviewBundle {
        bundle.putBoolean(IS_DELETE, isDelete)
        return this
    }

    override fun clear(): GalleryPreviewBundle {
        super.clear()
        return this
    }


    class Result() : Parcelable {
        var resultList = arrayListOf<GalleryBean>()
        var isComplete = false

        constructor(parcel: Parcel) : this() {
            parcel.readTypedList(resultList, GalleryBean.CREATOR)
            isComplete = parcel.readByte() != 0.toByte()
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeTypedList(resultList)
            parcel.writeByte(if (isComplete) 1 else 0)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<Result> {
            override fun createFromParcel(parcel: Parcel): Result {
                return Result(parcel)
            }

            override fun newArray(size: Int): Array<Result?> {
                return arrayOfNulls(size)
            }
        }

    }
}