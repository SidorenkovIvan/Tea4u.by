package ru.SidorenkovIvan.MyApplication.ui.Catalog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.SidorenkovIvan.MyApplication.Category;
import ru.SidorenkovIvan.MyApplication.R;

public class Catalog extends Fragment {

    private static final String DBname = "data.sqlite";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.catalog_fragment, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        FragmentManager fragmentManager = getFragmentManager();

        String dbPath = requireContext().getApplicationInfo().dataDir + "/" + DBname;
        List<Category> category = Category.getNotEmptyCategories(dbPath);

        CatalogAdapter catalogAdapter = new CatalogAdapter(fragmentManager, category);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(catalogAdapter);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ViewModelProviders.of(this).get(CatalogViewModel.class);
        // TODO: Use the ViewModel
    }
}