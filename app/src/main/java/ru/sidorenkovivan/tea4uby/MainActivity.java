package ru.sidorenkovivan.tea4uby;

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
import ru.sidorenkovivan.tea4uby.services.ImageLoader;
import ru.sidorenkovivan.tea4uby.ui.catalog.CatalogFragment;
import ru.sidorenkovivan.tea4uby.ui.search.SearchFragment;
import ru.sidorenkovivan.tea4uby.ui.home.HomeFragment;
import ru.sidorenkovivan.tea4uby.util.Constants;

import static androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE;

public class MainActivity extends AppCompatActivity {

    private final String FRAGMENT_HOME = "home";
    private final String FRAGMENT_OTHER = "other";
    private final Constants mConstants = new Constants();
    private BottomNavigationView mNavigationView;
    private AlertDialog mPhoneCallDialog;
    private AlertDialog mPhoneCallBackDialog;
    private AlertDialog mPhoneCallBackFinalDialog;
    private View mPhoneCallView;
    private View mPhoneCallBackView;
    private View mPhoneCallBackFinalView;
    private Button mBackButton;
    private Button mNextPageButton;
    private Button mBackCallButton;
    private Button mFinalPageButton;
    private Button mBackFinalButton;
    private Button mOkButton;
    private EditText mPhoneEditText;
    private EditText mNameEditText;
    private TextView mNameTextView;

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
                    HomeFragment homeFragment = new HomeFragment();
                    viewFragment(homeFragment, FRAGMENT_HOME);

                    return true;
                case R.id.catalog:
                    CatalogFragment catalogFragment = new CatalogFragment();
                    viewFragment(catalogFragment, FRAGMENT_OTHER);

                    return true;
                case R.id.search:
                    SearchFragment searchFragment = new SearchFragment();
                    viewFragment(searchFragment, FRAGMENT_OTHER);

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
        final Button buttonPhoneCall = findViewById(R.id.toolBarButtonCall);
        buttonPhoneCall.setOnClickListener(view -> {
            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
            mPhoneCallDialog = alertDialogBuilder.create();
            mPhoneCallBackDialog = alertDialogBuilder.create();
            mPhoneCallBackFinalDialog = alertDialogBuilder.create();
            createCustomAlertDialog();
        });
    }

    private void createCustomAlertDialog() {
        initPhoneCallViews();
        initButtonsWithContacts(mPhoneCallView);
        initOtherViews();
        mBackButton.setOnClickListener(close -> mPhoneCallDialog.dismiss());
        mNextPageButton.setOnClickListener(nextPage -> {
            mPhoneCallDialog.dismiss();
            mBackCallButton.setOnClickListener(close -> mPhoneCallBackDialog.dismiss());
            mFinalPageButton.setOnClickListener(finalPage -> {
                final String phone = mPhoneEditText.getText().toString().trim();
                final String name = mNameEditText.getText().toString().trim();

                if (BuildConfig.DEBUG) {
                    Log.i("My phone is ", phone + " - " + phone.length());
                    Log.i("My name in form ", name);
                }

                if (phone.length() == mConstants.PHONE_LENGTH && !TextUtils.isEmpty(name)) {
                    mPhoneCallBackDialog.dismiss();
                    sendEmail(phone, name);
                    mBackFinalButton.setOnClickListener(close -> mPhoneCallBackFinalDialog.dismiss());
                    mNameTextView.setText(getString(R.string.genericAlertDialog, name));
                    mOkButton.setOnClickListener(ok -> mPhoneCallBackFinalDialog.dismiss());

                    mPhoneCallBackFinalDialog.setView(mPhoneCallBackFinalView);
                    mPhoneCallBackFinalDialog.show();
                } else {
                    Toast.makeText(this, "Your phone or name is incorrect", Toast.LENGTH_SHORT).show();
                }
            });
            mPhoneCallBackDialog.setView(mPhoneCallBackView);
            mPhoneCallBackDialog.show();
        });
        mPhoneCallDialog.setView(mPhoneCallView);
        mPhoneCallDialog.show();
    }

    @SuppressLint("InflateParams")
    private void initPhoneCallViews() {
        mPhoneCallView = getLayoutInflater().inflate(R.layout.phone_call, null);
        mPhoneCallBackView = getLayoutInflater().inflate(R.layout.phone_call_back, null);
        mPhoneCallBackFinalView = getLayoutInflater().inflate(R.layout.phone_call_back_final, null);
    }

    private void initButtonsWithContacts(final View pView) {
        final Button buttonSite = pView.findViewById(R.id.buttonSite);
        final Button buttonSiteText = pView.findViewById(R.id.buttonTextSite);
        final Button buttonVk = pView.findViewById(R.id.buttonVK);
        final Button buttonVkText = pView.findViewById(R.id.buttonTextVK);
        final Button buttonInstagram = pView.findViewById(R.id.buttonInstagram);
        final Button buttonInstagramText = pView.findViewById(R.id.buttonTextInstagram);
        final Button buttonCall = pView.findViewById(R.id.buttonCall);
        final Button buttonCallText = pView.findViewById(R.id.buttonCallText);
        final Button buttonViber = pView.findViewById(R.id.buttonViber);
        final Button buttonViberText = pView.findViewById(R.id.buttonViberText);
        buttonSite.setOnClickListener(this::onClick);
        buttonSiteText.setOnClickListener(this::onClick);
        buttonVk.setOnClickListener(this::onClick);
        buttonVkText.setOnClickListener(this::onClick);
        buttonInstagram.setOnClickListener(this::onClick);
        buttonInstagramText.setOnClickListener(this::onClick);
        buttonCall.setOnClickListener(this::onClick);
        buttonCallText.setOnClickListener(this::onClick);
        buttonViber.setOnClickListener(this::onClick);
        buttonViberText.setOnClickListener(this::onClick);
    }

    private void initOtherViews() {
        mBackButton = mPhoneCallView.findViewById(R.id.buttonBack);
        mNextPageButton = mPhoneCallView.findViewById(R.id.buttonBackCallView);
        mBackCallButton = mPhoneCallBackView.findViewById(R.id.buttonBackCall);
        mFinalPageButton = mPhoneCallBackView.findViewById(R.id.buttonSendBackCall);
        mBackFinalButton = mPhoneCallBackFinalView.findViewById(R.id.buttonBackFinal);
        mOkButton = mPhoneCallBackFinalView.findViewById(R.id.buttonOk);
        mPhoneEditText = mPhoneCallBackView.findViewById(R.id.editTextPhone);
        mNameEditText = mPhoneCallBackView.findViewById(R.id.editTextName);
        mNameTextView = mPhoneCallBackFinalView.findViewById(R.id.textViewName);
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
            case R.id.toolBarButtonCall:
            case R.id.buttonCallText:
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + getString(R.string.mtc_call))));
                break;
            case R.id.buttonViber:
            case R.id.buttonViberText:
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + getString(R.string.viber_call))));
                break;
        }
    }

    private void sendEmail(final String pPhone, final String pName) {
        final String mail = getString(R.string.mail);
        final String subject = getString(R.string.subject);
        final String messageToEmail = getString(R.string.genericMessage, pPhone, pName);

        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{ mail});
        email.putExtra(Intent.EXTRA_SUBJECT, subject);
        email.putExtra(Intent.EXTRA_TEXT, messageToEmail);
        email.setType("message/rfc822");

        startActivity(Intent.createChooser(email, "Choose an Email client :"));

//        final JavaMailApi javaMailAPI = new JavaMailApi(this, mail, subject, messageToEmail);
//        javaMailAPI.execute();
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
                    mNavigationView.getMenu().getItem(mConstants.INDEX).setChecked(true);
                }
            }
        });
    }
}