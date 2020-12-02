package ru.SidorenkovIvan.MyApplication.ui.home;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.SidorenkovIvan.MyApplication.Category;
import ru.SidorenkovIvan.MyApplication.DBController;
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

    @SuppressLint("ResourceType")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        String dbPath = requireContext().getApplicationInfo().dataDir + "/" + DBname;
        FragmentManager fragmentManager = getFragmentManager();

        //Categories buttons
        RecyclerView categoriesView = view.findViewById(R.id.recyclerViewSmallCategories);
        List<Category> categories = DBController.getNotEmptyCategories(dbPath);
        HomeCategoriesAdapter catAdapter = new HomeCategoriesAdapter(fragmentManager, categories);
        categoriesView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        categoriesView.setAdapter(catAdapter);

        //Find new products in shop
//        RecyclerView productsView = view.findViewById(R.id.recyclerViewNewProducts);
//        List<Product> products = DBController.getProducts(dbPath, getProductsId());
//        ProductAdapter productAdapter = new ProductAdapter(fragmentManager, products);
//        productsView.setLayoutManager(new GridLayoutManager(getContext(), 2));
//        productsView.setAdapter(productAdapter);

        return view;
    }

    private ArrayList<String> getProductsId() {
        ArrayList<String> newProductsId = new ArrayList<>();
        String dbPath = requireContext().getApplicationInfo().dataDir + "/" + DBname;
        SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY);
        Cursor query = db.rawQuery("SELECT product.product_id FROM product INNER JOIN latest ON product.product_id = latest.product_id", null);
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ViewModelProviders.of(this).get(HomeViewModel.class);
        // TODO: Use the ViewModel
    }
}