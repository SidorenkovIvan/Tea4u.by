package ru.SidorenkovIvan.MyApplication.ui.Search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.SidorenkovIvan.MyApplication.Product;
import ru.SidorenkovIvan.MyApplication.R;
import ru.SidorenkovIvan.MyApplication.ui.ProductPage.ProductPage;

public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Product> mProducts;
    private final FragmentManager fragmentManager;

    private boolean isLoadingAdded = false;

    private static final int ITEM = 0;
    private static final int LOADING = 1;


    public SearchAdapter (FragmentManager manager) {
        fragmentManager = manager;
        mProducts = new ArrayList<>();
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder = null;

        switch (viewType) {
            case ITEM:
                viewHolder = getViewHolder(parent, inflater);
                break;
            case LOADING:
                View viewProgress = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new LoadingVH(viewProgress);
                break;
        }

        return Objects.requireNonNull(viewHolder);
    }

    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        View view = inflater.inflate(R.layout.item_search, parent, false);
        viewHolder = new ProductVH(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Product product = mProducts.get(position);

        switch (getItemViewType(position)) {
            case ITEM:
                ProductVH productVH = (ProductVH) holder;
                productVH.buttonSearchProduct.setText(product.toString());

                productVH.buttonSearchProduct.setOnClickListener(v -> {
                    ProductPage productPage = new ProductPage();
                    Bundle bundle = new Bundle();
                    bundle.putString("productID", product.getId());
                    productPage.setArguments(bundle);
                    Objects.requireNonNull(fragmentManager).beginTransaction().replace(R.id.nav_host_fragment, productPage).addToBackStack(null).commit();
                });
                break;
            case LOADING:
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position == mProducts.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new Product());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = mProducts.size() - 1;
        Product item = getItem(position);

        if (item != null) {
            mProducts.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void add(Product mc) {
        mProducts.add(mc);
        notifyItemInserted(mProducts.size() - 1);
    }

    public void addAll(List<Product> productList) {
        for (Product product : productList) {
            add(product);
        }
    }

    public Product getItem(int position) {
        return mProducts.get(position);
    }


    @Override
    public int getItemCount() {
        return mProducts.size();
    }

    public static class ProductVH extends RecyclerView.ViewHolder {
        public Button buttonSearchProduct;

        public ProductVH(View itemView) {
            super(itemView);
            buttonSearchProduct = itemView.findViewById(R.id.buttonSearchProduct);
        }
    }

    protected static class LoadingVH extends RecyclerView.ViewHolder {

        public LoadingVH(View itemView) {
            super(itemView);
        }
    }
}
