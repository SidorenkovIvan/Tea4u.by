package ru.SidorenkovIvan.MyApplication.ui.home;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.TextView;
import ru.SidorenkovIvan.MyApplication.ForCache;
import ru.SidorenkovIvan.MyApplication.R;
import ru.SidorenkovIvan.MyApplication.ui.Categories.Categories;
import ru.SidorenkovIvan.MyApplication.ui.PageOfProduct.PageOfProduct;
import java.util.ArrayList;
import java.util.Objects;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;

public class HomeFragment extends Fragment {

    private static final String TAG = "MyApp";
    private ScrollView scrollView;
    private LinearLayout linearLayoutForAll;
    private static final String DBname = "data.sqlite";
    private ArrayList<String> categoryID = new ArrayList<>();
    private ArrayList<String> categoryTitle = new ArrayList<>();
    private ArrayList<String> newProductID = new ArrayList<>();

    @SuppressLint("ResourceType")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HorizontalScrollView horizontalScrollView = new HorizontalScrollView(getContext());
        horizontalScrollView.setBackgroundColor(Color.WHITE);
        Typeface typeface = ResourcesCompat.getFont(requireActivity(), R.font.opensans);
        int textColor = ContextCompat.getColor(requireActivity(), R.color.textColor);

        scrollView = new ScrollView(getContext());
        scrollView.setBackgroundColor(Color.WHITE);

        linearLayoutForAll = new LinearLayout(getContext());
        linearLayoutForAll.setOrientation(LinearLayout.VERTICAL);
        //Params for ImageButtons
        layoutParamsForImageButtons.setMarginStart(20);
        layoutParamsForImageButtons.setMargins(0, 20, 0, 0);
        layoutParamsForImageButtons.setMarginEnd(20);
        //Params for TextViews
        layoutParamsForTextViews.setMarginStart(20);
        layoutParamsForTextViews.setMargins(0, 0, 0, 20);
        layoutParamsForTextViews.setMarginEnd(20);
        //Params for small buttons with categories
        layoutParamsForButtons.setMarginStart(20);
        layoutParamsForButtons.setMargins(0, 10, 0, 10);
        layoutParamsForButtons.setMarginEnd(20);

        //Categories buttons
        findCategoriesId();

        LinearLayout linearLayoutForSmallCategories = new LinearLayout(getContext());
        linearLayoutForSmallCategories.setPadding(30, 60, 20 , 20);
        for (byte i = 0; i < categoryID.size(); i++) {
            Button button1 = new Button(getContext());
            button1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            button1.setTextSize(14);
            button1.setTextColor(textColor);
            button1.setTypeface(typeface);
            button1.setText(categoryTitle.get(i));
            button1.setAllCaps(false);
            button1.setPadding(20, 0, 20, 0);
            button1.setBackgroundResource(R.drawable.small_categories_background);
            byte finalI = i;
            button1.setOnClickListener(v -> {
                Categories categories = new Categories();
                Bundle bundle = new Bundle();
                bundle.putString("categoryID", categoryID.get(finalI));
                bundle.putString("categoryTitle", categoryTitle.get(finalI));
                categories.setArguments(bundle);
                FragmentManager fragmentManager = getFragmentManager();
                Objects.requireNonNull(fragmentManager).beginTransaction().replace(R.id.nav_host_fragment, categories).addToBackStack(null).commit();
            });
            linearLayoutForSmallCategories.addView(button1, layoutParamsForButtons);
        }
        horizontalScrollView.setHorizontalScrollBarEnabled(false);
        horizontalScrollView.addView(linearLayoutForSmallCategories);
        linearLayoutForAll.addView(horizontalScrollView);

        //"New Products"
        TextView newProducts = new TextView(getContext());
        newProducts.setPadding(0, 50, 0, 20);
        newProducts.setText("НОВЫЕ ПОСТУПЛЕНИЯ");
        newProducts.setTextSize(18);
        newProducts.setTypeface(typeface);
        newProducts.setTextColor(textColor);
        newProducts.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        linearLayoutForAll.addView(newProducts, layoutParamsForTextViews);

        //Find new products in shop
        new Thread(() -> {
            findLatestProductsId();
            findProductParams();
            //Creating "New Products"
            makeMainLayout();
        }).start();
        return scrollView;
    }

    private void findCategoriesId() {
        categoryID.clear();
        categoryTitle.clear();
        newProductID.clear();
        String dbPath = requireContext().getApplicationInfo().dataDir + "/" + DBname;
        SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY);
        Cursor query = db.rawQuery("SELECT DISTINCT category.category_id, category.title FROM category INNER JOIN category_product ON category_product.category_id = category.category_id", null);
        query.moveToFirst();
        while (!query.isAfterLast()) {
            categoryID.add(query.getString(0));
            categoryTitle.add(query.getString(1));
            query.moveToNext();
        }
        query.close();
        db.close();
        Log.i(TAG, "Ids of categories: " + categoryID);
    }

    private void findLatestProductsId() {
        String dbPath = requireContext().getApplicationInfo().dataDir + "/" + DBname;
        SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY);
        Cursor query = db.rawQuery("SELECT * FROM product INNER JOIN latest ON product.product_id = latest.product_id", null);
        query.moveToFirst();
        while (!query.isAfterLast()) {
            newProductID.add(query.getString(0));
            query.moveToNext();
        }
        query.close();
        db.close();
        Log.i(TAG, "Ids of new products: " + newProductID);
    }

    private void findProductParams() {
        String dbPath = requireContext().getApplicationInfo().dataDir + "/" + DBname;
        SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY);
        for (byte i = 0; i < newProductID.size(); i++) {
            if (ForCache.getTitleFromMemoryCache(newProductID.get(i)) == null) {
                Cursor query = db.rawQuery("SELECT DISTINCT * FROM product WHERE product_id = '" + newProductID.get(i) + "'", null);
                query.moveToFirst();
                while (!query.isAfterLast()) {
                    ForCache.addTitleToMemoryCache(newProductID.get(i), query.getString(2));
                    ForCache.addUrlToMemoryCache(newProductID.get(i), query.getString(3));
                    query.moveToNext();
                }
                query.close();

                Cursor query1 = db.rawQuery("SELECT DISTINCT product.product_id, image.base64 FROM image INNER JOIN product ON product.imgUrl = image.url AND product.product_id = '" + newProductID.get(i) + "';", null);
                query1.moveToFirst();
                while (!query1.isAfterLast()) {
                    byte[] decodedString = Base64.decode(query1.getString(1), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    ForCache.addBitmapToMemoryCache(query1.getString(0), decodedByte);
                    query1.moveToNext();
                }
                query1.close();
            }
        }
        db.close();
    }

    private void makeMainLayout() {
        Typeface typeface = ResourcesCompat.getFont(requireContext(), R.font.opensans);
        int textColor = ContextCompat.getColor(requireContext(), R.color.textColor);
        for (byte i = 0; i < newProductID.size(); i++) {
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
            textView.setPadding(20,0,20, 50);

            final int finalI = i;
            imageButton.post(() -> {
                imageButton.setImageBitmap(ForCache.getBitmapFromMemoryCache(newProductID.get(finalI)));
                imageButton.setOnClickListener(v -> {
                    PageOfProduct pageOfProduct = new PageOfProduct();
                    Bundle bundle = new Bundle();
                    bundle.putString("productID", newProductID.get(finalI));
                    pageOfProduct.setArguments(bundle);
                    FragmentManager fragmentManager = getFragmentManager();
                    Objects.requireNonNull(fragmentManager).beginTransaction().replace(R.id.nav_host_fragment, pageOfProduct).addToBackStack(null).commit();
                });
            });
            textView.post(() -> textView.setText(ForCache.getTitleFromMemoryCache(newProductID.get(finalI))));

            i++;

            if (i < newProductID.size()) {
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
                textView_1.setPadding(20,0,20, 50);

                final byte finalI_1 = i;
                imageButton_1.post(() -> {
                    imageButton_1.setImageBitmap(ForCache.getBitmapFromMemoryCache(newProductID.get(finalI_1)));
                    imageButton_1.setOnClickListener(v -> {
                        PageOfProduct pageOfProduct = new PageOfProduct();
                        Bundle bundle = new Bundle();
                        bundle.putString("productID", newProductID.get(finalI_1));
                        pageOfProduct.setArguments(bundle);
                        FragmentManager fragmentManager = getFragmentManager();
                        Objects.requireNonNull(fragmentManager).beginTransaction().replace(R.id.nav_host_fragment, pageOfProduct).addToBackStack(null).commit();
                    });
                });
                textView_1.post(() -> textView_1.setText(ForCache.getTitleFromMemoryCache(newProductID.get(finalI_1))));
                linearLayoutForImageButtons.post(() -> linearLayoutForImageButtons.addView(imageButton, layoutParamsForImageButtons));
                linearLayoutForImageButtons.post(() -> linearLayoutForImageButtons.addView(imageButton_1, layoutParamsForImageButtons));
                linearLayoutForTextViews.post(() -> linearLayoutForTextViews.addView(textView, layoutParamsForTextViews));
                linearLayoutForTextViews.post(() -> linearLayoutForTextViews.addView(textView_1, layoutParamsForTextViews));
            } else {
                Space space = new Space(getContext());
                Space space_1 = new Space(getContext());
                linearLayoutForImageButtons.post(() -> linearLayoutForImageButtons.addView(imageButton, layoutParamsForImageButtons));
                linearLayoutForImageButtons.post(() -> linearLayoutForImageButtons.addView(space, layoutParamsForImageButtons));
                linearLayoutForTextViews.post(() -> linearLayoutForTextViews.addView(textView, layoutParamsForTextViews));
                linearLayoutForTextViews.post(() -> linearLayoutForTextViews.addView(space_1, layoutParamsForTextViews));
            }
            linearLayoutForAll.post(() -> linearLayoutForAll.addView(linearLayoutForImageButtons));
            linearLayoutForAll.post(() -> linearLayoutForAll.addView(linearLayoutForTextViews));
        }
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

    private final LinearLayout.LayoutParams layoutParamsForButtons = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
    );

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ViewModelProviders.of(this).get(HomeViewModel.class);
        // TODO: Use the ViewModel
    }
}