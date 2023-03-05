package com.gt.zega.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.gt.zega.R;

public class ResetPasswordConfirmationFragment extends Fragment {

    private TextView textView;
    private Button button;
    private Bundle bundle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_reset_password_confirmation, container, false);

        textView = view.findViewById(R.id.confirmationTextView);
        button = view.findViewById(R.id.backToLoginPageButton);
        bundle = this.getArguments();

        textView.setText("V-am trimis un e-mail la adresa\n" + bundle.getString("email") + " pentru a putea reseta parola.\n\nDaca nu ati primit nimic este posibil ca e-mailul sa-l fi primit in folderul de spam");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginFragment loginFragment = new LoginFragment();
                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, loginFragment)
                        .commit();
            }
        });

        return view;
    }

}