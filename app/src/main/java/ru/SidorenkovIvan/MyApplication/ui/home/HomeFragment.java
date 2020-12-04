package ru.SidorenkovIvan.MyApplication.ui.home;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.SidorenkovIvan.MyApplication.Category;
import ru.SidorenkovIvan.MyApplication.DBController;
import ru.SidorenkovIvan.MyApplication.PaginationScrollListener;
import ru.SidorenkovIvan.MyApplication.R;
import java.util.ArrayList;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import ru.SidorenkovIvan.MyApplication.Product;
import ru.SidorenkovIvan.MyApplication.ui.Categories.ProductAdapter;

public class HomeFragment extends Fragment {

    private static final String TAG = "MyApp";
    private static final String DBname = "data.sqlite";
    private String dbPath;

    private ProductAdapter productAdapter;
    private ProgressBar progressBar;

    private boolean isLoading;
    private boolean isLastPage;
    private final int TOTAL_PAGES = 1;
    private int currentPage;

    @SuppressLint("ResourceType")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        dbPath = requireContext().getApplicationInfo().dataDir + "/" + DBname;
        FragmentManager fragmentManager = getFragmentManager();
        currentPage = 0;
        isLoading = false;
        isLastPage = false;

        //Categories buttons
        RecyclerView RecyclerViewCategories = view.findViewById(R.id.recyclerViewSmallCategories);
        List<Category> categories = DBController.getNotEmptyCategories(dbPath);
        HomeCategoriesAdapter catAdapter = new HomeCategoriesAdapter(fragmentManager, categories);
        RecyclerViewCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        RecyclerViewCategories.setAdapter(catAdapter);

        //Find new products in shop
        RecyclerView recyclerViewProducts = view.findViewById(R.id.recyclerViewNewProducts);
        progressBar = view.findViewById(R.id.home_progress);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerViewProducts.setLayoutManager(layoutManager);

        productAdapter = new ProductAdapter(fragmentManager);
        recyclerViewProducts.setAdapter(productAdapter);

        recyclerViewProducts.addOnScrollListener(new PaginationScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;

                new Handler().postDelayed(() -> loadNextPage(), 100);
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

        loadFirstPage();

        return view;
    }

    private ArrayList<String> getProductsId(int offset) {
        ArrayList<String> newProductsId = new ArrayList<>();
        String dbPath = requireContext().getApplicationInfo().dataDir + "/" + DBname;
        SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY);
        Cursor query = db.rawQuery("SELECT product.product_id FROM product INNER JOIN latest ON product.product_id = latest.product_id LIMIT 10 OFFSET '" + offset + "'", null);
        query.moveToFirst();
        while (!query.isAfterLast()) {
            newProductsId.add(query.getString(0));
            query.moveToNext();
        }
        query.close();
        db.close();
        Log.i(TAG, "Ids of new products: " + newProductsId);

        return newProductsId;
    }

    private void loadFirstPage() {
        Log.d("Loading...", "loadFirstPage: ");
        List<Product> products = DBController.getProducts(dbPath, getProductsId(currentPage));
        progressBar.setVisibility(View.GONE);
        productAdapter.addAll(products);

        if (currentPage <= TOTAL_PAGES) productAdapter.addLoadingFooter();
        else isLastPage = true;

    }

    private void loadNextPage() {
        Log.d("Loading...", "loadNextPage: " + currentPage);
        List<Product> products = DBController.getProducts(dbPath, getProductsId(currentPage * 10));

        productAdapter.removeLoadingFooter();
        isLoading = false;

        productAdapter.addAll(products);

        if (currentPage != TOTAL_PAGES) productAdapter.addLoadingFooter();
        else isLastPage = true;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ViewModelProviders.of(this).get(HomeViewModel.class);
        // TODO: Use the ViewModel
    }
}