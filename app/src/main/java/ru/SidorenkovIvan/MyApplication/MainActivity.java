package ru.SidorenkovIvan.MyApplication;

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
import ru.SidorenkovIvan.MyApplication.ui.Catalog.Catalog;
import ru.SidorenkovIvan.MyApplication.ui.Search.Search;
import ru.SidorenkovIvan.MyApplication.ui.home.HomeFragment;
import static androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE;

public class MainActivity extends AppCompatActivity {

    private static final String FRAGMENT_HOME = "home";
    private static final String FRAGMENT_OTHER = "other";
    private BottomNavigationView navigationView;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setSupportActionBar(findViewById(R.id.custom_toolbar));

        navigationView = findViewById(R.id.nav_view);
        navigationView.setOnNavigationItemSelectedListener(item -> {
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
    }

    @SuppressLint("SetTextI18n")
    private void initAlertDialog() {
        Button buttonPhoneCall = findViewById(R.id.button_call);
        buttonPhoneCall.setOnClickListener(v -> {
            AlertDialog.Builder phone_call = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
            View custom_back_call = getLayoutInflater().inflate(R.layout.phone_call, null);
            final AlertDialog dialog = phone_call.create();

            initButtons(custom_back_call);

            Button close = custom_back_call.findViewById(R.id.button_back);
            close.setOnClickListener(v1 -> dialog.dismiss());

            Button next_page = custom_back_call.findViewById(R.id.button_back_call_view);
            next_page.setOnClickListener(v2 -> {
                dialog.dismiss();

                View input = getLayoutInflater().inflate(R.layout.phone_call_back, null);
                final AlertDialog dialog1 = phone_call.create();

                Button close1 = input.findViewById(R.id.button_input_back);
                close1.setOnClickListener(v1 -> dialog1.dismiss());

                EditText editTextPhone = input.findViewById(R.id.editTextPhone);
                EditText editTextName = input.findViewById(R.id.editTextName);

                Button final_page = input.findViewById(R.id.button_send_back_call);
                final_page.setOnClickListener(v3 -> {
                    String phone = editTextPhone.getText().toString().trim();
                    String name = editTextName.getText().toString().trim();
                    Log.i("My phone is ", phone + " - " + phone.length());
                    Log.i("My name in form ", name);

                    if (phone.length() == 17 && !TextUtils.isEmpty(name)) {
                        dialog1.dismiss();

                        View finalDialog = getLayoutInflater().inflate(R.layout.final_phone_call_back, null);
                        final AlertDialog dialog2 = phone_call.create();

                        Button close2 = finalDialog.findViewById(R.id.button_input_back);
                        close2.setOnClickListener(v1 -> dialog2.dismiss());

                        TextView textView = finalDialog.findViewById(R.id.textViewName);
                        textView.setText("Спасибо, " + name + "." + "\n" + "Мы скоро Вам перезвоним");

                        Button ok = finalDialog.findViewById(R.id.button_end);
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
        });
    }

    private void initButtons(View view) {
        Button buttonSite = view.findViewById(R.id.buttonSite);
        Button buttonSiteT = view.findViewById(R.id.buttonTextSite);
        Button buttonVk = view.findViewById(R.id.buttonVK);
        Button buttonVkT = view.findViewById(R.id.buttonTextVK);
        Button buttonInstagram = view.findViewById(R.id.buttonInstagram);
        Button buttonInstagramT = view.findViewById(R.id.buttonTextInstagram);
        Button buttonCall = view.findViewById(R.id.buttonCall);
        Button buttonCallT = view.findViewById(R.id.buttonCallText);
        Button buttonViber = view.findViewById(R.id.buttonViber);
        Button buttonViberT = view.findViewById(R.id.buttonViberText);

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
    public void onClick(View view) {
        switch (view.getId()) {
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

    private void sendEmail(String phone, String name) {
        String mail = "info@tea4u.by";
        String subject = "Обратный звонок от приложения";
        String messageToEmail = "Телефон: " + phone + "\n" + "Имя: " + name;

        JavaMailAPI javaMailAPI = new JavaMailAPI(this, mail, subject, messageToEmail);
        javaMailAPI.execute();
    }

    private void viewFragment(Fragment fragment, String name) {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
        fragmentTransaction.setReorderingAllowed(true);

        final int count = fragmentManager.getBackStackEntryCount();
        if (name.equals(FRAGMENT_OTHER)) {
            fragmentTransaction.addToBackStack(name);
        }
        fragmentTransaction.commit();

        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (fragmentManager.getBackStackEntryCount() <= count) {
                    fragmentManager.popBackStack(FRAGMENT_OTHER, POP_BACK_STACK_INCLUSIVE);
                    fragmentManager.removeOnBackStackChangedListener(this);
                    navigationView.getMenu().getItem(0).setChecked(true);
                }
            }
        });
    }
}