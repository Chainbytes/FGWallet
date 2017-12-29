package com.ivan.fgwallet;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ivan.fgwallet.helper.PrefManager;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.ArrayList;
import java.util.Random;

public class RecoveryPhraseActivity2 extends AppCompatActivity {
    public final String TAG = "VOLLEY";
    String tag_json_obj = "json_obj_req";
    KProgressHUD progress_dialog;
    int thSelect;
    String[] stringsRecovery;

    TextView tv1;
    TextView tv2;
    TextView tv3;
    TextView tv4;
    TextView tv5;
    TextView tv6;
    TextView tvTitle;
    private void init() {
        tv1 = findViewById(R.id.tv1);
        tv2 = findViewById(R.id.tv2);
        tv3 = findViewById(R.id.tv3);
        tv4 = findViewById(R.id.tv4);
        tv5 = findViewById(R.id.tv5);
        tv6 = findViewById(R.id.tv6);
        tvTitle = findViewById(R.id.tv_title_select);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery_phrase1);
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
        if (!new PrefManager(getApplicationContext()).getpref(PrefManager.KEY_RECOVERY_PHRASE).equals("")) {
            strRecovery = new PrefManager(getApplicationContext()).getpref(PrefManager.KEY_RECOVERY_PHRASE);
        }
        stringsRecovery = strRecovery.split(" ");
        ArrayList<Integer> numsRandom = new ArrayList<>();
        int iNew = 0;
        Random rd = new Random();
        for (int i = 0; i < 6; ) {
            iNew = rd.nextInt(12);
            if (!numsRandom.contains(iNew)) {
                i++;
                numsRandom.add(iNew);
            }
        }

        tv1.setText(stringsRecovery[numsRandom.get(0)]);
        tv2.setText(stringsRecovery[numsRandom.get(1)]);
        tv3.setText(stringsRecovery[numsRandom.get(2)]);
        tv4.setText(stringsRecovery[numsRandom.get(3)]);
        tv5.setText(stringsRecovery[numsRandom.get(4)]);
        tv6.setText(stringsRecovery[numsRandom.get(5)]);

        thSelect = rd.nextInt(12);
        while (!numsRandom.contains(thSelect)) {
            thSelect = rd.nextInt(12);
        }
        if (thSelect == 0) {
            tvTitle.setText(getResources().getString(R.string.msg_recovery1));
        } else if (thSelect == 1) {
            tvTitle.setText(getResources().getString(R.string.msg_recovery2));
        } else if (thSelect == 2) {
            tvTitle.setText(getResources().getString(R.string.msg_recovery3));
        } else if (thSelect == 3) {
            tvTitle.setText(getResources().getString(R.string.msg_recovery4));
        } else if (thSelect == 4) {
            tvTitle.setText(getResources().getString(R.string.msg_recovery5));
        } else if (thSelect == 5) {
            tvTitle.setText(getResources().getString(R.string.msg_recovery6));
        } else if (thSelect == 6) {
            tvTitle.setText(getResources().getString(R.string.msg_recovery7));
        } else if (thSelect == 7) {
            tvTitle.setText(getResources().getString(R.string.msg_recovery8));
        } else if (thSelect == 8) {
            tvTitle.setText(getResources().getString(R.string.msg_recovery9));
        } else if (thSelect == 9) {
            tvTitle.setText(getResources().getString(R.string.msg_recovery10));
        } else if (thSelect == 10) {
            tvTitle.setText(getResources().getString(R.string.msg_recovery11));
        } else if (thSelect == 11) {
            tvTitle.setText(getResources().getString(R.string.msg_recovery12));
        }
//        tvTitle.setText("select the " + (thSelect + 1) + "th word of your recovery phrase from the list below");

        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tv1.getText().toString().equals(stringsRecovery[thSelect])) {
                    tv1.setTextColor(getResources().getColor(R.color.green));
                    new CountDownTimer(1000, 1000) {
                        @Override
                        public void onTick(long l) {
                        }

                        @Override
                        public void onFinish() {
                            Bundle b = getIntent().getExtras();
                            String number = "", pin = "";
                            if (b != null) {
                                number = b.getString("number");
                                pin = b.getString("pin");
                            }
                            Intent intent = new Intent(RecoveryPhraseActivity2.this, HomeActivity.class);
                            intent.putExtra("number", number);
                            intent.putExtra("pin", pin);
                            startActivity(intent);
                            finish();
                        }
                    }.start();
                } else {
                    tv1.setTextColor(getResources().getColor(R.color.red));
                    new CountDownTimer(1000, 1000) {
                        @Override
                        public void onTick(long l) {
                        }

                        @Override
                        public void onFinish() {
                            finish();
                        }
                    }.start();
                }
            }
        });
        tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tv2.getText().toString().equals(stringsRecovery[thSelect])) {
                    tv2.setTextColor(getResources().getColor(R.color.green));
                    new CountDownTimer(1000, 1000) {
                        @Override
                        public void onTick(long l) {
                        }

                        @Override
                        public void onFinish() {
                            Bundle b = getIntent().getExtras();
                            String number = "", pin = "";
                            if (b != null) {
                                number = b.getString("number");
                                pin = b.getString("pin");
                            }
                            Intent intent = new Intent(RecoveryPhraseActivity2.this, HomeActivity.class);
                            intent.putExtra("number", number);
                            intent.putExtra("pin", pin);
                            startActivity(intent);
                            finish();
                        }
                    }.start();
                } else {
                    tv2.setTextColor(getResources().getColor(R.color.red));
                    new CountDownTimer(1000, 1000) {
                        @Override
                        public void onTick(long l) {
                        }

                        @Override
                        public void onFinish() {
                            finish();
                        }
                    }.start();
                }
            }
        });
        tv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tv3.getText().toString().equals(stringsRecovery[thSelect])) {
                    tv3.setTextColor(getResources().getColor(R.color.green));
                    new CountDownTimer(1000, 1000) {
                        @Override
                        public void onTick(long l) {
                        }

                        @Override
                        public void onFinish() {
                            Bundle b = getIntent().getExtras();
                            String number = "", pin = "";
                            if (b != null) {
                                number = b.getString("number");
                                pin = b.getString("pin");
                            }
                            Intent intent = new Intent(RecoveryPhraseActivity2.this, HomeActivity.class);
                            intent.putExtra("number", number);
                            intent.putExtra("pin", pin);
                            startActivity(intent);
                            finish();
                        }
                    }.start();
                } else {
                    tv3.setTextColor(getResources().getColor(R.color.red));
                    new CountDownTimer(1000, 1000) {
                        @Override
                        public void onTick(long l) {
                        }

                        @Override
                        public void onFinish() {
                            finish();
                        }
                    }.start();
                }
            }
        });
        tv4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tv4.getText().toString().equals(stringsRecovery[thSelect])) {
                    tv4.setTextColor(getResources().getColor(R.color.green));
                    new CountDownTimer(1000, 1000) {
                        @Override
                        public void onTick(long l) {
                        }

                        @Override
                        public void onFinish() {
                            Bundle b = getIntent().getExtras();
                            String number = "", pin = "";
                            if (b != null) {
                                number = b.getString("number");
                                pin = b.getString("pin");
                            }
                            Intent intent = new Intent(RecoveryPhraseActivity2.this, HomeActivity.class);
                            intent.putExtra("number", number);
                            intent.putExtra("pin", pin);
                            startActivity(intent);
                            finish();
                        }
                    }.start();
                } else {
                    tv4.setTextColor(getResources().getColor(R.color.red));
                    new CountDownTimer(1000, 1000) {
                        @Override
                        public void onTick(long l) {
                        }

                        @Override
                        public void onFinish() {
                            finish();
                        }
                    }.start();
                }
            }
        });
        tv5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tv5.getText().toString().equals(stringsRecovery[thSelect])) {
                    tv5.setTextColor(getResources().getColor(R.color.green));
                    new CountDownTimer(1000, 1000) {
                        @Override
                        public void onTick(long l) {
                        }

                        @Override
                        public void onFinish() {
                            Bundle b = getIntent().getExtras();
                            String number = "", pin = "";
                            if (b != null) {
                                number = b.getString("number");
                                pin = b.getString("pin");
                            }
                            Intent intent = new Intent(RecoveryPhraseActivity2.this, HomeActivity.class);
                            intent.putExtra("number", number);
                            intent.putExtra("pin", pin);
                            startActivity(intent);
                            finish();
                        }
                    }.start();
                } else {
                    tv5.setTextColor(getResources().getColor(R.color.red));
                    new CountDownTimer(1000, 1000) {
                        @Override
                        public void onTick(long l) {
                        }

                        @Override
                        public void onFinish() {
                            finish();
                        }
                    }.start();
                }
            }
        });
        tv6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tv6.getText().toString().equals(stringsRecovery[thSelect])) {
                    tv6.setTextColor(getResources().getColor(R.color.green));
                    new CountDownTimer(1000, 1000) {
                        @Override
                        public void onTick(long l) {
                        }

                        @Override
                        public void onFinish() {
                            Bundle b = getIntent().getExtras();
                            String number = "", pin = "";
                            if (b != null) {
                                number = b.getString("number");
                                pin = b.getString("pin");
                            }
                            Intent intent = new Intent(RecoveryPhraseActivity2.this, HomeActivity.class);
                            intent.putExtra("number", number);
                            intent.putExtra("pin", pin);
                            startActivity(intent);
                            finish();
                        }
                    }.start();
                } else {
                    tv6.setTextColor(getResources().getColor(R.color.red));
                    new CountDownTimer(1000, 1000) {
                        @Override
                        public void onTick(long l) {
                        }

                        @Override
                        public void onFinish() {
                            finish();
                        }
                    }.start();
                }
            }
        });
    }
}
