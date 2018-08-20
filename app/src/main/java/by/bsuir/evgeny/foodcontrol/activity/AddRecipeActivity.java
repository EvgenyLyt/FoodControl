package by.bsuir.evgeny.foodcontrol.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import by.bsuir.evgeny.foodcontrol.R;
import by.bsuir.evgeny.foodcontrol.entity.Recipe;

public class AddRecipeActivity extends AppCompatActivity {

    AddRecipeActivityImplementation activityImplementation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);
        activityImplementation = new AddRecipeActivityImplementation(this);
        activityImplementation.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        activityImplementation.setIngredients(resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_recipe_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case (R.id.action_clean_recipe): {
                activityImplementation.cleanRecipe();
                Toast.makeText(getApplicationContext(), "Рецепт очищен.", Toast.LENGTH_SHORT).show();
            }
            break;
        }
        return super.onOptionsItemSelected(item);
    }
}
