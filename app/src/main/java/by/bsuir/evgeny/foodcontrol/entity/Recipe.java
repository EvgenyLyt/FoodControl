package by.bsuir.evgeny.foodcontrol.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Recipe implements Parcelable {
    private String name="";
    private String category="";
    private String time="";
    private String instruction="";
    private String id="";
    private ArrayList<Product> ingredients = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public ArrayList<Product> getIngredients() {
        return ingredients;
    }

    public void setIngredients(ArrayList<Product> ingredients) {
        this.ingredients = ingredients;
    }

    public Recipe(String _name, String _category, String _time, String _instruction, ArrayList<Product> products){
        name=_name;
        category=_category;
        time=_time;
        instruction=_instruction;
        ingredients=products;
    }

    public Recipe(){

    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(name);
        parcel.writeString(category);
        parcel.writeString(time);
        parcel.writeString(instruction);
        parcel.writeString(id);
        parcel.writeTypedList(ingredients);
    }

    public static final Parcelable.Creator<Recipe> CREATOR = new Parcelable.Creator<Recipe>() {
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    private Recipe(Parcel parcel) {
        name = parcel.readString();
        category = parcel.readString();
        time = parcel.readString();
        instruction = parcel.readString();
        id = parcel.readString();
        parcel.readTypedList(ingredients, Product.CREATOR);
        //ingredients = ArrayList<Product>(Arrays.asList(parcel.createTypedArrayList(Product.CREATOR)));
    }

}
