package com.sunny.gallery.select.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sunny.gallery.GalleryFlagBundle
import com.sunny.gallery.GalleryIntent
import com.sunny.gallery.R
import com.sunny.gallery.callback.GalleryPreviewCallback
import com.sunny.gallery.crop.CropActivity
import com.sunny.gallery.select.adapter.GalleryContentAdapter
import com.sunny.gallery.select.adapter.GalleryFolderAdapter
import com.sunny.gallery.select.bean.GalleryBean
import com.sunny.gallery.select.bean.GalleryFolderBean
import com.sunny.gallery.select.contract.GalleryContract
import com.sunny.kit.utils.ToastUtil
import com.sunny.zy.base.BaseActivity
import com.sunny.zy.utils.IntentUtil
import com.sunny.zy.utils.permission.PermissionResult
import com.sunny.zy.utils.permission.PermissionUtil

/**
 * Desc
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2021/9/22 17:12
 */
class GallerySelectActivity : BaseActivity(), GalleryContract.IView, GalleryPreviewCallback,
    PermissionResult {

    private val galleryResultList = arrayListOf<GalleryBean>()

    private val folderAdapter = GalleryFolderAdapter()

    private val contentAdapter = GalleryContentAdapter(galleryResultList)

    private var maxSize = 1

    private var aspectX = 0
    private var aspectY = 0

    private var fileType = GalleryFlagBundle.FILE_TYPE_ALL

    private var isCrop = false

    private val presenter: GalleryContract.Presenter by lazy {
        GalleryContract.Presenter(this)
    }

    private val permissionUtil by lazy {
        PermissionUtil(this)
    }

    companion object {
        const val MAX_SIZE_INT = "maxSize"
        const val IS_CROP_BOOLEAN = "isCrop"
        const val ASPECT_X_INT = "aspectX"
        const val ASPECT_Y_INT = "aspectY"
        const val FILE_TYPE_INT = "fileType"
        const val DATA = "data"

        fun getIntentUtil(activity: AppCompatActivity, flags: GalleryFlagBundle? = null): IntentUtil<List<GalleryBean>> {
            val intentUtil = IntentUtil<List<GalleryBean>>(activity)
            intentUtil.intent = Intent(activity, GallerySelectActivity::class.java).apply {
                flags?.let {
                    putExtra("flags", it.build())
                }
            }
            return intentUtil
        }
    }


    private val rvFolder by lazy {
        findViewById<RecyclerView>(R.id.rvFolder)
    }

    private val vBg by lazy {
        findViewById<View>(R.id.vBg)
    }

    private val rvContent by lazy {
        findViewById<RecyclerView>(R.id.rvContent)
    }

    private val galleryIntent by lazy {
        GalleryIntent()
    }

    private lateinit var cropLauncher: ActivityResultLauncher<Intent>

    override fun initLayout() = R.layout.zy_act_photo_select


    override fun initView() {
        //初始化标题栏
        toolbar.setTitleCustom(R.layout.zy_layout_photo_select_title)
        setImmersionResource(R.color.gallery_bg)
        toolbar.getView<ImageView>(R.id.ivBack)?.setOnClickListener(this)
        toolbar.getView<ConstraintLayout>(R.id.clTitle)?.setOnClickListener(this)
        val tvComplete = toolbar.getView<TextView>(R.id.tvComplete)
        tvComplete?.setOnClickListener(this)

        galleryIntent.init(this)

        intent.getBundleExtra("flags")?.let {
            fileType = it.getInt(FILE_TYPE_INT, GalleryFlagBundle.FILE_TYPE_ALL)
            isCrop = it.getBoolean(IS_CROP_BOOLEAN, false)
            aspectX = it.getInt(ASPECT_X_INT, 0)
            aspectY = it.getInt(ASPECT_Y_INT, 0)
            maxSize = it.getInt(MAX_SIZE_INT, 1)
        }

        if (isCrop) {
            maxSize = 1
            cropLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        it.data?.getParcelableExtra(DATA, GalleryBean::class.java)
                    } else {
                        it.data?.getParcelableExtra(DATA)
                    }
                    if (data != null) {
                        galleryResultList.add(data)
                        setResult()
                    } else {
                        ToastUtil.show(R.string.crop_error)
                    }
                }
                finish()
            }
        }

        if (maxSize > 1) {
            tvComplete?.visibility = View.VISIBLE
        } else {
            tvComplete?.visibility = View.GONE
        }

        contentAdapter.isMultiple = maxSize > 1

        rvFolder.layoutManager = LinearLayoutManager(this)
        folderAdapter.setOnItemClickListener { _, position ->
            val lastPosition = folderAdapter.selectIndex
            folderAdapter.selectIndex = position
            folderAdapter.notifyItemChanged(lastPosition)
            folderAdapter.notifyItemChanged(position)
            updateTitle(position)
            toggleGallery()
        }

        rvContent.itemAnimator = null
        rvContent.layoutManager = GridLayoutManager(this, 4)
        rvContent.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
            ) {
                val margin = resources.getDimension(com.sunny.zy.R.dimen.dp_1).toInt()
                val position = parent.getChildAdapterPosition(view)
                if (position % 4 != 0) {
                    outRect.left = margin
                }
                outRect.bottom = margin

            }
        })

        contentAdapter.selectCallback = { position ->
            val data = contentAdapter.getData(position)
            if (galleryResultList.contains(data)) {
                galleryResultList.remove(data)
                contentAdapter.notifyItemChanged(position)
                contentAdapter.getData().forEachIndexed { index, galleryContentBean ->
                    galleryResultList.forEach {
                        if (it == galleryContentBean) {
                            contentAdapter.notifyItemChanged(index)
                        }
                    }
                }
                updateCount()
            } else {
                if (galleryResultList.size < maxSize) {
                    galleryResultList.add(data)
                    updateCount()
                    contentAdapter.notifyItemChanged(position)
                } else {
                    ToastUtil.show(
                        String.format(
                            getString(
                                R.string.max_size_hint,
                                maxSize.toString()
                            )
                        )
                    )
                }
            }
        }

        contentAdapter.setOnItemClickListener { _, position ->

            val data = contentAdapter.getData(position)

            if (maxSize == 1) {
                if (data.type.contains("image") && isCrop) {
                    intentCrop(data)
                } else {
                    galleryResultList.add(data)
                    setResult()
                }
                return@setOnItemClickListener
            }

            val dataList = contentAdapter.getData()

            galleryIntent.startGallerySelectPreview(
                dataList,
                galleryResultList,
                position,
                maxSize,
                this
            )
        }
    }

    override fun loadData() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionUtil.requestPermissions(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO
            )
        } else {
            permissionUtil.requestPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
    }


    private fun updateTitle(position: Int) {
        toolbar.findViewById<TextView>(R.id.tvTitleGalleryName)?.text =
            folderAdapter.getData(position).name
        contentAdapter.getData().clear()
        contentAdapter.getData().addAll(folderAdapter.getData(position).list)
        rvContent.adapter = contentAdapter
    }

    private fun updateCount() {
        val completeText = toolbar.findViewById<TextView>(R.id.tvComplete)
        val textSb = StringBuilder()
        textSb.append(getString(R.string.complete))

        if (galleryResultList.isNotEmpty()) {
            completeText?.setBackgroundResource(R.drawable.sp_gallery_title_btn_bg_enable)
            completeText?.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.gallery_complete_text_ok
                )
            )
            textSb.append("(").append(galleryResultList.size).append("/").append(maxSize)
                .append(")")
        } else {
            completeText?.setBackgroundResource(R.drawable.sp_gallery_title_btn_bg_disenable)
            completeText?.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.gallery_complete_text_no
                )
            )
        }
        completeText?.text = textSb.toString()
    }


    private fun toggleGallery() {

        val expandAnim: Animation

        if (rvFolder.visibility == View.VISIBLE) {
            rvFolder.visibility = View.GONE
            expandAnim = AnimationUtils.loadAnimation(this, R.anim.gallery_folder_collect)
        } else {
            rvFolder.visibility = View.VISIBLE
            expandAnim = AnimationUtils.loadAnimation(this, R.anim.gallery_folder_expand)
        }

        toolbar.findViewById<View>(R.id.ivExpand)?.startAnimation(expandAnim)

        if (rvFolder.visibility == View.GONE) {
            rvFolder.animation =
                AnimationUtils.loadAnimation(this, R.anim.gallery_folder_out).apply {
                    setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationStart(animation: Animation?) {}
                        override fun onAnimationEnd(animation: Animation?) {
                            vBg.visibility = View.GONE
                        }

                        override fun onAnimationRepeat(animation: Animation?) {}

                    })
                }
        } else {
            rvFolder.animation =
                AnimationUtils.loadAnimation(this, R.anim.gallery_folder_in).apply {
                    setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationStart(animation: Animation?) {
                            vBg.visibility = View.VISIBLE
                        }

                        override fun onAnimationEnd(animation: Animation?) {}
                        override fun onAnimationRepeat(animation: Animation?) {}
                    })
                }
        }
    }


    override fun onClickEvent(view: View) {
        when (view.id) {
            R.id.ivBack -> finish()

            R.id.clTitle -> {
                toggleGallery()
            }

            R.id.tvComplete -> {
                if (galleryResultList.isEmpty()) {
                    return
                }
                setResult()
            }
        }
    }

    private fun setResult() {
        val resultIntent = Intent()
        resultIntent.putParcelableArrayListExtra(DATA, galleryResultList)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    override fun onClose() {}

    override fun showGalleryData(data: List<GalleryFolderBean>) {
        folderAdapter.getData().clear()
        folderAdapter.getData().addAll(data)
        rvFolder.adapter = folderAdapter

        if (folderAdapter.getData().isNotEmpty()) {
            updateTitle(0)
        }
    }

    private fun intentCrop(data: GalleryBean) {
        val intent = Intent(this, CropActivity::class.java)
        intent.putExtra(DATA, data)
        intent.putExtra(ASPECT_X_INT, aspectX)
        intent.putExtra(ASPECT_Y_INT, aspectY)
        cropLauncher.launch(intent)
    }


    override fun onPermissionFailed(failedPermissions: List<String>) {}

    override fun onPermissionSuccess(successPermissions: List<String>) {
        when (fileType) {
            GalleryFlagBundle.FILE_TYPE_ALL -> presenter.loadImageAndVideData()
            GalleryFlagBundle.File_TYPE_IMAGE -> presenter.loadImageData()
            GalleryFlagBundle.File_TYPE_VIDEO -> presenter.loadVideoData()
        }
    }

    /**
     * 预览结果处理
     * @param flag 是否处理结果
     */
    override fun onResult(flag: Boolean, resultList: ArrayList<GalleryBean>) {
        galleryResultList.clear()
        galleryResultList.addAll(resultList)

        if (flag) {
            setResult()
        } else {
            contentAdapter.notifyItemRangeChanged(0, contentAdapter.itemCount)
            updateCount()
        }
    }
}