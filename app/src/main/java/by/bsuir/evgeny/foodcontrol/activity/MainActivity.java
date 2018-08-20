package by.bsuir.evgeny.foodcontrol.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import by.bsuir.evgeny.foodcontrol.R;

public class MainActivity extends AppCompatActivity {

    MainActivityImplementation activityImplementation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activityImplementation = new MainActivityImplementation(this);
        activityImplementation.start();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.fridge_settings, menu);
        activityImplementation.setCheckItem(menu.getItem(2));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case (R.id.action_add): {
                Intent intent = new Intent(getApplicationContext(), ProductsActivity.class);
                intent.putExtra("ParentActivity", getTitle().toString());
                startActivityForResult(intent, 1);
            }
            break;
            case (R.id.action_delete): {
                activityImplementation.deleteProducts();
            }
            break;
            case (R.id.action_check): {
                activityImplementation.checkProducts();
            }
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        activityImplementation.setProducts(resultCode, data);
    }
}
