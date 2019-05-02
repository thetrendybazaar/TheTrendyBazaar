package com.thetrendybazaar.thetrendybazaar;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;

import java.sql.Array;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;

public class PlacesManager {
    static SQLiteDatabase readDb, writeDb;
    static String tableName = "Adds";

    public long add(Customer customer, Order order){
        ContentValues vals = new ContentValues();
        vals.put("CustomerId", customer.id);
        vals.put("OrderNumber", order.orderNumber);
        vals.put("Date", Calendar.getInstance().getTime().toString());
        long index = writeDb.insert(tableName, null, vals);
        adjustForOrder(order);
        return index;
    }
    public void delete(Order order){
        writeDb.delete(tableName, "OrderNumber= ?", new String[] {order.orderNumber  + ""});
    }

    public void update(Customer customer, Order order, Date date){
        ContentValues vals = new ContentValues();
        vals.put("CustomerId", customer.id);
        vals.put("OrderNumber", order.orderNumber);
        vals.put("Date", date.toString());
        writeDb.update(tableName, vals, "OrderNumber = ?", new String[] {order.orderNumber + ""});
    }

    public static void setDb(SQLiteDatabase rDb, SQLiteDatabase wDb){
        readDb = rDb;
        writeDb = wDb;
    }

    public void adjustForOrder(Order order){
        ShoppingCart cart = DatabaseManager.currentShoppingCarts.select(order.cartId);
        ArrayList<Item> itemsInOrder = DatabaseManager.contains.getItemsForShoppingCart(cart.cartId);
        for(int j = 0; j<cart.itemQuantity; j++){
            Item i = itemsInOrder.get(j);
            i.quantity = i.quantity - 1;
            DatabaseManager.items.update(i);
        }
    }
    public ArrayList<Order> getOrdersMadeByCustomer(Customer customer){
        Cursor cursor = readDb.query(tableName, null, "CustomerId = ?", new String[]{customer.id + ""}, null, null, null, null);
        ArrayList<Order> ordersMade = new ArrayList<>();
        Order o;
        if(cursor != null){
            cursor.moveToFirst();
            do{
                o = DatabaseManager.orders.select(cursor.getInt(1));
                ordersMade.add(o);
            }while(cursor.moveToNext());
            cursor.close();
        }
        return ordersMade;
    }

    public int getOrderCount(Customer customer){
        Cursor cursor = readDb.rawQuery("SELECT COUNT(*) FROM " + tableName + " WHERE CustomerId = " + customer.id, null);
        cursor.moveToFirst();
        return cursor.getInt(0);
    }
}