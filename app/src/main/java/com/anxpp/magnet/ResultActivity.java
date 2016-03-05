package com.anxpp.magnet;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class ResultActivity extends AppCompatActivity {

    String searchKey="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        searchKey=this.getIntent().getStringExtra("search_key");
    }
}

