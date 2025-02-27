package com.example.homework;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RecommendActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private TextView tvRecommendation;
    private ImageView imgRecommendation;
    private String currentFood;
    private int currentCalories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recommend);

        // DBHelper 초기화
        dbHelper = new DBHelper(this);

        // UI 요소 참조
        ImageView imgBack = findViewById(R.id.img_back);
        Button btnRecommend = findViewById(R.id.btn_recommend);
        Button btnSave = findViewById(R.id.btn_save);
        tvRecommendation = findViewById(R.id.tv_recommendation);
        imgRecommendation = findViewById(R.id.img_recommendation);

        // 뒤로가기 버튼 클릭 이벤트
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecommendActivity.this, HomeActivity.class);
                startActivity(intent);
                finish(); // 현재 액티비티 종료
            }
        });

        // 추천 버튼 클릭 이벤트
        btnRecommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentFood = getRandomRecommendation();
                currentCalories = getFoodCalories(currentFood);

                tvRecommendation.setText(currentFood + "\n칼로리: " + currentCalories + "kcal");

                // 음식 이름에 따라 이미지 설정
                int imageResId = getFoodImageResource(currentFood);
                if (imageResId != 0) {
                    imgRecommendation.setImageResource(imageResId);
                } else {
                    imgRecommendation.setImageResource(R.drawable.food); // 기본 이미지
                }
            }
        });

        // 저장 버튼 클릭 이벤트
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveRecommendation(currentFood, currentCalories);
                Intent intent = new Intent(RecommendActivity.this, CaloriesActivity.class);
                startActivity(intent);
            }
        });
    }

    // 랜덤 추천 음식 가져오기
    private String getRandomRecommendation() {
        Cursor cursor = dbHelper.getAllRecommendations();
        int count = cursor.getCount();

        if (count == 0) {
            cursor.close();
            return "추천할 음식이 없습니다.";
        }

        // 랜덤으로 하나 선택
        Random random = new Random();
        int randomIndex = random.nextInt(count);
        cursor.moveToPosition(randomIndex);

        String foodName = cursor.getString(cursor.getColumnIndex("name"));
        cursor.close();
        return foodName;
    }

    // 음식 이름에 따라 이미지 리소스 반환
    private int getFoodImageResource(String foodName) {
        if (foodName.equals("떡볶이")) {
            return R.drawable.tteokbokki;
        } else if (foodName.equals("볶음밥")) {
            return R.drawable.friedrice;
        } else if (foodName.equals("계란말이")) {
            return R.drawable.eggroast;
        } else if (foodName.equals("파스타")) {
            return R.drawable.pasta;
        } else if (foodName.equals("샌드위치")) {
            return R.drawable.sandwich;
        } else {
            return R.drawable.food; // 기본 이미지
        }
    }

    // 음식 이름에 따라 칼로리 반환
    private int getFoodCalories(String foodName) {
        if (foodName.equals("떡볶이")) {
            return 550;
        } else if (foodName.equals("볶음밥")) {
            return 330;
        } else if (foodName.equals("계란말이")) {
            return 193;
        } else if (foodName.equals("파스타")) {
            return 325;
        } else if (foodName.equals("샌드위치")) {
            return 560;
        } else {
            return 0; // 기본 값
        }
    }

    // 추천 기록 저장
    private void saveRecommendation(String foodName, int calories) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", foodName);
        values.put("calories", calories);
        db.insert("calorie_records", null, values);
    }
}