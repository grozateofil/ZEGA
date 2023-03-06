package com.gt.zega.fragment;

import static android.content.ContentValues.TAG;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gt.zega.R;
import com.gt.zega.entity.User;
import com.gt.zega.util.Validations;
import com.gt.zega.util.ValidationsImpl;


public class LoginFragment extends Fragment implements View.OnClickListener {

    private TextInputLayout email;
    private TextInputLayout password;

    private Button forgotPasswordButton;
    private Button loginButton;
    private Button registerButton;

    private FirebaseAuth fAuth;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;

    private SharedPreferences sharedPreferences;

    private Validations validations;
    private String userKey;
    private static User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);
        email = view.findViewById(R.id.loginEmail);
        password = view.findViewById(R.id.loginPassword);

        forgotPasswordButton = view.findViewById(R.id.forgotPasswordButton);
        loginButton = view.findViewById(R.id.loginButton);
        registerButton = view.findViewById(R.id.createAccountButton);

        fAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        firebaseUser = fAuth.getCurrentUser();

        sharedPreferences = getContext().getSharedPreferences("Preferences", 0);

        validations = new ValidationsImpl();

        //email.getEditText().setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS|InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        email.getEditText().setCustomInsertionActionModeCallback(getActionModeCallback());
        password.getEditText().setCustomInsertionActionModeCallback(getActionModeCallback());

        forgotPasswordButton.setOnClickListener(this);
        loginButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);
        return view;
    }

    @NonNull
    private ActionMode.Callback getActionModeCallback() {
        return new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {

            }
        };
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.loginButton):
                if (validation()) {
                    firebaseLogin();

                }
                break;

            case (R.id.forgotPasswordButton):
                ResetPasswordFragment forgotPasswordFragment = new ResetPasswordFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.hide(this);
                transaction.add(R.id.content_frame, forgotPasswordFragment);
                transaction.addToBackStack(TAG);
                transaction.commit();
                break;

            case (R.id.createAccountButton):
                RegisterFragment registerFragment = new RegisterFragment();
                FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                fragmentTransaction.hide(this);
                fragmentTransaction.add(R.id.content_frame, registerFragment);
                fragmentTransaction.addToBackStack(TAG);
                fragmentTransaction.commit();
                break;
        }
    }

    private boolean validation() {
        return (validations.emailValidation(email) &
                validations.passwordValidation(password));
    }

    private void firebaseLogin() {
        String emailAddress = email.getEditText().getText().toString().replaceAll("\\s", "");
        String pass = password.getEditText().getText().toString();

        fAuth.signInWithEmailAndPassword(emailAddress, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {


                if (task.isSuccessful()) {
                    email.setError(null);
                    email.setErrorEnabled(false);

                    password.setError(null);
                    password.setErrorEnabled(false);

                    Toast.makeText(getActivity().getApplicationContext(), "Succes!", Toast.LENGTH_SHORT).show();

                    databaseReference.child(fAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            user = dataSnapshot.getValue(User.class);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });

                    HomeFragment homeFragment = new HomeFragment();
                    FragmentManager fragmentManager = getParentFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.content_frame, homeFragment)
                            .commit();

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("LOGIN", emailAddress);
                    editor.commit();


                } else {
                    email.setError("\0");
                    password.setError("\0");
                    Toast.makeText(getActivity().getApplicationContext(), "Email sau parolă incorectă", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawerLayout);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawerLayout);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNDEFINED);
    }

    public static User getCurrentUser() {
        return user;
    }
}