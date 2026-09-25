
package com.AsandeMazibuko402311204.smartpantrymanager.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Bump DB_VERSION whenever you change table structure during development
    private static final String DB_NAME = "smart_pantry.db";
    private static final int DB_VERSION = 1;

    // Table names
    public static final String TABLE_PANTRY = "pantry_items";
    public static final String TABLE_RECIPES = "recipes";
    public static final String TABLE_RECIPE_INGREDIENTS = "recipe_ingredients";

    // pantry_items columns
    public static final String COL_PANTRY_ID = "id";
    public static final String COL_PANTRY_NAME = "name";
    public static final String COL_PANTRY_QTY = "quantity";
    public static final String COL_PANTRY_UNIT = "unit";
    public static final String COL_PANTRY_EXPIRY = "expiry_date";

    // recipes columns
    public static final String COL_RECIPE_ID = "id";
    public static final String COL_RECIPE_NAME = "name";
    public static final String COL_RECIPE_INSTRUCTIONS = "instructions";

    // recipe_ingredients columns
    public static final String COL_RI_ID = "id";
    public static final String COL_RI_RECIPE_ID = "recipe_id";
    public static final String COL_RI_NAME = "ingredient_name";
    public static final String COL_RI_QTY = "required_quantity";
    public static final String COL_RI_UNIT = "unit";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_PANTRY + " (" +
                COL_PANTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_PANTRY_NAME + " TEXT NOT NULL, " +
                COL_PANTRY_QTY + " REAL NOT NULL, " +
                COL_PANTRY_UNIT + " TEXT, " +
                COL_PANTRY_EXPIRY + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_RECIPES + " (" +
                COL_RECIPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_RECIPE_NAME + " TEXT NOT NULL, " +
                COL_RECIPE_INSTRUCTIONS + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_RECIPE_INGREDIENTS + " (" +
                COL_RI_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_RI_RECIPE_ID + " INTEGER NOT NULL, " +
                COL_RI_NAME + " TEXT NOT NULL, " +
                COL_RI_QTY + " REAL NOT NULL, " +
                COL_RI_UNIT + " TEXT, " +
                "FOREIGN KEY(" + COL_RI_RECIPE_ID + ") REFERENCES " + TABLE_RECIPES + "(" + COL_RECIPE_ID + "))");

        seedRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANTRY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE_INGREDIENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        onCreate(db);
    }

    // Pre-loads a starter set of recipes on first run (Section 2.2 requires 15-20)
    private void seedRecipes(SQLiteDatabase db) {
        long tomatoPastaId = insertRecipe(db, "Tomato Pasta",
                "1. Boil pasta.\n2. Fry garlic and tomato.\n3. Mix together and serve.");
        insertIngredient(db, tomatoPastaId, "pasta", 200, "g");
        insertIngredient(db, tomatoPastaId, "tomato", 2, "pcs");
        insertIngredient(db, tomatoPastaId, "garlic", 1, "pcs");

        long omeletteId = insertRecipe(db, "Cheese Omelette",
                "1. Beat eggs.\n2. Add cheese.\n3. Cook on low heat until set.");
        insertIngredient(db, omeletteId, "egg", 2, "pcs");
        insertIngredient(db, omeletteId, "cheese", 50, "g");

        // TODO: add more recipes here until you reach 15-20 total (Section 2.2)
    }

    private long insertRecipe(SQLiteDatabase db, String name, String instructions) {
        ContentValues cv = new ContentValues();
        cv.put(COL_RECIPE_NAME, name);
        cv.put(COL_RECIPE_INSTRUCTIONS, instructions);
        return db.insert(TABLE_RECIPES, null, cv);
    }

    private void insertIngredient(SQLiteDatabase db, long recipeId, String name, double qty, String unit) {
        ContentValues cv = new ContentValues();
        cv.put(COL_RI_RECIPE_ID, recipeId);
        cv.put(COL_RI_NAME, name);
        cv.put(COL_RI_QTY, qty);
        cv.put(COL_RI_UNIT, unit);
        db.insert(TABLE_RECIPE_INGREDIENTS, null, cv);
    }


    // ---------- PANTRY CRUD ----------

    public long addPantryItem(String name, double quantity, String unit, String expiryDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_PANTRY_NAME, name.trim().toLowerCase());
        cv.put(COL_PANTRY_QTY, quantity);
        cv.put(COL_PANTRY_UNIT, unit);
        cv.put(COL_PANTRY_EXPIRY, expiryDate);
        return db.insert(TABLE_PANTRY, null, cv);
    }

    public List<PantryItem> getAllPantryItems() {
        List<PantryItem> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PANTRY, null, null, null, null, null, COL_PANTRY_NAME + " ASC");

        if (cursor.moveToFirst()) {
            do {
                items.add(new PantryItem(
                        cursor.getLong(cursor.getColumnIndexOrThrow(COL_PANTRY_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_PANTRY_NAME)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PANTRY_QTY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_PANTRY_UNIT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_PANTRY_EXPIRY))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return items;
    }

    public int updatePantryItem(long id, String name, double quantity, String unit, String expiryDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_PANTRY_NAME, name.trim().toLowerCase());
        cv.put(COL_PANTRY_QTY, quantity);
        cv.put(COL_PANTRY_UNIT, unit);
        cv.put(COL_PANTRY_EXPIRY, expiryDate);
        return db.update(TABLE_PANTRY, cv, COL_PANTRY_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public void deletePantryItem(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PANTRY, COL_PANTRY_ID + " = ?", new String[]{String.valueOf(id)});
    }

    // ---------- RECIPE READ (with ingredients attached) ----------

    public List<Recipe> getAllRecipesWithIngredients() {
        List<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor recipeCursor = db.query(TABLE_RECIPES, null, null, null, null, null, COL_RECIPE_NAME + " ASC");

        if (recipeCursor.moveToFirst()) {
            do {
                long recipeId = recipeCursor.getLong(recipeCursor.getColumnIndexOrThrow(COL_RECIPE_ID));
                Recipe recipe = new Recipe(
                        recipeId,
                        recipeCursor.getString(recipeCursor.getColumnIndexOrThrow(COL_RECIPE_NAME)),
                        recipeCursor.getString(recipeCursor.getColumnIndexOrThrow(COL_RECIPE_INSTRUCTIONS))
                );
                recipe.setIngredients(getIngredientsForRecipe(db, recipeId));
                recipes.add(recipe);
            } while (recipeCursor.moveToNext());
        }
        recipeCursor.close();
        return recipes;
    }

    private List<RecipeIngredient> getIngredientsForRecipe(SQLiteDatabase db, long recipeId) {
        List<RecipeIngredient> ingredients = new ArrayList<>();
        Cursor cursor = db.query(TABLE_RECIPE_INGREDIENTS, null,
                COL_RI_RECIPE_ID + " = ?", new String[]{String.valueOf(recipeId)},
                null, null, null);

        if (cursor.moveToFirst()) {
            do {
                ingredients.add(new RecipeIngredient(
                        cursor.getLong(cursor.getColumnIndexOrThrow(COL_RI_ID)),
                        cursor.getLong(cursor.getColumnIndexOrThrow(COL_RI_RECIPE_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_RI_NAME)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COL_RI_QTY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_RI_UNIT))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ingredients;
    }


    // ---------- STRICT MATCHING LOGIC (Section 2.3) ----------

    public List<Recipe> getSuggestedRecipes() {
        List<Recipe> allRecipes = getAllRecipesWithIngredients();
        List<PantryItem> pantryItems = getAllPantryItems();
        List<Recipe> suggested = new ArrayList<>();

        for (Recipe recipe : allRecipes) {
            if (canMakeRecipe(recipe, pantryItems)) {
                suggested.add(recipe);
            }
        }
        return suggested;
    }

    private boolean canMakeRecipe(Recipe recipe, List<PantryItem> pantryItems) {
        for (RecipeIngredient required : recipe.getIngredients()) {
            if (!pantryHasEnough(required, pantryItems)) {
                return false; // even ONE missing ingredient disqualifies the whole recipe
            }
        }
        return true;
    }

    private boolean pantryHasEnough(RecipeIngredient required, List<PantryItem> pantryItems) {
        String neededName = normalize(required.getIngredientName());

        for (PantryItem item : pantryItems) {
            if (normalize(item.getName()).equals(neededName)) {
                return item.getQuantity() >= required.getRequiredQuantity();
            }
        }
        return false; // ingredient not found in pantry at all
    }

    // Handles "tomato" vs "tomatoes", extra spaces, and case differences (Section 2.3)
    private String normalize(String ingredientName) {
        String cleaned = ingredientName.trim().toLowerCase();
        if (cleaned.endsWith("es")) {
            cleaned = cleaned.substring(0, cleaned.length() - 2);
        } else if (cleaned.endsWith("s") && !cleaned.endsWith("ss")) {
            cleaned = cleaned.substring(0, cleaned.length() - 1);
        }
        return cleaned;
    }

}