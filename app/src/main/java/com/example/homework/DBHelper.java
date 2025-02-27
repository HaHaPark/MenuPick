package com.example.homework;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "men.db";
    private static final int DATABASE_VERSION = 4;

    private static final String TABLE_RECOMMEND = "recommend";
    private static final String TABLE_CALORIE_RECORDS = "calorie_records";
    private static final String TABLE_RECIPE = "recipe";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_CALORIES = "calories";
    private static final String COLUMN_INGREDIENTS = "ingredients";
    private static final String COLUMN_RECIPE = "recipe";

    private static final String COLUMN_ENGLISH_NAME = "english_name"; // 영어 이름 컬럼 추가

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createRecommendTable = "CREATE TABLE " + TABLE_RECOMMEND + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT NOT NULL);";
        db.execSQL(createRecommendTable);

        insertInitialData(db);

        String createCalorieRecordsTable = "CREATE TABLE " + TABLE_CALORIE_RECORDS + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT, "
                + COLUMN_CALORIES + " INTEGER);";
        db.execSQL(createCalorieRecordsTable);

        String createRecipeTable = "CREATE TABLE " + TABLE_RECIPE + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT NOT NULL, "
                + COLUMN_ENGLISH_NAME + " TEXT NOT NULL, " // 영어 이름 컬럼 추가
                + COLUMN_INGREDIENTS + " TEXT, "
                + COLUMN_RECIPE + " TEXT);";
        db.execSQL(createRecipeTable);

        insertRecipeData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECOMMEND);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CALORIE_RECORDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE);
        onCreate(db);
    }

    private void insertInitialData(SQLiteDatabase db) {
        String[] initialData = {"떡볶이", "볶음밥", "계란말이", "파스타", "샌드위치"};
        for (String name : initialData) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME, name);
            db.insert(TABLE_RECOMMEND, null, values);
        }
    }

    public void deleteCalorieRecord(String name, int calories) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("calorie_records", "name = ? AND calories = ?", new String[]{name, String.valueOf(calories)});
    }


    private void insertRecipeData(SQLiteDatabase db) {
        String[][] recipes = {
                {"떡볶이", "tteokbokki", "떡, 고추장, 설탕, 물, 어묵, 대파", "물에 고추장, 설탕, 떡, 어묵을 넣고 끓이다가 대파를 넣어 걸쭉해질 때까지 조리합니다."},
                {"샌드위치", "sandwich", "식빵, 햄, 치즈, 양상추, 마요네즈", "식빵에 마요네즈를 바르고 햄, 치즈, 양상추를 올려 샌드위치 형태로 겹칩니다."},
                {"볶음밥", "friedrice", "밥, 간장, 기름, 달걀, 야채 (당근, 양파 등)", "기름에 야채와 달걀을 볶은 뒤 밥과 간장을 넣고 섞어가며 볶습니다."},
                {"파스타", "pasta", "파스타 면, 토마토소스, 올리브오일, 마늘, 파마산 치즈", "삶은 파스타 면에 올리브오일, 마늘, 토마토소스를 섞고 치즈를 뿌립니다."},
                {"계란말이", "eggroast", "달걀, 소금, 기름, 파", "달걀에 소금을 넣고 푼 뒤 팬에 얇게 부쳐가며 돌돌 말아 완성합니다."}
        };

        for (String[] recipe : recipes) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME, recipe[0]);
            values.put(COLUMN_ENGLISH_NAME, recipe[1]);
            values.put(COLUMN_INGREDIENTS, recipe[2]);
            values.put(COLUMN_RECIPE, recipe[3]);
            db.insert(TABLE_RECIPE, null, values);
        }
    }

    public Cursor getAllRecommendations() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_RECOMMEND, null, null, null, null, null, null);
    }

    public void insertCalorieRecord(String name, int calories) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_CALORIES, calories);
        db.insert(TABLE_CALORIE_RECORDS, null, values);
    }

    public Cursor getAllCalorieRecords() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_CALORIE_RECORDS, null, null, null, null, null, null);
    }

    public Cursor getAllRecipes() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_RECIPE, null, null, null, null, null, null);
    }

    public Cursor searchRecipes(String query) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_RECIPE,
                null,
                COLUMN_NAME + " LIKE ? OR " + COLUMN_ENGLISH_NAME + " LIKE ?",
                new String[]{"%" + query + "%", "%" + query + "%"},
                null,
                null,
                null);
    }
}
