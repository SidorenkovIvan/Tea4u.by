package ru.sidorenkovivan.myapplication.recyclerviewadapter;

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
import ru.sidorenkovivan.myapplication.entities.Product;
import ru.sidorenkovivan.myapplication.R;
import ru.sidorenkovivan.myapplication.ui.productpage.ProductPage;

public class AdapterSearch extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Product> mProducts;
    private final FragmentManager mFragmentManager;

    private boolean mIsLoadingAdded = false;

    private final int ITEM = 0;
    private final int LOADING = 1;

    public AdapterSearch(final FragmentManager pFragmentManager) {
        mFragmentManager = pFragmentManager;
        mProducts = new ArrayList<>();
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final ViewGroup pParent, final int pViewType) {
        final LayoutInflater inflater = LayoutInflater.from(pParent.getContext());
        RecyclerView.ViewHolder viewHolder = null;

        switch (pViewType) {
            case ITEM:
                viewHolder = getViewHolder(pParent, inflater);
                break;
            case LOADING:
                final View viewProgress = inflater.inflate(R.layout.item_progress, pParent, false);
                viewHolder = new LoadingVH(viewProgress);
                break;
        }

        return Objects.requireNonNull(viewHolder);
    }

    private RecyclerView.ViewHolder getViewHolder(final ViewGroup pParent, final LayoutInflater pInflater) {
        final RecyclerView.ViewHolder viewHolder;
        final View view = pInflater.inflate(R.layout.item_search, pParent, false);
        viewHolder = new ProductVH(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder pHolder, final int pPosition) {
        final Product product = mProducts.get(pHolder.getAdapterPosition());

        switch (getItemViewType(pPosition)) {
            case ITEM:
                final ProductVH productVH = (ProductVH) pHolder;
                productVH.buttonSearchProduct.setText(product.toString());

                productVH.buttonSearchProduct.setOnClickListener(v -> {
                    final ProductPage productPage = new ProductPage();
                    final Bundle bundle = new Bundle();
                    bundle.putString("productID", product.getId());
                    productPage.setArguments(bundle);
                    Objects.requireNonNull(mFragmentManager).beginTransaction().replace(R.id.nav_host_fragment, productPage).addToBackStack(null).commit();
                });
                break;
            case LOADING:
                break;
        }
    }

    @Override
    public int getItemViewType(final int pPosition) {
        return (pPosition == mProducts.size() - 1 && mIsLoadingAdded) ? LOADING : ITEM;
    }

    public void addLoadingFooter() {
        mIsLoadingAdded = true;
        add(new Product());
    }

    public void removeLoadingFooter() {
        mIsLoadingAdded = false;

        final int position = mProducts.size() - 1;
        final Product item = getItem(position);

        if (item != null) {
            mProducts.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void add(final Product pProduct) {
        mProducts.add(pProduct);
        notifyItemInserted(mProducts.size() - 1);
    }

    public void addAll(final List<Product> pProductList) {
        for (Product product : pProductList) {
            add(product);
        }
    }

    public Product getItem(final int pPosition) {
        return mProducts.get(pPosition);
    }


    @Override
    public int getItemCount() {
        return mProducts.size();
    }

    public static class ProductVH extends RecyclerView.ViewHolder {

        final Button buttonSearchProduct;

        public ProductVH(final View pItemView) {
            super(pItemView);

            buttonSearchProduct = pItemView.findViewById(R.id.buttonSearchProduct);
        }
    }

    protected static class LoadingVH extends RecyclerView.ViewHolder {
        public LoadingVH(final View pItemView) {
            super(pItemView);
        }
    }
}
