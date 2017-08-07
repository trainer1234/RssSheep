package com.example.sss.rsssheep;

import java.util.List;

/**
 * Created by SSS on 25/04/2017.
 */

public class NavDrawerArray implements Parent<NavDrawerItem>{
    private List<NavDrawerItem> navDrawerItems;
    private String name;

    public List<NavDrawerItem> getNavDrawerItems() {
        return navDrawerItems;
    }

    public void setNavDrawerItems(List<NavDrawerItem> navDrawerItems) {
        this.navDrawerItems = navDrawerItems;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {

        return name;
    }

    public int getSize(){
        return navDrawerItems.size();
    }

    public NavDrawerItem getHead(){
        return navDrawerItems.get(0);
    }

    public NavDrawerArray(String name, List<NavDrawerItem> navDrawerItems) {
        this.navDrawerItems = navDrawerItems;
        this.name = name;
    }

    @Override
    public List<NavDrawerItem> getChildList() {
        return navDrawerItems;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }
}
