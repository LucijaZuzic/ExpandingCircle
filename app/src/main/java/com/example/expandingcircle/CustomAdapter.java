package com.example.expandingcircle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
    private final List<Throughput> throughputValues;
    private final List<Integer> nodes;

    public CustomAdapter(List<Throughput> throughputValues, List<Integer> nodes) {
        this.throughputValues = throughputValues;
        this.nodes = nodes;
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
        
        TextView textViewA,
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
                textViewX;
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
        Throughput thp = throughputValues.get(position);
        textViewA.setText(HtmlCompat.fromHtml("<b>A: </b>" + ((Float) thp.getA() + " dp"), HtmlCompat.FROM_HTML_MODE_LEGACY));
        textViewNodes.setText(HtmlCompat.fromHtml("<b>Number Of Targets: </b>" + nodes.get(position), HtmlCompat.FROM_HTML_MODE_LEGACY));
        textViewAe.setText(HtmlCompat.fromHtml("<b>Ae: </b>" + ((Float) thp.getAe()) + " dp", HtmlCompat.FROM_HTML_MODE_LEGACY));
        textViewErrorRate.setText(HtmlCompat.fromHtml("<b>Error Rate: </b>" + ((Float) thp.getErrorRate()) + "%", HtmlCompat.FROM_HTML_MODE_LEGACY));
        textViewID.setText(HtmlCompat.fromHtml("<b>ID: </b>" + ((Float) thp.getID()) + " b", HtmlCompat.FROM_HTML_MODE_LEGACY));
        textViewIDe.setText(HtmlCompat.fromHtml("<b>IDe: </b>" + ((Float) thp.getIDe()) + " b", HtmlCompat.FROM_HTML_MODE_LEGACY));
        textViewMisses.setText(HtmlCompat.fromHtml("<b>Misses: </b>" + ((Integer) thp.getMisses()), HtmlCompat.FROM_HTML_MODE_LEGACY));
        textViewMT.setText(HtmlCompat.fromHtml("<b>MT: </b>" + ((Float) thp.getMT() + " ms"), HtmlCompat.FROM_HTML_MODE_LEGACY));
        textViewNumberOfTrials.setText(HtmlCompat.fromHtml("<b>Number of Trials: </b>" + ((Integer) thp.getNumberOfTrials()), HtmlCompat.FROM_HTML_MODE_LEGACY));
        textViewSDx.setText(HtmlCompat.fromHtml("<b>SDx: </b>" + ((Float) thp.getSDx()) + " dp", HtmlCompat.FROM_HTML_MODE_LEGACY));
        textViewThroughput.setText(HtmlCompat.fromHtml("<b>Thp: </b>" + ((Float) thp.getThroughput()) + " b/ms", HtmlCompat.FROM_HTML_MODE_LEGACY));
        textViewW.setText(HtmlCompat.fromHtml("<b>W: </b>" + ((Float) thp.getW()) + " dp", HtmlCompat.FROM_HTML_MODE_LEGACY));
        textViewWe.setText(HtmlCompat.fromHtml("<b>We: </b>" + ((Float) thp.getWe()) + " dp", HtmlCompat.FROM_HTML_MODE_LEGACY));
        textViewX.setText(HtmlCompat.fromHtml("<b>X: </b>" + ((Float) thp.getX()) + " dp", HtmlCompat.FROM_HTML_MODE_LEGACY));
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
        private final TextView textViewA,
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
                textViewX;
        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

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
        
        
    }
}