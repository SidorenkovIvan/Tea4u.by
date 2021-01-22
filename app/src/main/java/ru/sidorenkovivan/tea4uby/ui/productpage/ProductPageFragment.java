package ru.sidorenkovivan.tea4uby.ui.productpage;

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import ru.sidorenkovivan.tea4uby.BuildConfig;
import ru.sidorenkovivan.tea4uby.R;
import ru.sidorenkovivan.tea4uby.adapters.ViewPagerAdapter;
import ru.sidorenkovivan.tea4uby.entities.Product;
import ru.sidorenkovivan.tea4uby.services.LteService;
import ru.sidorenkovivan.tea4uby.services.WifiService;
import ru.sidorenkovivan.tea4uby.util.Constants;
import ru.sidorenkovivan.tea4uby.util.database.DBController;

public class ProductPageFragment extends Fragment {

    private ViewPager mViewPager;
    private PageIndicatorView mPageIndicatorView;
    private Product mProduct = new Product();
    private final LteService mLteService = new LteService();
    private final WifiService mWifiService = new WifiService();
    private final DBController mDbController = new DBController();
    private final Constants mConstants = new Constants();
    private TextView mTextViewTitle;
    private TextView mTextViewDescription;
    private Button mButtonWebsite;
    private TextView mTextViewPrice;

    @Override
    public View onCreateView(final LayoutInflater pInflater,
                             final ViewGroup pContainer,
                             final Bundle pSavedInstanceState) {
        final View view = pInflater.inflate(R.layout.fragment_product_page, pContainer, false);
        final StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        final boolean isWifiConnected = mWifiService.isConnected(requireContext());
        final boolean isLteConnected = mLteService.isConnected(requireContext());
        final Bundle bundle = getArguments();
        final String id = (String) Objects.requireNonNull(bundle).get(mConstants.PRODUCT_ID);
        final String dbPath = requireContext().getApplicationInfo().dataDir + getString(R.string.databaseName);
        final ViewPagerAdapter viewPagerAdapter;
        mProduct = mDbController.getProduct(dbPath, id);
        initViewPager(view);
        initViews(view);

        if (isLteConnected || isWifiConnected) {
            viewPagerAdapter = new ViewPagerAdapter(getContext(), getImagesUrls(mProduct.getImages()));
        } else {
            viewPagerAdapter = new ViewPagerAdapter(getContext(), new BitmapDrawable(Resources.getSystem(), mProduct.getImage()));
        }

        mViewPager.setAdapter(viewPagerAdapter);
        mPageIndicatorView.setViewPager(mViewPager);
        mTextViewTitle.setText(getString(R.string.genericTextViewTitle, mProduct.getTitle(), mProduct.getCode()));
        mTextViewDescription.setText(mProduct.getDescription());
        mButtonWebsite.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mProduct.getProductUrl()))));
        String price = makePrice(mProduct.getPrice());
        mTextViewPrice.setText(price);

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

    private void initViews(final View pView) {
        mTextViewTitle = pView.findViewById(R.id.textViewTitle);
        mTextViewDescription = pView.findViewById(R.id.textViewDescription);
        mButtonWebsite = pView.findViewById(R.id.buttonToWebsite);
        mTextViewPrice = pView.findViewById(R.id.textViewPrice);
    }

    private ArrayList<String> getImagesUrls(final String pImages) {
        ArrayList<String> urls = new ArrayList<>();
        final String imagesString = pImages.replace("|", " ").trim();
        final String[] imagesUrls = imagesString.split("[ ]");
        Collections.addAll(urls, imagesUrls);

        if (BuildConfig.DEBUG) {
            Log.i("Length", String.valueOf(urls.size()));
        }

        return urls;
    }

    private String makePrice(String pPrice) {
        pPrice = pPrice.replace("{", "");
        pPrice = pPrice.replace("}", ":\n");
        pPrice = pPrice.replace("|", "\n");

        return pPrice;
    }
}