package com.example.sss.rsssheep;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by SSS on 25/04/2017.
 */

public class NavDrawerArrayAdapter extends ExpandableRecyclerAdapter<NavDrawerArray, NavDrawerItem, NavDrawerArrayHolder, NavDrawerItemHolder> {
    private static final int PARENT_NORMAL = 0;

    private LayoutInflater mInflater;
    private List<NavDrawerArray> mNavDrawerArrayList;

    public NavDrawerArrayAdapter(Context context, @NonNull List<NavDrawerArray> parentList) {
        super(parentList);
        mInflater = LayoutInflater.from(context);
        mNavDrawerArrayList = parentList;
    }

    @NonNull
    @Override
    public NavDrawerArrayHolder onCreateParentViewHolder(@NonNull ViewGroup parentViewGroup, int viewType) {
        View navDrawerArrayView = mInflater.inflate(R.layout.nav_drawer_row_array, parentViewGroup, false);
        // In case when there are other style of view to change for user
        /*switch (viewType){
            case PARENT_NORMAL:
                navDrawerArrayView = mInflater.inflate(R.layout.nav_drawer_row_array, parentViewGroup, false);
                break;
        }*/
        return new NavDrawerArrayHolder(navDrawerArrayView);
    }

    @NonNull
    @Override
    public NavDrawerItemHolder onCreateChildViewHolder(@NonNull ViewGroup childViewGroup, int viewType) {
        View navDrawerItemView = mInflater.inflate(R.layout.nav_drawer_row_item, childViewGroup, false);
        // In case when there are other style of navigation view row to change for user
        /*switch (viewType){
            case PARENT_NORMAL:
                navDrawerItemView = mInflater.inflate(R.layout.nav_drawer_row_, childViewGroup, false);
                break;
        }*/
        return new NavDrawerItemHolder(navDrawerItemView);
    }

    @Override
    public void onBindParentViewHolder(@NonNull NavDrawerArrayHolder navDrawerArrayViewHolder, int parentPosition, @NonNull NavDrawerArray navDrawerArray) {
        navDrawerArrayViewHolder.bind(navDrawerArray);
    }

    @Override
    public void onBindChildViewHolder(@NonNull NavDrawerItemHolder navDrawerItemViewHolder, int parentPosition, int childPosition, @NonNull NavDrawerItem navDrawerItem) {
        navDrawerItemViewHolder.bind(navDrawerItem);
    }
}
