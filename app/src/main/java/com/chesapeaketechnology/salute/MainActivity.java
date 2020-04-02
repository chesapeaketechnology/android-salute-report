package com.chesapeaketechnology.salute;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

/**
 * The main activity for the Salute Report app.  The Salute Report app allows for users to create SALUTE Reports that
 * are saved to a JSON file and then shared to Sync Monkey.
 *
 * @since 0.1.0
 */
public class MainActivity extends AppCompatActivity
{
    private AppBarConfiguration appBarConfiguration;
    public NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupNavigation();
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }

    /**
     * Configure navigation so toolbar back button works properly.
     *
     * @since 0.1.0
     */
    private void setupNavigation()
    {
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        appBarConfiguration = new AppBarConfiguration.Builder(R.id.HomeFragment).build();

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
    }
}
