package com.example.MyApplication.ui.PageOfProduct;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import com.example.MyApplication.R;
import com.example.MyApplication.ViewPagerAdapter;
import com.rd.PageIndicatorView;
import com.rd.animation.type.AnimationType;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class PageOfProduct extends Fragment {

    private PageOfProductViewModel mViewModel;
    private static final String TAG = "MyApp";
    private static final String DBname = "data.sqlite";
    private String description;
    private String code;
    private String images;
    private String productTitle;
    private String productUrl;
    private String price;
    private Bitmap productImage;
    private ArrayList<Bitmap> bitmapOfImages = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.opensans);
        int textColor = ContextCompat.getColor(getContext(), R.color.textColor);
        int textColor1 = ContextCompat.getColor(getContext(), R.color.colorForSmallCategories);

        ScrollView scrollView = new ScrollView(getContext());
        scrollView.setBackgroundColor(Color.WHITE);
        ProgressBar progressBar = new ProgressBar(getContext());
        //Params for scroll view
        scrollView.setBackgroundColor(Color.WHITE);
        scrollView.setLayoutParams(layoutParamsForScrollView);
        //Params for Progress Bar
        progressBar.setLayoutParams(progressBarParam);
        scrollView.addView(progressBar, layoutParamsForScrollView);

        //Linear layout for all views
        LinearLayout linearLayoutForAll = new LinearLayout(getContext());
        linearLayoutForAll.setOrientation(LinearLayout.VERTICAL);
        //Params for ImageView
        layoutParamsForImageButtons.setMarginStart(20);
        layoutParamsForImageButtons.setMargins(0, 20, 0, 0);
        layoutParamsForImageButtons.setMarginEnd(20);
        //Params for Title
        layoutParamsForTextViews.setMarginStart(20);
        layoutParamsForTextViews.setMargins(0, 20, 0, 20);
        layoutParamsForTextViews.setMarginEnd(20);
        //Params for Description
        layoutParamsForDescription.setMarginStart(20);
        layoutParamsForDescription.setMargins(0, 20, 0, 20);
        layoutParamsForDescription.setMarginEnd(20);
        //Params for indicator
        layoutParamsForIndicator.gravity = Gravity.CENTER_HORIZONTAL;
        layoutParamsForIndicator.setMargins(0, 0, 0, 20);

        new Thread(() -> {
            findProductParams();

            //TextView with title and code
            TextView textView = new TextView(getContext());
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textView.setTextSize(16);
            textView.setTextColor(textColor);
            textView.setTypeface(typeface);
            textView.setPadding(30, 50, 30, 0);
            String finalCode = code;
            String finalProductTitle = productTitle;
            textView.post(() -> textView.setText(finalProductTitle + "  (" + finalCode + ")"));
            linearLayoutForAll.post(() -> linearLayoutForAll.addView(textView, layoutParamsForTextViews));

            //Check for connection and choosing between image of database and images from site
            if (connected()) {
                //Big images from site
                largeImages();

                if (bitmapOfImages.size() == 1) {
                    ImageView imageView = new ImageView(getContext());
                    imageView.setBackgroundColor(Color.WHITE);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    imageView.setAdjustViewBounds(true);
                    imageView.post(() -> imageView.setImageBitmap(bitmapOfImages.get(0)));
                    linearLayoutForAll.post(() -> linearLayoutForAll.addView(imageView, layoutParamsForImageButtons));
                } else {
                    //View pager
                    ViewPager viewPager = new ViewPager(getContext());
                    viewPager.setPadding(10, 0, 10, 0);
                    ViewTreeObserver viewTreeObserver = viewPager.getViewTreeObserver();
                    viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT);
                            int viewPagerWidth = viewPager.getWidth();
                            float viewPagerHeight = (float) (viewPagerWidth);
                            layoutParams.width = viewPagerWidth;
                            layoutParams.height = (int) viewPagerHeight;
                            viewPager.setLayoutParams(layoutParams);
                            viewPager.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }
                    });
                    PagerAdapter adapter = new ViewPagerAdapter(getContext(), bitmapOfImages);
                    viewPager.setAdapter(adapter);
                    //Indicator for view pager
                    PageIndicatorView pageIndicatorView = new PageIndicatorView(getContext());
                    pageIndicatorView.setViewPager(viewPager);
                    pageIndicatorView.setCount(bitmapOfImages.size());
                    pageIndicatorView.setRadius(8);
                    pageIndicatorView.setSelectedColor(Color.DKGRAY);
                    pageIndicatorView.setUnselectedColor(Color.GRAY);
                    pageIndicatorView.setAnimationType(AnimationType.THIN_WORM);
                    viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                        @Override
                        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

                        @Override
                        public void onPageSelected(int position) {
                            pageIndicatorView.setSelection(position);
                        }

                        @Override
                        public void onPageScrollStateChanged(int state) {}
                    });
                    linearLayoutForAll.post(() -> linearLayoutForAll.addView(viewPager));
                    linearLayoutForAll.post(() -> linearLayoutForAll.addView(pageIndicatorView, layoutParamsForIndicator));
                }
            } else { //Image from database
                ImageView imageView = new ImageView(getContext());
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setAdjustViewBounds(true);
                imageView.setBackgroundColor(Color.WHITE);
                imageView.setImageBitmap(productImage);
                linearLayoutForAll.post(() -> linearLayoutForAll.addView(imageView, layoutParamsForImageButtons));
            }

            //TextView with description
            TextView textView1 = new TextView(getContext());
            textView1.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            textView1.setTextSize(16);
            textView1.setTextColor(textColor);
            textView1.setTypeface(typeface);
            textView1.setPadding(40, 50, 40, 50);
            String finalDescription = description;
            textView1.post(() -> textView1.setText(finalDescription));
            linearLayoutForAll.post(() -> linearLayoutForAll.addView(textView1, layoutParamsForDescription));

            //Output of price and button to go to the site
            LinearLayout linearLayoutForPriceAndBuy = new LinearLayout(getContext());
            Button button = new Button(getContext());
            button.setBackgroundColor(textColor1);
            button.setTypeface(typeface);
            button.setTextColor(textColor);
            button.setText("Купить");
            button.setAllCaps(false);
            button.setTextSize(16);
            button.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            String finalProductUrl = productUrl;
            button.post(() -> {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(finalProductUrl));
                button.setOnClickListener(v -> startActivity(browserIntent));
            });
            TextView textView2 = new TextView(getContext());
            price = price.replace("{", "");
            price = price.replace("}", ":\n");
            price = price.replace("|", "\n");
            Log.i(TAG, "Price is: " + price);
            textView2.setText(price);
            textView2.setTextColor(textColor);
            textView2.setTypeface(typeface);
            textView2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textView2.setTextSize(16);
            linearLayoutForPriceAndBuy.post(() -> linearLayoutForPriceAndBuy.addView(textView2, layoutParamsForDescription));
            linearLayoutForPriceAndBuy.post(() -> linearLayoutForPriceAndBuy.addView(button, layoutParamsForDescription));
            linearLayoutForAll.post(() -> linearLayoutForAll.addView(linearLayoutForPriceAndBuy));
            scrollView.post(scrollView::removeAllViews);
            scrollView.post(() -> scrollView.addView(linearLayoutForAll));
        }).start();
        return scrollView;
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            return null;
        }
    }

    public boolean connected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
    }

    private void findProductParams() {
        String dbPath = getContext().getApplicationInfo().dataDir + "/" + DBname;
        Bundle bundle = getArguments();
        String ID = (String) bundle.get("productID");
        SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY);
        Cursor query = db.rawQuery("SELECT * FROM product WHERE product_id = '" + ID + "'", null);
        query.moveToFirst();
        String productImg = query.getString(1);
        productTitle = query.getString(2);
        productUrl = query.getString(3);
        description = query.getString(4);
        images = query.getString(5);
        code = query.getString(6);
        price = query.getString(7);
        query.close();
        Cursor query1 = db.rawQuery("SELECT * FROM image WHERE url = '" + productImg + "'", null);
        query1.moveToFirst();
        byte[] decodedString = Base64.decode(query1.getString(1), Base64.DEFAULT);
        productImage = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        query1.close();
        db.close();
    }

    private void largeImages() {
        bitmapOfImages.clear();
        String img = images.replace("|", " ").trim();
        String[] imgg = img.split("[ ]");
        for (String s : imgg) bitmapOfImages.add(getBitmapFromURL(s));
    }

    private final ScrollView.LayoutParams layoutParamsForScrollView = new ScrollView.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
            Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);

    private final LinearLayout.LayoutParams progressBarParam = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT);

    private final LinearLayout.LayoutParams layoutParamsForImageButtons = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
            1);

    private final LinearLayout.LayoutParams layoutParamsForTextViews = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
            1);

    private final LinearLayout.LayoutParams layoutParamsForDescription = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
            1);

    private final LinearLayout.LayoutParams layoutParamsForIndicator = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            1);

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(PageOfProductViewModel.class);
        // TODO: Use the ViewModel
    }
}