package ru.SidorenkovIvan.MyApplication.ui.Categories;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.SidorenkovIvan.MyApplication.DBController;
import ru.SidorenkovIvan.MyApplication.PaginationScrollListener;
import ru.SidorenkovIvan.MyApplication.Product;
import ru.SidorenkovIvan.MyApplication.R;

public class Categories extends Fragment {

    private static final String DBname = "data.sqlite";
    private String dbPath;

    private ProductAdapter productAdapter;
    private GridLayoutManager layoutManager;
    private ProgressBar progressBar;

    private String categoryId;
    private static final int PAGE_START = 0;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private final int TOTAL_PAGES = 3;
    private int currentPage = PAGE_START;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.categories_fragment, container, false);

        dbPath = requireContext().getApplicationInfo().dataDir + "/" + DBname;
        FragmentManager fragmentManager = getFragmentManager();

        Bundle bundle = getArguments();
        categoryId = (String) Objects.requireNonNull(bundle).get("categoryID");
        String categoryTitle = (String) bundle.get("categoryTitle");

        TextView textViewCategory = view.findViewById(R.id.textViewCategory);
        textViewCategory.setText(categoryTitle);

        RecyclerView recyclerViewProducts = view.findViewById(R.id.recyclerViewProducts);
        progressBar = view.findViewById(R.id.main_progress);
        layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerViewProducts.setLayoutManager(layoutManager);

        productAdapter = new ProductAdapter(fragmentManager);
        recyclerViewProducts.setAdapter(productAdapter);

        recyclerViewProducts.addOnScrollListener(new PaginationScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;

                // mocking network delay for API call
                new Handler().postDelayed(() -> loadNextPage(), 1000);
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });


        // mocking network delay for API call
        new Handler().postDelayed(this::loadFirstPage, 1000);

        return view;
    }

    public ArrayList<String> getProductsId(String ID, String dbPath, int offset) {
        ArrayList<String> productId = new ArrayList<>();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY);
        Cursor query = db.rawQuery("SELECT DISTINCT product_id FROM category_product WHERE category_id = '" + ID + "' LIMIT 10 OFFSET '" + offset + "'", null);
        query.moveToFirst();
        while (!query.isAfterLast()) {
            productId.add(query.getString(0));
            query.moveToNext();
        }
        query.close();
        db.close();

        return productId;
    }

    private void loadFirstPage() {
        Log.d("Loading...", "loadFirstPage: ");
        List<Product> products = DBController.getProducts(dbPath, getProductsId(categoryId, dbPath, currentPage));
        progressBar.setVisibility(View.GONE);
        productAdapter.addAll(products);

        if (currentPage <= TOTAL_PAGES) productAdapter.addLoadingFooter();
        else isLastPage = true;

    }

    private void loadNextPage() {
        Log.d("Loading...", "loadNextPage: " + currentPage);
        List<Product> products = DBController.getProducts(dbPath, getProductsId(categoryId, dbPath, currentPage * 10));

        productAdapter.removeLoadingFooter();
        isLoading = false;

        productAdapter.addAll(products);

        if (currentPage != TOTAL_PAGES) productAdapter.addLoadingFooter();
        else isLastPage = true;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ViewModelProviders.of(this).get(CategoriesViewModel.class);
        // TODO: Use the ViewModel
    }
}