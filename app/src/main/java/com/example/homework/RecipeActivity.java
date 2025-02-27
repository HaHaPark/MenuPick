package com.example.homework;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class RecipeActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> recipeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe);

        // DBHelper 초기화
        dbHelper = new DBHelper(this);

        // 뒤로가기 아이콘 참조
        ImageView imgBack = findViewById(R.id.img_back);

        // 뒤로가기 클릭 이벤트
        imgBack.setOnClickListener(v -> {
            // HomeActivity로 이동
            Intent intent = new Intent(RecipeActivity.this, HomeActivity.class);
            startActivity(intent);
            finish(); // 현재 액티비티 종료
        });

        // 검색창 참조
        EditText searchInput = findViewById(R.id.search_input);

        // 리스트뷰 참조
        ListView listView = findViewById(R.id.recipe_list);
        recipeList = new ArrayList<>();

        // 어댑터 설정
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, recipeList);
        listView.setAdapter(adapter);

        // 검색창 초기 상태로 리스트뷰 숨김
        listView.setVisibility(View.GONE);

        // 검색 입력 이벤트 처리
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    loadSearchResults(s.toString());
                    listView.setVisibility(View.VISIBLE); // 검색어 입력 시 리스트뷰 표시
                } else {
                    recipeList.clear(); // 검색어가 없으면 리스트 초기화
                    adapter.notifyDataSetChanged();
                    listView.setVisibility(View.GONE); // 리스트뷰 숨김
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // 리스트 아이템 클릭 이벤트
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedFood = adapter.getItem(position);
            showRecipeDetails(selectedFood);
        });
    }

    // 검색 결과를 로드하는 메서드
    private void loadSearchResults(String query) {
        recipeList.clear(); // 기존 데이터 초기화
        Cursor cursor = dbHelper.searchRecipes(query); // 검색 쿼리 실행
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                recipeList.add(name); // 검색 결과 추가
            } while (cursor.moveToNext());
            cursor.close();
        }
        adapter.notifyDataSetChanged(); // 어댑터 갱신
    }

    // 선택된 음식의 상세 정보를 보여주는 메서드
    private void showRecipeDetails(String foodName) {
        Cursor cursor = dbHelper.getAllRecipes();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                if (name.equals(foodName)) {
                    String ingredients = cursor.getString(cursor.getColumnIndex("ingredients"));
                    String recipe = cursor.getString(cursor.getColumnIndex("recipe"));

                    // 새 액티비티로 레시피 데이터 전달
                    Intent intent = new Intent(RecipeActivity.this, RecipeDetailActivity.class);
                    intent.putExtra("name", name);
                    intent.putExtra("ingredients", ingredients);
                    intent.putExtra("recipe", recipe);
                    startActivity(intent);
                    return;
                }
            } while (cursor.moveToNext());
            cursor.close();
        }

        Toast.makeText(this, "레시피를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
    }
}
