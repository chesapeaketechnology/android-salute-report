package com.chesapeaketechnology.salute;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

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

    /* Delete me
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }*/

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

        OnBackPressedCallback callback = new OnBackPressedCallback(true)
        {
            @Override
            public void handleOnBackPressed()
            {
                // For whatever reason calling navigateUp from one of the top level destinations results in the
                // navigation drawer being opened.  Therefore, if the current destination a top level we have custom
                // code here to move this activity to the back stack.
                final NavDestination currentDestination = navController.getCurrentDestination();
                if (currentDestination != null && currentDestination.getId() == R.id.home)
                {
                    moveTaskToBack(true);
                } else
                {
                    NavigationUI.navigateUp(navController, appBarConfiguration);
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home)
        {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
