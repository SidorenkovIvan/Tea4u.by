package ru.SidorenkovIvan.MyApplication;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Objects;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.SidorenkovIvan.MyApplication.ui.Categories.Categories;

public class CatalogAdapter extends RecyclerView.Adapter<CatalogAdapter.ViewHolder> {

    private final ArrayList<String> categoryID;
    private final ArrayList<String> categoryTitle;
    FragmentManager fragmentManager;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public Button catalogButton;

        public ViewHolder(View itemView) {
            super(itemView);
            catalogButton = (Button) itemView.findViewById(R.id.catalogButton);
        }
    }
    @Override
    public int getItemViewType(final int position) {
        return R.layout.item_catalog;
    }

    public CatalogAdapter(FragmentManager manager, ArrayList<String> catId, ArrayList<String> catTit) {
        fragmentManager = manager;
        categoryID = catId;
        categoryTitle = catTit;
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
        Button button = holder.catalogButton;
        button.setText(categoryTitle.get(position));

        button.setOnClickListener(v -> {
            Categories categories = new Categories();
            Bundle bundle = new Bundle();
            bundle.putString("categoryID", categoryID.get(position));
            bundle.putString("categoryTitle", categoryTitle.get(position));
            categories.setArguments(bundle);
            Objects.requireNonNull(fragmentManager).beginTransaction().replace(R.id.nav_host_fragment, categories).addToBackStack(null).commit();
        });
    }


    @Override
    public int getItemCount() {
        return categoryID.size();
    }
}
