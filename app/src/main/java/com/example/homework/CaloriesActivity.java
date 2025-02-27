package com.example.homework;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CaloriesActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private LinearLayout recordContainer;
    private TextView tvTotalCalories;
    private int totalCalories = 0; // 총 칼로리 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calories);

        dbHelper = new DBHelper(this);
        recordContainer = findViewById(R.id.record_container);
        tvTotalCalories = findViewById(R.id.tv_total_calories);

        // 입력 필드 및 버튼 참조
        EditText etFoodName = findViewById(R.id.et_food_name);
        EditText etCalories = findViewById(R.id.et_calories);
        Button btnAddFood = findViewById(R.id.btn_add_food);

        // 뒤로가기 아이콘 참조
        ImageView imgBack = findViewById(R.id.img_back);

        // 뒤로가기 클릭 이벤트
        imgBack.setOnClickListener(v -> {
            Intent intent = new Intent(CaloriesActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });

        // 저장된 기록 표시
        displaySavedRecords();

        // 새로운 메뉴 추가 버튼 클릭 이벤트
        btnAddFood.setOnClickListener(v -> {
            String foodName = etFoodName.getText().toString().trim();
            String caloriesStr = etCalories.getText().toString().trim();

            if (foodName.isEmpty() || caloriesStr.isEmpty()) {
                Toast.makeText(CaloriesActivity.this, "메뉴 이름과 칼로리를 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            int calories = Integer.parseInt(caloriesStr);

            // DB에 새로운 기록 저장
            dbHelper.insertCalorieRecord(foodName, calories);

            // UI에 추가
            addRecordToUI(foodName, calories);

            // 입력 필드 초기화
            etFoodName.setText("");
            etCalories.setText("");

            // 총 칼로리 업데이트
            totalCalories += calories;
            updateTotalCaloriesDisplay();
        });
    }

    private void addRecordToUI(String foodName, int calories) {
        // 각 기록 항목 생성
        LinearLayout recordLayout = new LinearLayout(this);
        recordLayout.setOrientation(LinearLayout.HORIZONTAL);
        recordLayout.setPadding(8, 8, 8, 8);

        // 음식 이미지 추가
        ImageView foodImage = new ImageView(this);
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(100, 100);
        imageParams.setMargins(0, 0, 16, 0);
        foodImage.setLayoutParams(imageParams);
        foodImage.setImageResource(getFoodImageResource(foodName));
        recordLayout.addView(foodImage);

        // 음식 정보 텍스트 추가
        TextView recordView = new TextView(this);
        recordView.setText(foodName + " - " + calories + "kcal");
        recordView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1));
        recordView.setTextSize(18);
        recordView.setTextColor(0xFF431903);
        recordLayout.addView(recordView);

        // 삭제 버튼 추가
        Button deleteButton = new Button(this);
        deleteButton.setText("삭제");
        deleteButton.setTextSize(16);
        deleteButton.setTextColor(0xFFFFFFFF);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // DB에서 삭제
                dbHelper.deleteCalorieRecord(foodName, calories);

                // 총 칼로리 감소
                totalCalories -= calories;
                updateTotalCaloriesDisplay();

                // UI에서 삭제
                recordContainer.removeView(recordLayout);
            }
        });
        recordLayout.addView(deleteButton);

        // 컨테이너에 추가
        recordContainer.addView(recordLayout);
    }

    private void displaySavedRecords() {
        Cursor cursor = dbHelper.getAllCalorieRecords();
        while (cursor.moveToNext()) {
            String foodName = cursor.getString(cursor.getColumnIndex("name"));
            int calories = cursor.getInt(cursor.getColumnIndex("calories"));
            int recordId = cursor.getInt(cursor.getColumnIndex("id"));

            // 총 칼로리 업데이트
            totalCalories += calories;
            updateTotalCaloriesDisplay();

            // 각 기록 항목 생성
            LinearLayout recordLayout = new LinearLayout(this);
            recordLayout.setOrientation(LinearLayout.HORIZONTAL);
            recordLayout.setPadding(8, 8, 8, 8);

            // 음식 이미지 추가
            ImageView foodImage = new ImageView(this);
            LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(100, 100);
            imageParams.setMargins(0, 0, 16, 0); // 오른쪽에 16dp 간격 추가
            foodImage.setLayoutParams(imageParams);
            foodImage.setImageResource(getFoodImageResource(foodName));
            recordLayout.addView(foodImage);

            // 음식 정보 텍스트 추가
            TextView recordView = new TextView(this);
            recordView.setText(foodName + " - " + calories + "kcal");
            recordView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1)); // 텍스트를 확장 가능하게
            recordView.setTextSize(18); // 글자 크기 키움
            recordView.setTextColor(0xFF431903); // 글자 색상 설정
            recordLayout.addView(recordView);

            // 삭제 버튼 추가
            Button deleteButton = new Button(this);
            deleteButton.setText("삭제");
            deleteButton.setTextSize(16); // 삭제 버튼 글자 크기 키움

            deleteButton.setTextColor(0xFFFFFFFF); // 삭제 버튼 글자 색상 흰색으로 설정
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteRecord(recordId, calories);
                    recordContainer.removeView(recordLayout); // UI에서 삭제
                }
            });
            recordLayout.addView(deleteButton);

            // 컨테이너에 추가
            recordContainer.addView(recordLayout);
        }
        cursor.close();
    }

    private void deleteRecord(int recordId, int calories) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("calorie_records", "id = ?", new String[]{String.valueOf(recordId)});
        totalCalories -= calories; // 총 칼로리 감소
        updateTotalCaloriesDisplay();
    }

    private void updateTotalCaloriesDisplay() {
        tvTotalCalories.setText("오늘 먹은 칼로리는 " + totalCalories + " kcal 입니다.");
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
}
