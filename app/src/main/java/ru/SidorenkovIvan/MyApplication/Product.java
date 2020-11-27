package ru.SidorenkovIvan.MyApplication;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Product implements Serializable {
    private String id;
    private String title;
    private String code;
    private Bitmap image;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Bitmap getImage() {
        return this.image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    @NotNull
    public String toString() {
        return this.title + "\n" + this.code;
    }

    public static ArrayList<Product> getProducts(String dbPath, List<String> productId) {
        ArrayList<Product> products = new ArrayList<>();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY);
        for (byte i = 0; i < productId.size(); i++) {
            Product product = new Product();
            Cursor query = db.rawQuery("SELECT DISTINCT product.product_id, product.productTitle FROM product WHERE product_id = '" + productId.get(i) + "'", null);
            query.moveToFirst();
            product.setId(query.getString(0));
            product.setTitle(query.getString(1));
            query.close();

            Cursor query1 = db.rawQuery("SELECT DISTINCT product.product_id, image.base64 FROM image INNER JOIN product ON product.imgUrl = image.url AND product.product_id = '" + productId.get(i) + "';", null);
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
}