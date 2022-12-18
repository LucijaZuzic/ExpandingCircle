package com.example.expandingcircle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;

public class Results extends AppCompatActivity {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final List<Throughput> throughputValues = new ArrayList<>();
    private final List<String> usernameList = new ArrayList<>();
    private final List<String> codeList = new ArrayList<>();
    private final List<Integer> nodesList = new ArrayList<>();
    private final List<Integer> widthMinList = new ArrayList<>();
    private final List<Integer> widthMaxList = new ArrayList<>();
    private final List<Integer> speedList = new ArrayList<>();
    private final Map<Throughput, String> thpUsernames = new HashMap<>();
    private final Map<Throughput, String> thpCodes = new HashMap<>();
    private final Map<Throughput, Integer> thpNodes = new HashMap<>();
    private final Map<Throughput, Integer> thpWidthMin = new HashMap<>();
    private final Map<Throughput, Integer> thpWidthMax = new HashMap<>();
    private final Map<Throughput, Integer> thpSpeed = new HashMap<>();
    private final Comparator<Throughput> nodesCmp = Comparator.comparing(thpNodes::get);
    private final Comparator<Throughput> speedCmp = Comparator.comparing(thpSpeed::get);
    private final Comparator<Throughput> codeCmp = Comparator.comparing(thpCodes::get);
    private final Comparator<Throughput> usernameCmp = Comparator.comparing(thpUsernames::get);
    private final Comparator<Throughput> widthCmp = Comparator.comparing(thp -> (Math.round(thp.getW())));
    private final Comparator<Throughput> finalCmp = speedCmp.thenComparing(nodesCmp).thenComparing(codeCmp).thenComparing(usernameCmp).thenComparing(widthCmp);
    private final Set<Throughput> throughputSelected = new TreeSet<>(finalCmp);
    private final List<PointF> from = new ArrayList<>();
    private final List<PointF> to = new ArrayList<>();
    private final List<PointF> select = new ArrayList<>();
    private final List<Float> mt = new ArrayList<>();
    private CheckBox checkBoxNodesAndSpeed, checkBoxNodes, checkBoxSpeed;
    private NumberPicker numberOfTargetsNumberPicker, speedNumberPicker;
    private TextView noResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        numberOfTargetsNumberPicker = findViewById(R.id.numberOfTargetsNumberPicker);
        numberOfTargetsNumberPicker.setMinValue(3);
        numberOfTargetsNumberPicker.setMaxValue(12);
        NumberPicker.Formatter formatter = value -> {
            int temp = value * 2 - 1;
            return "" + temp;
        };
        numberOfTargetsNumberPicker.setFormatter(formatter);
        speedNumberPicker = findViewById(R.id.speedNumberPicker);
        speedNumberPicker.setMinValue(1);
        speedNumberPicker.setMaxValue(3);
        NumberPicker.Formatter formatter2 = value -> {
            int temp = value * 50;
            return "" + temp;
        };
        speedNumberPicker.setFormatter(formatter2);
        checkBoxNodesAndSpeed = findViewById(R.id.checkBoxNodesAndSpeed);
        checkBoxNodes = findViewById(R.id.checkBoxNodes);
        checkBoxSpeed = findViewById(R.id.checkBoxSpeed);
        checkBoxNodes.setOnCheckedChangeListener((v, e) -> {
            if (checkBoxNodes.isChecked()) {
                checkBoxNodesAndSpeed.setChecked(false);
                checkBoxSpeed.setChecked(false);
            }
        });
        checkBoxNodesAndSpeed.setOnCheckedChangeListener((v, e) -> {
            if (checkBoxNodesAndSpeed.isChecked()) {
                checkBoxNodes.setChecked(false);
                checkBoxSpeed.setChecked(false);
            }
        });
        checkBoxSpeed.setOnCheckedChangeListener((v, e) -> {
            if (checkBoxSpeed.isChecked()) {
                checkBoxNodes.setChecked(false);
                checkBoxNodesAndSpeed.setChecked(false);
            }
        });
        noResults = findViewById(R.id.noResults);
        ImageButton back = findViewById(R.id.back);
        back.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        });
        ImageButton startSearch = findViewById(R.id.startSearch);
        startSearch.setOnClickListener(v -> get());
        get();
    }

    // Calculate the mean of the values in a Double List.
    private float mean(List<Double> n)
    {
        float mean = 0.0f;
        for (Double f : n)
            mean += f;
        return mean / n.size();
    }

    public void get() {
        Integer nodesToFind = numberOfTargetsNumberPicker.getValue() * 2 - 1;
        Integer speedToFind = speedNumberPicker.getValue() * 50;

        throughputValues.clear();
        throughputSelected.clear();

        thpUsernames.clear();
        thpNodes.clear();
        thpCodes.clear();
        thpWidthMin.clear();
        thpWidthMax.clear();
        thpSpeed.clear();

        usernameList.clear();
        codeList.clear();
        nodesList.clear();
        widthMinList.clear();
        widthMaxList.clear();
        speedList.clear();

        db.collection("tests")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.isSuccessful()) {
                            if (task.getResult().size() > 0) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Map<String, Object> data = document.getData();
                                    Integer nodes = Integer.parseInt(Objects.requireNonNull(data.get("nodes")).toString().split("/[,.\\s]/")[0]);
                                    Integer widthMin = ((Float) Float.parseFloat(Objects.requireNonNull(data.get("width")).toString().split("/[,.\\s]/")[0])).intValue();
                                    Integer widthMax = ((Float) Float.parseFloat(Objects.requireNonNull(data.get("maxWidth")).toString().split("/[,.\\s]/")[0])).intValue();
                                    Integer speed = ((Float) Float.parseFloat(Objects.requireNonNull(data.get("speed")).toString().split("/[,.\\s]/")[0])).intValue();
                                    String code = Objects.requireNonNull(data.get("code")).toString();
                                    String username = Objects.requireNonNull(data.get("username")).toString();
                                    float amplitude = Float.parseFloat(Objects.requireNonNull(data.get("amplitude")).toString());
                                    List<Double> widths = (List<Double>) data.get("currentWidths");
                                    List<HashMap> from = (List<HashMap>) data.get("from");
                                    List<HashMap> to = (List<HashMap>) data.get("to");
                                    List<HashMap> select = (List<HashMap>) data.get("select");
                                    List<Double> mt = (List<Double>) data.get("mt");
                                    if (from != null && to != null && select != null && widths != null && mt != null) {
                                        PointF[] fromArray = new PointF[widths.size()];
                                        PointF[] toArray = new PointF[widths.size()];
                                        PointF[] selectArray = new PointF[widths.size()];
                                        float[] mtArray = new float[widths.size()];
                                        for (int i = 0; i < widths.size(); i++) {
                                            if (from.get(i) != null && to.get(i) != null && select.get(i) != null) {
                                                fromArray[i] = new PointF(((Double) Objects.requireNonNull(from.get(i).get("x"))).floatValue(), ((Double) Objects.requireNonNull(from.get(i).get("y"))).floatValue());
                                                toArray[i] = new PointF(((Double) Objects.requireNonNull(to.get(i).get("x"))).floatValue(), ((Double) Objects.requireNonNull(to.get(i).get("y"))).floatValue());
                                                selectArray[i] = new PointF(((Double) Objects.requireNonNull(select.get(i).get("x"))).floatValue(), ((Double) Objects.requireNonNull(select.get(i).get("y"))).floatValue());
                                                mtArray[i] = mt.get(i).floatValue();
                                            }
                                        }
                                        Throughput thp = new Throughput(code,
                                                amplitude,
                                                mean(widths),
                                                Throughput.ONE_DIMENSIONAL,
                                                Throughput.DISCRETE,
                                                fromArray,
                                                toArray,
                                                selectArray,
                                                mtArray);
                                        if (checkBoxNodes.isChecked() || checkBoxNodesAndSpeed.isChecked()) {
                                            if (!nodesToFind.equals(nodes)) {
                                                continue;
                                            }
                                        }
                                        if (checkBoxSpeed.isChecked() || checkBoxNodesAndSpeed.isChecked()) {
                                            if (!speedToFind.equals(speed)) {
                                                continue;
                                            }
                                        }
                                        thpCodes.put(thp, code);
                                        thpUsernames.put(thp, username);
                                        thpNodes.put(thp, nodes);
                                        thpWidthMin.put(thp, widthMin);
                                        thpWidthMax.put(thp, widthMax);
                                        thpSpeed.put(thp, speed);
                                        throughputSelected.add(thp);
                                    }
                                }
                            }
                        }
                        for (Throughput thp : throughputSelected) {
                            throughputValues.add(thp);
                            usernameList.add(thpUsernames.get(thp));
                            codeList.add(thpCodes.get(thp));
                            nodesList.add(thpNodes.get(thp));
                            widthMinList.add(thpWidthMin.get(thp));
                            widthMaxList.add(thpWidthMax.get(thp));
                            speedList.add(thpSpeed.get(thp));
                        }
                        CustomAdapter customAdapter = new CustomAdapter(throughputValues, nodesList, usernameList, codeList, widthMinList, widthMaxList, speedList);
                        RecyclerView recyclerView = findViewById(R.id.results);
                        recyclerView.setLayoutManager(new LinearLayoutManager(this));
                        recyclerView.setAdapter(customAdapter);
                        if (Objects.requireNonNull(recyclerView.getAdapter()).getItemCount() != 0) {
                            recyclerView.setVisibility(View.VISIBLE);
                            noResults.setVisibility(View.GONE);
                        } else {
                            recyclerView.setVisibility(View.GONE);
                            noResults.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }
}