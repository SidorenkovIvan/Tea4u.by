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
import androidx.appcompat.widget.Toolbar;
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
    private String name;
    private String phone;
    private BottomNavigationView navigationView;

    @SuppressLint("SetTextI18n")
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

        Button buttonPhoneCall = findViewById(R.id.button_call);
        buttonPhoneCall.setOnClickListener(v -> {
            @SuppressLint("ResourceType") AlertDialog.Builder phone_call = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
            View custom_back_call = getLayoutInflater().inflate(R.layout.phone_call, null);
            final AlertDialog dialog = phone_call.create();

            Button close = custom_back_call.findViewById(R.id.button_back);
            close.setOnClickListener(v1 -> dialog.dismiss());

            Button buttonSite = custom_back_call.findViewById(R.id.buttonSite);
            buttonSite.setOnClickListener(q -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://tea4u.by"))));

            Button buttonTextSite = custom_back_call.findViewById(R.id.buttonTextSite);
            buttonTextSite.setOnClickListener(q -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://tea4u.by"))));

            Button buttonVK = custom_back_call.findViewById(R.id.buttonVK);
            buttonVK.setOnClickListener(b -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://vk.com/tea4uby"))));

            Button buttonVKText = custom_back_call.findViewById(R.id.buttonTextVK);
            buttonVKText.setOnClickListener(b -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://vk.com/tea4uby"))));

            Button buttonInstagram = custom_back_call.findViewById(R.id.buttonInstagram);
            buttonInstagram.setOnClickListener(b1 -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/tea4u.by"))));

            Button buttonInstagramText = custom_back_call.findViewById(R.id.buttonTextInstagram);
            buttonInstagramText.setOnClickListener(b1 -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/tea4u.by"))));

            Button button = custom_back_call.findViewById(R.id.buttonCall);
            button.setOnClickListener(i -> startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + getResources().getString(R.string.mtc_call)))));

            Button buttonCall = custom_back_call.findViewById(R.id.buttonCallText);
            buttonCall.setOnClickListener(i -> startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + getResources().getString(R.string.mtc_call)))));

            Button buttonViber = custom_back_call.findViewById(R.id.buttonViber);
            buttonViber.setOnClickListener(i -> startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + getResources().getString(R.string.viber_call)))));

            Button buttonViberText = custom_back_call.findViewById(R.id.buttonViberText);
            buttonViberText.setOnClickListener(i -> startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + getResources().getString(R.string.viber_call)))));

            Button next_page = custom_back_call.findViewById(R.id.button_back_call_view);
            next_page.setOnClickListener(v2 -> {
                dialog.dismiss();

                AlertDialog.Builder inputData = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
                View input = getLayoutInflater().inflate(R.layout.phone_call_back, null);
                final AlertDialog dialog1 = inputData.create();

                Button close1 = input.findViewById(R.id.button_input_back);
                close1.setOnClickListener(v1 -> dialog1.dismiss());

                EditText editTextPhone = input.findViewById(R.id.linearLayout3);
                EditText editTextName = input.findViewById(R.id.linearLayout4);

                Button final_page = input.findViewById(R.id.button_send_back_call);
                final_page.setOnClickListener(v3 -> {
                    phone = editTextPhone.getText().toString().trim();
                    name = editTextName.getText().toString().trim();
                    Log.i("My phone is ", phone + " - " + phone.length());

                    if (phone.length() == 17 && !TextUtils.isEmpty(name)) {
                        Log.i("My name in form ", name);
                        dialog1.dismiss();

                        AlertDialog.Builder final_alert_dialog = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
                        View finalDialog = getLayoutInflater().inflate(R.layout.final_phone_call_back, null);
                        final AlertDialog dialog2 = final_alert_dialog.create();

                        Button close2 = finalDialog.findViewById(R.id.button_input_back);
                        close2.setOnClickListener(v1 -> dialog2.dismiss());

                        TextView textView = finalDialog.findViewById(R.id.textView2);
                        textView.setText("Спасибо, " + name + "." + "\n" + "Мы скоро Вам перезвоним");

                        Button ok = finalDialog.findViewById(R.id.button_end);
                        ok.setOnClickListener(v4 -> dialog2.dismiss());

                        sendEmail(phone, name);

                        dialog2.setView(finalDialog);
                        dialog2.show();
                    } else {
                        Toast.makeText(this, "Your phone or name is incorrect", Toast.LENGTH_LONG).show();
                    }
                });
                dialog1.setView(input);
                dialog1.show();
            });
            dialog.setView(custom_back_call);
            dialog.show();
        });
    }

    private void sendEmail(String phone, String name) {
        String mail = "info@tea4u.by";
        String subject = "Обратный звонок от приложения";
        String messageToEmail = "Телефон: " + phone + "\n" + "Имя: " + name;

        JavaMailAPI javaMailAPI = new JavaMailAPI(this, mail, subject, messageToEmail);
        javaMailAPI.execute();
    }

    private void viewFragment(Fragment fragment, String name){
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
                if( fragmentManager.getBackStackEntryCount() <= count){
                    fragmentManager.popBackStack(FRAGMENT_OTHER, POP_BACK_STACK_INCLUSIVE);
                    fragmentManager.removeOnBackStackChangedListener(this);
                    navigationView.getMenu().getItem(0).setChecked(true);
                }
            }
        });
    }
}