package ru.sidorenkovivan.tea4uby.adapters;

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
import ru.sidorenkovivan.tea4uby.MainActivity;
import ru.sidorenkovivan.tea4uby.services.ImageLoader;
import ru.sidorenkovivan.tea4uby.util.Constants;

public class ViewPagerAdapter extends PagerAdapter {

    private final ArrayList<String> mUrls;
    private final Context mContext;
    private final BitmapDrawable mBitmapDrawable;
    private final Constants mConstants = new Constants();

    public ViewPagerAdapter(final Context pContext, final ArrayList<String> pUrls) {
        mContext = pContext;
        mBitmapDrawable = null;
        mUrls = pUrls;
    }

    public ViewPagerAdapter(final Context pContext, final BitmapDrawable pBitmapDrawable) {
        mContext = pContext;
        mBitmapDrawable = pBitmapDrawable;
        mUrls = new ArrayList<>();
    }

    @Override
    public boolean isViewFromObject(@NonNull final View pView, @NonNull final Object pObject) {
        return pView.equals(pObject);
    }

    @NotNull
    @Override
    public Object instantiateItem(@NonNull final ViewGroup pContainer, final int pPosition) {
        final PhotoView photoView = new PhotoView(mContext);
        final ImageLoader imageLoader = MainActivity.getImageLoader();
        photoView.setScaleType(PhotoView.ScaleType.CENTER_CROP);
        photoView.setAdjustViewBounds(true);

        if(mUrls.size() != 0) {
            imageLoader.loadAndShow(mUrls.get(pPosition), photoView);
        } else {
            photoView.setImageDrawable(mBitmapDrawable);
        }

        pContainer.addView(photoView, mConstants.INDEX);

        return photoView;
    }

    @Override
    public void destroyItem(@NonNull final ViewGroup pContainer, final int pPosition, @NonNull final Object pObject) {
        pContainer.removeView((ImageView) pObject);
    }

    @Override
    public int getCount() {
        return mUrls.size();
    }
}