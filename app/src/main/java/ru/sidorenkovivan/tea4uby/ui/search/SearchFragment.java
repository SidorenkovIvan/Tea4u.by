package ru.sidorenkovivan.tea4uby.ui.search;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;
import java.util.ArrayList;
import java.util.List;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.sidorenkovivan.tea4uby.BuildConfig;
import ru.sidorenkovivan.tea4uby.R;
import ru.sidorenkovivan.tea4uby.adapters.SearchAdapter;
import ru.sidorenkovivan.tea4uby.entities.Product;
import ru.sidorenkovivan.tea4uby.util.Constants;
import ru.sidorenkovivan.tea4uby.util.PaginationScrollListener;

public class SearchFragment extends Fragment {

    private final String TAG = "Search fragment";
    private String mDbPath;
    private String mText;
    private FragmentManager mFragmentManager;
    private SearchView mSearchView;
    private SearchAdapter mSearchAdapter;
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerViewSearch;
    private LinearLayoutManager mLinearLayoutManager;
    private final int TOTAL_PAGES = 15;
    private final int ITEMS_ON_PAGE = 15;
    private final int ZERO_PAGE = 0;
    private boolean mIsLoading;
    private boolean mIsLastPage;
    private int mCurrentPage;
    private final Constants mConstants = new Constants();

    @Override
    public View onCreateView(final LayoutInflater pInflater,
                             final ViewGroup pContainer,
                             final Bundle pSavedInstanceState) {
        final View view = pInflater.inflate(R.layout.fragment_search, pContainer, false);

        mFragmentManager = getFragmentManager();
        mDbPath = requireContext().getApplicationInfo().dataDir + getString(R.string.databaseName);
        mCurrentPage = ZERO_PAGE;
        mIsLoading = false;
        mIsLastPage = false;

        initViews(view);
        mRecyclerViewSearch.setLayoutManager(mLinearLayoutManager);
        mRecyclerViewSearch.setAdapter(mSearchAdapter);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

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

                mSearchAdapter = new SearchAdapter(mFragmentManager);
                mRecyclerViewSearch.setAdapter(mSearchAdapter);
                mText = pNewText;
                mCurrentPage = ZERO_PAGE;
                loadFirstPage(mText);

                return false;
            }
        });

        mRecyclerViewSearch.addOnScrollListener(new PaginationScrollListener(mLinearLayoutManager) {

            @Override
            protected void loadMoreItems() {
                mIsLoading = true;
                mCurrentPage += 1;
                loadNextPage(mText);
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

    private void initViews(final View pView) {
        mSearchView = pView.findViewById(R.id.searchView);
        mRecyclerViewSearch = pView.findViewById(R.id.recyclerViewSearch);
        mProgressBar = pView.findViewById(R.id.searchProgressBar);
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mSearchAdapter = new SearchAdapter(mFragmentManager);
    }

    private ArrayList<Product> search(final String pKeyword, final int pOffset) {
        final ArrayList<Product> products = new ArrayList<>();
        final SQLiteDatabase db = SQLiteDatabase.openDatabase(mDbPath, null, SQLiteDatabase.OPEN_READONLY);
        final Cursor titleQuery = db.rawQuery("SELECT product.product_id, product.productTitle, product.code FROM product WHERE productTitle LIKE ? LIMIT 15 OFFSET '" + pOffset + "'", new String[]{"%" + pKeyword + "%"});
        final Cursor codeQuery = db.rawQuery("SELECT product.product_id, product.productTitle, product.code FROM product WHERE code LIKE ? LIMIT 15 OFFSET '" + pOffset + "'", new String[]{"%" + pKeyword + "%"});
        try {
            if (titleQuery.moveToFirst()) {
                do {
                    final Product product = new Product();
                    product.setId(titleQuery.getString(mConstants.COLUMN_ZERO));
                    product.setTitle(titleQuery.getString(mConstants.COLUMN_ONE));
                    product.setCode(titleQuery.getString(mConstants.COLUMN_TWO));
                    products.add(product);
                } while (titleQuery.moveToNext());
            } else if (codeQuery.moveToFirst()) {
                do {
                    final Product product = new Product();
                    product.setId(codeQuery.getString(mConstants.COLUMN_ZERO));
                    product.setTitle(codeQuery.getString(mConstants.COLUMN_ONE));
                    product.setCode(codeQuery.getString(mConstants.COLUMN_TWO));
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
}