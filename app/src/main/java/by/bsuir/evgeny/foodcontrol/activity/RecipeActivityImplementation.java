package by.bsuir.evgeny.foodcontrol.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import by.bsuir.evgeny.foodcontrol.ListViewHeight;
import by.bsuir.evgeny.foodcontrol.R;
import by.bsuir.evgeny.foodcontrol.adapter.NameAdapter;
import by.bsuir.evgeny.foodcontrol.adapter.RecipeIngredientAdapter;
import by.bsuir.evgeny.foodcontrol.database.Database;
import by.bsuir.evgeny.foodcontrol.entity.Name;
import by.bsuir.evgeny.foodcontrol.entity.Product;
import by.bsuir.evgeny.foodcontrol.entity.Recipe;

class RecipeActivityImplementation {

    private Context context;
    private AppCompatActivity activity;
    private Recipe recipe;

    RecipeActivityImplementation(Context context) {
        this.context = context;
        this.activity = (AppCompatActivity) context;
    }

    void start(){
        activity.setTitle(activity.getResources().getString(R.string.recipeActivityName));
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });
        recipe = activity.getIntent().getParcelableExtra("Recipe");
        initRecipe(recipe);
        ImageButton btnAddToBasket = (ImageButton) activity.findViewById(R.id.btn_add_to_basket);
        btnAddToBasket.setOnClickListener(addToBasket);
    }

    private void initRecipe(Recipe recipe){
        TextView recipe_name = (TextView) activity.findViewById(R.id.text_recipe_name);
        TextView recipe_category = (TextView) activity.findViewById(R.id.text_recipe_category);
        TextView recipe_time = (TextView) activity.findViewById(R.id.text_recipe_time);
        TextView recipe_instruction = (TextView) activity.findViewById(R.id.text_recipe_instruction);
        ListView recipe_ingredients = (ListView) activity.findViewById(R.id.text_recipe_ingredients);
        recipe_ingredients.setAdapter(new RecipeIngredientAdapter(context, recipe));
        ListViewHeight.set(recipe_ingredients);
        recipe_name.setText(recipe.getName());
        recipe_category.setText(recipe.getCategory());
        recipe_time.setText(activity.getResources().getString(R.string.time_recipe)+" "+recipe.getTime());
        recipe_instruction.setText(recipe.getInstruction());
    }

    void deleteRecipe(){
        Database database = new Database(context);
        database.openDatabase();
        database.deleteRecipe(recipe.getId());
        database.closeDatabase();
        Toast.makeText(context, "Рецепт успешно удалён.", Toast.LENGTH_SHORT).show();
        activity.finish();
    }

    void editRecipe(){
        Intent intent = new Intent(context, AddRecipeActivity.class);
        intent.putExtra("Recipe", recipe);
        activity.startActivity(intent);
    }

    private View.OnClickListener addToBasket = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Database database = new Database(context);
            database.openDatabase();
            for (Product p: recipe.getIngredients())
                if (!p.isCheck_product())
                    database.addToBasket(p.getProduct_name(),p.getProduct_descripton());
            database.closeDatabase();
            Toast.makeText(context,"Продукты успешно добавлены в корзину.", Toast.LENGTH_SHORT).show();
        }
    };
}
