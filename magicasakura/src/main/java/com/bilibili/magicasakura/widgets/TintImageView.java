package com.bilibili.magicasakura.widgets;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.bilibili.magicasakura.utils.TintManager;

import androidx.appcompat.widget.AppCompatImageView;

/**
 * @author xyczero617@gmail.com
 * @time 15/11/8
 */
public class TintImageView extends AppCompatImageView implements Tintable,
        AppCompatBackgroundHelper.BackgroundExtensible,
        AppCompatImageHelper.ImageExtensible {

    private AppCompatBackgroundHelper mBackgroundHelper;
    private AppCompatImageHelper mImageHelper;

    public TintImageView(Context context) {
        this(context, null);
    }

    public TintImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TintImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (isInEditMode()) {
            return;
        }
        final TintManager tintManager = TintManager.get(context);

        mBackgroundHelper = new AppCompatBackgroundHelper(this, tintManager);
        mBackgroundHelper.loadFromAttribute(attrs, defStyleAttr);

        mImageHelper = new AppCompatImageHelper(this, tintManager);
        mImageHelper.loadFromAttribute(attrs, defStyleAttr);
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (getBackground() != null) {
            invalidateDrawable(getBackground());
        }
    }

    @Override
    public void setBackground(Drawable background) {
        super.setBackground(background);
        if (mBackgroundHelper != null) {
            mBackgroundHelper.setBackgroundDrawableExternal(background);
        }
    }

    @Override
    public void setBackgroundResource(int resId) {
        if (mBackgroundHelper != null) {
            mBackgroundHelper.setBackgroundResId(resId);
        } else {
            super.setBackgroundResource(resId);
        }
    }

    @Override
    public void setBackgroundColor(int color) {
        super.setBackgroundColor(color);
        if (mBackgroundHelper != null) {
            mBackgroundHelper.setBackgroundColor(color);
        }
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        if (mImageHelper != null) {
            mImageHelper.setImageDrawable();
        }
    }

    @Override
    public void setImageResource(int resId) {
        if (mImageHelper != null) {
            mImageHelper.setImageResId(resId);
        } else {
            super.setImageResource(resId);
        }
    }

    @Override
    public void setBackgroundTintList(int resId) {
        if (mBackgroundHelper != null) {
            mBackgroundHelper.setBackgroundTintList(resId, null);
        }
    }

    @Override
    public void setBackgroundTintList(int resId, PorterDuff.Mode mode) {
        if (mBackgroundHelper != null) {
            mBackgroundHelper.setBackgroundTintList(resId, mode);
        }
    }

    @Override
    public void setImageTintList(int resId) {
        if (mImageHelper != null) {
            mImageHelper.setImageTintList(resId, null);
        }
    }

    @Override
    public void setImageTintList(int resId, PorterDuff.Mode mode) {
        if (mImageHelper != null) {
            mImageHelper.setImageTintList(resId, mode);
        }
    }

    @Override
    public void tint() {
        if (mBackgroundHelper != null) {
            mBackgroundHelper.tint();
        }
        if (mImageHelper != null) {
            mImageHelper.tint();
        }
    }
}
