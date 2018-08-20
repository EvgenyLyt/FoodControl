package by.bsuir.evgeny.foodcontrol.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {
    private String product_name;
    private String product_descripton;
    private boolean check_product;
    public Product(String _name, String _description, boolean _check) {
        product_name=_name;
        product_descripton=_description;
        check_product=_check;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getProduct_descripton() {
        return product_descripton;
    }

    public void setProduct_descripton(String product_descripton) {
        this.product_descripton = product_descripton;
    }

    public boolean isCheck_product() {
        return check_product;
    }

    public void setCheck_product(boolean check_product) {
        this.check_product = check_product;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(product_name);
        parcel.writeString(product_descripton);
        parcel.writeByte((byte) (check_product ? 1 : 0));
    }

    public static final Parcelable.Creator<Product> CREATOR = new Parcelable.Creator<Product>() {
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    private Product(Parcel parcel) {
        product_name = parcel.readString();
        product_descripton = parcel.readString();
        check_product = parcel.readByte() != 0;
    }
}