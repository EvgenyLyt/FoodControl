package by.bsuir.evgeny.foodcontrol.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import by.bsuir.evgeny.foodcontrol.R;

public class BasketActivity extends AppCompatActivity {

    private BasketActivityImplementation activityImplementation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basket);
        activityImplementation = new BasketActivityImplementation(this);
        activityImplementation.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.fridge_settings, menu);
        activityImplementation.setCheckItem(menu.getItem(2));
        return true;
    }

    @Override
    public void onBackPressed(){
        activityImplementation.onBackPressed();
        super.onBackPressed();
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
