package ru.SidorenkovIvan.MyApplication.ui.PageOfProduct;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ScrollView;
import android.widget.TextView;
import ru.SidorenkovIvan.MyApplication.ForCache;
import ru.SidorenkovIvan.MyApplication.R;
import ru.SidorenkovIvan.MyApplication.ViewPagerAdapter;
import com.rd.PageIndicatorView;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class PageOfProduct extends Fragment {

    private PageOfProductViewModel mViewModel;
    private static final String TAG = "MyApp";
    private static final String DBname = "data.sqlite";
    private String ID;
    private String description;
    private String code;
    private String images;
    private String productTitle;
    private String productUrl;
    private String price;
    private ArrayList<Bitmap> productImages = new ArrayList<>();
    private ArrayList<Bitmap> bitmapOfImages = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.page_of_product_fragment, container, false);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        new Thread(() -> {

        Bundle bundle = getArguments();
        ID = (String) bundle.get("productID");

        findProductParams();

        //TextView with title and code
        TextView textViewTitle = view.findViewById(R.id.textViewTitle);
        textViewTitle.post(() -> textViewTitle.setText(productTitle + "  (" + code + ")"));

        //Check for connection and choosing between image of database and images from site
        if (connected()) {
            //Big images from site
            if (ForCache.getLargeImagesFromMemoryCache(ID) == null) {
                largeImages();
            }

            //View pager
            ViewPager viewPager = view.findViewById(R.id.viewPager);
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
            PagerAdapter adapter = new ViewPagerAdapter(getContext(), ForCache.getLargeImagesFromMemoryCache(ID));
            viewPager.post(() -> viewPager.setAdapter(adapter));
            //Indicator for view pager
            PageIndicatorView pageIndicatorView = view.findViewById(R.id.viewPagerIndicator);
            pageIndicatorView.post(() -> pageIndicatorView.setViewPager(viewPager));
            pageIndicatorView.post(() -> pageIndicatorView.setCount(ForCache.getLargeImagesFromMemoryCache(ID).size()));
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    pageIndicatorView.setSelection(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });
        } else { //Image from database
            //View pager
            ViewPager viewPager = view.findViewById(R.id.viewPager);
            ViewTreeObserver viewTreeObserver = viewPager.getViewTreeObserver();
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    int viewPagerWidth = viewPager.getWidth();
                    float viewPagerHeight = (float) (viewPagerWidth);
                    layoutParams.width = viewPagerWidth;
                    layoutParams.height = (int) viewPagerHeight;
                    viewPager.setLayoutParams(layoutParams);
                    viewPager.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            });
            PagerAdapter adapter = new ViewPagerAdapter(getContext(), productImages);
            viewPager.post(() -> viewPager.setAdapter(adapter));
            //Indicator for view pager
            PageIndicatorView pageIndicatorView = view.findViewById(R.id.viewPagerIndicator);
            pageIndicatorView.post(() -> pageIndicatorView.setViewPager(viewPager));
            pageIndicatorView.post(() -> pageIndicatorView.setCount(productImages.size()));
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    pageIndicatorView.setSelection(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });
        }

        //TextView with description
        TextView textViewDescription = view.findViewById(R.id.textViewDescription);
        textViewDescription.post(() -> textViewDescription.setText(description));

        //Output of price and button to go to the site
        Button button = view.findViewById(R.id.buttonToSite);
        button.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(productUrl))));

        TextView textViewPrice = view.findViewById(R.id.textViewPrice);
        price = price.replace("{", "");
        price = price.replace("}", ":\n");
        price = price.replace("|", "\n");
        Log.i(TAG, "Price is: " + price);
        textViewPrice.post(() -> textViewPrice.setText(price));

        ScrollView scrollView = view.findViewById(R.id.scrollView2);
        scrollView.post(() -> scrollView.setVisibility(View.VISIBLE));

        }).start();

        return view;
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
        Bitmap productImage;
        String dbPath = getContext().getApplicationInfo().dataDir + "/" + DBname;
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
        productImages.add(productImage);
        query1.close();
        db.close();
    }

    private void largeImages() {
        bitmapOfImages.clear();
        String img = images.replace("|", " ").trim();
        String[] imgg = img.split("[ ]");
        for (String s : imgg) bitmapOfImages.add(getBitmapFromURL(s));
        ForCache.addLargeImagesToMemoryCache(ID, bitmapOfImages);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(PageOfProductViewModel.class);
        // TODO: Use the ViewModel
    }
}