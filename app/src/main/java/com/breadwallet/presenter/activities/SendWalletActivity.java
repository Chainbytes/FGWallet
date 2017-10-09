package com.breadwallet.presenter.activities;

import android.app.Activity;
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

public class SendWalletActivity extends Activity {
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



    }

    private boolean checkIfAddressIsValid(String str) {
        return BRWalletManager.validateAddress(str.trim());
    }
}
