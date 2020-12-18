package com.example.finalapp.Reclamation;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.finalapp.R;

import java.util.ArrayList;

public class ReclamationAdapter extends ArrayAdapter {
    Context adapterContext;
    int adapterResource;
    ArrayList<Reclamation> adapterReclamations;

    public ReclamationAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Reclamation> reclamations) {
        super(context, resource, reclamations);
        adapterContext = context;
        adapterResource = resource;
        adapterReclamations = reclamations;
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return super.getItem(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row;
        LayoutInflater rowInflater = LayoutInflater.from(adapterContext);
        row = rowInflater.inflate(adapterResource, parent, false);

        TextView textView1 = row.findViewById(R.id.textView1);
        TextView textView2 = row.findViewById(R.id.textView2);
        TextView textView3 = row.findViewById(R.id.textView3);
        TextView textView4 = row.findViewById(R.id.textView4);

        textView1.setText(adapterReclamations.get(position).date);
        textView2.setText(adapterReclamations.get(position).categorie);
        textView3.setText(adapterReclamations.get(position).type);
        textView4.setText(adapterReclamations.get(position).etat);
        textView4.setTextColor(Color.parseColor(getStateColor(adapterReclamations.get(position).etat)));

        return row;
    }

    public static String getStateColor(String etat) {
        if(etat.startsWith("N")) return "#ff0000";
        if(etat.startsWith("E")) return "#ffa500";
        if(etat.startsWith("T")) return "#008000";
        return "#000000";
    }
}
