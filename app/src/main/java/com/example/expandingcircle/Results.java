package com.example.expandingcircle;

import static com.example.expandingcircle.MyEntry.fetchAndGet;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.slider.Slider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

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
    private final List<MyEntry> throughputEntries = new ArrayList<>();
    private final List<Throughput> throughputValues = new ArrayList<>();
    private final List<String> usernameList = new ArrayList<>();
    private final List<String> codeList = new ArrayList<>();
    private final List<Integer> nodesList = new ArrayList<>();
    private final List<Integer> widthMinList = new ArrayList<>();
    private final List<Integer> widthMaxList = new ArrayList<>();
    private final List<Integer> speedList = new ArrayList<>();
    private final List<Boolean> expandList = new ArrayList<>();
    private final List<Integer> errorList = new ArrayList<>();
    private final List<List<Double>> speedsArrayList = new ArrayList<>();
    private final List<List<Integer>> nodesArrayList = new ArrayList<>();
    private final Map<Throughput, MyEntry> thpEntry = new HashMap<>();
    private final Map<Throughput, String> thpUsernames = new HashMap<>();
    private final Map<Throughput, String> thpCodes = new HashMap<>();
    private final Map<Throughput, Integer> thpNodes = new HashMap<>();
    private final Map<Throughput, Integer> thpWidthMin = new HashMap<>();
    private final Map<Throughput, Integer> thpWidthMax = new HashMap<>();
    private final Map<Throughput, Integer> thpSpeed = new HashMap<>();
    private final Map<Throughput, Boolean> thpExpand = new HashMap<>();
    private final Map<Throughput, Integer> thpOtherError = new HashMap<>();
    private final Map<Throughput, List<Integer>> thpNodesArray = new HashMap<>();
    private final Map<Throughput, List<Double>> thpSpeedsArray = new HashMap<>();
    private final Comparator<Throughput> nodesCmp = Comparator.comparing(thpNodes::get);
    private final Comparator<Throughput> speedCmp = Comparator.comparing(thpSpeed::get);
    private final Comparator<Throughput> codeCmp = Comparator.comparing(thpCodes::get);
    private final Comparator<Throughput> usernameCmp = Comparator.comparing(thpUsernames::get);
    private final Comparator<Throughput> widthCmp = Comparator.comparing(thp -> (Math.round(thp.getW())));
    private final Comparator<Throughput> finalCmp = speedCmp.thenComparing(nodesCmp).thenComparing(usernameCmp).thenComparing(widthCmp).thenComparing(codeCmp);
    private final Set<Throughput> throughputSelected = new TreeSet<>(finalCmp);
    private CheckBox checkBoxNodesAndSpeed, checkBoxNodes, checkBoxSpeed, checkBoxUsername;
    private Slider numberOfTargetsSlider, speedSlider;
    private TextView noResults;
    private RecyclerView recyclerView;
    private EditText username;
    private Results results;

    private DialogInterface.OnClickListener removeUsernameDialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    MyEntry.deleteUsername(results, username.getText().toString());

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        numberOfTargetsSlider = findViewById(R.id.numberOfTargetsSlider);
        speedSlider = findViewById(R.id.speedSlider);
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
        startSearch.setOnClickListener(v -> fetchAndGet(this, false));
        ImageButton save = findViewById(R.id.save);
        save.setOnClickListener(v -> fetchAndGet(this, true));
        recyclerView = findViewById(R.id.results);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        checkBoxUsername = findViewById(R.id.checkBoxUsername);
        username = findViewById(R.id.username);
        Button delete_username = findViewById(R.id.delete_username);
        delete_username.setOnClickListener((e) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            results = this;
            builder.setMessage(this.getApplicationContext().getResources().getString(R.string.delete_username_dialog))
                    .setPositiveButton(this.getApplicationContext().getResources().getString(R.string.yes), removeUsernameDialogClickListener)
                    .setNegativeButton(this.getApplicationContext().getResources().getString(R.string.no), removeUsernameDialogClickListener).show();
        });
    }

    // Calculate the mean of the values in a Double List.
    private float mean(List<Float> n)
    {
        float mean = 0.0f;
        for (Float f : n)
            mean += f;
        return mean / n.size();
    }

    public void get(MyEntry.MyEntrySet myEntrySet, boolean saveEnable) {
        Integer nodesToFind = ((Float) numberOfTargetsSlider.getValue()).intValue();
        Integer speedToFind = ((Float) speedSlider.getValue()).intValue();
        String usernameToFind = username.getText().toString();

        throughputValues.clear();
        throughputSelected.clear();

        thpEntry.clear();
        thpUsernames.clear();
        thpNodes.clear();
        thpCodes.clear();
        thpWidthMin.clear();
        thpWidthMax.clear();
        thpSpeed.clear();
        thpExpand.clear();
        thpNodesArray.clear();
        thpSpeedsArray.clear();
        thpOtherError.clear();

        throughputEntries.clear();
        usernameList.clear();
        codeList.clear();
        nodesList.clear();
        widthMinList.clear();
        widthMaxList.clear();
        speedList.clear();
        expandList.clear();
        nodesArrayList.clear();
        speedsArrayList.clear();
        errorList.clear();

        for (MyEntry entry: myEntrySet.getMyEntrySet()) {
            List<Integer> nodes_list = new ArrayList<>();
            for (int i = 0; i < entry.node_array.length; i++) {
                nodes_list.add(entry.node_array[i]);
            }
            List<Double> speed_list = new ArrayList<>();
            for (int i = 0; i < entry.speed_array.length; i++) {
                speed_list.add( entry.speed_array[i]);
            }
            List<Float> widths =  entry.currentWidths;
            List<Boolean> errors = entry.error;
            int num_error = 0;
            for (Boolean b: errors) {
                if (b) {
                    num_error++;
                }
            }
            List<PointF> from = entry.from;
            List<PointF> to = entry.to;
            List<PointF> select = entry.select;
            List<Float> mt = entry.mt;
            if (from != null && to != null && select != null && widths != null && mt != null) {
                PointF[] fromArray = new PointF[widths.size()];
                PointF[] toArray = new PointF[widths.size()];
                PointF[] selectArray = new PointF[widths.size()];
                float[] mtArray = new float[widths.size()];
                for (int i = 0; i < widths.size(); i++) {
                    if (from.get(i) != null && to.get(i) != null && select.get(i) != null) {
                        fromArray[i] = from.get(i);
                        toArray[i] = to.get(i);
                        selectArray[i] = select.get(i);
                        mtArray[i] = mt.get(i);
                    }
                }
                Throughput thp = new Throughput(
                        entry.mId,
                        entry.amplitude,
                        mean(widths),
                        Throughput.TWO_DIMENSIONAL,
                        Throughput.DISCRETE,
                        fromArray,
                        toArray,
                        selectArray,
                        mtArray);
                if (checkBoxUsername.isChecked() && !usernameToFind.equals(entry.username)) {
                    continue;
                }
                if (checkBoxNodes.isChecked() || checkBoxNodesAndSpeed.isChecked()) {
                    if (!nodesToFind.equals(entry.nodes)) {
                        continue;
                    }
                }
                if (checkBoxSpeed.isChecked() || checkBoxNodesAndSpeed.isChecked()) {
                    if (!speedToFind.equals((int) entry.speed)) {
                        continue;
                    }
                }
                thpEntry.put(thp, entry);
                thpCodes.put(thp, entry.mId);
                thpUsernames.put(thp, entry.username);
                thpNodes.put(thp, entry.nodes);
                thpWidthMin.put(thp, (int) entry.minWidth);
                thpWidthMax.put(thp, (int) entry.maxWidth);
                thpSpeed.put(thp, (int) entry.speed);
                thpExpand.put(thp, entry.expand);
                thpNodesArray.put(thp, nodes_list);
                thpSpeedsArray.put(thp, speed_list);
                thpOtherError.put(thp, num_error);
                throughputSelected.add(thp);
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
                "Expand",
                "Drop Error Rate",
                "Drop Misses",
                "Speed List",
                "Nodes List"});
        for (Throughput thp : throughputSelected) {
            data.add(new String[] {
                    thpUsernames.get(thp),
                    thpCodes.get(thp),
                    ((Float) thp.getThroughput()).toString(),
                    thpNodes.get(thp).toString(),
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
                    thpWidthMin.get(thp).toString(),
                    thpWidthMax.get(thp).toString(),
                    thpSpeed.get(thp).toString(),
                    thpExpand.get(thp).toString(),
                    ((Float) ((float) thpOtherError.get(thp) / thpNodes.get(thp) * 100.0f)).toString(),
                    thpOtherError.get(thp).toString(),
                    thpSpeedsArray.get(thp).toString(),
                    thpNodesArray.get(thp).toString()});
            throughputEntries.add(thpEntry.get(thp));
            throughputValues.add(thp);
            usernameList.add(thpUsernames.get(thp));
            codeList.add(thpCodes.get(thp));
            nodesList.add(thpNodes.get(thp));
            widthMinList.add(thpWidthMin.get(thp));
            widthMaxList.add(thpWidthMax.get(thp));
            speedList.add(thpSpeed.get(thp));
            expandList.add(thpExpand.get(thp));
            nodesArrayList.add(thpNodesArray.get(thp));
            speedsArrayList.add(thpSpeedsArray.get(thp));
            errorList.add(thpOtherError.get(thp));
        }
        if (saveEnable) {
            saveFile(data);
        }
        CustomAdapter customAdapter = new CustomAdapter(throughputEntries, nodesArrayList, speedsArrayList, errorList, expandList, throughputValues, nodesList, usernameList, codeList, widthMinList, widthMaxList, speedList);
        recyclerView.setAdapter(customAdapter);
        if (Objects.requireNonNull(recyclerView.getAdapter()).getItemCount() != 0) {
            recyclerView.setVisibility(View.VISIBLE);
            noResults.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            noResults.setVisibility(View.VISIBLE);
        }
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
        Integer nodesToFind = ((Float) numberOfTargetsSlider.getValue()).intValue();
        Integer speedToFind = ((Float) speedSlider.getValue()).intValue();
        String fileName = "ExpandingCircle";
        if (checkBoxNodes.isChecked() || checkBoxNodesAndSpeed.isChecked()) {
            fileName += "_nodes_" + nodesToFind.toString();
        }
        if (checkBoxSpeed.isChecked() || checkBoxNodesAndSpeed.isChecked()) {
            fileName += "_speed_" + speedToFind.toString();
        }
        if (checkBoxUsername.isChecked()) {
            fileName += "_username_" + username.getText().toString();
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
    }
}