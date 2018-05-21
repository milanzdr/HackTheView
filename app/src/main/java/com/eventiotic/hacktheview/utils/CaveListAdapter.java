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

public class CaveListAdapter extends RecyclerView.Adapter<CaveListAdapter.ViewHolder>  {
    private List<OSMNode> adaptercaves;

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
        adaptercaves=new ArrayList<OSMNode>(p);
        for (Iterator<OSMNode> iter = adaptercaves.listIterator(); iter.hasNext(); ) {
            OSMNode a = iter.next();
            if(Math.abs(a.getCurAngle())>phoneAngle/2 || a.getDistance()>r/1000) {
                iter.remove();
            }
        }

        notifyDataSetChanged();
    }


    public CaveListAdapter(List<OSMNode> caves) {
        this.adaptercaves = caves;
    }


    @Override
    public CaveListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.cavesmodel,parent,false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String caveNameStr;
        String caveElevationStr="";
        String caveDescStr="";

        caveNameStr=adaptercaves.get(position).getTags().getName();
        if(adaptercaves.get(position).getTags().getName()=="") {
            caveNameStr="Unnamed";
        }
        holder.nameTxt.setText(caveNameStr);
        if(adaptercaves.get(position).getTags().getEle()!=0) {
            caveElevationStr="("+adaptercaves.get(position).getTags().getEle().toString()+"m)";
        }

        DecimalFormat numberFormat = new DecimalFormat("#.0");
        caveDescStr=caveDescStr+"Distance: "+numberFormat.format(adaptercaves.get(position).getDistance())+"km, Azimuth: "+numberFormat.format(adaptercaves.get(position).getAz())+"deg";
        double ca = adaptercaves.get(position).getCurAngle();
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
        holder.eleTxt.setText(caveElevationStr);
        holder.distTxt.setText(caveDescStr);


    }

    @Override
    public int getItemCount() {
        return adaptercaves.size();
    }

}