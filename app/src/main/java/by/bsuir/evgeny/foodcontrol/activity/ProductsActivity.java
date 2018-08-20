package by.bsuir.evgeny.foodcontrol.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import java.util.List;

import by.bsuir.evgeny.foodcontrol.R;
import by.bsuir.evgeny.foodcontrol.entity.Product;
import by.bsuir.evgeny.foodcontrol.entity.Recipe;

public class ProductsActivity extends AppCompatActivity {

    ProductsActivityImplementation activityImplementation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        activityImplementation = new ProductsActivityImplementation(this);
        activityImplementation.start();
    }

    @Override
    public void onBackPressed() {
        activityImplementation.onBackPressed();
        super.onBackPressed();
    }
}
