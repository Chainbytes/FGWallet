package com.breadwallet.presenter.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.breadwallet.R;
import com.breadwallet.presenter.entities.RequestObject;
import com.breadwallet.presenter.fragments.FragmentScanResult;
import com.breadwallet.tools.animation.BRAnimator;
import com.breadwallet.tools.manager.BRClipboardManager;
import com.breadwallet.tools.security.RequestHandler;
import com.breadwallet.wallet.BRWalletManager;

public class SendWalletActivity extends AppCompatActivity {
    private ImageButton scanQRButton;
    public EditText addressEditText;
    Button payAddressFromClipboardButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_wallet);
        init();
        clickListner();
    }

    private void init() {
        scanQRButton = (ImageButton) findViewById(R.id.scanQRButton);
        payAddressFromClipboardButton = (Button) findViewById(R.id.main_button_pay_address_from_clipboard);
        addressEditText = (EditText) findViewById(R.id.address_edit_text);
    }

    private void clickListner() {
        scanQRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BRAnimator.checkTheMultipressingAvailability()) {
                    BRAnimator.animateDecoderFragment();
                }
            }
        });

        payAddressFromClipboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog[] alert = {null};
                final AlertDialog.Builder builder = new AlertDialog.Builder(SendWalletActivity.this);
                if (BRAnimator.checkTheMultipressingAvailability()) {

                    final String bitcoinUrl = BRClipboardManager.readFromClipboard(SendWalletActivity.this);
                    String ifAddress = null;
                    RequestObject obj = RequestHandler.getRequestFromString(bitcoinUrl);
                    if (obj == null) {
                        //builder.setTitle(getResources().getString(R.string.alert));
                        builder.setMessage(getResources().getString(R.string.mainfragment_clipboard_invalid_data));
                        builder.setNeutralButton(getResources().getString(R.string.ok),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alert[0] = builder.create();
                        alert[0].show();
                        BRClipboardManager.copyToClipboard(SendWalletActivity.this, "");
                        addressEditText.setText("");
                        return;
                    }
                    if (!addressEditText.getText().toString().isEmpty()) {
                        ifAddress = addressEditText.getText().toString();
                    } else {
                        ifAddress = obj.address;
                    }
                    if (ifAddress == null) {
                        //builder.setTitle(getResources().getString(R.string.alert));
                        builder.setMessage(getResources().getString(R.string.mainfragment_clipboard_invalid_data));
                        builder.setNeutralButton(getResources().getString(R.string.ok),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alert[0] = builder.create();
                        alert[0].show();
                        BRClipboardManager.copyToClipboard(SendWalletActivity.this, "");
                        addressEditText.setText("");
                        return;
                    }
//                    final String finalAddress = tempAddress;
                    BRWalletManager wm = BRWalletManager.getInstance(SendWalletActivity.this);

                    if (wm.isValidBitcoinPrivateKey(ifAddress) || wm.isValidBitcoinBIP38Key(ifAddress)) {
                        BRWalletManager.getInstance(SendWalletActivity.this).confirmSweep(SendWalletActivity.this, ifAddress);
                        addressEditText.setText("");
                        return;
                    }

                    if (checkIfAddressIsValid(ifAddress)) {
                        final BRWalletManager m = BRWalletManager.getInstance(SendWalletActivity.this);
                        final String finalIfAddress = ifAddress;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                final boolean contained = m.addressContainedInWallet(finalIfAddress);
                                final boolean used = m.addressIsUsed(finalIfAddress);
                                SendWalletActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (contained) {

                                            //builder.setTitle(getResources().getString(R.string.alert));
                                            builder.setMessage(getResources().getString(R.string.address_already_in_your_wallet));
                                            builder.setNeutralButton(getResources().getString(R.string.ok),
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.dismiss();
                                                        }
                                                    });
                                            alert[0] = builder.create();
                                            alert[0].show();
                                            BRClipboardManager.copyToClipboard(SendWalletActivity.this, "");
                                            addressEditText.setText("");


                                        } else if (used) {
                                            builder.setTitle(getResources().getString(R.string.warning));

                                            builder.setMessage(getResources().getString(R.string.address_already_used));
                                            builder.setPositiveButton(getResources().getString(R.string.ignore),
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.dismiss();
                                                            FragmentScanResult.address = finalIfAddress;
                                                            BRAnimator.animateScanResultFragment();
                                                        }
                                                    });
                                            builder.setNegativeButton(getResources().getString(R.string.cancel),
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.dismiss();
                                                        }
                                                    });
                                            alert[0] = builder.create();
                                            alert[0].show();
                                        } else {
//                                FragmentScanResult.address = finalAddress;
//                                BRAnimator.animateScanResultFragment();
                                            RequestHandler.processRequest((SendWalletActivity) SendWalletActivity.this, bitcoinUrl);
                                        }
                                    }
                                });
                            }
                        }).start();

                    } else {
                        //builder.setTitle(getResources().getString(R.string.alert));
                        builder.setMessage(getResources().getString(R.string.mainfragment_clipboard_invalid_data));
                        builder.setNeutralButton(getResources().getString(R.string.ok),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alert[0] = builder.create();
                        alert[0].show();
                        BRClipboardManager.copyToClipboard(SendWalletActivity.this, "");
                        addressEditText.setText("");
                    }
                }
            }

        });

    }

    private boolean checkIfAddressIsValid(String str) {
        return BRWalletManager.validateAddress(str.trim());
    }
}
