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

public class LakeListAdapter extends RecyclerView.Adapter<LakeListAdapter.ViewHolder>  {
    private List<OSMNode> adapterlakes;

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
        adapterlakes=new ArrayList<OSMNode>(p);
        for (Iterator<OSMNode> iter = adapterlakes.listIterator(); iter.hasNext(); ) {
            OSMNode a = iter.next();
            if(Math.abs(a.getCurAngle())>phoneAngle/2 || a.getDistance()>r/1000) {
                iter.remove();
            }
        }

        notifyDataSetChanged();
    }


    public LakeListAdapter(List<OSMNode> lakes) {
        this.adapterlakes = lakes;
    }


    @Override
    public LakeListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.lakesmodel,parent,false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String lakeNameStr;
        String lakeElevationStr="";
        String lakeDescStr="";

        lakeNameStr=adapterlakes.get(position).getTags().getName();
        if(adapterlakes.get(position).getTags().getName()=="") {
            lakeNameStr="Unnamed";
        }
        holder.nameTxt.setText(lakeNameStr);
        if(adapterlakes.get(position).getTags().getEle()!=0) {
            lakeElevationStr="("+adapterlakes.get(position).getTags().getEle().toString()+"m)";
        }

        DecimalFormat numberFormat = new DecimalFormat("#.0");
        lakeDescStr=lakeDescStr+"Distance: "+numberFormat.format(adapterlakes.get(position).getDistance())+"km, Azimuth: "+numberFormat.format(adapterlakes.get(position).getAz())+"deg";
        double ca = adapterlakes.get(position).getCurAngle();
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
        holder.eleTxt.setText(lakeElevationStr);
        holder.distTxt.setText(lakeDescStr);


    }

    @Override
    public int getItemCount() {
        return adapterlakes.size();
    }

}