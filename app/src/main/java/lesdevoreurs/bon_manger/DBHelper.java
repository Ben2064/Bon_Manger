package lesdevoreurs.bon_manger;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Nicolas on 2015-02-26.
 */
public class DBHelper extends SQLiteOpenHelper {

    static final String DB_NAME = "bonmanger.db";
    static final int DB_VERSION = 46;    //******************METTRE À JOUR À CHAQUE FOIS!!!!!!!***********************//

    //CURRENT::table recipe for current recipe
    static final String TABLE_RECIPES = "recipes";
    //CURRENT::table ingredients for current recipe
    static final String TABLE_RINGREDIENTS = "ringredients";
    //GROCERY::table for ingredients for grocerylsit
    static final String TABLE_GROCERY = "grocery";
    //COOKBOOK::Table cookbook
    static final String TABLE_COOKBOOK = "cookbook";
    //COOKBOOK::table ingredients for cookbook
    static final String TABLE_CINGREDIENTS = "cookbookingredients";

    public static final String R_ID = "_id";
    public static final String R_TITRE = "titre_recette";
    public static final String R_IMAGE = "image";
    public static final String R_DESCRIPTION = "description";
    public static final String R_COOKTIME = "cooktime";
    public static final String R_TOTALTIME = "totaltime";
    public static final String R_INSTRUCTIONS = "instructions";
    public static final String RI_ID = "_id";    //link with previous table
    public static final String RI_NAME = "name";
    public static final String RI_NUMBER = "number";
    public static final String RI_METRIC = "metric";
    public static final String G_NAME = "name";
    public static final String G_NUMBER = "number";
    public static final String G_METRIC = "metric";
    public static final String G_ID = "_id";
    public static final String C_ID = "_id";
    public static final String C_TITRE = "titre_recette";
    public static final String C_IMAGE = "image";
    public static final String C_DESCRIPTION = "description";
    public static final String C_COOKTIME = "cooktime";
    public static final String C_TOTALTIME = "totaltime";
    public static final String C_INSTRUCTIONS = "instructions";
    public static final String CI_ID = "_id";    //link with previous table
    public static final String CI_NAME = "name";
    public static final String CI_NUMBER = "number";
    public static final String CI_METRIC = "metric";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //CURRENT::
        String sql = "create table " + TABLE_RECIPES
                + " (" + R_ID + " primary key, "
                + R_TITRE + " text, "
                + R_IMAGE + " text, "
                + R_DESCRIPTION + " text,"
                + R_COOKTIME + " text, "
                + R_TOTALTIME + " text, "
                + R_INSTRUCTIONS + " text)";
        db.execSQL(sql);

        //CURRENT::
        sql = "create table " + TABLE_RINGREDIENTS
                + " (" + RI_ID + " primary key, "
                + RI_NAME + " text, "
                + RI_NUMBER + " text, "
                + RI_METRIC + " text)";
        db.execSQL(sql);

        //GROCERY::
        sql = "create table " + TABLE_GROCERY
                + " (" + G_NAME + " primary key, "
                + G_NUMBER + " text, "
                + G_METRIC + " text, "
                + G_ID + " text)";
        db.execSQL(sql);

        //COOKBOOK::
        sql = "create table " + TABLE_COOKBOOK
                + " (" + C_ID + " primary key, "
                + C_TITRE + " text, "
                + C_IMAGE + " text, "
                + C_DESCRIPTION + " text,"
                + C_COOKTIME + " text, "
                + C_TOTALTIME + " text, "
                + C_INSTRUCTIONS + " text)";
        db.execSQL(sql);

        //COOKBOOK::
        sql = "create table " + TABLE_CINGREDIENTS
                + " (" + CI_NAME + " text, "
                + CI_ID + " text, "
                + CI_NUMBER + " text, "
                + CI_METRIC + " text)";
        db.execSQL(sql);

        Log.d("DB", "database created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_RECIPES);
        db.execSQL("drop table if exists " + TABLE_RINGREDIENTS);
        db.execSQL("drop table if exists " + TABLE_GROCERY);
        db.execSQL("drop table if exists " + TABLE_COOKBOOK);
        db.execSQL("drop table if exists " + TABLE_CINGREDIENTS);
        onCreate(db);
    }

    //COOKBOOK::Return the recipes for cookbook
    public static Cursor listRecipe(SQLiteDatabase db) {
        String sql = "select * from " + TABLE_COOKBOOK
                + " order by " + C_TITRE + " asc";
        Cursor c = db.rawQuery(sql, null);
        Log.d("DB", "lisle recette nb = " + c.getCount());
        return c;
    }

    //COOKBOOK::Return the ingredients for cookbook
    public static Cursor listRecipeIngredients(SQLiteDatabase db) {
        String sql = "select * from " + TABLE_CINGREDIENTS
                + " order by " + CI_NAME + " asc";
        Cursor c = db.rawQuery(sql, null);
        Log.d("DB", "lisle recette nb = " + c.getCount());
        return c;
    }

    //COOKBOOK::Search recipe by id
    public static Cursor searchBookRecipe(SQLiteDatabase db, String id) {
        String args[] = new String[]{"%" + id + "%"};
        Cursor c = db.rawQuery("select * from " + TABLE_COOKBOOK + " where " + R_ID + " like ?", args);
        return c;
    }

    //COOKBOOK::Search list of ingredients by repice id
    public static Cursor searchBookRecipeIngredients(SQLiteDatabase db, String id) {
        String args[] = new String[]{"%" + id + "%"};
        Cursor c = db.rawQuery("select * from " + TABLE_CINGREDIENTS + " where " + RI_ID + " like ?", args);
        return c;
    }

    //COOKBOOK::add recipe
    public static void addRecipe(SQLiteDatabase db, String ID, String name, String image, String description,
                                 String cooktime, String totaltime, String instructions) {
        String sql = "INSERT INTO "
                + TABLE_COOKBOOK
                + " (" + C_ID + ", " + C_TITRE + ", " + C_IMAGE + ", " + C_DESCRIPTION + ", " + C_COOKTIME
                + ", " + C_TOTALTIME + ", " + C_INSTRUCTIONS + ")"
                + " VALUES ('" + ID + "', '" + name.replace("'", "''") + "', '" + image + "', '" + description.replace("'", "''") + "', '" + cooktime + "', '" + totaltime + "', '" + instructions.replace("'", "''") + "')";
        db.execSQL(sql);
    }

    //COOKBOOK::add ingredients
    public static void addRecipeIngredient(SQLiteDatabase db, String name, String number, String metric, String id) {
        String sql = "INSERT INTO "
                + TABLE_CINGREDIENTS
                + " (" + CI_NAME + ", " + CI_NUMBER + ", " + CI_METRIC + ", " +  CI_ID + ")"
                + " VALUES ('" + name.replace("'", "''") + "', '" + number + "', '" + metric + "', '" + id + "')";
        db.execSQL(sql);
    }

    //COOKBOOK::delete recipe
    public static void deleteRecipe(SQLiteDatabase db, String id) {
        db.execSQL("delete from " + TABLE_COOKBOOK + " where id='" + id + "'");
        db.execSQL("delete from " + TABLE_CINGREDIENTS + " where id='" + id + "'");
    }

    //CURRENT::Return the recipe for current
    public static Cursor currentRecipe(SQLiteDatabase db) {
        String sql = "select * from " + TABLE_RECIPES
                + " order by " + R_TITRE + " asc";
        Cursor c = db.rawQuery(sql, null);
        Log.d("DB", "lisle recette nb = " + c.getCount());
        return c;
    }

    //CURRENT::Return the ingredients for current
    public static Cursor currentRecipeIngredients(SQLiteDatabase db) {
        String sql = "select * from " + TABLE_RINGREDIENTS
                + " order by " + RI_NAME + " asc";
        Cursor c = db.rawQuery(sql, null);
        Log.d("DB", "lisle recette nb = " + c.getCount());
        return c;
    }

    //CURRENT::Check if empty
    //http://stackoverflow.com/questions/22630307/sqlite-check-if-table-is-empty
    public static boolean isEmpty(SQLiteDatabase db) {

        boolean empty = true;

        String sql = "select count(*) from " + TABLE_RECIPES;
        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();
        int icount = c.getInt(0);

        if (icount > 0)
            empty = false;

        return empty;
    }

    //CURRENT::add recipe
    public static void addCurrent(SQLiteDatabase db, String ID, String name, String imagePath, String description,
                                  String cooktime, String totaltime, String instructions) {
        db.execSQL("DELETE FROM " + TABLE_RECIPES);
        String sql = "INSERT INTO "
                + TABLE_RECIPES
                + " (" + R_ID + ", " + R_TITRE + ", " + R_IMAGE + ", " + R_DESCRIPTION + ", " + R_COOKTIME
                + ", " + R_TOTALTIME + ", " + R_INSTRUCTIONS + ")"
                + " VALUES ('" + ID + "', '" + name.replace("'", "''") + "', '" + imagePath + "', '" + description.replace("'", "''") + "', '" + cooktime + "', '" + totaltime + "', '" + instructions.replace("'", "''") + "')";
        db.execSQL(sql);
    }

    //CURRENT::add ingredients
    public static void addCurrentIngredient(SQLiteDatabase db, String name, String number, String metric, int id) {
        //db.execSQL("DELETE FROM "+TABLE_RINGREDIENTS+" WHERE "+RI_NAME+" = plxrrmpa)");
        String sql = "INSERT INTO "
                + TABLE_RINGREDIENTS
                + " (" + RI_ID + ", " + RI_NAME + ", " + RI_NUMBER + ", " + RI_METRIC + ")"
                + " VALUES ('" + id + "', '" + name.replace("'", "''") + "', '" + number + "', '" + metric + "')";
        db.execSQL(sql);
    }

    //GROCERY::Return ingredients for grocery list
    public static Cursor listIngredients(SQLiteDatabase db) {
        String sql = "select * from " + TABLE_GROCERY
                + " order by " + G_NAME + " asc";
        Cursor c = db.rawQuery(sql, null);
        Log.d("DB", "lisle recette nb = " + c.getCount());
        return c;
    }

    //GROGERY::search ingredients by name
    public static Cursor searchIngredient(SQLiteDatabase db, String name) {
        String args[] = new String[]{"%" + name.replace("'", "''") + "%"};
        Cursor c = db.rawQuery("select * from " + TABLE_GROCERY + " where " + G_NAME + " like ?", args);
        return c;
    }

    //GROCERY::add more of the same ingredients by passing the new number and the name of the ingredient
    public static void setIngredientsNumber(SQLiteDatabase db, String name, String number) {
        ContentValues cv = new ContentValues();
        cv.put(G_NUMBER, number);
        db.update(TABLE_GROCERY, cv, G_NAME + " = " + name.replace("'", "''"), null);
    }

    //GROCERY::add ingredients
    public static void addIngredient(SQLiteDatabase db, String name, String number, String metric) {
        String sql = "INSERT INTO "
                + TABLE_GROCERY
                + " (" + G_NAME + ", " + G_NUMBER + ", " + G_METRIC + ")"
                + " VALUES ('" + name.replace("'", "''") + "', '" + number + "', '" + metric +"')";
        db.execSQL(sql);
    }
}
