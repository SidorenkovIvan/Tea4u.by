package ru.SidorenkovIvan.MyApplication.ui.ProductPage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import ru.SidorenkovIvan.MyApplication.ImageLoader;
import ru.SidorenkovIvan.MyApplication.Product;
import ru.SidorenkovIvan.MyApplication.R;
import ru.SidorenkovIvan.MyApplication.ViewPagerAdapter;

public class ProductPage extends Fragment {

    public static final Object load = new Object();
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private PageIndicatorView pageIndicatorView;
    private Product product;
    private String id;
    private final ArrayList<Bitmap> productImage = new ArrayList<>();

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.product_page_fragment, container, false);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Bundle bundle = getArguments();
        id = (String) Objects.requireNonNull(bundle).get("productID");

        product = new Product();
        initViewPager(view);
        getProduct();

        new Thread(() -> {
            try {
                if (connected()) {
                    ImageLoader imageLoader = new ImageLoader(getContext(), id, product.getImages());
                    imageLoader.isLoad();

                    synchronized (load) {
                        ArrayList<Bitmap> bitmaps = new ArrayList<>();
                        for (BitmapDrawable d : imageLoader.getCache(id)) bitmaps.add(d.getBitmap());
                        pagerAdapter = new ViewPagerAdapter(getContext(), bitmaps);

                        viewPager.post(() -> viewPager.setAdapter(pagerAdapter));

                        pageIndicatorView.post(() -> pageIndicatorView.setViewPager(viewPager));
                        pageIndicatorView.post(() -> pageIndicatorView.setCount(imageLoader.getCache(id).size()));
                    }
                } else {
                    pagerAdapter = new ViewPagerAdapter(getContext(), productImage);
                    viewPager.post(() -> viewPager.setAdapter(pagerAdapter));

                    pageIndicatorView.post(() -> pageIndicatorView.setViewPager(viewPager));
                    pageIndicatorView.post(() -> pageIndicatorView.setCount(productImage.size()));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        //TextView with title and code
        TextView textViewTitle = view.findViewById(R.id.textViewTitle);
        textViewTitle.post(() -> textViewTitle.setText(product.getTitle() + "  (" + product.getCode() + ")"));

        //TextView with description
        TextView textViewDescription = view.findViewById(R.id.textViewDescription);
        textViewDescription.setText(product.getDescription());

        //Output of price and button to go to the site
        Button button = view.findViewById(R.id.buttonToSite);
        button.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(product.getProductUrl()))));

        TextView textViewPrice = view.findViewById(R.id.textViewPrice);
        String price = product.getPrice();
        price = price.replace("{", "");
        price = price.replace("}", ":\n");
        price = price.replace("|", "\n");
        String TAG = "MyApp";
        Log.i(TAG, "Price is: " + price);
        textViewPrice.setText(price);

        return view;
    }

    private void initViewPager(View view) {
        viewPager = view.findViewById(R.id.viewPager);
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

        pageIndicatorView = view.findViewById(R.id.viewPagerIndicator);
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

    private void getProduct() {
        String DBname = "data.sqlite";
        String dbPath = requireContext().getApplicationInfo().dataDir + "/" + DBname;
        SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY);

        Cursor query = db.rawQuery("SELECT * FROM product WHERE product_id = '" + id + "'", null);
        query.moveToFirst();
        String productImg = query.getString(1);
        product.setTitle(query.getString(2));
        product.setProductUrl(query.getString(3));
        product.setDescription(query.getString(4));
        product.setImages(query.getString(5));
        product.setCode(query.getString(6));
        product.setPrice(query.getString(7));
        query.close();

        Cursor query1 = db.rawQuery("SELECT image.base64 FROM image WHERE url = '" + productImg + "'", null);
        query1.moveToFirst();
        byte[] decodedString = Base64.decode(query1.getString(0), Base64.DEFAULT);
        product.setImage(BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
        productImage.add(product.getImage());
        query1.close();

        db.close();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ViewModelProviders.of(this).get(ProductPageViewModel.class);
        // TODO: Use the ViewModel
    }
}