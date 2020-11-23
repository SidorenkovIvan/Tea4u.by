package ru.SidorenkovIvan.MyApplication;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class CategoriesIdAndTitles {
    private String id;
    private String title;

    public CategoriesIdAndTitles(String mId, String mTitle) {
        id = mId;
        title = mTitle;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public static ArrayList<CategoriesIdAndTitles> findCategoriesIdTit(String dbPath) {
        ArrayList<CategoriesIdAndTitles> categoriesIdAndTitles = new ArrayList<>();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY);
        Cursor query = db.rawQuery("SELECT DISTINCT category.category_id, category.title FROM category INNER JOIN category_product ON category_product.category_id = category.category_id", null);
        query.moveToFirst();
        while (!query.isAfterLast()) {
            categoriesIdAndTitles.add(new CategoriesIdAndTitles(query.getString(0), query.getString(1)));
            query.moveToNext();
        }
        query.close();
        db.close();

        return categoriesIdAndTitles;
    }
}
