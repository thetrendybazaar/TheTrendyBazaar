package com.thetrendybazaar.thetrendybazaar;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class ItemManager {
    static SQLiteDatabase readDb, writeDb;
    static String tableName = "Item";

    public long add(Item item){
        ContentValues vals = new ContentValues();
        vals.put("ArticleId", item.articleId);
        vals.put("ManufacturerId", item.manufacturerId);
        vals.put("Quantity", item.quantity);
        vals.put("Price", item.price);
        vals.put("Description", item.description);
        vals.put("Category", item.category);
        vals.put("ItemName", item.name);
        long id =  writeDb.insert(tableName, null, vals);
        item.articleId = (int) id;
        return id;
    }
    public void delete(Item item){
        writeDb.delete(tableName, "ArticleId=" + item.articleId, null);
    }

    public void update(Item item){
        ContentValues vals = new ContentValues();
        if(item.articleId!=null)
            vals.put("ArticleId", item.articleId);
        if(item.manufacturerId!=null)
            vals.put("ManufacturerId", item.manufacturerId);
        if(item.quantity!=null)
            vals.put("Quantity", item.quantity);
        if(item.price!=null)
            vals.put("Price", item.price);
        if(item.description!=null)
            vals.put("Description", item.description);
        if(item.category!=null)
            vals.put("Category", item.category);
        if(item.name!=null)
            vals.put("ItemName", item.name);
        writeDb.update(tableName, vals, "ArticleId=" + item.articleId, null);
    }

    public static void setDb(SQLiteDatabase rDb, SQLiteDatabase wDb){
        readDb = rDb;
        writeDb = wDb;
    }


    public ArrayList<Item> getItems(int ordered){
        Cursor cursor = null;
        if(ordered==0) {
            cursor = readDb.query(tableName, null, null, null, null, null, null, null);
        }
        else if(ordered==1){
            cursor = readDb.query(tableName, null, null, null, null, null, "Price ASC", null);
        }
        else if(ordered==2){
            cursor = readDb.query(tableName, null, null, null, null, null, "Price DESC", null);
        }
        ArrayList<Item> items = new ArrayList<>();
        Item i;
        if(cursor != null && cursor.getCount() > 0){
            cursor.moveToFirst();
            do{
                i = new Item(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getInt(2),
                        cursor.getDouble(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6)
                );
                items.add(i);
            }while(cursor.moveToNext());
            cursor.close();
        }
        return items;
    }

    public ArrayList<Item> getItemsByCategory(String category, int ordered){
        Cursor cursor = null;
        if(ordered==0) {
            cursor = readDb.query(tableName, null, "Category = ?" , new String[]{category + ""}, null, null, null, null);
        }
        else if(ordered==1){
            cursor = readDb.query(tableName, null, "Category = ?" , new String[]{category + ""}, null, null, "Price ASC", null);
        }
        else if(ordered==2){
            cursor = readDb.query(tableName, null, "Category = ?" , new String[]{category + ""}, null, null, "Price DESC", null);
        }
        ArrayList<Item> categoryItems = new ArrayList<>();
        if(cursor != null && cursor.getCount() > 0){
            Item i;
            cursor.moveToFirst();
            do{
                i = DatabaseManager.items.select(cursor.getInt(0));
                categoryItems.add(i);
            }while(cursor.moveToNext());
            cursor.close();
        }
        return categoryItems;
    }

    public int numOfItemsInCategory(String category){
        Cursor cursor = readDb.rawQuery("SELECT COUNT(*) FROM " + tableName + " WHERE Category = " + category, null);
        cursor.moveToFirst();
        return cursor.getInt(0);
    }
    public int getItemCount(){
        Cursor cursor = readDb.rawQuery("SELECT COUNT(*) FROM " + tableName, null);
        cursor.moveToFirst();
        return cursor.getInt(0);
    }
    public void alterQuantity(int articleId, int addQuantity){
        ContentValues vals = new ContentValues();
        Item i = DatabaseManager.items.select(articleId);
        i.quantity = i.quantity + addQuantity;
        vals.put("ArticleId", articleId);
        vals.put("Quantity", i.quantity);
        writeDb.update(tableName, vals, "ArticleId=" + articleId, null);
    }

    public Item select(int articleId){
        Cursor cursor = readDb.query(tableName, null, "ArticleId = ?", new String[]{articleId + ""}, null, null, null, null);
        Item i = null;
        if(cursor != null && cursor.getCount() > 0){
            cursor.moveToFirst();
            i = new Item(
                    cursor.getInt(0),
                    cursor.getInt(1),
                    cursor.getInt(2),
                    cursor.getDouble(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6)
            );
        }
        return i;
    }
}
