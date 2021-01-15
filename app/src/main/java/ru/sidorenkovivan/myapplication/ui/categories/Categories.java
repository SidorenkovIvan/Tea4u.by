package ru.sidorenkovivan.myapplication.ui.categories;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;
import java.util.Objects;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.sidorenkovivan.myapplication.BuildConfig;
import ru.sidorenkovivan.myapplication.util.database.DBController;
import ru.sidorenkovivan.myapplication.util.PaginationScrollListener;
import ru.sidorenkovivan.myapplication.entities.Product;
import ru.sidorenkovivan.myapplication.R;
import ru.sidorenkovivan.myapplication.recyclerviewadapter.AdapterProduct;

public class Categories extends Fragment {

    private String mDbPath;
    private String mCategoryId;
    private final DBController mDbController = new DBController();

    private AdapterProduct mProductAdapter;
    private ProgressBar mProgressBar;

    private final int TOTAL_PAGES = 3;
    private final int ITEMS_ON_PAGE = 10;
    private boolean mIsLoading;
    private boolean mIsLastPage;
    private int mCurrentPage;

    @Override
    public View onCreateView(@NonNull final LayoutInflater pInflater,
                             @Nullable final ViewGroup pContainer,
                             @Nullable final Bundle pSavedInstanceState) {
        final View view = pInflater.inflate(R.layout.fragment_categories, pContainer, false);

        final int COLUMNS = 2;
        final FragmentManager fragmentManager = getFragmentManager();
        mDbPath = requireContext().getApplicationInfo().dataDir + getString(R.string.databaseName);
        mCurrentPage = 0;
        mIsLoading = false;
        mIsLastPage = false;

        final Bundle bundle = getArguments();
        mCategoryId = (String) Objects.requireNonNull(bundle).get("categoryID");
        final String categoryTitle = (String) bundle.get("categoryTitle");

        final TextView textViewCategory = view.findViewById(R.id.textViewCategory);
        textViewCategory.setText(categoryTitle);

        final RecyclerView recyclerViewProducts = view.findViewById(R.id.recyclerViewProducts);
        mProgressBar = view.findViewById(R.id.main_progress);

        final GridLayoutManager layoutManager = new GridLayoutManager(getContext(), COLUMNS);
        recyclerViewProducts.setLayoutManager(layoutManager);

        mProductAdapter = new AdapterProduct(fragmentManager);
        recyclerViewProducts.setAdapter(mProductAdapter);

        recyclerViewProducts.addOnScrollListener(new PaginationScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                mIsLoading = true;
                mCurrentPage += 1;

                final Handler handler = new Handler();
                handler.postDelayed(() -> loadNextPage(), 50);
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

        loadFirstPage();

        return view;
    }

    private void loadFirstPage() {
        if (BuildConfig.DEBUG) {
            Log.d("Loading...", "loadFirstPage: ");
        }

        final List<Product> products = mDbController.getProducts(mDbPath, mDbController.getProductsId(mCategoryId, mDbPath, mCurrentPage));
        mProgressBar.setVisibility(View.GONE);
        mProductAdapter.addAll(products);
        if (products.size() != ITEMS_ON_PAGE) {
            mCurrentPage = TOTAL_PAGES;
        }

        if (mCurrentPage < TOTAL_PAGES) {
            mProductAdapter.addLoadingFooter();
        } else {
            mIsLastPage = true;
        }
    }

    private void loadNextPage() {
        if (BuildConfig.DEBUG) {
            Log.d("Loading...", "loadNextPage: " + mCurrentPage);
        }

        final List<Product> products = mDbController.getProducts(mDbPath, mDbController.getProductsId(mCategoryId, mDbPath, mCurrentPage * ITEMS_ON_PAGE));
        mProductAdapter.removeLoadingFooter();
        mIsLoading = false;
        mProductAdapter.addAll(products);

        if (mCurrentPage != TOTAL_PAGES) {
            mProductAdapter.addLoadingFooter();
        } else {
            mIsLastPage = true;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ViewModelProviders.of(this).get(CategoriesViewModel.class);
        // TODO: Use the ViewModel
    }
}