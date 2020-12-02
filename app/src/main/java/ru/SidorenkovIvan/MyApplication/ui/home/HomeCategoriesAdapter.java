package ru.SidorenkovIvan.MyApplication.ui.home;

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

public class HomeCategoriesAdapter extends RecyclerView.Adapter<HomeCategoriesAdapter.ViewHolder>{
    private final List<Category> mCategory;
    private final FragmentManager fragmentManager;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public Button homeCategoriesButton;

        public ViewHolder(View itemView) {
            super(itemView);
            homeCategoriesButton = itemView.findViewById(R.id.homeCategoriesButton);
        }
    }

    public HomeCategoriesAdapter(FragmentManager manager, List<Category> categories) {
        fragmentManager = manager;
        mCategory = categories;
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.item_home;
    }

    @NotNull
    @Override
    public HomeCategoriesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View catalogView = inflater.inflate(R.layout.item_home, parent, false);

        return new ViewHolder(catalogView);
    }

    @Override
    public void onBindViewHolder(HomeCategoriesAdapter.ViewHolder holder, int position) {
        Category category = mCategory.get(position);
        String title = category.getTitle();

        Button button = holder.homeCategoriesButton;
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
