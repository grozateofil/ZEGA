package com.gt.zega;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.gt.zega.fragment.AboutUsFragment;
import com.gt.zega.fragment.AddNewErrorFragment;
import com.gt.zega.fragment.HomeFragment;
import com.gt.zega.fragment.LoginFragment;
import com.gt.zega.fragment.ProfileFragment;
import com.gt.zega.fragment.ReportsFragment;
import com.gt.zega.fragment.SettingsFragment;
import com.gt.zega.fragment.StatisticsFragment;
import com.gt.zega.fragment.SuppliesFragment;
import com.gt.zega.fragment.UserReportFragment;
import com.gt.zega.internetConnection.NetworkChangeListener;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, LoginFragment.OnUserRoleSelectedListener {

    private static final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE
    };
    private static final int PERMISSIONS_REQUEST_CODE = 777;

    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    private static final float END_SCALE = 0.7f;

    private View contentView;
    private View navigationHeader;

    private SharedPreferences sharedPreferences;
    private SharedPreferences sharedPref;

    private TextView userNameHeader;

    private ImageView userPhoto;

    private LoginFragment loginFragment;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    private Menu menu;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private String userRole;

    private ArrayList<String> superUsersList;
    private ArrayList<String> usersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseUser = firebaseAuth.getCurrentUser();

        sharedPreferences = getApplicationContext().getSharedPreferences("Preferences", 0);
        String login = sharedPreferences.getString("LOGIN", null);
//        String userName = sharedPreferences.getString("userData", null);

        sharedPref = getApplicationContext().getSharedPreferences("myPrefs", 0);
        userRole = sharedPref.getString("userRole", "user");

        contentView = findViewById(R.id.holder);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        superUsersList = new ArrayList<>(Arrays.asList(getText(R.string.super_user).toString().split(",")));
        usersList = new ArrayList<>(Arrays.asList(getText(R.string.user).toString().split(",")));

        toolbar.setContentInsetStartWithNavigation(0);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        navigationHeader = navigationView.getHeaderView(0);
        menu = navigationView.getMenu();

        hideAndShowMenuItems();

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.bringToFront();
        menu.getItem(0).setChecked(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawerLayout.isDrawerOpen(navigationView)) {
                    drawerLayout.closeDrawer(navigationView);
                } else {
                    drawerLayout.openDrawer(navigationView);
                }
            }
        });

        drawerLayout.setScrimColor(Color.TRANSPARENT);
        drawerLayout.setDrawerElevation(0);

        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                // Scale the View based on current slide offset
                final float diffScaledOffset = slideOffset * (1f - END_SCALE);
                final float offsetScale = 1 - diffScaledOffset;
                contentView.setScaleX(offsetScale);
                contentView.setScaleY(offsetScale);

                // Translate the View, accounting for the scaled width
                final float xOffset = drawerView.getWidth() * slideOffset;
                final float xOffsetDiff = contentView.getWidth() * diffScaledOffset / 2;
                final float xTranslation = xOffset - xOffsetDiff;
                contentView.setTranslationX(xTranslation);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
            }
        });

        if (hasPermissions()) {

        } else
            requestPermissions();

//        userPhoto = navigationView.getHeaderView(0).findViewById(R.id.userPictureMenuHeader);
//        userPhoto.setOnClickListener(this);

        if (login != null) {
            HomeFragment loginFragment = new HomeFragment();
            setFragment(loginFragment);
        } else {
            loginFragment = new LoginFragment();
            setFragment(loginFragment);
        }

    }

    public void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
    }

    private boolean hasPermissions() {
        if (getApplicationContext() != null && PERMISSIONS != null) {
            for (String permission : PERMISSIONS) {
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSIONS_REQUEST_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            boolean allGranted = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if (view instanceof EditText) {
                Rect outRect = new Rect();
                view.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    view.clearFocus();
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
//            case (R.id.userPictureMenuHeader): {
//                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new ProfileFragment()).commit();
//                navigationView.setCheckedItem(R.id.nav_profile);
//                drawerLayout.closeDrawer(GravityCompat.START);
//                break;
//            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        boolean fragmentSelected = false;
        switch (menuItem.getItemId()) {
            case (R.id.nav_home):
                fragmentSelected = true;
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new HomeFragment()).commit();
                break;

            case (R.id.nav_error):
                fragmentSelected = true;
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new AddNewErrorFragment()).commit();
                break;

            case (R.id.nav_supplies):
                fragmentSelected = true;
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new SuppliesFragment()).commit();
                break;

            case (R.id.nav_profile):
                fragmentSelected = true;
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new ProfileFragment()).commit();
                break;

//            case (R.id.nav_edit):
//                fragmentSelected = true;
//                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new EditPhotoFragment()).commit();
//                break;

            case (R.id.nav_myReport):
                fragmentSelected = true;
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new UserReportFragment()).commit();
                break;

            case (R.id.nav_allReports):
                fragmentSelected = true;
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new ReportsFragment()).commit();
                break;

            case (R.id.nav_statistics):
                fragmentSelected = true;
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new StatisticsFragment()).commit();
                break;

            case (R.id.nav_settings):
                fragmentSelected = true;
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new SettingsFragment()).commit();
                break;

            case (R.id.nav_aboutUs):
                fragmentSelected = true;
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new AboutUsFragment()).commit();
                break;

            case (R.id.nav_logout):
                confirmLogout();
                break;

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return fragmentSelected;
    }

    private void confirmLogout() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.confirm_logout, null);
        builder.setView(view);

        Button cancelButton = view.findViewById(R.id.btn_No);
        Button okButton = view.findViewById(R.id.btn_Yes);

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCancelable(false);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.setCanceledOnTouchOutside(true);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();

                logout();
                loginFragment = new LoginFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, loginFragment).commit();

                FirebaseAuth.getInstance().signOut();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("LOGIN");
                editor.apply();

                SharedPreferences.Editor editor2 = sharedPref.edit();
                editor2.remove("userRole");
                editor2.apply();

                navigationView.getMenu().getItem(0).setChecked(true);

            }
        });

    }

    public void logout() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, intentFilter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if (this.drawerLayout.isDrawerVisible(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onUserRoleSelected(String role) {
        this.userRole = role;

        hideAndShowMenuItems();

//        SharedPreferences prefs = getApplicationContext().getSharedPreferences("myPrefs", 0);
//        SharedPreferences.Editor editor = prefs.edit();
//        editor.putString("userRole", userRole);
//        editor.apply();
    }


    public void hideAndShowMenuItems() {

        if (userRole.equals(getText(R.string.admin).toString()) || superUsersList.contains(userRole)) {
            menu.findItem(R.id.nav_allReports).setVisible(true);
            menu.findItem(R.id.nav_statistics).setVisible(true);
            menu.findItem(R.id.nav_myReport).setVisible(false);
        } else if (usersList.contains(userRole)) {
            menu.findItem(R.id.nav_allReports).setVisible(false);
            menu.findItem(R.id.nav_statistics).setVisible(false);
            menu.findItem(R.id.nav_myReport).setVisible(true);

        }
    }
}

