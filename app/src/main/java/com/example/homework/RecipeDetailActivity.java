package com.example.homework;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class RecipeDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        // TextView와 ImageView 참조
        TextView foodNameTextView = findViewById(R.id.food_name);
        TextView ingredientsTextView = findViewById(R.id.ingredients);
        TextView recipeTextView = findViewById(R.id.recipe);
        ImageView foodImageView = findViewById(R.id.food_image);
        ImageView imgBack = findViewById(R.id.img_back);

        // Intent로 전달된 데이터 가져오기
        String name = getIntent().getStringExtra("name");
        String ingredients = getIntent().getStringExtra("ingredients");
        String recipe = getIntent().getStringExtra("recipe");

        // TextView에 데이터 설정
        foodNameTextView.setText(name);
        foodNameTextView.setGravity(Gravity.CENTER_HORIZONTAL); // 텍스트 중앙 정렬
        ingredientsTextView.setText(ingredients);
        recipeTextView.setText(recipe);

        // 음식 이미지 설정
        int imageResource = getFoodImageResource(name);
        foodImageView.setImageResource(imageResource);

        // 뒤로가기 클릭 이벤트
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 현재 액티비티 종료
            }
        });
    }

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
