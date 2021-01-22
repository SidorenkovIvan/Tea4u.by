package ru.sidorenkovivan.tea4uby.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import java.util.List;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.sidorenkovivan.tea4uby.BuildConfig;
import ru.sidorenkovivan.tea4uby.R;
import ru.sidorenkovivan.tea4uby.adapters.HomeCategoriesAdapter;
import ru.sidorenkovivan.tea4uby.adapters.ProductAdapter;
import ru.sidorenkovivan.tea4uby.entities.Category;
import ru.sidorenkovivan.tea4uby.entities.Product;
import ru.sidorenkovivan.tea4uby.util.Constants;
import ru.sidorenkovivan.tea4uby.util.PaginationScrollListener;
import ru.sidorenkovivan.tea4uby.util.database.DBController;

public class HomeFragment extends Fragment {

    private String mDbPath;
    private ProductAdapter mProductAdapter;
    private HomeCategoriesAdapter mHomeCategoriesAdapter;
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerViewCategories;
    private RecyclerView mRecyclerViewProducts;
    private FragmentManager mFragmentManager;
    private LinearLayoutManager mLinearLayoutManager;
    private GridLayoutManager mGridLayoutManager;
    private final DBController mDbController = new DBController();
    private final Constants mConstants = new Constants();
    private final int TOTAL_PAGES = 1;
    private final int ITEMS_ON_PAGE = 10;
    private boolean mIsLoading;
    private boolean mIsLastPage;
    private int mCurrentPage;

    public View onCreateView(final LayoutInflater pInflater,
                             final ViewGroup pContainer,
                             final Bundle pSavedInstanceState) {
        final View view = pInflater.inflate(R.layout.fragment_home, pContainer, false);
        mFragmentManager = getFragmentManager();
        mDbPath = requireContext().getApplicationInfo().dataDir + getString(R.string.databaseName);
        mCurrentPage = 0;
        mIsLoading = false;
        mIsLastPage = false;

        initViews(view);
        mRecyclerViewCategories.setLayoutManager(mLinearLayoutManager);
        mRecyclerViewCategories.setAdapter(mHomeCategoriesAdapter);
        mRecyclerViewProducts.setLayoutManager(mGridLayoutManager);
        mRecyclerViewProducts.setAdapter(mProductAdapter);
        mRecyclerViewProducts.addOnScrollListener(new PaginationScrollListener(mGridLayoutManager) {

            @Override
            protected void loadMoreItems() {
                mIsLoading = true;
                mCurrentPage += 1;
                loadNextPage();
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

    private void initViews(final View pView) {
        mRecyclerViewCategories = pView.findViewById(R.id.recyclerViewSmallCategories);
        mRecyclerViewProducts = pView.findViewById(R.id.recyclerViewNewProducts);
        mProgressBar = pView.findViewById(R.id.home_progress);

        final List<Category> categories = mDbController.getNotEmptyCategories(mDbPath);
        mHomeCategoriesAdapter = new HomeCategoriesAdapter(mFragmentManager, categories);
        mLinearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mGridLayoutManager = new GridLayoutManager(getContext(), mConstants.GRID_LAYOUT_COLUMNS);
        mProductAdapter = new ProductAdapter(mFragmentManager);
    }

    private void loadFirstPage() {
        if (BuildConfig.DEBUG) {
            Log.d("Loading...", "loadFirstPage: ");
        }

        final List<Product> products = mDbController.getProducts(mDbPath, mDbController.getLastProductsId(mDbPath, mCurrentPage));
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

        final List<Product> products = mDbController.getProducts(mDbPath, mDbController.getLastProductsId(mDbPath,mCurrentPage * ITEMS_ON_PAGE));
        mProductAdapter.removeLoadingFooter();
        mIsLoading = false;
        mProductAdapter.addAll(products);

        if (mCurrentPage != TOTAL_PAGES) {
            mProductAdapter.addLoadingFooter();
        } else {
            mIsLastPage = true;
        }
    }
}