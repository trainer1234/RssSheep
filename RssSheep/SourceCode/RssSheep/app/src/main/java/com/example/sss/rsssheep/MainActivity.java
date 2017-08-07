package com.example.sss.rsssheep;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.Pair;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {
    private static final int RESULT_SETTINGS = 1;
    private final int DAYS = 2;
    private static String TAG = MainActivity.class.getSimpleName();

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    private ArrayList< ArrayList< Pair<String, String> > > urlList;
    private int kindOfView, currentPosition, currentTheme;
    private boolean openDirectly;
    private List<NavDrawerArray> datas;
    private ArrayList<Boolean> chkHeadPosExpanded;
    private final int maxChkHeadPosExpandedSize = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        urlList = new ArrayList< ArrayList< Pair<String, String> > >();
        restoringUserSettings();
        if(currentTheme == 0){
            SwitchTheme.changeTheme(this, SwitchTheme.THEME_DEFAULT);
        }
        else if(currentTheme == 1){
            SwitchTheme.changeTheme(this, SwitchTheme.THEME_DARK);
        }
        SwitchTheme.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_main);

        /*currentTheme = 0;
        kindOfView = 2;
        openDirectly = false;*/

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

        // display the first navigation drawer view on app launch
        currentPosition = -1;
        firstRun();
        datas = drawerFragment.getDataFromDb();
        //datas = drawerFragment.getData();
        //initURL();
        initURLFromDb();
        chkHeadPosExpanded = new ArrayList<Boolean>();
        for(int i=0; i<maxChkHeadPosExpandedSize; i++){
            chkHeadPosExpanded.add(i, false);
        }
        autoDeletePost();
        displayView(0, false);
    }

    private void autoDeletePost(){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(sharedPrefs.getBoolean("Time", false)){
            PostDatabaseHandler postDb = new PostDatabaseHandler(getApplicationContext());
            postDb.autoDeleteAfterNDays(DAYS);
            postDb.close();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.getItem(8).setChecked(openDirectly);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case RESULT_SETTINGS:
                restoringUserSettings();
                Intent it = getIntent();
                finish();
                startActivity(it);
                // recreate (make getContext() null)
                if(currentPosition != -1) displayView(currentPosition, true);
                break;
        }
    }

    private void firstRun(){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(!sharedPrefs.getBoolean("Time", false)){
            datas = drawerFragment.getData();
            initURL();
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putBoolean("Time", true);
            editor.commit();
            Intent it = getIntent();
            finish();
            startActivity(it);
        }
    }

    private void restoringUserSettings(){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String view = sharedPrefs.getString("prefView", "2");
        String theme = sharedPrefs.getString("prefTheme", "0");
        kindOfView = Integer.parseInt(view);
        currentTheme = Integer.parseInt(theme);
        openDirectly = sharedPrefs.getBoolean("prefOpenDirectly", false);

    }

    private void savingUserSettings(){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPrefs.edit();

        editor.putBoolean("prefOpenDirectly", openDirectly);
        String theme = Integer.toString(currentTheme);
        String view = Integer.toString(kindOfView);
        editor.putString("prefTheme", theme);
        editor.putString("prefView", view);
        editor.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent it = new Intent(MainActivity.this, UserSettingActivity.class);
            startActivityForResult(it, RESULT_SETTINGS);
            return true;
        }
        if(id == R.id.action_about){
            AlertDialog.Builder alg = new AlertDialog.Builder(MainActivity.this);
            alg.setTitle(getString(R.string.action_about));
            alg.setMessage(getString(R.string.author));

            LinearLayout layout = new LinearLayout(getApplicationContext());
            layout.setOrientation(LinearLayout.VERTICAL);
            alg.setView(layout);
            alg.setIcon(R.drawable.rss_sheep);
            alg.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            alg.create().show();
            //Toast.makeText(getApplicationContext(), "About action is selected!", Toast.LENGTH_SHORT).show();
            return true;
        }
        if(id == R.id.action_simple_list){
            kindOfView = 1;
            savingUserSettings();
            Toast.makeText(MainActivity.this, getString(R.string.action_simple_list), Toast.LENGTH_SHORT).show();
            if(currentPosition != -1) displayView(currentPosition, true);
            return true;
        }
        if(id == R.id.action_listview){
            kindOfView = 2;
            savingUserSettings();
            Toast.makeText(MainActivity.this, getString(R.string.action_listview), Toast.LENGTH_SHORT).show();
            if(currentPosition != -1) displayView(currentPosition, true);
            return true;
        }
        if(id == R.id.action_cardview){
            kindOfView = 3;
            savingUserSettings();
            Toast.makeText(MainActivity.this, getString(R.string.action_cardview), Toast.LENGTH_SHORT).show();
            if(currentPosition != -1) displayView(currentPosition, true);
        }
        if(id == R.id.action_switch_themes){
            if(currentTheme == 0){
                currentTheme = 1;
                SwitchTheme.changeTheme(this, SwitchTheme.THEME_DARK);
            }
            else if(currentTheme == 1){
                currentTheme = 0;
                SwitchTheme.changeTheme(this, SwitchTheme.THEME_DEFAULT);
            }
            savingUserSettings();
            Intent it = getIntent();
            finish();
            startActivity(it);
            // recreate();
            /*if(SwitchTheme.getsTheme() == SwitchTheme.THEME_DEFAULT){
                SwitchTheme.changeTheme(this, SwitchTheme.THEME_DARK);
            }
            else if(SwitchTheme.getsTheme() == SwitchTheme.THEME_DARK){
                SwitchTheme.changeTheme(this, SwitchTheme.THEME_DEFAULT);
            }*/
            return true;
        }
        if(id == R.id.action_open_webpage_directly){
            if(item.isChecked()){
                item.setChecked(false);
                openDirectly = false;
            }
            else{
                item.setChecked(true);
                openDirectly = true;
            }
            savingUserSettings();
            if(currentPosition != -1) displayView(currentPosition, true);
            return true;
        }
        if(id == R.id.action_delete_website){
            Fragment fragment = new DeleteWebsiteFragment();
            String title = getString(R.string.delete);

            Toast.makeText(this, title, Toast.LENGTH_SHORT).show();

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();

            // set the toolbar title
            getSupportActionBar().setTitle(title);
        }
        if(id == R.id.action_bookmark){
            String title = getString(R.string.bookmark);

            Fragment fragment = new PostFragment();
            Bundle bundle = new Bundle();
            bundle.putBoolean("bookmark", true);
            bundle.putInt("kind_of_view", kindOfView);
            bundle.putBoolean("open_webpage_directly", openDirectly);
            bundle.putString("action_bar_title", title);
            // set Fragmentclass Arguments
            fragment.setArguments(bundle);

            Toast.makeText(this, title, Toast.LENGTH_SHORT).show();

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();

            // set the toolbar title
            getSupportActionBar().setTitle(title);
        }
        if(id == R.id.randomPost){
            PostDatabaseHandler postDb = new PostDatabaseHandler(getApplicationContext());
            List<PostData> posts = postDb.getAllPost();
            Random rnd = new Random();
            int rndNum = rnd.nextInt(posts.size());
            PostData data = posts.get(rndNum);
            if(openDirectly){
                Intent openWebpage = new Intent(Intent.ACTION_VIEW, Uri.parse(data.postLink));
                startActivity(openWebpage);
            }
            else{
                Bundle postInfo = new Bundle();
                postInfo.putString("link", data.postLink);
                Intent postViewIntent = new Intent(getApplicationContext(), PostViewActivity.class);
                postViewIntent.putExtras(postInfo);
                startActivity(postViewIntent);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        currentPosition = position;
        displayView(position, true);
    }

    private void initURLFromDb(){
        urlList.clear();
        RSSDatabaseHandler rssDB = new RSSDatabaseHandler(getApplicationContext());
        List<Website> siteList = rssDB.getAllSites();

        for(int i=0; i<siteList.size(); i++){
            ArrayList<Pair<String, String>> tmp = new ArrayList< Pair<String, String> >();
            boolean chk = true;
            for(int j=i; j<siteList.size(); j++){
                if(!siteList.get(i).getGroup().equals(siteList.get(j).getGroup())){
                    urlList.add(tmp);
                    i = j - 1;
                    chk = false;
                    break;
                }
                tmp.add(Pair.create(siteList.get(j).getRssLink(), siteList.get(j).getTitle()));
            }
            if(chk) {
                urlList.add(tmp);
                break;
            }
        }
        rssDB.close();
    }

    private ArrayList<Integer> getGroupTitles(){
        ArrayList<Integer> groupTitles = new ArrayList<Integer>();
        RSSDatabaseHandler rssDb = new RSSDatabaseHandler(getApplicationContext());
        List<Website> siteList = rssDb.getAllSites();
        String group = siteList.get(0).getGroup();
        groupTitles.add(0);
        for(int i=1; i<siteList.size(); i++){
            if(!siteList.get(i).getGroup().equals(group)){
                group = siteList.get(i).getGroup();
                groupTitles.add(i+1);
            }
        }
        rssDb.close();
        return groupTitles;
    }

    private void initURL(){
        RSSDatabaseHandler rssDatabaseHandler = new RSSDatabaseHandler(getApplicationContext());
        ArrayList<Pair<String, String>> tmp = new ArrayList< Pair<String, String> >();
        tmp.add(Pair.create("http://tuoitre.vn/rss/tt-tin-moi-nhat.rss", getString(R.string.nav_item_home)));
        tmp.add(Pair.create("http://tuoitre.vn/rss/tt-the-gioi.rss", getString(R.string.nav_item_world)));
        tmp.add(Pair.create("http://tuoitre.vn/rss/tt-kinh-te.rss", getString(R.string.nav_item_economy)));
        tmp.add(Pair.create("http://tuoitre.vn/rss/tt-giao-duc.rss", getString(R.string.nav_item_education)));
        tmp.add(Pair.create("http://tuoitre.vn/rss/tt-van-hoa-giai-tri.rss", getString(R.string.nav_item_culture_entertainment)));
        tmp.add(Pair.create("http://tuoitre.vn/rss/tt-nhip-song-so.rss", getString(R.string.nav_item_tech)));
        tmp.add(Pair.create("http://tuoitre.vn/rss/tt-du-lich.rss", getString(R.string.nav_item_travel)));
        tmp.add(Pair.create("http://tuoitre.vn/rss/tt-chinh-tri-xa-hoi.rss", getString(R.string.nav_item_politic_society)));
        tmp.add(Pair.create("http://tuoitre.vn/rss/tt-phap-luat.rss", getString(R.string.nav_item_law)));
        tmp.add(Pair.create("http://tuoitre.vn/rss/tt-song-khoe.rss", getString(R.string.nav_item_healthy_life)));
        tmp.add(Pair.create("http://tuoitre.vn/rss/tt-the-thao.rss", getString(R.string.nav_item_sport)));
        tmp.add(Pair.create("http://tuoitre.vn/rss/tt-nhip-song-tre.rss", getString(R.string.nav_item_nhip_song_tre)));
        tmp.add(Pair.create("http://tuoitre.vn/rss/tt-ban-doc.rss", getString(R.string.nav_item_readers)));
        urlList.add(tmp);
        tmp = new ArrayList< Pair<String, String> >();
        tmp.add(Pair.create("http://vnexpress.net/rss/tin-moi-nhat.rss", getString(R.string.nav_item_home)));
        tmp.add(Pair.create("http://vnexpress.net/rss/thoi-su.rss", getString(R.string.nav_item_news)));
        tmp.add(Pair.create("http://vnexpress.net/rss/the-gioi.rss", getString(R.string.nav_item_world)));
        tmp.add(Pair.create("http://vnexpress.net/rss/kinh-doanh.rss", getString(R.string.nav_item_business)));
        tmp.add(Pair.create("http://vnexpress.net/rss/startup.rss", getString(R.string.nav_item_startup)));
        tmp.add(Pair.create("http://vnexpress.net/rss/giai-tri.rss", getString(R.string.nav_item_entertainment)));
        tmp.add(Pair.create("http://vnexpress.net/rss/the-thao.rss", getString(R.string.nav_item_sport)));
        tmp.add(Pair.create("http://vnexpress.net/rss/phap-luat.rss", getString(R.string.nav_item_law)));
        tmp.add(Pair.create("http://vnexpress.net/rss/giao-duc.rss", getString(R.string.nav_item_education)));
        tmp.add(Pair.create("http://vnexpress.net/rss/suc-khoe.rss", getString(R.string.nav_item_health)));
        tmp.add(Pair.create("http://vnexpress.net/rss/gia-dinh.rss", getString(R.string.nav_item_family)));
        tmp.add(Pair.create("http://vnexpress.net/rss/du-lich.rss", getString(R.string.nav_item_travel)));
        tmp.add(Pair.create("http://vnexpress.net/rss/khoa-hoc.rss", getString(R.string.nav_item_science)));
        tmp.add(Pair.create("http://vnexpress.net/rss/so-hoa.rss", getString(R.string.nav_item_digitizing)));
        tmp.add(Pair.create("http://vnexpress.net/rss/oto-xe-may.rss", getString(R.string.nav_item_vehicle)));
        tmp.add(Pair.create("http://vnexpress.net/rss/cong-dong.rss", getString(R.string.nav_item_community)));
        tmp.add(Pair.create("http://vnexpress.net/rss/tam-su.rss", getString(R.string.nav_item_talk)));
        tmp.add(Pair.create("http://vnexpress.net/rss/cuoi.rss", getString(R.string.nav_item_smile)));
        urlList.add(tmp);

        for(int i=0; i<urlList.size(); i++){
            for(int j=0; j<urlList.get(i).size(); j++){
                Website web = new Website(urlList.get(i).get(j).second, datas.get(i).getName(),
                        urlList.get(i).get(j).first);
                rssDatabaseHandler.addSite(web);
            }
        }
        rssDatabaseHandler.close();
    }

    private void displayView(int position, boolean itemClicked) {
        Fragment fragment = new PostFragment();
        String title = getString(R.string.app_name);
        ArrayList<Integer> headPos = drawerFragment.getHeadTitles();
        Bundle bundle = new Bundle();
        if(itemClicked){
            for(int i=0; i<headPos.size(); i++){
                if(position == headPos.get(i)){
                    fragment = null;
                    if(chkHeadPosExpanded.get(position)){ // if already expanded
                        // Collapse
                        chkHeadPosExpanded.set(position, false);
                        if((i+1) < headPos.size()){
                            int j = i+1;
                            int sz = urlList.get(i).size();
                            for(; j<headPos.size(); j++){
                                int tmp = headPos.get(j);
                                Boolean beforeCollapse = chkHeadPosExpanded.get(tmp);
                                if(beforeCollapse) chkHeadPosExpanded.set(tmp, false);
                                tmp -= sz;
                                chkHeadPosExpanded.set(tmp, beforeCollapse);
                                headPos.set(j, tmp);
                            }
                            drawerFragment.setHeadTitles(headPos);
                        }
                    }
                    else { // if not expanded yet
                        // Expand
                        chkHeadPosExpanded.set(position, true);
                        if((i+1) < headPos.size()) {
                            int j = i+1;
                            int sz = urlList.get(i).size();
                            for(; j<headPos.size(); j++){
                                int tmp = headPos.get(j);
                                Boolean beforeExpand = chkHeadPosExpanded.get(tmp);
                                if(beforeExpand) chkHeadPosExpanded.set(tmp, false);
                                tmp += sz;
                                headPos.set(j, tmp);
                                chkHeadPosExpanded.set(tmp, beforeExpand);
                            }
                            drawerFragment.setHeadTitles(headPos);
                        }
                    }
                    break;
                }
            }

            if(fragment != null){
                int row = 0, column = 0, last = 0, cnt = 0;
                for (int i = 0; i < urlList.size(); i++) {
                    if(chkHeadPosExpanded.get(headPos.get(i))) {
                        cnt += urlList.get(i).size();
                        if(i != 0) cnt += i - last;
                        last = i;
                    }
                    if (position <= cnt) {
                        //if(chkHeadPosExpanded.get(headPos.get(i))) cnt -= urlList.get(i).size();
                        row = i;
                        if(headPos.get(i) == 0){
                            column = (position / (headPos.get(i) + 1)) - 1;
                        }
                        else {
                            column = ((position / (headPos.get(i) + 1)) - 1)*(headPos.get(i) + 1)
                                    + (position % (headPos.get(i) + 1));
                        }
                        /*if(cnt == 0) column = position/(cnt+1) - 1;
                        else {
                            if(position == cnt){
                                column = (position / (cnt + 1))*(cnt+1) + (position % (cnt + 1));
                            }
                            else {
                                column = ((position / (cnt + 1)) - 1)*(cnt+1) + (position % (cnt + 1));
                            }
                        }*/
                        break;
                    }
                }
                Pair<String, String> navDrawerItemChosen = urlList.get(row)
                        .get(column);
                String url = navDrawerItemChosen.first;
                title = navDrawerItemChosen.second;
                bundle.putString("URL", url);

            }
        }

        if (fragment != null) {
            bundle.putInt("kind_of_view", kindOfView);
            bundle.putBoolean("open_webpage_directly", openDirectly);
            bundle.putString("action_bar_title", title);
            // set Fragmentclass Arguments
            fragment.setArguments(bundle);

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();

            // set the toolbar title
            getSupportActionBar().setTitle(title);
        }
    }
}