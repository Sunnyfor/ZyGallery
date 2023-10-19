package com.sunny.gallery.crop

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.sunny.gallery.R
import com.sunny.gallery.crop.view.CropView
import com.sunny.gallery.crop.view.HorizontalProgressWheelView
import com.sunny.gallery.crop.view.TransformImageView
import com.sunny.gallery.select.bean.GalleryBean
import com.sunny.gallery.select.view.GallerySelectActivity
import com.sunny.kit.utils.FileUtil
import com.sunny.zy.base.BaseActivity
import com.sunny.zy.base.BasePresenter
import com.sunny.zy.base.IBaseView
import com.sunny.zy.base.bean.MenuBean
import com.sunny.zy.base.bean.PlaceholderBean
import kotlinx.coroutines.launch
import java.io.File
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale


class CropActivity : BaseActivity(), TransformImageView.TransformImageListener {

    private val cropView by lazy {
        findViewById<CropView>(R.id.cropView)
    }

    private val flReset by lazy {
        findViewById<View>(R.id.flReset)
    }

    private val flRotate by lazy {
        findViewById<View>(R.id.flRotate)
    }

    private val hpv by lazy {
        findViewById<HorizontalProgressWheelView>(R.id.hpv)
    }

    private val tvRotate by lazy {
        findViewById<TextView>(R.id.tvRotate)
    }

    private val presenter by lazy {
        object : BasePresenter<IBaseView>(this) {}
    }

    private val data: GalleryBean? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(GallerySelectActivity.DATA, GalleryBean::class.java)
        } else {
            intent.getParcelableExtra(GallerySelectActivity.DATA)
        }
    }

    private val aspectX by lazy {
        intent.getIntExtra(GallerySelectActivity.ASPECT_X_INT, 0)
    }

    private val aspectY by lazy {
        intent.getIntExtra(GallerySelectActivity.ASPECT_Y_INT, 0)
    }

    override fun initLayout() = R.layout.zy_act_crop

    override fun initView() {
        val uri = data?.uri
        if (uri == null) {
            toolbar.setTitleDefault("裁剪")
            setImmersionResource(R.color.gallery_bg)
            showError(PlaceholderBean().setEmptyData(getString(R.string.load_error)))
            return
        }

        val textButton = TextView(this).apply {
            text = "完成"
            height = resources.getDimension(com.sunny.zy.R.dimen.dp_30).toInt()
            gravity = Gravity.CENTER
            setTextColor(ContextCompat.getColor(context, com.sunny.zy.R.color.textColorPrimary))


            setBackgroundResource(R.drawable.sp_gallery_title_btn_bg_enable)
        }
        val padding = resources.getDimension(com.sunny.zy.R.dimen.dp_14).toInt()
        textButton.setPadding(padding, 0, padding, 0)

        val menuBean = MenuBean(textButton) {
            showLoading()
            presenter.launch {
                val resultFile = cropView.mGestureCropImageView.saveImage()
                hideLoading()
                val intent = Intent()
                intent.putExtra(GallerySelectActivity.DATA, data?.apply {
                    this.id = 0
                    this.uri = FileUtil.getUriFromPath(resultFile.path)
                    this.type = "image/jpeg"
                    this.duration = 0
                    this.size = resultFile.length()
                })
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
        toolbar.setTitleDefault("裁剪", menuBean)
        setImmersionResource(R.color.gallery_bg)

        settingView()

        val inputStream = contentResolver.openInputStream(uri)
        val fileName = data?.name ?: "${System.currentTimeMillis()}.temp"
        val privateFile = File(FileUtil.getExternalDir(), fileName)
        if (privateFile.exists()) {
            privateFile.delete()
        }
        inputStream?.let {
            privateFile.outputStream().write(it.readBytes())
            it.close()
        }
        cropView.mGestureCropImageView.initImageFile(privateFile)
        cropView.mGestureCropImageView.setTransformImageListener(this)

        setOnClickListener(flReset, flRotate)
    }

    override fun loadData() {

    }

    override fun onClickEvent(view: View) {
        when (view.id) {
            R.id.flReset -> {
                with(cropView) {
                    mGestureCropImageView.postRotate(-mGestureCropImageView.currentAngle)
                    mGestureCropImageView.setImageToWrapCropBounds()
                }

            }

            R.id.flRotate -> {
                rotateByAngle()
            }
        }
    }

    private fun settingView() {
        with(cropView.mGestureCropImageView) {
            isRotateEnabled = false
            targetAspectRatio = 1F
            if (aspectX > 0 && aspectY > 0) {
                targetAspectRatio = (aspectX.toFloat() / aspectY.toFloat())
            }
        }

        with(cropView.mViewOverlay) {
            setShowCropFrame(true)
            setCropFrameStrokeWidth(resources.getDimensionPixelSize(com.sunny.zy.R.dimen.dp_2))
            setCropFrameColor(ContextCompat.getColor(context, R.color.gallery_complete_text_ok))
            setDimmedColor(Color.parseColor("#8c000000"))
        }

        hpv.setScrollingListener(object : HorizontalProgressWheelView.ScrollingListener {
            override fun onScrollStart() {
                cropView.mGestureCropImageView.cancelAllAnimations()
            }

            override fun onScroll(delta: Float, totalDistance: Float) {
                val value = (delta / 42)
                cropView.mGestureCropImageView.postRotate(value)
            }

            override fun onScrollEnd() {
                cropView.mGestureCropImageView.setImageToWrapCropBounds()
            }

        })
    }

    private fun rotateByAngle() {
        with(cropView) {
            mGestureCropImageView.postRotate(90F)
            mGestureCropImageView.setImageToWrapCropBounds()
        }
    }

    override fun onLoadComplete() {
        cropView.animate().alpha(1f).setDuration(300).interpolator = AccelerateInterpolator()
    }

    override fun onLoadFailure(e: Exception) {}


    override fun onRotate(currentAngle: Float) {
        var value = currentAngle
        if (value == 360f) {
            value = 0f
        }
        val angle = formatWithoutRounding(value)
        tvRotate.text = angle
    }

    override fun onScale(currentScale: Float) {}


    private fun formatWithoutRounding(value: Float): String {
        val customSymbols = DecimalFormatSymbols(Locale.getDefault())
        customSymbols.decimalSeparator = '.'
        val decimalFormat = DecimalFormat("0.0", customSymbols)
        decimalFormat.roundingMode = RoundingMode.DOWN // 截断小数部分
        return decimalFormat.format(value) + "°"
    }

    override fun onClose() {

    }
}