package ru.SidorenkovIvan.MyApplication.ui.ProductPage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rd.PageIndicatorView;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;
import ru.SidorenkovIvan.MyApplication.DBController;
import ru.SidorenkovIvan.MyApplication.ImageLoader;
import ru.SidorenkovIvan.MyApplication.MainActivity;
import ru.SidorenkovIvan.MyApplication.Product;
import ru.SidorenkovIvan.MyApplication.R;
import ru.SidorenkovIvan.MyApplication.ViewPagerAdapter;

public class ProductPage extends Fragment {

    private ViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    private PageIndicatorView mPageIndicatorView;
    private ImageLoader mImageLoader;
    private Product mProduct = new Product();

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.product_page_fragment, container, false);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Bundle bundle = getArguments();
        String id = (String) Objects.requireNonNull(bundle).get("productID");

        mImageLoader = MainActivity.getImageLoader();
        mProduct = DBController.getProduct(requireContext(), id);
        initViewPager(view);

        new Thread(() -> {
            if (connected()) {
                mViewPagerAdapter = new ViewPagerAdapter(getContext());
                mViewPager.post(() -> mViewPager.setAdapter(mViewPagerAdapter));

                mImageLoader.isLoad(mProduct.getImages(), mViewPagerAdapter, mPageIndicatorView);

                mPageIndicatorView.post(() -> mPageIndicatorView.setViewPager(mViewPager));
            } else {
                mViewPagerAdapter = new ViewPagerAdapter(getContext());
                mViewPager.post(() -> mViewPager.setAdapter(mViewPagerAdapter));

                mViewPagerAdapter.add(new BitmapDrawable(Resources.getSystem(), mProduct.getImage()));
                mPageIndicatorView.setCount(mViewPagerAdapter.getCount());

                mPageIndicatorView.post(() -> mPageIndicatorView.setViewPager(mViewPager));
                mPageIndicatorView.post(() -> mPageIndicatorView.setCount(1));
            }
        }).start();

        //TextView with title and code
        TextView textViewTitle = view.findViewById(R.id.textViewTitle);
        textViewTitle.post(() -> textViewTitle.setText(mProduct.getTitle() + "  (" + mProduct.getCode() + ")"));

        //TextView with description
        TextView textViewDescription = view.findViewById(R.id.textViewDescription);
        textViewDescription.setText(mProduct.getDescription());

        //Output of price and button to go to the site
        Button button = view.findViewById(R.id.buttonToSite);
        button.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mProduct.getProductUrl()))));

        TextView textViewPrice = view.findViewById(R.id.textViewPrice);
        String price = mProduct.getPrice();
        price = price.replace("{", "");
        price = price.replace("}", ":\n");
        price = price.replace("|", "\n");
        String TAG = "MyApp";
        Log.i(TAG, "Price is: " + price);
        textViewPrice.setText(price);

        return view;
    }

    private void initViewPager(final View pView) {
        mViewPager = pView.findViewById(R.id.viewPager);
        ViewTreeObserver viewTreeObserver = mViewPager.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                int viewPagerWidth = mViewPager.getWidth();
                float viewPagerHeight = (float) (viewPagerWidth);
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

    public boolean connected() {
        ConnectivityManager cm = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        return wifiInfo != null && wifiInfo.isConnected();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ViewModelProviders.of(this).get(ProductPageViewModel.class);
        // TODO: Use the ViewModel
    }
}