package ru.sidorenkovivan.myapplication.viewpager;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.github.chrisbanes.photoview.PhotoView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class ViewPagerAdapter extends PagerAdapter {

    private final ArrayList<BitmapDrawable> mBitmapDrawables;
    private final Context mContext;

    public ViewPagerAdapter(final Context pContext) {
        mContext = pContext;
        mBitmapDrawables = new ArrayList<>();
    }

    @Override
    public boolean isViewFromObject(@NonNull final View pView, @NonNull final Object pObject) {
        return pView.equals(pObject);
    }

    @NotNull
    @Override
    public Object instantiateItem(@NonNull final ViewGroup pContainer, final int pPosition) {
        final PhotoView imageView = new PhotoView(mContext);
        imageView.setScaleType(PhotoView.ScaleType.CENTER_CROP);
        imageView.setAdjustViewBounds(true);
        imageView.setImageBitmap(mBitmapDrawables.get(pPosition).getBitmap());
        pContainer.addView(imageView, 0);

        return imageView;
    }

    @Override
    public void destroyItem(@NonNull final ViewGroup pContainer, final int pPosition, @NonNull final Object pObject) {
        pContainer.removeView((ImageView) pObject);
    }

    @Override
    public int getCount() {
        return mBitmapDrawables.size();
    }

    public void add(final BitmapDrawable pBitmapDrawable) {
        mBitmapDrawables.add(pBitmapDrawable);
        notifyDataSetChanged();
    }
}