package com.example.expandingcircle;

import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CircleActivity extends AppCompatActivity {
    public FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FloatingActionButton drag;
    private double level_up = 0, speed = 100;
    public long startTime;
    private int nodes = 5;
    private int start_node = 0;
    private int end_node;
    private String username;
    private final float circleRadius = 280;
    private float width = 50;
    private final float maxWidth = 140;
    public static float amplitude;
    public static List<Float> mt = new ArrayList<>();
    public static List<Float> currentWidths = new ArrayList<>();
    public static List<Boolean> error = new ArrayList<>();
    public static List<PointF> from = new ArrayList<>(), to = new ArrayList<>(), select = new ArrayList<>();

    public void nextLevel(boolean increase) {
        Intent i = new Intent(this.getApplicationContext(), CircleActivity.class);
        if (end_node == 0) {
            i.putExtra("speed", speed + level_up);
        } else {
            i.putExtra("speed", speed);
        }
        i.putExtra("username", username);
        i.putExtra("width", width);
        i.putExtra("level_up", level_up);
        i.putExtra("nodes", nodes);
        if (start_node + 1 >= nodes) {
            i.putExtra("start_node", 0);
        } else {
            i.putExtra("start_node", start_node + 1);
        }
        finish();
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
        Intent i = getIntent();
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

        if (mt.size() == nodes) {
            sendData();
            mt.clear();
            from.clear();
            to.clear();
            select.clear();
            error.clear();
            currentWidths.clear();
        }

        ConstraintLayout drag_area = findViewById(R.id.drag_area);
        FloatingActionButton center = findViewById(R.id.center);

        end_node = (int) (start_node + Math.floor(nodes / 2.0));

        if (end_node >= nodes) {
            end_node = (int) (start_node - Math.ceil(nodes / 2.0));
        }

        View[] array = new View[nodes];

        for (int node_number = 0; node_number < nodes; node_number++) {
            if (node_number != end_node) {
                array[node_number] = new FloatingActionButton(this);
            } else {
                array[node_number] = new LinearLayout(this);
            }
            ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT);
            lp.circleConstraint = center.getId();
            lp.circleRadius = (int) circleRadius;
            lp.circleAngle = (float) (360.0 / nodes * node_number);
            array[node_number].setLayoutParams(lp);
            array[node_number].setEnabled(false);
            drag_area.addView(array[node_number]);
        }

        drag = (FloatingActionButton) array[start_node];
        drag.setEnabled(true);
        LinearLayout parent = (LinearLayout) array[end_node];

        ExpandingCircle ec = new ExpandingCircle(this, drag, parent, findViewById(R.id.banner), findViewById(R.id.speed), findViewById(R.id.radius), findViewById(R.id.time), speed, 0.01, width, width, maxWidth, 10, 10, 0.5, true);

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
}