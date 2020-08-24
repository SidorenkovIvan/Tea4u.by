package ru.SidorenkovIvan.MyApplication;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    private static final String DBname = "data.sqlite";
    private static final String TAG = "MyApp";

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        new Thread(() -> {
            workWithDatabase();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            startNextActivity();
        }).start();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void workWithDatabase() {
        String dbPath = getApplicationInfo().dataDir + "/" + DBname;
        File dbFile = new File(dbPath);

        CompletableFuture<String> remoteHash = CompletableFuture.supplyAsync(this::getRemoteHash);
        CompletableFuture<String> localHash = CompletableFuture.supplyAsync(this::getLocalHash);

        try {
            if (connected()) {
                if (!dbFile.exists() || !remoteHash.get().equals(localHash.get())) {
                    deleteDatabase();
                    Log.i(TAG, "Saving database");
                    saveDatabase();
                } else Log.i(TAG, "Database is correct on ur phone");
            } else if (!dbFile.exists()) Log.i(TAG, "Fail, u have no database for app");
            else Log.i(TAG, "U used old database");
        } catch (InterruptedException | ExecutionException ignored) {
        }
    }

    private void startNextActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
        Log.i(TAG, "Go to main page");
    }

    private boolean connected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
    }

    private String getLocalHash() {
        String dbPath = getApplicationInfo().dataDir + "/" + DBname;
        SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY);
        Cursor query = db.rawQuery("SELECT hash FROM hash_table", null);
        query.moveToFirst();
        String localHash = query.getString(0);
        Log.i(TAG, "Local hash: " + localHash);
        query.close();
        db.close();
        return localHash;
    }

    private String getRemoteHash() {
        String remoteHash = "";
        try {
            URL url = new URL(getString(R.string.remote_hash_API));
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) content.append(inputLine);
            in.close();
            JSONArray a = new JSONArray(content.toString());
            remoteHash = (String) a.getJSONObject(0).get("hash");
            Log.i(TAG, "Remote hash: " + remoteHash);
        } catch (IOException | JSONException e) {
            Log.i(TAG, "Something wrong with hash");
        }
        return remoteHash;
    }

    private void deleteDatabase() {
        String dbPath = getApplicationInfo().dataDir + "/" + DBname;
        File old = new File(dbPath);
        boolean deleted = old.delete();
        Log.i(TAG, "Is old file delete: " + deleted);
    }

    private void saveDatabase() {
        try {
            String dbPath = getApplicationInfo().dataDir + "/" + DBname;
            URL url = new URL(getString(R.string.download_database_API));
            File f = new File(dbPath);
            if (!f.isFile()) {
                FileUtils.copyURLToFile(url, f);
            }
        } catch (IOException e) {
            Log.i(TAG, "Something wrong with saving database");
        }
    }
}