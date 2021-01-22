package ru.sidorenkovivan.tea4uby.util.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import java.util.ArrayList;
import java.util.List;

import ru.sidorenkovivan.tea4uby.entities.Category;
import ru.sidorenkovivan.tea4uby.entities.Product;
import ru.sidorenkovivan.tea4uby.util.Constants;

public class DBController {

    private final Constants mConstants = new Constants();

    public ArrayList<String> getProductsId(final String pId, final String pDbPath, final int pOffset) {
        final ArrayList<String> productId = new ArrayList<>();
        final SQLiteDatabase db = SQLiteDatabase.openDatabase(pDbPath, null, SQLiteDatabase.OPEN_READONLY);
        final Cursor idQuery = db.rawQuery("SELECT DISTINCT product_id FROM category_product WHERE category_id = '" + pId + "' LIMIT 10 OFFSET '" + pOffset + "'", null);
        idQuery.moveToFirst();

        while (!idQuery.isAfterLast()) {
            productId.add(idQuery.getString(mConstants.COLUMN_ZERO));
            idQuery.moveToNext();
        }

        idQuery.close();
        db.close();

        return productId;
    }

    public ArrayList<String> getLastProductsId(final String pDbPath, final int pOffset) {
        final ArrayList<String> newProductsId = new ArrayList<>();
        final SQLiteDatabase db = SQLiteDatabase.openDatabase(pDbPath, null, SQLiteDatabase.OPEN_READONLY);
        final Cursor idQuery = db.rawQuery("SELECT product.product_id FROM product INNER JOIN latest ON product.product_id = latest.product_id LIMIT 10 OFFSET '" + pOffset + "'", null);
        idQuery.moveToFirst();

        while (!idQuery.isAfterLast()) {
            newProductsId.add(idQuery.getString(mConstants.COLUMN_ZERO));
            idQuery.moveToNext();
        }

        idQuery.close();
        db.close();

        return newProductsId;
    }

    public ArrayList<Product> getProducts(final String pDbPath, final List<String> pProductsIds) {
        final ArrayList<Product> products = new ArrayList<>();
        final SQLiteDatabase db = SQLiteDatabase.openDatabase(pDbPath, null, SQLiteDatabase.OPEN_READONLY);

        for (byte i = 0; i < pProductsIds.size(); i++) {
            final Product product = new Product();
            final Cursor productQuery = db.rawQuery("SELECT DISTINCT product.product_id, product.productTitle FROM product WHERE product_id = '" + pProductsIds.get(i) + "'", null);
            productQuery.moveToFirst();
            product.setId(productQuery.getString(mConstants.COLUMN_ZERO));
            product.setTitle(productQuery.getString(mConstants.COLUMN_ONE));
            productQuery.close();

            final Cursor imageQuery = db.rawQuery("SELECT DISTINCT image.base64 FROM image INNER JOIN product ON product.imgUrl = image.url AND product.product_id = '" + pProductsIds.get(i) + "';", null);
            imageQuery.moveToFirst();
            final byte[] decodedString = Base64.decode(imageQuery.getString(mConstants.COLUMN_ZERO), Base64.DEFAULT);
            final Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, mConstants.OFFSET, decodedString.length);
            product.setImage(decodedByte);
            imageQuery.close();

            products.add(product);
        }

        db.close();

        return products;
    }

    public ArrayList<Category> getNotEmptyCategories(final String pDbPath) {
        final ArrayList<Category> categories = new ArrayList<>();
        final SQLiteDatabase db = SQLiteDatabase.openDatabase(pDbPath, null, SQLiteDatabase.OPEN_READONLY);
        final Cursor categoryQuery = db.rawQuery("SELECT DISTINCT category.category_id, category.title FROM category INNER JOIN category_product ON category_product.category_id = category.category_id", null);
        categoryQuery.moveToFirst();

        while (!categoryQuery.isAfterLast()) {
            categories.add(new Category(categoryQuery.getString(mConstants.COLUMN_ZERO), categoryQuery.getString(mConstants.COLUMN_ONE)));
            categoryQuery.moveToNext();
        }

        categoryQuery.close();
        db.close();

        return categories;
    }

    public Product getProduct(final String dbPath, final String pId) {
        final Product product = new Product();
        final SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY);

        final Cursor productQuery = db.rawQuery("SELECT * FROM product WHERE product_id = '" + pId + "'", null);
        productQuery.moveToFirst();
        final String productImg = productQuery.getString(mConstants.COLUMN_ONE);
        product.setTitle(productQuery.getString(mConstants.COLUMN_TWO));
        product.setProductUrl(productQuery.getString(mConstants.COLUMN_THREE));
        product.setDescription(productQuery.getString(mConstants.COLUMN_FOUR));
        product.setImages(productQuery.getString(mConstants.COLUMN_FIVE));
        product.setCode(productQuery.getString(mConstants.COLUMN_SIX));
        product.setPrice(productQuery.getString(mConstants.COLUMN_SEVEN));
        productQuery.close();

        final Cursor imageQuery = db.rawQuery("SELECT image.base64 FROM image WHERE url = '" + productImg + "'", null);
        imageQuery.moveToFirst();
        final byte[] decodedString = Base64.decode(imageQuery.getString(mConstants.COLUMN_ZERO), Base64.DEFAULT);
        product.setImage(BitmapFactory.decodeByteArray(decodedString, mConstants.OFFSET, decodedString.length));
        imageQuery.close();
        db.close();

        return product;
    }
}
