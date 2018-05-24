package com.eventiotic.hacktheview.utils;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.eventiotic.hacktheview.OSMNodeViewActivity;
import com.eventiotic.hacktheview.R;
import com.eventiotic.hacktheview.models.OSMNode;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OSMNodeListAdapter extends RecyclerView.Adapter<OSMNodeListAdapter.ViewHolder>  {
    private List<OSMNode> adapternodes;
    private String nodeType;
    private RecyclerViewClickListener mListener;


    OSMNodeListAdapter(RecyclerViewClickListener listener) {
        mListener = listener;
    }

    public void setNodeType(String nodeType) {
        Log.i("", nodeType);
        this.nodeType = nodeType;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView nameTxt;
        public TextView eleTxt;
        public TextView distTxt;
        public ImageView dirImg;
        private RecyclerViewClickListener mListener;


        public ViewHolder(View v, RecyclerViewClickListener listener) {
            super(v);
            mListener=listener;
            v.setOnClickListener(this);
            nameTxt = (TextView) v.findViewById(R.id.nameTxt);
            eleTxt = (TextView) v.findViewById(R.id.eleTxt);
            distTxt = (TextView) v.findViewById(R.id.distTxt);
            dirImg = (ImageView) v.findViewById(R.id.pointerImage);

        }
        @Override
        public void onClick(View view) {
            mListener.onClick(view, this.getLayoutPosition());
        }
    }

    public void updateData(List<OSMNode> p, int phoneAngle, int r) {
        if(p == null || p.size()==0) {
            return;
        }
        adapternodes=new ArrayList<OSMNode>(p);
            for (Iterator<OSMNode> iter = adapternodes.listIterator(); iter.hasNext(); ) {
                OSMNode a = iter.next();
                if(Math.abs(a.getCurAngle())>phoneAngle/2 || a.getDistance()>r/1000) {
                    iter.remove();
                }
            }

            notifyDataSetChanged();
    }


    public OSMNodeListAdapter(List<OSMNode> onodes) {
        this.adapternodes = onodes;
    }


    @Override
    public OSMNodeListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.nodesmodel, parent, false);
        ImageView icon = (ImageView) v.findViewById(R.id.iconImage);
        if(this.nodeType.equals("peak")) {
            icon.setImageResource(R.drawable.ic_peak);
        } else if(this.nodeType.equals("spring")) {
            icon.setImageResource(R.drawable.ic_spring);
        } else if(this.nodeType.equals("waterfall")) {
            icon.setImageResource(R.drawable.ic_waterfall);
        } else if(this.nodeType.equals("cave_entrance")) {
            icon.setImageResource(R.drawable.ic_cave);
        } else if(this.nodeType.equals("lake")) {
            icon.setImageResource(R.drawable.ic_lake);
        } else if(this.nodeType.equals("settlement")) {
            icon.setImageResource(R.drawable.ic_settlement);
        }  else if(this.nodeType.equals("dam")) {
            icon.setImageResource(R.drawable.ic_dam);
        }  else if(this.nodeType.equals("forest")) {
            icon.setImageResource(R.drawable.ic_forest);
        }  else if(this.nodeType.equals("picnic_site")) {
            icon.setImageResource(R.drawable.ic_picnic_site);
        }  else if(this.nodeType.equals("saddle")) {
            icon.setImageResource(R.drawable.ic_saddle);
        }  else if(this.nodeType.equals("alpine_hut") || this.nodeType.equals("wilderness_hut")) {
            icon.setImageResource(R.drawable.ic_hut);
        }
        
        ViewHolder vh = new ViewHolder(v, new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(view.getContext(), OSMNodeViewActivity.class);
                OSMNode o = (OSMNode)adapternodes.get(position);
                //Log.i("", ""+o.getLat());
                intent.putExtra("osmid", o.getId());
                intent.putExtra("osmlat", o.getLat());
                intent.putExtra("osmlon", o.getLon());
                intent.putExtra("osmaz", o.getAz());
                intent.putExtra("osmdist", o.getDistance());
                intent.putExtra("osmcurangle", o.getCurAngle());
                intent.putExtra("osmcurangle", o.getCurAngle());
                intent.putExtra("osmname", o.getTags().getName());
                intent.putExtra("osmsource", o.getTags().getSource());
                intent.putExtra("osmwikidata", o.getTags().getWikidata());
                intent.putExtra("osmwikipedia", o.getTags().getWikipedia());
                intent.putExtra("osmele", o.getTags().getEle());
                intent.putExtra("osmname", o.getTags().getName());
                intent.putExtra("osmtype", nodeType);
                intent.putExtra("osmaccess", o.getTags().getAccess());
                intent.putExtra("osmfee", o.getTags().getFee());
                intent.putExtra("osmpop", o.getTags().getPopulation());
                intent.putExtra("osmdesc", o.getTags().getDescription());
                view.getContext().startActivity(intent);
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String nodeNameStr;
        String nodeElevationStr="";
        String nodeDescStr="";

        nodeNameStr=adapternodes.get(position).getTags().getName();
        if(adapternodes.get(position).getTags().getName()=="") {
            nodeNameStr="Unnamed";
        }
        holder.nameTxt.setText(nodeNameStr);
        if(adapternodes.get(position).getTags().getEle()!=0) {
            nodeElevationStr="("+adapternodes.get(position).getTags().getEle().toString()+"m)";
        }

        DecimalFormat numberFormat = new DecimalFormat("#.0");
        nodeDescStr=nodeDescStr+"Distance: "+numberFormat.format(adapternodes.get(position).getDistance())+"km, Azimuth: "+numberFormat.format(adapternodes.get(position).getAz())+"deg";
        double ca = adapternodes.get(position).getCurAngle();
        if(ca!=0) {
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
        holder.eleTxt.setText(nodeElevationStr);
        holder.distTxt.setText(nodeDescStr);

    }

    @Override
    public int getItemCount() {
        return adapternodes.size();
    }

}