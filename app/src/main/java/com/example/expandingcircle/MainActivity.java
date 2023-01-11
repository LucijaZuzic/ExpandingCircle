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
        EditText username = findViewById(R.id.username);
        Button start = findViewById(R.id.start);
        start.setOnClickListener((e) -> {
            Intent i = new Intent(this.getApplicationContext(), CircleActivity.class);
            i.putExtra("username", username.getText().toString());
            i.putExtra("speed", 50.0);
            i.putExtra("level_up", 50.0);
            i.putExtra("width", 200.0f);
            i.putExtra("nodes", 5);
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