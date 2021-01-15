package ru.sidorenkovivan.myapplication.ui.productpage;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rd.PageIndicatorView;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;
import ru.sidorenkovivan.myapplication.BuildConfig;
import ru.sidorenkovivan.myapplication.util.database.DBController;
import ru.sidorenkovivan.myapplication.util.ImageLoader;
import ru.sidorenkovivan.myapplication.MainActivity;
import ru.sidorenkovivan.myapplication.entities.Product;
import ru.sidorenkovivan.myapplication.R;
import ru.sidorenkovivan.myapplication.viewpager.ViewPagerAdapter;
import ru.sidorenkovivan.myapplication.servicelocator.ConnectionService;
import ru.sidorenkovivan.myapplication.servicelocator.ServiceLocator;

public class ProductPage extends Fragment {

    private ViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    private PageIndicatorView mPageIndicatorView;
    private ImageLoader mImageLoader;
    private Product mProduct = new Product();
    private final ServiceLocator mServiceLocator = new ServiceLocator();
    private final DBController mDbController = new DBController();

    @Override
    public View onCreateView(@NonNull final LayoutInflater pInflater,
                             @Nullable final ViewGroup pContainer,
                             @Nullable final Bundle pSavedInstanceState) {
        final View view = pInflater.inflate(R.layout.fragment_product_page, pContainer, false);

        final StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        final ConnectionService wifiService = mServiceLocator.getService("WifiService");
        final boolean isWifiConnected = wifiService.isConnected(getContext());
        final ConnectionService lteService = mServiceLocator.getService("LteService");
        final boolean isLteConnected = lteService.isConnected(getContext());

        final Bundle bundle = getArguments();
        final String id = (String) Objects.requireNonNull(bundle).get("productID");
        final String dbPath = getContext().getApplicationInfo().dataDir + getString(R.string.databaseName);

        mImageLoader = MainActivity.getImageLoader();
        mProduct = mDbController.getProduct(dbPath, id);
        initViewPager(view);

        final Thread thread = new Thread(() -> {
            if (isLteConnected || isWifiConnected) {
                mViewPagerAdapter = new ViewPagerAdapter(getContext());
                mViewPager.post(() -> mViewPager.setAdapter(mViewPagerAdapter));

                mImageLoader.loadAndShow(mProduct.getImages(), mViewPagerAdapter, mPageIndicatorView);

                mPageIndicatorView.post(() -> mPageIndicatorView.setViewPager(mViewPager));
            } else {
                mViewPagerAdapter = new ViewPagerAdapter(getContext());
                mViewPager.post(() -> mViewPager.setAdapter(mViewPagerAdapter));

                mViewPagerAdapter.add(new BitmapDrawable(Resources.getSystem(), mProduct.getImage()));
                mPageIndicatorView.setCount(mViewPagerAdapter.getCount());

                mPageIndicatorView.post(() -> mPageIndicatorView.setViewPager(mViewPager));
                mPageIndicatorView.post(() -> mPageIndicatorView.setCount(1));
            }

            Thread.currentThread().interrupt();
            if (BuildConfig.DEBUG) {
                Log.i("Product Thread", "is interrupted:" + Thread.currentThread().isInterrupted());
            }
        });
        thread.start();

        //TextView with title and code
        final TextView textViewTitle = view.findViewById(R.id.textViewTitle);
        textViewTitle.post(() -> textViewTitle.setText(getString(R.string.genericTextViewTitle, mProduct.getTitle(), mProduct.getCode())));

        //TextView with description
        final TextView textViewDescription = view.findViewById(R.id.textViewDescription);
        textViewDescription.setText(mProduct.getDescription());

        //Output of price and button to go to the site
        final Button button = view.findViewById(R.id.buttonToSite);
        button.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mProduct.getProductUrl()))));

        final TextView textViewPrice = view.findViewById(R.id.textViewPrice);
        String price = mProduct.getPrice();
        price = price.replace("{", "");
        price = price.replace("}", ":\n");
        price = price.replace("|", "\n");
        textViewPrice.setText(price);
        if (BuildConfig.DEBUG) {
            Log.i("MyApp", "Price is: " + price);
        }

        return view;
    }

    private void initViewPager(final View pView) {
        mViewPager = pView.findViewById(R.id.viewPager);
        final ViewTreeObserver viewTreeObserver = mViewPager.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                final int viewPagerWidth = mViewPager.getWidth();
                final float viewPagerHeight = (float) (viewPagerWidth);
                layoutParams.width = viewPagerWidth;
                layoutParams.height = (int) viewPagerHeight;
                mViewPager.setLayoutParams(layoutParams);
                mViewPager.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });

        mPageIndicatorView = pView.findViewById(R.id.viewPagerIndicator);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mPageIndicatorView.setSelection(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ViewModelProviders.of(this).get(ProductPageViewModel.class);
        // TODO: Use the ViewModel
    }
}