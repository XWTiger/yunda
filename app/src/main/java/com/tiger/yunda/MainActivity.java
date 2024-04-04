package com.tiger.yunda;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.tiger.yunda.data.model.LoggedInUser;
import com.tiger.yunda.databinding.ActivityMainBinding;
import com.tiger.yunda.ui.login.LoginActivity;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private LoggedInUser loggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

       /* if (Objects.isNull(loggedInUser) || TextUtils.isEmpty(loggedInUser.getToken())) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }*/

        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        navView.setSelectedItemId(R.id.navigation_mission);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_breakdown, R.id.navigation_mission, R.id.navigation_log, R.id.navigation_person, R.id.navigation_resource)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        //添加登录校验逻辑

    }

}