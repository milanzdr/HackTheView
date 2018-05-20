package com.eventiotic.hacktheview;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;


public class MainActivity extends AppCompatActivity {

    public String baseUrl="https://www.overpass-api.de/api/interpreter";
    public String tag = "HackTheView";
    public int viewRadius = 20000;
    public String nodeType = "peak";
    public int viewAngle = 45;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /** Called when the user taps the Send button */
    public void openPeaks(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, OSMNodesViewActivity.class);
        intent.putExtra("baseUrl", baseUrl);
        intent.putExtra("tag", tag);
        intent.putExtra("viewRadius", viewRadius);
        intent.putExtra("viewAngle", viewAngle);
        intent.putExtra("nodeType", "peak");
        startActivity(intent);
    }


    public void openSaddles(View view) {
        Intent intent = new Intent(this, OSMNodesViewActivity.class);
        intent.putExtra("baseUrl", baseUrl);
        intent.putExtra("tag", tag);
        intent.putExtra("viewRadius", viewRadius);
        intent.putExtra("viewAngle", viewAngle);
        intent.putExtra("nodeType", "saddle");
        startActivity(intent);
    }
    public void openSprings(View view) {
        Intent intent = new Intent(this, OSMNodesViewActivity.class);
        intent.putExtra("baseUrl", baseUrl);
        intent.putExtra("tag", tag);
        intent.putExtra("viewRadius", viewRadius);
        intent.putExtra("viewAngle", viewAngle);
        intent.putExtra("nodeType", "spring");
        startActivity(intent);

    }
    public void openWaterfalls(View view) {
        Intent intent = new Intent(this, OSMNodesViewActivity.class);
        intent.putExtra("baseUrl", baseUrl);
        intent.putExtra("tag", tag);
        intent.putExtra("viewRadius", viewRadius);
        intent.putExtra("viewAngle", viewAngle);
        intent.putExtra("nodeType", "waterfall");
        startActivity(intent);

    }
    public void openSettlements(View view) {
        Intent intent = new Intent(this, OSMNodesViewActivity.class);
        intent.putExtra("baseUrl", baseUrl);
        intent.putExtra("tag", tag);
        intent.putExtra("viewRadius", viewRadius);
        intent.putExtra("viewAngle", viewAngle);
        intent.putExtra("nodeType", "settlement");
        startActivity(intent);

    }
    public void openCaves(View view) {
        Intent intent = new Intent(this, OSMNodesViewActivity.class);
        intent.putExtra("baseUrl", baseUrl);
        intent.putExtra("tag", tag);
        intent.putExtra("viewRadius", viewRadius);
        intent.putExtra("viewAngle", viewAngle);
        intent.putExtra("nodeType", "cave_entrance");
        startActivity(intent);

    }
    public void openForests(View view) {
        Intent intent = new Intent(this, OSMNodesViewActivity.class);
        intent.putExtra("baseUrl", baseUrl);
        intent.putExtra("tag", tag);
        intent.putExtra("viewRadius", viewRadius);
        intent.putExtra("viewAngle", viewAngle);
        intent.putExtra("nodeType", "wood");
        startActivity(intent);

    }
    public void openLakes(View view) {
        Intent intent = new Intent(this, OSMNodesViewActivity.class);
        intent.putExtra("baseUrl", baseUrl);
        intent.putExtra("tag", tag);
        intent.putExtra("viewRadius", viewRadius);
        intent.putExtra("viewAngle", viewAngle);
        intent.putExtra("nodeType", "water");
        startActivity(intent);

    }
    public void openOther(View view) {
        Intent intent = new Intent(this, OSMNodesViewActivity.class);
        intent.putExtra("baseUrl", baseUrl);
        intent.putExtra("tag", tag);
        intent.putExtra("viewRadius", viewRadius);
        intent.putExtra("viewAngle", viewAngle);
        intent.putExtra("nodeType", "other");
        startActivity(intent);

    }



}
