package com.example.expandingcircle;

import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CircleActivity extends AppCompatActivity {
    private ExpandingCircle ec;
    public FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FloatingActionButton drag, center;
    private double level_up = 50, speed = 50;
    public long startTime;
    private int nodes = 5;
    private int start_node = 0;
    private int end_node = 0;
    private String username;
    private final float circleRadius = 280;
    private float width = 100;
    private final float maxWidth = 140;
    public static float amplitude;
    public static List<Float> mt = new ArrayList<>();
    public static List<Float> currentWidths = new ArrayList<>();
    public static List<Boolean> error = new ArrayList<>();
    public static List<PointF> from = new ArrayList<>(), to = new ArrayList<>(), select = new ArrayList<>();
    private ConstraintLayout drag_area;
    private TextView speedTextView, timeTextView, radiusTextView;
    private LinearLayout banner, parent;
    private View array[] = new View[nodes];

    public void nextLevel() {
        Intent i = new Intent(this.getApplicationContext(), CircleActivity.class);
        if (end_node == 0 && nodes == 23) {
            if (speed < 150) {
                i.putExtra("speed", speed + level_up);
            } else {
                sendData();
                finish();
                onBackPressed();
                return;
            }
        } else {
            i.putExtra("speed", speed);
        }
        i.putExtra("username", username);
        i.putExtra("width", width);
        i.putExtra("level_up", level_up);
        if (end_node == 0) {
            if (nodes == 23) {
                i.putExtra("nodes", 5);
            } else {
                i.putExtra("nodes", nodes + 2);
            }
        } else {
            i.putExtra("nodes", nodes);
        }
        i.putExtra("start_node", end_node);
        startActivity(i);
    }

    protected void sendData() {
        super.onStop();
        // Fetching Android ID and storing it into a constant
        String mId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Map<String, Object> data = new HashMap<>();
        data.put("nodes", nodes);
        data.put("circleRadius", circleRadius);
        data.put("maxWidth", maxWidth);
        data.put("speed", speed);
        data.put("code", mId);
        data.put("username", username);
        data.put("amplitude", amplitude);
        data.put("width", width);
        data.put("from", from);
        data.put("to", to);
        data.put("select", select);
        data.put("mt", mt);
        data.put("error", error);
        data.put("currentWidths", currentWidths);
        db.collection("tests")
                .add(data);
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
            mt.clear();
            from.clear();
            to.clear();
            select.clear();
            error.clear();
            currentWidths.clear();
        }

        if (i.hasExtra("username")) {
            username = (String) i.getExtras().get("username");
        }
        if (i.hasExtra("width")) {
            width = i.getExtras().getFloat("width");
        }
        if (i.hasExtra("speed")) {
            speed = i.getExtras().getDouble("speed");
        }
        if (i.hasExtra("level_up")) {
            level_up = i.getExtras().getDouble("level_up");
        }
        if (i.hasExtra("nodes")) {
            nodes = i.getExtras().getInt("nodes");
        }
        if (i.hasExtra("start_node")) {
            start_node = i.getExtras().getInt("start_node");
        }

        end_node = (int) (start_node + Math.ceil(nodes / 2.0)) % nodes;

        for (int node_number = 0; node_number < array.length; node_number++) {
            drag_area.removeView(array[node_number]);
        }

        array = new View[nodes];

        for (int node_number = 0; node_number < nodes; node_number++) {
            if (node_number != end_node) {
                array[node_number] = new FloatingActionButton(this);
            } else {
                array[node_number] = new LinearLayout(this);
            }
            array[node_number].setLayoutParams(setPosition(node_number));
            if (node_number != start_node) {
                array[node_number].setEnabled(false);
            }
            drag_area.addView(array[node_number]);
        }

        drag = (FloatingActionButton) array[start_node];
        parent = (LinearLayout) array[end_node];

        if (ec != null) {
            ec.stop();
        }

        ec = new ExpandingCircle(this, drag, parent, banner, speedTextView, radiusTextView, timeTextView, speed, 0.01, width, width, maxWidth, 10, 10, 0.5, true);

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
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT);
        lp.circleConstraint = center.getId();
        lp.circleRadius = (int) circleRadius;
        lp.circleAngle = (float) (360.0 / nodes * node_number);
        return lp;
    }
}