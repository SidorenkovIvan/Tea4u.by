package ru.sidorenkovivan.tea4uby;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import ru.sidorenkovivan.tea4uby.services.LteService;
import ru.sidorenkovivan.tea4uby.services.WifiService;
import ru.sidorenkovivan.tea4uby.util.Constants;

public class SplashActivity extends AppCompatActivity {

    private final String TAG = "MyApp";
    private ProgressBar mProgressBar;
    private final LteService mLteService = new LteService();
    private final WifiService mWifiService = new WifiService();
    private final Constants mConstants = new Constants();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        final StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        mProgressBar = findViewById(R.id.progressBarSplash);
        final TextView textViewForProgressBar = findViewById(R.id.textViewForProgressBar);
        final String[] splashTitles = getResources().getStringArray(R.array.splash_titles);
        final int random = new Random().nextInt(splashTitles.length);
        textViewForProgressBar.setText(splashTitles[random]);

        final Thread thread = new Thread(() -> {
            if (isDatabaseExists()) {
                startNextActivity();
            }
        });
        thread.start();
    }


    private boolean isDatabaseExists() {
        final String dbPath = getApplicationInfo().dataDir + getString(R.string.databaseName);
        final File dbFile = new File(dbPath);
        final boolean isWifiConnected = mWifiService.isConnected(this);
        final boolean isLteConnected = mLteService.isConnected(this);

        if (BuildConfig.DEBUG) {
            Log.i("Length of local db: ", String.valueOf(dbFile.length()));
            Log.i(TAG, isLteConnected + "   " + isWifiConnected);
        }

        try {
            if (isWifiConnected || isLteConnected) {
                if (!dbFile.exists()) {
                    if (BuildConfig.DEBUG) {
                        Log.i(TAG, "Saving database");
                    }

                    saveDatabase();
                } else if (dbFile.exists() && dbFile.length() < mConstants.MAX) {
                    if (BuildConfig.DEBUG) {
                        Log.i(TAG, "Delete and save database");
                    }

                    deleteDatabase();
                    saveDatabase();
                } else if (dbFile.exists()) {
                    CompletableFuture<String> remoteHash = CompletableFuture.supplyAsync(this::getRemoteHash);
                    CompletableFuture<String> localHash = CompletableFuture.supplyAsync(this::getLocalHash);

                    if (!remoteHash.get().equals(localHash.get())) {
                        if (BuildConfig.DEBUG) {
                            Log.i(TAG, "Saving new database");
                        }

                        deleteDatabase();
                        saveDatabase();
                    } else {
                        mProgressBar.setMax(mConstants.MAX_PROGRESS);
                        mProgressBar.setProgress(mConstants.MAX_PROGRESS);

                        if (BuildConfig.DEBUG) {
                            Log.i(TAG, "Database is correct on ur phone");
                        }
                    }
                }

                return true;
            } else if (!dbFile.exists()) {
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "Fail, u have no database for app");
                }

                return false;
            } else {
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "U used old database");
                }

                return true;
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return false;
    }

    private void startNextActivity() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    private String getLocalHash() {
        final String dbPath = getApplicationInfo().dataDir + getString(R.string.databaseName);
        final SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY);
        final Cursor hashQuery = db.rawQuery("SELECT hash FROM hash_table", null);
        hashQuery.moveToFirst();
        final String localHash = hashQuery.getString(mConstants.COLUMN_ZERO);
        hashQuery.close();
        db.close();

        if (BuildConfig.DEBUG) {
            Log.i(TAG, "Local hash: " + localHash);
        }

        return localHash;
    }

    private String getRemoteHash() {
        String remoteHash = "";
        try {
            final URL url = new URL(getString(R.string.remote_hash_API));
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
            String inputLine;
            final StringBuilder content = new StringBuilder();

            while ((inputLine = bufferedReader.readLine()) != null){
                content.append(inputLine);
            }

            bufferedReader.close();
            final JSONArray jsonArray = new JSONArray(content.toString());
            remoteHash = (String) jsonArray.getJSONObject(mConstants.COLUMN_ZERO).get(mConstants.HASH);

            if (BuildConfig.DEBUG) {
                Log.i(TAG, "Remote hash: " + remoteHash);
            }
        } catch (IOException | JSONException e) {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "Something wrong with hash");
            }
        }

        return remoteHash;
    }

    private void deleteDatabase() {
        final String dbPath = getApplicationInfo().dataDir + getString(R.string.databaseName);
        final File old = new File(dbPath);
        final boolean deleted = old.delete();

        if (BuildConfig.DEBUG) {
            Log.i(TAG, "Is old file delete: " + deleted);
        }
    }

    private void saveDatabase() {
        try {
            final String dbPath = getApplicationInfo().dataDir + getString(R.string.databaseName);
            final URL url = new URL(getString(R.string.download_database_API));
            final URLConnection connection = url.openConnection();
            connection.connect();
            final int fileLength = connection.getContentLength();

            if (BuildConfig.DEBUG) {
                Log.i("Length of remote db: ", String.valueOf(fileLength));
            }

            try (final BufferedInputStream inputStream = new BufferedInputStream(new URL(getString(R.string.download_database_API)).openStream());
                 FileOutputStream fileOS = new FileOutputStream(dbPath)) {
                final byte[] data = new byte[mConstants.BYTE];
                long total = 0;
                int byteContent;
                mProgressBar.setMax(mConstants.MAX);

                while ((byteContent = inputStream.read(data, mConstants.OFFSET, mConstants.BYTE)) != -1) {
                    total += byteContent;
                    Log.i(TAG, String.valueOf((int) (total * mConstants.TOTAL)));
                    mProgressBar.setProgress((int) (total * mConstants.TOTAL));
                    fileOS.write(data, mConstants.OFFSET, byteContent);
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        } catch (IOException e) {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "Something wrong with saving database");
            }
        }
    }
}