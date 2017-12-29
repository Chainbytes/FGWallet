package com.ivan.fgwallet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ivan.fgwallet.helper.PrefManager;
import com.kaopiz.kprogresshud.KProgressHUD;

public class RecoveryPhraseActivity extends AppCompatActivity {
    public final String TAG = "VOLLEY";
    String tag_json_obj = "json_obj_req";
    KProgressHUD progress_dialog;

    TextView btnNext;
    TextView tvRecovery;

    private void init() {
        btnNext = findViewById(R.id.btn_next);
        tvRecovery = findViewById(R.id.tv_string_recovery);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery_phrase);
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
        textView.setText("");
        String strRecovery = "";
        if(!new PrefManager(getApplicationContext()).getpref(PrefManager.KEY_RECOVERY_PHRASE).equals("")){
            strRecovery = new PrefManager(getApplicationContext()).getpref(PrefManager.KEY_RECOVERY_PHRASE);
        }
        tvRecovery.setText(strRecovery);

        final String finalStrRecovery = strRecovery;
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle b = getIntent().getExtras();
                String number = "", pin = "";
                if(b != null){
                    number = b.getString("number");
                    pin = b.getString("pin");
                }
                Intent intent = new Intent(RecoveryPhraseActivity.this, RecoveryPhraseActivity1.class);
                intent.putExtra("number", number);
                intent.putExtra("pin", pin);
                startActivity(intent);
//                finish();
            }
        });
    }
}
