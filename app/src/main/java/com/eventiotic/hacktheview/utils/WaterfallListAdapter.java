package com.eventiotic.hacktheview.utils;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.eventiotic.hacktheview.R;
import com.eventiotic.hacktheview.models.OSMNode;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WaterfallListAdapter extends RecyclerView.Adapter<WaterfallListAdapter.ViewHolder>  {
    private List<OSMNode> adapterwaterfalls;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTxt;
        public TextView eleTxt;
        public TextView distTxt;
        public ImageView dirImg;

        public ViewHolder(View v) {
            super(v);
            nameTxt = (TextView) v.findViewById(R.id.nameTxt);
            eleTxt = (TextView) v.findViewById(R.id.eleTxt);
            distTxt = (TextView) v.findViewById(R.id.distTxt);
            dirImg = (ImageView) v.findViewById(R.id.pointerImage);
        }
    }

    public void updateData(List<OSMNode> p, int phoneAngle, int r) {
        if(p == null || p.size()==0) {
            return;
        }
        //Log.i("", "Radius:"+r);
        adapterwaterfalls=new ArrayList<OSMNode>(p);
        for (Iterator<OSMNode> iter = adapterwaterfalls.listIterator(); iter.hasNext(); ) {
            OSMNode a = iter.next();
            if(Math.abs(a.getCurAngle())>phoneAngle/2 || a.getDistance()>r/1000) {
                iter.remove();
            }
        }

        notifyDataSetChanged();
    }


    public WaterfallListAdapter(List<OSMNode> waterfalls) {
        this.adapterwaterfalls = waterfalls;
    }


    @Override
    public WaterfallListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.waterfallsmodel,parent,false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String waterfallNameStr;
        String waterfallElevationStr="";
        String waterfallDescStr="";

        waterfallNameStr=adapterwaterfalls.get(position).getTags().getName();
        if(adapterwaterfalls.get(position).getTags().getName()=="") {
            waterfallNameStr="Unnamed";
        }
        holder.nameTxt.setText(waterfallNameStr);
        if(adapterwaterfalls.get(position).getTags().getEle()!=0) {
            waterfallElevationStr="("+adapterwaterfalls.get(position).getTags().getEle().toString()+"m)";
        }

        DecimalFormat numberFormat = new DecimalFormat("#.0");
        waterfallDescStr=waterfallDescStr+"Distance: "+numberFormat.format(adapterwaterfalls.get(position).getDistance())+"km, Azimuth: "+numberFormat.format(adapterwaterfalls.get(position).getAz())+"deg";
        double ca = adapterwaterfalls.get(position).getCurAngle();
        if(ca!=0) {
            //peakDescStr=peakDescStr+", View: "+numberFormat.format(adapterpeaks.get(position).getCurAngle());
            if(Math.abs(ca)<5) {
                holder.dirImg.setImageResource(R.drawable.ic_navigation_center);
            } else {
                if (ca<0) {
                    holder.dirImg.setImageResource(R.drawable.ic_navigation_left);
                } else if (ca>0) {
                    holder.dirImg.setImageResource(R.drawable.ic_navigation_right);
                }
            }
        }
        holder.eleTxt.setText(waterfallElevationStr);
        holder.distTxt.setText(waterfallDescStr);


    }

    @Override
    public int getItemCount() {
        return adapterwaterfalls.size();
    }

}