package by.bsuir.evgeny.foodcontrol;

import android.content.Context;
import android.support.v7.app.AlertDialog;

public class MessageBox {
    public static void Show(Context context, String title, String message)
    {
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(context);
        dlgAlert.setMessage(message);
        dlgAlert.setTitle(title);
        dlgAlert.setPositiveButton("OK", null);
        dlgAlert.setCancelable(false);
        dlgAlert.create().show();
    }
}
