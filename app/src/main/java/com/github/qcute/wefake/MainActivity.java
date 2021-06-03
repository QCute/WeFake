package com.github.qcute.wefake;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // check permissions
        checkPermissions();
    }

    private void checkPermissions() {
        ArrayList<String> permissions = new ArrayList<>();
        // collect permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_WIFI_STATE);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_NETWORK_STATE);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.INTERNET);
        }
        // start all permissions grant
        if (permissions.isEmpty()) {
            setup();
            return;
        }
        // request permissions
        ActivityCompat.requestPermissions(this, permissions.toArray(new String[0]), 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // start all permissions grant
        if (Arrays.stream(grantResults).allMatch(r -> r == 0)) {
            setup();
            return;
        }
        this.finish();
    }

    private void setup() {
        // main view
        setContentView(R.layout.activity_main);
        // bottom navigation
        NavHostFragment fragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_container);
        if (fragment == null) return;
        BottomNavigationView navView = findViewById(R.id.navigation_menu);
        NavigationUI.setupWithNavController(navView, fragment.getNavController());
    }
}