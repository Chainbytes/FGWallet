package com.ivan.fgwallet;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ivan.fgwallet.helper.PrefManager;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {


    TextView tv_newWallet;
    TextView tv_restoreWallet;
    LinearLayout lnLanguage;
    TextView tvLanguage;

    PrefManager prefManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
//        ButterKnife.inject(this);
        init();
        prefManager = new PrefManager(MainActivity.this);
        loadLanguage();
        if (!prefManager.getpref(PrefManager.KEY_PHONE).equals("") && !prefManager.getpref(PrefManager.KEY_PIN).equals("")) {
            startActivity(new Intent(getApplicationContext(), EnterPinActivtiy.class));
            finish();
        }

        tv_newWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), NewWalletActivtiy.class));
            }
        });
        tv_restoreWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), RestoreWalletActivtiy.class));
            }
        });

        lnLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChangeLanguageDialog();
            }
        });
    }

    private void init() {
        tv_newWallet = findViewById(R.id.tv_newWallet);
        tv_restoreWallet = findViewById(R.id.tv_restoreWallet);
        lnLanguage = findViewById(R.id.ln_language);
        tvLanguage = findViewById(R.id.tv_language);
    }

    public void loadLanguage() {
        String langPref = "Language";
        SharedPreferences prefs = getSharedPreferences("CommonPrefs",
                Activity.MODE_PRIVATE);
        String language = prefs.getString(langPref, "en");
        if (language.equals("en")) {
            tvLanguage.setText("English");
        } else {
            tvLanguage.setText("日本語");
        }
    }

    private void showChangeLanguageDialog() {
        AlertDialog.Builder dialogError = new AlertDialog.Builder(this);
// ...Irrelevant code for customizing the buttons and title
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_change_language, null);
        dialogError.setView(dialogView);
        dialogError.setCancelable(false);
        // set the custom dialog components - text, image and button
        TextView tvEnglish = (TextView) dialogView.findViewById(R.id.tv_english);
        TextView tvJapan = (TextView) dialogView.findViewById(R.id.tv_japan);
        final AlertDialog dialog = dialogError.show();
        tvEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateLanguage(getApplicationContext(), "en");
            }
        });
        tvJapan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateLanguage(getApplicationContext(), "ja");

            }
        });
    }
    public void updateLanguage(Context ctx, String lang) {
        Configuration cfg = new Configuration();
        if (!TextUtils.isEmpty(lang))
            cfg.locale = new Locale(lang);
        else
            cfg.locale = Locale.getDefault();

        ctx.getResources().updateConfiguration(cfg, null);
        saveLocale(lang);
        Intent i = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage( getBaseContext().getPackageName() );
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    public void saveLocale(String lang) {
        String langPref = "Language";
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("CommonPrefs",
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(langPref, lang);
        editor.commit();

        SharedPreferences mPrefs = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.commit();
    }
}
