package com.gt.login;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


public class VerifyEmailFragment extends Fragment implements View.OnClickListener {

    private EditText digit1;
    private EditText digit2;
    private EditText digit3;
    private EditText digit4;
    private Button resendCodeButton;
    private Button verifyButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_verify_email, container, false);

        digit1=view.findViewById(R.id.digit1);
        digit2=view.findViewById(R.id.digit2);
        digit3=view.findViewById(R.id.digit3);
        digit4=view.findViewById(R.id.digit4);

        resendCodeButton=view.findViewById(R.id.resendCodeButton);
        verifyButton=view.findViewById(R.id.verifyButton);

        resendCodeButton.setOnClickListener(this);
        verifyButton.setOnClickListener(this);

        digit1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(editable.length()==1){
                    digit2.requestFocus();
                }

            }
        });

        digit2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(editable.length()==1){
                    digit3.requestFocus();
                }

            }
        });

        digit3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(editable.length()==1){
                    digit4.requestFocus();
                }

            }
        });

        digit4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.resendCodeButton:
                break;

            case R.id.verifyButton:
                break;

        }

    }
    
}