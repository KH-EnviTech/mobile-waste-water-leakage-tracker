package com.example.wandy.waterwastereport.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.wandy.waterwastereport.Adapter.HistoryAdapter;
import com.example.wandy.waterwastereport.R;
import com.example.wandy.waterwastereport.model.HistoryModel;
import com.example.wandy.waterwastereport.model.PostModel;
import com.example.wandy.waterwastereport.sqlite.SaveSql;

import java.util.ArrayList;

public class History extends AppCompatActivity {

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Toolbar toolbar = findViewById(R.id.tool_bar_history);
        toolbar.setTitle("History");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        SaveSql saveSql = new SaveSql(this);
        final ArrayList<PostModel> models = saveSql.getDB();
        ArrayList<HistoryModel> historyModels = new ArrayList<>();

        for (PostModel postModel: models){
            HistoryModel model = new HistoryModel();
            model.setDate(postModel.getDate());
            model.setDescription(postModel.getDescription());
            historyModels.add(model);
        }

        ListView listView = findViewById(R.id.list_view);

        HistoryAdapter arrayAdapter = new HistoryAdapter(this,  historyModels);

        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent();
                intent.putExtra("id", models.get(i).getId());
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }
}
