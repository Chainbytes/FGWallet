package com.ivan.fgwallet;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ivan.fgwallet.helper.PrefManager;
import com.ivan.fgwallet.volley.AppController;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthProvider;
import com.kaopiz.kprogresshud.KProgressHUD;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SmsActivity extends AppCompatActivity {
    public final String TAG = "VOLLEY";
    String tag_json_obj = "json_obj_req";
    KProgressHUD progress_dialog;

    EditText inputOtp;

    String Otp, number = "", pin = "";
    private FirebaseAuth mAuth;
    // [END declare_auth]
    boolean mVerificationInProgress = false;
    String mVerificationId;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    ProgressDialog dialog;

    private void init() {
        inputOtp = findViewById(R.id.newPin);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);
        getSupportActionBar().hide();
//        ButterKnife.inject(this);
        init();
        mAuth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(SmsActivity.this);
        dialog.setMessage("Please Wait...");

        findViewById(R.id.btn_verify_otp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//
                if (inputOtp.getText().toString().equals("")) {
//                    inputOtp.setError("Please enter verification code");
                    Snackbar.make(getWindow().getDecorView().getRootView(), "Please enter verification code", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    verify(inputOtp.getText().toString());
                }


            }
        });
        findViewById(R.id.btn_reend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if(number.startsWith("+")){
//                    setOTP(number);
//                }else{
//                    setOTP("+86"+number);
//                }
                //  setOTP(number);

            }
        });

        Bundle b = getIntent().getExtras();
        if (b != null) {
            number = b.getString("number");
            Otp = b.getString("code");
            pin = b.getString("pin");
        }


    }

    public void verify(String s) {
        String path = "http://128.199.129.208/api/user/create";
        System.out.println(path);
        progress_dialog = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please  wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        progress_dialog.show();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("phone", number);
        params.put("pin", pin);
        params.put("code", inputOtp.getText().toString());

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
                            progress_dialog.dismiss();
                            PrefManager prefManager = new PrefManager(getApplicationContext());
                            prefManager.setPref(PrefManager.KEY_PHONE, number);
                            String apiToken = "", strRecovery = "";
                            try {
                                JSONObject ojb = response.getJSONObject("data");
                                apiToken = ojb.getString("api_token");
                                strRecovery = ojb.getString("recovery_phrase");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            prefManager.setPref(PrefManager.KEY_API_TOKEN, apiToken);
                            prefManager.setPref(PrefManager.KEY_RECOVERY_PHRASE, strRecovery);
                            Intent intent = new Intent(SmsActivity.this, RecoveryPhraseActivity.class);
                            startActivity(intent);
//                            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
//                            finish();
                        } else {
                            int code = 0;
                            String msg = "Something went wrong , Please try later";
                            try {
                                JSONObject ojb = response.getJSONObject("data");
                                msg = ojb.getString("message");
                                code = ojb.getInt("code");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (code == 33 || code == 0) {
                                restore(number, pin);
                            } else {
                                progress_dialog.dismiss();
//                                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                                Snackbar.make(getWindow().getDecorView().getRootView(), msg, Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
                        }
//                        progress_dialog.dismiss();
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

    public void restore(final String number, final String newPin) {

        String path = "http://128.199.129.208/api/user/login";
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("phone", number);
        params.put("pin", newPin);

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
                            PrefManager prefManager = new PrefManager(getApplicationContext());
                            prefManager.setPref(PrefManager.KEY_PHONE, number);
                            prefManager.setPref(PrefManager.KEY_PIN, newPin);
                            String apiToken = "", strRecovery = "";
                            try {
                                JSONObject ojb = response.getJSONObject("data");
                                apiToken = ojb.getString("api_token");
                                strRecovery = ojb.getString("recovery_phrase");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            prefManager.setPref(PrefManager.KEY_API_TOKEN, apiToken);
                            prefManager.setPref(PrefManager.KEY_RECOVERY_PHRASE, strRecovery);
                            Intent intent = new Intent(SmsActivity.this, RecoveryPhraseActivity.class);
                            startActivity(intent);
                        } else {
                            String msg = "Something went wrong , Please try later";
                            try {
                                JSONObject ojb = response.getJSONObject("data");
                                msg = ojb.getString("message");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
//                            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                            Snackbar.make(getWindow().getDecorView().getRootView(), msg, Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
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
