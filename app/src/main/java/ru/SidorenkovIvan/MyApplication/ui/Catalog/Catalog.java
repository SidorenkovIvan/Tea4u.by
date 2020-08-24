package ru.SidorenkovIvan.MyApplication.ui.Catalog;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import ru.SidorenkovIvan.MyApplication.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import ru.SidorenkovIvan.MyApplication.ui.Categories.Categories;

public class Catalog extends Fragment {

    private CatalogViewModel mViewModel;
    private static final String TAG = "MyApp";
    private static final String DBname = "data.sqlite";
    private ArrayList<String> categoryID = new ArrayList<>();
    private ArrayList<String> categoryTitle = new ArrayList<>();


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.opensans);
        int textColor = ContextCompat.getColor(getContext(), R.color.textColor);

        ScrollView scrollView = new ScrollView(getContext());
        scrollView.setBackgroundColor(Color.WHITE);
        LinearLayout linearLayoutForAll = new LinearLayout(getActivity());
        linearLayoutForAll.setOrientation(LinearLayout.VERTICAL);

        //Params for ImageButtons
        layoutParamsForImageButtons.setMarginStart(20);
        layoutParamsForImageButtons.setMargins(0, 20, 0, 0);
        layoutParamsForImageButtons.setMarginEnd(20);
        //Params for TextViews
        layoutParamsForTextViews.setMarginStart(20);
        layoutParamsForTextViews.setMargins(0, 0, 0, 20);
        layoutParamsForTextViews.setMarginEnd(20);
        //Params for Buttons
        layoutParamsForButtons.setMargins(0, 20, 0, 0);

        findCategoriesIdTit();

        for (byte i = 0; i < categoryID.size(); i++) {
            Button button = new Button(getContext());
            button.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            button.setTextSize(16);
            button.setBackgroundColor(Color.WHITE);
            button.setTextColor(textColor);
            button.setTypeface(typeface);
            button.setText(categoryTitle.get(i));
            button.setAllCaps(false);
            button.setStateListAnimator(null);
            button.setPadding(80, 0, 80, 0);

            byte finalI = i;
            button.setOnClickListener(v -> {
                Categories categories = new Categories();
                Bundle bundle = new Bundle();
                bundle.putString("categoryID", categoryID.get(finalI));
                bundle.putString("categoryTitle", categoryTitle.get(finalI));
                categories.setArguments(bundle);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.nav_host_fragment, categories).addToBackStack(null).commit();
            });

            linearLayoutForAll.addView(button, layoutParamsForButtons);
        }

        scrollView.addView(linearLayoutForAll);

        return scrollView;
    }

    private void findCategoriesIdTit() {
        categoryID.clear();
        categoryTitle.clear();
        String dbPath = getContext().getApplicationInfo().dataDir + "/" + DBname;
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
    }

    private final LinearLayout.LayoutParams layoutParamsForButtons = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
            1
    );

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
        mViewModel = ViewModelProviders.of(this).get(CatalogViewModel.class);
        // TODO: Use the ViewModel
    }
}