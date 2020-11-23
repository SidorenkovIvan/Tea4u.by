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

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.SidorenkovIvan.MyApplication.CatalogAdapter;
import ru.SidorenkovIvan.MyApplication.R;
import java.util.ArrayList;
import java.util.Objects;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import ru.SidorenkovIvan.MyApplication.ui.Categories.Categories;

public class Catalog extends Fragment {

    private static final String DBname = "data.sqlite";
    private ArrayList<String> categoryID = new ArrayList<>();
    private ArrayList<String> categoryTitle = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.catalog_fragment, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        FragmentManager fragmentManager = getFragmentManager();
        findCategoriesIdTit();

        CatalogAdapter catalogAdapter = new CatalogAdapter(fragmentManager, categoryID, categoryTitle);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(catalogAdapter);

        return view;
    }

    private void findCategoriesIdTit() {
        categoryID.clear();
        categoryTitle.clear();
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
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ViewModelProviders.of(this).get(CatalogViewModel.class);
        // TODO: Use the ViewModel
    }
}