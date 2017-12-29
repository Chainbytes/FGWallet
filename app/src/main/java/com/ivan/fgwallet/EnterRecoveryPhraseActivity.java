package com.ivan.fgwallet;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ivan.fgwallet.helper.PrefManager;
import com.kaopiz.kprogresshud.KProgressHUD;

public class EnterRecoveryPhraseActivity extends AppCompatActivity {
    public final String TAG = "VOLLEY";
    String tag_json_obj = "json_obj_req";
    KProgressHUD progress_dialog;
    String strRecovery = "";

    TextView btnNext;
    EditText edtRecovery;

    private void init() {
        btnNext = findViewById(R.id.btn_next);
        edtRecovery = findViewById(R.id.edt_recovery);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_recovery_phrase);
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
        textView.setText(getResources().getString(R.string.recovery_phrase));
        btnNext.setVisibility(View.VISIBLE);
        if(!new PrefManager(getApplicationContext()).getpref(PrefManager.KEY_RECOVERY_PHRASE).equals("")){
            strRecovery = new PrefManager(getApplicationContext()).getpref(PrefManager.KEY_RECOVERY_PHRASE);
        }
        final String finalStrRecovery = strRecovery;
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtRecovery.getText().toString().equals(strRecovery)) {
                    Intent intent = new Intent(EnterRecoveryPhraseActivity.this, HomeActivity.class);
                    startActivity(intent);
                } else {
                    showNoConnect();
                }
            }
        });
    }

    private void showNoConnect() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Bad Recovery Phrase")
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                return;
                            }
                        }).create().show();
    }
}
