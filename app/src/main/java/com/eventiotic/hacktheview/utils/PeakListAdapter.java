package com.eventiotic.hacktheview.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.eventiotic.hacktheview.R;

import org.w3c.dom.Text;

public class PeakListAdapter extends RecyclerView.Adapter<PeakListAdapter.ViewHolder>  {
    Peak[] peaks;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView nameTxt;
        public TextView eleTxt;
        //public TextView eleTxt;
        public ViewHolder(View v) {
            super(v);
            nameTxt = (TextView) v.findViewById(R.id.nameTxt);
            eleTxt = (TextView) v.findViewById(R.id.eleTxt);
        }
    }


    public PeakListAdapter(Peak[] peaks) {
        this.peaks = peaks;
    }


    @Override
    public PeakListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.peaksmodel,parent,false);

        // create a new view
        //TextView v = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.peaksmodel, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String peakNameStr;
        String peakElevationStr;
        //BIND DATA
        peakNameStr=peaks[position].getTags().getName();
        if(peaks[position].getTags().getName()=="") {
            peakNameStr="Unnamed";
        }
        holder.nameTxt.setText(peakNameStr);
        if(peaks[position].getTags().getEle()==0) {
            peakElevationStr="Not known";
        } else {
            peakElevationStr=peaks[position].getTags().getEle().toString()+"m";
        }
        holder.eleTxt.setText(peakElevationStr);

    }

    @Override
    public int getItemCount() {
        return peaks.length;
    }

}