package com.gt.zega;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

@SuppressWarnings("FieldCanBeLocal")
public class VerifyEmailFragment extends Fragment implements View.OnClickListener {

    private EditText digit1;
    private EditText digit2;
    private EditText digit3;
    private EditText digit4;
    private TextView phoneNumber;
    private Button resendCodeButton;
    private Button verifyButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_verify_email, container, false);

        digit1 = view.findViewById(R.id.digit1);
        digit2 = view.findViewById(R.id.digit2);
        digit3 = view.findViewById(R.id.digit3);
        digit4 = view.findViewById(R.id.digit4);

        phoneNumber=view.findViewById(R.id.phoneNumberTextView);

        resendCodeButton = view.findViewById(R.id.resendCodeButton);
        verifyButton = view.findViewById(R.id.verifyButton);

        resendCodeButton.setOnClickListener(this);
        verifyButton.setOnClickListener(this);

        nextEditText(digit1, digit2);
        nextEditText(digit2, digit3);
        nextEditText(digit3, digit4);


        previousEditText(digit1, null);
        previousEditText(digit2, digit1);
        previousEditText(digit3, digit2);
        previousEditText(digit4, digit3);

        return view;
    }

    private void nextEditText(EditText currentEditText, EditText nextEditText) {
        currentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 1) {
                    nextEditText.requestFocus();
                }
            }
        });

    }

    private void previousEditText(EditText currentEditText, EditText previousEditText) {
        currentEditText.setOnKeyListener((View view, int i, KeyEvent keyEvent) -> {

            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && keyEvent.getKeyCode() == KeyEvent.KEYCODE_DEL && currentEditText.getText().toString().isEmpty()) {
                previousEditText.requestFocus();
                return true;

            }
            return false;

        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.resendCodeButton:
                break;

            case R.id.verifyButton:
                break;

        }

    }

}