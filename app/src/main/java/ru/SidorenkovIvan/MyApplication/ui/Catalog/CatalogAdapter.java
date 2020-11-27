package ru.SidorenkovIvan.MyApplication.ui.Catalog;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import java.util.List;
import java.util.Objects;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.SidorenkovIvan.MyApplication.Category;
import ru.SidorenkovIvan.MyApplication.R;
import ru.SidorenkovIvan.MyApplication.ui.Categories.Categories;

public class CatalogAdapter extends RecyclerView.Adapter<CatalogAdapter.ViewHolder> {

    private final List<Category> mCategory;
    private final FragmentManager fragmentManager;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public Button catalogButton;

        public ViewHolder(View itemView) {
            super(itemView);
            catalogButton = itemView.findViewById(R.id.catalogButton);
        }
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.item_catalog;
    }

    public CatalogAdapter(FragmentManager manager, List<Category> categoriesIdAndTitles) {
        fragmentManager = manager;
        mCategory = categoriesIdAndTitles;
    }

    @Override
    public CatalogAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View catalogView = inflater.inflate(R.layout.item_catalog, parent, false);

        ViewHolder viewHolder = new ViewHolder(catalogView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CatalogAdapter.ViewHolder holder, int position) {
        Category category = mCategory.get(position);
        String title = category.getTitle();

        Button button = holder.catalogButton;
        button.setText(title);

        button.setOnClickListener(v -> {
            Categories categories = new Categories();
            Bundle bundle = new Bundle();
            bundle.putString("categoryID", category.getId());
            bundle.putString("categoryTitle", title);
            categories.setArguments(bundle);
            Objects.requireNonNull(fragmentManager).beginTransaction().replace(R.id.nav_host_fragment, categories).addToBackStack(null).commit();
        });
    }


    @Override
    public int getItemCount() {
        return mCategory.size();
    }
}
