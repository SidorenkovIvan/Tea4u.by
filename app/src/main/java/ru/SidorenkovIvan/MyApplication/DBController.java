package ru.SidorenkovIvan.MyApplication;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import java.util.ArrayList;
import java.util.List;

public class DBController {

    public static ArrayList<Product> getProducts(final String pDbPath, final List<String> pProductsIds) {
        ArrayList<Product> products = new ArrayList<>();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pDbPath, null, SQLiteDatabase.OPEN_READONLY);
        for (byte i = 0; i < pProductsIds.size(); i++) {
            Product product = new Product();
            Cursor query = db.rawQuery("SELECT DISTINCT product.product_id, product.productTitle FROM product WHERE product_id = '" + pProductsIds.get(i) + "'", null);
            query.moveToFirst();
            product.setId(query.getString(0));
            product.setTitle(query.getString(1));
            query.close();

            Cursor query1 = db.rawQuery("SELECT DISTINCT product.product_id, image.base64 FROM image INNER JOIN product ON product.imgUrl = image.url AND product.product_id = '" + pProductsIds.get(i) + "';", null);
            query1.moveToFirst();
            byte[] decodedString = Base64.decode(query1.getString(1), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            product.setImage(decodedByte);
            query1.close();

            products.add(product);
        }
        db.close();

        return products;
    }

    public static ArrayList<Category> getNotEmptyCategories(final String pDbPath) {
        ArrayList<Category> categories = new ArrayList<>();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pDbPath, null, SQLiteDatabase.OPEN_READONLY);
        Cursor query = db.rawQuery("SELECT DISTINCT category.category_id, category.title FROM category INNER JOIN category_product ON category_product.category_id = category.category_id", null);
        query.moveToFirst();
        while (!query.isAfterLast()) {
            categories.add(new Category(query.getString(0), query.getString(1)));
            query.moveToNext();
        }
        query.close();
        db.close();

        return categories;
    }

    public static Product getProduct(final Context pContext, final String pId) {
        Product product = new Product();
        String dbPath = pContext.getApplicationInfo().dataDir + "/" + "data.sqlite";
        SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY);

        Cursor query = db.rawQuery("SELECT * FROM product WHERE product_id = '" + pId + "'", null);
        query.moveToFirst();
        String productImg = query.getString(1);
        product.setTitle(query.getString(2));
        product.setProductUrl(query.getString(3));
        product.setDescription(query.getString(4));
        product.setImages(query.getString(5));
        product.setCode(query.getString(6));
        product.setPrice(query.getString(7));
        query.close();

        Cursor query1 = db.rawQuery("SELECT image.base64 FROM image WHERE url = '" + productImg + "'", null);
        query1.moveToFirst();
        byte[] decodedString = Base64.decode(query1.getString(0), Base64.DEFAULT);
        product.setImage(BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
        query1.close();

        db.close();

        return product;
    }
}
