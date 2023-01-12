package com.example.expandingcircle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.opencsv.CSVWriter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public class Results extends AppCompatActivity {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final List<Throughput> throughputValues = new ArrayList<>();
    private final List<String> usernameList = new ArrayList<>();
    private final List<String> codeList = new ArrayList<>();
    private final List<Integer> nodesList = new ArrayList<>();
    private final List<Integer> widthMinList = new ArrayList<>();
    private final List<Integer> widthMaxList = new ArrayList<>();
    private final List<Integer> speedList = new ArrayList<>();
    private final List<Boolean> expandList = new ArrayList<>();
    private final Map<Throughput, String> thpUsernames = new HashMap<>();
    private final Map<Throughput, String> thpCodes = new HashMap<>();
    private final Map<Throughput, Integer> thpNodes = new HashMap<>();
    private final Map<Throughput, Integer> thpWidthMin = new HashMap<>();
    private final Map<Throughput, Integer> thpWidthMax = new HashMap<>();
    private final Map<Throughput, Integer> thpSpeed = new HashMap<>();
    private final Map<Throughput, Boolean> thpExpand = new HashMap<>();
    private final Comparator<Throughput> nodesCmp = Comparator.comparing(thpNodes::get);
    private final Comparator<Throughput> speedCmp = Comparator.comparing(thpSpeed::get);
    private final Comparator<Throughput> codeCmp = Comparator.comparing(thpCodes::get);
    private final Comparator<Throughput> usernameCmp = Comparator.comparing(thpUsernames::get);
    private final Comparator<Throughput> widthCmp = Comparator.comparing(thp -> (Math.round(thp.getW())));
    private final Comparator<Throughput> finalCmp = speedCmp.thenComparing(nodesCmp).thenComparing(codeCmp).thenComparing(usernameCmp).thenComparing(widthCmp);
    private final Set<Throughput> throughputSelected = new TreeSet<>(finalCmp);
    private CheckBox checkBoxNodesAndSpeed, checkBoxNodes, checkBoxSpeed;
    private NumberPicker numberOfTargetsNumberPicker, speedNumberPicker;
    private TextView noResults;
    private RecyclerView recyclerView;

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
            return "" + temp + " px/s";
        };
        speedNumberPicker.setFormatter(formatter2);
        checkBoxNodesAndSpeed = findViewById(R.id.checkBoxNodesAndSpeed);
        checkBoxNodes = findViewById(R.id.checkBoxNodes);
        checkBoxSpeed = findViewById(R.id.checkBoxSpeed);
        checkBoxNodes.setOnCheckedChangeListener((v, e) -> {
            if (checkBoxNodes.isChecked()) {
                checkBoxNodesAndSpeed.setChecked(false);
                checkBoxSpeed.setChecked(false);
            } else {
                if (!checkBoxNodesAndSpeed.isChecked() && !checkBoxSpeed.isChecked()) {
                    checkBoxSpeed.setChecked(true);
                }
            }
        });
        checkBoxNodesAndSpeed.setOnCheckedChangeListener((v, e) -> {
            if (checkBoxNodesAndSpeed.isChecked()) {
                checkBoxNodes.setChecked(false);
                checkBoxSpeed.setChecked(false);
            } else {
                if (!checkBoxNodes.isChecked() && !checkBoxSpeed.isChecked()) {
                    checkBoxSpeed.setChecked(true);
                }
            }
        });
        checkBoxSpeed.setOnCheckedChangeListener((v, e) -> {
            if (checkBoxSpeed.isChecked()) {
                checkBoxNodes.setChecked(false);
                checkBoxNodesAndSpeed.setChecked(false);
            } else {
                if (!checkBoxNodes.isChecked() && !checkBoxNodesAndSpeed.isChecked()) {
                    checkBoxNodes.setChecked(true);
                }
            }
        });
        noResults = findViewById(R.id.noResults);
        ImageButton back = findViewById(R.id.back);
        back.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        });
        ImageButton startSearch = findViewById(R.id.startSearch);
        startSearch.setOnClickListener(v -> get(false));
        ImageButton save = findViewById(R.id.save);
        save.setOnClickListener(v -> get(true));
        recyclerView = findViewById(R.id.results);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    // Calculate the mean of the values in a Double List.
    private float mean(List<Double> n)
    {
        float mean = 0.0f;
        for (Double f : n)
            mean += f;
        return mean / n.size();
    }

    public void get(boolean saveEnable) {
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
        thpExpand.clear();

        usernameList.clear();
        codeList.clear();
        nodesList.clear();
        widthMinList.clear();
        widthMaxList.clear();
        speedList.clear();
        expandList.clear();

        db.collection("tests")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.isSuccessful()) {
                            if (task.getResult().size() > 0) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Map<String, Object> data = document.getData();
                                    Integer nodes = Integer.parseInt(Objects.requireNonNull(data.get("nodes")).toString().split("/[,.\\s]/")[0]);
                                    Integer widthMin = ((Float) Float.parseFloat(Objects.requireNonNull(data.get("minWidth")).toString().split("/[,.\\s]/")[0])).intValue();
                                    Integer widthMax = ((Float) Float.parseFloat(Objects.requireNonNull(data.get("maxWidth")).toString().split("/[,.\\s]/")[0])).intValue();
                                    Integer speed = ((Float) Float.parseFloat(Objects.requireNonNull(data.get("speed")).toString().split("/[,.\\s]/")[0])).intValue();
                                    String code = Objects.requireNonNull(data.get("code")).toString();
                                    String username = Objects.requireNonNull(data.get("username")).toString();
                                    float amplitude = Float.parseFloat(Objects.requireNonNull(data.get("amplitude")).toString());
                                    Boolean expand = (Boolean) Objects.requireNonNull(data.get("expand"));
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
                                        thpExpand.put(thp, expand);
                                        throughputSelected.add(thp);
                                    }
                                }
                            }
                        }

                        List<String[]> data = new ArrayList<String[]>();
                        data.add(new String[] {
                                "Username",
                                "Code",
                                "Thp",
                                "Number Of Targets",
                                "A",
                                "Ae",
                                "Avg. W",
                                "We",
                                "Error Rate",
                                "Misses",
                                "Number Of Trials",
                                "MT",
                                "ID",
                                "IDe",
                                "X",
                                "SDx",
                                "Min. W",
                                "Max. W",
                                "Speed",
                                "Expand"});
                        for (Throughput thp : throughputSelected) {
                            data.add(new String[] {
                                    thpUsernames.get(thp),
                                    thpCodes.get(thp),
                                    ((Float) thp.getThroughput()).toString(),
                                    ((Integer) thpNodes.get(thp)).toString(),
                                    ((Float) thp.getA()).toString(),
                                    ((Float) thp.getAe()).toString(),
                                    ((Float) thp.getW()).toString(),
                                    ((Float) thp.getWe()).toString(),
                                    ((Float) thp.getErrorRate()).toString(),
                                    ((Integer) thp.getMisses()).toString(),
                                    ((Integer) thp.getNumberOfTrials()).toString(),
                                    ((Float) thp.getMT()).toString(),
                                    ((Float) thp.getID()).toString(),
                                    ((Float) thp.getIDe()).toString(),
                                    ((Float) thp.getX()).toString(),
                                    ((Float) thp.getSDx()).toString(),
                                    ((Integer) thpWidthMin.get(thp)).toString(),
                                    ((Integer) thpWidthMax.get(thp)).toString(),
                                    ((Integer) thpSpeed.get(thp)).toString(),
                                    ((Boolean) thpExpand.get(thp)).toString()});
                            throughputValues.add(thp);
                            usernameList.add(thpUsernames.get(thp));
                            codeList.add(thpCodes.get(thp));
                            nodesList.add(thpNodes.get(thp));
                            widthMinList.add(thpWidthMin.get(thp));
                            widthMaxList.add(thpWidthMax.get(thp));
                            speedList.add(thpSpeed.get(thp));
                            expandList.add(thpExpand.get(thp));
                        }
                        if (saveEnable) {
                            saveFile(data);
                        }
                        CustomAdapter customAdapter = new CustomAdapter(expandList, throughputValues, nodesList, usernameList, codeList, widthMinList, widthMaxList, speedList);
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

    private void saveFile(List<String[]> data) {
        String textToSend = "";
        for (String[] arr: data) {
            int index = 0;
            for (String s: arr) {
                textToSend += s;
                index++;
                if (index == arr.length) {
                    textToSend += "\n";
                } else {
                    textToSend += ";";
                }
            }
        }
        Integer nodesToFind = numberOfTargetsNumberPicker.getValue() * 2 - 1;
        Integer speedToFind = speedNumberPicker.getValue() * 50;
        String fileName = "ExpandingCircle";
        if (checkBoxNodes.isChecked() || checkBoxNodesAndSpeed.isChecked()) {
            fileName += "_nodes_" + nodesToFind.toString();
        }
        if (checkBoxSpeed.isChecked() || checkBoxNodesAndSpeed.isChecked()) {
            fileName += "_speed_" + speedToFind.toString();
        }
        fileName += ".csv";

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"lzuzic49@gmail.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, fileName);
        i.putExtra(Intent.EXTRA_TEXT   , textToSend);
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
        /*
        Integer nodesToFind = numberOfTargetsNumberPicker.getValue() * 2 - 1;
        Integer speedToFind = speedNumberPicker.getValue() * 50;
        // String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("ExpandingCircle", Context.MODE_PRIVATE);
        String baseDir = directory.getAbsolutePath();
        String fileName = "ExpandingCircle";
        if (checkBoxNodes.isChecked() || checkBoxNodesAndSpeed.isChecked()) {
            fileName += "_nodes_" + nodesToFind.toString();
        }
        if (checkBoxSpeed.isChecked() || checkBoxNodesAndSpeed.isChecked()) {
            fileName += "_speed_" + speedToFind.toString();
        }
        fileName += ".csv";
        String filePath = baseDir + File.separator + fileName;
        File f = new File(filePath);
        CSVWriter writer;
        TextView error = findViewById(R.id.error);

        // File exist
        if(f.exists()&&!f.isDirectory())
        {
            FileWriter mFileWriter = null;
            try {
                mFileWriter = new FileWriter(filePath, true);
                writer = new CSVWriter(mFileWriter);
                writer.writeAll(data);
                Toast.makeText(this, filePath, Toast.LENGTH_SHORT).show();
                try {
                    writer.close();
                } catch (IOException e) {
                    Toast.makeText(this,  "1 " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } catch (IOException e) {
                Toast.makeText(this, "2 " + e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
        else
        {
            try {
                writer = new CSVWriter(new FileWriter(filePath));
                writer.writeAll(data);
                Toast.makeText(this, filePath, Toast.LENGTH_SHORT).show();
                try {
                    writer.close();
                } catch (IOException e) {
                    Toast.makeText(this, "3 " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } catch (IOException e) {
                error.setText(e.getMessage());
                Toast.makeText(this, "4 " + e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }*/
    }
}