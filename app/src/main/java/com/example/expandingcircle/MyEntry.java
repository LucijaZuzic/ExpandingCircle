package com.example.expandingcircle;

import android.Manifest;
import android.content.Context;
import android.graphics.PointF;
import android.provider.Settings;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class MyEntry {
    public static FirebaseFirestore db = FirebaseFirestore.getInstance();
    public double[] speed_array = {50.0d, 275.0d, 500.0d};
    public int[] node_array = {5, 7, 9, 11, 13, 15, 17, 19, 21, 23};
    public String mId;
    public double speed = 50;
    public boolean expand;
    public int nodes = 5;
    public String username;
    public float circleRadius = 280;
    public float minWidth = 100;
    public float maxWidth = 140;
    public float amplitude;
    public List<Float> mt = new ArrayList<>();
    public List<Float> currentWidths = new ArrayList<>();
    public List<Boolean> error = new ArrayList<>();
    public List<PointF> from = new ArrayList<>(), to = new ArrayList<>(), select = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyEntry myEntry = (MyEntry) o;
        return Double.compare(myEntry.speed, speed) == 0 && expand == myEntry.expand && nodes == myEntry.nodes && Float.compare(myEntry.circleRadius, circleRadius) == 0 && Float.compare(myEntry.minWidth, minWidth) == 0 && Float.compare(myEntry.maxWidth, maxWidth) == 0 && Float.compare(myEntry.amplitude, amplitude) == 0 && Arrays.equals(speed_array, myEntry.speed_array) && Arrays.equals(node_array, myEntry.node_array) && Objects.equals(mId, myEntry.mId) && Objects.equals(username, myEntry.username) && Objects.equals(mt, myEntry.mt) && Objects.equals(currentWidths, myEntry.currentWidths) && Objects.equals(error, myEntry.error) && Objects.equals(from, myEntry.from) && Objects.equals(to, myEntry.to) && Objects.equals(select, myEntry.select);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(mId, speed, expand, nodes, username, circleRadius, minWidth, maxWidth, amplitude, mt, currentWidths, error, from, to, select);
        result = 31 * result + Arrays.hashCode(speed_array);
        result = 31 * result + Arrays.hashCode(node_array);
        return result;
    }

    public MyEntry(int nodes, float circleRadius, float maxWidth, double speed, String mId, String username, float amplitude, float minWidth, boolean expand, List<PointF> from, List<PointF> to, List<PointF> select, List<Float> mt, List<Boolean> error, List<Float> currentWidths, int[] node_array, double[] speed_array) {
        this.nodes = nodes;
        this.circleRadius = circleRadius;
        this.maxWidth = maxWidth;
        this.speed = speed;
        this.mId = mId;
        this.username = username;
        this.amplitude = amplitude;
        this.minWidth = minWidth;
        this.expand = expand;
        this.from = new ArrayList<>(from);
        this.to = new ArrayList<>(to);
        this.select = new ArrayList<>(select);
        this.mt = new ArrayList<>(mt);
        this.error = new ArrayList<>(error);
        this.currentWidths = new ArrayList<>(currentWidths);
        this.node_array = new int[node_array.length];
        for (int i = 0; i < node_array.length; i++) {
            this.node_array[i] = node_array[i];
        }
        this.speed_array = new double[speed_array.length];
        for (int i = 0; i < speed_array.length; i++) {
            this.speed_array[i] = speed_array[i];
        }
    }

    public Map<String, Object> toData() {
        Map<String, Object> data = new HashMap<>();
        data.put("nodes",nodes);
        data.put("circleRadius",circleRadius);
        data.put("maxWidth",maxWidth);
        data.put("speed",speed);
        data.put("code",mId);
        data.put("username",username);
        data.put("amplitude",amplitude);
        data.put("minWidth",minWidth);
        data.put("expand",expand);
        data.put("from",from);
        data.put("to",to);
        data.put("select",select);
        data.put("mt",mt);
        data.put("error",error);
        data.put("currentWidths",currentWidths);
        List<Integer> myNodes = new ArrayList<>();
        List<Double> mySpeeds = new ArrayList<>();
        for (int i = 0; i < node_array.length; i++) {
            myNodes.add(node_array[i]);
        }
        for (int i = 0; i < speed_array.length; i++) {
            mySpeeds.add(speed_array[i]);
        }
        data.put("node_array",myNodes);
        data.put("speed_array",mySpeeds);
        return data;
    }

    public MyEntry(Map<String, Object> data) {
        this.nodes = Integer.parseInt(Objects.requireNonNull(data.get("nodes")).toString().split("/[,.\\s]/")[0]);
        this.circleRadius = ((Float) Float.parseFloat(Objects.requireNonNull(data.get("circleRadius")).toString().split("/[,.\\s]/")[0])).intValue();
        this.minWidth = ((Float) Float.parseFloat(Objects.requireNonNull(data.get("minWidth")).toString().split("/[,.\\s]/")[0])).intValue();
        this.maxWidth = ((Float) Float.parseFloat(Objects.requireNonNull(data.get("maxWidth")).toString().split("/[,.\\s]/")[0])).intValue();
        this.speed = ((Float) Float.parseFloat(Objects.requireNonNull(data.get("speed")).toString().split("/[,.\\s]/")[0])).intValue();
        this.mId = Objects.requireNonNull(data.get("code")).toString();
        this.username = Objects.requireNonNull(data.get("username")).toString();
        this.amplitude = Float.parseFloat(Objects.requireNonNull(data.get("amplitude")).toString());
        this.expand = (Boolean) Objects.requireNonNull(data.get("expand"));
        List<Long> nodes_list = new ArrayList<>(Objects.requireNonNull((List<Long>) data.get("node_array")));
        this.node_array = new int[nodes_list.size()];
        for (int i = 0; i < nodes_list.size(); i++) {
            node_array[i] = Math.toIntExact(nodes_list.get(i));
        }
        List<Double> speed_list = new ArrayList<>(Objects.requireNonNull((List<Double>) data.get("speed_array")));
        this.speed_array = new double[speed_list.size()];
        for (int i = 0; i < speed_list.size(); i++) {
            speed_array[i] = speed_list.get(i);
        }
        this.error = Objects.requireNonNull((List<Boolean>) data.get("error"));
        List<Double> widths =  new ArrayList<>(Objects.requireNonNull((List<Double>) data.get("currentWidths")));
        List<HashMap> from = new ArrayList<>(Objects.requireNonNull((List<HashMap>) data.get("from")));
        List<HashMap> to = new ArrayList<>(Objects.requireNonNull((List<HashMap>) data.get("to")));
        List<HashMap> select = new ArrayList<>(Objects.requireNonNull((List<HashMap>) data.get("select")));
        List<Double> mt = new ArrayList<>(Objects.requireNonNull((List<Double>) data.get("mt")));
        if (from != null && to != null && select != null && widths != null && mt != null) {
            this.from.clear();
            this.to.clear();
            this.select.clear();
            this.mt.clear();
            this.currentWidths.clear();
            for (int i = 0; i < widths.size(); i++) {
                if (from.get(i) != null && to.get(i) != null && select.get(i) != null) {
                    this.from.add(new PointF(((Double) Objects.requireNonNull(from.get(i).get("x"))).floatValue(), ((Double) Objects.requireNonNull(from.get(i).get("y"))).floatValue()));
                    this.to.add(new PointF(((Double) Objects.requireNonNull(to.get(i).get("x"))).floatValue(), ((Double) Objects.requireNonNull(to.get(i).get("y"))).floatValue()));
                    this.select.add(new PointF(((Double) Objects.requireNonNull(select.get(i).get("x"))).floatValue(), ((Double) Objects.requireNonNull(select.get(i).get("y"))).floatValue()));
                    this.mt.add(mt.get(i).floatValue());
                    this.currentWidths.add(widths.get(i).floatValue());
                }
            }
        }

    }


    private static String read(Context context, String fileName) {
        try {
            FileInputStream fis = context.openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (FileNotFoundException fileNotFound) {
            return null;
        } catch (IOException ioException) {
            return null;
        }
    }

    public static boolean create(Context context, String fileName, String fileString){
        try {
            FileOutputStream fos = context.openFileOutput(fileName,Context.MODE_PRIVATE);
            if (fileString != null) {
                fos.write(fileString.getBytes());
            }
            fos.close();
            return true;
        } catch (FileNotFoundException fileNotFound) {
            return false;
        } catch (IOException ioException) {
            return false;
        }
    }

    public static boolean isFilePresent(Context context, String fileName) {
        String path = context.getFilesDir().getAbsolutePath() + "/" + fileName;
        File file = new File(path);
        return file.exists();
    }

    public static class MyEntrySet {
        Set<MyEntry> myEntrySet;

        MyEntrySet() {
            myEntrySet = new HashSet<>();
        }

        public Set<MyEntry> getMyEntrySet() {
            return myEntrySet;
        }

        public void add(MyEntry myEntry) {
            myEntrySet.add(myEntry);
        }
    }

    public static void storeToFile(Context context, MyEntrySet myEntrySet, MyEntrySet fromDB) {
        Gson gson = new GsonBuilder().create();
        for (MyEntry myFileEntry: myEntrySet.getMyEntrySet()) {
            if (!fromDB.getMyEntrySet().contains(myFileEntry)) {
                fromDB.add(myFileEntry);
                db.collection("tests")
                        .add(myFileEntry.toData());
            }
        }
        create(context, "storage.json", gson.toJson(myEntrySet));
    }

    public static void start(MainActivity mainActivity, MyEntrySet myEntrySet) {
        Gson gson = new GsonBuilder().create();
        create(mainActivity.getApplicationContext(), "storage.json", gson.toJson(myEntrySet));
        mainActivity.counterbalance(myEntrySet);
    }

    public static void getResults(Results results, MyEntrySet myEntrySet, boolean saveEnable) {
        Gson gson = new GsonBuilder().create();
        create(results.getApplicationContext(), "storage.json", gson.toJson(myEntrySet));
        results.get(myEntrySet, saveEnable);
    }

    public static void afterFileCheckFetchAndStart(MainActivity mainActivity) {
        Gson gson = new GsonBuilder().create();
        String JSONString = read(mainActivity.getApplicationContext(), "storage.json");
        MyEntrySet myEntrySet = gson.fromJson(JSONString, MyEntrySet.class);
        db.collection("tests")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().size() > 0) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> data = document.getData();
                                MyEntry newEntry = new MyEntry(data);
                                if (!myEntrySet.getMyEntrySet().contains(newEntry)) {
                                    myEntrySet.add(newEntry);
                                }
                            }
                            start(mainActivity, myEntrySet);
                        } else {
                            start(mainActivity, myEntrySet);
                        }
                    } else {
                        start(mainActivity, myEntrySet);
                    }
                });
    }

    public static void afterFileCheckFetchAndGet(Results results, boolean saveEnable) {
        Gson gson = new GsonBuilder().create();
        String JSONString = read(results.getApplicationContext(), "storage.json");
        MyEntrySet myEntrySet = gson.fromJson(JSONString, MyEntrySet.class);
        db.collection("tests")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().size() > 0) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> data = document.getData();
                                MyEntry newEntry = new MyEntry(data);
                                if (!myEntrySet.getMyEntrySet().contains(newEntry)) {
                                    myEntrySet.add(newEntry);
                                }
                            }
                            getResults(results, myEntrySet, saveEnable);
                        } else {
                            getResults(results, myEntrySet, saveEnable);
                        }
                    } else {
                        getResults(results, myEntrySet, saveEnable);
                    }
                });
    }

    public static void afterFileCheckFetchAndSend(Context context, MyEntry entryAdd) {
        Gson gson = new GsonBuilder().create();
        String JSONString = read(context, "storage.json");
        MyEntrySet fromDB = new MyEntrySet();
        MyEntrySet myEntrySet = gson.fromJson(JSONString, MyEntrySet.class);
        if (entryAdd != null) {
            myEntrySet.add(entryAdd);
        }
        db.collection("tests")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().size() > 0) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> data = document.getData();
                                MyEntry newEntry = new MyEntry(data);
                                fromDB.add(newEntry);
                                if (!myEntrySet.getMyEntrySet().contains(newEntry)) {
                                    myEntrySet.add(newEntry);
                                }
                            }
                            storeToFile(context, myEntrySet, fromDB);
                        } else {
                            storeToFile(context, myEntrySet, fromDB);
                        }
                    } else {
                        storeToFile(context, myEntrySet, fromDB);
                    }
                });
    }

    public static void afterFileCheckFetchDeleteEntry(Results results, MyEntry entry) {
        Gson gson = new GsonBuilder().create();
        String JSONString = read(results.getApplicationContext(), "storage.json");
        MyEntrySet fromDB = new MyEntrySet();
        MyEntrySet myEntrySet = gson.fromJson(JSONString, MyEntrySet.class);
        MyEntrySet myEntrySetNew = new MyEntrySet();
        for (MyEntry myFileEntry: myEntrySet.getMyEntrySet()) {
            if (!myFileEntry.equals(entry)) {
                myEntrySetNew.add(myFileEntry);
            }
        }
        db.collection("tests")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().size() > 0) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                MyEntry newEntry = new MyEntry(document.getData());
                                if (entry.equals(newEntry)) {
                                    db.collection("tests").document(document.getId())
                                            .delete();
                                } else {
                                    fromDB.add(newEntry);
                                }
                            }
                            storeToFile(results.getApplicationContext(), myEntrySetNew, fromDB);
                            fetchAndGet(results, false);
                        } else {
                            storeToFile(results.getApplicationContext(), myEntrySetNew, fromDB);
                            fetchAndGet(results, false);
                        }
                    } else {
                        storeToFile(results.getApplicationContext(), myEntrySetNew, fromDB);
                        fetchAndGet(results, false);
                    }
                });
    }

    public static void afterFileCheckFetchDeleteUsername(Context context, String username) {
        Gson gson = new GsonBuilder().create();
        String JSONString = read(context, "storage.json");
        MyEntrySet fromDB = new MyEntrySet();
        MyEntrySet myEntrySet = gson.fromJson(JSONString, MyEntrySet.class);
        MyEntrySet myEntrySetNew = new MyEntrySet();
        for (MyEntry myFileEntry: myEntrySet.getMyEntrySet()) {
            if (!myFileEntry.username.equals(username)) {
                myEntrySetNew.add(myFileEntry);
            }
        }
        db.collection("tests")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().size() > 0) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.get("username").equals(username)) {
                                    db.collection("tests").document(document.getId())
                                            .delete();
                                } else {
                                    fromDB.add(new MyEntry(document.getData()));
                                }
                            }
                            storeToFile(context, myEntrySetNew, fromDB);
                        } else {
                            storeToFile(context, myEntrySetNew, fromDB);
                        }
                    } else {
                        storeToFile(context, myEntrySetNew, fromDB);
                    }
                });
    }

    public static void afterFileCheckFetchDeleteUsername(Results results, String username) {
        Gson gson = new GsonBuilder().create();
        String JSONString = read(results.getApplicationContext(), "storage.json");
        MyEntrySet fromDB = new MyEntrySet();
        MyEntrySet myEntrySet = gson.fromJson(JSONString, MyEntrySet.class);
        MyEntrySet myEntrySetNew = new MyEntrySet();
        for (MyEntry myFileEntry: myEntrySet.getMyEntrySet()) {
            if (!myFileEntry.username.equals(username)) {
                myEntrySetNew.add(myFileEntry);
            }
        }
        db.collection("tests")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().size() > 0) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.get("username").equals(username)) {
                                    db.collection("tests").document(document.getId())
                                            .delete();
                                } else {
                                    fromDB.add(new MyEntry(document.getData()));
                                }
                            }
                            storeToFile(results.getApplicationContext(), myEntrySetNew, fromDB);
                            fetchAndGet(results, false);
                        } else {
                            storeToFile(results.getApplicationContext(), myEntrySetNew, fromDB);
                            fetchAndGet(results, false);
                        }
                    } else {
                        storeToFile(results.getApplicationContext(), myEntrySetNew, fromDB);
                        fetchAndGet(results, false);
                    }
                });
    }

    public static void fetchAndSend(Context context, MyEntry entryAdd) {
        boolean isFilePresent = isFilePresent(context, "storage.json");
        if (isFilePresent) {
            afterFileCheckFetchAndSend(context, entryAdd);
        } else {
            boolean isFileCreated = create(context, "storage.json", "{}");
            if(isFileCreated) {
                afterFileCheckFetchAndSend(context, entryAdd);
            } else {
                //show error or try again.
            }
        }
    }

    public static void fetchAndStart(MainActivity mainActivity) {
        boolean isFilePresent = isFilePresent(mainActivity.getApplicationContext(), "storage.json");
        if (isFilePresent) {
            afterFileCheckFetchAndStart(mainActivity);
        } else {
            boolean isFileCreated = create(mainActivity.getApplicationContext(), "storage.json", "{}");
            if(isFileCreated) {
                afterFileCheckFetchAndStart(mainActivity);
            } else {
                //show error or try again.
            }
        }
    }

    public static void fetchAndGet(Results results, boolean saveEnable) {
        boolean isFilePresent = isFilePresent(results.getApplicationContext(), "storage.json");
        if (isFilePresent) {
            afterFileCheckFetchAndGet(results, saveEnable);
        } else {
            boolean isFileCreated = create(results.getApplicationContext(), "storage.json", "{}");
            if(isFileCreated) {
                afterFileCheckFetchAndGet(results, saveEnable);
            } else {
                //show error or try again.
            }
        }
    }

    public static void deleteUsername(Context context, String username) {
        boolean isFilePresent = isFilePresent(context, "storage.json");
        if (isFilePresent) {
            afterFileCheckFetchDeleteUsername(context, username);
        } else {
            boolean isFileCreated = create(context, "storage.json", "{}");
            if(isFileCreated) {
                afterFileCheckFetchDeleteUsername(context, username);
            } else {
                //show error or try again.
            }
        }
    }

    public static void deleteUsername(Results results, String username) {
        boolean isFilePresent = isFilePresent(results.getApplicationContext(), "storage.json");
        if (isFilePresent) {
            afterFileCheckFetchDeleteUsername(results, username);
        } else {
            boolean isFileCreated = create(results.getApplicationContext(), "storage.json", "{}");
            if(isFileCreated) {
                afterFileCheckFetchDeleteUsername(results, username);
            } else {
                //show error or try again.
            }
        }
    }

    public static void deleteEntry(Results results, MyEntry entry) {
        boolean isFilePresent = isFilePresent(results.getApplicationContext(), "storage.json");
        if (isFilePresent) {
            afterFileCheckFetchDeleteEntry(results, entry);
        } else {
            boolean isFileCreated = create(results.getApplicationContext(), "storage.json", "{}");
            if(isFileCreated) {
                afterFileCheckFetchDeleteEntry(results, entry);
            } else {
                //show error or try again.
            }
        }
    }
}
