package com.sunny.gallery

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.sunny.gallery.callback.GalleryPreviewCallback
import com.sunny.gallery.callback.GallerySelectCallback
import com.sunny.gallery.preview.view.GalleryPreviewActivity
import com.sunny.gallery.select.bean.GalleryBean
import com.sunny.gallery.select.view.GallerySelectActivity


class GalleryIntent {

    private var context: Context? = null

    private var launcher: ActivityResultLauncher<Intent>? = null

    /**
     * 相册选择结果回调
     */
    private var resultCallBack: Any? = null

    /**
     * 初始化方法
     */
    fun init(activity: AppCompatActivity) {
        activity.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_CREATE) {
                    context = activity
                    initLaunch(activity)
                }

                if (event == Lifecycle.Event.ON_DESTROY) {
                    activity.lifecycle.removeObserver(this)
                    context = null
                }
            }

        })
    }

    private fun initLaunch(activity: AppCompatActivity) {
        val activityResultContract = ActivityResultContracts.StartActivityForResult()
        launcher = activity.registerForActivityResult(activityResultContract)
        {
            if (it.resultCode == Activity.RESULT_OK) {
                val data: ArrayList<GalleryBean> =
                    it.data?.getParcelableArrayListExtra("data") ?: arrayListOf()

                when (resultCallBack) {
                    is GallerySelectCallback -> {
                        (resultCallBack as GallerySelectCallback).onSelectResult(data)
                    }
                    is GalleryPreviewCallback -> {
                        val flag = it.data?.getBooleanExtra("flag", false) ?: false
                        (resultCallBack as GalleryPreviewCallback).onResult(flag, data)
                    }
                }
            }
        }
    }

    /**
     * 启动相册选择
     */
    fun startGallerySelect(
        flags: GalleryFlagBundle? = null,
        selectResultCallBack: GallerySelectCallback
    ) {
        resultCallBack = selectResultCallBack
        val intent = Intent(context, GallerySelectActivity::class.java)
        intent.putExtra("flags", flags?.build())
        launcher?.launch(intent)
    }

    /**
     * 预览并选择
     */
    fun startGallerySelectPreview(
        dataList: ArrayList<GalleryBean>,
        selectList: ArrayList<GalleryBean>,
        index: Int = 0,
        maxSize: Int = 0,
        previewResultCallback: GalleryPreviewCallback
    ) {
        resultCallBack = previewResultCallback
        val intent = Intent(context, GalleryPreviewActivity::class.java)
        intent.putExtra("dataList", dataList)
        intent.putExtra("index", index)
        intent.putExtra("maxSize", maxSize)
        intent.putExtra("type", GalleryPreviewActivity.TYPE_SELECT)
        intent.putExtra("selectList", selectList)
        launcher?.launch(intent)
    }

    /**
     * 仅预览
     */
    fun startGalleryPreview(
        dataList: ArrayList<GalleryBean>,
        index: Int = 0,
        isDelete: Boolean,
        previewResultCallback: GalleryPreviewCallback
    ) {
        resultCallBack = previewResultCallback
        val intent = Intent(context, GalleryPreviewActivity::class.java)
        intent.putExtra("dataList", dataList)
        intent.putExtra("index", index)
        intent.putExtra("type", GalleryPreviewActivity.TYPE_PREVIEW)
        intent.putExtra("isDelete", isDelete)
        launcher?.launch(intent)
    }

}


