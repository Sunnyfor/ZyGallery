package com.sunny.gallery.widget.magical;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Build;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.RequiresApi;
import androidx.viewpager2.widget.ViewPager2;

import com.sunny.kit.utils.DensityUtil;

public class MagicalView extends FrameLayout {

    private float mAlpha = 0.0F;
    private final long animationDuration = 250;
    private int mOriginLeft;
    private int mOriginTop;
    private int mOriginHeight;
    private int mOriginWidth;

    private int screenWidth;
    private int screenHeight;
    private int targetImageTop;
    private int targetImageWidth;
    private int targetImageHeight;
    private int targetEndLeft;

    private int realWidth;
    private int realHeight;
    private boolean isAnimating = false;

    private final FrameLayout contentLayout;
    private final View backgroundView;
    private final MagicalViewWrapper magicalWrapper;

    public MagicalView(Context context) {
        this(context, null);
    }

    public MagicalView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MagicalView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getScreenSize();
        backgroundView = new View(context);
        backgroundView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        backgroundView.setAlpha(mAlpha);
        addView(backgroundView);

        contentLayout = new FrameLayout(context);
        contentLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addView(contentLayout);

        magicalWrapper = new MagicalViewWrapper(contentLayout);
    }


    public void setBackgroundColor(int color) {
        backgroundView.setBackgroundColor(color);
    }

    public void startNormal(int realWidth, int realHeight, boolean showImmediately) {
        this.realWidth = realWidth;
        this.realHeight = realHeight;
        mOriginLeft = 0;
        mOriginTop = 0;
        mOriginWidth = 0;
        mOriginHeight = 0;

        setVisibility(View.VISIBLE);
        setOriginParams();
        showNormalMin(targetImageTop, targetEndLeft, targetImageWidth, targetImageHeight);

        if (showImmediately) {
            mAlpha = 1f;
            backgroundView.setAlpha(mAlpha);
        } else {
            mAlpha = 0f;
            backgroundView.setAlpha(mAlpha);
            contentLayout.setAlpha(0f);
            contentLayout.animate().alpha(1f).setDuration(animationDuration).start();
            backgroundView.animate().alpha(1f).setDuration(animationDuration).start();
        }
        setShowEndParams();
    }

    public void start(boolean showImmediately) {
        mAlpha = showImmediately ? mAlpha = 1f : 0f;
        backgroundView.setAlpha(mAlpha);
        setVisibility(View.VISIBLE);
        setOriginParams();
        beginShow(showImmediately);
    }

    public void resetStart() {
        getScreenSize();
        start(true);
    }


    private void getScreenSize() {
        screenWidth = DensityUtil.INSTANCE.screenWidth();
        screenHeight = DensityUtil.INSTANCE.screenHeight();
    }


    public void resetStartNormal(int realWidth, int realHeight, boolean showImmediately) {
        getScreenSize();
        startNormal(realWidth, realHeight, showImmediately);
    }

    public void setViewParams(int left, int top, int originWidth, int originHeight, int realWidth, int realHeight) {
        this.realWidth = realWidth;
        this.realHeight = realHeight;

        mOriginLeft = left;
        mOriginTop = top;
        mOriginWidth = originWidth;
        mOriginHeight = originHeight;
    }

    private void setOriginParams() {
        int[] locationImage = new int[2];
        contentLayout.getLocationOnScreen(locationImage);
        targetEndLeft = 0;
        if (screenWidth / (float) screenHeight < realWidth / (float) realHeight) {
            targetImageWidth = screenWidth;
            targetImageHeight = (int) (targetImageWidth * (realHeight / (float) realWidth));
            targetImageTop = (screenHeight - targetImageHeight) / 2;
        } else {
            targetImageHeight = screenHeight;
            targetImageWidth = (int) (targetImageHeight * (realWidth / (float) realHeight));
            targetImageTop = 0;
            targetEndLeft = (screenWidth - targetImageWidth) / 2;
        }

        magicalWrapper.setWidth(mOriginWidth);
        magicalWrapper.setHeight(mOriginHeight);
        magicalWrapper.setMarginLeft(mOriginLeft);
        magicalWrapper.setMarginTop(mOriginTop);
    }

    private void beginShow(final boolean showImmediately) {
        if (showImmediately) {
            mAlpha = 1f;
            backgroundView.setAlpha(mAlpha);
            showNormalMin(targetImageTop, targetEndLeft, targetImageWidth, targetImageHeight);
            setShowEndParams();
        } else {
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (float) animation.getAnimatedValue();
                    showNormalMin(value, mOriginTop, targetImageTop, mOriginLeft, targetEndLeft,
                            mOriginWidth, targetImageWidth, mOriginHeight, targetImageHeight);
                }
            });
            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    setShowEndParams();
                }
            });
            valueAnimator.setDuration(animationDuration).start();
            changeBackgroundViewAlpha(false);
        }
    }

    private void setShowEndParams() {
        isAnimating = false;
        changeContentViewToFullscreen();
        if (onMagicalViewCallback != null) {
            onMagicalViewCallback.onBeginMagicalAnimComplete(MagicalView.this, false);
        }
    }

    private void showNormalMin(float animRatio, float startY, float endY, float startLeft, float endLeft,
                               float startWidth, float endWidth, float startHeight, float endHeight) {
        showNormalMin(false, animRatio, startY, endY, startLeft, endLeft, startWidth, endWidth, startHeight, endHeight);
    }

    private void showNormalMin(float endY, float endLeft, float endWidth, float endHeight) {
        showNormalMin(true, 0, 0, endY, 0, endLeft, 0, endWidth, 0, endHeight);
    }

    private void showNormalMin(boolean showImmediately, float animRatio, float startY, float endY, float startLeft, float endLeft,
                               float startWidth, float endWidth, float startHeight, float endHeight) {
        if (showImmediately) {
            magicalWrapper.setWidth(endWidth);
            magicalWrapper.setHeight(endHeight);
            magicalWrapper.setMarginLeft((int) (endLeft));
            magicalWrapper.setMarginTop((int) endY);
        } else {
            float xOffset = animRatio * (endLeft - startLeft);
            float widthOffset = animRatio * (endWidth - startWidth);
            float heightOffset = animRatio * (endHeight - startHeight);
            float topOffset = animRatio * (endY - startY);
            magicalWrapper.setWidth(startWidth + widthOffset);
            magicalWrapper.setHeight(startHeight + heightOffset);
            magicalWrapper.setMarginLeft((int) (startLeft + xOffset));
            magicalWrapper.setMarginTop((int) (startY + topOffset));
        }
    }

    public void backToMin() {
        if (isAnimating) {
            return;
        }
        if (mOriginWidth == 0 || mOriginHeight == 0) {
            backToMinWithoutView();
            return;
        }
        if (onMagicalViewCallback != null) {
            onMagicalViewCallback.onBeginBackMinAnim();
        }
        beginBackToMin(false);
        backToMinWithTransition();
    }


    private void backToMinWithTransition() {
        contentLayout.post(() -> {
            TransitionManager.beginDelayedTransition((ViewGroup) contentLayout.getParent(), new TransitionSet()
                    .setDuration(animationDuration)
                    .addTransition(new ChangeBounds())
                    .addTransition(new ChangeTransform())
                    .addTransition(new ChangeImageTransform())

            );
            beginBackToMin(true);
            contentLayout.setTranslationX(0);
            contentLayout.setTranslationY(0);
            magicalWrapper.setWidth(mOriginWidth);
            magicalWrapper.setHeight(mOriginHeight);
            magicalWrapper.setMarginTop(mOriginTop);
            magicalWrapper.setMarginLeft(mOriginLeft);

            changeBackgroundViewAlpha(true);
        });
    }


    private void beginBackToMin(boolean isResetSize) {
        if (isResetSize) {
            onMagicalViewCallback.onBeginBackMinMagicalFinish(true);
        }
    }

    private void backToMinWithoutView() {
        contentLayout.animate().alpha(0f).setDuration(animationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (onMagicalViewCallback != null) {
                            onMagicalViewCallback.onMagicalViewFinish();
                        }
                    }
                }).start();
        backgroundView.animate().alpha(0f).setDuration(animationDuration).start();
    }

    /**
     * @param isAlpha ????????????
     */
    private void changeBackgroundViewAlpha(final boolean isAlpha) {
        final float end = isAlpha ? 0 : 1f;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(mAlpha, end);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                isAnimating = true;
                mAlpha = (Float) animation.getAnimatedValue();
                backgroundView.setAlpha(mAlpha);
                if (onMagicalViewCallback != null) {
                    onMagicalViewCallback.onBackgroundAlpha(mAlpha);
                }
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimating = false;
                if (isAlpha) {
                    if (onMagicalViewCallback != null) {
                        onMagicalViewCallback.onMagicalViewFinish();
                    }
                }
            }
        });
        valueAnimator.setDuration(animationDuration);
        valueAnimator.start();
    }

    public void setMagicalContent(View view) {
        contentLayout.addView(view);
    }

    private void changeContentViewToFullscreen() {
        targetImageHeight = screenHeight;
        targetImageWidth = screenWidth;
        targetImageTop = 0;
        magicalWrapper.setHeight(screenHeight);
        magicalWrapper.setWidth(screenWidth);
        magicalWrapper.setMarginTop(0);
        magicalWrapper.setMarginLeft(0);
    }

    public void setBackgroundAlpha(float mAlpha) {
        this.mAlpha = mAlpha;
        backgroundView.setAlpha(mAlpha);
    }

    private int startX, startY;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        View childView = contentLayout.getChildAt(0);
        ViewPager2 viewPager2 = null;
        if (childView instanceof ViewPager2) {
            // ??????MagicalView????????????ViewPage2 ?????????????????????????????????????????????????????????????????????????????????????????????????????????
            viewPager2 = (ViewPager2) childView;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = (int) event.getX();
                startY = (int) event.getY();
                if (viewPager2 != null) {
                    viewPager2.setUserInputEnabled(true);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                int endX = (int) event.getX();
                int endY = (int) event.getY();
                int disX = Math.abs(endX - startX);
                int disY = Math.abs(endY - startY);
                if (disX > disY) {
                    if (viewPager2 != null) {
                        viewPager2.setUserInputEnabled(true);
                    }
                } else {
                    if (viewPager2 != null) {
                        viewPager2.setUserInputEnabled(canScrollVertically(startY - endY));
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (viewPager2 != null) {
                    viewPager2.setUserInputEnabled(true);
                }
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    private OnMagicalViewCallback onMagicalViewCallback;

    public void setOnMojitoViewCallback(OnMagicalViewCallback onMagicalViewCallback) {
        this.onMagicalViewCallback = onMagicalViewCallback;
    }
}
