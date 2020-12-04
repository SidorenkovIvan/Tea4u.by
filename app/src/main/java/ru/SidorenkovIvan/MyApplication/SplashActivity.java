package ru.SidorenkovIvan.MyApplication;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final String DBname = "data.sqlite";
    private static final String TAG = "MyApp";
    private ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        progressBar = findViewById(R.id.progressBarSplash);

        TextView textViewForProgressBar = findViewById(R.id.textViewForProgressBar);

        String[] splashTitles = getResources().getStringArray(R.array.splash_titles);
        int random = new Random().nextInt(9);
        textViewForProgressBar.setText(splashTitles[random]);

        new Thread(this::workWithDatabase).start();
    }


    private void workWithDatabase() {
        String dbPath = getApplicationInfo().dataDir + "/" + DBname;
        File dbFile = new File(dbPath);

        Log.i(TAG, String.valueOf(dbFile.length()));

        try {
            if (connected()) {
                if (!dbFile.exists()) {
                    Log.i(TAG, "Saving database");
                    saveDatabase();
                } else if (dbFile.exists() && dbFile.length() < 3400000) {
                    deleteDatabase();
                    Log.i(TAG, "Saving database");
                    saveDatabase();
                } else if (dbFile.exists()) {
                    CompletableFuture<String> remoteHash = CompletableFuture.supplyAsync(this::getRemoteHash);
                    CompletableFuture<String> localHash = CompletableFuture.supplyAsync(this::getLocalHash);

                    if (!remoteHash.get().equals(localHash.get())) {
                        deleteDatabase();
                        Log.i(TAG, "Saving new database");
                        saveDatabase();
                    } else {
                        progressBar.setMax(100);
                        progressBar.setProgress(100);
                        Log.i(TAG, "Database is correct on ur phone");
                    }
                }
                startNextActivity();
            } else if (!dbFile.exists()) {
                Log.i(TAG, "Fail, u have no database for app");
            } else {
                Log.i(TAG, "U used old database");
                startNextActivity();
            }
        } catch (InterruptedException | ExecutionException ignored) {
        }
    }

    private void startNextActivity() throws InterruptedException {
        Thread.sleep(1500);
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
        Log.i(TAG, "Go to main page");
    }

    public boolean connected() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        return wifiInfo != null && wifiInfo.isConnected();
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
            URLConnection connection = url.openConnection();
            connection.connect();

            int fileLength = connection.getContentLength();
            fileLength *= -1;
            Log.i("Length", String.valueOf(fileLength));

            try (BufferedInputStream inputStream = new BufferedInputStream(new URL(getString(R.string.download_database_API)).openStream());
                 FileOutputStream fileOS = new FileOutputStream(dbPath)) {
                byte[] data = new byte[1024];
                long total = 0;
                int byteContent;
                progressBar.setMax(343800000);
                while ((byteContent = inputStream.read(data, 0, 1024)) != -1) {
                    total += byteContent;
                    Log.i(TAG, String.valueOf((int) (total * 100 / fileLength)));
                    progressBar.setProgress((int) (total * 100 / fileLength));
                    fileOS.write(data, 0, byteContent);
                }
            } catch (IOException ignored) {}
        } catch (IOException e) {
            Log.i(TAG, "Something wrong with saving database");
        }
    }
}