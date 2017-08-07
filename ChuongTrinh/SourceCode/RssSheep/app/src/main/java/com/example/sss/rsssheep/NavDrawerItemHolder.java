package com.example.sss.rsssheep;

import android.view.View;
import android.widget.TextView;

/**
 * Created by SSS on 25/04/2017.
 */

public class NavDrawerItemHolder extends ChildViewHolder{
    private TextView tvTitle;

    public NavDrawerItemHolder(View itemView){
        super(itemView);
        tvTitle = (TextView)itemView.findViewById(R.id.tvTitle);
    }

    public void bind(NavDrawerItem navDrawerItem){
        tvTitle.setText(navDrawerItem.getTitle());
    }
}
