package com.example.MyApplication.ui.Categories;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.TextView;

import com.example.MyApplication.CategoriesViewModel;
import com.example.MyApplication.ForCache;
import com.example.MyApplication.R;
import com.example.MyApplication.ui.PageOfProduct.PageOfProduct;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;

public class Categories extends Fragment {

    private CategoriesViewModel mViewModel;
    private static final String DBname = "data.sqlite";
    ArrayList<String> productID = new ArrayList<>();


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ScrollView scrollView = new ScrollView(getContext());
        scrollView.setBackgroundColor(Color.WHITE);

        Bundle bundle = getArguments();
        String ID = (String) bundle.get("categoryID");
        String categoryTitle = (String) bundle.get("categoryTitle");
        productID.clear();
        String dbPath = getContext().getApplicationInfo().dataDir + "/" + DBname;
        getProductsId(ID, dbPath);
        getProductsParams(dbPath);
        makeMainLayout(scrollView, categoryTitle);

        return scrollView;
    }

    public void getProductsId(String ID, String dbPath) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY);
        Cursor query = db.rawQuery("SELECT DISTINCT product_id FROM category_product WHERE category_id = '" + ID + "'", null);
        query.moveToFirst();
        while (!query.isAfterLast()) {
            productID.add(query.getString(0));
            query.moveToNext();
        }
        query.close();
        db.close();
    }

    public void getProductsParams(String dbPath) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY);
        for (byte i = 0; i < productID.size(); i++) {
            if (ForCache.getTitleFromMemoryCache(productID.get(i)) == null) {
                Cursor query1 = db.rawQuery("SELECT DISTINCT * FROM product WHERE product_id = '" + productID.get(i) + "'", null);
                query1.moveToFirst();
                while (!query1.isAfterLast()) {
                    ForCache.addTitleToMemoryCache(productID.get(i), query1.getString(2));
                    ForCache.addUrlToMemoryCache(productID.get(i), query1.getString(3));
                    query1.moveToNext();
                }
                query1.close();

                Cursor query2 = db.rawQuery("SELECT DISTINCT product.product_id, image.base64 FROM image INNER JOIN product ON product.imgUrl = image.url AND product.product_id = '" + productID.get(i) + "';", null);
                query2.moveToFirst();
                while (!query2.isAfterLast()) {
                    byte[] decodedString = Base64.decode(query2.getString(1), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    ForCache.addBitmapToMemoryCache(query2.getString(0), decodedByte);
                    query2.moveToNext();
                }
                query2.close();
            }
        }
        db.close();
    }

    public void makeMainLayout(ScrollView scrollView, String categoryTitle) {
        LinearLayout linearLayoutForAll = new LinearLayout(getContext());
        linearLayoutForAll.setOrientation(LinearLayout.VERTICAL);

        Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.opensans);
        int textColor = ContextCompat.getColor(getContext(), R.color.textColor);

        TextView textViewForCategory = new TextView(getContext());
        textViewForCategory.setPadding(0, 80, 0, 50);
        textViewForCategory.setText(categoryTitle);
        textViewForCategory.setTextSize(20);
        textViewForCategory.setAllCaps(true);
        textViewForCategory.setTypeface(typeface);
        textViewForCategory.setTextColor(textColor);
        textViewForCategory.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        linearLayoutForAll.addView(textViewForCategory, layoutParamsForTextViews);


        for (byte i = 0; i < productID.size(); i++) {
            final LinearLayout linearLayoutForImageButtons = new LinearLayout(getContext());
            final LinearLayout linearLayoutForTextViews = new LinearLayout(getContext());
            final ImageButton imageButton = new ImageButton(getContext());
            final TextView textView = new TextView(getContext());

            imageButton.setScaleType(ImageButton.ScaleType.CENTER_CROP);
            imageButton.setAdjustViewBounds(true);
            imageButton.setBackgroundColor(Color.WHITE);
            imageButton.setPadding(20, 20, 20, 0);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textView.setTextSize(14);
            textView.setTextColor(textColor);
            textView.setTypeface(typeface);
            textView.setPadding(20, 0, 20, 50);

            final int finalI = i;
            imageButton.post(() -> {
                imageButton.setImageBitmap(ForCache.getBitmapFromMemoryCache(productID.get(finalI)));
                imageButton.setOnClickListener(v -> {
                    PageOfProduct pageOfProduct = new PageOfProduct();
                    Bundle bundle = new Bundle();
                    bundle.putString("productID", productID.get(finalI));
                    pageOfProduct.setArguments(bundle);
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.nav_host_fragment, pageOfProduct).addToBackStack(null).commit();
                });
            });
            textView.post(() -> textView.setText(ForCache.getTitleFromMemoryCache(productID.get(finalI))));

            i++;

            if (i < productID.size()) {
                final ImageButton imageButton_1 = new ImageButton(getContext());
                final TextView textView_1 = new TextView(getContext());

                imageButton_1.setScaleType(ImageButton.ScaleType.CENTER_CROP);
                imageButton_1.setAdjustViewBounds(true);
                imageButton_1.setBackgroundColor(Color.WHITE);
                imageButton_1.setPadding(20, 20, 20, 0);
                textView_1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                textView_1.setTextSize(14);
                textView_1.setTextColor(textColor);
                textView_1.setTypeface(typeface);
                textView_1.setPadding(20, 0, 20, 50);

                final byte finalI_1 = i;
                imageButton_1.post(() -> {
                    imageButton_1.setImageBitmap(ForCache.getBitmapFromMemoryCache(productID.get(finalI_1)));
                    imageButton_1.setOnClickListener(v -> {
                        PageOfProduct pageOfProduct = new PageOfProduct();
                        Bundle bundle = new Bundle();
                        bundle.putString("productID", productID.get(finalI_1));
                        pageOfProduct.setArguments(bundle);
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.nav_host_fragment, pageOfProduct).addToBackStack(null).commit();
                    });
                });
                textView_1.post(() -> textView_1.setText(ForCache.getTitleFromMemoryCache(productID.get(finalI_1))));
                linearLayoutForImageButtons.post(() -> linearLayoutForImageButtons.addView(imageButton, layoutParamsForImageButtons));
                linearLayoutForImageButtons.post(() -> linearLayoutForImageButtons.addView(imageButton_1, layoutParamsForImageButtons));
                linearLayoutForTextViews.post(() -> linearLayoutForTextViews.addView(textView, layoutParamsForTextViews));
                linearLayoutForTextViews.post(() -> linearLayoutForTextViews.addView(textView_1, layoutParamsForTextViews));
                linearLayoutForAll.post(() -> linearLayoutForAll.addView(linearLayoutForImageButtons));
                linearLayoutForAll.post(() -> linearLayoutForAll.addView(linearLayoutForTextViews));
            } else {
                Space space = new Space(getContext());
                Space space_1 = new Space(getContext());
                linearLayoutForImageButtons.post(() -> linearLayoutForImageButtons.addView(imageButton, layoutParamsForImageButtons));
                linearLayoutForImageButtons.post(() -> linearLayoutForImageButtons.addView(space, layoutParamsForImageButtons));
                linearLayoutForTextViews.post(() -> linearLayoutForTextViews.addView(textView, layoutParamsForTextViews));
                linearLayoutForTextViews.post(() -> linearLayoutForTextViews.addView(space_1, layoutParamsForTextViews));
                linearLayoutForAll.post(() -> linearLayoutForAll.addView(linearLayoutForImageButtons));
                linearLayoutForAll.post(() -> linearLayoutForAll.addView(linearLayoutForTextViews));
            }
        }
        scrollView.post(scrollView::removeAllViews);
        scrollView.post(() -> scrollView.addView(linearLayoutForAll));
    }

    private final LinearLayout.LayoutParams layoutParamsForImageButtons = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
            1);

    private final LinearLayout.LayoutParams layoutParamsForTextViews = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
            1);

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(CategoriesViewModel.class);
        // TODO: Use the ViewModel
    }

}