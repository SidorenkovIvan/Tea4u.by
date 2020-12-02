package ru.SidorenkovIvan.MyApplication.ui.Search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.Objects;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.SidorenkovIvan.MyApplication.Product;
import ru.SidorenkovIvan.MyApplication.R;
import ru.SidorenkovIvan.MyApplication.ui.ProductPage.ProductPage;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    private final List<Product> mProducts;
    private final FragmentManager fragmentManager;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public Button buttonSearchProduct;

        public ViewHolder(View itemView) {
            super(itemView);
            buttonSearchProduct = itemView.findViewById(R.id.buttonSearchProduct);
        }
    }

    public SearchAdapter (FragmentManager manager, List<Product> products) {
        fragmentManager = manager;
        mProducts = products;
    }

    @Override
    public int getItemViewType (final int position) {
        return R.layout.item_search;
    }

    @NotNull
    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View searchView = inflater.inflate(R.layout.item_search, parent, false);

        return new ViewHolder(searchView);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.ViewHolder holder, int position) {
        Product product = mProducts.get(position);

        Button buttonSearchProduct = holder.buttonSearchProduct;
        buttonSearchProduct.setText(product.toString());

        buttonSearchProduct.setOnClickListener(v -> {
            ProductPage pageOfProduct = new ProductPage();
            Bundle bundle = new Bundle();
            bundle.putString("productID", product.getId());
            pageOfProduct.setArguments(bundle);
            Objects.requireNonNull(fragmentManager).beginTransaction().replace(R.id.nav_host_fragment, pageOfProduct).addToBackStack(null).commit();
        });
    }

    @Override
    public int getItemCount() {
        return mProducts.size();
    }
}
