package ru.sidorenkovivan.myapplication.ui.catalog;

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
import ru.sidorenkovivan.myapplication.entities.Category;
import ru.sidorenkovivan.myapplication.util.database.DBController;
import ru.sidorenkovivan.myapplication.R;
import ru.sidorenkovivan.myapplication.recyclerviewadapter.AdapterCatalog;

public class Catalog extends Fragment {

    private final DBController mDbController = new DBController();

    @Override
    public View onCreateView(@NonNull final LayoutInflater pInflater,
                             @Nullable final ViewGroup pContainer,
                             @Nullable final Bundle pSavedInstanceState) {
        final View view = pInflater.inflate(R.layout.fragment_catalog, pContainer, false);

        final RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        final FragmentManager fragmentManager = getFragmentManager();

        final String dbPath = requireContext().getApplicationInfo().dataDir + getString(R.string.databaseName);
        final List<Category> category = mDbController.getNotEmptyCategories(dbPath);

        final AdapterCatalog catalogAdapter = new AdapterCatalog(fragmentManager, category);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(catalogAdapter);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable final Bundle pSavedInstanceState) {
        super.onActivityCreated(pSavedInstanceState);

        ViewModelProviders.of(this).get(CatalogViewModel.class);
        // TODO: Use the ViewModel
    }
}