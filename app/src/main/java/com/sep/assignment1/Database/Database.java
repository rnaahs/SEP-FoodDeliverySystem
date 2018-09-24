package com.sep.assignment1.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;
import com.sep.assignment1.model.Order;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteAssetHelper{
    private static final String DB_NAME="LocalDB";
    private static final int DB_VER=1;
    private Database (Context context){
        super(context, DB_NAME, null, DB_VER);
    }
    public List<Order> getCarts(){
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelect = {"FoodName", "FoodID", "Quantity", "Price","Discount"};
        String sqlTable = "OrderDetail";

        qb.setTables(sqlTable);
        Cursor c = qb.query(db,sqlSelect, null,null,null,null,null);

        final List<Order> result = new ArrayList<>();
        if (c.moveToFirst()) {

            do {
                result.add(new Order(c.getString(c.getColumnIndex("FoodID")),
                        c.getString(c.getColumnIndex("FoodName")),
                        c.getString(c.getColumnIndex("Quantity")),
                        c.getString(c.getColumnIndex("Price")),
                        c.getString(c.getColumnIndex("Discount"))
                ));
            }while (c.moveToNext());
            }
        return result;
    }

    public void addToCart(Order order)
    {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT INTO OrderDetail(FoodID,FoodName,Quantity,Price,Discount) VALUES('%s','%s','%s','%s','%s');",
                order.getFoodID(),
                order.getFoodName(),
                order.getQuantity(),
                order.getPrice(),
                order.getDiscount());
        db.execSQL(query);
    }

    public void cleanCart()
    {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM OrderDetail");
        db.execSQL(query);
    }
}
