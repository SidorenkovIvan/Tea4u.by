package ru.SidorenkovIvan.MyApplication.ui.Search;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;
import java.util.Objects;
import ru.SidorenkovIvan.MyApplication.R;
import ru.SidorenkovIvan.MyApplication.ui.PageOfProduct.PageOfProduct;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;

public class Search extends Fragment {

    private static final String TAG = "MyApp";
    private static final String DBname = "data.sqlite";
    private ScrollView scrollView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.search_fragment, container, false);

        SearchView searchView = view.findViewById(R.id.searchView);
        scrollView = view.findViewById(R.id.scrollView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i(TAG, "onQueryTextSubmit: " + query);
                search(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.i(TAG, "onQueryTextChange: " + newText);
                search(newText);
                return false;
            }
        });
        return view;
    }

    private void search(String keyword) {
        Typeface typeface = ResourcesCompat.getFont(requireContext(), R.font.opensans);
        String dbPath = requireContext().getApplicationInfo().dataDir + "/" + DBname;
        SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.rawQuery("SELECT * FROM product WHERE productTitle like ?", new String[]{"%" + keyword + "%"});
        Cursor cursor1 = db.rawQuery("SELECT * FROM product WHERE code like ?", new String[]{"%" + keyword + "%"});
        if (cursor.moveToFirst()) {
            LinearLayout linearLayout = new LinearLayout(getContext());
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setBackgroundColor(Color.WHITE);
            do {
                scrollView.post(scrollView::removeAllViews);
                Product product = new Product();
                Button button = new Button(getContext());
                product.setId(cursor.getString(0));
                product.setName(cursor.getString(2));
                product.setCode(cursor.getString(6));
                button.setText(product.toString());
                button.setAllCaps(false);
                button.setTypeface(typeface);
                button.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                button.setBackgroundColor(Color.WHITE);
                button.setPadding(20, 4, 20, 4);
                button.setOnClickListener(v -> {
                    PageOfProduct pageOfProduct = new PageOfProduct();
                    Bundle bundle = new Bundle();
                    bundle.putString("productID", product.getId());
                    pageOfProduct.setArguments(bundle);
                    FragmentManager fragmentManager = getFragmentManager();
                    Objects.requireNonNull(fragmentManager).beginTransaction().replace(R.id.nav_host_fragment, pageOfProduct).addToBackStack(null).commit();
                });
                linearLayout.post(() -> linearLayout.addView(button));
            } while (cursor.moveToNext());

            scrollView.post(() -> scrollView.addView(linearLayout));
        } else if (cursor1.moveToFirst()) {
            LinearLayout linearLayout = new LinearLayout(getContext());
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setBackgroundColor(Color.WHITE);
            do {
                scrollView.post(scrollView::removeAllViews);
                Product product = new Product();
                Button button = new Button(getContext());
                product.setId(cursor1.getString(0));
                product.setName(cursor1.getString(2));
                product.setCode(cursor1.getString(6));
                button.setText(product.toString());
                button.setAllCaps(false);
                button.setTypeface(typeface);
                button.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                button.setBackgroundColor(Color.WHITE);
                button.setPadding(20, 4, 20, 4);
                button.setOnClickListener(v -> {
                    PageOfProduct pageOfProduct = new PageOfProduct();
                    Bundle bundle = new Bundle();
                    bundle.putString("productID", product.getId());
                    pageOfProduct.setArguments(bundle);
                    FragmentManager fragmentManager = getFragmentManager();
                    Objects.requireNonNull(fragmentManager).beginTransaction().replace(R.id.nav_host_fragment, pageOfProduct).addToBackStack(null).commit();
                });
                linearLayout.post(() -> linearLayout.addView(button));
            } while (cursor1.moveToNext());

            scrollView.post(() -> scrollView.addView(linearLayout));
        } else {
            scrollView.post(scrollView::removeAllViews);
            LinearLayout linearLayout = new LinearLayout(getContext());
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setBackgroundColor(Color.WHITE);

            TextView textView = new TextView(getContext());
            textView.setText("Не найдено");
            textView.setTypeface(typeface);
            textView.setTextSize(18);

            linearLayout.post(() -> linearLayout.addView(textView));
            scrollView.post(() -> scrollView.addView(linearLayout));
        }

        cursor.close();
        cursor1.close();
        db.close();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ViewModelProviders.of(this).get(SearchViewModel.class);
        // TODO: Use the ViewModel
    }
}