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

    private static final String mDbName = "data.sqlite";
    private String mDbPath;

    private ProductAdapter mProductAdapter;
    private ProgressBar mProgressBar;

    private String mCategoryId;
    private boolean mIsLoading;
    private boolean mIsLastPage;
    private final int mTotalPages = 3;
    private int mCurrentPage;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.categories_fragment, container, false);

        mDbPath = requireContext().getApplicationInfo().dataDir + "/" + mDbName;
        FragmentManager fragmentManager = getFragmentManager();
        mCurrentPage = 0;
        mIsLoading = false;
        mIsLastPage = false;

        Bundle bundle = getArguments();
        mCategoryId = (String) Objects.requireNonNull(bundle).get("categoryID");
        String categoryTitle = (String) bundle.get("categoryTitle");

        TextView textViewCategory = view.findViewById(R.id.textViewCategory);
        textViewCategory.setText(categoryTitle);

        RecyclerView recyclerViewProducts = view.findViewById(R.id.recyclerViewProducts);
        mProgressBar = view.findViewById(R.id.main_progress);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerViewProducts.setLayoutManager(layoutManager);

        mProductAdapter = new ProductAdapter(fragmentManager);
        recyclerViewProducts.setAdapter(mProductAdapter);

        recyclerViewProducts.addOnScrollListener(new PaginationScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                mIsLoading = true;
                mCurrentPage += 1;

                new Handler().postDelayed(() -> loadNextPage(), 100);
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

        loadFirstPage();

        return view;
    }

    public ArrayList<String> getProductsId(final String pId, final String pDbPath, final int pOffset) {
        ArrayList<String> productId = new ArrayList<>();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pDbPath, null, SQLiteDatabase.OPEN_READONLY);
        Cursor query = db.rawQuery("SELECT DISTINCT product_id FROM category_product WHERE category_id = '" + pId + "' LIMIT 10 OFFSET '" + pOffset + "'", null);
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
        List<Product> products = DBController.getProducts(mDbPath, getProductsId(mCategoryId, mDbPath, mCurrentPage));
        mProgressBar.setVisibility(View.GONE);
        mProductAdapter.addAll(products);

        if (mCurrentPage <= mTotalPages) mProductAdapter.addLoadingFooter();
        else mIsLastPage = true;
    }

    private void loadNextPage() {
        Log.d("Loading...", "loadNextPage: " + mCurrentPage);
        List<Product> products = DBController.getProducts(mDbPath, getProductsId(mCategoryId, mDbPath, mCurrentPage * 10));

        mProductAdapter.removeLoadingFooter();
        mIsLoading = false;

        mProductAdapter.addAll(products);

        if (mCurrentPage != mTotalPages) mProductAdapter.addLoadingFooter();
        else mIsLastPage = true;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ViewModelProviders.of(this).get(CategoriesViewModel.class);
        // TODO: Use the ViewModel
    }
}