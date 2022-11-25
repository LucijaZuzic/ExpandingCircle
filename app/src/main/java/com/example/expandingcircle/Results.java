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
    private final List<Integer> nodesList = new ArrayList<>();
    private final Map<Throughput, Integer> thpNodes = new HashMap<>();
    private final Comparator<Throughput> nodesCmp = Comparator.comparing(thpNodes::get);
    private final Comparator<Throughput> widthCmp = Comparator.comparing(thp -> ((Integer) Math.round(thp.getW())));
    private final Comparator<Throughput> finalCmp = nodesCmp.thenComparing(widthCmp);
    private final Set<Throughput> throughputAll = new HashSet<>();
    private final Set<Throughput> throughputSelected = new TreeSet<>(finalCmp);
    private final List<PointF> from = new ArrayList<>();
    private final List<PointF> to = new ArrayList<>();
    private final List<PointF> select = new ArrayList<>();
    private final List<Float> mt = new ArrayList<>();
    private CheckBox checkBoxNodesAndWidth, checkBoxNodes, checkBoxWidth;
    private NumberPicker numberOfTargetsNumberPicker, widthNumberPicker;
    private TextView noResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        numberOfTargetsNumberPicker = findViewById(R.id.numberOfTargetsNumberPicker);
        numberOfTargetsNumberPicker.setMinValue(3);
        numberOfTargetsNumberPicker.setMaxValue(10);
        NumberPicker.Formatter formatter = value -> {
            int temp = value * 2;
            return "" + temp;
        };
        numberOfTargetsNumberPicker.setFormatter(formatter);
        widthNumberPicker = findViewById(R.id.widthNumberPicker);
        widthNumberPicker.setMinValue(50);
        widthNumberPicker.setMaxValue(140);
        checkBoxNodesAndWidth = findViewById(R.id.checkBoxNodesAndWidth);
        checkBoxNodes = findViewById(R.id.checkBoxNodes);
        checkBoxWidth = findViewById(R.id.checkBoxWidth);
        checkBoxNodes.setOnCheckedChangeListener((v, e) -> {
            if (checkBoxNodes.isChecked()) {
                checkBoxNodesAndWidth.setChecked(false);
                checkBoxWidth.setChecked(false);
            }
        });
        checkBoxNodesAndWidth.setOnCheckedChangeListener((v, e) -> {
            if (checkBoxNodesAndWidth.isChecked()) {
                checkBoxNodes.setChecked(false);
                checkBoxWidth.setChecked(false);
            }
        });
        checkBoxWidth.setOnCheckedChangeListener((v, e) -> {
            if (checkBoxWidth.isChecked()) {
                checkBoxNodes.setChecked(false);
                checkBoxNodesAndWidth.setChecked(false);
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

    public void get() {
        Integer nodesToFind = numberOfTargetsNumberPicker.getValue() * 2;
        Integer widthToFind = widthNumberPicker.getValue();
        throughputValues.clear();
        throughputAll.clear();
        throughputSelected.clear();
        thpNodes.clear();
        nodesList.clear();
        db.collection("tests")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.isSuccessful()) {
                            if (task.getResult().size() > 0) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Map<String, Object> data = document.getData();
                                    Integer nodes = Integer.parseInt(Objects.requireNonNull(data.get("nodes")).toString().split("/[,.\\s]/")[0]);
                                    String code = Objects.requireNonNull(data.get("code")).toString();
                                    float amplitude = Float.parseFloat(Objects.requireNonNull(data.get("amplitude")).toString());
                                    List<Double> widths = (List<Double>) data.get("currentWidths");
                                    List<HashMap> from = (List<HashMap>) data.get("from");
                                    List<HashMap> to = (List<HashMap>) data.get("to");
                                    List<HashMap> select = (List<HashMap>) data.get("select");
                                    List<Double> mt = (List<Double>) data.get("mt");
                                    if (from != null && to != null && select != null && widths != null && mt != null) {
                                        for (int i = 0; i < widths.size(); i++) {
                                            if (from.get(i) != null && to.get(i) != null && select.get(i) != null) {
                                                PointF[] fromArray = new PointF[]{new PointF(((Double) Objects.requireNonNull(from.get(i).get("x"))).floatValue(), ((Double) Objects.requireNonNull(from.get(i).get("y"))).floatValue())};
                                                PointF[] toArray = new PointF[]{new PointF(((Double) Objects.requireNonNull(to.get(i).get("x"))).floatValue(), ((Double) Objects.requireNonNull(to.get(i).get("y"))).floatValue())};
                                                PointF[] selectArray = new PointF[]{new PointF(((Double) Objects.requireNonNull(select.get(i).get("x"))).floatValue(), ((Double) Objects.requireNonNull(select.get(i).get("y"))).floatValue())};
                                                float[] mtArray = new float[]{mt.get(i).floatValue()};
                                                Throughput thp = new Throughput(code,
                                                        amplitude,
                                                        widths.get(i).floatValue(),
                                                        Throughput.ONE_DIMENSIONAL,
                                                        Throughput.DISCRETE,
                                                        fromArray,
                                                        toArray,
                                                        selectArray,
                                                        mtArray);
                                                if (checkBoxNodes.isChecked() || checkBoxNodesAndWidth.isChecked()) {
                                                    if (!nodesToFind.equals(nodes)) {
                                                        continue;
                                                    }
                                                }
                                                Integer tmp_width = Math.round(thp.getW());
                                                if (checkBoxWidth.isChecked() || checkBoxNodesAndWidth.isChecked()) {
                                                    if (!widthToFind.equals(tmp_width)) {
                                                        continue;
                                                    }
                                                }
                                                thpNodes.put(thp, nodes);
                                                throughputAll.add(thp);
                                                throughputSelected.add(thp);
                                            }
                                        }
                                    }
                                }
                                for (Throughput thpReference : throughputSelected) {
                                    Predicate<Throughput> nodesPredicate = (thp) -> !thpNodes.get(thp).equals(thpNodes.get(thpReference));
                                    Predicate<Throughput> widthPredicate = (thp) -> !((Integer) Math.round(thp.getW())).equals((Integer) Math.round(thpReference.getW()));
                                    Set<Throughput> throughputMatching = new HashSet<>(throughputAll);
                                    throughputMatching.removeIf(nodesPredicate);
                                    throughputMatching.removeIf(widthPredicate);
                                    from.clear();
                                    to.clear();
                                    select.clear();
                                    mt.clear();
                                    for (Throughput thp : throughputMatching) {
                                        from.addAll(Arrays.asList(thp.getFrom()));
                                        to.addAll(Arrays.asList(thp.getTo()));
                                        select.addAll(Arrays.asList(thp.getSelect()));
                                        mt.addAll(Arrays.asList(thp.getMTArray()[0]));
                                    }
                                    PointF[] fromArray = new PointF[from.size()];
                                    PointF[] toArray = new PointF[to.size()];
                                    PointF[] selectArray = new PointF[select.size()];
                                    float[] mtArray = new float[mt.size()];
                                    for (int i = 0; i < mt.size(); i++) {
                                        fromArray[i] = from.get(i);
                                        toArray[i] = to.get(i);
                                        selectArray[i] = select.get(i);
                                        mtArray[i] = mt.get(i);
                                    }
                                    Throughput thpNew = new Throughput(null,
                                            thpReference.getA(),
                                            thpReference.getW(),
                                            Throughput.ONE_DIMENSIONAL,
                                            Throughput.DISCRETE,
                                            fromArray,
                                            toArray,
                                            selectArray,
                                            mtArray);
                                    throughputValues.add(thpNew);
                                    nodesList.add(thpNodes.get(thpReference));
                                }
                                CustomAdapter customAdapter = new CustomAdapter(throughputValues, nodesList);
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
                        }
                    }
                });
    }
}