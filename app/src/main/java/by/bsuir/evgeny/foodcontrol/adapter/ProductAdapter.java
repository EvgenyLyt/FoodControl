package by.bsuir.evgeny.foodcontrol.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import by.bsuir.evgeny.foodcontrol.R;
import by.bsuir.evgeny.foodcontrol.database.Database;
import by.bsuir.evgeny.foodcontrol.database.Helper;
import by.bsuir.evgeny.foodcontrol.entity.Product;

public class ProductAdapter extends BaseAdapter {
    private List<Product> products;
    private LayoutInflater lInflater;
    private Database database;
    private String activityName;
    private String fridge;
    private MenuItem checkItem;
    private Context context;

    public ProductAdapter(Context context, List<Product> products, String activityName) {
        this.context = context;
        this.products = products;
        this.activityName = activityName;
        this.lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.database = new Database(context);
        this.fridge = context.getResources().getString(R.string.mainActivityName);
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public Object getItem(int position) {
        return products.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null)
            view = lInflater.inflate(R.layout.item_product, parent, false);
        Product product = getProduct(position);
        ((TextView) view.findViewById(R.id.text_product_name)).setText(product.getProduct_name());
        ((TextView) view.findViewById(R.id.text_product_description)).setText(product.getProduct_descripton());
        ImageButton productFavorite = (ImageButton) view.findViewById(R.id.btn_product_favs);
        productFavorite.setTag(position);
        getFavorite(productFavorite);
        productFavorite.setOnClickListener(clickFavorite);
        CheckBox productChoice = (CheckBox) view.findViewById(R.id.btn_check_product);
        productChoice.setOnCheckedChangeListener(choiceProduct);
        productChoice.setTag(position);
        productChoice.setChecked(product.isCheck_product());
        return view;
    }

    private void getFavorite(ImageButton imageButton){
        Product product = getProduct((Integer) imageButton.getTag());
        database.openDatabase();
        setFavorite(database.findFavorite(activityName.equals(fridge), product.getProduct_name()), imageButton);
        database.closeDatabase();
    }

    private Product getProduct(int position) {
        return products.get(position);
    }

    private View.OnClickListener clickFavorite = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ImageButton button = (ImageButton) v;
            Product product = getProduct((Integer) v.getTag());
            database.openDatabase();
            boolean state = database.findFavorite(activityName.equals(fridge), product.getProduct_name());
            database.updateFavorite(activityName.equals(fridge), product.getProduct_name(), !state);
            setFavorite(!state, button);
            database.closeDatabase();
        }
    };

    private CompoundButton.OnCheckedChangeListener choiceProduct = new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            getProduct((Integer) buttonView.getTag()).setCheck_product(isChecked);
            checkItem.setIcon(context.getDrawable(allProductsChecked() ? R.drawable.uncheck : R.drawable.check));
        }
    };

    private void setFavorite(boolean on, ImageButton imageButton){
        imageButton.setImageResource(on ? R.mipmap.btn_star_big_on : R.mipmap.btn_star_big_off);
    }

    public boolean allProductsChecked(){
        for (Product p: products)
            if (!p.isCheck_product())
                return false;
        return true;
    }

    public void setCheckItem(MenuItem checkItem) {
        this.checkItem = checkItem;
    }
}
