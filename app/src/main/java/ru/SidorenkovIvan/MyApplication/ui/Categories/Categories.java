package ru.SidorenkovIvan.MyApplication.ui.Categories;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.SidorenkovIvan.MyApplication.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import ru.SidorenkovIvan.MyApplication.Product;

public class Categories extends Fragment {

    private static final String DBname = "data.sqlite";
    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    private final List<String> productId = new ArrayList<>();
    int start = 0, end = 10;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.categories_fragment, container, false);

        String dbPath = requireContext().getApplicationInfo().dataDir + "/" + DBname;
        FragmentManager fragmentManager = getFragmentManager();

        Bundle bundle = getArguments();
        String id = (String) Objects.requireNonNull(bundle).get("categoryID");
        String categoryTitle = (String) bundle.get("categoryTitle");

        getProductsId(id, dbPath);

        TextView textViewCategory = view.findViewById(R.id.textViewCategory);
        textViewCategory.setText(categoryTitle);

        RecyclerView recyclerViewProducts = view.findViewById(R.id.recyclerViewProducts);
        if (end > productId.size()) {
            end = productId.size() - 1;
        }
        List<Product> products = Product.getProducts(dbPath, productId.subList(start, end));

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        ProductAdapter productAdapter = new ProductAdapter(fragmentManager, products);

        recyclerViewProducts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) { //check for scroll down
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            loading = false;
                            Log.v("...", "Last Item Wow !");
                            // Do pagination.. i.e. fetch new data
                            start += 10;
                            end += 10;
                            if (end > productId.size()) {
                                start -= 10;
                                end = productId.size() - 1;
                            }
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            products.addAll(Product.getProducts(dbPath, productId.subList(start, end)));
                            recyclerView.post(productAdapter::notifyDataSetChanged);
                            loading = true;
                        }
                    }
                }
            }
        });

        recyclerViewProducts.setLayoutManager(layoutManager);
        recyclerViewProducts.setAdapter(productAdapter);

        return view;
    }

    public void getProductsId(String ID, String dbPath) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY);
        Cursor query = db.rawQuery("SELECT DISTINCT product_id FROM category_product WHERE category_id = '" + ID + "'", null);
        query.moveToFirst();
        while (!query.isAfterLast()) {
            productId.add(query.getString(0));
            query.moveToNext();
        }
        query.close();
        db.close();

        Log.i("Products", String.valueOf(productId.size()));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ViewModelProviders.of(this).get(CategoriesViewModel.class);
        // TODO: Use the ViewModel
    }
}