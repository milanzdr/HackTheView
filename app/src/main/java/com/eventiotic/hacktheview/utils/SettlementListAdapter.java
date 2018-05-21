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

public class SettlementListAdapter extends RecyclerView.Adapter<SettlementListAdapter.ViewHolder>  {
    private List<OSMNode> adaptersettlements;

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
        adaptersettlements=new ArrayList<OSMNode>(p);
        for (Iterator<OSMNode> iter = adaptersettlements.listIterator(); iter.hasNext(); ) {
            OSMNode a = iter.next();
            if(Math.abs(a.getCurAngle())>phoneAngle/2 || a.getDistance()>r/1000) {
                iter.remove();
            }
        }

        notifyDataSetChanged();
    }


    public SettlementListAdapter(List<OSMNode> sets) {
        this.adaptersettlements = sets;
    }


    @Override
    public SettlementListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.settlementmodel,parent,false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String settlementNameStr;
        String settlementElevationStr="";
        String settlementDescStr="";

        settlementNameStr=adaptersettlements.get(position).getTags().getName();
        if(adaptersettlements.get(position).getTags().getName()=="") {
            settlementNameStr="Unnamed";
        }
        holder.nameTxt.setText(settlementNameStr);
        if(adaptersettlements.get(position).getTags().getEle()!=0) {
            settlementElevationStr="("+adaptersettlements.get(position).getTags().getEle().toString()+"m)";
        }

        DecimalFormat numberFormat = new DecimalFormat("#.0");
        settlementDescStr=settlementDescStr+"Distance: "+numberFormat.format(adaptersettlements.get(position).getDistance())+"km, Azimuth: "+numberFormat.format(adaptersettlements.get(position).getAz())+"deg";
        double ca = adaptersettlements.get(position).getCurAngle();
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
        holder.eleTxt.setText(settlementElevationStr);
        holder.distTxt.setText(settlementDescStr);


    }

    @Override
    public int getItemCount() {
        return adaptersettlements.size();
    }

}