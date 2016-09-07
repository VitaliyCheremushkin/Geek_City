package com.redvord.geekcity;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by redvo on 05.04.2016.
 */
public class CustomSpinnerAdapter extends ArrayAdapter<String> {

    private ArrayList<String> data;
    public Resources res;
    private LayoutInflater inflater;

    CustomSpinnerAdapter(Context context, ArrayList<String> objects) {
        super(context, R.layout.spinner_row, objects);
        data = objects;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, parent);
    }

    private View getCustomView(int position, ViewGroup parent) {
        View row = inflater.inflate(R.layout.spinner_row, parent, false);
        TextView newsCategory = (TextView) row.findViewById(R.id.newsCategory);
        newsCategory.setText(data.get(position));
        return row;
    }
}
