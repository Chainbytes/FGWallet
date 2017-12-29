package com.ivan.fgwallet;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ivan.fgwallet.HomeActivity;
import com.ivan.fgwallet.R;
import com.ivan.fgwallet.RestoreWalletActivtiy;
import com.ivan.fgwallet.helper.PrefManager;
import com.ivan.fgwallet.interfaces.NetworkCallBack;
import com.ivan.fgwallet.utils.Constant;
import com.ivan.fgwallet.volley.AppController;
import com.google.android.gms.internal.in;
import com.hbb20.CountryCodePicker;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChangePasswordActivtiy extends AppCompatActivity {
    public final String TAG = "VOLLEY";
    String tag_json_obj = "json_obj_req";
    KProgressHUD progress_dialog;

    EditText edtOldPin;
    EditText edtNewPin;
    EditText reEnterNewPin;
    TextView btnChangePassword;

    private void init() {
        edtOldPin = findViewById(R.id.old_pin);
        edtNewPin = findViewById(R.id.new_pin);
        reEnterNewPin = findViewById(R.id.re_enter_new_pin);
        btnChangePassword = findViewById(R.id.btn_change_password);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password_activtiy);
        getSupportActionBar().hide();
//        ButterKnife.inject(this);
        init();
        ImageView imageView = (ImageView) findViewById(R.id.menu);
        imageView.setImageResource(R.mipmap.backmenu);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        TextView textView = (TextView) findViewById(R.id.title);
        textView.setText(getResources().getString(R.string.change_password));
        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                String OldPin = edtOldPin.getText().toString();
                if (edtOldPin.getText().toString().equals("")) {
//                    edtOldPin.setError("Please enter old pin");
                    Snackbar.make(getWindow().getDecorView().getRootView(), "Please enter old pin", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else if (edtNewPin.getText().toString().equals("")) {
//                    edtNewPin.setError("Please enter new pin");
                    Snackbar.make(getWindow().getDecorView().getRootView(), "Please enter new pin", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else if (reEnterNewPin.getText().toString().equals("")) {
//                    reEnterNewPin.setError("Please re-enter new pin");
                    Snackbar.make(getWindow().getDecorView().getRootView(), "Please re-enter new pin", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else if (!edtNewPin.getText().toString().equals(reEnterNewPin.getText().toString())) {
//                    reEnterNewPin.setError("Your pin not match");
                    Snackbar.make(getWindow().getDecorView().getRootView(), "Your pin not match", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    changePassword();
                }
            }
        });
    }

    public void changePassword() {
        String path = "http://128.199.129.208/api/user/change_pin";
        System.out.println(path);
        progress_dialog = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        progress_dialog.show();
        String apiToken = "";
        if (!new PrefManager(getApplicationContext()).getpref(PrefManager.KEY_API_TOKEN).equals("")) {
            apiToken = new PrefManager(getApplicationContext()).getpref(PrefManager.KEY_API_TOKEN);
        }
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("api_token", apiToken);
        params.put("old_pin", edtOldPin.getText().toString());
        params.put("new_pin", edtNewPin.getText().toString());

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
//                            Toast.makeText(getApplicationContext(), "Change PIN success", Toast.LENGTH_LONG).show();
                            Snackbar.make(getWindow().getDecorView().getRootView(), "Change PIN success", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
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
