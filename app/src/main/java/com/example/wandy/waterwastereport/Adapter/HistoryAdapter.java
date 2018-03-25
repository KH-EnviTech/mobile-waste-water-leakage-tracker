package com.example.wandy.waterwastereport.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.wandy.waterwastereport.R;
import com.example.wandy.waterwastereport.model.HistoryModel;

import java.util.List;

/**
 * Created by wandy on 3/18/18.
 */

public class HistoryAdapter extends ArrayAdapter<HistoryModel> {

    public HistoryAdapter(@NonNull Context context,  @NonNull List<HistoryModel> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        if (convertView == null){

            HistoryModel model = getItem(position);

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.inflalte_history, parent, false);

            TextView description = convertView.findViewById(R.id.text_inflate_history_description);
            TextView date = convertView.findViewById(R.id.text_inflate_history_date);

            description.setText(model.getDescription());
            date.setText(model.getDate());
        }


        return convertView;
    }
}
