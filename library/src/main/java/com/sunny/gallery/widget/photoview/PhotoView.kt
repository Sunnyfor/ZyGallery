package com.sunny.gallery.widget.photoview

import android.content.Context
import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.view.GestureDetector
import androidx.appcompat.widget.AppCompatImageView

/**
 * A zoomable ImageView. See [PhotoViewAttache] for most of the details on how the zooming
 * is accomplished
 */
class PhotoView : AppCompatImageView {
    /**
     * Get the current [PhotoViewAttache] for this view. Be wary of holding on to references
     * to this attacher, as it has a reference to this view, which, if a reference is held in the
     * wrong place, can cause memory leaks.
     *
     * @return the attacher.
     */
    private var attache: PhotoViewAttache = PhotoViewAttache(this)

    private var pendingScaleType: ScaleType? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    )


    init {
        //We always pose as a Matrix scale type, though we can change to another scale type
        //via the attache
        super.setScaleType(ScaleType.MATRIX)
        //apply the previously applied scale type
        pendingScaleType?.let {
            scaleType = it
            pendingScaleType = null
        }
    }

    override fun getScaleType(): ScaleType {
        return attache.scaleType
    }

    override fun getImageMatrix(): Matrix {
        return attache.imageMatrix
    }

    override fun setOnLongClickListener(l: OnLongClickListener?) {
        attache.setOnLongClickListener(l)
    }

    override fun setOnClickListener(l: OnClickListener?) {
        attache.setOnClickListener(l)
    }

    override fun setScaleType(scaleType: ScaleType) {
        attache.scaleType = scaleType
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        // setImageBitmap calls through to this method

        attache.update()

    }

    override fun setImageResource(resId: Int) {
        super.setImageResource(resId)

        attache.update()

    }

    override fun setImageURI(uri: Uri?) {
        super.setImageURI(uri)
        attache.update()
    }

    override fun setFrame(l: Int, t: Int, r: Int, b: Int): Boolean {
        val changed = super.setFrame(l, t, r, b)
        if (changed) {
            attache.update()
        }
        return changed
    }

    fun setRotationTo(rotationDegree: Float) {
        attache.setRotationTo(rotationDegree)
    }

    fun setRotationBy(rotationDegree: Float) {
        attache.setRotationBy(rotationDegree)
    }

    var isZoomable: Boolean
        get() = attache.isZoomable
        set(zoomable) {
            attache.isZoomable = zoomable
        }

    val displayRect: RectF?
        get() = attache.displayRect

    fun getDisplayMatrix(matrix: Matrix) {
        attache.getDisplayMatrix(matrix)
    }

    fun setDisplayMatrix(finalRectangle: Matrix): Boolean {
        return attache.setDisplayMatrix(finalRectangle)
    }

    fun getSuppMatrix(matrix: Matrix) {
        attache.getSuppMatrix(matrix)
    }

    fun setSuppMatrix(matrix: Matrix?): Boolean {
        return attache.setDisplayMatrix(matrix)
    }

    var minimumScale: Float
        get() = attache.minimumScale
        set(minimumScale) {
            attache.minimumScale = minimumScale
        }
    var mediumScale: Float
        get() = attache.mediumScale
        set(mediumScale) {
            attache.mediumScale = mediumScale
        }
    var maximumScale: Float
        get() = attache.maximumScale
        set(maximumScale) {
            attache.maximumScale = maximumScale
        }
    var scale: Float
        get() = attache.scale
        set(scale) {
            attache.scale = scale
        }

    fun setAllowParentInterceptOnEdge(allow: Boolean) {
        attache.setAllowParentInterceptOnEdge(allow)
    }

    fun setScaleLevels(minimumScale: Float, mediumScale: Float, maximumScale: Float) {
        attache.setScaleLevels(minimumScale, mediumScale, maximumScale)
    }

    fun setOnMatrixChangeListener(listener: OnMatrixChangedListener?) {
        attache.setOnMatrixChangeListener(listener)
    }

    fun setOnPhotoTapListener(listener: OnPhotoTapListener?) {
        attache.setOnPhotoTapListener(listener)
    }

    fun setOnOutsidePhotoTapListener(listener: OnOutsidePhotoTapListener?) {
        attache.setOnOutsidePhotoTapListener(listener)
    }

    fun setOnViewTapListener(listener: OnViewTapListener?) {
        attache.setOnViewTapListener(listener)
    }

    fun setOnViewDragListener(listener: OnViewDragListener?) {
        attache.setOnViewDragListener(listener)
    }

    fun setScale(scale: Float, animate: Boolean) {
        attache.setScale(scale, animate)
    }

    fun setScale(scale: Float, focalX: Float, focalY: Float, animate: Boolean) {
        attache.setScale(scale, focalX, focalY, animate)
    }

    fun setZoomTransitionDuration(milliseconds: Int) {
        attache.setZoomTransitionDuration(milliseconds)
    }

    fun setOnDoubleTapListener(onDoubleTapListener: GestureDetector.OnDoubleTapListener?) {
        attache.setOnDoubleTapListener(onDoubleTapListener)
    }

    fun setOnScaleChangeListener(onScaleChangedListener: OnScaleChangedListener?) {
        attache.setOnScaleChangeListener(onScaleChangedListener)
    }

    fun setOnSingleFlingListener(onSingleFlingListener: OnSingleFlingListener?) {
        attache.setOnSingleFlingListener(onSingleFlingListener)
    }
}