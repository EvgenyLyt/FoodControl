package by.bsuir.evgeny.foodcontrol.activity;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import by.bsuir.evgeny.foodcontrol.MessageBox;
import by.bsuir.evgeny.foodcontrol.R;
import by.bsuir.evgeny.foodcontrol.adapter.ProductAdapter;
import by.bsuir.evgeny.foodcontrol.database.Database;
import by.bsuir.evgeny.foodcontrol.database.Helper;
import by.bsuir.evgeny.foodcontrol.entity.Product;

import static android.app.Activity.RESULT_OK;

class MainActivityImplementation implements NavigationView.OnNavigationItemSelectedListener{
    private ArrayList<Product> products = new ArrayList<>();
    private ProductAdapter productAdapter;
    private Context context;
    private AppCompatActivity activity;
    MainActivityImplementation(Context context){
        this.context = context;
        this.activity = (AppCompatActivity) context;
    }

    void start(){
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
        ListView lvFridgeProducts = (ListView) activity.findViewById(R.id.fridge);
        Button btnFormRecipes = (Button) activity.findViewById(R.id.btn_form_recipes);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                activity, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) activity.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
        activity.setTitle(activity.getResources().getString(R.string.mainActivityName));
        initDatabase(context);
        productAdapter = new ProductAdapter(context, products, activity.getTitle().toString());
        lvFridgeProducts.setAdapter(productAdapter);
        lvFridgeProducts.setOnItemClickListener(clickProduct);
        btnFormRecipes.setOnClickListener(formRecipes);
    }
    private void initDatabase(Context context){
        try {
            Database database = new Database(context);
            if (!context.getDatabasePath(Helper.DB_NAME).exists())
                database.databaseHelper.createDB(database.getHelper());
            database.openDatabase();
            database.fillItems(products,Helper.COLUMN_PRODUCT_DESCRIPTION,Helper.COLUMN_PRODUCT_FRIDGE);
            database.closeDatabase();
        }
        catch (Exception ex){
            MessageBox.Show(context,activity.getTitle().toString(),ex.toString()+"\n"+ex.getMessage());
        }
    }

    private AdapterView.OnItemClickListener clickProduct = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
            LayoutInflater li = LayoutInflater.from(context);
            View promptsView = li.inflate(R.layout.dialog, null);
            AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(context);
            mDialogBuilder.setView(promptsView);
            final EditText tv_description = (EditText) promptsView.findViewById(R.id.dialog_text_product_description);
            TextView tv_name = (TextView) promptsView.findViewById(R.id.dialog_text_product_name);
            tv_description.setText(products.get(position).getProduct_descripton());
            tv_name.setText(products.get(position).getProduct_name());
            mDialogBuilder.setCancelable(false).setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            products.get(position).setProduct_descripton(tv_description.getText().toString());
                            Database database = new Database(context);
                            database.openDatabase();
                            database.addProduct(products.get(position).getProduct_name(),products.get(position).getProduct_descripton(),
                                    Helper.COLUMN_PRODUCT_DESCRIPTION, Helper.COLUMN_PRODUCT_FRIDGE, Helper.COLUMN_PRODUCT_FAVORITE);
                            database.closeDatabase();
                            productAdapter.notifyDataSetChanged();
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

    private View.OnClickListener formRecipes = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ArrayList<String> recipes;
            Database database = new Database(context);
            database.openDatabase();
            ArrayList<String> fridge_products = new ArrayList<>();
            for (Product product : products)
                fridge_products.add(product.getProduct_name());
            recipes = database.findRecipe(fridge_products);
            database.closeDatabase();
            Intent intent = new Intent(context, ListRecipesActivity.class);
            intent.putExtra("Activity", activity.getResources().getString(R.string.listRecipesActivityName));
            intent.putExtra("Recipes", recipes);
            activity.startActivity(intent);
        }
    };

    void setProducts(int resultCode, Intent data){
        if (data == null) return;
        if (resultCode==RESULT_OK) {
            initDatabase(context);
            productAdapter.notifyDataSetChanged();
        }
    }

    void deleteProducts(){
        Database database = new Database(context);
        database.openDatabase();
        ArrayList<Product> list = new ArrayList<>(products);
        boolean delete = false;
        for (Product product : list) {
            if (product.isCheck_product()){
                delete = true;
                products.remove(product);
                database.deleteProduct(product.getProduct_name(), product.getProduct_descripton(),Helper.COLUMN_PRODUCT_DESCRIPTION,Helper.COLUMN_PRODUCT_FRIDGE,Helper.COLUMN_PRODUCT_FAVORITE);
            }
        }
        if (delete)
            Toast.makeText(context, "Выбранные продукты удалены из холодильника.", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(context, "Выберите продукты.", Toast.LENGTH_SHORT).show();
        productAdapter.notifyDataSetChanged();
        database.closeDatabase();
    }

    void setCheckItem(MenuItem checkItem) {
        productAdapter.setCheckItem(checkItem);
    }

    void checkProducts(){
        checkProductItems(!productAdapter.allProductsChecked());
        productAdapter.notifyDataSetChanged();
    }

    private void checkProductItems(boolean check){
        for (Product p: products)
            p.setCheck_product(check);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.nav_new_recipe): {
                Intent intent = new Intent(context, AddRecipeActivity.class);
                intent.putExtra("Name", activity.getTitle());
                context.startActivity(intent);
            }
            break;
            case (R.id.nav_recipe_categories): {
                Intent intent = new Intent(context, ListRecipesActivity.class);
                intent.putExtra("Activity", activity.getResources().getString(R.string.listRecipeCategoriesActivityName));
                activity.startActivity(intent);
            }
            break;
            case (R.id.nav_all_recipes): {
                Intent intent = new Intent(context, ListRecipesActivity.class);
                intent.putExtra("Activity", activity.getResources().getString(R.string.listRecipesActivityName));
                activity.startActivity(intent);
            }
            break;
            case (R.id.nav_food_basket): {
                Intent intent = new Intent(context, BasketActivity.class);
                activity.startActivityForResult(intent, 2);
            }
            break;
            case (R.id.nav_table_weights): {
                Intent intent = new Intent(context, WeightActivity.class);
                activity.startActivity(intent);
            }
            break;
            case (R.id.nav_cooking_time): {
                Intent intent = new Intent(context, WeightActivity.class);
                activity.startActivity(intent);
            }
            break;
            case (R.id.nav_exit): {
                Toast.makeText(context, "nav_exit", Toast.LENGTH_LONG).show();
                activity.finish();
            }
            break;
        }
        DrawerLayout drawer = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
