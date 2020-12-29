package ru.SidorenkovIvan.MyApplication.ui.Catalog;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.Objects;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.SidorenkovIvan.MyApplication.Category;
import ru.SidorenkovIvan.MyApplication.R;
import ru.SidorenkovIvan.MyApplication.ui.Categories.Categories;

public class CatalogAdapter extends RecyclerView.Adapter<CatalogAdapter.ViewHolder> {

    private final List<Category> mCategories;
    private final FragmentManager mFragmentManager;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public Button catalogButton;

        public ViewHolder(final View pItemView) {
            super(pItemView);
            catalogButton = pItemView.findViewById(R.id.catalogButton);
        }
    }

    @Override
    public int getItemViewType(final int pPosition) {
        return R.layout.item_catalog;
    }

    public CatalogAdapter(final FragmentManager pFragmentManager, final List<Category> pCategories) {
        mFragmentManager = pFragmentManager;
        mCategories = pCategories;
    }

    @NotNull
    @Override
    public CatalogAdapter.ViewHolder onCreateViewHolder(final ViewGroup pParent, final int pViewType) {
        Context context = pParent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View catalogView = inflater.inflate(R.layout.item_catalog, pParent, false);

        return new ViewHolder(catalogView);
    }

    @Override
    public void onBindViewHolder(final CatalogAdapter.ViewHolder pHolder, final int pPosition) {
        Category category = mCategories.get(pPosition);
        String title = category.getTitle();

        Button button = pHolder.catalogButton;
        button.setText(title);

        button.setOnClickListener(v -> {
            Categories categories = new Categories();
            Bundle bundle = new Bundle();
            bundle.putString("categoryID", category.getId());
            bundle.putString("categoryTitle", title);
            categories.setArguments(bundle);
            Objects.requireNonNull(mFragmentManager).beginTransaction().replace(R.id.nav_host_fragment, categories).addToBackStack(null).commit();
        });
    }


    @Override
    public int getItemCount() {
        return mCategories.size();
    }
}
