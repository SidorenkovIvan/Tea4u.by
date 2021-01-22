package ru.sidorenkovivan.tea4uby.ui.categories;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.List;
import java.util.Objects;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.sidorenkovivan.tea4uby.BuildConfig;
import ru.sidorenkovivan.tea4uby.R;
import ru.sidorenkovivan.tea4uby.adapters.ProductAdapter;
import ru.sidorenkovivan.tea4uby.entities.Product;
import ru.sidorenkovivan.tea4uby.util.Constants;
import ru.sidorenkovivan.tea4uby.util.PaginationScrollListener;
import ru.sidorenkovivan.tea4uby.util.database.DBController;

public class CategoriesFragment extends Fragment {

    private String mDbPath;
    private String mCategoryId;
    private ProductAdapter mProductAdapter;
    private ProgressBar mProgressBar;
    private FragmentManager mFragmentManager;
    private TextView mTextViewCategory;
    private RecyclerView mRecyclerViewProducts;
    private GridLayoutManager mLayoutManager;
    private final DBController mDbController = new DBController();
    private final Constants mConstants = new Constants();
    private final int TOTAL_PAGES = 3;
    private final int ITEMS_ON_PAGE = 10;
    private boolean mIsLoading;
    private boolean mIsLastPage;
    private int mCurrentPage;

    @Override
    public View onCreateView(final LayoutInflater pInflater,
                             final ViewGroup pContainer,
                             final Bundle pSavedInstanceState) {
        final View view = pInflater.inflate(R.layout.fragment_categories, pContainer, false);

        mCurrentPage = 0;
        mIsLoading = false;
        mIsLastPage = false;
        mFragmentManager = getFragmentManager();
        mDbPath = requireContext().getApplicationInfo().dataDir + getString(R.string.databaseName);
        final Bundle bundle = getArguments();
        mCategoryId = (String) Objects.requireNonNull(bundle).get(mConstants.CATEGORY_ID);
        final String categoryTitle = (String) bundle.get(mConstants.CATEGORY_TITLE);

        initViews(view);
        mTextViewCategory.setText(categoryTitle);
        mRecyclerViewProducts.setLayoutManager(mLayoutManager);
        mRecyclerViewProducts.setAdapter(mProductAdapter);
        mRecyclerViewProducts.addOnScrollListener(new PaginationScrollListener(mLayoutManager) {

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
        mRecyclerViewProducts = pView.findViewById(R.id.recyclerViewProducts);
        mTextViewCategory = pView.findViewById(R.id.textViewCategory);
        mProgressBar = pView.findViewById(R.id.main_progress);

        mLayoutManager = new GridLayoutManager(getContext(), mConstants.GRID_LAYOUT_COLUMNS);
        mProductAdapter = new ProductAdapter(mFragmentManager);
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
}