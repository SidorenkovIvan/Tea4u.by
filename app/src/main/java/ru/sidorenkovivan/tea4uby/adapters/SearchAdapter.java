package ru.sidorenkovivan.tea4uby.adapters;

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
import ru.sidorenkovivan.tea4uby.R;
import ru.sidorenkovivan.tea4uby.entities.Product;
import ru.sidorenkovivan.tea4uby.ui.productpage.ProductPageFragment;
import ru.sidorenkovivan.tea4uby.util.Constants;

public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Product> mProducts;
    private final FragmentManager mFragmentManager;
    private boolean mIsLoadingAdded = false;
    private final Constants mConstants = new Constants();

    public SearchAdapter(final FragmentManager pFragmentManager) {
        mFragmentManager = pFragmentManager;
        mProducts = new ArrayList<>();
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final ViewGroup pParent, final int pViewType) {
        final LayoutInflater inflater = LayoutInflater.from(pParent.getContext());
        RecyclerView.ViewHolder viewHolder = null;

        if (pViewType == mConstants.ITEM) {
            viewHolder = getViewHolder(pParent, inflater);
        } else if (pViewType == mConstants.LOADING) {
            final View viewProgress = inflater.inflate(R.layout.item_progress, pParent, false);
            viewHolder = new LoadingViewHolder(viewProgress);
        }

        return Objects.requireNonNull(viewHolder);
    }

    private RecyclerView.ViewHolder getViewHolder(final ViewGroup pParent, final LayoutInflater pInflater) {
        final RecyclerView.ViewHolder viewHolder;
        final View view = pInflater.inflate(R.layout.item_search, pParent, false);
        viewHolder = new ProductViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder pHolder, final int pPosition) {
        final Product product = mProducts.get(pHolder.getAdapterPosition());

        if (getItemViewType(pPosition) == mConstants.ITEM) {
            final ProductViewHolder productViewHolder = (ProductViewHolder) pHolder;
            productViewHolder.buttonSearchProduct.setText(product.toString());
            productViewHolder.buttonSearchProduct.setOnClickListener(view -> {
                final ProductPageFragment productPageFragment = new ProductPageFragment();
                final Bundle bundle = new Bundle();
                bundle.putString(mConstants.PRODUCT_ID, product.getId());
                productPageFragment.setArguments(bundle);
                Objects.requireNonNull(mFragmentManager).beginTransaction().replace(R.id.nav_host_fragment, productPageFragment).addToBackStack(null).commit();
            });
        }
    }

    @Override
    public int getItemViewType(final int pPosition) {
        return (pPosition == mProducts.size() - 1 && mIsLoadingAdded) ? mConstants.LOADING : mConstants.ITEM;
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

    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        final Button buttonSearchProduct;

        public ProductViewHolder(final View pItemView) {
            super(pItemView);

            buttonSearchProduct = pItemView.findViewById(R.id.buttonSearchProduct);
        }
    }

    protected static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public LoadingViewHolder(final View pItemView) {
            super(pItemView);
        }
    }
}
