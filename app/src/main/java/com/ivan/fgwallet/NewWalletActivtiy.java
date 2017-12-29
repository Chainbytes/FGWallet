package com.ivan.fgwallet;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ivan.fgwallet.helper.PrefManager;
import com.ivan.fgwallet.utils.Constant;
import com.ivan.fgwallet.volley.AppController;
import com.hbb20.CountryCodePicker;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NewWalletActivtiy extends AppCompatActivity {
    public final String TAG = "VOLLEY";
    String tag_json_obj = "json_obj_req";
    KProgressHUD progress_dialog;
    private CountryCodePicker ccp;

    TextView bt_restoreWallet;
    EditText _number;
    EditText _newPin;
    EditText _reenterPin;


    String phoneText;
    String address;

    private void init() {
        bt_restoreWallet = findViewById(R.id.bt_restoreWallet);
        _number = findViewById(R.id.number);
        _newPin = findViewById(R.id.newPin);
        _reenterPin = findViewById(R.id.reenterPin);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_wallet_activtiy);
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        getSupportActionBar().hide();
//        ButterKnife.inject(this);
        init();
        if (!new PrefManager(NewWalletActivtiy.this).getpref(PrefManager.KEY_ADDRESS).equals("")) {
            address = new PrefManager(NewWalletActivtiy.this).getpref(PrefManager.KEY_ADDRESS);

        }
        ccp.setCountryForPhoneCode(81);
        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                Constant.COUNTRY_CODE = "+" + ccp.getSelectedCountryCode();
            }
        });
//        final ArrayList<String> arrCode = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.country_code)));
//        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.country_code,R.id.textId,arrCode);
//        countryCode.setAdapter(stringArrayAdapter);
//        countryCode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                System.out.println(arrCode.get(i));
//                Constant.COUNTRY_CODE = arrCode.get(i);
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
        bt_restoreWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phoneText = _number.getText().toString();
                if (phoneText.length() < 10) {
//                    _number.setError("Please enter mobile number");
                    Snackbar.make(getWindow().getDecorView().getRootView(), "Please enter mobile number", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else if (_newPin.getText().toString().equals("")) {
//                    _newPin.setError("Please enter new pin");
                    Snackbar.make(getWindow().getDecorView().getRootView(), "Please enter new pin", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else if (_reenterPin.getText().toString().equals("")) {
//                    _reenterPin.setError("Please enter new pin");
                    Snackbar.make(getWindow().getDecorView().getRootView(), "Please enter new pin", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else if (!_reenterPin.getText().toString().equals(_newPin.getText().toString())) {
//                    _reenterPin.setError("Your pin not match");
                    Snackbar.make(getWindow().getDecorView().getRootView(), "Your pin not match", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    createWallet(Constant.COUNTRY_CODE + phoneText, _newPin.getText().toString(), _reenterPin.getText().toString());
                }
            }
        });

    }

    public void createWallet(final String number, final String newPin, String reenterPin) {
        String path = "http://128.199.129.208/api/user/verification";
        System.out.println(path);
        progress_dialog = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        progress_dialog.show();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("phone", number);
        params.put("reset", "false");

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                path, new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        String message = "";
                        try {
                            message = response.getString("status");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (message.equals("SUCCESS")) {
                            Intent intent = new Intent(NewWalletActivtiy.this, SmsActivity.class);
                            intent.putExtra("number", number);
                            intent.putExtra("pin", newPin);
                            startActivity(intent);
                            finish();
//                            Intent intent = new Intent(NewWalletActivtiy.this, RecoveryPhraseActivity.class);
//                            intent.putExtra("number", number);
//                            intent.putExtra("pin", newPin);
//                            startActivity(intent);
//                            finish();
                        } else {
                            String msg = "Something went wrong , Please try later";
                            int code = 0;
                            try {
                                JSONObject ojb = response.getJSONObject("data");
                                msg = ojb.getString("message");
                                code = ojb.getInt("code");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (code == 30) {
                                Intent intent = new Intent(NewWalletActivtiy.this, SmsActivity.class);
                                intent.putExtra("number", number);
                                intent.putExtra("pin", newPin);
                                startActivity(intent);
                                finish();
                            } else {
//                                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                                Snackbar.make(getWindow().getDecorView().getRootView(), msg, Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
                        }
                        progress_dialog.dismiss();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
//                Toast.makeText(getApplicationContext(), "Can't connect to server!", Toast.LENGTH_LONG).show();
                Snackbar.make(getWindow().getDecorView().getRootView(), "Can't connect to server!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                progress_dialog.dismiss();
            }
        }) {
            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                return headers;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }
}
