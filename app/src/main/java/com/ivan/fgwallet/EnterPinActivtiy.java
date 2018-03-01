package com.ivan.fgwallet;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.ivan.fgwallet.helper.PrefManager;
import com.kaopiz.kprogresshud.KProgressHUD;

public class EnterPinActivtiy extends AppCompatActivity {
    public final String TAG = "VOLLEY";
    String tag_json_obj = "json_obj_req";
    KProgressHUD progress_dialog;
    PrefManager prefManager;

    TextView bt_restoreWallet;
    EditText _pin;

    String pin = "";

    private void init() {
        bt_restoreWallet = findViewById(R.id.bt_restoreWallet);
        _pin = findViewById(R.id.pin);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_pin_activtiy);
        getSupportActionBar().hide();
//        ButterKnife.inject(this);
        init();
        prefManager = new PrefManager(getApplicationContext());
        pin = prefManager.getpref(PrefManager.KEY_PIN);
        bt_restoreWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (_pin.getText().toString().equals("")) {
//                    _pin.setError("Please enter your pin");
                    Snackbar.make(getWindow().getDecorView().getRootView(), "Please enter your pin", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }else{
                    if (_pin.getText().toString().equals(pin)) {
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        finish();
                    } else {
//                        _pin.setError("Your PIN not match");
                        Snackbar.make(getWindow().getDecorView().getRootView(), "Your PIN not match", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }

                }

            }
        });
    }
}
