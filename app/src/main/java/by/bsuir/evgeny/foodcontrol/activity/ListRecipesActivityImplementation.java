package by.bsuir.evgeny.foodcontrol.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import by.bsuir.evgeny.foodcontrol.R;
import by.bsuir.evgeny.foodcontrol.adapter.SummaryRecipeAdapter;
import by.bsuir.evgeny.foodcontrol.database.Database;
import by.bsuir.evgeny.foodcontrol.database.Helper;
import by.bsuir.evgeny.foodcontrol.entity.Product;
import by.bsuir.evgeny.foodcontrol.entity.Recipe;
import by.bsuir.evgeny.foodcontrol.entity.SummaryRecipe;

class ListRecipesActivityImplementation {
    private Context context;
    private AppCompatActivity activity;
    private ArrayList<Recipe> recipes = new ArrayList<>();
    private ArrayList<HashMap<String, SummaryRecipe>> items_recipes = new ArrayList<>();
    private ArrayList<String> recipe_categories = new ArrayList<>();
    private ArrayList<String> form_recipes;
    private int recipeCategory = -1;
    private String activityName;
    private SummaryRecipeAdapter recipeAdapter;

    ListRecipesActivityImplementation(Context context){
        this.context = context;
        this.activity = (AppCompatActivity) context;
        this.activityName = activity.getIntent().getStringExtra("Activity");
    }

    void start(){
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.setTitle(activityName);
        ListView lvRecipes = (ListView) activity.findViewById(R.id.list_recipes);
        EditText edtRecipe = (EditText) activity.findViewById(R.id.edit_name_recipe);
        ImageButton clrRecipeName = (ImageButton) activity.findViewById(R.id.clear_name_recipe);
        TextView btnBackToCategory = (TextView) activity.findViewById(R.id.back_to_list_recipes);
        initDatabase(context);
        initListRecipes();
        lvRecipes.setOnItemClickListener(clickCategory);
        btnBackToCategory.setOnClickListener(backToList);
        edtRecipe.addTextChangedListener(searchRecipe);
        clrRecipeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((EditText) activity.findViewById(R.id.edit_name_recipe)).setText("");
            }
        });
    }

    private View.OnClickListener backToList = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ((EditText) activity.findViewById(R.id.edit_name_recipe)).setText("");
            ViewGroup.LayoutParams params = v.getLayoutParams();
            params.height = 0;
            v.setLayoutParams(params);
            ListView lvRecipes = (ListView) activity.findViewById(R.id.list_recipes);
            lvRecipes.setAdapter(new ArrayAdapter<String>(context, R.layout.item_category, recipe_categories.toArray(new String[0])));
            recipeCategory = -1;
            lvRecipes.setTag("List");
        }
    };

    private TextWatcher searchRecipe = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            findRecipeByName(s.toString());
        }
    };

    private void findRecipeByName(String name){
        ListView lvRecipes = (ListView) activity.findViewById(R.id.list_recipes);
        if (name.length() <= 2) {
            if (form_recipes != null)
                setRecipes(lvRecipes, form_recipes);
            else
                if (recipeCategory != -1)
                    checkCategory(recipeCategory);
                else {
                    TextView back = (TextView) activity.findViewById(R.id.back_to_list_recipes);
                    ViewGroup.LayoutParams params = back.getLayoutParams();
                    params.height = 0;
                    back.setLayoutParams(params);
                    initListRecipes();
                }
        }
        else {
            lvRecipes.setTag("Word");
            ArrayList<SummaryRecipe> foundRecipes = new ArrayList<>();
            recipeAdapter = new SummaryRecipeAdapter(context, foundRecipes);
            for (Map<String, SummaryRecipe> w : items_recipes) {
                int index;
                if (recipeCategory == -1) {
                    index = 0;
                    while (w.get(recipe_categories.get(index)) == null) index++;
                }
                else
                    index = recipeCategory;
                SummaryRecipe recipe = w.get(recipe_categories.get(index));
                if (recipe!=null) {
                    boolean form = (form_recipes != null) ? form_recipes.contains(recipe.getName()) : true;
                    if (recipe.getName().trim().toLowerCase().contains(name.trim().toLowerCase()) && form)
                        foundRecipes.add(recipe);
                }
            }
            lvRecipes.setAdapter(recipeAdapter);
        }
    }

    private AdapterView.OnItemClickListener clickCategory = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (parent.getTag().equals("List"))
                checkCategory(position);
            else
            {
                Intent intent = new Intent(context, RecipeActivity.class);
                intent.putExtra("Recipe", findRecipe(view.getTag().toString()));
                activity.startActivity(intent);
            }
        }
    };

    private Recipe findRecipe(String name){
        for (Recipe r: recipes)
            if (r.getName().equals(name))
                return r;
        return null;
    }

    private void checkCategory(int position) {
        TextView btnBackToCategory = (TextView) activity.findViewById(R.id.back_to_list_recipes);
        ListView lvRecipes = (ListView) activity.findViewById(R.id.list_recipes);
        ArrayList<SummaryRecipe> foundRecipes = new ArrayList<>();
        recipeAdapter = new SummaryRecipeAdapter(context, foundRecipes);
        for (Map<String, SummaryRecipe> w : items_recipes) {
            SummaryRecipe recipe = w.get(recipe_categories.get(position));
            if (recipe != null)
                foundRecipes.add(recipe);
        }
        lvRecipes.setAdapter(recipeAdapter);
        btnBackToCategory.setText(recipe_categories.get(position));
        recipeCategory = position;
        lvRecipes.setTag("Words");
        btnBackToCategory.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
    }

    private void initListRecipes(){
        ListView lvRecipes = (ListView) activity.findViewById(R.id.list_recipes);
        form_recipes = activity.getIntent().getStringArrayListExtra("Recipes");
        if (activityName.equals(activity.getResources().getString(R.string.listRecipesActivityName)))
            setRecipes(lvRecipes, form_recipes);
        else
            if (activityName.equals(activity.getResources().getString(R.string.listRecipeCategoriesActivityName)))
            {
                lvRecipes.setAdapter(new ArrayAdapter<String>(context, R.layout.item_category, recipe_categories.toArray(new String[0])));
                lvRecipes.setTag("List");
            }
    }

    private void setRecipes(ListView lvRecipes, ArrayList<String> form_recipes){
        ArrayList<SummaryRecipe> foundRecipes = new ArrayList<>();
        recipeAdapter = new SummaryRecipeAdapter(context, foundRecipes);
        for (Map<String, SummaryRecipe> w : items_recipes) {
            for (String category: recipe_categories){
                SummaryRecipe recipe = w.get(category);
                if (recipe != null) {
                    if (form_recipes != null) {
                        if (form_recipes.contains(recipe.getName()))
                            foundRecipes.add(recipe);
                    }
                    else
                        foundRecipes.add(recipe);
                }
            }
        }
        lvRecipes.setAdapter(recipeAdapter);
        lvRecipes.setTag("Words");
    }

    private void initDatabase(Context context){
        Database database = new Database(context);
        if (!context.getDatabasePath(Helper.DB_NAME).exists())
            database.databaseHelper.createDB(database.getHelper());
        database.openDatabase();
        database.fillCategories(recipe_categories);
        database.fillRecipes(recipe_categories, items_recipes, recipes);
        database.closeDatabase();
    }
}
