package ru.SidorenkovIvan.MyApplication.ui.Search;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.SidorenkovIvan.MyApplication.DBController;
import ru.SidorenkovIvan.MyApplication.PaginationScrollListener;
import ru.SidorenkovIvan.MyApplication.Product;
import ru.SidorenkovIvan.MyApplication.R;

public class Search extends Fragment {

    private static final String TAG = "MyApp";
    private static final String DBname = "data.sqlite";
    private String dbPath;

    private SearchAdapter searchAdapter;
    private ProgressBar progressBar;

    private boolean isLoading;
    private boolean isLastPage;
    private final int TOTAL_PAGES = 13;
    private int currentPage;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_fragment, container, false);

        SearchView searchView = view.findViewById(R.id.searchView);
        FragmentManager fragmentManager = getFragmentManager();
        dbPath = requireContext().getApplicationInfo().dataDir + "/" + DBname;
        currentPage = 0;
        isLoading = false;
        isLastPage = false;

        RecyclerView recyclerViewSearch = view.findViewById(R.id.recyclerViewSearch);
        progressBar = view.findViewById(R.id.searchProgressBar);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerViewSearch.setLayoutManager(layoutManager);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.i(TAG, "onQueryTextChange: " + newText);

                searchAdapter = new SearchAdapter(fragmentManager);
                recyclerViewSearch.setAdapter(searchAdapter);

                currentPage = 0;
                isLoading = false;
                isLastPage = false;

                String text = newText;

                recyclerViewSearch.addOnScrollListener(new PaginationScrollListener(layoutManager) {
                    @Override
                    protected void loadMoreItems() {
                        isLoading = true;
                        currentPage += 1;

                        new Handler().postDelayed(() -> loadNextPage(text), 200);
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

                new Handler().postDelayed(() ->loadFirstPage(text), 200);
                return false;
            }
        });

        return view;
    }

    private ArrayList<Product> search(String keyword, int offset) {
        ArrayList<Product> products = new ArrayList<>();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.rawQuery("SELECT product.product_id, product.productTitle, product.code FROM product WHERE productTitle LIKE ? LIMIT 10 OFFSET '" + offset + "'", new String[]{"%" + keyword + "%"});
        Cursor cursor1 = db.rawQuery("SELECT product.product_id, product.productTitle, product.code FROM product WHERE code LIKE ? LIMIT 10 OFFSET '" + offset + "'", new String[]{"%" + keyword + "%"});
        if (cursor.moveToFirst()) {
            do {
                Product product = new Product();
                product.setId(cursor.getString(0));
                product.setTitle(cursor.getString(1));
                product.setCode(cursor.getString(2));
                products.add(product);
            } while (cursor.moveToNext());

            cursor.close();
            db.close();

            return products;
        } else if (cursor1.moveToFirst()) {
            do {
                Product product = new Product();
                product.setId(cursor1.getString(0));
                product.setTitle(cursor1.getString(1));
                product.setCode(cursor1.getString(2));
                products.add(product);
            } while (cursor1.moveToNext());

            cursor1.close();
            db.close();

            return products;
        } else {
            Product product = new Product();
            product.setTitle("Не найдено");
            product.setCode("");
            products.add(product);

            return products;
        }
    }

    private void loadFirstPage(String newText) {
        Log.d("Loading...", "loadFirstPage: ");
        List<Product> products = search(newText, currentPage);
        progressBar.setVisibility(View.GONE);
        searchAdapter.addAll(products);

        if (currentPage <= TOTAL_PAGES) searchAdapter.addLoadingFooter();
        else isLastPage = true;

    }

    private void loadNextPage(String newText) {
        Log.d("Loading...", "loadNextPage: " + currentPage);
        List<Product> products = search(newText, currentPage * 10);

        searchAdapter.removeLoadingFooter();
        isLoading = false;

        searchAdapter.addAll(products);

        if (currentPage != TOTAL_PAGES) searchAdapter.addLoadingFooter();
        else isLastPage = true;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ViewModelProviders.of(this).get(SearchViewModel.class);
        // TODO: Use the ViewModel
    }
}