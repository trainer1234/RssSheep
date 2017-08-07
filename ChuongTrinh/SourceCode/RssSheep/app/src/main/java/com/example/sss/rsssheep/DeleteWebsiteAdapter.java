package com.example.sss.rsssheep;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SSS on 01/05/2017.
 */

public class DeleteWebsiteAdapter extends ArrayAdapter<Website> {
    Activity context = null;
    List<Website> websites = null;
    int layoutId;

    public DeleteWebsiteAdapter(Activity context, int resource, List<Website> websites) {
        super(context, resource, websites);
        this.context = context;
        this.layoutId = resource;
        this.websites = websites;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        convertView = inflater.inflate(layoutId, null);

        final TextView txtRssLink = (TextView)convertView.findViewById(R.id.tvRssLink);
        final Website web = websites.get(position);
        txtRssLink.setText(web.getRssLink());
        return convertView;
    }
}
