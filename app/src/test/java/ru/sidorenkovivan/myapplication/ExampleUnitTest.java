package ru.sidorenkovivan.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import javax.inject.Inject;

import androidx.test.core.app.ApplicationProvider;

import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
public class ExampleUnitTest {

    @Inject
    private final Context mContext = ApplicationProvider.getApplicationContext();

    final String mDbPath = mContext.getApplicationInfo().dataDir + "/" + "data.sqlite";

    @Test
    public void isDatabaseExist() {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(mDbPath, null, SQLiteDatabase.OPEN_READONLY);
        assertNotNull(db);
        db.close();
    }
}