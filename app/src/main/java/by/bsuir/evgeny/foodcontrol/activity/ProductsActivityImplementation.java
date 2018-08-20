package by.bsuir.evgeny.foodcontrol.activity;

import android.content.Context;
import android.content.Intent;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import by.bsuir.evgeny.foodcontrol.MessageBox;
import by.bsuir.evgeny.foodcontrol.R;
import by.bsuir.evgeny.foodcontrol.adapter.NameAdapter;
import by.bsuir.evgeny.foodcontrol.database.Database;
import by.bsuir.evgeny.foodcontrol.database.Helper;
import by.bsuir.evgeny.foodcontrol.entity.Name;
import by.bsuir.evgeny.foodcontrol.entity.Product;

import static android.app.Activity.RESULT_OK;

class ProductsActivityImplementation {
    private ArrayList<String> product_categories = new ArrayList<>();
    private ArrayList<HashMap<String, Name>> products = new ArrayList<>();
    private NameAdapter nameAdapter;
    private Context context;
    private AppCompatActivity activity;
    private String mainActivityName;
    private ArrayList<Product> ingredients;

    ProductsActivityImplementation(Context context) {
        this.context = context;
        this.activity = (AppCompatActivity) context;
        this.mainActivityName = activity.getIntent().getStringExtra("ParentActivity");
        if (mainActivityName.equals(activity.getResources().getString(R.string.addRecipeActivityName)))
            ingredients = activity.getIntent().getParcelableArrayListExtra("Ingredients");
    }

    void start(){
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                activity.finish();
            }
        });
        activity.setTitle(activity.getResources().getString(R.string.productsActivityName));
        final EditText edtProductName = (EditText) activity.findViewById(R.id.edit_name_fridge_product);
        ImageButton clrProductName = (ImageButton) activity.findViewById(R.id.clear_name_fridge_product);
        TextView backProductCategory = (TextView) activity.findViewById(R.id.back_to_list_fridge_products);
        ListView lvProducts = (ListView) activity.findViewById(R.id.list_fridge_products);
        initDatabase(context);
        lvProducts.setAdapter(new ArrayAdapter<String>(context, R.layout.item_category, product_categories.toArray(new String[0])));
        lvProducts.setTag("List");
        edtProductName.addTextChangedListener(searchProduct);
        clrProductName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtProductName.setText("");
            }
        });
        lvProducts.setOnItemClickListener(choiceCategory);
        backProductCategory.setOnClickListener(backToList);
    }

    public void onBackPressed(){
        Intent intent = new Intent();
        if (mainActivityName.equals(activity.getResources().getString(R.string.addRecipeActivityName))) {
            intent.putParcelableArrayListExtra("Ingredients", getIngredients());
            activity.setResult(RESULT_OK, intent);
        }
        else
        if (mainActivityName.equals(activity.getResources().getString(R.string.mainActivityName))||
                mainActivityName.equals(activity.getResources().getString(R.string.basketActivityName))){
            intent.putExtra("Products", "Products");
            activity.setResult(RESULT_OK, intent);
        }
    }

    private void initDatabase(Context context){
        try {
            Database database = new Database(context);
            if (!context.getDatabasePath(Helper.DB_NAME).exists())
                database.databaseHelper.createDB(database.getHelper());
                findProducts(database);
        }
        catch (Exception ex){
            MessageBox.Show(context,activity.getTitle().toString(),ex.toString()+"\n"+ex.getMessage());
        }
    }

    private AdapterView.OnItemClickListener choiceCategory = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (parent.getTag().equals("List"))
                checkCategory(position);
        }
    };

    private View.OnClickListener backToList = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ViewGroup.LayoutParams params = v.getLayoutParams();
            params.height = 0;
            v.setLayoutParams(params);
            ListView lvFridgeProducts = (ListView) activity.findViewById(R.id.list_fridge_products);
            lvFridgeProducts.setAdapter(new ArrayAdapter<String>(context,  R.layout.item_category, product_categories.toArray(new String[0])));
            lvFridgeProducts.setTag("List");
        }
    };

    private TextWatcher searchProduct = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            ListView lvFridgeProducts = (ListView) activity.findViewById(R.id.list_fridge_products);
            if (s.length() <= 2) {
                TextView back = (TextView) activity.findViewById(R.id.back_to_list_fridge_products);
                ViewGroup.LayoutParams params = back.getLayoutParams();
                params.height = 0;
                back.setLayoutParams(params);
                lvFridgeProducts.setTag("List");
                lvFridgeProducts.setAdapter(new ArrayAdapter<String>(context,  R.layout.item_category, product_categories.toArray(new String[0])));
            }
            else {
                ArrayList<Name> foundProducts = new ArrayList<>();
                for (Map<String, Name> w : products) {
                    int i = 0;
                    while (w.get(product_categories.get(i)) == null) i++;
                    Name product_name = w.get(product_categories.get(i));
                    if (product_name.getName_product().toLowerCase().trim().contains(s.toString().toLowerCase().trim()))
                        foundProducts.add(product_name);
                }
                nameAdapter = new NameAdapter(context, foundProducts, mainActivityName);
                lvFridgeProducts.setAdapter(nameAdapter);
            }
        }
    };

    private void checkCategory(int position) {
        TextView btnBackToCategory = (TextView) activity.findViewById(R.id.back_to_list_fridge_products);
        ListView lvProducts = (ListView) activity.findViewById(R.id.list_fridge_products);
        findProducts(new Database(context));
        ArrayList<Name> foundProducts = new ArrayList<>();
        for (Map<String, Name> w : products) {
            Name product_name = w.get(product_categories.get(position));
            if (product_name != null)
                foundProducts.add(product_name);
        }
        nameAdapter = new NameAdapter(context, foundProducts, mainActivityName);
        lvProducts.setAdapter(nameAdapter);
        btnBackToCategory.setText(product_categories.get(position));
        lvProducts.setTag("Words");
        btnBackToCategory.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
    }

    private void findProducts(Database database){
        if (mainActivityName.equals(activity.getResources().getString(R.string.mainActivityName))){
            database.openDatabase();
            database.fillItems(product_categories, products, Helper.COLUMN_PRODUCT_DESCRIPTION, Helper.COLUMN_PRODUCT_FRIDGE);
            database.closeDatabase();
        }
        else
            if (mainActivityName.equals(activity.getResources().getString(R.string.addRecipeActivityName))){
                database.openDatabase();
                database.fillItems(product_categories, products, Helper.COLUMN_PRODUCT_DESCRIPTION, Helper.COLUMN_PRODUCT_FRIDGE);
                for (int i = 0; i < product_categories.size(); i++)
                    for (Map<String, Name> w : products) {
                        Name product_name = w.get(product_categories.get(i));
                        if (product_name != null)
                            product_name.setCheck_product(setProduct(product_name));
                    }
                database.closeDatabase();
            }
            else
                if (mainActivityName.equals(activity.getResources().getString(R.string.basketActivityName)))
                {
                    database.openDatabase();
                    database.fillItems(product_categories, products, Helper.COLUMN_PRODUCT_BOX_DESCRIPTION, Helper.COLUMN_PRODUCT_BOX);
                    database.closeDatabase();
                }
    }

    private boolean setProduct(Name product){
        for (Product p: ingredients)
            if (p.getProduct_name().equals(product.getName_product()))
                return true;
        return false;
    }

    private String setProductDescription(Name product){
        for (Product p: ingredients)
            if (p.getProduct_name().equals(product.getName_product()))
                return p.getProduct_descripton();
        return "";
    }

    private ArrayList<Product> getIngredients(){
        ArrayList<Product> ingredients = new ArrayList<>();
        for (int i = 0; i < product_categories.size(); i++)
            for (Map<String, Name> w : products) {
                Name product_name = w.get(product_categories.get(i));
                if (product_name != null)
                    if (product_name.isCheck_product())
                        ingredients.add(new Product(product_name.getName_product(), setProductDescription(product_name), false));
            }
        return ingredients;
    }


}
