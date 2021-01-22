package ru.sidorenkovivan.tea4uby.ui.catalog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.sidorenkovivan.tea4uby.R;
import ru.sidorenkovivan.tea4uby.adapters.CatalogAdapter;
import ru.sidorenkovivan.tea4uby.entities.Category;
import ru.sidorenkovivan.tea4uby.util.database.DBController;

public class CatalogFragment extends Fragment {

    private final DBController mDbController = new DBController();

    @Override
    public View onCreateView(final LayoutInflater pInflater,
                             final ViewGroup pContainer,
                             final Bundle pSavedInstanceState) {
        final View view = pInflater.inflate(R.layout.fragment_catalog, pContainer, false);

        final RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        final FragmentManager fragmentManager = getFragmentManager();
        final String dbPath = requireContext().getApplicationInfo().dataDir + getString(R.string.databaseName);
        final List<Category> category = mDbController.getNotEmptyCategories(dbPath);
        final CatalogAdapter catalogAdapter = new CatalogAdapter(fragmentManager, category);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(catalogAdapter);

        return view;
    }
}