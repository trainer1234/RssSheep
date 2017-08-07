package com.example.sss.rsssheep;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SSS on 01/05/2017.
 */

public class DeleteWebsiteFragment extends Fragment {
    ListView lvSite;
    List<String> siteURLList;


    public DeleteWebsiteFragment(){

    }

    private void getData(){
        RSSDatabaseHandler rssDb = new RSSDatabaseHandler(getContext());
        List<Website> siteList;
        siteList = rssDb.getAllSites();
        siteURLList = new ArrayList<String>();
        for(int i=0; i<siteList.size(); i++){
            siteURLList.add(siteList.get(i).getRssLink());
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int menuItemIndex = item.getItemId();
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int listPos = info.position;
        // check for selected option
        if(menuItemIndex == 0){
            // user selected delete
            // delete the feed
            RSSDatabaseHandler rssDb = new RSSDatabaseHandler(getContext());
            Website site = rssDb.getSite(siteURLList.get(listPos));

            // delete all old post in rss site except bookmark post first
            PostDatabaseHandler postDb = new PostDatabaseHandler(getContext());
            postDb.deleteAllPostInSite(site.getRssLink());
            postDb.close();

            rssDb.deleteSite(site);

            rssDb.close();
            //reloading same activity again
            getActivity().recreate();
        }

        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if(v.getId() == R.id.listSite){
            menu.setHeaderTitle(getString(R.string.remove));
            menu.add(Menu.NONE, 0, 0, getString(R.string.delete_site));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_website_delete, container, false);
        getData();

        lvSite = (ListView)layout.findViewById(R.id.listSite);
        //final DeleteWebsiteAdapter adapter = new DeleteWebsiteAdapter(getActivity(), R.layout.site_list_row, siteList);
        ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, siteURLList);
        lvSite.setAdapter(adapter);
        registerForContextMenu(lvSite);

        lvSite.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getContext(), siteURLList.get(i), Toast.LENGTH_SHORT).show();
            }
        });

        /*Button btnRemove = (Button)layout.findViewById(R.id.btnRemove);
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RSSDatabaseHandler rssDb = new RSSDatabaseHandler(getContext());
                for(int i=lvSite.getCount() - 1; i >= 0; i--){
                    view = lvSite.getChildAt(i);
                    CheckBox chk = (CheckBox)view.findViewById(R.id.chkItem);
                    if(chk.isChecked()){
                        Website web = rssDb.getSite(siteList.get(i).getRssLink());
                        rssDb.deleteSite(web);
                        siteList.remove(i);
                    }
                }
                adapter.notifyDataSetChanged();
                rssDb.close();
                getActivity().recreate();
            }
        });*/
        return layout;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
