package com.example.expandingcircle;

import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.text.HtmlCompat;

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
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CircleActivity extends AppCompatActivity {
    private ExpandingCircle ec;
    public FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FloatingActionButton drag, center;
    private double speed = 50;
    private boolean expand;
    public long startTime;
    private int nodes = 5;
    private int start_node = 0;
    private int end_node = 0;
    private int speed_index = 0;
    private int nodes_index = 0;
    private String username;
    private final float circleRadius = 280;
    private float width = 100;
    private final float minWidth = 100;
    private final float maxWidth = 140;
    public static float amplitude;
    public static List<Float> mt = new ArrayList<>();
    public static List<Float> currentWidths = new ArrayList<>();
    public static List<Boolean> error = new ArrayList<>();
    public static List<PointF> from = new ArrayList<>(), to = new ArrayList<>(), select = new ArrayList<>();
    private ConstraintLayout drag_area;
    private TextView speedTextView, timeTextView, radiusTextView, nodeOrder, speedOrder;
    private LinearLayout banner, parent;
    private View array[] = new View[nodes];

    public void nextLevel() {
        Intent i = new Intent(this.getApplicationContext(), CircleActivity.class);
        if (end_node == 0 && nodes_index == MainActivity.node_array.length - 1) {
            if (speed_index != MainActivity.speed_array.length - 1) {
                i.putExtra("speed_index", speed_index + 1);
            } else {
                sendData();
                finish();
                onBackPressed();
                return;
            }
        } else {
            i.putExtra("speed_index", speed_index);
        }
        i.putExtra("username", username);
        i.putExtra("width", width);
        if (end_node == 0) {
            if (nodes_index == MainActivity.node_array.length - 1) {
                i.putExtra("nodes_index", 0);
            } else {
                i.putExtra("nodes_index", nodes_index + 1);
            }
        } else {
            i.putExtra("nodes_index", nodes_index);
        }
        i.putExtra("start_node", end_node);
        i.putExtra("expand", expand);
        startActivity(i);
    }

    protected void sendData() {
        super.onStop();
        // Fetching Android ID and storing it into a constant
        String mId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        MyEntry newEntry = new MyEntry(nodes, circleRadius, maxWidth, speed, mId, username, amplitude, minWidth, expand, from, to, select, mt, error, currentWidths, MainActivity.node_array, MainActivity.speed_array);
        MyEntry.fetchAndSend(this.getApplicationContext(), newEntry);
        mt.clear();
        from.clear();
        to.clear();
        select.clear();
        error.clear();
        currentWidths.clear();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle);
        Button back = findViewById(R.id.back);
        back.setOnClickListener((e) -> {
            Intent i = new Intent(this.getApplicationContext(), MainActivity.class);
            startActivity(i);
        });

        drag_area = findViewById(R.id.drag_area);
        center = findViewById(R.id.center);
        banner = findViewById(R.id.banner);
        speedTextView = findViewById(R.id.speed);
        radiusTextView = findViewById(R.id.radius);
        timeTextView = findViewById(R.id.time);

        nodeOrder = findViewById(R.id.nodeOrder);
        speedOrder = findViewById(R.id.speedOrder);

        processIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        processIntent(intent);
    }

    private void processIntent(Intent i) {
        if (mt.size() == nodes) {
            sendData();
        }

        if (i.hasExtra("expand")) {
            expand = (boolean) i.getExtras().get("expand");
        }
        if (i.hasExtra("username")) {
            username = (String) i.getExtras().get("username");
        }
        if (i.hasExtra("width")) {
            width = i.getExtras().getFloat("width");
        }
        if (i.hasExtra("speed_index")) {
            speed_index = i.getExtras().getInt("speed_index");
        }
        speed = MainActivity.speed_array[speed_index];
        speedOrder.setText(HtmlCompat.fromHtml("<b>Speed Number: </b>" + (speed_index + 1) + " / " + MainActivity.original_speed_array.length, HtmlCompat.FROM_HTML_MODE_LEGACY));
        if (i.hasExtra("nodes_index")) {
            nodes_index = i.getExtras().getInt("nodes_index");
        }
        nodes = MainActivity.node_array[nodes_index];
        nodeOrder.setText(HtmlCompat.fromHtml("<b>Node Number: </b>" + (nodes_index + 1) + " / " + MainActivity.original_node_array.length, HtmlCompat.FROM_HTML_MODE_LEGACY));
        if (i.hasExtra("start_node")) {
            start_node = i.getExtras().getInt("start_node");
        }

        end_node = (int) (start_node + Math.ceil(nodes / 2.0)) % nodes;

        for (int node_number = 0; node_number < array.length; node_number++) {
            drag_area.removeView(array[node_number]);
        }

        array = new View[nodes];

        for (int node_number = 0; node_number < nodes; node_number++) {
            if (node_number == end_node) {
                continue;
            }
            array[node_number] = new FloatingActionButton(this);
            array[node_number].setLayoutParams(setPosition(node_number));
            if (node_number != start_node) {
                array[node_number].setEnabled(false);
            }
            drag_area.addView(array[node_number]);
        }

        LinearLayout ll = new LinearLayout(this);
        ll.setGravity(Gravity.CENTER);
        ll.setOrientation(LinearLayout.VERTICAL);
        array[end_node] = ll;
        array[end_node].setLayoutParams(setPosition(end_node));
        drag_area.addView(array[end_node]);

        drag = (FloatingActionButton) array[start_node];
        parent = (LinearLayout) array[end_node];

        if (ec != null) {
            ec.stop();
        }

        ec = new ExpandingCircle(this, (int) circleRadius, nodes, drag, parent, banner, speedTextView, radiusTextView, timeTextView, speed, 0.01, width, minWidth, maxWidth, 10, 10, 0.5, expand);

        drag.setOnLongClickListener( v -> {
            startTime = System.currentTimeMillis();
            ec.startMoving();
            // Instantiate the drag shadow builder.
            View.DragShadowBuilder myShadow = new View.DragShadowBuilder(drag);

            // Start the drag.
            v.startDragAndDrop(null,  // The data to be dragged
                    myShadow,  // The drag shadow builder
                    null,      // No need to use local data
                    0          // Flags (not currently used, set to 0)
            );

            // Indicate that the long-click was handled.
            return true;
        });

    }

    private ConstraintLayout.LayoutParams setPosition(int node_number) {
        ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(
                (int) maxWidth,
                (int) maxWidth);
        lp.circleConstraint = center.getId();
        lp.circleRadius = (int) circleRadius;
        lp.circleAngle = (float) (360.0 / nodes * node_number);
        return lp;
    }
}