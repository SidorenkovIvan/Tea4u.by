package ru.SidorenkovIvan.MyApplication.ui.Search;

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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.SidorenkovIvan.MyApplication.PaginationScrollListener;
import ru.SidorenkovIvan.MyApplication.Product;
import ru.SidorenkovIvan.MyApplication.R;

public class Search extends Fragment {

    private final String TAG = "MyApp";
    private String mDbPath;
    private String mText;

    private SearchAdapter mSearchAdapter;
    private ProgressBar mProgressBar;

    private boolean mIsLoading;
    private boolean mIsLastPage;
    private final int mTotalPages = 15;
    private int mCurrentPage;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_fragment, container, false);

        SearchView searchView = view.findViewById(R.id.searchView);
        FragmentManager fragmentManager = getFragmentManager();
        mDbPath = requireContext().getApplicationInfo().dataDir + "/" + "data.sqlite";
        mCurrentPage = 0;
        mIsLoading = false;
        mIsLastPage = false;

        RecyclerView recyclerViewSearch = view.findViewById(R.id.recyclerViewSearch);
        mProgressBar = view.findViewById(R.id.searchProgressBar);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerViewSearch.setLayoutManager(layoutManager);

        mSearchAdapter = new SearchAdapter(fragmentManager);
        recyclerViewSearch.setAdapter(mSearchAdapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String pQuery) {
                Log.i(TAG, "onQueryTextSubmit: " + pQuery);

                mSearchAdapter = new SearchAdapter(fragmentManager);
                recyclerViewSearch.setAdapter(mSearchAdapter);
                mText = pQuery;
                mCurrentPage = 0;

                loadFirstPage(mText);

                return false;
            }

            @Override
            public boolean onQueryTextChange(final String pNewText) {
                Log.i(TAG, "onQueryTextChange: " + pNewText);

                mSearchAdapter = new SearchAdapter(fragmentManager);
                recyclerViewSearch.setAdapter(mSearchAdapter);
                mText = pNewText;
                mCurrentPage = 0;

                loadFirstPage(mText);

                return false;
            }
        });

        recyclerViewSearch.addOnScrollListener(new PaginationScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                mIsLoading = true;
                mCurrentPage += 1;

                loadNextPage(mText);
            }

            @Override
            public int getTotalPageCount() {
                return mTotalPages;
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
        ArrayList<Product> products = new ArrayList<>();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(mDbPath, null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.rawQuery("SELECT product.product_id, product.productTitle, product.code FROM product WHERE productTitle LIKE ? LIMIT 15 OFFSET '" + pOffset + "'", new String[]{"%" + pKeyword + "%"});
        Cursor cursor1 = db.rawQuery("SELECT product.product_id, product.productTitle, product.code FROM product WHERE code LIKE ? LIMIT 15 OFFSET '" + pOffset + "'", new String[]{"%" + pKeyword + "%"});
        if (cursor.moveToFirst()) {
            do {
                Product product = new Product();
                product.setId(cursor.getString(0));
                product.setTitle(cursor.getString(1));
                product.setCode(cursor.getString(2));
                products.add(product);
            } while (cursor.moveToNext());

            cursor.close();
            cursor1.close();
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

            cursor.close();
            cursor1.close();
            db.close();

            return products;
        } else {
            Product product = new Product();
            product.setId("");
            product.setTitle("");
            product.setCode("");
            products.add(product);

            mIsLoading = false;
            mIsLastPage = true;
            mCurrentPage = mTotalPages;

            cursor.close();
            cursor1.close();
            db.close();

            return products;
        }
    }

    private void loadFirstPage(final String pText) {
        Log.d("Loading...", "loadFirstPage: ");
        List<Product> products = search(pText, mCurrentPage);
        mProgressBar.setVisibility(View.GONE);
        mSearchAdapter.addAll(products);

        Log.d("Products count", String.valueOf(mSearchAdapter.getItemCount()));
        if (mCurrentPage < mTotalPages && mSearchAdapter.getItemCount() >= 15)
            mSearchAdapter.addLoadingFooter();
        else
            mIsLastPage = true;
    }

    private void loadNextPage(final String pText) {
        Log.d("Loading...", "loadNextPage: " + mCurrentPage);
        List<Product> products = search(pText, mCurrentPage * 15);

        mSearchAdapter.removeLoadingFooter();
        mIsLoading = false;

        mSearchAdapter.addAll(products);

        if (mCurrentPage != mTotalPages)
            mSearchAdapter.addLoadingFooter();
        else
            mIsLastPage = true;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ViewModelProviders.of(this).get(SearchViewModel.class);
        // TODO: Use the ViewModel
    }
}