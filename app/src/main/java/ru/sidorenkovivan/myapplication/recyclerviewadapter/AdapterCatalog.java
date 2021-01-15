package ru.sidorenkovivan.myapplication.recyclerviewadapter;

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
import ru.sidorenkovivan.myapplication.entities.Category;
import ru.sidorenkovivan.myapplication.R;
import ru.sidorenkovivan.myapplication.ui.categories.Categories;

public class AdapterCatalog extends RecyclerView.Adapter<AdapterCatalog.ViewHolder> {

    private final List<Category> mCategories;
    private final FragmentManager mFragmentManager;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final Button catalogButton;

        public ViewHolder(final View pItemView) {
            super(pItemView);

            catalogButton = pItemView.findViewById(R.id.catalogButton);
        }
    }

    @Override
    public int getItemViewType(final int pPosition) {
        return R.layout.item_catalog;
    }

    public AdapterCatalog(final FragmentManager pFragmentManager, final List<Category> pCategories) {
        mFragmentManager = pFragmentManager;
        mCategories = pCategories;
    }

    @NotNull
    @Override
    public AdapterCatalog.ViewHolder onCreateViewHolder(final ViewGroup pParent, final int pViewType) {
        final Context context = pParent.getContext();
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View catalogView = inflater.inflate(R.layout.item_catalog, pParent, false);

        return new ViewHolder(catalogView);
    }

    @Override
    public void onBindViewHolder(final AdapterCatalog.ViewHolder pHolder, final int pPosition) {
        final Category category = mCategories.get(pHolder.getAdapterPosition());
        final String title = category.getTitle();

        final Button button = pHolder.catalogButton;
        button.setText(title);

        button.setOnClickListener(v -> {
            final Categories categories = new Categories();
            final Bundle bundle = new Bundle();
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
