package ru.SidorenkovIvan.MyApplication.ui.Categories;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
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

public class ProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<Product> mProducts;
    private final FragmentManager mFragmentManager;

    private boolean mIsLoadingAdded = false;

    private final int ITEM = 0;
    private final int LOADING = 1;

    public ProductAdapter (FragmentManager pFragmentManager) {
        mFragmentManager = pFragmentManager;
        mProducts = new ArrayList<>();
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final ViewGroup pParent, final int pViewType) {
        LayoutInflater inflater = LayoutInflater.from(pParent.getContext());
        RecyclerView.ViewHolder viewHolder = null;

        switch (pViewType) {
            case ITEM:
                viewHolder = getViewHolder(pParent, inflater);
                break;
            case LOADING:
                View viewProgress = inflater.inflate(R.layout.item_progress, pParent, false);
                viewHolder = new LoadingVH(viewProgress);
                break;
        }

        return Objects.requireNonNull(viewHolder);
    }

    private RecyclerView.ViewHolder getViewHolder(final ViewGroup pParent, final LayoutInflater pInflater) {
        RecyclerView.ViewHolder viewHolder;
        View view = pInflater.inflate(R.layout.item_product, pParent, false);
        viewHolder = new ProductVH(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder pHolder, final int pPosition) {
        Product product = mProducts.get(pPosition);

        switch (getItemViewType(pPosition)) {
            case ITEM:
                ProductVH productVH = (ProductVH) pHolder;
                Bitmap image = product.getImage();
                String title = product.getTitle();

                productVH.mImageButton.setImageBitmap(image);
                productVH.mTextView.setText(title);

                productVH.mImageButton.setOnClickListener(v -> {
                    ProductPage pageOfProduct = new ProductPage();
                    Bundle bundle = new Bundle();
                    bundle.putString("productID", product.getId());
                    pageOfProduct.setArguments(bundle);
                    Objects.requireNonNull(mFragmentManager).beginTransaction().replace(R.id.nav_host_fragment, pageOfProduct).addToBackStack(null).commit();
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

        int position = mProducts.size() - 1;
        Product item = getItem(position);

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
        public ImageButton mImageButton;
        public TextView mTextView;

        public ProductVH(final View pItemView) {
            super(pItemView);
            mImageButton = pItemView.findViewById(R.id.imageButtonProduct);
            mTextView = pItemView.findViewById(R.id.textViewImageButton);
        }
    }

    protected static class LoadingVH extends RecyclerView.ViewHolder {
        public LoadingVH(final View pItemView) {
            super(pItemView);
        }
    }
}
