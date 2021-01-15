package ru.sidorenkovivan.myapplication.ui.search;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.sidorenkovivan.myapplication.BuildConfig;
import ru.sidorenkovivan.myapplication.util.PaginationScrollListener;
import ru.sidorenkovivan.myapplication.entities.Product;
import ru.sidorenkovivan.myapplication.R;
import ru.sidorenkovivan.myapplication.recyclerviewadapter.AdapterSearch;

public class Search extends Fragment {

    private final int ITEMS_ON_PAGE = 15;
    private final int ZERO_PAGE = 0;
    private final String TAG = "Search fragment";
    private String mDbPath;
    private String mText;

    private AdapterSearch mSearchAdapter;
    private ProgressBar mProgressBar;

    private final int TOTAL_PAGES = 15;
    private boolean mIsLoading;
    private boolean mIsLastPage;
    private int mCurrentPage;

    @Override
    public View onCreateView(@NonNull final LayoutInflater pInflater,
                             @Nullable final ViewGroup pContainer,
                             @Nullable final Bundle pSavedInstanceState) {
        final View view = pInflater.inflate(R.layout.fragment_search, pContainer, false);

        final SearchView searchView = view.findViewById(R.id.searchView);
        final FragmentManager fragmentManager = getFragmentManager();
        mDbPath = requireContext().getApplicationInfo().dataDir + getString(R.string.databaseName);
        mCurrentPage = ZERO_PAGE;
        mIsLoading = false;
        mIsLastPage = false;

        final RecyclerView recyclerViewSearch = view.findViewById(R.id.recyclerViewSearch);
        mProgressBar = view.findViewById(R.id.searchProgressBar);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerViewSearch.setLayoutManager(layoutManager);

        mSearchAdapter = new AdapterSearch(fragmentManager);
        recyclerViewSearch.setAdapter(mSearchAdapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String pSubmit) {
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "onQueryTextSubmit: " + pSubmit);
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(final String pNewText) {
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "onQueryTextChange: " + pNewText);
                }

                mSearchAdapter = new AdapterSearch(fragmentManager);
                recyclerViewSearch.setAdapter(mSearchAdapter);

                mText = pNewText;
                mCurrentPage = ZERO_PAGE;
                loadFirstPage(mText);

                return false;
            }
        });

        recyclerViewSearch.addOnScrollListener(new PaginationScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                mIsLoading = true;
                mCurrentPage += 1;

                final Handler handler = new Handler();
                handler.postDelayed(() -> loadNextPage(mText), 50);
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return mIsLastPage;
            }

            @Override
            public boolean isLoading() {
                return mIsLoading;
            }
        });

        return view;
    }

    private ArrayList<Product> search(final String pKeyword, final int pOffset) {
        final ArrayList<Product> products = new ArrayList<>();
        final SQLiteDatabase db = SQLiteDatabase.openDatabase(mDbPath, null, SQLiteDatabase.OPEN_READONLY);
        final Cursor titleQuery = db.rawQuery("SELECT product.product_id, product.productTitle, product.code FROM product WHERE productTitle LIKE ? LIMIT 15 OFFSET '" + pOffset + "'", new String[]{"%" + pKeyword + "%"});
        final Cursor codeQuery = db.rawQuery("SELECT product.product_id, product.productTitle, product.code FROM product WHERE code LIKE ? LIMIT 15 OFFSET '" + pOffset + "'", new String[]{"%" + pKeyword + "%"});

        final int COLUMN_ID = 0;
        final int COLUMN_TITLE = 1;
        final int COLUMN_CODE = 2;
        try {
            if (titleQuery.moveToFirst()) {
                do {
                    final Product product = new Product();
                    product.setId(titleQuery.getString(COLUMN_ID));
                    product.setTitle(titleQuery.getString(COLUMN_TITLE));
                    product.setCode(titleQuery.getString(COLUMN_CODE));
                    products.add(product);
                } while (titleQuery.moveToNext());
            } else if (codeQuery.moveToFirst()) {
                do {
                    final Product product = new Product();
                    product.setId(codeQuery.getString(COLUMN_ID));
                    product.setTitle(codeQuery.getString(COLUMN_TITLE));
                    product.setCode(codeQuery.getString(COLUMN_CODE));
                    products.add(product);
                } while (codeQuery.moveToNext());
            } else {
                final Product product = new Product();
                product.setId("");
                product.setTitle("");
                product.setCode("");
                products.add(product);

                mIsLoading = false;
                mIsLastPage = true;
                mCurrentPage = TOTAL_PAGES;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            titleQuery.close();
            codeQuery.close();
            db.close();
        }

        return products;
    }

    private void loadFirstPage(final String pText) {
        if (BuildConfig.DEBUG) {
            Log.d("Loading...", "loadFirstPage: ");
        }

        final List<Product> products = search(pText, mCurrentPage);
        mProgressBar.setVisibility(View.GONE);
        mSearchAdapter.addAll(products);

        if (BuildConfig.DEBUG) {
            Log.d("Products count", String.valueOf(mSearchAdapter.getItemCount()));
        }

        if (mCurrentPage < TOTAL_PAGES && mSearchAdapter.getItemCount() >= ITEMS_ON_PAGE) {
            mSearchAdapter.addLoadingFooter();
        } else {
            mIsLastPage = true;
        }
    }

    private void loadNextPage(final String pText) {
        if (BuildConfig.DEBUG) {
            Log.d("Loading...", "loadNextPage: " + mCurrentPage);
        }

        final List<Product> products = search(pText, mCurrentPage * ITEMS_ON_PAGE);
        mSearchAdapter.removeLoadingFooter();
        mIsLoading = false;
        mSearchAdapter.addAll(products);

        if (mCurrentPage != TOTAL_PAGES) {
            mSearchAdapter.addLoadingFooter();
        } else {
            mIsLastPage = true;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ViewModelProviders.of(this).get(SearchViewModel.class);
        // TODO: Use the ViewModel
    }
}