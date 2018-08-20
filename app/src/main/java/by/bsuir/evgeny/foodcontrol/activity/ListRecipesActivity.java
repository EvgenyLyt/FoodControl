package by.bsuir.evgeny.foodcontrol.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import by.bsuir.evgeny.foodcontrol.R;

public class ListRecipesActivity extends AppCompatActivity {

    private ListRecipesActivityImplementation activityImplementation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_recipes);
        activityImplementation = new ListRecipesActivityImplementation(this);
        activityImplementation.start();
    }

}
