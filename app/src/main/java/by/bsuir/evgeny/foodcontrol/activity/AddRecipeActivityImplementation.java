package by.bsuir.evgeny.foodcontrol.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import by.bsuir.evgeny.foodcontrol.ListViewHeight;
import by.bsuir.evgeny.foodcontrol.MessageBox;
import by.bsuir.evgeny.foodcontrol.R;
import by.bsuir.evgeny.foodcontrol.adapter.IngredientAdapter;
import by.bsuir.evgeny.foodcontrol.database.Database;
import by.bsuir.evgeny.foodcontrol.database.Helper;
import by.bsuir.evgeny.foodcontrol.entity.Name;
import by.bsuir.evgeny.foodcontrol.entity.Product;
import by.bsuir.evgeny.foodcontrol.entity.Recipe;
import by.bsuir.evgeny.foodcontrol.entity.SavedRecipe;

import static android.app.Activity.RESULT_OK;

class AddRecipeActivityImplementation {
    private Context context;
    private AppCompatActivity activity;
    private ArrayList<Product> ingredients = new ArrayList<>();
    private ArrayList<String> recipe_categories = new ArrayList<>();
    private ArrayList<Recipe> recipes = new ArrayList<>();
    private Recipe recipe;

    AddRecipeActivityImplementation(Context context){
        this.context = context;
        this.activity = (AppCompatActivity) context;
    }

    void start(){
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.setTitle(activity.getResources().getString(R.string.addRecipeActivityName));
        ListView lvIngredients = (ListView) activity.findViewById(R.id.list_recipe_ingredients);
        Button btnSaveRecipe = (Button) activity.findViewById(R.id.btn_save_recipe);
        Button btnAddIngredients = (Button) activity.findViewById(R.id.btn_add_ingredients);
        initDatabase(context);
        initializeData();
        lvIngredients.setAdapter(new IngredientAdapter(context, ingredients));
        ListViewHeight.set(lvIngredients);
        lvIngredients.setAdapter(new IngredientAdapter(context, ingredients));
        lvIngredients.setOnItemClickListener(clickIngredient);
        btnSaveRecipe.setOnClickListener(saveRecipe);
        btnAddIngredients.setOnClickListener(addRecipeIngredients);
    }

    void setIngredients(int resultCode, Intent data){
        if (data == null) return;
        if (resultCode==RESULT_OK) {
            ingredients = data.getParcelableArrayListExtra("Ingredients");
            ListView lvIngredients = (ListView) activity.findViewById(R.id.list_recipe_ingredients);
            lvIngredients.setAdapter(new IngredientAdapter(context, ingredients));
            ListViewHeight.set(lvIngredients);
        }
    }

    private View.OnClickListener saveRecipe = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (recipe==null)
                recipe = new Recipe();
            if (isEmpty(recipe))
                Toast.makeText(context, "Не все поля рецепта заполнены.", Toast.LENGTH_SHORT).show();
            else
            if (checkNameRecipe(recipe.getName())&& recipe.getId().isEmpty())
                Toast.makeText(context, "Такое название уже есть в базе рецептов.", Toast.LENGTH_SHORT).show();
            else {
                Database database = new Database(context);
                database.openDatabase();
                database.addRecipe(recipe.getId(),recipe.getName(),recipe_categories.indexOf(recipe.getCategory())+1, recipe.getTime(), recipe.getInstruction(), recipe.getIngredients());
                database.fillRecipes(recipe_categories, recipes);
                database.closeDatabase();
                cleanRecipe();
                Toast.makeText(context, "Рецепт успешно сохранен.", Toast.LENGTH_SHORT).show();
            }
        }
    };


    private View.OnClickListener addRecipeIngredients = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, ProductsActivity.class);
            intent.putExtra("ParentActivity", activity.getTitle().toString());
            intent.putParcelableArrayListExtra("Ingredients",ingredients);
            activity.startActivityForResult(intent, 3);
        }
    };

    private void initDatabase(Context context){
        try {
            Database database = new Database(context);
            if (!activity.getApplicationContext().getDatabasePath(Helper.DB_NAME).exists())
                database.databaseHelper.createDB(database.getHelper());
            database.openDatabase();
            database.fillCategories(recipe_categories);
            database.fillRecipes(recipe_categories, recipes);
            database.closeDatabase();
        }
        catch (Exception ex){
            MessageBox.Show(context,activity.getTitle().toString(),ex.toString()+"\n"+ex.getMessage());
        }
    }

    private void initializeData(){
        initializeSpinner();
        recipe = activity.getIntent().getParcelableExtra("Recipe");
        if (recipe!=null){
            ingredients = recipe.getIngredients();
            ((EditText) activity.findViewById(R.id.edit_recipe_instruction)).setText(recipe.getInstruction());
            ((EditText) activity.findViewById(R.id.edit_recipe_name)).setText(recipe.getName());
            ((EditText) activity.findViewById(R.id.edit_recipe_time)).setText(recipe.getTime());
            ((Spinner) activity.findViewById(R.id.sp_recipe_categories)).setSelection(recipe_categories.indexOf(recipe.getCategory()));
        }
    }

    private void initializeSpinner(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.spinner_item, recipe_categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = (Spinner) activity.findViewById(R.id.sp_recipe_categories);
        spinner.setAdapter(adapter);
    }

    private AdapterView.OnItemClickListener clickIngredient = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
            LayoutInflater li = LayoutInflater.from(context);
            View promptsView = li.inflate(R.layout.dialog, null);
            AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(context);
            mDialogBuilder.setView(promptsView);
            final EditText tv_description = (EditText) promptsView.findViewById(R.id.dialog_text_product_description);
            final TextView tv_name = (TextView) promptsView.findViewById(R.id.dialog_text_product_name);
            tv_description.setText(ingredients.get(position).getProduct_descripton());
            tv_name.setText(ingredients.get(position).getProduct_name());
            mDialogBuilder.setCancelable(false).setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            if (tv_description.getText().toString().length()>20)
                                ingredients.get(position).setProduct_descripton(tv_description.getText().toString().substring(0,20));
                            else
                                ingredients.get(position).setProduct_descripton(tv_description.getText().toString());
                            ListView list = (ListView) activity.findViewById(R.id.list_recipe_ingredients);
                            list.setAdapter(new IngredientAdapter(context, ingredients));
                            ListViewHeight.set(list);
                        }
                    }).setNegativeButton("Отмена",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog dialog = mDialogBuilder.create();
            dialog.show();
        }
    };

    void cleanRecipe(){
        ((Spinner)activity.findViewById(R.id.sp_recipe_categories)).setSelection(0);
        ((EditText)activity.findViewById(R.id.edit_recipe_name)).setText("");
        ((EditText)activity.findViewById(R.id.edit_recipe_time)).setText("");
        ((EditText)activity.findViewById(R.id.edit_recipe_instruction)).setText("");
        ingredients.clear();
        ListView list = (ListView) activity.findViewById(R.id.list_recipe_ingredients);
        list.setAdapter(new IngredientAdapter(context, ingredients));
        ListViewHeight.set(list);
    }

    private boolean checkNameRecipe(String name){
        for (Recipe recipe: recipes)
            if (recipe.getName().trim().toLowerCase().equals(name.trim().toLowerCase()))
                return true;
        return false;
    }

    private boolean isEmpty(Recipe recipe){
        recipe.setName(((EditText) activity.findViewById(R.id.edit_recipe_name)).getText().toString().trim());
        recipe.setCategory(((Spinner) activity.findViewById(R.id.sp_recipe_categories)).getSelectedItem().toString().trim());
        recipe.setTime(((EditText) activity.findViewById(R.id.edit_recipe_time)).getText().toString().trim());
        recipe.setInstruction(((EditText) activity.findViewById(R.id.edit_recipe_instruction)).getText().toString().trim());
        recipe.setIngredients(ingredients);
        return (recipe.getName().isEmpty()||recipe.getCategory().isEmpty()||recipe.getTime().isEmpty()||recipe.getInstruction().isEmpty()||recipe.getIngredients().isEmpty());
    }

}
