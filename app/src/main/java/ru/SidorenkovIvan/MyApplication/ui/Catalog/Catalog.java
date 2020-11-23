package ru.SidorenkovIvan.MyApplication.ui.Catalog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.SidorenkovIvan.MyApplication.CatalogAdapter;
import ru.SidorenkovIvan.MyApplication.CategoriesIdAndTitles;
import ru.SidorenkovIvan.MyApplication.R;

public class Catalog extends Fragment {

    private static final String DBname = "data.sqlite";
    List<CategoriesIdAndTitles> categoriesIdAndTitles;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.catalog_fragment, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        FragmentManager fragmentManager = getFragmentManager();

        String dbPath = requireContext().getApplicationInfo().dataDir + "/" + DBname;
        categoriesIdAndTitles = CategoriesIdAndTitles.findCategoriesIdTit(dbPath);

        CatalogAdapter catalogAdapter = new CatalogAdapter(fragmentManager, categoriesIdAndTitles);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(catalogAdapter);

        Button button = view.findViewById(R.id.button_update);
        button.setOnClickListener(v -> {
            categoriesIdAndTitles.addAll(CategoriesIdAndTitles.findCategoriesIdTit(dbPath));
            catalogAdapter.notifyDataSetChanged();
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ViewModelProviders.of(this).get(CatalogViewModel.class);
        // TODO: Use the ViewModel
    }
}