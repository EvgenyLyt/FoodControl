package by.bsuir.evgeny.foodcontrol.activity;

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
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import by.bsuir.evgeny.foodcontrol.MessageBox;
import by.bsuir.evgeny.foodcontrol.R;
import by.bsuir.evgeny.foodcontrol.adapter.ProductAdapter;
import by.bsuir.evgeny.foodcontrol.database.Database;
import by.bsuir.evgeny.foodcontrol.database.Helper;
import by.bsuir.evgeny.foodcontrol.entity.Product;

import static android.app.Activity.RESULT_OK;

class BasketActivityImplementation {
    private ArrayList<Product> products = new ArrayList<>();
    private ProductAdapter productAdapter;
    private Context context;
    private AppCompatActivity activity;

    BasketActivityImplementation(Context context){
        this.context = context;
        this.activity = (AppCompatActivity) context;
    }

    void start(){
        activity.setTitle(activity.getResources().getString(R.string.basketActivityName));
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
        ListView lvBasketProducts = (ListView) activity.findViewById(R.id.basket);
        Button btnInTheFridge = (Button) activity.findViewById(R.id.btn_in_the_fridge);
        initDatabase(context);
        productAdapter = new ProductAdapter(context, products, activity.getTitle().toString());
        lvBasketProducts.setAdapter(productAdapter);
        lvBasketProducts.setOnItemClickListener(clickProduct);
        btnInTheFridge.setOnClickListener(inFridge);
    }

    public void onBackPressed(){
        Intent intent = new Intent();
        intent.putExtra("Products", "Products");
        activity.setResult(RESULT_OK, intent);
    }

    private void initDatabase(Context context){
        try {
            Database database = new Database(context);
            if (!context.getDatabasePath(Helper.DB_NAME).exists())
                database.databaseHelper.createDB(database.getHelper());
            database.openDatabase();
            database.fillItems(products, Helper.COLUMN_PRODUCT_BOX_DESCRIPTION, Helper.COLUMN_PRODUCT_BOX);
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
                                    Helper.COLUMN_PRODUCT_BOX_DESCRIPTION, Helper.COLUMN_PRODUCT_BOX, Helper.COLUMN_PRODUCT_BOX_FAVORITE);
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

    private View.OnClickListener inFridge = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Database database = new Database(context);
            database.openDatabase();
            ArrayList<Product> list = new ArrayList<>(products);
            boolean in = false;
            for (Product product : list) {
                if (product.isCheck_product()){
                    in = true;
                    products.remove(product);
                    database.addToFridge(product.getProduct_name(),product.getProduct_descripton());
                }
            }
            if (in)
                Toast.makeText(context, "Выбранные продукты убраны в холодильник.", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(context, "Выберите продукты.", Toast.LENGTH_SHORT).show();
            productAdapter.notifyDataSetChanged();
            database.closeDatabase();
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
                database.deleteProduct(product.getProduct_name(), product.getProduct_descripton(),Helper.COLUMN_PRODUCT_BOX_DESCRIPTION,Helper.COLUMN_PRODUCT_BOX,Helper.COLUMN_PRODUCT_BOX_FAVORITE);
            }
        }
        if (delete)
            Toast.makeText(context, "Выбранные продукты удалены из корзины.", Toast.LENGTH_SHORT).show();
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
}
