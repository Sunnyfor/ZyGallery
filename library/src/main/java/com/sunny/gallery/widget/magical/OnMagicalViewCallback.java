package com.sunny.gallery.widget.magical;

public interface OnMagicalViewCallback {

    void onBeginBackMinAnim();

    void onBeginBackMinMagicalFinish(boolean isResetSize);

    void onBeginMagicalAnimComplete(MagicalView mojitoView, boolean showImmediately);

    void onBackgroundAlpha(float alpha);

    void onMagicalViewFinish();
}
