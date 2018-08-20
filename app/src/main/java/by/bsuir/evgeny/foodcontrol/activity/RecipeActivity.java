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
import android.widget.Toast;

import by.bsuir.evgeny.foodcontrol.R;

public class RecipeActivity extends AppCompatActivity {

    RecipeActivityImplementation activityImplementation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        activityImplementation = new RecipeActivityImplementation(this);
        activityImplementation.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_recipe_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case (R.id.action_edit_recipe): {
                activityImplementation.editRecipe();
            }
            break;
            case (R.id.action_delete_recipe): {
                activityImplementation.deleteRecipe();
            }
            break;
        }
        return super.onOptionsItemSelected(item);
    }

}
