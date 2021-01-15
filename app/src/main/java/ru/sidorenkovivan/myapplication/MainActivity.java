package ru.sidorenkovivan.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import ru.sidorenkovivan.myapplication.util.ImageLoader;
import ru.sidorenkovivan.myapplication.util.JavaMailApi;
import ru.sidorenkovivan.myapplication.ui.catalog.Catalog;
import ru.sidorenkovivan.myapplication.ui.search.Search;
import ru.sidorenkovivan.myapplication.ui.home.HomeFragment;
import static androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE;

public class MainActivity extends AppCompatActivity {

    private final String FRAGMENT_HOME = "home";
    private final String FRAGMENT_OTHER = "other";
    private final int INDEX = 0;
    private BottomNavigationView mNavigationView;
    private static ImageLoader mImageLoader;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        final StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setSupportActionBar(findViewById(R.id.custom_toolbar));

        mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    viewFragment(new HomeFragment(), FRAGMENT_HOME);

                    return true;
                case R.id.catalog:
                    viewFragment(new Catalog(), FRAGMENT_OTHER);

                    return true;
                case R.id.search:
                    viewFragment(new Search(), FRAGMENT_OTHER);

                    return true;
            }

            return false;
        });

        initAlertDialog();
        initImageLoader();
    }

    private void initImageLoader() {
        mImageLoader = new ImageLoader(this);
    }

    public static ImageLoader getImageLoader() {
        return mImageLoader;
    }

    private void initAlertDialog() {
        final Button buttonPhoneCall = findViewById(R.id.button_call);
        buttonPhoneCall.setOnClickListener(v -> createCustomAlertDialog());
    }

    private void createCustomAlertDialog() {
        final int PHONE_LENGTH = 17;
        final AlertDialog.Builder phone_call = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
        final View custom_back_call = getLayoutInflater().inflate(R.layout.phone_call, null);
        final AlertDialog dialog = phone_call.create();

        initButtons(custom_back_call);

        final Button close = custom_back_call.findViewById(R.id.button_back);
        close.setOnClickListener(v1 -> dialog.dismiss());

        final Button next_page = custom_back_call.findViewById(R.id.button_back_call_view);
        next_page.setOnClickListener(v2 -> {
            dialog.dismiss();

            final View input = getLayoutInflater().inflate(R.layout.phone_call_back, null);
            final AlertDialog dialog1 = phone_call.create();

            final Button close1 = input.findViewById(R.id.button_input_back);
            close1.setOnClickListener(v1 -> dialog1.dismiss());

            final EditText editTextPhone = input.findViewById(R.id.editTextPhone);
            final EditText editTextName = input.findViewById(R.id.editTextName);

            final Button final_page = input.findViewById(R.id.button_send_back_call);
            final_page.setOnClickListener(v3 -> {
                final String phone = editTextPhone.getText().toString().trim();
                final String name = editTextName.getText().toString().trim();
                if (BuildConfig.DEBUG) {
                    Log.i("My phone is ", phone + " - " + phone.length());
                    Log.i("My name in form ", name);
                }

                if (phone.length() == PHONE_LENGTH && !TextUtils.isEmpty(name)) {
                    dialog1.dismiss();

                    final View finalDialog = getLayoutInflater().inflate(R.layout.phone_call_back_final, null);
                    final AlertDialog dialog2 = phone_call.create();

                    final Button close2 = finalDialog.findViewById(R.id.button_input_back);
                    close2.setOnClickListener(v1 -> dialog2.dismiss());

                    final TextView textView = finalDialog.findViewById(R.id.textViewName);
                    textView.setText(getString(R.string.genericAlertDialog, name));

                    final Button ok = finalDialog.findViewById(R.id.button_end);
                    ok.setOnClickListener(v4 -> dialog2.dismiss());

                    sendEmail(phone, name);

                    dialog2.setView(finalDialog);
                    dialog2.show();
                } else {
                    Toast.makeText(this, "Your phone or name is incorrect", Toast.LENGTH_SHORT).show();
                }
            });

            dialog1.setView(input);
            dialog1.show();
        });

        dialog.setView(custom_back_call);
        dialog.show();
    }

    private void initButtons(final View pView) {
        final Button buttonSite = pView.findViewById(R.id.buttonSite);
        final Button buttonSiteT = pView.findViewById(R.id.buttonTextSite);
        final Button buttonVk = pView.findViewById(R.id.buttonVK);
        final Button buttonVkT = pView.findViewById(R.id.buttonTextVK);
        final Button buttonInstagram = pView.findViewById(R.id.buttonInstagram);
        final Button buttonInstagramT = pView.findViewById(R.id.buttonTextInstagram);
        final Button buttonCall = pView.findViewById(R.id.buttonCall);
        final Button buttonCallT = pView.findViewById(R.id.buttonCallText);
        final Button buttonViber = pView.findViewById(R.id.buttonViber);
        final Button buttonViberT = pView.findViewById(R.id.buttonViberText);

        buttonSite.setOnClickListener(this::onClick);
        buttonSiteT.setOnClickListener(this::onClick);
        buttonVk.setOnClickListener(this::onClick);
        buttonVkT.setOnClickListener(this::onClick);
        buttonInstagram.setOnClickListener(this::onClick);
        buttonInstagramT.setOnClickListener(this::onClick);
        buttonCall.setOnClickListener(this::onClick);
        buttonCallT.setOnClickListener(this::onClick);
        buttonViber.setOnClickListener(this::onClick);
        buttonViberT.setOnClickListener(this::onClick);
    }

    @SuppressLint("NonConstantResourceId")
    public void onClick(final View pView) {
        switch (pView.getId()) {
            case R.id.buttonSite:
            case R.id.buttonTextSite:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://tea4u.by")));
                break;
            case R.id.buttonVK:
            case R.id.buttonTextVK:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://vk.com/tea4uby")));
                break;
            case R.id.buttonInstagram:
            case R.id.buttonTextInstagram:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/tea4u.by")));
                break;
            case R.id.button_call:
            case R.id.buttonCallText:
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + getResources().getString(R.string.mtc_call))));
                break;
            case R.id.buttonViber:
            case R.id.buttonViberText:
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + getResources().getString(R.string.viber_call))));
                break;
        }
    }

    private void sendEmail(final String pPhone, final String pName) {
        final String mail = getString(R.string.mail);
        final String subject = getString(R.string.subject);
        final String messageToEmail = getString(R.string.genericMessage, pPhone, pName);

        final JavaMailApi javaMailAPI = new JavaMailApi(this, mail, subject, messageToEmail);
        javaMailAPI.execute();
    }

    private void viewFragment(final Fragment pFragment, final String pName) {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.nav_host_fragment, pFragment);
        fragmentTransaction.setReorderingAllowed(true);

        final int count = fragmentManager.getBackStackEntryCount();
        if (pName.equals(FRAGMENT_OTHER)) {
            fragmentTransaction.addToBackStack(pName);
        }

        fragmentTransaction.commit();

        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (fragmentManager.getBackStackEntryCount() <= count) {
                    fragmentManager.popBackStack(FRAGMENT_OTHER, POP_BACK_STACK_INCLUSIVE);
                    fragmentManager.removeOnBackStackChangedListener(this);
                    mNavigationView.getMenu().getItem(INDEX).setChecked(true);
                }
            }
        });
    }
}