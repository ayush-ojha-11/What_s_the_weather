package com.example.weatherlayout;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView cityName = (TextView) findViewById(R.id.city);
        SearchView searchBtn = (SearchView) findViewById(R.id.searchBtn);
        searchBtn.setOnSearchClickListener((View.OnClickListener) v -> {
            cityName.setVisibility(View.GONE);
        });
        searchBtn.setOnCloseListener(() -> {
            cityName.setVisibility(View.VISIBLE);
            return false;
        });
    }



}
