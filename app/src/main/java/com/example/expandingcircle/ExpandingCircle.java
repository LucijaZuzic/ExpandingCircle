package com.example.expandingcircle;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.PointF;
import android.os.Handler;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;

public class ExpandingCircle extends View {

    private final double speed, update_interval, min_radius, max_radius, ring_radius, ring_edge_radius, curve;
    private double radius;
    private boolean expand;
    private int num_updates, nodes, circleRadius;
    private final LinearLayout banner, bg;
    private final CardView outer_edge_ring, ring, inner_edge_ring, fill;
    private final TextView speedTextView, radiusTextView, timeTextView;
    private PointF selected;
    private boolean stopped = false;

    public void stop() {
        stopped = true;
    }

    private Activity getActivity(View v) {
        Context context = v.getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity)context;
            }
            context = ((ContextWrapper)context).getBaseContext();
        }
        return null;
    }

    public void startMoving()  {
        final Handler handler = new Handler();
        final int delay = (int) (1000 * update_interval); // 1000 milliseconds == 1 second
        handler.postDelayed(new Runnable() {
            public void run() {
                if (!stopped) {
                    changeSize();
                    handler.postDelayed(this, delay);
                }
            }
        }, delay);
    }

    public ExpandingCircle(CircleActivity activity, int circleRadius, int nodes, View drag, ViewGroup parent, LinearLayout banner, TextView speedTextView, TextView radiusTextView, TextView timeTextView, double speed, double update_interval, double radius, double min_radius, double max_radius, double ring_radius, double ring_edge_radius, double curve, boolean expand) {
        super(parent.getContext());
        this.circleRadius = circleRadius;
        this.nodes = nodes;
        this.banner = banner;
        this.speedTextView = speedTextView;
        this.radiusTextView = radiusTextView;
        this.timeTextView = timeTextView;
        this.speed = speed;
        this.update_interval = update_interval;
        this.radius = radius;
        this.min_radius = min_radius;
        this.max_radius = max_radius;
        this.ring_radius = ring_radius;
        this.ring_edge_radius = ring_edge_radius;
        this.curve = curve;
        this.expand = expand;
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.moving_circle, parent);
        bg = (LinearLayout) parent.getChildAt(0);
        outer_edge_ring = (CardView) bg.getChildAt(0);
        ring = (CardView) outer_edge_ring.getChildAt(0);
        inner_edge_ring = (CardView) ring.getChildAt(0);
        fill = (CardView) inner_edge_ring.getChildAt(0);
        changeLayoutParams();
        // Set the drag event listener for the View.
        bg.setOnDragListener( (v, e) -> {
            if (stopped) {
                return true;
            }

            // Handles each of the expected events.
            switch(e.getAction()) {

                case DragEvent.ACTION_DRAG_STARTED:

                    int[] parentStart = new int[2];
                    parent.getLocationInWindow(parentStart);

                    selected = new PointF((float) parentStart[0] + e.getX(), (float) parentStart[1] + e.getY());

                    // As an example of what your application might do, applies a purple color tint
                    // to the View to indicate that it can accept data.
                    fill.setCardBackgroundColor(ContextCompat.getColor(activity.getApplicationContext(), R.color.purple_200));

                    // Invalidate the view to force a redraw in the new tint.
                    fill.invalidate();

                    // Returns true to indicate that the View can accept the dragged data.
                    return true;

                case DragEvent.ACTION_DRAG_ENTERED:

                    // Applies a teal tint to the View.
                    fill.setCardBackgroundColor(ContextCompat.getColor(activity.getApplicationContext(), R.color.teal_200));

                    // Invalidates the view to force a redraw in the new tint.
                    fill.invalidate();

                    // Returns true; the value is ignored.
                    return true;

                case DragEvent.ACTION_DRAG_LOCATION:

                    int[] parentLocation = new int[2];
                    parent.getLocationInWindow(parentLocation);

                    selected = new PointF((float) parentLocation[0] + e.getX(), (float) parentLocation[1] + e.getY());

                    // Ignore the event.
                    return true;

                case DragEvent.ACTION_DRAG_EXITED:

                    // Resets the color tint to white.
                    fill.setCardBackgroundColor(ContextCompat.getColor(activity.getApplicationContext(), R.color.white));

                    // Invalidates the view to force a redraw in the new tint.
                    fill.invalidate();

                    // Returns true; the value is ignored.
                    return true;

                case DragEvent.ACTION_DROP:

                    int[] parentPosition = new int[2];
                    parent.getLocationInWindow(parentPosition);

                    selected = new PointF((float) parentPosition[0] + e.getX(), (float) parentPosition[1] + e.getY());

                    // Turns off any color tints.
                    fill.setCardBackgroundColor(ContextCompat.getColor(activity.getApplicationContext(), R.color.white));

                    // Invalidates the view to force a redraw.
                    fill.invalidate();

                    // Returns true. DragEvent.getResult() will return true.
                    return true;

                case DragEvent.ACTION_DRAG_ENDED:

                    int[] src = new int[2];
                    drag.getLocationInWindow(src);

                    double src_centreX = src[0] + drag.getWidth() / 2.0;
                    double src_centreY = src[1] + drag.getHeight() / 2.0;

                    int[] dest = new int[2];
                    parent.getLocationInWindow(dest);

                    double dest_centreX = dest[0] + parent.getWidth() / 2.0;
                    double dest_centreY = dest[1] + parent.getHeight() / 2.0;

                    //CircleActivity.amplitude = (float) Math.sqrt(Math.pow(src_centreX - dest_centreX, 2) + Math.pow(src_centreY - dest_centreY, 2));

                    CircleActivity.amplitude = (float) (2 * circleRadius * Math.cos(Math.PI / 2.0 / nodes));

                    CircleActivity.from.add(new PointF((float) src_centreX, (float) src_centreY));
                    CircleActivity.to.add(new PointF((float) dest_centreX, (float) dest_centreY));
                    CircleActivity.select.add(selected);
                    CircleActivity.mt.add((float) (System.currentTimeMillis() - activity.startTime));
                    CircleActivity.currentWidths.add((float) parent.getWidth());

                    // Turns off any color tinting.
                    fill.setCardBackgroundColor(ContextCompat.getColor(activity.getApplicationContext(), R.color.white));

                    // Invalidates the view to force a redraw.
                    fill.invalidate();

                    // Does a getResult(), and displays what happened.
                    if (e.getResult()) {
                        if (getActivity(parent) instanceof CircleActivity) {
                            activity.nextLevel();
                            CircleActivity.error.add(false);
                        }
                    } else {
                        if (getActivity(parent) instanceof CircleActivity) {
                            activity.nextLevel();
                            CircleActivity.error.add(true);
                        }
                    }
                    // Returns true; the value is ignored.
                    return true;

                // An unknown action type was received.
                default:
                   //Log.e("DragDrop Example","Unknown action type received by View.OnDragListener.");
                    break;
            }

            return false;

        });
    }

    public void changeSize() {
        num_updates += 1;
        if (expand) {
            radius += speed * update_interval;
            if (radius > max_radius) {
                double diff = radius - max_radius;
                radius = max_radius - diff;
                expand = false;
            }
        } else {
            radius -= speed * update_interval;
            if (radius < min_radius) {
                double diff = min_radius - radius;
                radius = min_radius + diff;
                expand = true;
            }
        }
        changeLayoutParams();
    }

    private void changeLayoutParams() {
        banner.setVisibility(View.VISIBLE);
        bg.setVisibility(View.VISIBLE);

        LinearLayout.LayoutParams bg_params = (LinearLayout.LayoutParams) bg.getLayoutParams();
        bg_params.width = (int) radius;
        bg_params.height = (int) radius;
        bg.setLayoutParams(bg_params);

        LinearLayout.LayoutParams outer_edge_ring_params = (LinearLayout.LayoutParams) outer_edge_ring.getLayoutParams();
        outer_edge_ring_params.width = (int) radius;
        outer_edge_ring_params.height = (int) radius;
        outer_edge_ring.setLayoutParams(outer_edge_ring_params);
        outer_edge_ring.setRadius((int) (radius * curve));

        CardView.LayoutParams ring_params = (CardView.LayoutParams) ring.getLayoutParams();
        ring_params.width = (int) (radius - ring_edge_radius);
        ring_params.height = (int) (radius - ring_edge_radius);
        ring.setLayoutParams(ring_params);
        ring.setRadius((int) ((radius - ring_edge_radius) * curve));

        CardView.LayoutParams inner_edge_ring_params = (CardView.LayoutParams) inner_edge_ring.getLayoutParams();
        inner_edge_ring_params.width = (int) (radius - ring_radius - ring_edge_radius);
        inner_edge_ring_params.height = (int) (radius - ring_radius - ring_edge_radius);
        inner_edge_ring.setLayoutParams(inner_edge_ring_params);
        inner_edge_ring.setRadius((int) ((radius - ring_radius - ring_edge_radius) * curve));

        CardView.LayoutParams fill_params = (CardView.LayoutParams) fill.getLayoutParams();
        fill_params.width = (int) (radius - ring_radius - 2 * ring_edge_radius);
        fill_params.height = (int) (radius - ring_radius - 2 * ring_edge_radius);
        fill.setLayoutParams(fill_params);
        fill.setRadius((int) ((radius - ring_radius - ring_edge_radius) * curve));

        speedTextView.setText(HtmlCompat.fromHtml(String.format("<b>Speed:</b> %.2f dp/s", speed), HtmlCompat.FROM_HTML_MODE_LEGACY));
        radiusTextView.setText(HtmlCompat.fromHtml(String.format("<b>Radius:</b> %.2f dp", radius), HtmlCompat.FROM_HTML_MODE_LEGACY));
        timeTextView.setText(HtmlCompat.fromHtml(String.format("<b>Time:</b> %.2f s", num_updates * update_interval), HtmlCompat.FROM_HTML_MODE_LEGACY));
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {

    }
}
