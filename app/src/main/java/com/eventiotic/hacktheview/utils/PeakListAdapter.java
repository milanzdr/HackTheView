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

import java.text.DecimalFormat;
import java.util.List;

public class PeakListAdapter extends RecyclerView.Adapter<PeakListAdapter.ViewHolder>  {
    List<Peak> peaks;




    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTxt;
        public TextView eleTxt;
        public ViewHolder(View v) {
            super(v);
            nameTxt = (TextView) v.findViewById(R.id.nameTxt);
            eleTxt = (TextView) v.findViewById(R.id.eleTxt);
        }
    }

    public void updateData(List<Peak> p) {
        if(p == null || p.size()==0)
            return;
        if (peaks != null && peaks.size()>0)
            peaks.clear();
        peaks.addAll(p);
        notifyDataSetChanged();
    }


    public PeakListAdapter(List<Peak> peaks) {
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
        peakNameStr=peaks.get(position).getTags().getName();
        if(peaks.get(position).getTags().getName()=="") {
            peakNameStr="Unnamed";
        }
        holder.nameTxt.setText(peakNameStr);
        if(peaks.get(position).getTags().getEle()==0) {
            peakElevationStr="Elevation: Not known";
        } else {
            peakElevationStr="Elevation: "+peaks.get(position).getTags().getEle().toString()+"m";
        }
        //if(peaks.get(position).getDistance()) {
        DecimalFormat numberFormat = new DecimalFormat("#.0");
        peakElevationStr=peakElevationStr+", distance: "+numberFormat.format(peaks.get(position).getDistance())+"km, azimuth: "+numberFormat.format(peaks.get(position).getAz())+"deg";
        //}
        holder.eleTxt.setText(peakElevationStr);

    }

    @Override
    public int getItemCount() {
        return peaks.size();
    }

}