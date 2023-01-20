package com.example.expandingcircle;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
    private final List<Boolean> expand;
    private final List<MyEntry> data;
    private final List<Throughput> throughputValues;
    private final List<String> username;
    private final List<String> code;
    private final List<Integer> nodes;
    private final List<Integer> wMin;
    private final List<Integer> wMax;
    private final List<Integer> speed;
    private final List<Integer> dropMisses;
    private final List<List<Double>> speedsArrays;
    private final List<List<Integer>> nodesArrays;

    public CustomAdapter(List<MyEntry> data, List<List<Integer>> nodesArrays, List<List<Double>> speedsArrays ,List<Integer> dropMisses, List<Boolean> expand, List<Throughput> throughputValues, List<Integer> nodes, List<String> username, List<String> code, List<Integer> wMin, List<Integer> wMax, List<Integer> speed) {
        this.data = data;
        this.throughputValues = throughputValues;
        this.speedsArrays = speedsArrays;
        this.nodesArrays = nodesArrays;
        this.dropMisses = dropMisses;
        this.expand = expand;
        this.username = username;
        this.code = code;
        this.nodes = nodes;
        this.wMin = wMin;
        this.wMax = wMax;
        this.speed = speed;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public CustomAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout, viewGroup, false);

        return new CustomAdapter.ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(CustomAdapter.ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        
        TextView
                textViewUsername,
                textViewCode,
                textViewA,
                textViewNodes,
                textViewAe,
                textViewErrorRate,
                textViewID,
                textViewIDe,
                textViewMisses,
                textViewMT,
                textViewNumberOfTrials,
                textViewSDx,
                textViewThroughput,
                textViewW,
                textViewWe,
                textViewX,
                textViewWMin,
                textViewWMax,
                textViewSpeed,
                textViewExpand,
                textViewNodesArray,
                textViewSpeedsArray,
                textViewDropErrorRate,
                textViewDropMisses;
        Button buttonDelete = viewHolder.getButtonDelete();
        textViewUsername = viewHolder.getTextViewUsername();
        textViewCode = viewHolder.getTextViewCode();
        textViewA = viewHolder.getTextViewA();
        textViewNodes = viewHolder.getTextViewNodes();
        textViewAe = viewHolder.getTextViewAe();
        textViewErrorRate = viewHolder.getTextViewErrorRate();
        textViewID = viewHolder.getTextViewID();
        textViewIDe = viewHolder.getTextViewIDe();
        textViewMisses = viewHolder.getTextViewMisses();
        textViewMT = viewHolder.getTextViewMT();
        textViewNumberOfTrials = viewHolder.getTextViewNumberOfTrials();
        textViewSDx = viewHolder.getTextViewSDx();
        textViewThroughput = viewHolder.getTextViewThroughput();
        textViewW = viewHolder.getTextViewW();
        textViewWe = viewHolder.getTextViewWe();
        textViewX = viewHolder.getTextViewX();
        textViewNodesArray = viewHolder.getTextViewNodesArray();
        textViewSpeedsArray = viewHolder.getTextViewSpeedsArray();
        textViewDropErrorRate = viewHolder.getTextViewDropErrorRate();
        textViewDropMisses = viewHolder.getTextViewDropMisses();
        textViewWMin = viewHolder.getTextViewWMin();
        textViewWMax = viewHolder.getTextViewWMax();
        textViewSpeed = viewHolder.getTextViewSpeed();
        textViewExpand = viewHolder.getTextViewExpand();
        Throughput thp = throughputValues.get(position);
        textViewUsername.setText(HtmlCompat.fromHtml("<b>Username: </b>" + username.get(position), HtmlCompat.FROM_HTML_MODE_LEGACY));
        textViewCode.setText(HtmlCompat.fromHtml("<b>Code: </b>" + code.get(position), HtmlCompat.FROM_HTML_MODE_LEGACY));
        textViewA.setText(HtmlCompat.fromHtml("<b>A: </b>" + ((Float) thp.getA() + " px"), HtmlCompat.FROM_HTML_MODE_LEGACY));
        textViewNodes.setText(HtmlCompat.fromHtml("<b>Number Of Targets: </b>" + nodes.get(position), HtmlCompat.FROM_HTML_MODE_LEGACY));
        textViewAe.setText(HtmlCompat.fromHtml("<b>Ae: </b>" + thp.getAe() + " px", HtmlCompat.FROM_HTML_MODE_LEGACY));
        textViewErrorRate.setText(HtmlCompat.fromHtml("<b>Error Rate: </b>" + thp.getErrorRate() + "%", HtmlCompat.FROM_HTML_MODE_LEGACY));
        textViewID.setText(HtmlCompat.fromHtml("<b>ID: </b>" + thp.getID() + " b", HtmlCompat.FROM_HTML_MODE_LEGACY));
        textViewIDe.setText(HtmlCompat.fromHtml("<b>IDe: </b>" + thp.getIDe() + " b", HtmlCompat.FROM_HTML_MODE_LEGACY));
        textViewMisses.setText(HtmlCompat.fromHtml("<b>Misses: </b>" + ((Integer) thp.getMisses()), HtmlCompat.FROM_HTML_MODE_LEGACY));
        textViewMT.setText(HtmlCompat.fromHtml("<b>MT: </b>" + ((Float) thp.getMT() + " ms"), HtmlCompat.FROM_HTML_MODE_LEGACY));
        textViewNumberOfTrials.setText(HtmlCompat.fromHtml("<b>Number Of Trials: </b>" + ((Integer) thp.getNumberOfTrials()), HtmlCompat.FROM_HTML_MODE_LEGACY));
        textViewSDx.setText(HtmlCompat.fromHtml("<b>SDx: </b>" + thp.getSDx() + " px", HtmlCompat.FROM_HTML_MODE_LEGACY));
        textViewThroughput.setText(HtmlCompat.fromHtml("<b>Thp: </b>" + thp.getThroughput() + " b/s", HtmlCompat.FROM_HTML_MODE_LEGACY));
        textViewW.setText(HtmlCompat.fromHtml("<b>Avg. W: </b>" + thp.getW() + " px", HtmlCompat.FROM_HTML_MODE_LEGACY));
        textViewWe.setText(HtmlCompat.fromHtml("<b>We: </b>" + thp.getWe() + " px", HtmlCompat.FROM_HTML_MODE_LEGACY));
        textViewX.setText(HtmlCompat.fromHtml("<b>X: </b>" + thp.getX() + " px", HtmlCompat.FROM_HTML_MODE_LEGACY));
        textViewWMin.setText(HtmlCompat.fromHtml("<b>Min. W: </b>" + wMin.get(position) + " px", HtmlCompat.FROM_HTML_MODE_LEGACY));
        textViewWMax.setText(HtmlCompat.fromHtml("<b>Max. W: </b>" + wMax.get(position) + " px", HtmlCompat.FROM_HTML_MODE_LEGACY));
        textViewSpeed.setText(HtmlCompat.fromHtml("<b>Speed: </b>" + speed.get(position) + " px/s", HtmlCompat.FROM_HTML_MODE_LEGACY));
        textViewExpand.setText(HtmlCompat.fromHtml("<b>Expand: </b>" + expand.get(position), HtmlCompat.FROM_HTML_MODE_LEGACY));
        textViewNodesArray.setText(HtmlCompat.fromHtml("<b>Nodes List: </b>" + nodesArrays.get(position), HtmlCompat.FROM_HTML_MODE_LEGACY));
        textViewSpeedsArray.setText(HtmlCompat.fromHtml("<b>Speed List: </b>" + speedsArrays.get(position), HtmlCompat.FROM_HTML_MODE_LEGACY));
        DecimalFormat df = new DecimalFormat("#,#####0.00000");
        df.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
        String newError = df.format((float) dropMisses.get(position) / nodes.get(position) * 100.0f);
        textViewDropErrorRate.setText(HtmlCompat.fromHtml("<b>Drop Error Rate: </b>" + newError + "%", HtmlCompat.FROM_HTML_MODE_LEGACY));
        textViewDropMisses.setText(HtmlCompat.fromHtml("<b>Drop Misses: </b>" + dropMisses.get(position), HtmlCompat.FROM_HTML_MODE_LEGACY));

        final int positionfinal = position;

        buttonDelete.setOnClickListener(v -> {
            Context context = buttonDelete.getContext();
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(context.getResources().getString(R.string.delete_entry_dialog))
                    .setPositiveButton(context.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    MyEntry.deleteEntry((Results) buttonDelete.getContext(), data.get(positionfinal));

                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
                                    break;
                            }
                        }
                    })
                    .setNegativeButton(context.getResources().getString(R.string.no),  new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    MyEntry.deleteEntry((Results) buttonDelete.getContext(), data.get(positionfinal));

                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
                                    break;
                            }
                        }
                    }).show();
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return throughputValues.size();
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView
                textViewUsername,
                textViewCode,
                textViewA,
                textViewNodes,
                textViewAe, 
                textViewErrorRate, 
                textViewID, 
                textViewIDe,
                textViewMisses,
                textViewMT,
                textViewNumberOfTrials,
                textViewSDx,
                textViewThroughput,
                textViewW,
                textViewWe,
                textViewX,
                textViewWMin,
                textViewWMax,
                textViewSpeed,
                textViewExpand,
                textViewNodesArray,
                textViewSpeedsArray,
                textViewDropErrorRate,
                textViewDropMisses;
        private Button buttonDelete;
        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            textViewUsername = view.findViewById(R.id.textViewUsername);
            textViewCode = view.findViewById(R.id.textViewCode);
            textViewA = view.findViewById(R.id.textViewA);
            textViewNodes = view.findViewById(R.id.textViewNodes);
            textViewAe = view.findViewById(R.id.textViewAe);
            textViewErrorRate = view.findViewById(R.id.textViewErrorRate);
            textViewID = view.findViewById(R.id.textViewID);
            textViewIDe = view.findViewById(R.id.textViewIDe);
            textViewMisses = view.findViewById(R.id.textViewMisses);
            textViewMT = view.findViewById(R.id.textViewMT);
            textViewNumberOfTrials = view.findViewById(R.id.textViewNumberOfTrials);
            textViewSDx = view.findViewById(R.id.textViewSDx);
            textViewThroughput = view.findViewById(R.id.textViewThroughput);
            textViewW = view.findViewById(R.id.textViewW);
            textViewWe = view.findViewById(R.id.textViewWe);
            textViewX = view.findViewById(R.id.textViewX);
            textViewWMin = view.findViewById(R.id.textViewWMin);
            textViewWMax = view.findViewById(R.id.textViewWMax);
            textViewSpeed = view.findViewById(R.id.textViewSpeed);
            textViewExpand = view.findViewById(R.id.textViewExpand);
            textViewNodesArray = view.findViewById(R.id.textViewNodesArray);
            textViewSpeedsArray = view.findViewById(R.id.textViewSpeedsArray);
            textViewDropErrorRate = view.findViewById(R.id.textViewDropErrorRate);
            textViewDropMisses = view.findViewById(R.id.textViewDropMisses);
            buttonDelete = view.findViewById(R.id.buttonDelete);
        }

        public TextView getTextViewUsername() {
            return textViewUsername;
        }
        public TextView getTextViewCode() {
            return textViewCode;
        }
        public TextView getTextViewA() {
            return textViewA;
        }
        public TextView getTextViewNodes() {
            return textViewNodes;
        }
        public TextView getTextViewAe() {
            return textViewAe;
        }
        public TextView getTextViewErrorRate() {
            return textViewErrorRate;
        }
        public TextView getTextViewID() {
            return textViewID;
        }
        public TextView getTextViewIDe() {
            return textViewIDe;
        }
        public TextView getTextViewMisses() {
            return textViewMisses;
        }
        public TextView getTextViewMT() {
            return textViewMT;
        }
        public TextView getTextViewNumberOfTrials() {
            return textViewNumberOfTrials;
        }
        public TextView getTextViewSDx() {
            return textViewSDx;
        }
        public TextView getTextViewThroughput() {
            return textViewThroughput;
        }
        public TextView getTextViewW() {
            return textViewW;
        }
        public TextView getTextViewWe() {
            return textViewWe;
        }
        public TextView getTextViewX() {
            return textViewX;
        }
        public TextView getTextViewWMin() {
            return textViewWMin;
        }
        public TextView getTextViewWMax() {
            return textViewWMax;
        }
        public TextView getTextViewSpeed() {
            return textViewSpeed;
        }
        public TextView getTextViewExpand() {
            return textViewExpand;
        }
        public TextView getTextViewNodesArray() {
            return textViewNodesArray;
        }
        public TextView getTextViewSpeedsArray() {
            return textViewSpeedsArray;
        }
        public TextView getTextViewDropErrorRate() {
            return textViewDropErrorRate;
        }
        public TextView getTextViewDropMisses() {
            return textViewDropMisses;
        }
        public Button getButtonDelete() {
            return buttonDelete;
        }
        
    }
}