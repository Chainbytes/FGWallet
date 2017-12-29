package com.ivan.fgwallet.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ivan.fgwallet.R;
import com.ivan.fgwallet.ReciveActivtiy;
import com.ivan.fgwallet.ScannerActivity;
import com.ivan.fgwallet.SendBtcActivity;
import com.ivan.fgwallet.helper.PrefManager;
import com.ivan.fgwallet.interfaces.NetworkCallBack;
import com.ivan.fgwallet.listener.ChangeTitleListener;
import com.ivan.fgwallet.utils.Constant;
import com.ivan.fgwallet.utils.MyClipboardManager;
import com.ivan.fgwallet.volley.AppController;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class WalletMenuFragment extends Fragment {
    public final String TAG = "VOLLEY";
    String tag_json_obj = "json_obj_req";
    KProgressHUD progress_dialog;
    Double curency;
    BigDecimal totalBalance;
    String address;
    TextView tv_balance, tv_address, tv_jpy;
    ImageView qrimage;
    ImageView refresh;
    Spinner spinner;

    private static List<WalletMenuFragment> instances = new ArrayList<WalletMenuFragment>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instances.add(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        instances.remove(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        instances.remove(this);
    }

    public static List<WalletMenuFragment> getInstances() {
        return instances;
    }

    public void changeAddress() {
        if (!new PrefManager(getActivity()).getpref(PrefManager.KEY_ADDRESS).equals("")) {
            address = new PrefManager(getActivity()).getpref(PrefManager.KEY_ADDRESS);
        }
        tv_address.setText(address);
        qrimage.setImageBitmap(creatQrCode("address", address));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_wallet_menu, container, false);
        if (!new PrefManager(getActivity()).getpref(PrefManager.KEY_ADDRESS).equals("")) {
            address = new PrefManager(getActivity()).getpref(PrefManager.KEY_ADDRESS);
        }
        tv_balance = (TextView) rootView.findViewById(R.id.tv_balance);
        qrimage = (ImageView) rootView.findViewById(R.id.qrimage);
        tv_address = (TextView) rootView.findViewById(R.id.btn_address);
        tv_jpy = (TextView) rootView.findViewById(R.id.tv_jpy);
        refresh = (ImageView) rootView.findViewById(R.id.refresh);
        spinner = (Spinner) rootView.findViewById(R.id.currencSisgn);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshBtc();
            }
        });
        refreshBtc();

//        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String>(getActivity(),R.layout.curncy_spn,R.id.textcurncy,Utils.currency());
//        spinner.setAdapter(stringArrayAdapter);
//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                refreshBtc();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
        rootView.findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), SendBtcActivity.class));
            }
        });
        rootView.findViewById(R.id.recive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ReciveActivtiy.class));
            }
        });
        rootView.findViewById(R.id.camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), ScannerActivity.class);
                startActivityForResult(i, 1);
            }
        });
//        tv_address.setText(address);
//        qrimage.setImageBitmap(creatQrCode("address",address));
        qrimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MyClipboardManager().copyToClipboard(getActivity(), address);
//                Toast.makeText(getActivity(), "Address Copied", Toast.LENGTH_SHORT).show();
                Snackbar.make(getView(), "Address Copied", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        tv_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MyClipboardManager().copyToClipboard(getActivity(), address);
                Snackbar.make(getView(), "Address Copied", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });
        rootView.findViewById(R.id.copy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MyClipboardManager().copyToClipboard(getActivity(), address);
                Snackbar.make(getView(), "Address Copied", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
//        getBalance();
        ChangeTitleListener.getIntance().setTitle("FG Wallet");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra("result");
                PrefManager prefManager = new PrefManager(getActivity());
                prefManager.setPref(PrefManager.KEY_RESULT_SENT, result.split("[?]")[0]);
                Intent intent = new Intent(getActivity(), SendBtcActivity.class);
                startActivity(intent);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
//                Toast.makeText(getActivity(), "User cancel scanning", Toast.LENGTH_SHORT).show();
                Snackbar.make(getView(), "User cancel scanning", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        }
    }

    public void getLastAddress() {
        String path = "http://128.199.129.208/api/wallet/last_address";
        HashMap<String, String> params = new HashMap<String, String>();
        String apiToken = "";
        if (!new PrefManager(getActivity()).getpref(PrefManager.KEY_API_TOKEN).equals("")) {
            apiToken = new PrefManager(getActivity()).getpref(PrefManager.KEY_API_TOKEN);
        }
        params.put("api_token", apiToken);

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
                            String address = "";
                            try {
                                JSONObject ojb = response.getJSONObject("data");
                                JSONObject jsonAddress = ojb.getJSONObject("address");
                                address = jsonAddress.getString("address");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            PrefManager prefManager = new PrefManager(getActivity());
                            prefManager.setPref(PrefManager.KEY_ADDRESS, address);
                            tv_address.setText(address);
                            qrimage.setImageBitmap(creatQrCode("address", address));
                            getCurencyRate();
                        } else {
                            String msg = "Something went wrong , Please try later";
                            try {
                                JSONObject ojb = response.getJSONObject("data");
                                msg = ojb.getString("message");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
//                            Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                            Snackbar.make(getView(), msg, Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            progress_dialog.dismiss();
                        }
//                        progress_dialog.dismiss();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
//                Toast.makeText(getActivity(), "Can't connect to server!", Toast.LENGTH_LONG).show();
                Snackbar.make(getView(), "Can't connect to server!", Snackbar.LENGTH_LONG)
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

    public void getCurencyRate() {
        String path = "https://api.coindesk.com/v1/bpi/currentprice/JPY.json";

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                path, new JSONObject(),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        String message = "";
                        try {
                            JSONObject jsonObject = response.getJSONObject("bpi");
                            JSONObject jsoJPY = jsonObject.getJSONObject("JPY");
                            Constant.CURRENCY_JPY = Double.valueOf(jsoJPY.getString("rate_float"));
                            DecimalFormat df = new DecimalFormat("0");

                            if (jsoJPY.getString("rate_float") == null || jsoJPY.getString("rate_float").equals("")) {
                                getCurencyRate();
                                return;
                            }
                            curency = Double.valueOf(jsoJPY.getString("rate_float")) * Double.valueOf(String.valueOf(totalBalance));
                            tv_jpy.setText("￥ " + df.format(curency));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        progress_dialog.dismiss();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
//                Toast.makeText(getActivity(), "Can't connect to server!", Toast.LENGTH_LONG).show();
                Snackbar.make(getView(), "Can't connect to server!", Snackbar.LENGTH_LONG)
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

    public void getBalance() {
        String path = "http://128.199.129.208/api/wallet/balance";
        String apiToken = "";
        if (!new PrefManager(getActivity().getApplicationContext()).getpref(PrefManager.KEY_API_TOKEN).equals("")) {
            apiToken = new PrefManager(getActivity()).getpref(PrefManager.KEY_API_TOKEN);
        }
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("api_token", apiToken);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                path, new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        String message = "";
                        DecimalFormat df = new DecimalFormat("0.00000000");
                        try {
                            message = response.getString("status");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (message.equals("SUCCESS")) {
//                            Toast.makeText(getActivity().getApplicationContext(), "success", Toast.LENGTH_LONG).show();
                            try {
                                JSONObject ojb = response.getJSONObject("data");
                                JSONObject address = ojb.getJSONObject("address");
                                Double balance = Double.valueOf(address.getString("available_balance"));
                                Double balancePending = Double.valueOf(address.getString("pending_received_balance"));
                                totalBalance = BigDecimal.valueOf(balance + balancePending);

                                PrefManager prefManager = new PrefManager(getActivity());
                                prefManager.setPref(PrefManager.KEY_TOTAL_BALANCE, String.valueOf(totalBalance));
                                tv_balance.setText(df.format(totalBalance) + " BTC");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
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

    NetworkCallBack networkCallBack = new NetworkCallBack() {
        @Override
        public void callBack(String response) {

            try {
                JSONObject jsonObject = new JSONObject(response);
                tv_balance.setText(jsonObject.getString("balance") + " BTC");
                tv_address.setText(address);
                qrimage.setImageBitmap(creatQrCode("Bitcoin", address));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };


    public static Bitmap creatQrCode(String tag, String data) {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = null;
        try {
            matrix = writer.encode(data, BarcodeFormat.QR_CODE, 150, 150);
        } catch (WriterException ex) {
            ex.printStackTrace();
        }
        Bitmap bmp = Bitmap.createBitmap(150, 150, Bitmap.Config.ARGB_8888);
        for (int x = 0; x < 150; x++) {
            for (int y = 0; y < 150; y++) {
                bmp.setPixel(x, y, matrix.get(x, y) ? Color.BLACK : Color.WHITE);
            }
        }
        return bmp;
    }

    public void refreshBtc() {
        String path = "http://128.199.129.208/api/wallet/balance";
        progress_dialog = KProgressHUD.create(getActivity())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        progress_dialog.show();
        String apiToken = "";
        if (!new PrefManager(getActivity().getApplicationContext()).getpref(PrefManager.KEY_API_TOKEN).equals("")) {
            apiToken = new PrefManager(getActivity()).getpref(PrefManager.KEY_API_TOKEN);
        }
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("api_token", apiToken);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                path, new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        String message = "";
                        DecimalFormat df = new DecimalFormat("0.00000000");

                        try {
                            message = response.getString("status");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (message.equals("SUCCESS")) {
//                            Toast.makeText(getActivity().getApplicationContext(), "success", Toast.LENGTH_LONG).show();
                            try {
                                JSONObject ojb = response.getJSONObject("data");
                                JSONObject address = ojb.getJSONObject("address");
                                Double balance = Double.valueOf(address.getString("available_balance"));
                                Double balancePending = Double.valueOf(address.getString("pending_received_balance"));
                                totalBalance = BigDecimal.valueOf(balance + balancePending);
                                PrefManager prefManager = new PrefManager(getActivity());
                                prefManager.setPref(PrefManager.KEY_TOTAL_BALANCE, String.valueOf(totalBalance));
                                tv_balance.setText(df.format(totalBalance) + " BTC");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            getLastAddress();
                        } else {
                            String msg = "Something went wrong , Please try later";
                            try {
                                JSONObject ojb = response.getJSONObject("data");
                                msg = ojb.getString("message");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
//                            Toast.makeText(getActivity().getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                            Snackbar.make(getView(), msg, Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            progress_dialog.dismiss();
                        }
//                        progress_dialog.dismiss();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
//                Toast.makeText(getActivity().getApplicationContext(), "Can't connect to server!", Toast.LENGTH_LONG).show();
                Snackbar.make(getView(), "Can't connect to server!", Snackbar.LENGTH_LONG)
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
