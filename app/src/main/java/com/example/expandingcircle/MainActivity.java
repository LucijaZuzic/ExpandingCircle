package com.example.expandingcircle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static final double[] original_speed_array = {50.0d, 150.0d, 250.0d};
    public static final int[] original_node_array = {5, 7, 9, 11, 13, 15, 17, 19, 21, 23};
    public static double[] speed_array = {50.0d, 150.0d, 250.0d};
    public static int[] node_array = {5, 7, 9, 11, 13, 15, 17, 19, 21, 23};
    private EditText username;

    @Override
    protected void onResume() {
        super.onResume();
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
            Toast.makeText(this, "No internet", Toast.LENGTH_SHORT);
        }
    }

    private Map<List<Integer>, Integer> possibleNodeArrays() {
        Map<List<Integer>, Integer> node_array_map = new HashMap<>();
        int [][] table = balancedLatinSquare(node_array.length);
        for (int r = 0; r < original_node_array.length; r++) {
            List<Integer> possibleNodeArray = new ArrayList<>();
            for (int c = 0; c < original_node_array.length; c++) {
                possibleNodeArray.add(original_node_array[table[r][c]]);
            }
            node_array_map.put(possibleNodeArray, 0);
        }
        return node_array_map;
    }

    private Map<List<Double>, Integer> possibleSpeedArrays() {
        Map<List<Double>, Integer> speed_array_map = new HashMap<>();
        for (int i = 0; i < original_speed_array.length; i++) {
            for (int j = 0; j < original_speed_array.length; j++) {
                if (i == j) {
                    continue;
                }
                List<Double> possibleSpeedArray = new ArrayList<>();
                possibleSpeedArray.add(original_speed_array[i]);
                possibleSpeedArray.add(original_speed_array[j]);
                possibleSpeedArray.add(original_speed_array[3 - i - j]);
                speed_array_map.put(possibleSpeedArray, 0);
            }
        }
        return speed_array_map;
    }

    public int[][] balancedLatinSquare(int size) {
        int [][] table = new int[size][size];
        table[0][0] = 0;
        int order = 1;
        for (int c = 1; c < size; c += 2) {
            table[0][c] = order;
            if (c + 1 < size) {
                table[0][c + 1] = size - order;
            }
            order++;
        }
        for (int c = 0; c < size; c++) {
            for (int r = 1; r < size; r++) {
                table[r][c] = (table[r - 1][c] + 1) % size;
            }
        }
        return table;
    }

    public int[] randomIndexes(int size) {
        Random randomNew = new Random();
        Set<Integer> randomNumbersSet = new HashSet<>();
        int [] randomNumbersArray = new int[size];
        for (int i = 0; i < size; i++) {
            int number = randomNew.nextInt(size);
            while (randomNumbersSet.contains(number)) {
                number = randomNew.nextInt(size);
            }
            randomNumbersSet.add(number);
            randomNumbersArray[i] = number;
        }
        return randomNumbersArray;
    }

    public void counterbalance() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
            Toast.makeText(this, "No internet", Toast.LENGTH_SHORT);
            return;
        }
        Map<List<Double>, Integer> speed_array_map = possibleSpeedArrays();
        Map<List<Integer>, Integer> node_array_map = possibleNodeArrays();
        db.collection("tests")
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (task.getResult().size() > 0) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> data = document.getData();
                            List<Double> speed_list = (List<Double>) data.get("speed_array");
                            if (speed_list != null) {
                                if (speed_array_map.containsKey(speed_list)) {
                                    speed_array_map.put(speed_list,speed_array_map.get(speed_list) + 1);
                                } else {
                                    speed_array_map.put(speed_list,1);
                                }
                            }
                            List<Long> long_nodes_list = (List<Long>) data.get("node_array");
                            if (long_nodes_list != null) {
                                List<Integer> nodes_list = new ArrayList<>();
                                for (int i = 0; i < long_nodes_list.size(); i++) {
                                    nodes_list.add(i, Math.toIntExact(long_nodes_list.get(i)));
                                }
                                if (node_array_map.containsKey(nodes_list)) {
                                    node_array_map.put(nodes_list,node_array_map.get(nodes_list) + 1);
                                } else {
                                    node_array_map.put(nodes_list,1);
                                }
                            }
                        }
                    }
                }
                Map.Entry<List<Double>, Integer> minSpeedArray = null;
                for (Map.Entry<List<Double>, Integer> entry : speed_array_map.entrySet()) {
                    if (minSpeedArray == null || minSpeedArray.getValue() > entry.getValue()) {
                        minSpeedArray = entry;
                    }
                }
                List<Double> new_speed_array = minSpeedArray.getKey();
                for (int i = 0; i < original_speed_array.length; i++) {
                    speed_array[i] = new_speed_array.get(i);
                }
                Map.Entry<List<Integer>, Integer> minNodeArray = null;
                for (Map.Entry<List<Integer>, Integer> entry : node_array_map.entrySet()) {
                    if (minNodeArray == null || minNodeArray.getValue() > entry.getValue()) {
                        minNodeArray = entry;
                    }
                }
                List<Integer> new_node_array = minNodeArray.getKey();
                for (int i = 0; i < original_node_array.length; i++) {
                    node_array[i] = new_node_array.get(i);
                }
                Intent i = new Intent(this.getApplicationContext(), CircleActivity.class);
                i.putExtra("username", username.getText().toString());
                i.putExtra("speed_index", 0);
                i.putExtra("width", 140.0f);
                i.putExtra("nodes_index", 0);
                i.putExtra("start_node", 0);
                i.putExtra("expand", false);
                startActivity(i);
            });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        username = findViewById(R.id.username);
        Button start = findViewById(R.id.start);
        start.setOnClickListener((e) -> {
            counterbalance();
        });
        Button results = findViewById(R.id.results);
        results.setOnClickListener((e) -> {
            Intent i = new Intent(this.getApplicationContext(), Results.class);
            startActivity(i);
        });
    }
}