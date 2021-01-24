package com.mediaplayer.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mediaplayer.DisplayActivity;
import com.mediaplayer.Model.FolderModel;
import com.mediaplayer.R;

import java.util.ArrayList;


public class FolderAdapter extends BaseAdapter {
    Context context;
    ArrayList<FolderModel> folderModelArrayList;

    public FolderAdapter(Context context, ArrayList<FolderModel> folderModelArrayList) {
        this.context = context;
        this.folderModelArrayList = folderModelArrayList;
    }

    @Override
    public int getCount() {
        return folderModelArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.folderlist, null);
        TextView textView = convertView.findViewById(R.id.folderName);
        textView.setText(folderModelArrayList.get(position).getFolderName());
        convertView.setTag(position);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = Integer.parseInt(v.getTag().toString());
                FolderModel eventModel = folderModelArrayList.get(pos);
                Gson gson = new Gson();
                String folderJson = gson.toJson(eventModel);
                Intent intent = new Intent(context, DisplayActivity.class);
                intent.putExtra("FolderData", folderJson);
                context.startActivity(intent);
            }
        });
        return convertView;
    }
}
