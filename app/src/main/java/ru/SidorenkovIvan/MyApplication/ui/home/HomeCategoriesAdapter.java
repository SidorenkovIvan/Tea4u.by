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
    private final FragmentManager mFragmentManager;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public Button homeCategoriesButton;

        public ViewHolder(final View pItemView) {
            super(pItemView);
            homeCategoriesButton = pItemView.findViewById(R.id.homeCategoriesButton);
        }
    }

    public HomeCategoriesAdapter(FragmentManager pFragmentManager, List<Category> pCategories) {
        mFragmentManager = pFragmentManager;
        mCategory = pCategories;
    }

    @Override
    public int getItemViewType(final int pPosition) {
        return R.layout.item_home;
    }

    @NotNull
    @Override
    public HomeCategoriesAdapter.ViewHolder onCreateViewHolder(final ViewGroup pParent, final int pViewType) {
        Context context = pParent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View catalogView = inflater.inflate(R.layout.item_home, pParent, false);

        return new ViewHolder(catalogView);
    }

    @Override
    public void onBindViewHolder(final HomeCategoriesAdapter.ViewHolder pHolder, final int pPosition) {
        Category category = mCategory.get(pPosition);
        String title = category.getTitle();

        Button button = pHolder.homeCategoriesButton;
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
        return mCategory.size();
    }
}
