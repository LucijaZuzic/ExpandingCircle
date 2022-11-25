package com.example.expandingcircle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NumberPicker widthNumberPicker = findViewById(R.id.widthNumberPicker);
        widthNumberPicker.setMinValue(50);
        widthNumberPicker.setMaxValue(140);
        NumberPicker numberOfTargetsNumberPicker = findViewById(R.id.numberOfTargetsNumberPicker);
        numberOfTargetsNumberPicker.setMinValue(3);
        numberOfTargetsNumberPicker.setMaxValue(10);
        NumberPicker.Formatter formatter = value -> {
            int temp = value * 2;
            return "" + temp;
        };
        numberOfTargetsNumberPicker.setFormatter(formatter);
        EditText username = findViewById(R.id.username);
        Button start = findViewById(R.id.start);
        start.setOnClickListener((e) -> {
            Intent i = new Intent(this.getApplicationContext(), CircleActivity.class);
            i.putExtra("username", username.getText().toString());
            i.putExtra("speed", 100.0);
            i.putExtra("level_up", 0.0);
            i.putExtra("width", (float) widthNumberPicker.getValue());
            i.putExtra("nodes", numberOfTargetsNumberPicker.getValue() * 2);
            i.putExtra("start_node", 0);
            startActivity(i);
        });
        Button results = findViewById(R.id.results);
        results.setOnClickListener((e) -> {
            Intent i = new Intent(this.getApplicationContext(), Results.class);
            startActivity(i);
        });
    }
}