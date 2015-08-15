package pl.tomaszmartin.stuff;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;

public class SettingsActivity extends AnalyticsActivity {

    private String TAG = SettingsActivity.class.getSimpleName();
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set the menu icon in toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
        }

        drawer = (DrawerLayout) findViewById(R.id.drawer);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(new NavigationListener(this, drawer));

        attachFragment();
    }



    private void attachFragment() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentByTag(TAG);
        if (currentFragment != null) {
            getSupportFragmentManager().beginTransaction().remove(currentFragment);
        }
        getFragmentManager().beginTransaction()
                .add(R.id.container, new SettingsFragment(), TAG)
                .commit();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            drawer.openDrawer(Gravity.LEFT);
        }

        return super.onOptionsItemSelected(item);
    }

}