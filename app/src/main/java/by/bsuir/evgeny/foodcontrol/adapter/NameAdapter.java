package by.bsuir.evgeny.foodcontrol.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import by.bsuir.evgeny.foodcontrol.entity.Name;
import by.bsuir.evgeny.foodcontrol.entity.Product;
import by.bsuir.evgeny.foodcontrol.R;
import by.bsuir.evgeny.foodcontrol.database.Database;
import by.bsuir.evgeny.foodcontrol.database.Helper;

public class NameAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater lInflater;
    private ArrayList<Name> products;
    private Database database;
    private String activityName;

    public NameAdapter(Context context, ArrayList<Name> products, String activityName) {
        this.context = context;
        this.products = products;
        this.lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.database = new Database(context);
        this.activityName = activityName;
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
        if (view == null) {
            view = lInflater.inflate(R.layout.name_product, parent, false);
        }
        Name product_name = getName(position);
        TextView tv_name_product = (TextView) view.findViewById(R.id.text_name_product);
        tv_name_product.setText(product_name.getName_product());
        tv_name_product.setTag(position);
        CheckBox cb_check_name = (CheckBox) view.findViewById(R.id.btn_check_name);
        cb_check_name.setTag(position);
        cb_check_name.setOnCheckedChangeListener(check_product);
        cb_check_name.setChecked(product_name.isCheck_product());
        return view;
    }

    private Name getName(int position) {
        return ((Name) getItem(position));
    }

    private CompoundButton.OnCheckedChangeListener check_product = new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            Name product = getName((Integer) buttonView.getTag());
            if (product.isCheck_product()!= isChecked)
                Toast.makeText(context, getActivityName(activityName, isChecked), Toast.LENGTH_SHORT).show();
            product.setCheck_product(isChecked);
            if (!activityName.equals(context.getResources().getString(R.string.addRecipeActivityName))) {
                ArrayList<String> params = getParameters(activityName.equals(context.getResources().getString(R.string.mainActivityName)), product);
                database.openDatabase();
                if (isChecked)
                    database.addProduct(params.get(0), params.get(1), params.get(2), params.get(3), params.get(4));
                else
                    database.deleteProduct(params.get(0), params.get(1), params.get(2), params.get(3), params.get(4));
                database.closeDatabase();
            }
        }
    };

    private ArrayList<String> getParameters (boolean fridge, Name product){
        ArrayList<String> parameters = new ArrayList<>();
        parameters.add(product.getName_product());
        parameters.add("");
        parameters.add((fridge) ? Helper.COLUMN_PRODUCT_DESCRIPTION : Helper.COLUMN_PRODUCT_BOX_DESCRIPTION);
        parameters.add((fridge) ? Helper.COLUMN_PRODUCT_FRIDGE : Helper.COLUMN_PRODUCT_BOX);
        parameters.add((fridge) ? Helper.COLUMN_PRODUCT_FAVORITE : Helper.COLUMN_PRODUCT_BOX_FAVORITE);
        return parameters;
    }

    private String getActivityName(String activityName, boolean in){
        if (activityName.equals(context.getResources().getString(R.string.mainActivityName)))
            return (in ? "Продукт добавлен в холодильник." : "Продукт удален из холодильника.");
        else
            if (activityName.equals(context.getResources().getString(R.string.addRecipeActivityName)))
                return (in ? "Ингредиент добавлен в рецепт." : "Ингредиент удален из рецепта.");
                else
                    if (activityName.equals(context.getResources().getString(R.string.basketActivityName)))
                        return (in ? "Продукт добавлен в корзину." : "Продукт удален из корзины.");
                else
                    return "";
    }
}
