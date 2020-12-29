package ru.SidorenkovIvan.MyApplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.github.chrisbanes.photoview.PhotoView;
import java.util.ArrayList;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class ViewPagerAdapter extends PagerAdapter {

    private final Context mContext;
    private final ArrayList<BitmapDrawable> mBitmapDrawables;

    public ViewPagerAdapter(final Context pContext) {
        mContext = pContext;
        mBitmapDrawables = new ArrayList<>();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @SuppressLint("ClickableViewAccessibility")
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        PhotoView imageView = new PhotoView(mContext);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setAdjustViewBounds(true);
        imageView.setImageBitmap(mBitmapDrawables.get(position).getBitmap());
        container.addView(imageView, 0);
        return imageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ImageView) object);
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