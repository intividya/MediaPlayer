package com.mediaplayer.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.mediaplayer.DisplayActivity;
import com.mediaplayer.Model.FolderModel;
import com.mediaplayer.R;
import com.mediaplayer.SignUpActivity;

import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {
    Context context;
    ArrayList<String> folderModelArrayList;

    public ImageAdapter(Context context, ArrayList<String> folderModelArrayList) {
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
        convertView = inflater.inflate(R.layout.imageslist, null);
        ImageView imageView = convertView.findViewById(R.id.imageView);
        Glide.with(context)
                .load(folderModelArrayList.get(position)).apply(RequestOptions.centerCropTransform())
                .into(imageView);
        convertView.setTag(position);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = Integer.parseInt(v.getTag().toString());

            }
        });
        return convertView;
    }
}
